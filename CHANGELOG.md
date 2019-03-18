# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [0.1.8] - 2019-03-18

- Turn off debug

[0.1.8]: https://github.com/dvingo/cljs-styled-components/compare/0.1.7...0.1.8

## [0.1.7] - 2019-03-18

- Fix bugs from previous update
  - set-default-theme! was broken
- Nested selectors like ":hover" were getting inserted in the stylesheet as  ":hover:{}",
  fixed this scenario generally for nested selectors.
- Add support for passing vectors of maps/JS objects

[0.1.7]: https://github.com/dvingo/cljs-styled-components/compare/0.1.6...0.1.7

## [0.1.6] - 2019-03-17
- Update code to support breaking changes in Styled Components v4.
  https://github.com/dvingo/cljs-styled-components/issues/2

  Relase info here: https://www.styled-components.com/releases#v4.0.0-beta.0

  Extending one styled component based on another no longer uses `extend`,
  we just invoke `styled` with the base component now.

  - Update the `keyframes` API to match the new call style.

  >
Keyframes is now implemented in a "lazy" manner: its styles will be injected with the render phase of components using them.

>
keyframes no longer returns an animation name, instead it returns an object which has method .getName() for the purpose of getting the animation name.

[0.1.6]: https://github.com/dvingo/cljs-styled-components/compare/0.1.5...0.1.6

## [0.1.5] - 2019-02-07
- Do not use shadow-cljs only syntax:
  https://github.com/dvingo/cljs-styled-components/issues/1

[0.1.5]: https://github.com/dvingo/cljs-styled-components/compare/0.1.4...0.1.5

## [0.1.4] - 2018-08-07
- Fix seq children - not just vectors.

[0.1.4]: https://github.com/dvingo/cljs-styled-components/compare/0.1.3...0.1.4

## [0.1.3] - 2018-08-07
- Fix non-seq children passed to a component created with defstyled - missed a spot.

[0.1.3]: https://github.com/dvingo/cljs-styled-components/compare/0.1.2...0.1.3

## [0.1.2] - 2018-08-07
- Fix non-seq children passed to a component created with defstyled.

[0.1.2]: https://github.com/dvingo/cljs-styled-components/compare/0.1.1...0.1.2


## [0.1.1] - 2018-07-07
- Initial release of basic functionality.

[0.1.1]: https://github.com/dvingo/cljs-styled-components/compare/0.1.0...0.1.1
