(ns aoc.d4.password-processing
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [aoc.d2.password-philosophy :as pp]))

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

;; little workaround, eval the additional specs at runtime,
;; so that part-one of this exercise still works when using the CLI.
(def field-specs '(do
                    (require '[clojure.spec.alpha :as s])
                    (require '[clojure.string :as str])
                    (require '[aoc.d2.password-philosophy :as pp])
                    (s/def ::byr (s/and ::pp/str->int
                                        #(<= 1920 %)
                                        #(>= 2002 %)))
                    (s/def ::iyr (s/and ::pp/str->int
                                        #(<= 2010 %)
                                        #(>= 2020 %)))
                    (s/def ::eyr (s/and ::pp/str->int
                                        #(<= 2020 %)
                                        #(>= 2030 %)))
                    (s/def ::hgt (s/or :cm (s/and #(str/ends-with? % "cm")
                                                  (s/conformer #(str/replace % #"cm" ""))
                                                  ::pp/str->int
                                                  #(<= 150 %)
                                                  #(>= 193 %))
                                       :in (s/and #(str/ends-with? % "in")
                                                  (s/conformer #(str/replace % #"in" ""))
                                                  ::pp/str->int
                                                  #(<= 59 %)
                                                  #(>= 76 %))))
                    (s/def ::hcl (s/and string?
                                        #(re-matches #"#[0-9a-f]{6}" %)))
                    (s/def ::ecl #{"amb" "blu" "brn" "gry" "grn" "hzl" "oth"})
                    (s/def ::pid (s/and string?
                                        #(re-matches #"[0-9]{9}" %)))))

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

(defn passports-with-valid-fields
  "Count the number of valid passports - those that have all required fields and valid values. Continue to treat cid as optional. In your batch file, how many passports are valid?"
  []
  (do
    (eval field-specs)
    (count-invalid-passports)))

;; repl examples:
#_(s/conform ::passports input)

#_(s/valid? ::byr "1920")
#_(s/explain ::hgt "200cm")
#_(s/valid? ::hcl "#g23123")
#_(s/valid? ::ecl "gry")
#_(s/valid? ::pid "000000009")

