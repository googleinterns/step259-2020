const search = new Vue({
  el: '#search-bar',
  data: {
    request: "",
  },
  methods: {
      submit: function() {
        window.location.replace("/search-results.html");
        searchMeal();
      }
  }
});

