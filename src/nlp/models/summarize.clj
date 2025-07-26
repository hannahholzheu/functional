(ns nlp.models.summarize
  (:require
   [clojure.string :as str]
   [nlp.metrics.similarity :refer [cosine-sim]]
   [nlp.text.vectorize :as vec]))

(defn extractive-summary
  "Return top-N sentences most similar to document vector (centroid)."
  [text n]
  (let [sentences (str/split text #"[.!?]")
        vectors (map vec/tf-idf sentences)
        doc-vec (reduce (fn [a b] (merge-with + a b)) vectors)
        scored (map (fn [[s v]] [s (cosine-sim v doc-vec)])
                    (map vector sentences vectors))]
    (->> scored (sort-by second >) (take n) (map first) (str/join ". "))))
