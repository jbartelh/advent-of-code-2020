(ns aoc.d5.binary-boarding
  (:require [clojure.string :as str]
            [clojure.math.numeric-tower :as m]))

(def input (into [] (line-seq (clojure.java.io/reader "src/aoc/d5/input.txt"))))

(defn binary-walk [binary-partition true-char code]
  (first (reduce
           (fn [remaining-rows code]
             (let [size (count remaining-rows)]
               (if (= code true-char)
                 (subvec remaining-rows 0 (/ size 2))
                 (subvec remaining-rows (/ size 2)))))
           binary-partition
           code)))

(defn get-column [columns binary-col-code]
  (binary-walk columns \L binary-col-code))

(defn get-row [rows binary-row-code]
  (binary-walk rows \F binary-row-code))

(defn get-seat [rows columns binary-code]
  (let [row-code (subs binary-code 0 7)
        row (get-row rows row-code)
        col-code (subs binary-code 7)
        column (get-column columns col-code)]
    (+ (* 8 row) column)))

(defn highest-seat
  "As a sanity check, look through your list of boarding passes. What is the highest seat ID on a boarding pass?"
  []
  (let [rows (into [] (range 128))
        columns (into [] (range 8))
        code->seat (partial get-seat rows columns)]
    (->> (map code->seat input)
         (sort)
         (reverse)
         (first))))

;; repl-examples:
(comment
  ;def example code from description
  (def example "FBFBBFFRLR")
  ;def binary partitions, immutable so needed just once
  (def rows (into [] (range 128)))
  (def columns (into [] (range 8)))
  ;get row and column
  (get-row rows "FBFBBFF")
  (get-column columns "RLR")
  ;44 * 8 + 5 357
  (get-seat rows columns example)
  )