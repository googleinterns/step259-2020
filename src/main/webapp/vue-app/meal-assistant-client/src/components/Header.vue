<!-- All pages but Home contain a navigation header rendered at the very top. -->
<template>
  <div id="header">
    <nav class="topnav">
      <b> <a href="/"> Meal Assistant</a> </b>
    </nav>
    <div id="search-bar-block">
      <SearchBar />
    </div>
  </div>
</template>

<script>
import SearchBar from "@/components/SearchBar.vue";

export default {
  name: "Header",
  components: {
    SearchBar,
  },
};
</script>

<style>
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
