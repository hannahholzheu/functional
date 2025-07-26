(ns nlp.text.vectorize)

(defn term-frequency
  "Returns a map of token frequencies in a single document."
  [tokens]
  (frequencies tokens))

(defn document-frequency
  "Counts how many documents each token appears in."
  [docs]
  (reduce
   (fn [df doc]
     (reduce (fn [acc term] (update acc term (fnil inc 0)))
             df
             (set doc)))
   {}
   docs))

(defn inverse-document-frequency
  "Calculates IDF with smoothing as log(total-docs / (doc-frequency + 1))."
  [docs]
  (let [N (count docs)
        dfs (document-frequency docs)]
    (into {}
          (for [[term df] dfs]
            [term (Math/log (/ N (+ 1 df)))]))))

(defn tf-idf
  "Returns a function that computes TF-IDF vectors for given tokens."
  [docs]
  (let [idf-map (inverse-document-frequency docs)]
    (fn [tokens]
      (let [tf (term-frequency tokens)]
        (into {}
              (for [[term freq] tf]
                [term (* freq (get idf-map term 0))]))))))

(defn one-hot
  "Creates a presence vector mapping tokens to 1."
  [tokens]
  (into {}
        (for [t tokens]
          [t 1])))

(defn count-vector
  "Returns token counts, identical to term-frequency."
  [tokens]
  (term-frequency tokens))
