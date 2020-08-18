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

function fetchMealInfo() {
    // use mapping /meal.html?id=<id> 
    // fetches form server by action meal/<id>
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const id = urlParams.get("id");

    fetch('/meal/' + id.toString()).then(response => response.json()).then((meal) => 
    {
        const { title, description, ingredients } = meal;
        const titleElement = document.getElementById("title");
        titleElement.innerText = encodingCheck(title);
        const descriptionElement = document.getElementById("description");
        descriptionElement.innerText = encodingCheck(description);
        const ingredientsElement = document.getElementById("ingredients");
        for (const ingredient of ingredients) {
            ingredientsElement.appendChild(createElementByTag(encodingCheck(ingredient), 'li'));
        }
        createMap();
    });
}

// solution from https://stackoverflow.com/questions/20174280/nodejs-convert-string-into-utf-8
function encodingCheck(string) {
    return JSON.parse(JSON.stringify(string));
}

function createElementByTag(text, tag) {
  const element = document.createElement(tag);
  element.innerText = text;
  return element;
}

/** Creates a map and adds it to the page. */
function createMap() {
  const userLocation = new google.maps.LatLng(55.746514, 37.627022);
  const mapOptions = {
    zoom: 16,
    center: userLocation
  };
  const map = new google.maps.Map(document.getElementById("map"), mapOptions);
  // TODO(grenlayk): implement real geolocation here
  // TODO(grenlayk): change "You are here" marker on custom one
  const locationMarker = new google.maps.Marker({
    position: userLocation,
    map,
    title: "You are here",
    animation: google.maps.Animation.DROP,
  });
  // TODO(grenlayk): implement restaurants search here
}