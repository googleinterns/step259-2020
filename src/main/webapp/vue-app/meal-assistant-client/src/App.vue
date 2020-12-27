<!-- This boilerplate-ish template will:
- define elements common for each page
- define the main stylesheet, shared across all elements
- render a `router-view`, which will take care of rendering the right view
  (a component defined under src/views/) based on current URL
-->
<template>
  <!-- Top level div. All Vue rendered stuff will be under the "app" element -->
  <div id="app" :style="{ backgroundImage: `url(${backgroundImage})` }">
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
import backgroundImage from "../public/background-main.jpg";

export default {
  name: "App",
  components: {
    SearchBar,
    PhotoCopyright,
  },
  computed: {
    // This bit seems weird, and, to be honest, is weird, but one way to
    // reference an image is to import it in the script, expose it as a
    // property and reference it in the template.
    // Yep, it's massively overcompliated. Luckily, images defined in a
    // repo are not a common sight.
    backgroundImage() {
      return backgroundImage;
    },
  },
};
</script>

<style>
#app {
  width: 100%;
  text-align: center;
  min-height: 100vh;
  font-family: Georgia, serif;
  color: black;
  opacity: 0.75;
  background-repeat: no-repeat;
  background-size: cover;
  background-position: right top;
  background-attachment: fixed;

  /* To be honest, noone really understands CSS.
  Luckily, 99% of CSS problems can be solved by using flex.
  https://developer.mozilla.org/en-US/docs/Web/CSS/flex
  */
  display: flex;
  flex-direction: column;
}

#content {
  width: 80%;
  background: white;
  /* auto margin centers the element horizontally */
  margin: auto;
}

#header {
  overflow: hidden;
  font-size: 22px;
  margin: 20px;
}

/* add some margin between children */
#header > * {
  margin: 10px;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
}
</style>
