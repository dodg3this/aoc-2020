(ns core
  (:require [clojure.string :as str]
            [clojure.set :refer :all]
            [clojure.spec.alpha :as s])) 5

(def sample-door-public-key "17807724")
(def sample-card-public-key "5764801")

(def sample-door-public-key "7734663")
(def sample-card-public-key "1614360")

(defn sieve [s]
  (cons (first s)
        (lazy-seq (sieve (filter #(not= 0 (mod % (first s)))
                                 (rest s))))))
(def primes (memoize (fn [n]
                       (nth (sieve (iterate inc 2)) n))))

(loop [subject 2 public-key 1614360 transformation 1 loop 0 n 1]
  (if (= transformation public-key) [subject loop transformation]
                                    (if (> loop 20201227)
                                      (recur (primes n) public-key 1 0 (inc n))
                                      (recur subject public-key (rem (* transformation subject) 20201227) (inc loop) n))))

(loop [subject 7734663 loop 1182212 encryption-key 1]
  (if (= loop 0) encryption-key
                 (recur subject (dec loop) (rem (* encryption-key subject) 20201227))))

(defn -main [& args]
  (println "hello" args))
