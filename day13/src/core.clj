(ns core
  (:require [clojure.string :as str]
            [clojure.set :refer :all]))

(defn begin [input]
  (let [earliest (Integer/parseInt (first input))
        buses (map #(Integer/parseInt %) (filter #(not (= % "x")) (str/split (second input) #",")))
        find-latest (fn [bus] (loop [n 0]
                                (let [latest (* n bus)]
                                  (if (>= latest earliest)
                                    latest
                                    (recur (inc n))))))
        latest-timings (map find-latest buses)
        latest (apply min (map find-latest buses))
        idx (.indexOf latest-timings latest)]

    (println (* (nth buses idx) (- latest earliest)))
    ))

(def min-value (atom 0))
(def product (atom 1))

;Use Euclidean theorem to calculate the congruent value
(defn- find-min-value [bus index]
  (if-not (zero? (rem (+ @min-value index) bus))
    (do (swap! min-value + @product)
        (find-min-value bus index))
    (swap! product * bus)))

(defn beginB [input]
  (let [buses (map #(if (= "x" %) 1 (Integer/parseInt %)) (str/split (second input) #","))
        buses-indexed (map-indexed list buses)]
    (into [] (for [[i b] buses-indexed :let [_ (find-min-value b i)]] b))
    (println @min-value)))

(defn -main [& args]
  (println "hello" args)
  (beginB (str/split (slurp (or (first args) "sample")) #"\n")))
