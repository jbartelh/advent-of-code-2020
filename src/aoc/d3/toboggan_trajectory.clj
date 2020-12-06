(ns aoc.d3.toboggan-trajectory)

(def input (into [] (line-seq (clojure.java.io/reader "src/aoc/d3/input.txt"))))

(defn get-flight-map-sizes
  "Calculates the width and length of the flight-map,
   where width is the number of chars in one column
   and the length is the number of columns"
  [flight-map]
  {:width  (count (first flight-map))
   :length (count flight-map)})

(defn get-next-pos
  "Calculates the next position based on flight-map-width,
  the direction (right and down) and the current position (right and down)"
  [width {move-right :right move-down :down} {:keys [right down]}]
  {:right (mod (+ right move-right) width)
   :down  (+ down move-down)})

(defn get-trajectory
  "Calculates the trajectory as a collection of points.
   It needs a start-point, width and length of the flight-map and the direction"
  [{:keys [start width length direction]}]
  (let [next-pos (partial get-next-pos width direction)]
    (loop [pos start
           trajectory [start]]
      (let [new-pos (next-pos pos)]
        (if (< (:down new-pos) length)
          (recur new-pos (conj trajectory new-pos))
          trajectory)))))

(defn tree?
  "Predicate, which returns 'true' when there is a tree ('#') at the given position,
  otherwise 'false'"
  [flight-map {:keys [right down]}]
  (let [row (get flight-map down)
        column (get row right)]
    (= column \#)))

(defn number-of-trees
  "Takes a flight, calculates its trajectory, filters trajectory point when there is a tree
  at its position and then counts the number of points."
  [flight-map flight]
  (->> (get-trajectory flight)
       (filter (partial tree? flight-map))
       (count)))

(defn part-one
  "Starting at the top-left corner of your map and following a slope of right 3 and down 1, how many trees would you encounter?"
  []
  (let [flight-map-sizes (get-flight-map-sizes input)
        flight (merge flight-map-sizes {:direction {:right 3
                                              :down  1}
                                  :start     {:right 0
                                              :down  0}})]
    (number-of-trees input flight)))


;; repl-examples
#_(get-board-sizes input)
;=> {:width 31, :length 323}

#_(get-next-pos 31
                {:right 3
                 :down  1}
                {:right 30
                 :down  10})
;=> {:right 2, :down 11}

#_(get-trajectory {:start     {:right 0
                               :down  0}
                   :width     31
                   :length    323
                   :direction {:right 3
                               :down  1}})
;=> [ ...
; {:right 30, :down 320}
; {:right 2, :down 321}
; {:right 5, :down 322}]