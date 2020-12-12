(ns core
  (:require [clojure.string :as str]
            [clojure.set :refer :all]))

(def position (atom {:east 0 :north 0 :west 0 :south 0}))
(def waypoint (atom {:east 10 :north 1}))

(def directions-right [:east :south :west :north])
(def directions-left [:east :north :west :south])

(defn move-left [val]
  (swap! waypoint rename-keys {:east  (nth (cycle directions-left) (+ val (.indexOf directions-left :east)))
                               :south (nth (cycle directions-left) (+ val (.indexOf directions-left :south)))
                               :west  (nth (cycle directions-left) (+ val (.indexOf directions-left :west)))
                               :north (nth (cycle directions-left) (+ val (.indexOf directions-left :north)))})
  (println @waypoint))

(defn move-right [val]
  (swap! waypoint rename-keys {:east  (nth (cycle directions-right) (+ val (.indexOf directions-right :east)))
                               :south (nth (cycle directions-right) (+ val (.indexOf directions-right :south)))
                               :west  (nth (cycle directions-right) (+ val (.indexOf directions-right :west)))
                               :north (nth (cycle directions-right) (+ val (.indexOf directions-right :north)))})
  (println @waypoint))

(defn move-forward [val]
  (println (for [[k v] @waypoint :let [_ (swap! position assoc k (+ (* val v) (get @position k)))]] [k v])))

(defn find-dir [new-direction val]
  (let [switches (quot val 90)
        now-facing (cond
                     (= \L new-direction) (move-left switches)
                     (= \R new-direction) (move-right switches)
                     (= \F new-direction) (move-forward val)
                     (= \N new-direction) (swap! waypoint assoc :north (+ val (:north @waypoint 0)))
                     (= \E new-direction) (swap! waypoint assoc :east (+ val (:east @waypoint 0)))
                     (= \S new-direction) (swap! waypoint assoc :south (+ val (:south @waypoint 0)))
                     (= \W new-direction) (swap! waypoint assoc :west (+ val (:west @waypoint 0))))]
    @position))

(defn begin [input]
  (let [directions (map #(vector (first %) (Integer/parseInt (subs % 1))) input)]
    (println (for [[d n] directions :let [next-dir (find-dir d n)]] @position)))
  (println (+ (Math/abs (- (:east @position) (:west @position)))
              (Math/abs (- (:north @position) (:south @position))))))

(defn -main [& args]
  (println "hello" args)
  (begin (str/split (slurp (or (first args) "sample")) #"\n")))
