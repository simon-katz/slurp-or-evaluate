(ns com.nomistech.slurp-or-evaluate
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def slurp-or-evaluate-store-dir
  "The directory where slurp-or-evaluate stores saved values, as a string."
  "_slurp-or-evaluate-store")

(defn ^:private symbol->filename [sym]
  (str slurp-or-evaluate-store-dir
       "/"
       (name sym)))

(defn slurp-or-evaluate
  "Private.
  Private, but needs to be public because of the way that symbols, namespaces
  and macros work in Clojure."
  [sym init-fun replace-stored-value?]
  ;; Clojure symbol/ns weirdness means this must be public.
  (let [file (-> sym
                 symbol->filename
                 io/file)]
    (if (or replace-stored-value?
            (not (.exists file)))
      (let [v (init-fun)]
        (io/make-parents file)
        (binding [*print-length* nil
                  *print-level*  nil]
          (spit file v))
        v)
      (edn/read-string (slurp file)))))

(defmacro def-expensive
  "Like `def`, except:
  - If there is a saved value, uses that instead of evaluating `init`.
  - If there is not a saved value, saves the result of evaluating `init`
    to file for future use."
  ([sym init]
   `(def ~sym
      (slurp-or-evaluate '~sym
                         (fn [] ~init)
                         false)))
  ([sym doc-string init]
   `(def ~sym
      ~doc-string
      (slurp-or-evaluate '~sym
                         (fn [] ~init)
                         false))))

(defmacro def-expensive-replacing
  "Like `def-expensive-replacing`, but ignores any existing saved value."
  ([sym init]
   `(def ~sym
      (slurp-or-evaluate '~sym
                         (fn [] ~init)
                         true)))
  ([sym doc-string init]
   `(def ~sym
      ~doc-string
      (slurp-or-evaluate '~sym
                         (fn [] ~init)
                         true))))
