
(ns mantle.core
  "In which is defined utility macros and functions that wrap the core Clojure
  language to provide additional functionality or convenience.")

(defmacro returning
  "Takes a single binding, executes `forms` in the context of that
  binding, and returns `val`.  Although similar in spirit to `doto`,
  it allows for arbitrary forms to be evaluated before returning
  `val`, and is thus closer to Ruby's `tap`."
  [[var val] & body]
  `(let [~var ~val]
     ~@body
     ~var))
