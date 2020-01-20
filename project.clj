(defproject cljs-styled-components "0.1.8"
  :description "ClojureScript interface to styled-components"
  :url "http://github.com/dvingo/cljs-styled-components"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.10.1"]]

  :source-paths ["src/main"]
  :resource-paths ["resources"]

  :profiles {:cljs       {:source-paths ["src/main" "src/test" "src/cards"]
                          :dependencies [[binaryage/devtools "0.9.10"]
                                         [thheller/shadow-cljs "2.4.17"]
                                         [reagent "0.8.1" :exclusions [cljsjs/react]]
                                         [fulcrologic/fulcro "2.5.12"]
                                         [fulcrologic/fulcro-inspect "2.2.0-beta5"]
                                         [devcards "0.2.4" :exclusions [cljsjs/react cljsjs/react-dom]]]}})
