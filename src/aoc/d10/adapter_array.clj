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