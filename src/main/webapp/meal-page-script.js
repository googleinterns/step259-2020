// Copyright 2020 Google LLC

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

//     https://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

function uploadMealInfo() {
    fetch('/meal-info').then(response => response.json()).then((meal) => 
    {
        const titleElement = document.getElementById("title");
        titleElement.innerText = meal.title;
        const descriptionElement = document.getElementById("description");
        descriptionElement.innerText = meal.description;
        const ingredientsElement = document.getElementById("ingredients");
        for (const ingredient of meal.ingredients) {
            ingredientsElement.appendChild(createMyElement(ingredient, 'li'));
        }
        createMap();
    });
}

function createMyElement(text, type) {
  const element = document.createElement(type);
  element.innerText = text;
  return element;
}

/** Creates a map and adds it to the page. */
function createMap() {
  const styleOptions = [];
  const mapOptions = {
    zoom: 16,
    center: {lat: 55.746514, lng: 37.627022},
  };
  const map = new google.maps.Map(document.getElementById("map"), mapOptions);
}