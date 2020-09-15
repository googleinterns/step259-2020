Vue.component("photo-copyright", {
  template: `
    <div id="source">
      This web page uses images from 
      <a href="https://www.freepik.com/"> 
        freepik.com</a> and data from <a href="https://en.wikibooks.org/wiki/Cookbook:Table_of_Contents">wikibooks.org</a>
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
      <input v-on:keyup.enter="submit" type="text" id="query" name="query" placeholder="Search for what you want to eat..." />
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
