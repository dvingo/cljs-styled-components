# cljs-styled-components


---

_note_ 

I made a similar library to this one built on top of emotion instead of styled-components.

https://github.com/dvingo/cljs-emotion

Emotion allows passing functions as children when defining styles and has built-in server-side rendering support.

---


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
npm i styled-components
```

Then specify this library as a dependency using lein, boot, deps:

[![Clojars Project](https://img.shields.io/clojars/v/cljs-styled-components.svg)](https://clojars.org/cljs-styled-components)

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
  - a styled component - as constructed with `defstyled`

    ```clojure
    (defstyled example2 example1 {:border "1px solid"})
    ```

     https://www.styled-components.com/docs/basics#extending-styles
  - any react component, which will invoke styled on the component

    ```clojure
    (defn my-component [props]
      (dom/div {:className (goog.object/get props "className")}
          (goog.object/get props "children")))

    (defstyled example2 my-component {:border "1px solid"}).
    ```

    as described here:
    https://www.styled-components.com/docs/advanced#styling-normal-react-components

The third argument must be a ClojureScript map or a ClojureScript vector, this is computed at runtime
so you can construct this map/vector any way you like.

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
(example {:clj {:round? true}})

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
(defstyled example2 :div
           {:color #(goog.object/get % "color")})

;; render time:
(example2 {:color "blue"})
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

As of Styled components [V4](https://www.styled-components.com/releases#v4.0.0):

> Keyframes is now implemented in a "lazy" manner: its styles will be injected with the render phase of components using them.

> keyframes no longer returns an animation name, instead it returns an object which has method .getName() for the purpose of getting the animation name.

The current strategy to deal with this change is to have `defkeyframes` return
a function that delegates to `css` for you. (see: https://www.styled-components.com/docs/basics#animations)

An example:

```clojure
(:require
  [cljs-styled-components.core :refer [clj-props] :refer-macros [defstyled defkeyframes]])

(defkeyframes
  spin
  "from { transform: rotate(0deg);}
   to { transform: rotate(360deg); }")

;; `spin` will be a function that delegates to the styled components css helper
;; as described here:
;; https://www.styled-components.com/docs/basics#animations
(defstyled rotate-text :span
           {:animation (spin "2s linear infinite")
            :display "inline-block"
            :font-size "20px"})

;; Then just render like any component.
(defn animation [txt]
      (rotate-text txt))

;; An example reading data from props:
(defstyled rotate-text2
  :span
  {:animation (clj-props (fn [a] (spin (str (:time a) "s linear infinite"))))
   :display   "inline-block"
   :font-size "20px"})

(defn animation2 [txt]
      (rotate-text2  {:clj {:time 10}} txt))
```

## Style Mixins

### CLJS Maps

The styles are just maps so you can use whatever code you want to combine them together:

```clojure
(def row
  {:display "flex"
   :justify-content "space-between"})

(defstyled my-list :div
  (merge
    row
    {:background "blue"}))
 ```

### Vectors
As a convenience you can also pass a vector of maps which will be merged for you:

```clojure
(defstyled example-12 :div
           [{:background "red"}
            {:font-size "20px"}])
```

Passing JavaScript objects is also supported, as well as in nested positions:

```clojure
(defstyled example-11 :div
 [(position "relative" "20px")
   {:background "green"
    :opacity 1
    ":hover" [(transitions "opacity 1s ease-in 0s")
              {:background "blue"
               :opacity .5}]}])
```

### JS Objects

This library plays well with "mixins" such as [polished](https://github.com/styled-components/polished)

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
The library will merge JS objects and CLJS maps for you if you pass a vector like so:

```clojure
(defstyled my-component :div
  [(position "absolute" "-22px" "5px" "5px" "4px")
   {:color "blue"}])

;;
(defstyled sample :section
  [(position "absolute" "-22px" "5px" "5px" "4px")
   (transitions "opacity 0.5s ease-in 0s")
   (size "40px" "300px")
   (borderStyle "solid" "dashed" "dotted" "double")
   {:background-color "lightblue"
    :opacity          1
    :font-size        (em "16px")
    ":hover"          {:opacity 0.5}}])
```

So either of the these forms work for including style mixin objects.

## Global styles

You can use the macro `defglobalstyle` which takes the same arguments as defstyled except for the "type" of element
as there is none, and delegates to `createGlobalStyle` of styled-components.

see: https://styled-components.com/docs/api#createglobalstyle

Example:

```clojure
;; require the macro:
(:require [cljs-styled-components.core :refer-macros [defglobalstyle]])
(:require [cljs-styled-components.reagent :refer-macros [defglobalstyle]])

(defglobalstyle
  my-global-styles
  {".my-global-class" {:background "palevioletred"
                       :border "2px dashed"
                       :border-radius (clj-props #(if (:round %) "8px") "0")}})

;; reagent
(defn my-component []
  [:div.my-global-class
    [my-global-styles {:clj {:round true}}]
    "This inserts global styles"])

;; fulcro
(dom/div {:className "my-global-class"}
    (my-global-styles)
    "This inserts global styles")
```

## Props macro helper

You can use the `sprops` macro to clean up accessing props.

```clojure
[cljs-styled-components.core :refer-macros [defstyled sprops]]
;; or:
[cljs-styled-components.reagent :refer-macros [defstyled sprops]]

(defstyled use-props-macro :div
  {:border-radius (sprops [round?] (if round? "4px" 0))})
;; expands to:
(defstyled use-props-macro :div
  {:border-radius (clj-props (fn [{:keys [round?]}] (if round? "4px" 0)))})

(dom/div {:clj {:round? true}} "use props")
or
[:div {:clj {:round? true}} "use props"]
```

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

# Development

Right now all dev is done in dev cards.

```bash
yarn start
```

Browse to:

http://localhost:8923/cards.html

## Run tests

```bash
# Optional but starts shadow cljs server for quicker test compile times.
yarn start
yarn test
```

Deploy notes

- Update project.clj version
- Update changelog
- git commit new code
- git tag the current version
- push to remote
- git push origin --tags
- lein deploy clojars

# License

MIT License.

Copyright © 2020 Daniel Vingo.
