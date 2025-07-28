(ns nlp.text.preprocess-test
  (:require
    [clojure.test :refer [deftest is testing]]
    [nlp.text.tokenize :as tokenize])) 

(deftest tokenize-test
  (testing "Tokenizes a simple sentence correctly"
    (is (= ["hello" "world"] (tokenize/tokenize "Hello world!"))))

  (testing "Handles punctuation removal and lowercasing"
    (is (= ["this" "is" "a" "test"] (tokenize/tokenize "This, is a test."))))

  (testing "Returns an empty list for empty input"
    (is (= [] (tokenize/tokenize ""))))

  (testing "Handles multiple spaces and newlines"
    (is (= ["one" "two" "three"] (tokenize/tokenize "One   two\nthree"))))

  (testing "Handles numbers as tokens"
    (is (= ["item" "123" "is" "here"] (tokenize/tokenize "Item 123 is here."))))
)
