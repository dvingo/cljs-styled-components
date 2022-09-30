(defproject cljs-styled-components "0.1.12"
  :description "ClojureScript interface to styled-components"
  :url "http://github.com/dvingo/cljs-styled-components"
  :license {:name "MIT" :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.10.1"]]
  :source-paths ["src/main"]
  :resource-paths ["resources"]

  :profiles {:cljs {:source-paths ["src/main" "src/test" "src/cards"]
                    :dependencies [[binaryage/devtools "1.0.4"]
                                   [thheller/shadow-cljs "2.16.10"]
                                   [thheller/shadow-cljsjs "0.0.21"]
                                   [com.fulcrologic/fulcro "3.5.27"]
                                   [com.bhauman/rebel-readline-cljs "0.1.4"]
                                   [com.bhauman/figwheel-main "0.2.18"]
                                   [devcards/devcards "0.2.7"]
                                   [reagent "1.1.1" :exclusions [cljsjs/react]]]}})
