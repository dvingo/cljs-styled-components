# cljs-styled-components

A ClojureScript interface to the [styled-components](https://www.styled-components.com) library.

The main interface of styled-components' template strings is replaced by
ClojureScript maps.

It's mostly a lightweight transformation from ClojureScript maps to the form
that a template literal gets invoked with.

# Installation

This library will require "styled-components" from node_modules.

You can install it like so:

```bash
yarn add styled-components
# or
npm i -D styled-components
```

Then specify this library as a dependency:

```clj
[cljs-styled-components "0.1.0-SNAPSHOT"]
```

# Usage

Add the dependency to your namespace form:

```clojure
;; Plain React elements (e.g. used in fulcro):
[cljs-styled-components.core :refer [defstyled defkeyframes theme-provider clj-props set-default-theme!]]

;; For reagent support:
[cljs-styled-components.reagent :refer [defstyled defkeyframes theme-provider clj-props set-default-theme!]]
```

Here is a very simple usage:

```clojure
(defstyled row-container
  :div {:display "flex"})
```
Which is functionally equivalent to the JS:

```js
const RowContainer = styled.div`
  display: flex;
`
const rowContainer = (props, children) => React.createElement(RowContainer, props, children)
```

The first argument is the Var name that will be created, the second argument
can be one of:
  - a keyword will be invoked on the `styled` object

    ```clojure
    (defstyled example1 :p {:color "blue"})
    ```
  - a styled component - as constructed with `defstyled`, this will use `.extend`

    ```clojure
    (defstyled example2 example1 {:border "1px solid"})
    ```

     https://www.styled-components.com/docs/basics#extending-styles
  - any react component, which will invoke styled on the component

    ```clojure
    (defn my-component [props children]
      (dom/div {:className (:className props)} children))

    (defstyled example2 my-component {:border "1px solid"}).
    ```

    as described here:
    https://www.styled-components.com/docs/advanced#styling-normal-react-components

The third argument must be a ClojureScript map, this is computed at runtime
so you can construct this map anyway you like.

A more featureful example:

```clojure
(defstyled number-cell-styled
  :div
  {:background-color
                    (clj-props (fn [{:keys [selected? answered? answered-correctly? background-color]}]
                                 (cond
                                   background-color background-color
                                   selected? "darkGrey"
                                   (and answered? answered-correctly?) "green"
                                   (and answered? (not answered-correctly?)) "red"
                                   :else "hsl(37, 67%, 99%)")))
   :font-family     "patua"
   :color           #(goog.object/getValueByKeys % "theme" "textColor")
   :padding         "0"
   :height          cell-size-px
   :width           cell-size-px
   :min-width       cell-size-px
   :font-size       (str (/ cell-size 2) "px")
   :display         "flex"
   :justify-content "center"
   :align-items     "center"
   "@media (max-width: 700px)"
                    {:height    sm-cell-size-px
                     :min-width sm-cell-size-px
                     :width     sm-cell-size-px}})

(set-default-theme! number-cell-styled #js {:textColor "red"})
```

Nested selectors are supported as shown in this example with media queries.

## Props

Any property value that is a function will get passed the props that the component
was rendered with.

The props will be a JavaScript object, to make the code more cljs friendly the
following custom is used:

```clojure
;; At render time any data under the `:clj` key will remain as ClojureScript
;; data structures.
(dom/div {:clj {:round? true}})

;; Then pull them out with the helper `clj-props`
(defstyled example :div
           {:color         "red"
            :border        "1px solid blue"
            :border-radius (clj-props #(if (:round? %) "10px" "0px"))})
```

The top level map will converted to a JS object with (clj->js)
(the `:clj` key is dissoc'ed first).

Example using JS data structures:

```clojure
;; render time:
(example2 {:color "blue"})

(defstyled example :div
           {:color #(goog.object/get % "color")})
```

## Theme support

This is essentially the unmodified theme code that styled-components uses,
so everything must be in JS data.


```clojure
(defstyled theme-user :div
           {:color #(goog.object/getValueByKeys % "theme" "textColor")})

(def theme
  #js {:textColor "purple"})

;; fulcro
  (theme-provider #js {:theme theme}
    (dom/div
      (theme-user "hello")))

;; reagent
[theme-provider #js {:theme theme}
  [:div
   [theme-user "hello"]]]
```

## Animations


```clojure
(:require
  [cljs-styled-components.core :refer [clj-props] :refer-macros [defstyled defkeyframes]])

;; This just delegates to keyframes helper of styled-components.
(defkeyframes
  spin
  "from { transform: rotate(0deg);}
   to { transform: rotate(360deg); }")

;; `spin` will be the animation name generated by styled-components
(defstyled rotate-text :span
           {:animation (str spin " 2s linear infinite")
            :display "inline-block"
            :font-size "20px"})

;; Then just render like any component.
(defn animation [txt]
      (rotate-text txt))
```

## Style Mixins

### CLJS Maps

The styles are just maps so whatever code you want to combine them together:

```clojure
(def row
  {:display "flex"
   :justify-content "space-between"})

(defstyled my-list :div
 (merge
   row
   {:background "blue"}))
 ```

### JS Objects

This library play well with "mixins" such as [polished](https://github.com/styled-components/polished)

```bash
yarn add polished
```

Support for nested objects of properties is included, for example, many of the
mixins in polished (https://polished.js.org/docs/) have this shape:

```js
const div = styled.div`
  backgroundImage: url(logo.png);
  ${hideText()};
`
```

In cljs we need a map to have an even number of forms so support for this is
added by putting the mixins under the keyword: `:styled/mixins`.

Here's an example:

```clojure
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

```
If you only need one mixin, you can just include it and do not need to embed in
a vector:

```clojure
(defstyled :div
  {:background-image: "url(logo.png)"
   :sytled/mixins (hideText)})
```

Sometime after adding support for the above I realized you could also just:

```clojure
(defstyled my-component :div
  (merge
    (js->clj (position "absolute" "-22px" "5px" "5px" "4px"))
    {:color "blue"}))

(defstyled a-mixin-component
  (apply merge
         (js->clj
           #js[(position "absolute" "-22px" "5px" "5px" "4px")
               (transitions "opacity 0.5s ease-in 0s")
               (size "40px" "300px")
               (borderStyle "solid" "dashed" "dotted" "double")]
           {:background-color "lightblue"
            :opacity          1
            :font-size        (em "16px")
            ":hover"          {:opacity 0.5}})))
```

So either of the these forms work for including style mixin objects.

# Implementation notes

In JS:

```js
const aVar = 'good';

// These are equivalent:
fn`this is a ${aVar} day`;
fn([ 'this is a ', ' day' ], aVar);
```

So you could use styled components from ClojureScript directly by using the second
form, but this library is an experiment in making it a bit nicer to work with
from ClojureScript.

You can read more about template literals here:

https://www.styled-components.com/docs/advanced#tagged-template-literals

and here:

https://mxstbr.blog/2016/11/styled-components-magic-explained/

# License

MIT License.

Copyright © 2018 Daniel Vingo.
