
(ns mantle.test
  "In which is defined utility functions and macros that wrap clojure.test
  test functions to provide additional functionality or convenience."
  (:require [mantle.core :refer [fmtstr returning]]))

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
     {:setup      (fn [] ~(:setup spec))
      :binding    '~(:binding spec)
      :redefining '~(:redefining spec)
      :teardown   (fn [] ~(:teardown spec))}))

(defn ^{:private true} wrap-in-bindings
  [body bindings]
  (cond (empty? bindings)
        body
        (= (count bindings) 1)
        `(binding ~(first bindings) ~body)
        :else
        `(binding ~(first bindings)
           ~(wrap-in-bindings body (rest bindings)))))

(defn ^{:private true} wrap-in-setup-and-teardown
  [body setup-spec teardown-spec]
  `(do (~setup-spec)
       (let [x# (atom nil)]
         (try
           (swap! x# (fn [_#] (do ~@body)))
           (finally (~teardown-spec)))
         (deref x#))))

(defn ^{:private true} wrap-in-redefs-fn
  [body redefs-spec]
  `(with-redefs-fn ~(or redefs-spec {})
     (fn [] ~body)))

(defmacro with-fixture
  "Execute the test body (which may consist of multiple forms) in the context
  of the named `fixture`."
  [fixture & body]
  (let [{:keys [setup binding redefining teardown]
         :or   {:binding [] :redefining {}}}
        (@(or (resolve fixture)
              (throw (RuntimeException.
                      (fmtstr nil "Unknown fixture: ~a" fixture)))))]
    (-> body
        (wrap-in-setup-and-teardown setup teardown)
        (wrap-in-bindings binding)
        (wrap-in-redefs-fn redefining))))
