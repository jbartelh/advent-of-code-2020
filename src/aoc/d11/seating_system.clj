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

(defn match-rule-empty? [seat adjacent-seats]
  (and
    (seat-empty? seat)
    (= 0 (get adjacent-seats \# 0))))

(defn match-rule-occupied? [seat adjacent-seats]
  (and
    (seat-occupied? seat)
    (<= 4 (get adjacent-seats \# 0))))

(def ^:dynamic occupied-rule match-rule-occupied?)
(def ^:dynamic empty-rule match-rule-empty?)
(def ^:dynamic adjacent-freq (comp frequencies adjacent))

(defn apply-rules [row col board]
  (let [seat (get-in board [row col])
        adjacent-seats (adjacent-freq row col board)]
    (cond
      (empty-rule seat adjacent-seats) \#
      (occupied-rule seat adjacent-seats) \L
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

;; part two

(defn match-rule-occupied-p2? [seat adjacent-seats]
  (and
    (seat-occupied? seat)
    (<= 5 (get adjacent-seats \# 0))))

(defn lazy-adjacent-pos-fn
  "Returns a function from the given direction (row-fn col-fn), which takes a position and the board and creates a lazy
  sequence of position in one direction. A directions is defined as two functions describing the position change, one
  for row and one for col, e.g. 'dec', 'inc'. "
  [row-fn col-fn]
  (fn lazy-adjacent-pos [row col board] (lazy-seq
                                          (let [next-row (row-fn row)
                                                next-col (col-fn col)
                                                next-pos (get-in board [next-row next-col])]
                                            (when next-pos
                                              (cons next-pos
                                                    (lazy-adjacent-pos next-row next-col board)))))))

(def delta-directions-fns [[dec dec]                        ;top left
                           [dec identity]                    ;top mid
                           [dec inc]                         ;top right
                           [identity dec]                    ;left
                           [identity inc]                    ;right
                           [inc dec]                         ;bottom left
                           [inc identity]                    ;bottom mid
                           [inc inc]])                       ;bottom right

(defn delta->lazy-pos-seq [row col board]
  (fn [delta-directions-fn]
    (let [generator-fn (apply lazy-adjacent-pos-fn delta-directions-fn)]
      (generator-fn row col board))))

(defn adjacent-multi
  "returns all surrounding/neighbor/adjacent, considering the first seat in each direction, for a given position"
  [row col board]
  (->> delta-directions-fns                                 ;coll of functions, one for each direction
       (map (delta->lazy-pos-seq row col board))            ;direction-fn -> lazy seq positions in that direction
       (map #(remove #{\.} %))                              ;remove floors in each direction-seq
       (map first)                                          ;get first, empty or occupied seat
       (remove nil?)))                                      ;remove directions without seats

(defn number-of-occupied-new-visibility
  "Given the new visibility method and the rule change for occupied seats becoming empty, once equilibrium is reached, how many seats end up occupied?"
  []
  (binding [occupied-rule match-rule-occupied-p2?
            adjacent-freq (comp frequencies adjacent-multi)]
    (->> (loop [board input]
           (let [next (next-board board)]
             (if (= next board)
               board
               (recur next))))
         (map frequencies)
         (map #(get % \# 0))
         (reduce +))))

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
  ;;part two

  (def multi-adjacent-input (into [] '(".......#."
                                        "...#....."
                                        ".#......."
                                        "........."
                                        "..#L....#"
                                        "....#...."
                                        "........."
                                        "#........"
                                        "...#.....")))      ; \L: (4 3)

  (adjacent-multi 4 3 multi-adjacent-input)

  (def demo-input2 (into [] '("............."
                               ".L.L.#.#.#.#."
                               ".............")))           ; \L: (1 1)

  (adjacent-multi 1 1 demo-input2)

  (def no-occupied-demo (into [] '(".##.##."
                                    "#.#.#.#"
                                    "##...##"
                                    "...L..."
                                    "##...##"
                                    "#.#.#.#"
                                    ".##.##.")))            ; \L: (3 3)

  (adjacent-multi 3 3 no-occupied-demo)
)