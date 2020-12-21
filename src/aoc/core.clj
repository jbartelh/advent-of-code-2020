(ns aoc.core
  (:require [aoc.d1.expense-report :as day1]
            [aoc.d2.password-philosophy :as day2]
            [aoc.d3.toboggan-trajectory :as day3]
            [aoc.d4.password-processing :as day4]
            [aoc.d5.binary-boarding :as day5]
            [aoc.d6.custom-customs :as day6]
            [aoc.d7.handy-haversacks :as day7]
            [aoc.d8.handheld-halting :as day8]
            [aoc.d9.encoding-error :as day9]
            [aoc.d10.adapter-array :as day10]
            [aoc.d11.seating-system :as day11]
            [aoc.d12.rain-risk :as day12]))

(def exercises {:1 #'day1/find-two-entries
                :1b #'day1/find-three-entries
                :2 #'day2/count-invalid-pws
                :2b #'day2/invalid-pws-by-pos
                :3 #'day3/part-one
                :3b #'day3/part-two-multiple-slopes
                :4 #'day4/count-invalid-passports
                :4b #'day4/passports-with-valid-fields
                :5 #'day5/highest-seat
                :5b #'day5/missing-seat
                :6 #'day6/sum-of-anyone-yes
                :6b #'day6/sum-of-everyone-yes
                :7 #'day7/bags-containing-shiny-gold
                :7b #'day7/bags-inside-shiny-gold
                :8 #'day8/accumulators-value
                :8b #'day8/accumulator-after-fix
                :9 #'day9/find-weakness
                :9b #'day9/find-contiguous-set
                :10 #'day10/jolt-deltas
                :10b #'day10/all-distinct-combinations
                :11 #'day11/number-of-occupied-seats
                :11b #'day11/number-of-occupied-new-visibility
                :12 #'day12/fix-nav})

(defn exercise->str [[k exercise-fn]]
  (let [arg-str (name k)
        fn-name (:name (meta exercise-fn))
        fn-doc (:doc (meta exercise-fn))]
    (format "%-4s%-30s%s%n" arg-str fn-name fn-doc)))

(def available-exercises-as-str
  (->> exercises
       (sort)
       (map exercise->str)
       (reduce str)))

(defn error-str
  ([]
   (str "Please specify a day/exercise to execute, the following are available:\n"
        available-exercises-as-str))
  ([day]
   (str "No exercise found for '" day "', try one of:\n"
        available-exercises-as-str)))
#_ ((:1b exercises))

(defn -main
  [& [day & _]]
  (println (if day
             (if-let [exercise ((keyword day) exercises)]
               (exercise)
               (error-str day))
             (error-str))))
