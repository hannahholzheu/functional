(ns nlp.metrics.similarity
  (:require
   [clojure.set :as set]))


(defn dot-product
  "Calculate dot product between two vectors represented as maps."
  [v1 v2]
  (reduce + (for [[k val] v1 :when (contains? v2 k)]
              (* val (get v2 k)))))


(defn magnitude
  "Compute vector magnitude (L2 norm) for sparse vector map."
  [v]
  (Math/sqrt (reduce + (map (fn [[_ val]] (* val val)) v))))


(defn cosine-sim
  "Calculate cosine similarity between two vectors, 0 if one is zero vector."
  [v1 v2]
  (let [mag1 (magnitude v1)
        mag2 (magnitude v2)]
    (if (or (zero? mag1) (zero? mag2))
      0
      (/ (dot-product v1 v2) (* mag1 mag2)))))


(defn jaccard
  "Compute Jaccard similarity between two token collections."
  [tokens1 tokens2]
  (let [set1 (set tokens1)
        set2 (set tokens2)
        intersection (count (set/intersection set1 set2))
        union (count (set/union set1 set2))]
    (if (zero? union) 0 (/ intersection union))))


(defn sorensen-dice
  "Compute Sørensen-Dice similarity between two token collections."
  [tokens1 tokens2]
  (let [set1 (set tokens1)
        set2 (set tokens2)
        intersection (count (set/intersection set1 set2))
        total (+ (count set1) (count set2))]
    (if (zero? total) 0 (/ (* 2 intersection) total))))


(defn overlap-coefficient
  "Calculate overlap coefficient: intersection size divided by smaller set size."
  [tokens1 tokens2]
  (let [set1 (set tokens1)
        set2 (set tokens2)
        intersection (count (set/intersection set1 set2))
        smaller (min (count set1) (count set2))]
    (if (zero? smaller) 0 (/ intersection smaller))))


(defn dice-coefficient
  "Compute Dice coefficient for token frequency maps."
  [freq-map1 freq-map2]
  (let [terms (set/union (set (keys freq-map1)) (set (keys freq-map2)))
        intersect (reduce
                   (fn [acc term]
                     (+ acc (min (get freq-map1 term 0) (get freq-map2 term 0))))
                   0
                   terms)
        total (+ (reduce + (vals freq-map1)) (reduce + (vals freq-map2)))]
    (if (zero? total) 0 (/ (* 2 intersect) total))))
