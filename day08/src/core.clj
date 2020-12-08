(ns core
  (:require [clojure.string :as str]))

(def idx (atom 0))
(def visited (atom {}))
(def acc (atom 0))
(def swapped-instruction (atom {}))
(def swapped (atom false))
(def counter (atom 0))

(def replace-instruction-map {"nop" "jmp", "jmp" "nop"})

(defn beginB [input]
  (let [last-idx (count input)]
    (while (not (>= @idx last-idx))
      (reset! idx 0)
      (reset! visited {})
      (reset! acc 0)
      (reset! swapped false)
      (while (and (not (get @visited @idx)) (not (>= @idx last-idx)))
        (let [instruction (nth input @idx)
              [op val] (str/split instruction #" ")
              op-mod (if (and (not @swapped) (not (get @swapped-instruction @idx))) (replace-instruction-map op op) op)]
          (if (not (= op op-mod)) (do (swap! swapped complement)
                                      (swap! swapped-instruction assoc @idx true)))
          (swap! visited assoc @idx true)
          (cond
            (= op-mod "nop") (swap! idx inc)
            (= op-mod "acc") (do (swap! idx inc)
                                 (swap! acc + (Integer/parseInt val)))
            (= op-mod "jmp") (do (swap! idx + (Integer/parseInt val)))))))))

(defn beginA [input]
  (while (not (get @visited @idx))
    (let [instruction (nth input @idx)
          [op val] (str/split instruction #" ")]
      (swap! visited assoc @idx true)
      (cond
        (= op "nop") (swap! idx inc)
        (= op "acc") (do (swap! idx inc)
                         (swap! acc + (Integer/parseInt val)))
        (= op "jmp") (do (swap! idx + (Integer/parseInt val)))))))

(defn -main [& args]
  (println "hello")
  (beginA (str/split (slurp "input") #"\n"))
  (println @idx)
  (println @visited)
  (println @acc))
