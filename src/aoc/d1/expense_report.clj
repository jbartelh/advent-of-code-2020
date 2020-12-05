(ns aoc.d1.expense-report
  (:require [clojure.edn :as edn]
            [clojure.java.io :refer [reader]])
  (:import (java.io PushbackReader)))

(defn find-two-entries
  "Find the two entries that sum to 2020; what do you get if you multiply them together?"
  ([]
   (find-two-entries (edn/read (PushbackReader. (reader "src/aoc/d1/input.edn")))))
  ([expense-report]
   (->> (for [a expense-report
              b expense-report
              :let [sum (+ a b)]
              :let [mul (* a b)]
              :when (= sum 2020)]
          mul)
        first)))