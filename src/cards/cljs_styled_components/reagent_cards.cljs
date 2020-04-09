(ns cljs-styled-components.reagent-cards
  (:require
    [devcards.core :as dc :refer-macros [defcard]]
    [reagent.core :as r]
    ["polished" :refer [position size transitions em borderStyle hideText]]
    [cljs-styled-components.reagent
     :refer [clj-props theme-provider set-default-theme! defglobalstyle]
     :refer-macros [defstyled defkeyframes]]))

(defstyled red :div
           {:color         "red"
            :border        "1px solid blue"
            :border-radius (clj-props #(if (:round? %) "10px" "0px"))})

(defstyled just-content :div
           {":before"
            {:content "'hello this is :before'"
             :width   "200px"}})

(defn example []
      [red
       [:div "Hello Universe"]])

(defn example-2 []
      [:div
       [red "this is example 2"]])

(defn helper [txt] [:p txt])

(defn example-3 []
      [:div
       [red {:clj {:round? true}}
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
       [red {:className "EXAMPLE"}
        [:p "child one"]
        [:p "child two"]
        [:p "child three"]]])

(defn example-6 []
      [red
       {:some-prop 5}
       [:p "child one"]
       [:p "child two"]
       [:p "child three"]])

(defn example-7 []
      [red
       {:some-prop 5}
       [:p "child one"]
       [:p "child two"]
       [red "sub-red"]
       [:p "child three"]
       [:p "child four"]])

(defn example-8 []
      [red {:clj {:round? true}}
       [:p "some nested form"]])

(defn example-9 []
      [red {:clj {:round? true}}
       "some nested form"])

(defn example-10 []
      [just-content])

(defstyled theme-user :div
           {:color #(goog.object/getValueByKeys % "theme" "textColor")})

(set-default-theme! theme-user #js {:textColor "yellow"})

(defn theme-default []
      [:div [theme-user "TEXT"]])

(defn themes []
      [theme-provider {:theme {:textColor "blue"}}
       [:div
        [theme-user "TEXT"]]])

(def map-o-styles
  {:color  "turquoise"
   :border "1px solid"})

(defstyled example-11
           :div
           (merge
             (js->clj (size "40px" "300px"))
             map-o-styles))

(defkeyframes
  spin
  "from { transform: rotate(0deg);}
   to { transform: rotate(360deg); }")

(defstyled rotate-text1
           :span
           {:animation (spin "2s linear infinite")
            :display   "inline-block"
            :font-size "20px"})

(defn example-12 []
      [rotate-text1 "hi"])

(defstyled example-13 :div
           [(position "relative" "20px")
            {:background "green"
             :opacity    1
             ":hover"    [(transitions "opacity 1s ease-in 0s")
                          {:background "blue"
                           :opacity    .5}]}])

(def bp-query "@media (max-width: 700px)")

(defstyled breakpoints :div
           {"backgroundColor" "slategrey"
            bp-query          {:background "blue"}})

(defstyled ampersand breakpoints
           {"&&&"
            {"backgroundColor" "purple"}})

(defn override-example []
      [ampersand "this is ampersand"])

(defonce test-data (r/atom {:name "testing"}))

(defglobalstyle
  my-global-styles
  {".my-global-class" {:background "palevioletred"
                       :border "2px dashed"
                       :border-radius (clj-props #(if (:round %) "8px") "0")}})

(defcard nil-props-with-children (dc/reagent (red nil "hi123")))
(defcard just-children (dc/reagent (red "hi456")))
(defcard testing-1 (dc/reagent example) test-data)
(defcard testing-2 (dc/reagent example-2) test-data)
(defcard testing-3 (dc/reagent example-3) test-data)
(defcard testing-4 (dc/reagent example-4) test-data)
(defcard testing-5 (dc/reagent example-5) test-data)
(defcard testing-6 (dc/reagent example-6) test-data)
(defcard testing-7 (dc/reagent example-7) test-data)
(defcard testing-8 (dc/reagent example-8) test-data)
(defcard testing-9 (dc/reagent example-9) test-data)
(defcard testing-10 (dc/reagent example-10) test-data)
(defcard testing-override (dc/reagent override-example) test-data)
(defcard rotate-text1-card (dc/reagent example-12) test-data)
(defcard theme-card (dc/reagent themes) test-data)
(defcard theme-card-default (dc/reagent theme-default) test-data)
(defcard map-of-styles (dc/reagent
                         [:div [example-11 "hi"]]) test-data)
(defcard vetor-mixins (dc/reagent
                        [:div [example-13 "hi"]]) test-data)

(defcard global-styles-card
         (dc/reagent
           [:div.my-global-class
            [my-global-styles {:clj {:round true}}]
            "This card inserts global styles"]))
