(ns aoc.d13.shuttle-search
  (:require [clojure.string :as str]))

#_(def input '("939" "7,13,x,x,59,x,31,19"))
(def input (line-seq (clojure.java.io/reader "src/aoc/d13/input.txt")))


(defn schedule [period]
  (iterate (partial + period) 0))

(defn str->line
  [line]
  {:line (Integer/parseInt line)})

(defn +next-schedule [start-depart line]
  (let [line-schedule (schedule (:line line))
        nearest (first (drop-while #(<= % start-depart) line-schedule))]
    (assoc line :nearest nearest)))

(defn +wait-time [start-depart line-nearest-schedule]
  (assoc line-nearest-schedule :wait (- (:nearest line-nearest-schedule) start-depart)))

(defn min-wait [min line]
  (if (< (:wait line) (:wait min))
    line
    min))

(defn get-next-shuttle
  ""
  []
  (let [start-depart (Integer/parseInt (first input))
        calc-result (fn [{:keys [line wait]}] (* line wait))]
    (->> (str/split (second input) #",")                    ;coll of shuttle-lines
         (remove #{"x"})                                    ;remove out-of-service
         (map str->line)
         (map (partial +next-schedule start-depart))
         (map (partial +wait-time start-depart))
         (reduce min-wait)
         (calc-result))))

(comment
  ; get fist 30 departs from shuttle-line 7
  (take 30 (shuttle-schedule 7))


  )