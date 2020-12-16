(ns core
  (:require [clojure.string :as str]
            [clojure.set :refer :all]
            [clojure.spec.alpha :as s]))

(defmacro rule-spec [^String s]
  `(let [
         split# (first (re-seq #"^([^0-9]*): (\d+)-(\d+) or (\d+)-(\d+)" ~s))
         rule-attr# (keyword (str *ns*) (second split#))
         a# (nth split# 2)
         b# (nth split# 3)
         c# (nth split# 4)
         d# (nth split# 5)]
     (s/def rule-attr#
       (s/or ::range-a #(<= (Integer/parseInt a#) % (Integer/parseInt b#))
             ::range-b #(<= (Integer/parseInt c#) % (Integer/parseInt d#))))))

(defn begin [[r your-ticket nearby-tickets]]
  (let [rules (str/split r #"\n")
        ticket (map #(Integer/parseInt %) (str/split (second (str/split your-ticket #"\n")) #","))
        tickets (map #(map (fn [v] (Integer/parseInt v)) (str/split % #",")) (rest (str/split nearby-tickets #"\n")))
        is-valid? (memoize (fn [v] (some #(s/valid? (rule-spec %) v) rules)))
        valid-tickets (for [ticket tickets :when (every? is-valid? ticket)] ticket)
        valid-rules (fn [number]
                      (filter #(s/valid? (rule-spec %) number) rules))
        result (loop [vts valid-tickets m {} i 0]
                 (if (empty? vts) m
                                  (let [t (for [[i rs] (zipmap (range) (map valid-rules (first vts)))] [i (intersection (into #{} rs) (get m i (into #{} rs)))])]
                                    (recur (rest vts)
                                           (into m t)
                                           (inc i)))))
        mappings (loop [r result i 0]
                   (if (= i 20) r
                                (let [single-entries (apply union (for [[k v] r :when (= 1 (count v))] v))]
                                  (recur (into {} (map (fn [[k v]]
                                                         (if (= 1 (count v)) [k v] [k (difference v single-entries)])) r)) (inc i)))))]

    (println mappings)
    (println (apply * (map #(nth ticket %) (for [[k v] (filter (fn [[_ v]]
                                                                 (str/includes? (first v) "departure")) mappings)] k))))))

(defn -main [& args]
  (println "hello" args)
  (time (begin (str/split (slurp (or (first args) "sample")) #"\n\n"))))
