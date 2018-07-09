(ns cljs-styled-components.core
  (:require
    [clojure.string :as string]
    #?@(:cljs
        [["styled-components" :default styled :refer [keyframes ThemeProvider]]
         ["react" :as react]]))

  #?(:cljs (:require-macros cljs-styled-components.core)))

(defn keyword->str [kw] (subs (str kw) 1))

(defn keyword->css-str [kw] (str (subs (str kw) 1) ":"))

(defn join-last [avec astr]
  (if (empty? avec)
    (conj avec astr)
    (conj (pop avec) (str (last avec) astr))))

(defn vconcat [avec & args]
  (vec (apply concat avec args)))

#?(:cljs
   (defn factory-apply
     [cls]
     (fn [props & children]
       (apply react/createElement cls props children))))

#?(:cljs (def theme-provider (factory-apply ThemeProvider)))

#?(:cljs
   (defn clj-props [f]
     (fn [props] (f (goog.object/get props "clj-props")))))

#?(:cljs
   (defn set-default-theme! [component-var theme-props]
     (goog.object/set (-> component-var meta :react-component) "defaultProps"
                      #js {:theme theme-props})))

#?(:cljs
   (defn style-factory-apply
     [component-name class]
     (with-meta
       (fn [orig-props & orig-children]
         ;; Wrap our cljs props in js object
         ;; TODO: deduplicate the test logic.
         (let [children (cond
                          ;; Client code passed in an object as props.
                          ;; I think the intention here is when you pass a single react element as as child.
                          ;; I am not sure what happens if you pass a JS object as props, instead of a clojurescript map,
                          ;; will need to test. If this is problematic, then I should find a way to test
                          ;; if orig-props is a react element or not and use that to discriminate the use cases by.
                          (object? orig-props) (concat [orig-props] orig-children)

                          ;; Client code only passed in a map of props.
                          (and (nil? orig-children) (map? orig-props)) nil

                          ;; No props were passed, only children to render (which are bound to orig-props).
                          (nil? orig-children)
                          (if (coll? orig-props) orig-props [orig-props])

                          :else orig-children)
               props (cond
                       (object? orig-props) {}
                       (and (nil? orig-children) (map? orig-props)) orig-props
                       (nil? orig-children) {}
                       :else orig-props)
               clj-props (or (:clj props) {})
               react-props (clj->js (assoc (dissoc props :clj) :className component-name))
               ;; We place the clj props under their own key so the styled component callbacks can be passed
               ;; a clojure map instead of a JS object.
               react-props (goog.object/set react-props "clj-props" clj-props)]
           (apply react/createElement
                  class
                  react-props
                  children)))
       {:styled-class class})))

(comment
  ;; add to the children cond to support reagent.
  ;; Reagent hiccup style vector render.
  ;; (:require [reagent.core :as r])
  (and (seq orig-children)
       (vector? (first orig-children)))
  (let [component (first orig-children)]
    [(r/as-element component)]))

#?(:cljs
   (defn map->template-str-args
     "Takes a map of css declaration properties to values, returns a vector containing
     two vectors: the static strings in the first, and the dynamic values in the second."
     [amap]
     (reduce
       (fn [[strs args :as acc] [k v]]
         (let [key-val
               (if (and (keyword? k) (not= :styled/mixins k))
                 (keyword->css-str k)
                 k)]
           (cond

             ;; A JS object in shape expected by styled components as produced by polished for example.
             (= :styled/mixins key-val)
             [(conj strs "")
              (vconcat args v)]

             (string? v)
             [(join-last strs (str key-val v ";"))
              args]

             ;; This adds support of nested selector queries, such as media query support or hover pseudo selectors.
             (map? v)
             (let [[nested-strs nested-args] (map->template-str-args v)]
               (if (string? key-val)
                 (let [new-nested-str
                       ;; There are no dynamic args, just combine the strings and surround with braces.
                       (if (= (count nested-strs) 1)
                         (join-last strs
                                    (string/join " "
                                                 (conj (vconcat [key-val "{"] nested-strs)
                                                       "}")))
                         ;; There are dynamic args.
                         (vconcat (join-last strs (str key-val "{" (first nested-strs)))
                                  (join-last (vec (rest nested-strs)) "}")))]

                   [new-nested-str
                    (vconcat args nested-args)])

                 (let [new-strs-vec
                       ;; if nested-strs count is one we have: ["css here..."]
                       ;; There are no dynamic args, just combine the strings and surround with braces.
                       ;; Leave a gap for the dynamic selector (the key is resolved at runtime).
                       (if (= (count nested-strs) 1)
                         (conj strs
                               (string/join " " (conj (vconcat ["{"] nested-strs) "}")))
                         ;; There are dynamic args.
                         (vconcat (conj strs (str "{" (first nested-strs)))
                                  (join-last (vec (rest nested-strs)) "}")))]

                   [new-strs-vec
                    (vconcat (conj args key-val) nested-args)])))

             ;; Found a symbol or similar - supports using values from the lexical scope.
             :else
             [(conj (join-last strs key-val) ";")
              (conj args v)])))
       [[] []]
       amap)))

;; Without making a new var the compiler will give a warning
;; Use of undeclared Var cljs-styled-components.core/styled

#?(:cljs (def my-styled styled))

(defmacro defstyled

  ([component-name tag-name style-map]
   `(defstyled ~component-name `~my-styled ~tag-name ~style-map))

  ([component-name styled tag-name style-map]
   `(let [orig-name# ~(str (-> &env :ns :name) "/" component-name)
          ~'_ (js/console.log " in defstyled - component name is: " orig-name# " ns-name: ")
          ~'_ (js/console.log "in defstyled - styled is " (~'js* "typeof ~{}" ~styled) ", " ~styled)
          component-type# (cond
                            ;; a dom element like :div, same as styled.div``
                            ~(keyword? tag-name)
                            (goog.object/get ~styled ~(keyword->str tag-name))

                            ;; Any React Component.
                            (-> ~tag-name meta :styled-class nil?)
                            (~styled ~tag-name)

                            ;; Another styled component.
                            :else
                            (goog.object/get (-> ~tag-name meta :styled-class) "extend"))
          [template-str-args# template-dyn-args#] (map->template-str-args ~style-map)
          component-class# (.apply component-type#
                                   component-type#
                                   (apply cljs.core/array
                                          (concat
                                            [(apply cljs.core/array template-str-args#)]
                                            template-dyn-args#)))]
      (goog.object/set component-class# "displayName" orig-name#)
      (def ~component-name
        (~'cljs-styled-components.core/style-factory-apply orig-name# component-class#))
      (alter-meta! ~component-name assoc :react-component component-class#))))

(defmacro defkeyframes [name animation-str]
  `(def ~name
     ('`~keyframes
       (cljs.core/array ~animation-str))))
