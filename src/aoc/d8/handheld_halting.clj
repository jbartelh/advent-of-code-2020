(ns aoc.d8.handheld-halting
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(def input (line-seq (clojure.java.io/reader "src/aoc/d8/input.txt")))

(defn str->int? [s]
  (if (string? s)
    (try
      (Integer/parseInt s)
      (catch Exception _
        :clojure.spec.alpha/invalid))))

;; specification of the 'pseudo assembler'
(s/def ::op (s/and string?
                   (s/conformer #(str/split % #" "))
                   (s/cat :op-code (s/and #{"jmp" "acc" "nop"}
                                          (s/conformer keyword))
                          :arg (s/conformer str->int?))))

(s/def ::program (s/* ::op))

(defmulti execute
          "Polymorph implementations for the op-codes: acc, nop and jmp.
          Executes the given operation in the given context and returns the new context

          acc: increase accumulator by arg and increment line
          jmp: add/sub arg to line
          nop: increment line"
          (fn [cmd _] (:op-code cmd)))

(defmethod execute :acc [{arg :arg} ctx]
  (-> ctx
      (update :accumulator + arg)
      (update :line inc)))

(defmethod execute :jmp [{arg :arg} ctx]
  (update ctx :line + arg))

(defmethod execute :nop [_ ctx]
  (update ctx :line inc))

;; part-two
(defn lazy-changes
  "Returns a lazy sequence of possible bugfix changes in form of [line changed-op].
  A possible change are :nop changed to :jmp and vice versa."
  [program]
  (keep-indexed (fn [idx {code :op-code :as op}]
                  (when (or (= code :nop) (= code :jmp))
                    (let [changed-op-code (code {:nop :jmp
                                                 :jmp :nop})]
                      [idx (assoc op :op-code changed-op-code)])))
                program))
;; part-two
(defn lazy-changed-programs
  [program]
  (->> (lazy-changes program)
       (map (fn [[line changed-op]] (assoc program line changed-op)))))

(defn run
  "Runs the given program.
  It executes the first operation in an initial context, to derive a new context.
  Recursively executes the next operations until an operation would be executed
  twice. To avoid twice execution, every call is stored in a hash-set, where a
  call is defined by the executed line and the operation"
  [program]
  (loop [ctx {:accumulator 0 :line 0}
         op (first program)
         call-set #{}]
    (let [call {(:line ctx) op}]
      (cond
        (contains? call-set call)
        {:op op :ctx ctx :call-set call-set}
        (= op :end)
        {:op op :ctx ctx :call-set call-set :terminated true}
        :else (let [next-ctx (execute op ctx)
              next-op (get program (:line next-ctx) :end)
              next-call-set (conj call-set call)]
          (recur next-ctx next-op next-call-set))))))

(defn accumulators-value
  "Run your copy of the boot code. Immediately before any instruction is executed a second time, what value is in the accumulator?"
  []
  (let [program (s/conform ::program input)
        return (run program)]
    (get-in return [:ctx :accumulator])))

(defn accumulator-after-fix
  "Fix the program so that it terminates normally by changing exactly one jmp (to nop) or nop (to jmp). What is the value of the accumulator after the program terminates?"
  []
  (let [program (s/conform ::program input)
        program-variations (lazy-changed-programs program)]
    (->> (map #(run %) program-variations)
         (filter :terminated)
         (first)
         (:ctx)
         (:accumulator))))

(comment
  ;; exec single op
  (execute {:op-code :jmp, :arg 1} {:accumulator 0 :line 0})

  (map #(operation % {:accumulator 1 :line 0}) parsed-program))
