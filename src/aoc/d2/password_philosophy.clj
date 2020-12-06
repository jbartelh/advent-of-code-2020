(ns aoc.d2.password-philosophy
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(defn str->int? [s]
  (if (string? s)
    (try
      (Integer/parseInt s)
      (catch Exception _
        :clojure.spec/invalid))))

(defn count-char [{:keys [char password]}]
  (->> (seq password)
       (filter #{char})
       (count)))

(defn char-at-pos [{:keys [first-number second-number password]}]
  (let [pw-seq (into [] (seq password))
        ->char #(get pw-seq (dec %))]
    {:first-char (->char first-number)
     :second-char (->char second-number)}))

(s/def ::str->int (s/conformer str->int? str))

(s/def ::more-than-lowest #(<= (:first-number %) (count-char %)))
(s/def ::less-than-highest #(<= (count-char %) (:second-number %)))

(s/def ::first-not-second #(and (= (:first-char %)  (:char %)) (not= (:second-char %) (:char %))))
(s/def ::second-not-first #(and (= (:second-char %)  (:char %)) (not= (:first-char %) (:char %))))
(s/def ::either-first-or-second-char (s/or ::first-char-matches ::first-not-second
                                           ::second-char-matches ::second-not-first))

(s/def ::base-password-policy (s/and string?
                                     (s/conformer #(str/split % #"[\-|\s|:]+" 4))
                                     (s/cat :first-number ::str->int
                                            :second-number ::str->int
                                            :char (s/and #(= 1 (count %))
                                                         (s/conformer first))
                                            :password string?)))

(s/def ::upper-lower-bound-policy (s/and ::base-password-policy
                                         ::more-than-lowest
                                         ::less-than-highest))

(s/def ::position-based-policy (s/and ::base-password-policy
                                      (s/conformer #(merge % (char-at-pos %)))
                                      ::either-first-or-second-char))

(def input (line-seq (clojure.java.io/reader "src/aoc/d2/input.txt")))

(defn count-invalid-pws-by-spec [pw-spec]
  (fn [pw-list]
    (->> pw-list
         (filter (partial s/valid? pw-spec))
         (count))))

(defn count-invalid-pws
  "How many passwords are valid according to their policies?"
  []
  (let [counter-fn (count-invalid-pws-by-spec ::upper-lower-bound-policy)]
    (counter-fn input)))

(defn invalid-pws-by-pos
  "Part-two: How many passwords are valid according to position-based-policies?"
  []
  (let [counter-fn (count-invalid-pws-by-spec ::position-based-policy)]
    (counter-fn input)))

; repl-examples:
#_(first input)
;=> "13-15 c: cqbhncccjsncqcc"

#_(s/valid? ::upper-lower-bound-policy (first input))
;=> false

#_(s/explain ::upper-lower-bound-policy (first input))
;{:lowest 13, :highest 15, :char \c, :password "cqbhncccjsncqcc"} - failed: (<= (:lowest %) (count-char %)) spec: :aoc.d2.password-philosophy/more-than-lower
;=> nil


#_ (char-at-pos {:first 13, :second 15, :char \c, :password "cqbhncccjsncqcc"})
;=> {:first-char \q, :second-char \c}

#_(s/valid? ::either-first-or-second-char {:char \c :first-char \c :second-char \c})
;=> false

#_(s/explain ::either-first-or-second-char {:char \c :first-char \c :second-char \c})
;{:char \c, :first-char \c, :second-char \c} - failed: (and (= (:first-char %) (:char %)) (not= (:second-char %) (:char %))) at: [:aoc.d2.password-philosophy/first-char-matches] spec: :aoc.d2.password-philosophy/first-not-second
;{:char \c, :first-char \c, :second-char \c} - failed: (and (= (:second-char %) (:char %)) (not= (:first-char %) (:char %))) at: [:aoc.d2.password-philosophy/second-char-matches] spec: :aoc.d2.password-philosophy/second-not-first
;=> nil

#_(s/conform ::position-based-policy (first input))
;=>
;[:aoc.d2.password-philosophy/second-char-matches
; {:first 13, :second 15, :char \c, :password "cqbhncccjsncqcc", :first-char \q, :second-char \c}]