(ns aoc.d9.encoding-error)

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

(defn find-weakness
  "The first step of attacking the weakness in the XMAS data is to find the first number in the list (after the preamble) which is not the sum of two of the 25 numbers before it. What is the first number that does not have this property?"
  []
  (let [numbers (map str->long? input)
        idx-num (apply vector numbers)
        not-sum-of-prev? (not-sum-of-prev idx-num preamble)]
    (->> (drop preamble numbers)
         (keep-indexed not-sum-of-prev?)
         (first))))


