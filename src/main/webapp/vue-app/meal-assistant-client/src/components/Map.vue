<template>
  <div>
    <div v-if="warning" id="warning">
      <b>Warning</b>
      <span>{{ warning }}</span>
    </div>
    <div id="map"></div>
  </div>
</template>

<script>
import { createMap } from "@/logic/map_utils";
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
      warning: null,
    };
  },
  mounted() {
    this.createMap();
  },
  methods: {
    createMap: function () {
      // Do the `getElementById` here, instead of inside the function - this
      // way it will be easier to maintain the code, because both the element
      // and the code referencing it are defined in the same file.
      const mapElement = document.getElementById("map");
      createMap(this.type, mapElement).then(
        (geoWarning) => (this.warning = geoWarning)
      );
    },
  },
};
</script>

<style scoped>
#map {
  border: thin solid black;
  height: 500px;
  padding-bottom: 10px;
  margin: auto;
}
</style>
