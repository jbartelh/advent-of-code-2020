(ns aoc.d2.password-philosophy
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(defn str->int? [s]
  (if (string? s)
    (try
      (Integer/parseInt s)
      (catch Exception _
        :clojure.spec/invalid))))

(defn count-char [{:keys [char password]}]
  (->> (seq password)
       (filter #{char})
       (count)))

(s/def ::str->int (s/conformer str->int? str))

(s/def ::more-than-lowest #(<= (:lowest %) (count-char %)))
(s/def ::less-than-highest #(<= (count-char %) (:highest %)))

(s/def ::password-policy (s/and string?
                                (s/conformer #(str/split % #"[\-|\s|:]+" 4))
                                (s/cat :lowest ::str->int
                                       :highest ::str->int
                                       :char (s/and #(= 1 (count %))
                                                    (s/conformer first))
                                       :password string?)
                                ::more-than-lowest
                                ::less-than-highest))

(def input (line-seq (clojure.java.io/reader "src/aoc/d2/input.txt")))

(defn count-invalid-pws
  "How many passwords are valid according to their policies?"
  []
  (->> input
       (filter (partial s/valid? ::password-policy))
       (count)))

; repl-examples:
#_(first input)
;=> "13-15 c: cqbhncccjsncqcc"
#_(s/valid? ::password-policy (first input))
;=> false
#_(s/explain ::password-policy (first input))
;{:lowest 13, :highest 15, :char \c, :password "cqbhncccjsncqcc"} - failed: (<= (:lowest %) (count-char %)) spec: :aoc.d2.password-philosophy/more-than-lower
;=> nil



