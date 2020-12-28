import { render, fireEvent } from "@testing-library/vue";
import App from "@/App.vue";
import SearchResults from "@/views/SearchResults.vue";
import Home from "@/views/Home.vue";

// See https://testing-library.com/docs/vue-testing-library/examples for the testing API.
test("renders the app", async () => {
  const { findByText, getByText, getByPlaceholderText, getByRole } = render(
    App,
    {
      routes: [
        {
          path: "/search-results/",
          name: "SearchResults",
          component: SearchResults,
        },
        {
          path: "/",
          name: "Home",
          component: Home,
        },
      ],
      stubs: ["font-awesome-icon"],
    }
  );

  // Check that the header's there.
  // The `/` characters mean we're using a [Regular Expression](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Regular_Expressions).
  // Long story short, they're a bit more flexible than regular strings.
  getByText(/Meal Assistant/);

  const searchBox = getByPlaceholderText(/search/i);
  await fireEvent.update(searchBox, "some meal");
  await fireEvent.click(getByRole("button"));

  // Use `findBy` instead of `getBy`.
  // The main difference is that it waits for up to 1 second for the element to appear.
  // Since the results are returned by a Promise, it takes some time for them to appear.
  await findByText(/MealADescription/i);
});
