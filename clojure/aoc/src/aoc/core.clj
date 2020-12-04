(ns aoc.core
  (:gen-class)
  (:require [clojure.string :as str]
            [aoc.day01 :as day01]
            [aoc.day02 :as day02]
            [aoc.day03 :as day03]
            [aoc.day04 :as day04]))

(defn- get-input [filename]
  (str/split-lines (slurp filename)))

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

(defn day03 [filename solution]
  (println filename solution)
  (let [input (get-input filename)
        counts (day03/begin input solution)]
    (println "total trees found: " counts)
    (println "total trees multiplied: " (apply * counts))))

(defn day04 [filename solution]
  (println filename solution)
  (let [input (get-input filename)
        count (day04/begin input solution)]
    (println "valid passports:" count)))

(defn -main
  "I don't do a whole lot ... yet."
  [_ day & args]
  (println "Hello, World!" day args)
  (when-let [fun (ns-resolve *ns* (symbol "aoc.core" day))]
    (apply fun (str "resources/" day ".txt") (first args))))
