(ns cljs-styled-components.fulcro-cards
  (:require
    [fulcro.client.primitives :refer [defsc]]
    [fulcro.client.dom :as dom]
    [devcards.core :as dc :refer-macros [defcard]]
    ["polished" :refer [position size transitions em borderStyle hideText]]
    [cljs-styled-components.core
     :refer [clj-props set-default-theme!]
     :refer-macros [defstyled defkeyframes defglobalstyle]]))



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

(defstyled theme-consumer :div
           {:color #(goog.object/getValueByKeys % "theme" "textColor")})

(set-default-theme! theme-consumer #js {:textColor "yellow"})

(defn example-10 []
      (theme-consumer "hi"))

(defkeyframes
  spin
  "from { transform: rotate(0deg);}
   to { transform: rotate(360deg); }")

(defstyled rotate-text1
           :span
           {:animation (spin "2s linear infinite")
            :display "inline-block"
            :font-size "20px"})

(defn animation1 [txt]
      (rotate-text1 txt))

(def lex-time 20)

(defstyled rotate-text2
           :span
           {:animation (spin (str lex-time "s linear infinite"))
            :display   "inline-block"
            :font-size "20px"})

(defn animation2 [txt]
      (rotate-text2 txt))

(defstyled rotate-text3
           :span
           {:animation (clj-props (fn [a] (spin (str (:time a) "s linear infinite"))))
            :display   "inline-block"
            :font-size "20px"})

(defn animation3 [txt]
      (rotate-text3  {:clj {:time 10}} txt))

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

;(defstyled mixme4 :section stuff)

(defstyled
  mixme2 :section
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

(defstyled img
           :div
           {:background-image "url(img.png)"
            :styled/mixins (hideText)})


(defstyled example-11 :div
           [(position "relative" "20px")
             {:background "green"
              :opacity 1
              ":hover" [(transitions "opacity 1s ease-in 0s")
                        {:background "blue"
                         :opacity .5}]}])

(defstyled example-12 :div
           [{:background "red"}
            {:font-size "20px"}])

(def bp-query "@media (max-width: 700px)")

(defstyled breakpoints :div
           {"backgroundColor" "slategrey"
            bp-query           {:background "blue"}})

(defn my-component [props]
      (dom/div {:className (goog.object/get props "className")}
               (goog.object/get props "children")))

(defstyled extends-react-comp my-component {:border "1px solid"}).

(defglobalstyle
  my-global-styles
  {".my-global-class" {:background "palevioletred"}})

(defn px [x] (str x "px"))
(def cell-size 50)
(def get-styles
  (clj-props (fn [{:keys [width height empty?] :or {width cell-size height cell-size empty? false}}]
               {:width            (px width)
                :height           (px height)
                :display          "flex"
                :justify-content  "center"
                :align-items      "center"
                :border           (cond empty? "none" :else "1px solid")
                :background-color (cond empty? "none" :else "#e3e3e3")}))
  )
(defstyled fn-symbol :div get-styles)

(defstyled tempcell :div
             (clj-props (fn [{:keys [width height empty?] :or {width cell-size height cell-size empty? false}}]
                          {:width            (px width)
                           :height           (px height)
                           :display          "flex"
                           :justify-content  "center"
                           :align-items      "center"
                           :border           (cond empty? "none" :else "1px solid")
                           :background-color (cond empty? "none" :else "#e3e3e3")})))

(defstyled breakpointsfn :div
             (fn [_]
               {"backgroundColor" "slategrey"
                bp-query          {:background "blue"}}))

(defcard testing-fn #(tempcell "HIIII") {})
(defcard testing-fn-symbol #(fn-symbol "Function symbol") {})
(defcard testing-fn-w-props #(tempcell {:clj {:width 10 :height 100}} "HIIII") {})
(defcard breakpoints-fn  #(breakpointsfn "BREAK @ 700px") {})

(defcard testing-1 (example-1) {})
(defcard testing-2 (example-2) {})
(defcard testing-3 (example-3) {})
(defcard testing-5 (example-5) {})
(defcard testing-6 (example-6) {})
(defcard testing-7 (example-7) {})
(defcard testing-8 (example-8) {})
(defcard testing-9 (example-9) {})
(defcard base-component-card (extends-react-comp "hi2") {})
(defcard default-theme (example-10) {})
(defcard mixins-with-vectors (example-11 "mixins with vectors"))
(defcard mixins-with-vectors2 (example-12 "mixins with vectors"))
(defcard animation (animation1  " hello "))
(defcard animation2-card (animation2  " hello "))
(defcard animation3-card (animation3  " hello "))
(defcard mixins (mixins))
(defcard hide-text-polished-mixin (dom/div (img "some text")))
(defcard mixins3 (dom/div (mixme2 "some text")))
;(defcard mixins4 (dom/div (mixme4 "mixme 4 some text")))

(defcard breakpoint-card (breakpoints "testing breakpoints "))

(defcard global-styles (dom/div {:className "my-global-class"}
                                (my-global-styles)
                                (dom/p "hello paragraph")
                                "hello global styles"))
