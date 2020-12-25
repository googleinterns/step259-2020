<template>
  <div class="home">
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
// `@` is an alias for "src/" (aka the main app directory).
import SearchBar from "@/components/SearchBar.vue";
import PhotoCopyright from "@/components/PhotoCopyright.vue";

export default {
  name: "Home",
  components: {
    SearchBar,
    PhotoCopyright,
  },
};
</script>
