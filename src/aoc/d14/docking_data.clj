(ns aoc.d14.docking-data
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [clojure.math.combinatorics :refer [cartesian-product]]))

#_(def input '("mask = XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X"
              "mem[8] = 11"
              "mem[7] = 101"
              "mem[8] = 0"))
(def input (line-seq (clojure.java.io/reader "src/aoc/d14/input.txt")))

(defn str->int? [s]
  (if (string? s)
    (try
      (Integer/parseInt s)
      (catch Exception _
        :clojure.spec.alpha/invalid))))

(s/def ::bit-mask (s/and #(str/starts-with? % "mask = ")
                         (s/conformer (partial re-find #"[X|0|1]+"))))

(s/def ::set-memory (s/and #(str/starts-with? % "mem[")
                           (s/conformer (fn [s] (drop 1 (re-find #"mem\[([0-9]+)\] = ([0-9]+)" s))))
                           (s/cat :address (s/conformer str->int?)
                                  :value (s/conformer str->int?))))

(s/def ::code (s/alt :mem ::set-memory
                     :mask ::bit-mask))
(s/def ::init-code (s/* ::code))

(def init-code (s/conform ::init-code input))

(defn apply-bit-mask [val [pos bit-mask]]
  (case bit-mask
    \0 (bit-clear val pos)
    \1 (bit-set val pos)))

(defn value-mask->fn [mask]
  (let [bits-to-change (->> (keep-indexed (fn [idx val] (when (#{\1 \0} val) [idx val])) mask)
                            (map (fn [[idx val]] [(- (count mask) idx 1) val])))]
    (fn [value] (reduce apply-bit-mask value bits-to-change))))

;; part two
(defn mask-op? [op]
  (fn [[_ change-op]] (= op change-op)) )

;; part two
(defn set-ones [masked-ones address]
  (reduce
    (fn [add [pos _]](bit-set add pos))
    address
    masked-ones))

;; part two
(defn x->01 [[pos _]]
  [[pos \1] [pos \0]])

;; part two
(defn x->addresses [x-ops address]
  (let [bit-ops (apply cartesian-product (map x->01 x-ops))]
    (reduce (fn [addresses bit-mask-ops]
              (conj addresses (reduce apply-bit-mask address bit-mask-ops)))
            #{}
            bit-ops)))

;; part two
(defn address-mask->fn [mask]
  (let [bits-to-change (->> (keep-indexed (fn [idx val] (when (#{\1 \X} val) [idx val])) mask)
                            (map (fn [[idx val]] [(- (count mask) idx 1) val])))
        ops-1 (filter (mask-op? \1) bits-to-change)
        ops-x (filter (mask-op? \X) bits-to-change)]
    (fn [address value]
      (->> address
           (set-ones ops-1)
           (x->addresses ops-x)
           (reduce (fn [mem address] (assoc mem address value)) {})))))

(defmulti exec (fn [version ctx [op-code args]] [version op-code]))

(defmethod exec [:v1 :mem] [_ ctx [_ {:keys [address value]}]]
  (assoc-in ctx [:memory address] ((:mask-fn ctx) value)))

(defmethod exec [:v1 :mask] [_ ctx [_ mask]]
  (assoc ctx :mask-fn (value-mask->fn mask)))

;; part two
(defmethod exec [:v2 :mem] [_ ctx [_ {:keys [address value]}]]
  (update ctx :memory #(merge % ((:mask-fn ctx) address value))))

;; part two
(defmethod exec [:v2 :mask] [_ ctx [_ mask]]
  (assoc ctx :mask-fn (address-mask->fn mask)))

(defn exec-emulator [version]
  (->> (reduce (partial exec version) {} init-code)
       (:memory)
       (vals)
       (reduce +)))

(defn exec-init-and-calc-mem
  "Execute the initialization program. What is the sum of all values left in memory after it completes?"
  []
  (exec-emulator :v1))

(defn exec-emulator-v2
  "Execute the initialization program using an emulator for a version 2 decoder chip. What is the sum of all values left in memory after it completes?"
  []
  (exec-emulator :v2))

(comment
  (def demo-input-2 (s/conform ::init-code '("mask = 000000000000000000000000000000X1001X"
                                              "mem[42] = 100"
                                              "mask = 00000000000000000000000000000000X0XX"
                                              "mem[26] = 1")))
  (addr-mask->fn "000000000000000000000000000000X1001X")

  (x->addresses '([5 \X] [0 \X]) 58)
  ((address-mask->fn "000000000000000000000000000000X1001X") 42 100)
  )
