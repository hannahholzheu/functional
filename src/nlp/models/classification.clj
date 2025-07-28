(ns nlp.models.classification)

(defn train-naive-bayes
  "Train Naive Bayes on labeled tokenized texts: {:label 'sports' :tokens [...]}"
  [docs]
  (let [; Group documents by their label (e.g., 'sports', 'tech').
        classes (group-by :label docs)
        ; Get the total number of documents.
        total (count docs)
        ; Calculate the prior probability for each class.
        ; P(class) = (number of documents in class) / (total number of documents)
        class-priors (into {} (map (fn [[label items]]
                                     [label (/ (count items) total)])
                                   classes))
        ; Count the frequency of each word within each class.
        word-counts (into {}
                          (for [[label items] classes]
                            [label
                             (frequencies (mapcat :tokens items))]))
        ; Calculate the total number of words for each class.
        class-totals (into {}
                           (map (fn [[label freqs]]
                                  [label (reduce + (vals freqs))])
                                word-counts))]
    ; Return the trained model as a map.
    {:priors class-priors
     :counts word-counts
     :totals class-totals}))

(defn classify
  "Classify token list using trained Naive Bayes model."
  [model tokens]
  (let [{:keys [priors counts totals]} model
        ; Calculate the score for each class based on the input tokens.
        scores
        (for [[label prior] priors]
          (let [; The score is the sum of log probabilities.
                score
                (reduce
                 (fn [acc word]
                   (let [; Get the frequency of the word in the current class.
                         count (get-in counts [label word] 0)
                         ; Get the total number of words in the current class.
                         total (get totals label)]
                     ; Add the log probability of the word given the class.
                     ; Uses Laplace smoothing (+1) to avoid zero probabilities.
                     (+ acc (Math/log (/ (+ count 1) (+ total 1))))))
                 ; Start with the log of the class's prior probability.
                 (Math/log prior)
                 tokens)]
            [label score]))]
    ; Return the label of the class with the highest score.
    (->> scores
         (sort-by second >)
         ffirst)))
