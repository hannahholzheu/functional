(ns nlp.cli
  (:require
   [clojure.string :as str]
   [clojure.tools.cli :as cli]
   [nlp.metrics.distance :as distance]
   [nlp.metrics.similarity :as similarity]
   [nlp.models.classification :as classification]
   [nlp.models.clustering :as clustering]
   [nlp.models.spellcheck :as spellcheck]
   [nlp.models.summarize :as summarize]
   [nlp.text.preprocess :as preprocess]
   [nlp.text.tokenize :as tokenize]
   [nlp.text.vectorize :as vectorize]))

(def cli-options
  [["-h" "--help" "Show help"]
   ["-t" "--tokenize [text]" "Tokenize the input text"]
   ["-p" "--preprocess [text]" "Preprocess the input text"]
   ["-v" "--vectorize" "Vectorize the input text using the specified function (e.g., tf-idf, one-hot, count-vector)"]
   ["-d" "--distance [function] [text1] [text2]" "Calculate the distance between two texts using the specified function (e.g., levenshtein, manhattan, euclidean, hamming, chebyshev, minkowski, canberra, normalized-levenshtein)"]
   ["-s" "--similarity [function] [text1] [text2]" "Calculate the similarity between two texts using the specified function (e.g., cosine, jaccard, sorensen-dice, overlap-coefficient, dice-coefficient)"]
   ["-cla" "--classify [model] [text]" "Classify the input text using the specified model"]
   ["-clu" "--cluster [k] [iterations] [texts]" "Cluster the input texts using k-means"]
   ["-sp" "--spellcheck [word] [dictionary]" "Spellcheck the input word against a dictionary"]
   ["-su" "--summarize [n] [text]" "Summarize the input text into n sentences"]])

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)]
    (cond
      (:help options)
      (println summary)

      (:tokenize options)
      (println (tokenize/tokenize (:tokenize options)))

      (:preprocess options)
      (println (preprocess/preprocess (:preprocess options)))

      (:vectorize options)
      (let [[func text] arguments]
        (println "func:" func)
        (println "text:" text)
        (case func
          "tf-idf" (println ((vectorize/tf-idf [(tokenize/tokenize text)]) (tokenize/tokenize text)))
          "one-hot" (println (vectorize/one-hot (tokenize/tokenize text)))
          "count-vector" (println (vectorize/count-vector (tokenize/tokenize text)))))

      (:distance options)
      (let [[func text1 text2] arguments]
        (case func
          "levenshtein" (println (distance/levenshtein text1 text2))
          "manhattan" (println (distance/manhattan-distance
                                (first (vals ((vectorize/tf-idf [(tokenize/tokenize text1)]) (tokenize/tokenize text1))))
                                (first (vals ((vectorize/tf-idf [(tokenize/tokenize text2)]) (tokenize/tokenize text2))))))
          "euclidean" (println (distance/euclidean-distance
                                (first (vals ((vectorize/tf-idf [(tokenize/tokenize text1)]) (tokenize/tokenize text1))))
                                (first (vals ((vectorize/tf-idf [(tokenize/tokenize text2)]) (tokenize/tokenize text2))))))
          "hamming" (println (distance/hamming-distance text1 text2))
          "chebyshev" (println (distance/chebyshev-distance
                                (first (vals ((vectorize/tf-idf [(tokenize/tokenize text1)]) (tokenize/tokenize text1))))
                                (first (vals ((vectorize/tf-idf [(tokenize/tokenize text2)]) (tokenize/tokenize text2))))))
          "minkowski" (println (distance/minkowski-distance
                                (first (vals ((vectorize/tf-idf [(tokenize/tokenize text1)]) (tokenize/tokenize text1))))
                                (first (vals ((vectorize/tf-idf [(tokenize/tokenize text2)]) (tokenize/tokenize text2))))
                                (read-string (last arguments))))
          "canberra" (println (distance/canberra-distance
                               (first (vals ((vectorize/tf-idf [(tokenize/tokenize text1)]) (tokenize/tokenize text1))))
                               (first (vals ((vectorize/tf-idf [(tokenize/tokenize text2)]) (tokenize/tokenize text2))))))
          "normalized-levenshtein" (println (distance/normalized-levenshtein text1 text2))))

      (:similarity options)
      (let [[func text1 text2] arguments]
        (case func
          "cosine" (println (similarity/cosine-sim
                             (first (vals ((vectorize/tf-idf [(tokenize/tokenize text1)]) (tokenize/tokenize text1))))
                             (first (vals ((vectorize/tf-idf [(tokenize/tokenize text2)]) (tokenize/tokenize text2))))))
          "jaccard" (println (similarity/jaccard (tokenize/tokenize text1) (tokenize/tokenize text2)))
          "sorensen-dice" (println (similarity/sorensen-dice (tokenize/tokenize text1) (tokenize/tokenize text2)))
          "overlap-coefficient" (println (similarity/overlap-coefficient (tokenize/tokenize text1) (tokenize/tokenize text2)))
          "dice-coefficient" (println (similarity/dice-coefficient
                                       (vectorize/term-frequency (tokenize/tokenize text1))
                                       (vectorize/term-frequency (tokenize/tokenize text2))))))

      (:classify options)
      (let [[model-str text] arguments
            model (read-string model-str)]
        (println (classification/classify model (tokenize/tokenize text))))

      (:cluster options)
      (let [[k-str iterations-str & texts] arguments
            k (read-string k-str)
            iterations (read-string iterations-str)
            tokenized-texts (map tokenize/tokenize texts)
            vectorizer (vectorize/tf-idf tokenized-texts)
            vectors (map vectorizer tokenized-texts)]
        (println (clustering/k-means vectors k iterations)))

      (:spellcheck options)
      (let [[word & dictionary] arguments]
        (println (spellcheck/correct-word word dictionary)))

      (:summarize options)
      (let [[n-str & text-parts] arguments
            n (read-string n-str)
            text (str/join " " text-parts)]
        (println (summarize/extractive-summary text n))))))