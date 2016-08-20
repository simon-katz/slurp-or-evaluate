(ns com.nomistech.slurp-or-evaluate
  (:require [clojure.java.io :as io]))

(def ^:private expensive-store-dir "_expensive-store")

(defn ^:private symbol->filename [sym]
  (str expensive-store-dir
       "/"
       (name sym)))

(defn slurp-or-evaluate [sym init-fun replace-stored-value?]
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
      (clojure.edn/read-string (slurp file)))))

(defmacro def-expensive
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
