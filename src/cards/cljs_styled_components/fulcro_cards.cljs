(ns cljs-styled-components.fulcro-cards
  (:require
    [fulcro.client.primitives :as prim :refer [defsc]]
    [fulcro.client.dom :as dom]
    [devcards.core :as dc :refer-macros [defcard]]
    [cljs-styled-components.core :refer [clj-props] :refer-macros [defstyled]]))

(defstyled red :div
           {:color         "red"
            :border        "1px solid blue"
            :border-radius (clj-props #(if (:is-round? %) "10px" "0px"))})

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
        (red {:clj {:is-round? true}}
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



(defcard testing-1 (example-1) {})
;(defcard testing-2 (example-2) {})
;(defcard testing-3 (example-3) {})
;(defcard testing-5 (example-5) {})
;(defcard testing-6 (example-6) {})
