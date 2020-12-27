<template>
  <div class="meal-detail">
    <div v-if="this.meal">
      <h1 id="title">Best meal ever</h1>
      <div id="content">
        <p id="description">{{ meal.description }}</p>
        <h2>Ingredients:</h2>
        <ul id="ingredients">
          <li v-for="ingredient in meal.ingredients" :key="ingredient">
            {{ encodingCheck(ingredient) }}
          </li>
        </ul>
        <h2>Explore on a map:</h2>
        <Map :type="meal.type" />
      </div>
      <ShowSimilarMealButton :currentMealId="Number.parseInt(meal.id)" />
    </div>
    <div v-else>Waiting for the results...</div>
  </div>
</template>

<script>
import Map from "@/components/Map.vue";
import ShowSimilarMealButton from "@/components/ShowSimilarMealButton.vue";
import { FakeMealService } from "@/logic/service";
import { encodingCheck } from "@/logic/helpers";

export default {
  name: "MealDetail",
  components: {
    Map,
    ShowSimilarMealButton,
  },
  data() {
    return {
      meal: null,
    };
  },
  created() {
    // This hook will be executed whenever we first render this component.
    this.fetchMeal();
  },
  watch: {
    // call again the method if the route changes
    $route: "fetchMeal",
  },
  methods: {
    fetchMeal() {
      const service = FakeMealService;
      service.getOne(this.$route.params.id).then((meal) => (this.meal = meal));
    },
    encodingCheck(content) {
      return encodingCheck(content);
    },
  },
};
</script>
