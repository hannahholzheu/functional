(ns text-distance.features-test
  (:require [clojure.test :refer [deftest is]]
            [text-distance.features :refer [term-frequency inverse-document-frequency tf-idf generate-ngrams]]))

(deftest term-frequency-test
  (is (= {"a" 2, "b" 1, "c" 1} (term-frequency ["a" "b" "c" "a"])))
  (is (= {} (term-frequency []))))

(deftest inverse-document-frequency-test
  (let [docs [["a" "b"] ["a" "c"]]]
    (is (= {"a" (Math/log (/ 2.0 2.0))
            "b" (Math/log (/ 2.0 1.0))
            "c" (Math/log (/ 2.0 1.0))} (inverse-document-frequency docs)))))

(deftest tf-idf-test
  (let [docs [["a" "b"] ["a" "c"]]
        idf (inverse-document-frequency docs)
        doc ["a" "b" "a"]] 
    (is (= {"a" (* 2 (get idf "a"))
            "b" (* 1 (get idf "b"))} (tf-idf doc idf)))))

(deftest generate-ngrams-test
  (is (= [["a" "b"] ["b" "c"]] (generate-ngrams ["a" "b" "c"] 2)))
  (is (= [["a" "b" "c"]] (generate-ngrams ["a" "b" "c"] 3)))
  (is (= [] (generate-ngrams ["a" "b"] 3))))
