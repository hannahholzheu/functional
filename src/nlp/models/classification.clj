(ns nlp.models.classification)

(defn train-naive-bayes
  "Train Naive Bayes on labeled tokenized texts: {:label 'sports' :tokens [...]}"
  [docs]
  (let [classes (group-by :label docs)
        total (count docs)
        class-priors (into {} (map (fn [[label items]]
                                     [label (/ (count items) total)])
                                   classes))
        word-counts (into {}
                          (for [[label items] classes]
                            [label
                             (frequencies (mapcat :tokens items))]))
        class-totals (into {}
                           (map (fn [[label freqs]]
                                  [label (reduce + (vals freqs))])
                                word-counts))]
    {:priors class-priors
     :counts word-counts
     :totals class-totals}))

(defn classify
  "Classify token list using trained Naive Bayes model."
  [model tokens]
  (let [{:keys [priors counts totals]} model
        scores
        (for [[label prior] priors]
          (let [score
                (reduce
                 (fn [acc word]
                   (let [count (get-in counts [label word] 0)
                         total (get totals label)]
                     (+ acc (Math/log (/ (+ count 1) (+ total 1))))))
                 (Math/log prior)
                 tokens)]
            [label score]))]
    (->> scores (sort-by second >) ffirst)))
