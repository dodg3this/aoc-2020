(ns core
  (:require [clojure.string :as str]
            [clojure.set :refer :all]))

(defn begin [game]
  (let [initial-numbers (map #(Integer/parseInt %) (str/split (first game) #","))
        turn (zipmap initial-numbers [{:min 0 :max 0} {:min 1 :max 1} {:min 2 :max 2} {:min 3 :max 3} {:min 4 :max 4} {:min 5 :max 5}])]
    (println (loop [t turn i (count initial-numbers) prev (last initial-numbers)]
               (if (= i 30000000) prev
                              (if (get t prev)
                                (let [rng (get t prev)
                                      diff (- (:max rng 0) (:min rng ))]
                                  (do
                                      (recur (assoc t diff {:max i :min (:max (get t diff) i)}) (inc i) diff)))
                                (recur (assoc t prev {:min i :max i}) (inc i) 0)))))))

(defn -main [& args]
  (println "hello" args)
  (time (begin (str/split (slurp (or (first args) "sample")) #"\n"))))
