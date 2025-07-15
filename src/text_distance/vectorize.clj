; TF-IDF

(ns text-distance.vectorize)

(defn term-freq [tokens]
  (let [total (count tokens)]
    (frequencies tokens)))

(defn doc-freqs [docs]
  (reduce
   (fn [df doc]
     (reduce (fn [acc term]
               (update acc term (fnil inc 0)))
             df
             (set doc)))
   {}
   docs))

(defn idf [docs]
  (let [n (count docs)
        dfs (doc-freqs docs)]
    (into {}
          (for [[term df] dfs]
            [term (Math/log (/ n (+ 1 df)))])))) ; +1 for smoothing

(defn tf-idf [tokens idf-map]
  (let [tf (term-freq tokens)]
    (into {}
          (for [[term freq] tf]
            [term (* freq (get idf-map term 0))]))))

(defn tfidf [docs]
  (let [idf-map (idf docs)]
    (fn [tokens]
      (tf-idf tokens idf-map))))
