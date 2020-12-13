(ns aoc.d9.encoding-error)

;; test input
#_(def input '("35" "20" "15" "25" "47" "40" "62" "55" "65" "95" "102" "117" "150" "182" "127" "219" "299" "277" "309" "576"))
#_(def preamble 5)

(def input (line-seq (clojure.java.io/reader "src/aoc/d9/input.txt")))

(def preamble 25)

(defn str->long? [s]
  (if (string? s)
    (try
      (Long/parseLong s)
      (catch Exception _
        :clojure.spec.alpha/invalid))))

(defn find-sum [previous number]
  (first (for [a previous
               b previous
               :let [sum (+ a b)]
               :when (and (not= a b) (= sum number))]
           [a :+ b := sum])))

(defn not-sum-of-prev [prevs-idx offset]
  (fn [idx number]
    (let [previous (subvec prevs-idx idx (+ idx offset))]
      (when-not (find-sum previous number)
        number))))

(defn contiguous-set-fn [input search-sum]
  (fn [idx number]
    (loop [index idx
           contiguous-set [number]
           sum number]
      (cond
        (= sum search-sum) contiguous-set
        (> sum search-sum) nil
        :else (let [next-idx (inc index)
                    acc (input next-idx)]
                (recur next-idx (conj contiguous-set acc) (+ sum acc)))))))

(defn min-max-sum [contiguous-set]
  (let [smallest (apply min contiguous-set)
        largest (apply max contiguous-set)]
    (+ smallest largest)))

(defn find-weakness
  "Find the first number in the list (after the preamble) which is not the sum of two of the 25 numbers before it. What is the first number that does not have this property?"
  []
  (let [numbers (map str->long? input)
        idx-num (apply vector numbers)
        not-sum-of-prev? (not-sum-of-prev idx-num preamble)]
    (->> (drop preamble numbers)
         (keep-indexed not-sum-of-prev?)
         (first))))

(defn find-contiguous-set
  "What is the encryption weakness in your XMAS-encrypted list of numbers?"
  []
  (let [numbers (map str->long? input)
        idx-num (apply vector numbers)
        invalid-number (find-weakness)
        get-contiguous-set (contiguous-set-fn idx-num invalid-number)
        ]
    (->> numbers
         (keep-indexed get-contiguous-set)
         (first)
         (min-max-sum))))