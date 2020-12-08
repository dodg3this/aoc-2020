(ns core
  (:require [clojure.string :as str]))

(defn- break-rule [rule]
  (let [bag (str/split rule #"( bags contain | bags contain no other bags.)")
        bags (str/split (last bag) #", ")
        trimmed-bags (map #(str/split % #" bags{0,1}\D{0,1}") bags)]

    ;(println bag)
    ;(println bags)
    ;(println (first bags))
    ;(println trimmed-bags)
    {(first bag) (into {} (map #(let [parts (str/split (first %) #" " 2)]
                                  {(last parts) (first parts)}) trimmed-bags))}))

(defn- traverseA [m val]
  (let [bags []]
    (for [[k v] m :when (get v val)] (conj bags k (traverseA m k)))))

(defn sum [tree]
  ;(println tree)
  ;(println (type tree))
  ;(println (list? tree))
  (every? string? tree)
  (cond
    (not (coll? tree)) (Long/parseLong tree)
    (every? string? tree) (Long/parseLong (first tree))
    :default (let [s (reduce #(if (vector? tree) (* %1 (sum %2)) (+ %1 (sum %2))) (if (vector? tree) 1 0) tree)
                   ;_ (println tree)
                   p (if (vector? tree) (+ s (Long/parseLong (first tree))) s)]
               (println "^^  " s p tree)
               p)))

(defn- traverseB [m node]
  (let [children (get m node)]
    ;(println children)
    ;(println "##############")
    (if (get children "other") "1"
                               (for [[k v] children :when (not (= "other" k))] (conj [] v (traverseB m k))))
    ;(for [[k v] children :when (not (= "other" k))] (conj [] v (traverseB m k)))
    ))

(defn begin [input]
  (let [rules (into {} (map break-rule input))]
    ;(println rules)
    ;(println "********************")
    ;(println (traverseA rules "shiny gold"))

    (println (traverseA rules "shiny gold"))
    (println (count (into #{} (flatten (traverseA rules "shiny gold")))))
    (println (traverseB rules "shiny gold"))
    (println (sum (traverseB rules "shiny gold")))))

(defn -main [& args]
  (println "hello")
  (begin (str/split (slurp "input") #"\n")))
