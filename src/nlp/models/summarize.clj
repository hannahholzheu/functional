(ns nlp.models.summarize
  (:require
   [clojure.string :as str]
   [nlp.metrics.similarity :refer [cosine-sim]]
   [nlp.text.vectorize :as vec]
   [nlp.text.tokenize :as tokenize]
   [nlp.text.preprocess :as preprocess]))

(defn extractive-summary
  "Return top-N sentences most similar to document vector (centroid)."
  [text n]
  (let [sentences (tokenize/tokenize-sentences text)
        preprocessed_sentences (map preprocess/preprocess sentences)
        vectorizer (vec/tf-idf preprocessed_sentences) ; function that creates TF-IDF vectors based on the entire document 
        vectors (map vectorizer preprocessed_sentences) ; create TF-IDF vector from vectorizer 
        doc-vec (reduce (fn [a b] (merge-with + a b)) vectors) ; create document vector (merge all TF-IDF vectors in the document)
        scored (map (fn [[s v]] [s (cosine-sim v doc-vec)])
                    (map vector sentences vectors))]
    (->> scored 
         (sort-by second >) 
         (take n) 
         (map first) 
         (str/join ". "))))
