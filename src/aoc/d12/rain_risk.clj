(ns aoc.d12.rain-risk
  (:require [clojure.spec.alpha :as s]))

#_(def input '("F10" "N3" "F7" "R90" "F11"))
(def input (line-seq (clojure.java.io/reader "src/aoc/d12/input.txt")))

(defn parse-nav-instruction [[direction & steps]]
  {:direction direction
   :steps     (Integer/parseInt (apply str steps))})

(defn turn [orientation direction degree]
  (let [nesw [\N \E \S \W]
        lr-op ({\R + \L -} direction)
        current (.indexOf nesw orientation)
        quarters (/ degree 90)]
    (-> (lr-op current quarters)
        (mod 4)
        (nesw))))

(defn move [mhd nesw steps]
  (let [directions {\N [:y +]
                    \E [:x +]
                    \S [:y -]
                    \W [:x -]}
        [axis op] (directions nesw)]
    (update-in mhd [:distance axis] #(op % steps))))

(defn manhattan-distance [mhd {direction :direction steps :steps}]
  (case direction
    \F (move mhd (:orientation mhd) steps)
    (\L \R) (update mhd :orientation #(turn % direction steps))
    (move mhd direction steps)))                            ;default

(defn calc-sum [{:keys [distance]}]
  (+ (Math/abs (:x distance)) (Math/abs (:y distance))))

(defn fix-nav
  "Figure out where the navigation instructions lead. What is the Manhattan distance between that location and the ship's starting position?"
  []
  (->> input
       (map parse-nav-instruction)
       (reduce manhattan-distance {:orientation \E :distance {:x 0 :y 0}})
       (calc-sum)))



(comment
  (parse-nav-instruction "F10")

  (turn \S \R 180)
  (turn \S \L 90)

  (manhattan-distance {:orientation \E :distance {:x 0 :y 0}} {:direction \F :steps 10})
  (manhattan-distance {:orientation \E :distance {:x 0 :y 0}} {:direction \L :steps 270})

  )