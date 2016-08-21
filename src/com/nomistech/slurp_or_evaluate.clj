(ns com.nomistech.slurp-or-evaluate
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def ^:private slurp-or-evaluate-store-dir "_slurp-or-evaluate-store")

(defn ^:private symbol->filename [sym]
  (str slurp-or-evaluate-store-dir
       "/"
       (name sym)))

(defn slurp-or-evaluate
  "Private.
  Private to this namespace, but needs to be public because of the
  way that symbols, namespaces and macros work in Clojure."
  [sym init-fun replace-stored-value?]
  ;; Clojure symbol/ns weirdness means this must be public.
  (let [file (-> sym
                 symbol->filename
                 io/file)]
    (if (or replace-stored-value?
            (not (.exists file)))
      (let [v (init-fun)]
        (do
          (io/make-parents file)
          (spit file v))
        v)
      (edn/read-string (slurp file)))))

(defmacro def-expensive
  "The same as `def`, except:
  - If `def-expensive` has a saved value, it will use that instead
    of evaluating `init`.
  - If `def-expensive` does not have a saved value, it will save
    the result of evaluating `init` for future use by `def-expensive`."
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
  "The same as `def`, except saves the result of evaluating `init` for
  future use by `def-expensive`."
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
