<!-- This boilerplate-ish template will:
- define elements common for each page
- define the main stylesheet, shared across all elements
- render a `router-view`, which will take care of rendering the right view
  (a component defined under src/views/) based on current URL
-->
<template>
  <!-- Top level div. All Vue rendered stuff will be under the "app" element -->
  <div id="app">
    <div id="content">
      <!-- Each page of our app shares some navigation-related things. Let's
          always render them at the very top.
          Note, that our app uses a consistent layout on every subpage, which is
          why we can simply define the shared bits here.
          In more complex apps, is is more common for each View to define its
          own layout, as it may differ across pages, e.g. a Meal form may not
          contain a search bar.
        -->
      <div id="header">
        <nav class="topnav">
          <b> <a href="/"> Meal Assistant</a> </b>
        </nav>
        <div id="search-bar-block">
          <SearchBar />
        </div>
      </div>

      <!-- This bit will dynamically render one of the components defined under
          views/ -->
      <router-view />
    </div>

    <!-- Copyright etc. can be stored in a separate div. They are not part of
        the app's content. Let's keep them at the bottom.  -->
    <footer>
      <PhotoCopyright />
    </footer>
  </div>
</template>

<script>
// `@` is an alias for "src/" (aka the main app directory).
import SearchBar from "@/components/SearchBar.vue";
import PhotoCopyright from "@/components/PhotoCopyright.vue";

export default {
  name: "App",
  components: {
    SearchBar,
    PhotoCopyright,
  },
};
</script>

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
