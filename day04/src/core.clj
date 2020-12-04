(ns core
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s]))

(defn- get-passport [record]
  (let [temp (vec (map #(str/split % #":") record))]
    (into {} (for [[k v] temp]
               [(keyword k) v]))))

(defn- is-valid-length? [value]
  (let [unit (str/join (take-last 2 value))]
    (if (contains? #{"cm" "in"} unit)
      (cond
        (= "cm" unit) (<= 150 (Integer/parseInt (subs value 0 (- (count value) 2))) 193)
        (= "in" unit) (<= 59 (Integer/parseInt (subs value 0 (- (count value) 2))) 76)))))

(def hex-regex #"^#[0-9a-f]{6}$")
(def pid-regex #"^[0-9]{9}$")

(s/def ::byr (s/and string? #(<= 1920 (Integer/parseInt %) 2003)))
(s/def ::iyr (s/and string? #(<= 2010 (Integer/parseInt %) 2020)))
(s/def ::eyr (s/and string? #(<= 2020 (Integer/parseInt %) 2030)))
(s/def ::hgt (s/and string? is-valid-length?))
(s/def ::hcl (s/and string? #(re-matches hex-regex %)))
(s/def ::ecl (s/and string? #{"amb" "blu" "brn" "gry" "grn" "hzl" "oth"}))
(s/def ::pid (s/and string? #(re-matches pid-regex %)))

(s/def ::passport (s/keys :req-un [::byr ::iyr ::eyr ::hgt ::hcl ::ecl ::pid]
                          :opt-un [::cid]))

(defn begin [rows]
  (let [records (map #(str/split % #"(\n| )") rows)
        count (count (filter #(s/valid? ::passport %) (map get-passport records)))]
    (println "number of valid passwords: " count)))

(defn -main [& args]
  (println "hello")
  (begin (str/split (slurp "input") #"\n\n")))
