<!-- This boilerplate-ish template will:
- define elements common for each page
  - TODO: move topnav and copyright notices to this file
- define the main stylesheet, shared across all elements
- render a `router-view`, which will take care of rendering the right view
  (a component defined under src/views/) based on current URL
-->
<template>
  <div id="app">
    <router-view />
  </div>
</template>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

#nav {
  padding: 30px;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
}

#nav a.router-link-exact-active {
  color: #42b983;
}
</style>
