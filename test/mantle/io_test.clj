
(ns mantle.io-test
  (:refer-clojure :exclude [format])
  (:require [clojure.test :refer :all]
            [mantle.core :refer [returning]]
            [mantle.io :refer :all])
  (:import [java.io StringWriter]))

(deftest test:format
  (is (= ":foo" (format nil "~a" :foo)))
  (is (= ":foo :bar" (format nil "~a ~a" :foo :bar)))
  (is (= ":foo" (str (returning [w (StringWriter.)] (format w "~a" :foo))))))

(deftest test:fmtstr
  (is (= ":foo" (fmtstr "~a" :foo)))
  (is (= ":foo :bar" (fmtstr "~a ~a" :foo :bar))))
