(ns core
  (:require [clojure.string :as str]))

(defn- reduce-to [s max]
  (first (reduce #(let [val (if %2 [(+ 1 (quot (apply + %1) 2)) (second %1)] [(first %1) (quot (apply + %1) 2)])]
                    val) [0 max] s)))

(defn- get-id [barcode]
  (let [ys (for [y barcode :while (contains? #{\B, \F} y)] (= y \B))
        xs (for [x barcode :when (contains? #{\R, \L} x)] (= x \R))
        y (reduce-to ys 127)
        x (reduce-to xs 7)]
    (+ (* y 8) x)))

(defn begin [barcodes]
  (let [ids (sort (map get-id barcodes))
        actualSum (reduce + ids)
        expectedSum (reduce + (range (first ids) (inc (last ids))))]
    (println "my seat number is " (- expectedSum actualSum))
    (println "max seat id is" (last ids))))

(defn -main [& args]
  (println "hello")
  (begin (str/split-lines (slurp "input"))))
