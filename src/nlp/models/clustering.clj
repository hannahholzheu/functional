(ns nlp.models.clustering
  (:require [nlp.metrics.similarity :refer [cosine-sim]]))

(defn closest-centroid [vector centroids]
  (first (apply max-key second
                (map (fn [c] [c (cosine-sim c vector)]) centroids))))

(defn average-centroid [vectors]
  (let [keys (set (mapcat keys vectors))]
    (into {} (for [k keys]
               [k (/ (reduce + (map #(get % k 0) vectors))
                     (count vectors))]))))

(defn k-means
  "Basic k-means clustering on sparse vectors (TF-IDF-style)."
  [vectors k iterations]
  (loop [centroids (take k vectors)
         i iterations]
    (if (zero? i)
      centroids
      (let [groups (group-by #(closest-centroid % centroids) vectors)
            new-centroids (mapv average-centroid (vals groups))]
        (recur new-centroids (dec i))))))
