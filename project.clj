(defproject cljs-styled-components "0.1.9"
  :description "ClojureScript interface to styled-components"
  :url "http://github.com/dvingo/cljs-styled-components"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.10.1"]]

  :source-paths ["src/main"]
  :resource-paths ["resources"]

  :profiles {:cljs       {:source-paths ["src/main" "src/test" "src/cards"]
                          :dependencies [[binaryage/devtools "1.0.0"]
                                         [thheller/shadow-cljs "2.8.94"]
                                         [reagent "0.10.0" :exclusions [cljsjs/react]]
                                         [com.fulcrologic/fulcro "3.2.0"]
                                         [devcards "0.2.6" :exclusions [cljsjs/react cljsjs/react-dom]]]}})
