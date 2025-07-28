(ns nlp.models.spellcheck
  (:require [nlp.metrics.distance :refer [levenshtein]]))

(defn correct-word
  "Suggest the closest word from dictionary using Levenshtein distance."
  [word dictionary]
  (first (sort-by #(levenshtein word %) dictionary)))
