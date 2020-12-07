(ns aoc.d6.custom-customs
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [clojure.set :refer [union intersection]]))

(def input (slurp (clojure.java.io/reader "src/aoc/d6/input.txt")))

;; 4. A valid answer just has to match the predicate 'char?'
(s/def ::answer char?)

;; 3. A person has a (hash-)set of answers, which is not empty
(s/def ::person (s/and (s/conformer (comp set seq))
                       set?
                       #(not-empty %)
                       (s/every ::answer)))

;; 2. A group as at least one person
(s/def ::group (s/and (s/conformer #(str/split % #"\n"))
                       (s/+ ::person)))

;; 1. Answers contains zero or more groups
(s/def ::answers (s/and string?
                          (s/conformer #(str/split % #"\n\n"))
                          (s/* ::group)))

(defn group-answers-xf [set-op] (comp
                                  (map set-op)
                                  (map count)))

(defn sum-of-anyone-yes
  "For each group, count the number of questions to which anyone answered \"yes\". What is the sum of those counts?"
  []
  (let [answers (s/conform ::answers input)]
    (transduce (group-answers-xf #(apply union %)) + answers)))


(defn sum-of-everyone-yes
  "For each group, count the number of questions to which everyone answered \"yes\". What is the sum of those counts?"
  []
  (let [answers (s/conform ::answers input)]
    (transduce (group-answers-xf #(apply intersection %)) + answers)))


;; repl-example
#_(s/conform ::answers input)
