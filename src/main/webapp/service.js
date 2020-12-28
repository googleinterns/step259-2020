// This module exposes objects responsible for communicating with the backend.
// One "proper" version, talking to the real Java service, and one fake
// variant, returning hardcoded values, useful for local testing.
//
// All communication with the service should be done through one of the objects
// below.
// All exported objects should implement the same interface (the same set of
// public methods, each with the exact same signature). If we used TypeScript,
// we'd have defined an
// [interface](https://www.typescriptlang.org/docs/handbook/interfaces.html).
//
// NOTE(zajonc): the reason I've introduced this layer is because I wanted to
// detach the frontend part from the backend.
// Ideally, we'd like to be able to run a simple HTTP server serving the
// frontend, without the need to spin up Tomcat / Google App Engine.
// By mocking the service calls, we get rid of a strong dependency on the
// server.
// (TLDR: it's easier to test a Vue app this way).
// It's often a good idea to have a clear split between the frontend and the
// backend, though. In many cases they may even belong to separate
// repositories. You may even have multiple frontend apps for the same backend.

// Builds HTTP requests and forwards them to the right endpoint.
// NOTE: I wasn't really able to test it - I might have messed something up here.
const RealMealService = {
  getOne: (mealId) =>
    fetch(`/meal/${mealId}`).then((response) => {
      if (!response.ok) {
        throw new Error("Network response was not ok");
        window.location.replace("error.html");
      }
      return response.json();
    }),
  getSimilar: (mealId) =>
    fetch(`/meal/similar?id=${mealId}`).then((response) => response.json()),
  query: (queryContent) =>
    fetch(`/meal?query=${queryContent}`).then((response) => response.json()),
};

// Fake variant, returning hardcoded values.
// No real network requests are made to the service.
// All methods return `Promise`s, for compatibility with the real service.
const FakeMealService = {
  getOne: (mealId) => {
    if (mealId == 1) {
      return Promise.resolve(fakeMealA);
    } else if (mealId == 2) {
      return Promise.resolve(fakeMealB);
    } else {
      return Promise.resolve(null);
    }
  },
  // If current meal is A, suggest B. Suggest A otherwise.
  getSimilar: (mealId) =>
    Promise.resolve(mealId == fakeMealA.id ? fakeMealB.id : fakeMealA.id),
  // Return all known meals, regardless of the query.
  query: (queryContent) => Promise.resolve([fakeMealA, fakeMealB]),
};

// NOTE(zajonc): I'm not too creative :/
const fakeMealA = {
  id: 1,
  title: "MealA",
  description: "MealADescription",
  ingredients: ["Ingredient1, Ingredient2"],
  type: "MealAType",
};

const fakeMealB = {
  id: 2,
  title: "MealB",
  description: "MealBDescription",
  ingredients: ["Ingredient3, Ingredient4"],
  type: "MealBType",
};
