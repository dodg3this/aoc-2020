(ns core
  (:require [clojure.string :as str]
            [clojure.set :refer :all]
            [clojure.spec.alpha :as s]))

(defn evaluate [rule first-pass]
  (let [pass (loop [rule rule]
               (if (some number? (flatten rule))
                 (let [pass (for [r rule] (cond
                                            (number? r) (get first-pass r)
                                            (string? r) r
                                            :default (evaluate r first-pass)))]
                   (recur pass))
                 rule))]
    pass))

(defn resolve [combine pair]
  (let [v (cond
            (empty? (first pair)) (second pair)
            (empty? (second pair)) (first pair)
            (every? string? pair) (str/join pair)
            (every? set? pair) (let [[s1 s2] pair] (into #{} (for [e1 s1 e2 s2] (str e1 e2))))
            (and (> (count pair) 2) (string? (first pair)) (set? (second pair))) (into #{} (for [e (second pair)] (str (first pair) e (nth pair 2))))
            (and combine (string? (first pair)) (set? (second pair))) (into #{} (for [e (second pair)] (str (first pair) e)))
            (and (not combine) (string? (first pair)) (set? (second pair))) (conj (second pair) (first pair))
            (and combine (set? (first pair)) (string? (second pair))) (into #{} (for [e (first pair)] (str e (second pair))))
            (and (not combine) (set? (first pair)) (string? (second pair))) (conj (first pair) (second pair))
            )]
    v))

(defn build-combos [last-pass counter]
  (let [val (loop [c last-pass counter counter]
              (if (some seq? c)
                (recur (for [i (range (count c)) :let [s (nth c i)]] (if (seq? s) (build-combos s (+ (* 10 counter) i)) s)) (inc counter))
                (if (set? c) c
                             (let [
                                   pairs (filter (fn [p]
                                                   (not (symbol? (first p)))) (partition-by symbol? c))
                                   resolved-pairs (map (partial resolve true) pairs)
                                   v (cond
                                       (every? string? resolved-pairs) (into #{} resolved-pairs)
                                       (every? set? resolved-pairs) (apply union resolved-pairs)
                                       :default resolved-pairs)]
                               (if (set? v) v (resolve false v))))))]
    val))

(defn begin [[rules messages]]
  (let [rules (into (sorted-map) (map (fn [s]
                                        (let [[k v] (str/split s #": ")
                                              vs (str/split v #" ")]
                                          [(read-string k) vs])) rules))
        base (into {} (map (fn [[k v]]
                             [k (str/replace (first v) "\"" "")]) (filter (fn [[k [v]]]
                                                                            (or (= "\"a\"" v) (= "\"b\"" v))) rules)))
        first-pass (into (sorted-map) (for [[k v] (merge rules base) :let [
                                                                           vs (if (coll? v) (map #(get base (read-string %) (read-string %)) v)
                                                                                            v)]] [k vs]))
        forty-two-combs (build-combos (evaluate (get first-pass 42) first-pass) 1)
        thirty-one-combs (build-combos (evaluate (get first-pass 31) first-pass) 1)
        split-messages (map #(map (partial apply str) (partition-all 8 %)) messages)]

    (println (count (filter #(and (= :31 (last %))
                                  (= :42 (first %))
                                  (= :42 (second %))
                                  (< (:31 (frequencies %)) (:42 (frequencies %)))
                                  (= 2 (count (partition-by identity %)))) (map (fn [split-message] (for [split split-message :let [found-in-42 (get forty-two-combs split)
                                                                                                                          found-in-31 (get thirty-one-combs split)] :when (or found-in-42 found-in-31)] (if found-in-42 :42 :31))) split-messages))))))

(defn -main [& args]
  (println "hello" args)
  (time (begin (map #(str/split % #"\n") (str/split (slurp (or (first args) "sample2")) #"\n\n")))))
