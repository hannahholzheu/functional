(ns nlp.models.spellcheck-test
   (:require
    [clojure.test :refer [deftest is testing]]
    [nlp.models.spellcheck :as spellcheck]))

((deftest spellcheck-test
      (testing "Spellcheck should return the closest word"
        (is (= "train" (spellcheck/correct-word "Traon" ["Dog" "Food" "Lunch" "trainer" "train"]))))) )
