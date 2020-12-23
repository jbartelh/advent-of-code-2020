(ns aoc.d14.docking-data
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

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

(defn mask->fn [mask]
  (let [bits-to-change (->> (keep-indexed (fn [idx val] (when (#{\1 \0} val) [idx val])) mask)
                            (map (fn [[idx val]] [(- (count mask) idx 1) val])))]
    (fn [val] (reduce
                (fn [val [pos bit-mask]]
                  (case bit-mask
                    \0 (bit-clear val pos)
                    \1 (bit-set val pos)))
                val
                bits-to-change))))

  (defmulti exec (fn [[code _] _] code))

(defmethod exec :mem [[_ {:keys [address value]}] ctx]
  (assoc-in ctx [:mem address] ((:mask-fn ctx) value)))

(defmethod exec :mask [[_ mask] ctx]
  (assoc ctx :mask-fn (mask->fn mask)))

(defn exec-init-and-calc-mem
  "Execute the initialization program. What is the sum of all values left in memory after it completes?"
  []
  (->> (loop [ctx {:mem {}}
              [first & rest] init-code]
         (if-not first
           ctx
           (recur (exec first ctx) rest)))
       (:mem)
       (vals)
       (reduce +)))

