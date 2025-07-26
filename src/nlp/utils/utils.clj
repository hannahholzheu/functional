(ns nlp.utils.utils
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]))

; loads stopwords from file (optional)
(defn load-stopwords [filepath]
  (->> (slurp (io/file filepath))
       str/split-lines
       (remove str/blank?)
       set))

; normalizes map-vectors (optional for cosine)
(defn normalize-vector [vec]
  (let [mag (Math/sqrt (reduce + (map #(* % %) (vals vec))))]
    (if (zero? mag)
      vec
      (into {} (map (fn [[k v]] [k (/ v mag)]) vec)))))

; map to a complete vector over a given vocabulary (e.g. for Euclidian)
(defn fill-missing [vec vocab]
  (into {} (for [term vocab]
             [term (get vec term 0)])))
