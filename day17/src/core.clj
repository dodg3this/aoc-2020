(ns core
  (:require [clojure.string :as str]
            [clojure.set :refer :all]
            [clojure.spec.alpha :as s]))

(defn parse [input]
  (let [xs (reduce (fn [{:keys [i l] :as m} v]
                     (if (= \. v)
                       (update m :i inc)
                       (-> (update m :points conj {:x (quot i l)
                                                   :y (rem i l)
                                                   :z 0
                                                   :w 0})
                           (update :i inc))))
                   {:points #{} :i 0 :l (.length (first input))} (str/join input))]
    (:points xs)))

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
  (reduce #(union (into %1 (neighbours %2))) (into #{} points) points))

(defn cycle [points]
  (loop [points points counter 0]
    (if (= 6 counter)
      points
      (let [space (space points)
            new-points (into #{} (for [p space :let [ns (neighbours p)
                                                     c (count (filter #(contains? points %) ns))]
                                       :when (or (and (contains? points p) (<= 2 c 3))
                                                 (and (nil? (get p points)) (= c 3)))]
                                   p))]
        (recur new-points (inc counter))))))

(defn begin [input]
  (let [active-points (-> input
                          parse)]
    (println (count (cycle active-points)))))

(defn -main [& args]
  (println "hello" args)
  (time (begin (str/split (slurp (or (first args) "sample")) #"\n"))))
