(ns nlp.text.preprocess-test
  (:require
    [clojure.test :refer [deftest is testing]]
    [nlp.text.preprocess :as preprocess])) 

(deftest preprocess-test
  (testing "Normalizes a normal sentence"
    (is (= ["hello" "world"] (preprocess/preprocess "Hello world"))))

  (testing "Handles punctuation removal and lowercasing. Removes stopword (a)"
    (is (= ["this" "is" "test"] (preprocess/preprocess "This, is a test."))))

  (testing "Returns an empty list for empty input"
    (is (= [] (preprocess/preprocess ""))))

  (testing "Handles multiple spaces and newlines"
    (is (= ["one" "two" "three"] (preprocess/preprocess "One   two\nthree"))))

  (testing "Removes numbers"
    (is (= ["item" "is" "here"] (preprocess/preprocess "Item 123 is here."))))
)
