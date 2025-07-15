
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
