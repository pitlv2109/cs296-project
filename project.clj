(defproject url-shortener "0.1.0-SNAPSHOT"
  :description "A URL shortener service"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring "1.4.0-beta2"]
                 [compojure "1.3.4"]]

  :main url-shortener.core
  :min-lein-version "2.0.0"
  :uberjar-name "url-shortener.jar"
  :profiles {:dev
              {:main url-shortener.core/-dev-main}})