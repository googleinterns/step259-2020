Vue.component("photo-copyright", {
  template: `
    <div id="source">
      <a href="https://www.freepik.com/"> 
        This web page uses images from freepik.com
      </a>
    </div>
  `,
});

Vue.component("search-bar", {
  props: {
    input: {
        type: String,
        required: false
    }
  },
  template: `
  <div id="search-bar">
    <div id="bar">
      <input v-on:keyup.enter="submit" type="text" id="query" name="query" placeholder="Search..." />
      <div v-on:click="submit" type=submit id="search-icon"></div>
    </div>
  </div>
  `,
  methods: {
    submit: function () {
      const searchLine = document.getElementById('query').value;
      window.location.replace(`search-results.html?query=${encodeURIComponent(searchLine)}`); 
    },
  },
  data() {
    return {
      request: "",
    };
  },
});

const app = new Vue({
  el: "#app",
});
