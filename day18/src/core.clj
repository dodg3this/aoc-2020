(ns core
  (:require [clojure.string :as str]
            [clojure.set :refer :all]
            [clojure.spec.alpha :as s]))
(def operations #{"+"  "*"})
(def braces #{\( \)})

(defn eval! [expression]
  (loop [expression expression]
    (if (empty? expression) expression
                            (let [[a b c & rest] expression]
                              (cond
                                (or (nil? c) (contains? operations a)) expression
                                (and (nil? b) (nil? c)) a
                                :default (recur (cons (eval (read-string (str "(" b " " a " " c ")"))) rest)))))))

(defn evalB! [expression]
  (loop [expression expression p '()]
    (if (or (empty? expression) (= p expression)) expression
                            (let [[a b c & rest] expression]
                              (cond
                                (or (nil? c) (contains? operations a)) expression
                                (and (nil? b) (nil? c)) a
                                (and (= "*" b) (some? (first rest)) (nil? (second rest))) expression
                                (and (= "*" b) (>= (count rest) 2)) (recur (cons a (cons b (evalB! (cons c rest)))) expression)
                                :default (recur (cons (eval (read-string (str "(" b " " a " " c ")"))) rest) expression))))))


(defn evaluate [expression]
  (read-string (last (last (take-while (fn [[x y]]
                                         (not= x y)) (partition 2 1 (iterate (fn [sample]
                                                                             (-> ((comp (partial str/join " ") flatten) (map evalB! (map #(str/split (str/trim (str/join %)) #" ") (partition-by #(contains? braces %) sample))))
                                                                                 (str/replace #"\( (\w+) \)" "$1"))) expression)))))))

(defn begin [expressions]
  (let [calcs (->> expressions
             (map evaluate))]
    (println calcs)
    (println (reduce + calcs))))

(defn -main [& args]
  (println "hello" args)
  (time (begin (str/split (slurp (or (first args) "sample")) #"\n"))))
