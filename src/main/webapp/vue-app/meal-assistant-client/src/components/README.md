# Components

Reusable Vue components.
As a rule of thumb, they should be kept small. If you find yourself writing some
complex functions as coponent methods, consider extracting the logic to a
standalone, pure-JS function and importing it into the component.

Components can be tested by rendering them inside a browser-like environment.
[Vue testing library](https://testing-library.com/docs/vue-testing-library/intro/)
provides a set of utilities that make testing Vue components easy.
