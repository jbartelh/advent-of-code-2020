(ns aoc.d7.handy-haversacks
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(def input (line-seq (clojure.java.io/reader "src/aoc/d7/input.txt")))

(def color->key #(keyword (str %1 "-" %2)))

(defn inner-rule->map [raw-inner]
  (reduce (fn [rules [amount c-shade c-main]]
            (conj rules {(color->key c-shade c-main) (Integer/parseInt amount)}))
          {}
          (partition 3 raw-inner)))

(defn parse-rule [rule]
  (let [rule-parts (filter (complement #{"bags" "bag" "no" "other" "contain"})
                           (str/split rule #",\s|\s|\."))
        [color-shade color-main & inner-rules] rule-parts
        color (color->key color-shade color-main)
        rules (inner-rule->map inner-rules)]
    [color rules]))

(def parsed (reduce (fn [rules raw]
                      (conj rules (parse-rule raw)))
                    {}
                    input))

(defn contains-shiny-gold? [values rules color]
  (if (empty? values)
    false
    (or (contains? values color)
        (some
          #(contains-shiny-gold? (get rules (first %)) rules color)
          values))))

(defn bags-containing-shiny-gold
  "How many bag colors can eventually contain at least one shiny gold bag? "
  []
  (->>
    (filter #(contains-shiny-gold? (second %) parsed :shiny-gold) parsed)
    (count)))

