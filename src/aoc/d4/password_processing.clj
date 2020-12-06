(ns aoc.d4.password-processing
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(def input (slurp (clojure.java.io/reader "src/aoc/d4/input.txt")))

(defn keyValues->map
  "transforms a collection of key-value maps into a single map"
  [coll]
  (reduce (fn [acc kv]
            (let [k (keyword (str 'aoc.d4.password-processing) (::key kv))
                  v (::value kv)]
              (into acc {k v})))
          {}
          coll))

(s/def ::key-values (s/and (s/conformer #(str/split % #":"))
                           (s/cat ::key #{"byr" "iyr" "eyr" "hgt" "hcl" "ecl" "pid" "cid"}
                                  ::value string?)))

(s/def ::passport (s/and (s/conformer #(str/split % #"\n|\s"))
                         (s/* ::key-values)
                         (s/conformer keyValues->map)))

(s/def ::passports (s/and string?
                          (s/conformer #(str/split % #"\n\n"))
                          (s/* ::passport)))

(s/def ::valid-passport (s/keys :req [::byr ::iyr ::eyr ::hgt ::hcl ::ecl ::pid]
                                :opt [::cid]))

(defn count-invalid-passports
  "Count the number of valid passports - those that have all required fields. Treat cid as optional. In your batch file, how many passports are valid?"
  []
  (let [passports (s/conform ::passports input)]
    (->> passports
         (filter #(s/valid? ::valid-passport %))
         (count))))

;; repl examples:

#_(s/conform ::passports input)

