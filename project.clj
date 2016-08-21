(defproject com.nomistech/slurp-or-evaluate "0.1.0"
  :description "A kind of `def` that slurps cached values from file"
  :url "https://github.com/simon-katz/slurp-or-evaluate"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [midje "1.7.0"]]
                   :plugins [[lein-midje "3.1.3"]]}
             :uberjar {:aot :all}}
  :target-path "target/%s/")
