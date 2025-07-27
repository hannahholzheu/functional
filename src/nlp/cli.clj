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
   ["-t" "--tokenize" "Tokenize the input text"]
   ["-p" "--preprocess" "Preprocess the input text"]
   ["-v" "--vectorize" "Vectorize the input text"]
   ["-d" "--distance" "Calculate distance"]
   ["-s" "--similarity" "Calculate similarity"]
   ["-cla" "--classify" "Classify text"]
   ["-clu" "--cluster" "Cluster texts"]
   ["-sp" "--spellcheck" "Spellcheck word"]
   ["-su" "--summarize" "Summarize text"]])

(defn print-error [msg]
  (binding [*out* *err*]
    (println "Error:" msg)))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)]
    (cond
      (:help options)
      (println summary)

      (:tokenize options)
      (if (empty? arguments)
        (print-error "Tokenize requires one text argument.")
        (println (tokenize/tokenize (first arguments))))

      (:preprocess options)
      (if (empty? arguments)
        (print-error "Preprocess requires one text argument.")
        (println (preprocess/preprocess (first arguments))))

      (:vectorize options)
      (if (< (count arguments) 2)
        (print-error "Vectorize requires a function name and a text.")
        (let [[func text] arguments]
          (case func
            "tf-idf" (println ((vectorize/tf-idf [(tokenize/tokenize text)]) (tokenize/tokenize text)))
            "one-hot" (println (vectorize/one-hot (tokenize/tokenize text)))
            "count-vector" (println (vectorize/count-vector (tokenize/tokenize text)))
            (print-error (str "Unknown vectorize function: " func)))))

      (:distance options)
      (if (< (count arguments) 3)
        (print-error "Distance requires a function name and two texts.")
        (let [[func text1 text2 & more] arguments]
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
            "minkowski" (if (empty? more)
                          (print-error "Minkowski distance requires an order parameter as well.")
                          (println (distance/minkowski-distance
                                    (first (vals ((vectorize/tf-idf [(tokenize/tokenize text1)]) (tokenize/tokenize text1))))
                                    (first (vals ((vectorize/tf-idf [(tokenize/tokenize text2)]) (tokenize/tokenize text2))))
                                    (read-string (first more)))))
            "canberra" (println (distance/canberra-distance
                                 (first (vals ((vectorize/tf-idf [(tokenize/tokenize text1)]) (tokenize/tokenize text1))))
                                 (first (vals ((vectorize/tf-idf [(tokenize/tokenize text2)]) (tokenize/tokenize text2))))))
            "normalized-levenshtein" (println (distance/normalized-levenshtein text1 text2))
            (print-error (str "Unknown distance function: " func)))))

      (:similarity options)
      (if (< (count arguments) 3)
        (print-error "Similarity requires a function name and two texts.")
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
                                         (vectorize/term-frequency (tokenize/tokenize text2))))
            (print-error (str "Unknown similarity function: " func)))))

      (:classify options)
      (if (< (count arguments) 2)
        (print-error "Classify requires a model and a text.")
        (let [[model-str text & more] arguments
              model (try
                      (read-string model-str)
                      (catch Exception _
                        (print-error "Invalid model format. Please provide a valid Clojure data structure.")
                        nil))]
          (when model
            (println (classification/classify model (tokenize/tokenize text))))))

      (:cluster options)
      (if (< (count arguments) 3)
        (print-error "Cluster requires k, iterations, and at least one text.")
        (let [[k-str iterations-str & texts] arguments
              k (try
                  (read-string k-str)
                  (catch Exception _
                    (print-error "Invalid k parameter. Should be a number.")
                    nil))
              iterations (try
                           (read-string iterations-str)
                           (catch Exception _
                             (print-error "Invalid iterations parameter. Should be a number.")
                             nil))]
          (when (and k iterations)
            (let [tokenized-texts (map tokenize/tokenize texts)
                  vectorizer (vectorize/tf-idf tokenized-texts)
                  vectors (map vectorizer tokenized-texts)]
              (println (clustering/k-means vectors k iterations))))))

      (:spellcheck options)
      (if (empty? arguments)
        (print-error "Spellcheck requires a word and optionally a dictionary.")
        (let [[word & dictionary] arguments]
          (println (spellcheck/correct-word word dictionary))))

      (:summarize options)
      (if (< (count arguments) 2)
        (print-error "Summarize requires a number of sentences and a text.")
        (let [[n-str & text-parts] arguments
              n (try
                  (read-string n-str)
                  (catch Exception _
                    (print-error "Invalid number for sentences.")
                    nil))
              text (str/join " " text-parts)]
          (when n
            (println (summarize/extractive-summary text n)))))

      (not (empty? errors))
      (do
        (doseq [e errors] (print-error e))
        (println summary))

      :else
      (print-error "Keine gültige Option angegeben. Benutze --help für Hilfe."))))
