(ns aoc.core
  (:gen-class)
  (:require [clojure.string :as str]
            [aoc.day01 :as day01]
            [aoc.day02 :as day02]))

(defn- get-input [filename]
  (str/split-lines (slurp filename)))

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

(defn day02 [filename solution]
  (println filename solution)
  (let [input (get-input filename)
        count (day02/find-valid-passwords input solution)]
    (println "total valid passwords found: " count)))

(defn day01 [filename solution]
  (println filename solution)
  (let [input (get-input filename)
        comb (first (day01/find-combination 2020 input (keyword (str solution))))]
    (println "combination found" comb "with a product of" (apply * (map #(Integer/parseInt %) comb)))))

(defn -main
  "I don't do a whole lot ... yet."
  [_ day & args]
  (println "Hello, World!" day args)
  (when-let [fun (ns-resolve *ns* (symbol "aoc.core" day))]
    (apply fun (str "resources/" day ".txt") (first args))))
