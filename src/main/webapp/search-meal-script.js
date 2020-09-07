// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

function searchMeal() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const searchLine = urlParams.get("query") ?? "";
  app.input = searchLine;
  console.log(app.input);

  fetch(`/meal?query=${searchLine}`)
    .then((response) => response.json())
    .then((dishes) => {
      const amount = document.getElementById('amount-block');
      const isSingular = dishes.length == 1;
      amount.innerText = `${dishes.length} result${isSingular? "" : "s"}`;
      const container = document.getElementById("dishes-container");
      container.innerText = "";
      dishes = dishes ?? { 0: "" };
      Object.entries(dishes).forEach((dish) => {
        container.appendChild(createMealBlock(dish[1]));
      });
      console.log(app.input);
    });
}

function createMealBlock(dish) {
  const blockElement = document.createElement("div");
  blockElement.setAttribute("id", "meal-block");
  blockElement.appendChild(createMealElement(dish));
  return blockElement;
}

function createMealElement(dish) {
  const { id, title, description } = dish;
  const aElement = document.createElement("a");
  aElement.setAttribute("href", `meal.html?id=${id}`);
  const insideDivElement = document.createElement("div");
  insideDivElement.appendChild(createElementByTag(title, "b"));
  insideDivElement.appendChild(createElementByTag(description, "p"));
  aElement.appendChild(insideDivElement);
  return aElement;
}
