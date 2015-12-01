
(ns mantle.test
  "In which is defined utility functions and macros that wrap clojure.test
  test functions to provide additional functionality or convenience."
  (:require [mantle [core :refer [returning]]
                    [io :as io]]))

(defmacro defixture
  "Defines a fixture; that is: a context within which a test may be executed.

  The `spec` that defines the fixture is a map that may contain any combination
  of the keys: `:setup`, `:teardown`, and `:binding`.  The `:setup` and
  `:teardown` keys should each have as their value a single form that will be
  run before and after the test, respectively.

  The value for the `:binding` key is a vector of bindings, where each element
  is a binding spec as one would give to `clojure.core/binding`; thus a vector
  of var-name, value pairs.  This successive set of bindings is nested below
  the previous, allowing inner bindings to refer to the freshly bound value of
  vars in the outer bindings, and ensuring that the values of vars bound for
  testing are reverted at the end of the test.  Bindings are created before
  setup and are released after teardown.

  A reasonably complete, albeit contrived, example of a fixture is:
  ```
  {:binding [[*conn* (atom nil)]]
   :setup (swap! *conn* (fn [_] (open-connection ...)))
   :teardown (close *conn*)}
  ```

  Fixtures are simply `defun`s and may be removed using `ns-unmap`."
  [name spec]
  `(defn ~name
     []
     {:setup (fn [] ~(:setup spec))
      :binding '~(:binding spec)
      :teardown (fn [] ~(:teardown spec))}))

(defn ^{:private true} make-bindings
  [bindings body]
  (if (= (count bindings) 1)
    `(binding ~(first bindings) ~body)
    `(binding ~(first bindings)
       ~(make-bindings (rest bindings) body))))

(defn ^{:private true} make-body
  [setup-spec body teardown-spec]
  `(do (~setup-spec)
       (try
         (do ~@body)
         (finally (~teardown-spec)))))

(defmacro with-fixture
  "Execute the test body (which may consist of multiple forms) in the context
  of the named `fixture`."
  [fixture & body]
  (let [{setup-spec :setup binding-spec :binding teardown-spec :teardown}
        (@(or (resolve fixture)
              (throw (RuntimeException.
                      (io/format nil "Unknown fixture: ~a" fixture)))))
        wrapped-body
        (make-body setup-spec body teardown-spec)
        bindings
        (if binding-spec
          (make-bindings binding-spec wrapped-body)
          wrapped-body)]
    bindings))
