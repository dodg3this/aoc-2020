(ns core
  (:require [clojure.string :as str]))

(defn- get-id [barcode]
  (println )
  (let [binary (str/replace (str/replace (str/replace (str/replace barcode #"F" "0") #"L" "0") #"B" "1") #"R" "1")]
    (Integer/parseInt binary 2)))

(defn begin [barcodes]
  (let [ids (sort (map get-id barcodes))
        actualSum (reduce + ids)
        expectedSum (reduce + (range (first ids) (inc (last ids))))]
    (println "my seat number is " (- expectedSum actualSum))
    (println "max seat id is" (last ids))))

(defn -main [& args]
  (println "hello")
  (begin (str/split-lines (slurp "input"))))
