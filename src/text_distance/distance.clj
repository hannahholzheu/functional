
(ns text-distance.distance
  (:require [clojure.set :as set]))

; Cosine Similarity for TF-IDF vectors (maps)
(defn dot-product [v1 v2]
  (reduce + (map (fn [[k v]] (* v (get v2 k 0))) v1)))

(defn magnitude [v]
  (Math/sqrt (reduce + (map #(* % %) (vals v)))))

(defn cosine-sim [v1 v2]
  (let [dot (dot-product v1 v2)
        mag (* (magnitude v1) (magnitude v2))]
    (if (zero? mag) 0 (/ dot mag))))

;; Jaccard Similarity for sets (tokens)
(defn jaccard [tokens1 tokens2]
  (let [s1 (set tokens1)
        s2 (set tokens2)
        inter (count (set/intersection s1 s2))
        union (count (set/union s1 s2))]
    (if (zero? union) 0 (/ inter union))))

;; Sørensen-Dice Coefficient
(defn sorensen-dice [tokens1 tokens2]
  (let [s1 (set tokens1)
        s2 (set tokens2)
        intersection-count (count (set/intersection s1 s2))]
    (/ (* 2.0 intersection-count)
       (+ (count s1) (count s2)))))

;; Levenshtein Distance
(defn levenshtein [s1 s2]
  (let [len1 (count s1)
        len2 (count s2)]
    (cond
      (zero? len1) len2
      (zero? len2) len1
      :else
      (let [v0 (vec (range (inc len2)))]
        (last (reduce
         (fn [v0 i]
           (let [c1 (nth s1 (dec i))]
             (reduce
              (fn [v1 j]
                (let [c2 (nth s2 (dec j))
                      cost (if (= c1 c2) 0 1)]
                  (assoc v1 j (min (inc (v1 (dec j)))
                                   (inc (v0 j))
                                   (+ cost (v0 (dec j)))))))
              (assoc v0 0 i)
              (range 1 (inc len2)))))
         v0
         (range 1 (inc len1))))))))


