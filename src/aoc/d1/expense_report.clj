(ns aoc.d1.expense-report
  (:require [clojure.edn :as edn]
            [clojure.java.io :refer [reader]]
            [clojure.math.combinatorics :refer [cartesian-product]])
  (:import (java.io PushbackReader)))

;; first solution with for-comprehension
#_(->> (for [a expense-report
           b expense-report
           :let [sum (+ a b)]
           :let [mul (* a b)]
           :when (= sum 2020)]
       mul)
     first)

(def input (edn/read (PushbackReader. (reader "src/aoc/d1/input.edn"))))

(defn expense-report [report number-of-entries]
  (let [coll-of-reports (repeat number-of-entries report)]
    (first (reduce (fn [acc coll]
                     (if (= (apply + coll) 2020)
                       (conj acc (apply * coll))
                       acc))
                   []
                   (apply cartesian-product coll-of-reports)))))

(defn find-two-entries
  "Find the two entries that sum to 2020; what do you get if you multiply them together?"
  []
  (expense-report input 2))

(defn find-three-entries
  "Part two: In your expense report, what is the product of the three entries that sum to 2020?"
  []
  (expense-report input 3))

#_(let [input [1721 979 366 299 675 1456]]
  (expense-report input 2))
;=> 514579