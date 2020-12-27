<template>
  <div id="meal-button"><button>Show similar meal!</button></div>
</template>

<script>
import { FakeMealService } from "@/logic/service";

export default {
  props: {
    currentMealId: {
      type: Number,
      required: true,
    },
  },
  methods: {
    redirectToSimilar: function () {
      const service = FakeMealService;
      service.getSimilar(this.currentMealId).then((similarMealId) => {
        this.$router.push(`meal/${similarMealId}`);
      });
    },
  },
};
</script>
