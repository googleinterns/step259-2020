<template>
  <div id="search-bar">
    <input
      v-on:keyup.enter="submit"
      type="text"
      id="query"
      name="query"
      placeholder="Search for what you want to eat..."
      :value="query()"
    />
    <div v-on:click="submit" type="submit" id="search-icon"></div>
  </div>
</template>

<script>
import { getQueryParam } from "../logic/helpers";

export default {
  props: {
    input: {
      type: String,
      required: false,
    },
  },
  methods: {
    submit: function () {
      const searchLine = document.getElementById("query").value;
      this.$router.push(
        `search-results?query=${encodeURIComponent(searchLine)}`
      );
    },
    // `methods` can be used to render dynamic values in a template, based on the injected properties
    // and the current component state.
    // Since `query` is globally available, we can simply calculate it on every render.
    // Note, that if we were to build a full Vue app, the we'd probably want to read `getQueryParam` once,
    // somewhere at the root of our app, and simply pass it as a property to child components.
    query: function () {
      return getQueryParam("query");
    },
  },
  data() {
    return {
      request: "",
    };
  },
};
</script>
