
(ns mantle.test-test
  (:require [clojure.test :refer :all]
            [mantle.test :refer :all]))

(def ^{:dynamic :true} *test-var-0* (atom nil))
(def ^{:dynamic :true} *test-var-1* (atom nil))
(def ^{:dynamic :true} *test-var-2* (atom nil))

(defixture null-fixture
  {})

(deftest test-fixture:null
  (testing "single value as test body"
    (is (not (nil? (with-fixture null-fixture true)))))
  (testing "expression as test body"
    (is (with-fixture null-fixture (= true true))))
  (testing "multiple forms in test body"
    (binding [*test-var-0* (atom 0)]
      (is (with-fixture null-fixture
            (swap! *test-var-0* (fn [_] 42))
            (= true true)))
      (is (= 42 @*test-var-0*))))
  (is (nil? @*test-var-0*)))

(defixture setup-and-teardown-fixture
  {:setup (swap! *test-var-0* (fn [_] 42))
   :teardown (swap! *test-var-0* (fn [_] nil))})

(deftest test-with-fixture:setup-and-teardown
  (is (nil? @*test-var-0*))
  (with-fixture setup-and-teardown-fixture
    (is (= 42 @*test-var-0*)))
  (is (nil? @*test-var-0*)))

(deftest test-exception-in-fixture
  (is (nil? @*test-var-0*))
  (is (thrown? RuntimeException
               (with-fixture setup-and-teardown-fixture
                 (throw (RuntimeException. "boom")))))
  (is (nil? @*test-var-0*)))

(defixture binding-fixture:single-var
  {:binding [[*test-var-0* 42]]})

(defixture binding-fixture:single-var-to-expression
  {:binding [[*test-var-0* (+ 40 2)]]})

(defixture binding-fixture:multiple-vars
  {:binding [[*test-var-0* 42 *test-var-1* 24]]})

(defixture binding-fixture:nested-vars
  {:binding [[*test-var-0* 42]
             [*test-var-1* (inc *test-var-0*)]]})

(deftest test-with-fixture:binding
  (testing "binding single var"
    (with-fixture binding-fixture:single-var
      (is (= 42 *test-var-0*))))
  (testing "binding single var to expression"
    (with-fixture binding-fixture:single-var-to-expression
      (is (= 42 *test-var-0*))))
  (testing "binding multiple vars"
    (with-fixture binding-fixture:multiple-vars
      (is (= 42 *test-var-0*))
      (is (= 24 *test-var-1*))))
  (testing "nested vars"
    (with-fixture binding-fixture:nested-vars
      (is (= 42 *test-var-0*))
      (is (= 43 *test-var-1*)))))
