(ns core
  (:require [clojure.string :as str]
            [clojure.set :refer :all]
            [clojure.spec.alpha :as s])) 5

(def sample "esew")

(re-seq #"(se|ne|w|sw|nw|e)*\/g" sample)

(defn parse [line]
  (loop [line line steps []]
    (if (empty? line) steps
                        (cond
                          (= \e (first line)) (recur (rest line) (conj steps (str \e)))
                          (= \w (first line)) (recur (rest line) (conj steps (str \w)))
                          (= \s (first line)) (recur (rest (rest line)) (conj steps (str (first line) (first (rest line)))))
                          (= \n (first line)) (recur (rest (rest line)) (conj steps (str (first line) (first (rest line)))))))))


(defn neighbours [[x y]]
  #{[(+ 2 x) y]
    [(- x 2) y]
    [(inc x) (inc y)]
    [(inc x) (dec y)]
    [(dec x) (dec y)]
    [(dec x) (inc y)]})

(defn traverse [steps]
  (loop [steps steps [x y] [0 0]]
    (if (empty? steps) [x y]
                       (recur (rest steps) (condp = (first steps)
                                             "e" [(+ 2 x) y]
                                             "w" [(- x 2) y]
                                             "se" [(inc x) (dec y)]
                                             "sw" [(dec x) (dec y)]
                                             "nw" [(dec x) (inc y)]
                                             "ne" [(inc x) (inc y)])))))

(defn change-art [black-tiles]
  (let [tiles (apply union (map neighbours black-tiles))
        round (for [tile tiles :let [adjacent-blacks (count (intersection (neighbours tile) black-tiles))]] (if (get black-tiles tile)
                                                                                                                (if (or (zero? adjacent-blacks) (> adjacent-blacks 2)) [tile 2] [tile 1])
                                                                                                                (if (= 2 adjacent-blacks) [tile 1] [tile 2])))]
    (into #{} (map first (filter #(= 1 (second %)) round)))))

(defn begin [input]
  (let [instructions (map parse input)
        black-tiles (into #{} (map first (filter #(= 1 (second %)) (frequencies (map traverse instructions)))))]
    (println (count (nth (iterate change-art black-tiles) 100)))))

(defn -main [& args]
  (println "hello" args)
  (time (begin (str/split (slurp (or (first args) "sample")) #"\n"))))
