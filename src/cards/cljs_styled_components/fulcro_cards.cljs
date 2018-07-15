(ns cljs-styled-components.fulcro-cards
  (:require
    [fulcro.client.primitives :as prim :refer [defsc]]
    [fulcro.client.dom :as dom]
    [devcards.core :as dc :refer-macros [defcard]]
    ["polished" :refer [position size transitions em borderStyle]]
    [cljs-styled-components.core :refer [clj-props] :refer-macros [defstyled defkeyframes]]))

(defstyled red :div
           {:color         "red"
            :border        "1px solid blue"
            :border-radius (clj-props #(if (:round? %) "10px" "0px"))})

(defstyled just-content :div
           {":before"
            {:content "'hello'"
             :width   "200px"}})

(def flex
  {:display "flex"})

(defstyled big-red red
           {:font-size "24px"})

(defstyled flexes :div
           (merge flex {:background "grey"}))

(defn example-1 []
      (red (dom/div "Hello Universe")))

(defn example-2 []
      (dom/div
        (red "this is my example")
        (big-red "here is larger text")))

(defn helper [txt] (dom/p txt))

(defn example-3 []
      (flexes
        (dom/div "hello")
        (red {:clj {:round? true}}
             (dom/p "the border is rounded"))
        (red (helper "here is some text for you"))))


(defn example-4 []
      (dom/div
        (dom/div "hello")
        (red
          (dom/p "child one")
          (dom/p "child two"))))

(defn example-5 []
      (dom/div
        (dom/div "hello")
        (red
          (dom/p "child one")
          (dom/p "child two")
          (dom/p "child three"))))


(defn example-6 []
      (red {:prop-one 5}
           (dom/p "child one")))


(defn example-7 []
      (dom/div
        (dom/div "hello")
        (red
          (dom/p "child one")
          (dom/p "child two")
          (red "sub-red")
          (dom/p "child three"))))

(defn example-8 []
      (dom/div
        (dom/div "hello")
        (red {:some-prop 5
              :clj       {:round? true}}
             (dom/p "child one")
             (dom/p "child two")
             (red "sub-red")
             (dom/p "child three"))))


(defn example-9 []
      (just-content))

(defkeyframes
  spin
  "from { transform: rotate(0deg);}
   to { transform: rotate(360deg); }")

(defstyled rotate-text
           :span
           {:animation (str spin " 2s linear infinite")
            :display "inline-block"
            :font-size "20px"})

(defn animation [txt]
      (rotate-text txt))

(defstyled mixme :section
           {:background-color "lightblue"
            :opacity 1
            :font-size (em "16px")
            :styled/mixins
                              [(position "absolute" "-22px" "5px" "5px" "4px")
                               (transitions "opacity 0.5s ease-in 0s")
                               (size "40px" "300px")
                               (borderStyle "solid" "dashed" "dotted" "double")]
            ":hover"          {:opacity 0.5}})

(defn mixins []
      (dom/div {:style {:position "relative"}}
               (mixme " hi ")))

;(defcard testing-1 (example-1) {})
;(defcard testing-2 (example-2) {})
;(defcard testing-3 (example-3) {})
;(defcard testing-5 (example-5) {})
;(defcard testing-6 (example-6) {})
;(defcard testing-7 (example-7) {})
;(defcard testing-8 (example-8) {})
;(defcard testing-9 (example-9) {})
;(defcard animation (animation " hello "))
(defcard mixins (mixins))
