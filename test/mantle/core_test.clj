
(ns mantle.core-test
  (:require [clojure.test :refer :all]
            [mantle.core :refer :all]))

(deftest test:returning
  (testing "standalone form"
    (is (= 42 (returning [x 42]))))
  (testing "binding is available to body"
    (let [y (atom 0)]
      (is (= 42 (returning [x 42]
                  (reset! y (inc x)))))
      (is (= 43 @y))))
  (testing "returns the var, not the value"
    (let [y (atom nil)]
      (is (= (returning [x (Object.)]
               (reset! y x))
             @y)))))
