(ns core
  (:require [clojure.string :as str]))

(def slopes [{:x 1 :y 1} {:x 3 :y 1} {:x 5 :y 1} {:x 7 :y 1} {:x 1 :y 2}])

(defn found-a-tree? [slope row-num row]
  (let [pos-y (quot row-num (:y slope))
        pos-x (* pos-y (:x slope))
        index (rem pos-x (count row))]
    (if (= (and (= 0 (rem row-num (:y slope))) (nth row index)) \#) true false)))

(defn begin [rows]
  (let [counts (map #(count (filter true? (map-indexed (partial found-a-tree? %) rows))) slopes)]
    (println "total trees found: " counts)
    (println "total trees multiplied: " (apply * counts))))

(defn -main [& args]
  (println "hello")
  (begin (str/split-lines (slurp "input"))))
