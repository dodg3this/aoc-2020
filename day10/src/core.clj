(ns core
  (:require [clojure.string :as str]))

(defn begin [input]
  (let [jolts (sort (map #(Integer/parseInt %) input))
        pairs (partition 2 1 jolts)
        diff (for [[x y] pairs] (- y x))
        freq (frequencies diff)]
    (println freq)
    (println (apply * (vals freq)))))

(defn beginC [input]
  (let [voltages (sort (map #(Integer/parseInt %) input))
        exists (zipmap voltages (range))
        ways (atom {(- (count voltages) 1) 1})
        waysB (for [v (rest (reverse voltages))] (let [sum (atom 0)
                                                       i (get exists v)]
                                                   (loop [j 1]
                                                     (when (< j 4)
                                                       (if (get exists (+ v j))
                                                         (swap! sum + (get @ways (get exists (+ v j)) 0)))
                                                       (recur (+ j 1))))
                                                   (swap! ways assoc i @sum)
                                                   @sum))]

    (println "*************")
    (println waysB)
    (println @ways)))

(defn -main [& args]
  (println "hello" args)
  (beginC (str/split (slurp (or (first args) "input")) #"\n")))
