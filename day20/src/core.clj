(ns core
  (:require [clojure.string :as str]
            [clojure.set :refer :all]
            [clojure.spec.alpha :as s]))

(def size 3)

(defn right [v]
  (apply str (map last v)))

(defn left [v]
  (apply str (map first v)))

(defn top [v]
  (first v))

(defn bottom [v]
  (last v))

(defn side-match [side [key sides]]
  (let [direction (cond
                    (= (top sides) side) :top
                    (= (bottom sides) side) :bottom
                    (= (right sides) side) :right
                    (= (left sides) side) :left
                    (= (str/reverse (top sides)) side) :reverse-top
                    (= (str/reverse (bottom sides)) side) :reverse-bottom
                    (= (str/reverse (right sides)) side) :reverse-right
                    (= (str/reverse (left sides)) side) :reverse-left
                    :default nil)]
    direction))

(defn rotate-left [[name frame]]
  [name (into '() (for [i (range (count frame))] (apply str (for [v frame] (nth v i)))))])

(defn reverse-horizontal [[name sides]]
  [name (reverse sides)])

(defn reverse-vertical [[name sides]]
  [name (map str/reverse sides)])

(defn get-new-orientation-to-match-right [side match]
  (let [corrected (iterate rotate-left match)
        new-orientation (condp = side
                          :left match
                          :top (reverse-horizontal (nth corrected 1))
                          :right (reverse-horizontal (nth corrected 2))
                          :bottom (nth corrected 3)
                          :reverse-left (reverse-horizontal match)
                          :reverse-right (nth corrected 2)
                          :reverse-bottom (reverse-horizontal (nth corrected 3))
                          :reverse-top (nth corrected 1)
                          nil)]
    new-orientation))

(defn get-new-orientation-to-match-bottom [side match]
  (let [corrected (iterate rotate-left match)
        new-orientation (condp = side
                          :bottom (reverse-vertical (nth corrected 2))
                          :top match
                          :left (reverse-vertical (nth corrected 3))
                          :right (nth corrected 1)
                          :reverse-left (nth corrected 3)
                          :reverse-right (reverse-vertical (nth corrected 1))
                          :reverse-bottom (nth corrected 2)
                          :reverse-top (reverse-vertical match)
                          nil)]
    new-orientation))

(defn mosaic [tiles]
  (loop [frames [["Tile 1187:" (get tiles "Tile 1187:")]] i 1]
    (if (= (* size size) i) frames
                            (let [column (rem i size)
                                  row (quot i size)
                                  [tile-name tile] (if (= 0 column) (nth frames (+ column (* size (- row 1)))) (last frames))
                                  match (if (= 0 column) (first (filter #(and (side-match (bottom tile) %)
                                                                              (not= tile-name (first %))) tiles))
                                                         (first (filter #(and (side-match (right tile) %)
                                                                              (not= tile-name (first %))) tiles)))
                                  vside (if (= 0 row) :top (side-match (bottom (second (nth frames (+ column (* size (- row 1)))))) match))
                                  vertically-oriented-tile (if (= 0 row) match (get-new-orientation-to-match-bottom vside match))
                                  hside (side-match (right tile) vertically-oriented-tile)
                                  oriented-tile (if (= 0 column) vertically-oriented-tile (get-new-orientation-to-match-right hside vertically-oriented-tile))]
                              (recur (conj frames oriented-tile) (inc i))))))


(defn sea-monsters [partitions]
  (filter #(re-seq #".{18}(#).\n(#)....(##)....(##)....(###)\n.(#)..(#)..(#)..(#)..(#)..(#).{3}" (str/join "\n" %)) (partition 3 (apply interleave (map #(map str/join (partition 20 1 %)) partitions)))))

(defn begin [tiles]
  (let [mosaic (mosaic tiles)
        total-#s (count (filter #(= \# %) (str/join (flatten (for [row (partition size mosaic)]
                                                               (for [i (range 1 9)] (apply str (for [[k r] row] (subs (nth r i) 1 9)))))))))
        total-sea-monsters (reduce + (map count (map sea-monsters (partition 3 1 (second (reverse-vertical (nth (iterate rotate-left ["mosaic" (flatten (for [row (partition size mosaic)]
                                                                                                                                                          (for [i (range 1 9)] (apply str (for [[k r] row] (subs (nth r i) 1 9))))))]) 3)))))))]
    (println "roughness of sea is" (- total-#s (* 15 total-sea-monsters)))))

(defn -main [& args]
  (println "hello" args)
  (if (= "input" (first args)) (alter-var-root (var size) (fn [_] 12)))
  (time (begin (into {} (map (fn [tiles]
                               (let [inpt (str/split tiles #"\n")]
                                 [(first inpt) (rest inpt)])) (str/split (slurp (or (first args) "sample")) #"\n\n"))))))
