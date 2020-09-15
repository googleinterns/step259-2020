Vue.component("photo-copyright", {
  template: `
    <div id="source">
      This web page uses images from 
      <a href="https://www.freepik.com/"> 
        freepik.com
      </a> 
      and data from 
      <a href="https://en.wikibooks.org/wiki/Cookbook:Table_of_Contents">
        wikibooks.org
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
      <input v-on:keyup.enter="submit" type="text" id="query" name="query" placeholder="Search for what you want to eat..." :value=query() />
      <div v-on:click="submit" type=submit id="search-icon"></div>
    </div>
  </div>
  `,
  methods: {
    submit: function () {
      const searchLine = document.getElementById('query').value;
      window.location.replace(`search-results.html?query=${encodeURIComponent(searchLine)}`); 
    },
    // `methods` can be used to render dynamic values in a template, based on the injected properties
    // and the current component state.
    // Since `query` is globally available, we can simply calculate it on every render.
    // Note, that if we were to build a full Vue app, the we'd probably want to read `getQueryParam` once,
    // somewhere at the root of our app, and simply pass it as a property to child components.
    query: function() {
      return getQueryParam("query");
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
