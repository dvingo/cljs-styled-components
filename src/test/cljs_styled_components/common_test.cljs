(ns cljs-styled-components.common-test
  (:require
    [cljs.test :refer-macros [deftest is testing run-tests]]
    [cljs.pprint :refer [pprint]]
    ["polished" :refer [position size transitions em borderStyle hideText]]
    [cljs-styled-components.common
     :refer [map->template-str-args]]))

(enable-console-print!)

(def sample-one
  (apply merge
         (js->clj
           #js[(position "absolute" "-22px" "5px" "5px" "4px")
               (transitions "opacity 0.5s ease-in 0s")
               (size "40px" "300px")
               (borderStyle "solid" "dashed" "dotted" "double")]

           {:background-color "lightblue"
            :opacity          1
            :font-size        (em "16px")
            ":hover"          {:opacity 0.5}})))

(deftest test-map->template-str-args
  (testing "It should work"
    (let [out (map->template-str-args sample-one)]
         (js/console.log "test out: " )
         (pprint out))
    (is (= 1 1))))

(run-tests)
