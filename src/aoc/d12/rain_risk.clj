(ns aoc.d12.rain-risk
  (:require [clojure.algo.generic.functor :refer [fmap]]))

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

;; part-two
(defn rotate-waypoint [mhd lr degree]
  (let [-x (comp #(* -1 %) :x)
        -y (comp #(* -1 %) :y)
        rotations {90  [:y -x]
                   180 [-x -y]
                   270 [-y :x]}
        norm-degree ({\R degree
                      \L (- 360 degree)} lr)
        [rot-x rot-y] (rotations norm-degree)]
    (update mhd :waypoint (fn [wp]
                            {:x (rot-x wp)
                             :y (rot-y wp)}))))

(defn move
  ([mhd nesw steps] (move mhd nesw steps :mh-distance))
  ([mhd nesw steps type]
   (let [directions {\N [:y +]
                     \E [:x +]
                     \S [:y -]
                     \W [:x -]}
         [axis op] (directions nesw)]
     (update-in mhd [type axis] #(op % steps)))))

;part-two
(defn +mhd-distance [distance-to-add mh-distance]
  {:x (+ (:x mh-distance) (:x distance-to-add))
   :y (+ (:y mh-distance) (:y distance-to-add))})

;part-two
(defn move-towards-waypoint
  "part two"
  [mhd times]
  (let [new-distance (fmap (partial * times) (:waypoint mhd))]
    (update mhd :mh-distance (partial +mhd-distance new-distance))))

(defn manhattan-distance [mhd {direction :direction steps :steps}]
  (case direction
    \F (move mhd (:orientation mhd) steps)
    (\L \R) (update mhd :orientation #(turn % direction steps))
    (move mhd direction steps)))                            ;default

;part-two
(defn waypoint-manhattan-distance [mhd {direction :direction steps :steps}]
  (case direction
    \F (move-towards-waypoint mhd steps)
    (\L \R) (rotate-waypoint mhd direction steps)
    (move mhd direction steps :waypoint)))                  ;default

(defn calc-sum [{:keys [mh-distance]}]
  (+ (Math/abs ^int (:x mh-distance)) (Math/abs ^int (:y mh-distance))))

(defn fix-nav
  "Figure out where the navigation instructions lead. What is the Manhattan distance between that location and the ship's starting position?"
  []
  (->> input
       (map parse-nav-instruction)
       (reduce manhattan-distance {:orientation \E :mh-distance {:x 0 :y 0}})
       (calc-sum)))

(defn waypoint-based-nav
  "Figure out where the navigation instructions actually lead. What is the Manhattan distance between that location and the ship's starting position?"
  []
  (->> input
       (map parse-nav-instruction)
       (reduce waypoint-manhattan-distance {:waypoint {:x 10 :y 1} :mh-distance {:x 0 :y 0}})
       (calc-sum)))


(comment
  (parse-nav-instruction "F10")

  (turn \S \R 180)
  (turn \S \L 90)

  (manhattan-distance {:orientation \E :mh-distance {:x 0 :y 0}} {:direction \F :steps 10})
  (manhattan-distance {:orientation \E :mh-distance {:x 0 :y 0}} {:direction \L :steps 270})

  ; part two
  (fmap (partial * 10) {:x 10 :y 1})

  (update {:waypoint {:x 10 :y 1} :mh-distance {:x 10 :y 1}} :mh-distance #(fmap (partial +)))

  (move-towards-waypoint {:waypoint {:x 10 :y 1} :mh-distance {:x 0 :y 0}} 10)
  (waypoint-manhattan-distance {:waypoint {:x 0 :y 0} :mh-distance {:x 10 :y 1}} {:direction \F :steps 10})
  (waypoint-manhattan-distance {:waypoint {:x 0 :y 0} :mh-distance {:x 0 :y 0}} {:direction \L :steps 270})

  (rotate-waypoint {:waypoint {:x 10 :y 4} :mh-distance {:x 0 :y 0}} \R 90)


  )