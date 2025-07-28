(ns nlp.models.clustering
  (:require [nlp.metrics.similarity :refer [cosine-sim]]))

(defn closest-centroid
  "Finds the closest centroid for a given vector.
  It calculates cosine similarity between the vector and each centroid,
  then returns the centroid with the highest similarity.
  `max-key second` finds the [centroid, score] pair with the highest score."
  [vector centroids]
  (first (apply max-key second
                (map (fn [c] [c (cosine-sim c vector)]) centroids))))

(defn average-centroid
  "Calculates the average of a list of vectors to find the new centroid.
  Since vectors are sparse maps, it first gathers all unique keys
  across all vectors in the cluster before averaging each dimension."
  [vectors]
  (let [keys (set (mapcat keys vectors))]
    (into {} (for [k keys]
               [k (/ (reduce + (map #(get % k 0) vectors))
                     (count vectors))]))))

(defn k-means
  "Performs k-means clustering on a collection of vectors for a fixed number of iterations.
  The algorithm works as follows:
  1. Initialize `k` centroids (here, by taking the first k vectors from the dataset).
  2. Assign each vector to its closest centroid, which forms `k` clusters.
  3. Recalculate each centroid as the average (mean) of all vectors in its cluster.
  4. Repeat steps 2 and 3 for the specified number of iterations."
  [vectors k iterations]
  (loop [centroids (take k vectors) ; Step 1: Initialize centroids
         i iterations]
    (if (zero? i)
      centroids ; Return final centroids after all iterations are done.
      (let [;; Step 2: Assign vectors to the closest centroid. `group-by` creates the clusters.
            groups (group-by #(closest-centroid % centroids) vectors)
            ;; Step 3: Recalculate centroids from the new groups.
            ;; `vals` gets the vector lists from the grouped map.
            new-centroids (mapv average-centroid (vals groups))]
        ;; Step 4: Repeat the process with the new centroids.
        (recur new-centroids (dec i))))))
