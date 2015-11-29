
(ns mantle.io
  "In which is defined utility functions and macros that wrap the Clojure
  I/O functions to provide additional functionality or convenience."
  (:require [clojure.pprint :refer [cl-format]]))

(defmacro format
  "Replaces `clojure.core/format` with `clojure.pprint/cl-format`.  Takes the
  same arguments as [cl-format]; for details please refer to
  the [documentation][cl-format] for that function.
  [cl-format]: https://clojure.github.io/clojure/clojure.pprint-api.html#clojure.pprint/cl-format"
  [writer format-in & args]
  `(cl-format ~writer ~format-in ~@args))
