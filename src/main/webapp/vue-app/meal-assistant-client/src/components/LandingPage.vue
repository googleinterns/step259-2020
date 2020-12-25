<template>
  <div class="landing-page">
    <nav class="topnav">
      <b> <a href="/"> Meal Assistant</a> </b>
    </nav>
    <div id="background">
      <div id="search-block">
        <div id="search-title">Meal Assistant</div>
        <div id="search-bar-block">
          <SearchBar />
        </div>
      </div>
      <PhotoCopyright />
    </div>
  </div>
</template>

<script>
import SearchBar from "./SearchBar.vue";
import PhotoCopyright from "./PhotoCopyright.vue";

export default {
  name: "LandingPage",
  components: {
    SearchBar,
    PhotoCopyright,
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
div {
  margin: 40px;
}
a {
  color: #42b983;
}
</style>
