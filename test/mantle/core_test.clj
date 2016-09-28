
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

(deftest test:select-values
  (let [m {:c 3 :a 1 :b 2}]
    (is (= [3] (select-values m [:c])))
    (is (= [1 2] (select-values m [:a :b])))
    (is (= [2 1] (select-values m [:b :a])))
    (is (= [3 nil] (select-values m [:c :d])))
    (is (= [nil 3] (select-values m [:d :c])))))

(deftest test:single
  (letfn [(test-single-modes [<1 =1 >1]
            (is (thrown-with-msg? AssertionError #"found an empty collection"
                  (single <1)))
            (is (= (first =1) (single =1)))
            (is (thrown-with-msg? AssertionError #"found a collection with multiple elements"
                  (single >1))))]
    (testing "simple collections"
      (test-single-modes [] [1] [1 2 3]))
    (testing "compound collections"
      (test-single-modes {} {:a 1} {:a 1 :b 2}))
    (testing "lazy collections"
      (letfn [(gen-seq [n] (take n (repeatedly rand)))]
        (test-single-modes (gen-seq 0) (gen-seq 1) (gen-seq 3))))))
