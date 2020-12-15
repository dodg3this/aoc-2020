(ns core
  (:require [clojure.string :as str]
            [clojure.set :refer :all]))

(def address-length 36)

(defn apply-bit-mask [mask val]
  (loop [m mask i 0 v (Integer/parseInt val)]
    (if (empty? m) v
                   (recur (next m) (inc i) (case (first m)
                                             \0 (bit-clear v (- address-length 1 i))
                                             \1 (bit-set v (- address-length 1 i))
                                             v)))))

(defn begin [input]
  (let [program (map (fn [line]
                       (let [[_ instruction idx val] (first (re-seq #"(mem|mask)\[?(\d*)\]? = (\w*)" line))]
                         {:instruction instruction :idx idx :val val})) input)]

    (println "sum of all values in memory is" (apply + (vals (loop [prog program
                                                                    mask "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
                                                                    memory {}]
                                                               (if (empty? prog)
                                                                 memory
                                                                 (let [{:keys [instruction idx val]} (first prog)]
                                                                   (case instruction
                                                                     "mask" (recur (next prog) val memory)
                                                                     "mem" (recur (next prog) mask (assoc memory idx (apply-bit-mask mask val))))))))))))

(defn apply-bit-mask-b [mask val]
  (loop [m mask i 0 v (Integer/parseInt val)]
    (if (empty? m) v
                   (recur (next m) (inc i) (if (= \1 (first m)) (bit-set v (- address-length 1 i)) v)))))

(defn beginB [input]
  (let [program (map (fn [line]
                       (let [[_ instruction idx val] (first (re-seq #"(mem|mask)\[?(\d*)\]? = (\w*)" line))]
                         {:instruction instruction :idx idx :val val})) input)]
    (println "sum of all values in memory is" (apply + (vals (loop [prog program
                                                                    mask "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
                                                                    memory {}]
                                                               (if (empty? prog)
                                                                 memory
                                                                 (let [{:keys [instruction idx val]} (first prog)]
                                                                   (case instruction
                                                                     "mask" (recur (next prog) val memory)

                                                                     "mem" (let [masked-idx (apply-bit-mask-b mask idx)
                                                                                 new-val (Integer/parseInt val)
                                                                                 floaters (loop [m mask i 0 mems #{masked-idx}]
                                                                                            (if (empty? m) mems
                                                                                                           (if (= \X (first m))
                                                                                                             (recur (next m) (inc i) (union mems (map #(bit-flip % (- address-length 1 i)) mems)))
                                                                                                             (recur (next m) (inc i) mems))))]
                                                                             (recur (next prog) mask (reduce #(assoc %1 %2 new-val) memory floaters))))))))))))


(defn -main [& args]
  (println "hello" args)
  (time (beginB (str/split (slurp (or (first args) "sample")) #"\n"))))
