(ns cljs-styled-components.common
  (:require
   [clojure.string :as string]
   #?@(:cljs
       [[goog.object]
        ["styled-components"
         :as styled
         :refer [keyframes ThemeProvider createGlobalStyle]]
        ["react" :as react]])))

#?(:cljs (goog-define DEBUG false))

;; Used to prevent generated code from needing to require goog.object
(defn obj-set [o k v] #?(:cljs (goog.object/set o k v) :clj  nil))
(defn obj-get [o k] #?(:cljs (goog.object/get o k) :clj  nil))

#?(:cljs
   (defn debug [& args]
         (when DEBUG
               (apply js/console.log (concat ["[DEBUG]"] args)))))

(def clj-props-key "styled$clj-props")

(defn keyword->css-str [kw] (str (name kw) ":"))

(defn props-macro
  [ks body]
  `(cljs-styled-components.core/clj-props
     (fn [{:keys ~ks}]
       ~body)))
(comment (props-macro [hidden? round?] {:background (if hidden? "black" "white")}))

(defn join-last [avec astr]
  (if (empty? avec)
    (conj avec astr)
    (conj (pop avec) (str (last avec) astr))))

(defn vconcat [avec & args]
  (vec (apply concat avec args)))

#?(:cljs
   (defn element? [el]
         (react/isValidElement el)))

#?(:cljs
   (defn factory-apply
         [cls]
         (fn [props & children]
             (apply react/createElement cls props children))))

#?(:cljs (def theme-provider* (factory-apply ThemeProvider)))

#?(:cljs
   (defn clj-props* [f]
         (fn [props] (f (obj-get props clj-props-key)))))

#?(:cljs
   (defn set-default-theme!* [component theme-props]
         (let [styled-class (or (-> component meta :styled-class) component)]
              (obj-set styled-class "defaultProps"
                               #js {:theme theme-props}))))

#?(:cljs
   (defn camel->kebab
         "e.g. borderTopStyle -> border-top-style"
         [a-str]
         (let [words (re-seq #"[a-zA-Z][a-z]+" a-str)]
              (string/join "-" (map #(string/lower-case %) words)))))

#?(:cljs
   (defn merge-vec-of-maps [vec-of-maps]
         (apply merge (js->clj vec-of-maps))))

#?(:cljs
   (defn map->template-str-args
         "Takes a map of css declaration properties to values, returns a vector containing
         two vectors: the static strings in the first, and the dynamic values in the second."
         [amap-or-vec]
         (let [amap (cond-> amap-or-vec (vector? amap-or-vec) merge-vec-of-maps)]
              (reduce
                (fn [[strs args :as acc] [k v]]
                    (let [key-val
                          (cond
                            (and (keyword? k) (not= :styled/mixins k))
                            (keyword->css-str k)

                            ;; Nested selector - do not append a ":" to the selector.
                            (and (string? k)
                                 (or (map? v) (vector? v)))
                            k

                            ;; If the key is camelCased, converted to kebab.
                            ;; camelCased keys would come from JS mixins like polished "fontFamily" etc.
                            ;; Append a ":" to the property as this will be part of the static part of the resultant template string.
                            (string? k)
                            (if (re-find #"[A-Z][a-z]+" k)
                              (str (camel->kebab k) ":")
                              (str k ":"))

                            :else k)]
                         (cond

                           ;; A JS object in shape expected by styled components as produced by polished for example.
                           (= :styled/mixins key-val)
                           [(conj strs "")
                            (vconcat args (if (vector? v) v [v]))]

                           (or (string? v) (number? v))
                           [(join-last strs (str key-val v ";"))
                            args]

                           ;; This adds support of nested selector queries, such as media query support or hover pseudo selectors.
                           (or (map? v) (vector? v))
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
                amap))))

#?(:cljs
   (defn clj-props->js
     "1. Removes :clj key,
      2. Adds a className of the component to the props. (could make this configurable) try setting a closure-define.
      3. Converts cljs map to js object.
      "
     [component-name props]
     (clj->js (-> (dissoc props :clj)
                  (update :className #(if (nil? %) component-name (str component-name " " %)))))))
