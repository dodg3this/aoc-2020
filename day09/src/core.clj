(ns core
  (:require [clojure.string :as str]))

(def preamble 25)

(def found (atom 0))

(defn find [m]
  (if (= 0 @found)
    (let [searchable (take preamble m)
          sum (nth m preamble)
          sums (into #{} (for [x searchable y searchable :when (not= x y)] (+ x y)))]
      (if (get sums sum) (find (drop 1 m)) (reset! found sum)))))

(defn find-fault [m]
  (if (not (empty? m))
    (let [sum (atom 0)
          sqnce (take-while #(<= (swap! sum + %) @found) m)]
      (if (= (reduce + sqnce) @found)
        (do (println "range " sqnce)
            (println (sort sqnce)))
        (find-fault (rest m))))))

(defn begin [input]
  (let [numbers (map #(Integer/parseInt %) input)]
    (find numbers)
    (find-fault numbers)
    (println "*****" @found)))


(defn -main [& args]
  (println "hello")
  (begin (str/split (slurp "input") #"\n")))
