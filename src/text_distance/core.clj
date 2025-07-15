; core function: preprocessing -> vectorization -> distance
; :require makes it modular
(ns text-distance.core
  (:require [text-distance.preprocess :as prep]
            [text-distance.vectorize :as vec]
            [text-distance.distance :as dist]))

(defn compute-distance [text1 text2]
  (let [tokens1     (prep/preprocess text1)
        tokens2     (prep/preprocess text2)
        vectorizer  (vec/tfidf [tokens1 tokens2])
        vec1        (vectorizer tokens1)
        vec2        (vectorizer tokens2)]
    {:cosine  (dist/cosine-sim vec1 vec2)
     :jaccard (dist/jaccard tokens1 tokens2)}))

; CLI entry point
(defn -main [& args]
  (let [[t1 t2] args
        result (compute-distance t1 t2)]
    (println "Text Distance Results:")
    (doseq [[k v] result]
      (println (name k) ":" v))))
