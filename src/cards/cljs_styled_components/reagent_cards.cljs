(ns cljs-styled-components.reagent-cards
  (:require
    [devcards.core :as dc :refer-macros [defcard]]
    [reagent.core :as r]
    [cljs-styled-components.reagent :refer [clj-props] :refer-macros [defstyled]]))

(defstyled red :div
           {:color         "red"
            :border        "1px solid blue"
            :border-radius (clj-props #(if (:is-round? %) "10px" "0px"))})

(defn example []
      [red
       [:div "Hello Universe"]])

(defn example-2 []
      [:div
       [red "this is example 2"]])

(defn helper [txt] [:p txt])

(defn example-3 []
      [:div
       [red {:clj {:is-round? true}}
        [:p "some nested form"]
        [:p "antoher child"]]
       [red (helper "here is some text for you")]])

(defn example-4 []
      [:div
       [red
        [:p "child one"]
        [:p "child two"]]])

(defn example-5 []
      [:div
       [red
        [:p "child one"]
        [:p "child two"]
        [:p "child three"]]])

(defn example-6 []
      [red
       {:some-prop 5}
       [:p "child one"]
       [:p "child two"]
       [:p "child three"]]
      )

(defn example-7 []
      [red
       {:some-prop 5}
       [:p "child one"]
       [:p "child two"]
       [red "sub-red"]
       [:p "child three"]
       [:p "child four"]
       ]
      )

(defn example-8 []
      [red {:clj {:is-round? true}}
       [:p "some nested form"]])

(defn example-9 []
      [red {:clj {:is-round? true}}
       "some nested form"])


(defonce test-data (r/atom {:name "testing"}))

;(defcard testing-1 (dc/reagent example) test-data)
;(defcard testing-2 (dc/reagent example-2) test-data)
;(defcard testing-3 (dc/reagent example-3) test-data)
;(defcard testing-4 (dc/reagent example-4) test-data)
;(defcard testing-5 (dc/reagent example-5) test-data)
;(defcard testing-6 (dc/reagent example-6) test-data)
(defcard testing-7 (dc/reagent example-7) test-data)
;(defcard testing-8 (dc/reagent example-8) test-data)
;(defcard testing-9 (dc/reagent example-9) test-data)
