(ns cljs-styled-components.reagent
  (:require
    [reagent.core :as r]
    [cljs-styled-components.common :refer [keyword->css-str vconcat]]
    #?@(:cljs
        [["styled-components" :default styled :refer [keyframes ThemeProvider]]
         ["react" :as react]
         [cljs-styled-components.common
          :refer
          [element? factory-apply theme-provider clj-props* set-default-theme! clj-props-key debug]]]))
  #?(:cljs (:require-macros cljs-styled-components.reagent)))

#?(:cljs (def clj-props clj-props*))

#?(:cljs
   (defn relement?
         "Is it a reagent vector? (or a best effort guess at least.)"
         [el]
         (and (vector? el)
              (or
                (-> el first keyword?)
                (-> el first symbol?)
                (-> el first fn?)))))

#?(:cljs
   (defn parse-props
         "Normalize the properties passed to support all the usual calling use cases."
         ;; Setup props and children for calling React.createElement
         [component-name orig-props orig-children]
         (debug "parse-props props: " orig-props)
         (debug "parse-props children: " orig-children)
         (let [parse-children
               ;; Figure out what children were passed
               (fn parse-children* [ch]
                   (cond
                     (relement? ch)
                     [(r/as-element ch)]

                     (vector? ch)
                     (mapv (fn [el]
                               (if (relement? el)
                                 (r/as-element el)
                                 el))
                           ch)

                     :else [ch]))

               [react-props clj-props children]
               (cond

                 (nil? orig-props)
                 [(js-obj) {} nil]

                 ;; Client code passed in an object as props
                 ;; possibly children as well
                 (object? orig-props)
                 [orig-props {} (parse-children orig-children)]

                 (relement? orig-props)
                 (do
                   (debug "orig children: " orig-children)
                   [(js-obj) {}
                    (parse-children
                      (cond
                        (relement? orig-children)
                        (conj [orig-props] orig-children)

                        (vector? orig-children)
                        (vconcat [orig-props] orig-children)

                        :else orig-props))])

                 ;; Client code passed in a clj map of props and possible children.
                 (map? orig-props)
                 [(clj->js (assoc (dissoc orig-props :clj) :className component-name))
                  (or (:clj orig-props) {})
                  (parse-children orig-children)]

                 :else [(js-obj) {} orig-props])]
              ;; We place the clj props under their own key so the styled component callbacks can be passed
              ;; a clojure map instead of a JS object.
              (goog.object/set react-props clj-props-key clj-props)
              (debug "parse-props returning: " react-props " children: " children)
              [react-props children])))

#?(:cljs
   (defn style-factory-apply
         [component-name class]
         (with-meta
           (fn style-factory-apply*
               ([]
                 (let [[react-props children] (parse-props component-name nil nil)]
                      (apply react/createElement class react-props children))
                 )
               ;; Only props were passed
               ([orig-props]
                (debug "in arity 1 factory called with: " orig-props)
                (let [[react-props children] (parse-props component-name orig-props nil)]
                     ;(debug "calling createElement with: "  react-props "children: " children)
                    (apply react/createElement class react-props children)))

               ;; Props and children passed.
               ([orig-props orig-children]
                (debug "in arity 2 factory called with: " orig-props " children: " orig-children)
                (let [[react-props children] (parse-props component-name orig-props orig-children)]
                      ;(debug "in arity 2 creating element with: " react-props " children: " children)
                     (apply react/createElement class react-props children)))

               ([orig-props child-one & orig-children]
                 ;(debug "in arity 3 factory called with: " orig-props " child-one: " child-one " children: " orig-children)
                (when-not (relement? child-one)
                          (throw (js/Error. (str "Expected a Reagent element after first arg for: " component-name))))
                (let [[react-props children] (parse-props component-name orig-props (vconcat [child-one] orig-children))]
                     (apply react/createElement class react-props children))))

           {:styled-class class})))

;; Without making a new var the compiler will give a warning
;; Use of undeclared Var cljs-styled-components.core/styled

#?(:cljs (def my-styled styled))
#?(:cljs (def my-keyframes keyframes))

(defmacro defstyled

  ([component-name tag-name style-map]
   `(defstyled ~component-name `~my-styled ~tag-name ~style-map))

  ([component-name styled tag-name style-map]
   `(let [orig-name# ~(str (-> &env :ns :name) "/" component-name)
          ;~'_ (debug " in defstyled - component name is: " orig-name# " ns-name: ")
          ;~'_ (debug "in defstyled - styled is " (~'js* "typeof ~{}" ~styled) ", " ~styled)
          component-type# (cond
                            ;; a dom element like :div, same as styled.div``
                            ~(keyword? tag-name)
                            (goog.object/get ~styled ~(name tag-name))

                            ;; Any React Component.
                            (-> ~tag-name meta :styled-class nil?)
                            (~styled ~tag-name)

                            ;; Another styled component.
                            :else
                            (goog.object/get (-> ~tag-name meta :styled-class) "extend"))
          [template-str-args# template-dyn-args#] (~'cljs-styled-components.common/map->template-str-args ~style-map)
          component-class# (.apply component-type#
                                   component-type#
                                   (apply cljs.core/array
                                          (concat
                                            [(apply cljs.core/array template-str-args#)]
                                            template-dyn-args#)))]
      (goog.object/set component-class# "displayName" orig-name#)
      (def ~component-name
        (style-factory-apply orig-name# component-class#))
      (alter-meta! ~component-name assoc :react-component component-class#))))

(defmacro defkeyframes [name animation-str]
  `(def ~name
     (my-keyframes
       (cljs.core/array ~animation-str))))
