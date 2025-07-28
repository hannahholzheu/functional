(ns nlp.text.vectorize-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [nlp.text.vectorize :as vectorize]))

(deftest vectorize-test
  (testing "term frequency"
    (is (= {"hello" 2 "world" 1} (vectorize/term-frequency ["hello" "hello" "world"]))))
  (testing "document frequency"
    (is (= {"hello" 1 "world" 2} (vectorize/document-frequency [["hello" "world"] ["world"]]))))
 )
