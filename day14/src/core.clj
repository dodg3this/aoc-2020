(ns core
  (:require [clojure.string :as str]
            [clojure.set :refer :all]))

(defn begin [input]
  (let [program (map (fn [line]
                       (let [[_ instruction idx val] (first (re-seq #"(mem|mask)\[?(\d*)\]? = (\w*)" line))]
                         {:instruction instruction :idx idx :val val})) input)]
    (println "*****" (apply + (vals (loop [prog program
                                           mask "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
                                           memory {}]
                                      (if (empty? prog)
                                        memory
                                        (let [{:keys [instruction idx val]} (first prog)]
                                          (case instruction
                                            "mask" (recur (next prog) val memory)
                                            "mem" (let [idx (Integer/parseInt idx)
                                                        new-val (atom (Integer/parseInt val))
                                                        masks (for [i (range (.length mask))] (case (nth mask i)
                                                                                                \0 (swap! new-val bit-clear (- 35 i))
                                                                                                \1 (swap! new-val bit-set (- 35 i))
                                                                                                @new-val))]
                                                    (recur (next prog) mask (assoc memory idx (last masks)))))))))))))

(defn beginB [input]
  (let [program (map (fn [line]
                       (let [[_ instruction idx val] (first (re-seq #"(mem|mask)\[?(\d*)\]? = (\w*)" line))]
                         {:instruction instruction :idx idx :val val})) input)]
    (println "*****" (apply + (vals (loop [prog program
                                           mask "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
                                           memory {}]
                                      (if (empty? prog)
                                        memory
                                        (let [{:keys [instruction idx val]} (first prog)]
                                          (case instruction
                                            "mask" (recur (next prog) val memory)
                                            "mem" (let [idx (atom (Integer/parseInt idx))
                                                        new-val (Integer/parseInt val)
                                                        masked (last (for [i (range (.length mask)) :let [msk (nth mask i)] :when (not= \X msk)] (if (= \1 msk) (swap! idx bit-set (- 35 i)) @idx)))
                                                        mems (atom #{masked})
                                                        floatings (for [i (range (.length mask)) :let [msk (nth mask i)] :when (= \X msk)] (swap! mems union (apply union (for [m @mems] (conj #{} (bit-set m (- 35 i))
                                                                                                                                                                                               (bit-clear m (- 35 i)))))))]
                                                    (recur (next prog) mask (reduce #(assoc %1 %2 new-val) memory (last floatings)))))))))))))


(defn -main [& args]
  (println "hello" args)
  (beginB (str/split (slurp (or (first args) "sample")) #"\n")))
