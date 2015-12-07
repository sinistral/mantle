
(ns mantle.collection
  "In which is defined utility macros and functions that provide additional
  functionality or convenience functions for Clojure collections.")

(defn single
  "Returns the only element of a collection, and throws an exception if there
  is not exactly one element in the sequence."
  [coll]
  (assert (not (empty? coll)) "Expected a collection with a single element, found an empty collection.")
  (assert (empty? (rest coll)) "Expected a collection with a single element, found a collection with multiple elements.")
  (first coll))
