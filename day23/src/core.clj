(ns core
  (:require [clojure.string :as str]
            [clojure.set :refer :all]
            [clojure.spec.alpha :as s])) 5

(def number-of-cups 1000000)

(defn play [[cups current]]
  (let [
        a (nth cups current)
        b (nth cups a)
        c (nth cups b)
        next (loop [next (dec current)]
               (if (and (> next 0) (not-any? #(= next %) [a b c])) next
                                                                   (if (<= next 0) (recur number-of-cups)
                                                                                   (recur (dec next)))))
        cursor (nth cups c)]
    [(assoc! (assoc! (assoc! cups current (nth cups c)) c (nth cups next)) next a) cursor]))

(defn begin [input]
  (let [initial-cups (map #(Integer/parseInt (str %)) input)
        a (partition 2 1 initial-cups)
        cups (vec (repeat 10 0))
        cups (loop [cups cups a a]
               (if (empty? a) cups
                              (let [[n v] (first a)]
                                (recur (assoc cups n v) (rest a)))))
        cups (if (> number-of-cups 9) (assoc cups (last initial-cups) 10) cups)
        cups (vec (concat cups (range 11 (+ 2 number-of-cups))))
        cups (if (> number-of-cups 9) (assoc cups number-of-cups (first initial-cups)) (assoc cups (last initial-cups) 3))
        [final _] (nth (iterate play [(transient cups) (first initial-cups)]) 10000000)
        final (persistent! final)
        one (nth final 1)
        two (nth final one)]
    (println one " * " two " = " (* one two))))

(defn -main [& args]
  (println "hello" args)
  (time (begin (slurp (or (first args) "sample")))))
