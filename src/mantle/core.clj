
(ns mantle.core
  "In which is defined utility macros and functions that wrap the clojure.core
  functions to provide additional functionality or convenience."
  (:refer-clojure :exclude [format merge-with])
  (:require [clojure.pprint :refer [cl-format]]))

(def t true)

(defmacro format
  "Replaces `clojure.core/format` with `clojure.pprint/cl-format`.  Takes the
  same arguments as [cl-format]; for details please refer to
  the [documentation][cl-format] for that function.
  [cl-format]: https://clojure.github.io/clojure/clojure.pprint-api.html#clojure.pprint/cl-format"
  [writer format-in & args]
  `(cl-format ~writer ~format-in ~@args))

(defn fmtstr
  "Provides a marginally more convenient form of `clojure.pprint/cl-format` by
  not requiring a writer to be specified when a formatted string is all that
  is needed."
  [format-in & args]
  (apply cl-format (conj args format-in nil)))

(defn merge-with [f & opts-and-maps]
  "Like `clojure.core/merge-with`, but nested maps are merged
  recursively. `merge-with` has the following structure:

  ```
  (merge-with f options* maps+)
  ```

  Options are specified as pairs of keywords and values; currently
  `:recursively` is the only supported option and its value is interpreted as
  either truthy or falsey.  If omitted, behaviour is exactly as
  `clojure.core/merge-with`.

  For example: `(merge-with f :recursively true {:a {}} {:a {:b 0}})`"
  ;; https://git.io/vDCeK by way of the now defunct clojure-contrib.
  (letfn [(destructure-args [opts-and-maps]
            (let [pairs       (partition-all 2 opts-and-maps)
                  [opts maps] (split-with #(keyword? (first %)) pairs)]
              [(into {} (map vec opts)) (reduce into [] maps)]))]
    (let [[opts maps] (destructure-args opts-and-maps)]
      (if (:recursively opts)
        (apply
         (fn m [& maps]
           (if (every? map? maps)
             (apply clojure.core/merge-with m maps)
             (apply f maps)))
         maps)
        (apply clojure.core/merge-with f maps)))))

(defmacro returning
  "Takes a single binding, executes `forms` in the context of that
  binding, and returns `val`.  Although similar in spirit to `doto`,
  it allows for arbitrary forms to be evaluated before returning
  `val`, and is thus closer to Ruby's `tap`."
  {:style/indent 1}
  [[var val] & body]
  `(let [~var ~val]
     ~@body
     ~var))

(defn select-values
  "Returns a sequence of only the values in `map` for which there keys in
  `keyseq`. Values are returned in the order in which the keys are
  specified. `nil`s are included for keys not present in the map; use `(remove
  nil? (select-values ...))` to filter them out."
  [m keyseq]
  (reduce #(conj %1 (get m %2)) [] keyseq))

(defn single
  "Returns the only element of a collection, and throws an exception if there
  is not exactly one element in the sequence."
  [coll]
  (assert (not (empty? coll)) "Expected a collection with a single element, found an empty collection.")
  (assert (empty? (rest coll)) "Expected a collection with a single element, found a collection with multiple elements.")
  (first coll))
