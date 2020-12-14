(ns aoc.d10.adapter-array)

(def input (line-seq (clojure.java.io/reader "src/aoc/d10/input.txt")))

(defn jolt-deltas
  "Chain all your adapters. What is the number of 1-jolt differences multiplied by the number of 3-jolt differences?"
  []
  (let [in (sort (map #(Integer/parseInt %) input))
        build-in (+ 3 (apply max in))
        calc-res #(* (% 1) (% 3))]
    (->> (concat [0] in [build-in])
         (partition 2 1)
         (map (fn [[f s]] (- s f)))
         (frequencies)
         (calc-res))))

(defn get-sum [v i]
  (let [m1 (get v (- i 1) 0)
        m2 (get v (- i 2) 0)
        m3 (get v (- i 3) 0)]
    (+ m1 m2 m3)))

(defn all-distinct-combinations
  "What is the total number of distinct ways you can arrange the adapters to connect the charging outlet to your device?"
  []
  (last (let [in (sort (map #(Integer/parseInt %) input))
              build-in (+ 3 (apply max in))
              ]
          (loop [vals (concat in [build-in])
                 poss (vec (conj (repeat build-in 0) 1))]
            (if-let [idx (first vals)]
              (recur (drop 1 vals) (assoc poss idx (get-sum poss idx)))
              poss)))))
