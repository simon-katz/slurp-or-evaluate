(ns com.nomistech.slurp-or-evaluate-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [com.nomistech.slurp-or-evaluate :as soe :refer :all]
            [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________

(defn filename->value [filename]
  (clojure.edn/read-string (slurp filename)))

(defn test-name->doc-string [test-name]
  (-> (resolve test-name)
      meta
      :doc))

;;;; ___________________________________________________________________________

(fact "`def-expensive` with no doc string works"

  (let [filename       (#'soe/symbol->filename 'test-with-no-doc-string)
        commentary     (atom [])
        add-commentary (partial swap! commentary conj)]

    (fact "no stored value"
      (io/delete-file filename true)
      (def-expensive test-with-no-doc-string
        (do (add-commentary "Computing a first time")
            :first-value))
      (fact (test-name->doc-string 'test-with-no-doc-string) => nil)
      (fact (filename->value filename) => :first-value)
      (fact @commentary => ["Computing a first time"]))
    
    (fact "with a stored value"
      (def-expensive test-with-no-doc-string
        (do (add-commentary "Computing a second time")
            :second-value))
      (fact (test-name->doc-string 'test-with-no-doc-string) => nil)
      (fact (filename->value filename) => :first-value)
      (fact @commentary => ["Computing a first time"]))))

;;;; ___________________________________________________________________________

(fact "`def-expensive` with a doc string works"
  
  (let [filename       (#'soe/symbol->filename 'test-with-a-doc-string)
        commentary     (atom [])
        add-commentary (partial swap! commentary conj)]

    (fact "no stored value"
      (io/delete-file filename true)
      (def-expensive test-with-a-doc-string
        "the first doc string"
        (do (add-commentary "Computing a first time")
            :first-value))
      (fact (test-name->doc-string 'test-with-a-doc-string) => "the first doc string")
      (fact (filename->value filename) => :first-value)
      (fact @commentary => ["Computing a first time"]))
    
    (fact "with a stored value"
      (def-expensive test-with-a-doc-string
        "the second doc string"
        (do (add-commentary "Computing a second time")
            :second-value))
      (fact (test-name->doc-string 'test-with-a-doc-string) => "the second doc string")
      (fact (filename->value filename) => :first-value)
      (fact @commentary => ["Computing a first time"]))))
