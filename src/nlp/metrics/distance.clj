
(ns nlp.metrics.distance 
  (:require
   [clojure.set :as set]))

;; Levenshtein edit distance between strings
(defn levenshtein 
  "Calculate Levenshtein edit distance between two strings."
  [s1 s2]
  (let [len1 (count s1)
        len2 (count s2)]
    (cond
      (zero? len1) len2
      (zero? len2) len1
      :else
      (let [v0 (vec (range (inc len2)))]
        (last (reduce
         (fn [v0 i]
           (let [c1 (nth s1 (dec i))]
             (reduce
              (fn [v1 j]
                (let [c2 (nth s2 (dec j))
                      cost (if (= c1 c2) 0 1)]
                  (assoc v1 j (min (inc (v1 (dec j)))
                                   (inc (v0 j))
                                   (+ cost (v0 (dec j)))))))
              (assoc v0 0 i)
              (range 1 (inc len2)))))
         v0
         (range 1 (inc len1))))))))


;; Manhattan (L1) distance for sparse vector maps
(defn manhattan-distance
  "Calculate Manhattan (L1) distance between two sparse vectors."
  [v1 v2]
  (reduce
   (fn [acc k]
     (+ acc (Math/abs (- (get v1 k 0) (get v2 k 0))))) ; reducer function: accumulates distance between the values in the vectors 
   0 ; initial value 
   (clojure.set/union (set (keys v1)) (set (keys v2))))) ; reduce on the union of the keys of both vectors 


;; Euclidean (L2) distance for sparse vector maps
(defn euclidean-distance
  "Calculate Euclidean (L2) distance between two sparse vectors."
  [v1 v2]
  (Math/sqrt
   (reduce
    (fn [acc k]
      (let [diff (- (get v1 k 0) (get v2 k 0))]
        (+ acc (* diff diff)))) ; squared distance 
    0 ; initial value 
    (clojure.set/union (set (keys v1)) (set (keys v2))))) ; reduce on the union of the keys of both vectors 
  )


;; Hamming distance for equal-length strings
(defn hamming-distance
  "Calculate Hamming distance between two strings of equal length. Hamming distance is a measure that returns the amount of indexes at which the characters differ."
  [s1 s2]
  (if (not= (count s1) (count s2))
    (throw (ex-info "Strings must have equal length" {}))
    (count (filter false? (map = s1 s2)))))


(defn chebyshev-distance
  "Calculate Chebyshev (L∞) distance between two sparse vectors. The biggest difference between the numbers in the same spots of two vectors"
  [v1 v2]
  (reduce
   (fn [max-diff k] ; max-diff = acc
     (max max-diff (Math/abs (- (get v1 k 0) (get v2 k 0))))) ; keep track of largest distance between tokens 
   0
   (clojure.set/union (set (keys v1)) (set (keys v2))))) ; reduce on the union of the keys of both vectors 


(defn minkowski-distance
  "Calculate Minkowski distance of order p between two sparse vectors. Larger p = more influence of differences in a spot. Smaller p = care evenly about all differences in the vectors."
  [v1 v2 p]
  (Math/pow
   (reduce
    (fn [acc k]
      (+ acc (Math/pow (Math/abs (- (get v1 k 0) (get v2 k 0))) p)))
    0
    (clojure.set/union (set (keys v1)) (set (keys v2))))
   (/ 1 p)))


(defn canberra-distance
  "Calculate Canberra distance between two sparse vectors. Canberra distance is a weighted version of the Manhattan distance"
  [v1 v2]
  (reduce
   (fn [acc k]
     (let [x (get v1 k 0)
           y (get v2 k 0)
           denom (+ (Math/abs x) (Math/abs y))] ; denominator = |x| + |y|
       (if (zero? denom)
         acc
         (+ acc (/ (Math/abs (- x y)) denom))))) ; acc + (|x -y|) : |x| + |y|
   0
   (clojure.set/union (set (keys v1)) (set (keys v2))))) ; reduce on the union of the keys of both vectors 


(defn normalized-levenshtein
  "Calculate normalized Levenshtein distance between two strings."
  [s1 s2]
  (let [dist (levenshtein s1 s2)
        max-len (max (count s1) (count s2))]
    (if (zero? max-len)
      0
      (/ dist max-len))))
