(ns core
  (:require [clojure.string :as str]
            [clojure.set :refer :all]
            [clojure.spec.alpha :as s]))

(defn parse [input]
  (let [column-size (.length (first input))
        s ^String (str/join input)]
    (into #{} (for [i (range (.length s)) :let [v (.charAt s i)] :when (not= \. v)] {:x (quot i column-size)
                                                                                     :y (rem i column-size)
                                                                                     :z 0
                                                                                     :w 0}))))

(defn neighbours [{:keys [x y z w]}]
  (for [a [x (inc x) (dec x)]
        b [y (inc y) (dec y)]
        c [z (inc z) (dec z)]
        d [w (inc w) (dec w)] :when (not (and (= a x)
                                              (= b y)
                                              (= c z)
                                              (= d w)))]
    {:x a :y b :z c :w d}))

(defn space [points]
  (reduce #(into %1 (neighbours %2)) (into #{} points) points))

(defn cycle [points]
  (let [space (space points)]
    (into #{} (for [p space :let [ns (neighbours p)
                                  c (count (filter #(contains? points %) ns))]
                    :when (or (and (contains? points p) (<= 2 c 3))
                              (and (nil? (get p points)) (= c 3)))]
                p))))

(defn begin [input]
  (println (count (nth (->> input
                            parse
                            (iterate cycle)) 6))))

(hash-set)
(defn -main [& args]
  (println "hello" args)
  (time (begin (str/split (slurp (or (first args) "sample")) #"\n"))))
