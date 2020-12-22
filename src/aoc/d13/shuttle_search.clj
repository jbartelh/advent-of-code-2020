(ns aoc.d13.shuttle-search
  (:require [clojure.string :as str]))

#_(def input '("939" "7,13,x,x,59,x,31,19"))
(def input (line-seq (clojure.java.io/reader "src/aoc/d13/input.txt")))

(defn schedule [period]
  (iterate (partial + period) 0))

(defn str->line
  [line]
  {:line (Integer/parseInt line)})

(defn +next-schedule [line]
  (let [start-depart (:start-depart line)
        line-schedule (schedule (:line line))
        nearest (first (drop-while #(<= % start-depart) line-schedule))]
    (assoc line :nearest nearest)))

(defn +wait-time [line]
  (let [start-depart (:start-depart line)]
    (assoc line :wait (- (:nearest line) start-depart))))

(defn min-wait [min line]
  (if (< (:wait line) (:wait min))
    line
    min))

(defn get-next-shuttle
  ""
  []
  (let [start-depart (Integer/parseInt (first input))
        +start-depart (fn [line] (assoc line :start-depart start-depart))
        calc-result (fn [{:keys [line wait]}] (* line wait))]
    (->> (str/split (second input) #",")                    ;coll of shuttle-lines
         (remove #{"x"})                                    ;remove out-of-service
         (map str->line)                                    ;make line hash-map
         (map +start-depart)                                ;add possible start departure time
         (map +next-schedule)                               ;add next schedule
         (map +wait-time)                                   ;add wait time
         (reduce min-wait)                                  ;find line with min wait time
         (calc-result))))                                   ;calc result

;; first approach: inefficient AF
(defn init [input] (->> (str/split (second input) #",")
                        (map-indexed vector)
                        (remove #(= (second %) "x"))
                        (mapv #(assoc % 1 (Integer/parseInt (second %))))
                        ))

#_(defn next-time [line-nr times]
  (let [smallest (apply min times)
        idx-smallest (.indexOf times smallest)]
    (update times idx-smallest (partial + (line-nr idx-smallest)))))

#_(let [lines (init input)
      line-off (mapv first lines)
      line-nr (mapv second lines)]
  (loop [times line-nr]
    (let [offsets (mapv - times line-off)]
      #_(println "times" times "offsets" offsets)
      (if #_(= 700 (first times)) (apply = offsets)
        times
        (recur (next-time line-nr times))))))


; second approach: a bit better but still inefficient and still not able to solve the given input
#_(defn calc-other-lines [offset-biggest offsets time-of-biggest]
  (let [no-offset-time (- time-of-biggest offset-biggest)]
    (map (partial + no-offset-time) offsets)))

#_(defn solution-valid? [line-nr solution]
  (apply = (map mod solution line-nr)))

#_(let [lines (init input)
      offsets (mapv first lines)
      line-nr (mapv second lines)
      biggest-line (apply max (map second lines))
      offset-biggest (offsets (.indexOf line-nr biggest-line))
      other-by-time (partial calc-other-lines offset-biggest offsets)]
  (loop [time biggest-line]
    (let [possible-solution (other-by-time time)]
      (if (solution-valid? line-nr possible-solution)
        possible-solution
        (recur (+ time biggest-line))))))

;;third approach: found crt approach @j-sattler
(defn shuttle-data->remainders [[idx shuttle-line]]
  (mod (- shuttle-line idx) shuttle-line))

(defn xgcd
  "Extended Euclidean Algorithm. Returns [gcd(a,b) x y] where ax + by = gcd(a,b).
  Source: https://en.wikibooks.org/wiki/Algorithm_Implementation/Mathematics/Extended_Euclidean_algorithm#Extended"
  [a b]
  (if (= a 0)
    [b 0 1]
    (let [[g x y] (xgcd (mod b a) a)]
      [g (- y (* (Math/floorDiv ^long b ^long a) x)) x])))

(defn chinese-remainder-theorem
  "Implemented after: https://www.math.uni-bielefeld.de/~pwegener/ChinesischerRestsatz.pdf" [ak mk]
  (let [m (apply * mk)
        Mk (map (partial / m) mk)
        Nk (map (fn [Mk n] (mod (second (xgcd Mk n)) n)) Mk mk)
        ]
    (->> (map * ak Mk Nk)
         (reduce +)
         ((fn [x] (mod x m))))))

(defn shuttle-company-contest
  "What is the earliest timestamp such that all of the listed bus IDs depart at offsets matching their positions in the list?"
  []
  (let [shuttle-data (init input)                           ;vector of [idx shuttle-line]
        mk (mapv second shuttle-data)
        ak (mapv shuttle-data->remainders shuttle-data)]
    ;; x ≡ idx/offset mod shuttle-line
    (chinese-remainder-theorem ak mk)))



(comment
  ; get fist 30 departs from shuttle-line 7
  (take 30 (shuttle-schedule 7))

  (chinese-remainder-theorem [2 3 2] [3 5 7])
  (chinese-remainder-theorem [6 5 4] [9 10 13])

  (chinese-remainder-theorem ak mk)

  ;part-two
  (calc-other-lines 4 [0 1 4 6 7] 59)
  (solution-valid? '(55 56 59 61 62) [7 13 59 31 19])
  )