(ns aoc.d11.seating-system)

#_(def input (into [] '("L.LL.LL.LL" "LLLLLLL.LL" "L.L.L..L.." "LLLL.LL.LL" "L.LL.LL.LL" "L.LLLLL.LL" "..L.L....." "LLLLLLLLLL" "L.LLLLLL.L" "L.LLLLL.LL")))
(def input (into [] (line-seq (clojure.java.io/reader "src/aoc/d11/input.txt"))))

(def seat-occupied? #{\#})
(def seat-empty? #{\L})

(defn adjacent
  "returns all surrounding/neighbor/adjacent seats for a given position"
  [r c board]
  (let [at (partial get-in board)]
    (remove nil? (vector
                   (at [(dec r) (dec c)])
                   (at [(dec r) c])
                   (at [(dec r) (inc c)])
                   (at [r (dec c)])
                   (at [r (inc c)])
                   (at [(inc r) (dec c)])
                   (at [(inc r) c])
                   (at [(inc r) (inc c)])))))

(def adjacent-freq
  "returns the empty/occupied/floor frequencies of adjacent seats for a given position"
  (comp frequencies adjacent))

(defn match-rule-empty? [seat adjacent-seats]
  (and
    (seat-empty? seat)
    (= 0 (get adjacent-seats \# 0))))

(defn match-rule-occupied? [seat adjacent-seats]
  (and
    (seat-occupied? seat)
    (<= 4 (get adjacent-seats \# 0))))

(defn apply-rules [row col board]
  (let [seat (get-in board [row col])
        adjacent-seats (adjacent-freq row col board)]
    (cond
      (match-rule-empty? seat adjacent-seats) \#
      (match-rule-occupied? seat adjacent-seats) \L
      :else seat)))

(defn next-board [board]
  (let [board-width (count (first board))]
    (->> (for [row (range (count board))
               column (range board-width)]
           (apply-rules row column board))
         (partition board-width)
         (map #(apply str %))
         (vec))))

(defn number-of-occupied-seats
  "Simulate your seating area by applying the seating rules repeatedly until no seats change state. How many seats end up occupied?"
  []
  (->> (loop [board input]
         (let [next (next-board board)]
           (if (= next board)
             board
             (recur next))))
       (map frequencies)
       (map #(get % \# 0))
       (reduce +)))


(comment
  (def input (into [] '("L.LL.LL.LL" "LLLLLLL.LL" "L.L.L..L.." "LLLL.LL.LL" "L.LL.LL.LL" "L.LLLLL.LL" "..L.L....." "LLLLLLLLLL" "L.LLLLLL.L" "L.LLLLL.LL")))
  (clojure.pprint/pprint input)

  (adjacent 1 1 input)
  (adjacent-freq 1 1 input)
  ;=> {\L 6, \. 2}

  (match-rule-empty? \L {\L 6, \. 2})

  (apply-rules 0 0 input)

  (next-board input)

  (-> input
      (next-board)
      (next-board))
  )