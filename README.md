# cljs-styled-components

A ClojureScript interface to the venerable [styled-components](https://www.styled-components.com) library.

The main interface of styled-components template strings is replaced by
Clojure maps.

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

## Usage


```clj
[cljs-styled-components.core :refer [defstyled defkeyframes theme-provider clj-props set-default-theme!]]
```

Here is a very simple usage:
```clj
(defstyled row-container
  :div {:display "flex"})
```
Which is functionally equivalent to the JS:

```js
const RowContainer = styled.div`
  display: flex;
`
const rowContainer =  (props, children) => React.createElement(RowContainer, props, children)
```


```clj
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


### Style mixins

This library play well with "mixins" such as [polished](https://github.com/styled-components/polished)

```bash
yarn add polished
```

## License

MIT License.

Copyright © 2018 Daniel Vingo.
