<template>
  <div id="home">
    <h1 id="title">Meal Assistant</h1>
    <SearchBar />
  </div>
</template>

<script>
import SearchBar from "@/components/SearchBar.vue";

export default {
  name: "Home",
  components: { SearchBar },
};
</script>

<style scoped>
#home > * {
  margin: 30px auto;
}

#title {
  font-size: 40px;
}
</style>
