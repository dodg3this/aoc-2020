(ns aoc.core
  (:gen-class)
  (:require [clojure.string :as str]))

(defn find-combination [sum input type]
  (cond
    (= :a type) (for [x input y input :when (= sum (+ (Integer/parseInt x) (Integer/parseInt y)))] [x y])
    (= :b type) (for [x input y input z input :when (= sum (+ (Integer/parseInt x) (Integer/parseInt y) (Integer/parseInt z)))] [x y z])
    :else nil))

(defn day01 [filename solution]
  (println filename solution)
  (let [contents (slurp filename)
        input (str/split-lines contents)]
    (println "combination found" (first (find-combination 2020 input (keyword (str solution)))))))

(defn -main
  "I don't do a whole lot ... yet."
  [_ day & args]
  (println "Hello, World!" day args)
  (when-let [fun (ns-resolve *ns* (symbol "aoc.core" day))]
    (apply fun (str "resources/" day ".txt") (first args))))
