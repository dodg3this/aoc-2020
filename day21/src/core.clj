(ns core
  (:require [clojure.string :as str]
            [clojure.set :refer :all]
            [clojure.spec.alpha :as s]))

(defn parse [line]
  (let [[igs algns] (str/split line #" \(")
        ingredients (into #{} (str/split igs #" "))
        allergens (into #{} (str/split (subs algns 9 (- (.length algns) 1)) #", "))]
    [ingredients allergens]))

(defn trim [m v]
  (into {} (for [[allergen ingredients] m] [allergen
                                            (disj ingredients v)])))


(defn begin [input]
  (let [foods (map parse input)
        all-allergens (apply union (for [[_ allergens] foods] allergens))
        all-ingredients (apply union (for [[ingredients _] foods] ingredients))
        ingredients-allergens-map (loop [allergens all-allergens m {}]
                                    (if (empty? allergens) m
                                                           (let [allergen (first allergens)
                                                                 common-foods (map first (filter #(contains? (second %) allergen) foods))]
                                                             (recur (disj allergens allergen) (conj m [allergen (apply intersection common-foods)])))))
        mappings (loop [m ingredients-allergens-map unmatched {} r {}]
                   (if (= (count r) (count ingredients-allergens-map)) r
                                                                       (let [[allergen ingredients] (first m)]
                                                                         (if (= 1 (count ingredients)) (recur (trim (into (dissoc m allergen) unmatched) (first ingredients)) {} (conj r [allergen (first ingredients)]))
                                                                                                       (recur (dissoc m allergen) (conj unmatched [allergen ingredients]) r)))))
        ingredients-without-allergies (difference all-ingredients (into #{} (vals mappings)))]


    (println (reduce + (vals (filter #(contains? ingredients-without-allergies (first %)) (frequencies (flatten (for [[is as] foods] (into [] is))))))))
    (println (str/join "," (map second (sort mappings))))))

(defn -main [& args]
  (println "hello" args)
  (time (begin (str/split (slurp (or (first args) "sample")) #"\n"))))
