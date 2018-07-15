(ns cljs-styled-components.common
  (:require
    [clojure.string :as string]
    #?@(:cljs
        [["styled-components" :default styled :refer [keyframes ThemeProvider]]
         ["react" :as react]])))

#?(:cljs
   (defn debug [& args]
         (when goog/DEBUG
               (apply js/console.log args))))

(def clj-props-key "styled$clj-props")

(defn keyword->css-str [kw] (str (name kw) ":"))

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
         (fn [props] (f (goog.object/get props clj-props-key)))))

#?(:cljs
   (defn set-default-theme! [component-var theme-props]
         (goog.object/set (-> component-var meta :react-component) "defaultProps"
                          #js {:theme theme-props})))

#?(:cljs
   (defn map->template-str-args
         "Takes a map of css declaration properties to values, returns a vector containing
         two vectors: the static strings in the first, and the dynamic values in the second."
         [amap]
         (reduce
           (fn [[strs args :as acc] [k v]]
               (let [key-val
                     (cond
                       (and (keyword? k) (not= :styled/mixins k))
                       (keyword->css-str k)
                       (string? k) (str k ":")
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
