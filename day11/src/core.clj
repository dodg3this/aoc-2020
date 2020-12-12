(ns core
  (:require [clojure.string :as str]))

(def column-size (atom 10))
(def row-size (atom 15))
(defn neighbours [seat-num]
  (let [rows [(- seat-num @column-size) (+ seat-num @column-size)]
        remainder (rem seat-num @column-size)
        neighbours (cond
                     (zero? remainder) (conj rows (- seat-num @column-size -1) (+ seat-num @column-size 1) (inc seat-num))
                     (= (- @column-size 1) remainder) (conj rows (- seat-num @column-size 1) (+ seat-num @column-size -1) (dec seat-num))
                     :default [(dec seat-num) (inc seat-num) (- seat-num @column-size) (+ seat-num @column-size)
                               (- seat-num @column-size 1) (- seat-num @column-size -1) (+ seat-num @column-size -1) (+ seat-num @column-size 1)])]

    (filter #(not (neg? %))
            neighbours)))

(defn neighboursB [seat-num]
  (let [x (rem seat-num @column-size)
        y (quot seat-num @column-size)
        left (range  (- seat-num 1) (- (- seat-num x) 1) -1)
        right (range (+ seat-num 1) (+ seat-num (- @column-size x)))
        up (range (- seat-num @column-size) -1 (- @column-size))
        down (range (+ seat-num @column-size) (* @column-size @row-size) @column-size)
        down-right (for [d down :let [y_ (quot d @column-size)] :while (> @column-size (+ x (- y_ y)))] (+ d (- y_ y)))
        down-left (for [d down :let [y_ (quot d @column-size)] :while (>= x (- y_ y))] (- d (- y_ y)))
        up-right (for [u up :let [y_ (quot u @column-size)] :while (> @column-size (+ x (- y y_)))] (+ u (- y y_)))
        up-left (for [u up :let [y_ (quot u @column-size)] :while (>= x (- y y_))] (- u (- y y_)))]
    [up down right left down-right down-left up-left up-right]
    ))

(defn find-neighbour [seats indices]
  ;(println "@@@@@@" indices)
  (let [neighbour (get seats (first (filter #(not (= \. (get seats %))) indices)) nil)]
    ;(if (nil? neighbour) (println "#########" (map #(get seats % nil) indices)))
    ;(println neighbour)
    neighbour)
  )

(defn state [seats seat index]
  ;(println (count seats) seat index)
  ;(println "$$$$$$$$")

  (let [new-state
        (if (= \. seat) seat
                        (let [neighs (map #(find-neighbour seats %) (neighboursB index))
                              ;neighs (map #(get seats % nil) (neighbours index))
                              freq (frequencies neighs)
                              ;_ (println "*******" freq (neighbours index) neighs)
                              occupied (get freq \# 0)]
                          (cond
                            (and (= \L seat) (= occupied 0)) \#
                            (and (= \# seat) (>= occupied 5)) \L
                            :default seat)))]
    ;(println new-state)
    new-state))

(defn begin [input]
  (reset! column-size (count (first input)))
  (reset! row-size (count input))
  (let [seats (flatten (map #(into [] %) input))

        seatsMap (zipmap (range) seats)
        counter (atom 0)
        result (loop [s seatsMap p []]
                 ;(println "equals " (last (drop-last p)) (last p) (= (last (drop-last p)) (last p)))

                 (if
                   (and (not (empty? p)) (= (last (drop-last p)) (last p)))
                   ;(not (and (< @counter 3) (or (empty? p) (not (= (last (last p)) (last p))))))
                   p
                   (let [newMap (into {} (for [[y x] s] [y (state s x y)]))]
                     ;(println "^^^^^^^^^^")
                     ;(println p)
                     (swap! counter inc)
                     (recur newMap (conj p newMap)))))]

    ;(println seats)
    ;(println "******" seatsMap)

    (println "$$$$$$$")
    ;(println (partition @row-size (for [[x y] (sort (last result))] y)))
    (println (count (filter #(= % \#) (vals (last result)))))
    (println "##############")))

(defn -main [& args]
  (println "hello" args)
  (begin (str/split (slurp (or (first args) "sample")) #"\n")))
