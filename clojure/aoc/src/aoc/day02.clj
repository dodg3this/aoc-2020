(ns aoc.day02
  (:require [clojure.string :as str]))

(defn- parse-each-record [record]
  (let [[range char password] (str/split record #" ")
        [range-min range-max] (str/split range #"-")
        [character] (str/split char #":")]
    {:min (Integer/parseInt range-min) :max (Integer/parseInt range-max) :char character :password password}))

(defn- is-valid-password? [record]
  (let [count (get (frequencies (:password record)) (first (:char record)) 0)
        valid? (and (<= (:min record) count (:max record)))]
    valid?))

(defn- is-valid-password-b? [record]
  (let [password (:password record)
        char (first (:char record))
        first-char (nth password (- (:min record) 1))
        second-char (nth password (- (:max record) 1))
        present-at-both-places (= char first-char second-char)
        first-pos-present (= char first-char)
        second-pos-present (= char second-char)]
    (if (not present-at-both-places) (or first-pos-present second-pos-present))))

(defn find-valid-passwords [input rule-type]
  (let [records (map parse-each-record input)
        solution-type (keyword (str rule-type))
        valid-fn (cond (= :a solution-type) is-valid-password?
                       (= :b solution-type) is-valid-password-b?
                       :else is-valid-password?)
        output (map valid-fn  records)]
    (count (filter true? output))))
