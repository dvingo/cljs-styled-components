(defproject cljs-styled-components "0.1.11"
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
                                         [thheller/shadow-cljsjs "0.0.21"]
                                         [reagent "0.10.0" :exclusions [cljsjs/react]]
                                         [com.fulcrologic/fulcro "3.3.5"]
                                         ;; using devcards 0.2.6 results in:
                                         ;failed to load devcards.system.js ReferenceError: React is not defined
                                         ;    at eval (system.cljs:321)
                                         ;    at eval (system.cljs:321)
                                         ;    at eval (<anonymous>)
                                         ;    at Object.goog.globalEval (main.js:836)
                                         ;    at Object.env.evalLoad (main.js:2224)
                                         ;    at main.js:2417
                                         ;env.evalLoad @ main.js:2226
                                         ;(anonymous) @ main.js:2417
                                         ;20:48:14.723
                                         [devcards "0.2.5"]]}})
