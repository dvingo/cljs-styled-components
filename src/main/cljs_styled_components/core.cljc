(ns cljs-styled-components.core
  (:require
    [clojure.string :as string]
    [cljs-styled-components.common :refer [keyword->css-str vconcat]]
    #?@(:cljs
        [["styled-components" :refer [default keyframes ThemeProvider css] :rename {default styled}]
         ["react" :as react]
         [cljs-styled-components.common
          :refer
          [element? factory-apply theme-provider* clj-props* set-default-theme!* clj-props-key debug]]]))
  #?(:cljs (:require-macros cljs-styled-components.core)))

#?(:cljs (def clj-props clj-props*))
#?(:cljs (def theme-provider theme-provider*))
#?(:cljs (def set-default-theme! set-default-theme!*))

#?(:cljs
   (defn parse-props
         "Normalize the properties passed to support all the usual calling use cases."
         ;; Setup props and children for calling React.createElement
         [component-name orig-props orig-children]
         (debug "parse-props props: " orig-props)
         (debug "parse-props children: " orig-children)
         (let [[react-props clj-props children]
               (cond

                 (and (nil? orig-props) (not (nil? orig-children)))
                 [(js-obj) {} orig-children]

                 (nil? orig-props)
                 [(js-obj) {} nil]

                 ;; Client code passed in an object as props or a single react element.
                 ;; possibly children as well
                 (object? orig-props)
                 (if (element? orig-props)
                   (cond
                     ;; Only react elements were passed.
                     (sequential? orig-children)
                     [(js-obj) {} (vconcat [orig-props] orig-children)]

                     ;; Two react elements were passed
                     (element? orig-children)
                     [(js-obj) {} [orig-props orig-children]]

                     ;; Only one element passed
                     (nil? orig-children)
                     [(js-obj) {} orig-props]

                     :else
                     (throw (js/Error. (str "Unknown child type passed to " component-name))))
                   ;; They only passed a JS object of props, wrap the children in a vector if needed.
                   [orig-props
                    {}
                    (if (sequential? orig-children) orig-children [orig-children])])

                 ;; Client code passed in a clj map of props and possible children.
                 (map? orig-props)
                 [(clj->js (assoc (dissoc orig-props :clj) :className component-name))
                  (or (:clj orig-props) {})
                  ;; Determine what children were passed
                  (if (not (sequential? orig-children))
                    [orig-children]
                    orig-children)]

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
                      (apply react/createElement class react-props children)))

               ;; Only props were passed
               ([orig-props]
                 (debug "in arity 1 factory called with: " orig-props)
                 (let [[react-props children] (parse-props component-name orig-props nil)]
                      (debug "calling createElement with: "  react-props "children: " children)
                      (apply react/createElement class react-props [children])))

               ;; Props and children passed.
               ([orig-props orig-children]
                 (debug "in arity 2 factory called with: " orig-props " children: " orig-children)
                 (let [[react-props children] (parse-props component-name orig-props orig-children)]
                      (debug "in arity 2 creating element with: " react-props " children: " children)
                      (apply react/createElement class react-props children)))

               ;; Mutliple children passed
               ([orig-props child-one & orig-children]
                 (debug "in arity 3 factory called with: " orig-props " child-one: " child-one " children: " orig-children)
                 (when-not (element? child-one)
                           (throw (js/Error. (str "Expected a React element after first arg for: " component-name))))
                 (let [[react-props children] (parse-props component-name orig-props (vconcat [child-one] orig-children))]
                      (debug "calling create el with: " children)
                      (apply react/createElement class react-props children))))
           {:styled-class class})))

;; Without making a new Var the compiler will give a warning
;; Use of undeclared Var cljs-styled-components.core/styled

#?(:cljs (def my-styled styled))
#?(:cljs (def my-keyframes keyframes))
#?(:cljs (def my-css css))

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

                            ;; Another styled component
                            (-> ~tag-name meta :styled-class)
                            (~styled (-> ~tag-name meta :styled-class))

                            ;; A React component
                            :else
                            (~styled ~tag-name))
          [template-str-args# template-dyn-args#] (~'cljs-styled-components.common/map->template-str-args ~style-map)
          ~'_ (cljs-styled-components.common/debug "template args: " template-dyn-args#)
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

;;
;; As of v4 we need to use the `css` helper. See:
;;  https://www.styled-components.com/docs/basics#animations
(defmacro defkeyframes [name animation-defn]
  `(defn ~name [animation-params#]
     (let [kf-name# (my-keyframes (cljs.core/array ~animation-defn))]
       (my-css (cljs.core/array "" (str " " animation-params#)) kf-name#))))
