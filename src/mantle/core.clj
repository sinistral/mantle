
(ns mantle.core
  "In which is defined utility macros and functions that wrap the clojure.core
  functions to provide additional functionality or convenience.")

(defmacro returning
  "Takes a single binding, executes `forms` in the context of that
  binding, and returns `val`.  Although similar in spirit to `doto`,
  it allows for arbitrary forms to be evaluated before returning
  `val`, and is thus closer to Ruby's `tap`."
  [[var val] & body]
  `(let [~var ~val]
     ~@body
     ~var))

(defn select-values
  "Returns a sequence of only the values in `map` for which there keys in
  `keyseq`. Values are returned in the order in which the keys are
  specified. `nil`s are included for keys not present in the map; use `(remove
  nil? (select-values ...))` to filter them out."
  [map keyseq]
  (reduce #(conj %1 (map %2)) [] keyseq))

(defn single
  "Returns the only element of a collection, and throws an exception if there
  is not exactly one element in the sequence."
  [coll]
  (assert (not (empty? coll)) "Expected a collection with a single element, found an empty collection.")
  (assert (empty? (rest coll)) "Expected a collection with a single element, found a collection with multiple elements.")
  (first coll))
