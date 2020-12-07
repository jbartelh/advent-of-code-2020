(ns aoc.core
  (:require [aoc.d1.expense-report :as day1]
            [aoc.d2.password-philosophy :as day2]
            [aoc.d3.toboggan-trajectory :as day3]
            [aoc.d4.password-processing :as day4]
            [aoc.d5.binary-boarding :as day5]))

(def exercises {:1 #'day1/find-two-entries
                :1b #'day1/find-three-entries
                :2 #'day2/count-invalid-pws
                :2b #'day2/invalid-pws-by-pos
                :3 #'day3/part-one
                :3b #'day3/part-two-multiple-slopes
                :4 #'day4/count-invalid-passports
                :4b #'day4/passports-with-valid-fields
                :5 #'day5/highest-seat
                :5b #'day5/missing-seat})

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
