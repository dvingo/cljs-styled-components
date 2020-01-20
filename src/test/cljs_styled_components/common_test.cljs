(ns cljs-styled-components.common-test
  (:require
    [cljs.test :refer-macros [deftest is testing run-tests]]
    [cljs.pprint :refer [pprint]]
    ["polished" :refer [position size transitions em borderStyle hideText]]
    [cljs-styled-components.core :refer [parse-props]]
    [cljs-styled-components.common
     :refer [map->template-str-args]]))

(enable-console-print!)

(def sample-one
  (apply merge
         (js->clj
           [(position "absolute" "-22px" "5px" "5px" "4px")
            (transitions "opacity 0.5s ease-in 0s")
            (size "40px" "300px")
            (borderStyle "solid" "dashed" "dotted" "double")
            {:background-color "lightblue"
             :opacity          1
             :font-size        (em "16px")
             ":hover"          {:opacity 0.5}}])))

(def expected
  (str "border-right-style:dashed;width:300px;"
       "right:5px;top:-22px;height:40px;font-size:1em;border-top-style:solid;"
       "background-color:lightblue;border-left-style:double;"
       "position:absolute;:hover { opacity:0.5; }opacity:1;"
       "border-bottom-style:dotted;bottom:5px;transition:opacity 0.5s ease-in 0s;left:4px;"))

(deftest test-map->template-str-args
  (testing "It should work"
           (let [[[strs] args] (map->template-str-args sample-one)
                 light-blue (re-find #"lightblue" strs)]
                (js/console.log "test out str: ")
                (js/console.log "lightblue: " light-blue)
                (pprint strs)
                (is (= expected strs)))))

(deftest test-parse-props-js
         (let [props #js {:key-one 5}
               [out-props out-children] (parse-props "component name" props 2)]
              (is
                (= (js->clj out-props) {"key-one" 5 "styled$clj-props" {}}))
              (is (= out-children [2]))))

(deftest test-parse-props-cljs
         (let [props {:key-one 5}
               [out-props out-children] (parse-props "component name" props 2)]
              (is
                (= (js->clj out-props) {"key-one" 5 "className" "component name" "styled$clj-props" {}}))
              (is (= out-children [2]))))

(deftest test-parse-props-seq-children
         (let [props {}
               [out-props out-children] (parse-props "component name" props '(1 2 3))]
              (println "out-children: " out-children)
              (is
                (= (js->clj out-props) {"className" "component name" "styled$clj-props" {}}))
              (is (= out-children [1 2 3]))))

