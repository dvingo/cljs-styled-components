(ns cljs-styled-components.fn-args
  (:require
    [fulcro.client.primitives :refer [defsc]]
    [fulcro.client.dom :as dom]
    [devcards.core :as dc :refer-macros [defcard]]
    ["polished" :refer [position size transitions em borderStyle hideText]]
    [cljs-styled-components.core
     :refer [clj-props set-default-theme!]
     :refer-macros [defstyled defstyledfn defkeyframes defglobalstyle]]))

(defn px [x] (str x "px"))
(def cell-size 50)
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
(defcard testing-fn-w-props #(tempcell {:clj {:width 10 :height 100}} "HIIII") {})
(defcard breakpoints-fn  #(breakpointsfn "BREAK @ 700px") {})



