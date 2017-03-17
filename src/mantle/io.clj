
(ns mantle.io
  (:import [java.io ByteArrayInputStream]))

(defn string-input-stream
  "Returns a `slurp`-able input stream that contains `s`.  This can be useful
  as a double when testing functions that expect an input stream. The `charset`
  may be either a `String` naming a valid charset, or an instance of
  `java.nio.Charset` (please see the Javadoc for details).  If a charset is not
  specified, the default charset for the platform is used."
  [s & {:keys [charset]}]
  (letfn [(get-bytes [s]
            (if-not charset (.getBytes s) (.getBytes s charset)))]
    (-> s get-bytes ByteArrayInputStream.)))
