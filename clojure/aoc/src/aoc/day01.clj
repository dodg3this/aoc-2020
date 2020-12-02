(ns aoc.day01
  (:require [clojure.string :as str]))

(defn find-combination [sum input type]
  (let [contains (reduce #(assoc %1 %2 true) {} input)]
    (cond
      (= :a type) (for [x input :when (contains? contains (str (- sum (Integer/parseInt x))))] [x (str (- sum (Integer/parseInt x)))])
      (= :b type) (for [x input y input :when (contains? contains (str (- sum (Integer/parseInt x) (Integer/parseInt y))))] [x y (str (- sum (Integer/parseInt x) (Integer/parseInt y)))])
      :else nil)))