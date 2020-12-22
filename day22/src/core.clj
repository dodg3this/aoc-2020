(ns core
  (:require [clojure.string :as str]
            [clojure.set :refer :all]
            [clojure.spec.alpha :as s]))

(defn play [deck1 deck2]
  (loop [deck1 deck1 deck2 deck2 i 0 player1 #{} player2 #{}]
    (cond
      (empty? deck1) [nil deck2]
      (or (empty? deck2) (contains? player2 deck2) (contains? player1 deck1)) [deck1 nil]
      :default (let [draw1 (first deck1)
                     draw2 (first deck2)
                     winner (if (and (<= draw1 (count (rest deck1))) (<= draw2 (count (rest deck2))))
                              (first (play (into [] (take draw1 (rest deck1))) (into [] (take draw2 (rest deck2)))))
                              (> draw1 draw2))]
                 (if winner
                   (recur (conj (into [] (rest deck1)) draw1 draw2) (into [] (rest deck2)) (inc i) (conj player1 deck1) (conj player2 deck2))
                   (recur (into [] (rest deck1)) (conj (into [] (rest deck2)) draw2 draw1) (inc i) (conj player1 deck1) (conj player2 deck2)))))))

(defn begin[[player1 player2]]
  (let [deck1 (into [] (map #(Integer/parseInt %) (rest (str/split player1 #"\n"))))
        deck2 (into [] (map #(Integer/parseInt %) (rest (str/split player2 #"\n"))))
        end-game (play deck1 deck2)
        winning-deck (or (first end-game) (second end-game))]
    (println end-game)
    (println (reduce + (map #(* (first %) (second %)) (partition 2 (interleave winning-deck (range (count winning-deck) 0 -1))))))))

(defn -main [& args]
  (println "hello" args)
  (time (begin (str/split (slurp (or (first args) "sample")) #"\n\n"))))
