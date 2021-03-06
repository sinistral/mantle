
(ns mantle.io-test
  (:require [clojure.test :refer [deftest is testing]]
            [mantle.io    :refer [string-input-stream]]))

(deftest test:string-input-stream
  (is (= "foo" (slurp (string-input-stream "foo"))))
  (is (= "foo" (slurp (string-input-stream "foo" :charset "UTF-8"))))
  (is (= "�� f o o" (slurp (string-input-stream "foo" :charset "UTF-16")))))
