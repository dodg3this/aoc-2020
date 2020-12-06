(ns core
  (:require [clojure.string :as str]))

(defn- get-all-answered-count [m]
  (println m)
  (let [num-person (+ 1 (get m \newline 0))]
    (count (filter (fn [[_ v]] (= num-person v)) m))))

(defn begin [groups]
  (let [answers (for [group groups :let [y (filter #(not (= \newline %)) (into #{} group))]] y)
        frequencies (for [group groups :let [y (frequencies group)]] y)]

    (println (reduce #(+ %1 (count %2)) 0 answers))
    (println (reduce #(+ %1 (get-all-answered-count %2)) 0 frequencies))))

(defn -main [& args]
  (println "hello")
  (begin (str/split (slurp "input") #"\n\n")))
