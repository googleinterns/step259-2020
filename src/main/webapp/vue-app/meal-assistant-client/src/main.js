import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import LoadScript from "vue-plugin-load-script";

Vue.use(LoadScript);

Vue.config.productionTip = false;

// NOTE(zajonc): do not include the key - it's inactive anyway (as the time of writing).
// Requests to the places API will, unfortunately, fail, but the map will load correctly.
// That should do for UI purposes.
Vue.loadScript("https://maps.googleapis.com/maps/api/js?libraries=places")
  .catch((err) => {
    console.error(`Failed fetching google maps script: ${err}`);
  })
  .then(() => {
    new Vue({
      router,
      render: (h) => h(App),
    }).$mount("#app");
  });
