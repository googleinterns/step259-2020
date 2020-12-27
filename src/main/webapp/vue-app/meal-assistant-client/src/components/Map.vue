<template>
  <div>
    <div id="warning"></div>
    <div id="map">todo</div>
  </div>
</template>

<script>
import { FakeMealService } from "@/logic/service";

export default {
  props: {
    type: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      userLocation: null,
    };
  },
  created() {
    // This hook will be executed whenever we first render this component.
    this.getUserLocation();
  },
  methods: {
    getUserLocation: function () {
      // TODO
    },
  },
};
</script>
