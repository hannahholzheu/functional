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
   [nlp.text.vectorize :as vectorize]
   [nlp.utils.files :as files]))

(def cli-options
  [["-h" "--help" "Show help"]
   ["-f" "--file-input" "Read texts from filepaths"]
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
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)
        file-input? (:file-input options) ; check if file input flag is present 
        get-text (fn [arg] (if file-input? (files/read-file-content arg)
                               arg))]
    (cond
      (:help options)
      (println summary)

      (:tokenize options)
      (if (empty? arguments)
        (print-error "Tokenize requires one text argument.")
        (println (tokenize/tokenize (get-text (first arguments)))))

      (:preprocess options)
      (if (empty? arguments)
        (print-error "Preprocess requires one text argument.")
        (println (preprocess/preprocess (get-text (first arguments)))))

      (:vectorize options)
      (if (< (count arguments) 2)
        (print-error "Vectorize requires a function name and a text. (Functions: 'tf-idf', 'one-hot', 'count-vector')")
        (let [[func text] arguments]
          (let [text (get-text text)]
            (case func
              "tf-idf" (println ((vectorize/tf-idf [(tokenize/tokenize text)]) (tokenize/tokenize text)))
              "one-hot" (println (vectorize/one-hot (tokenize/tokenize text)))
              "count-vector" (println (vectorize/count-vector (tokenize/tokenize text)))
              (print-error (str "Unknown vectorize function: " func))))))

      (:distance options)
      (if (< (count arguments) 3)
        (print-error "Distance requires a function name and two texts. (Functions: 'levenshtein', 'hamming', 'normalized-levenshtein', 'manhattan', 'euclidean', 'chebyshev', 'canberra', 'minkowski')")
        (let [[func text1 text2 & more] arguments]
          (let [text1 (get-text text1) text2 (get-text text2)]
            (case func
              ;; String-based distances - no preprocessing needed
              "levenshtein" (println (distance/levenshtein text1 text2))
              "hamming" (println (distance/hamming-distance text1 text2))
              "normalized-levenshtein" (println (distance/normalized-levenshtein text1 text2))

              ;; Vector-based distances - need preprocessing and vectorization
              ("manhattan" "euclidean" "chebyshev" "canberra" "minkowski")
              (let [;; Preprocess both texts first
                    preprocessed1 (preprocess/preprocess text1)
                    preprocessed2 (preprocess/preprocess text2)
                    ;; Create a corpus with BOTH preprocessed documents
                    corpus [preprocessed1 preprocessed2]
                    ;; Create TF-IDF vectorizer from the shared corpus
                    tf-idf-fn (vectorize/tf-idf corpus)
                    ;; Create vectors for each document using the SAME vectorizer
                    vec1 (tf-idf-fn preprocessed1)
                    vec2 (tf-idf-fn preprocessed2)]

                (case func
                  "manhattan" (println (distance/manhattan-distance vec1 vec2))
                  "euclidean" (println (distance/euclidean-distance vec1 vec2))
                  "chebyshev" (println (distance/chebyshev-distance vec1 vec2))
                  "canberra" (println (distance/canberra-distance vec1 vec2))
                  "minkowski" (if (empty? more)
                                (print-error "Minkowski distance requires an order parameter.")
                                (let [p (try
                                          (Double/parseDouble (first more))
                                          (catch NumberFormatException _
                                            (print-error "Minkowski order parameter must be a number.")
                                            nil))]
                                  (when p
                                    (println (distance/minkowski-distance vec1 vec2 p)))))))

              ;; Unknown function
              (print-error (str "Unknown distance function: " func))))))

      (:similarity options)
      (if (< (count arguments) 3)
        (print-error "Similarity requires a function name and two texts.")
        (let [[func text1 text2] arguments
              text1 (get-text text1) text2 (get-text text2)]
          (case func
            ("cosine-sim" "jaccard" "sorensen-dice" "overlap-coefficient" "dice-coefficient")
            (let [;; Preprocess both texts first
                  preprocessed1 (preprocess/preprocess text1)
                  preprocessed2 (preprocess/preprocess text2)]

              (case func
                "cosine-sim" (let [;; Preprocess both texts first
                                   preprocessed1 (preprocess/preprocess text1)
                                   preprocessed2 (preprocess/preprocess text2)
                                   ;; vectorize 
                                   vectorizer (vectorize/tf-idf [preprocessed1 preprocessed2])
                                   vec1 (vectorizer preprocessed1)
                                   vec2 (vectorizer preprocessed2)]
                               (println (similarity/cosine-sim vec1 vec2)))
                "jaccard" (println (similarity/jaccard preprocessed1 preprocessed2))
                "sorensen-dice" (println (similarity/sorensen-dice preprocessed1 preprocessed2))
                "overlap-coefficient" (println (similarity/overlap-coefficient preprocessed1 preprocessed2))
                "dice-coefficient" (let [;; Preprocess both texts first
                                         preprocessed1 (preprocess/preprocess text1)
                                         preprocessed2 (preprocess/preprocess text2)
                                         ;; vectorize 
                                         vectorizer (vectorize/tf-idf [preprocessed1 preprocessed2])
                                         vec1 (vectorizer preprocessed1)
                                         vec2 (vectorizer preprocessed2)]
                                     (println (similarity/dice-coefficient vec1 vec2)))))
            ;; Error: Unknown function
            (print-error (str "Unknown distance function: " func)))))


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
            (let [texts (map preprocess/preprocess (map get-text texts))
                  vectorizer (vectorize/tf-idf texts)
                  vectors (map vectorizer texts)]
              (println (clustering/k-means vectors k iterations))))))

      (:spellcheck options)
      (if (empty? arguments)
        (print-error "Spellcheck requires a word and optionally a dictionary.")
        (let [[word & dictionary] arguments]
          (println (spellcheck/correct-word word dictionary))))

      (:summarize options)
      ;; does not support file inputs 
      (if (< (count arguments) 2)
        (print-error "Summarize requires a number of sentences and a text.")
        (let [[n-str & text-parts] arguments
              n (try
                  (read-string n-str)
                  (catch Exception _
                    (print-error "Invalid number for sentences.")
                    nil))
              text (str/join " " (map get-text text-parts))]
          (when n
            (println (summarize/extractive-summary text n)))))

      (not (empty? errors))
      (do
        (doseq [e errors] (print-error e))
        (println summary))

      :else
      (print-error "No valid option provided. Use --help for help."))))