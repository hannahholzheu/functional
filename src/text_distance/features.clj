(ns text-distance.features)

(defn term-frequency
  "Calculates TF for a single document.
   Returns a map of {term -> frequency}."
  [tokens]
  (frequencies tokens))

(defn inverse-document-frequency
  "Calculates IDF for a set of documents.
   Returns a map of {term -> idf-score}."
  [documents]
  (let [doc-count (count documents)
        doc-freqs (reduce (fn [counts terms]
                            (reduce (fn [m term] (update m term (fnil inc 0)))
                                    counts
                                    (set terms)))
                          {} documents)]
    (into {} (for [[term freq] doc-freqs]
               [term (Math/log (/ (double doc-count) (double freq)))]))))

(defn tf-idf
  "Calculates TF-IDF for a single document against a corpus."
  [document corpus-idf]
  (let [tf (term-frequency document)]
    (into {} (for [[term freq] tf]
               [term (* freq (get corpus-idf term 0))]))))

(defn generate-ngrams
  "Creates n-grams from a collection of tokens."
  [tokens n]
  (partition n 1 tokens))
