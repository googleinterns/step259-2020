<template>
  <div class="search-results">
    <div v-if="this.results">
      <div id="amount-container">
        <div id="amount-block">{{ results.length }} result(s)</div>
      </div>
      <div id="dishes-container">
        <div v-for="dish in results" :key="dish.id">
          <a v-bind:href="`/meal/${dish.id}`">
            <div>
              <b>{{ capitalizeItems(dish.title) }}</b>
              <p>{{ dish.description }}</p>
            </div>
          </a>
        </div>
      </div>
    </div>
    <div v-else>Waiting for the results...</div>
  </div>
</template>

<script>
import { FakeMealService } from "@/logic/service";
import { getQueryParam, capitalizeItems } from "@/logic/helpers";

export default {
  name: "SearchResults",
  data() {
    return {
      results: null,
    };
  },
  created() {
    // This hook will be executed whenever we first render this component.
    this.fetchResults();
  },
  watch: {
    // call again the method if the route changes
    $route: "fetchResults",
  },
  methods: {
    fetchResults() {
      const service = FakeMealService;
      const query = getQueryParam("query");
      service.query(query).then((results) => (this.results = results));
    },
    capitalizeItems(text) {
      // Define a method calling an external JS function.
      // Vue templates cannot (AFAIK) call external functions, but they can access methods defined for the component.
      return capitalizeItems(text);
    },
  },
};
</script>

<style scoped>
#dishes-container > * {
  margin: 20px 0;
}
</style>
