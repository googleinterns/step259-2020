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
  if (id == null) {
    window.location.replace("error.html");
  }

  fetch(`/meal/${id}`)
    .then((response) => {
      if (!response.ok) {
        throw new Error("Network response was not ok");
        window.location.replace("error.html");
      }
      return response.json();
    })
    .then((meal) => {
      const { title, description, ingredients, type } = meal;
      const titleElement = document.getElementById("title");
      titleElement.innerText = encodingCheck(capitalizeFirst(title));
      const descriptionElement = document.getElementById("description");
      descriptionElement.innerText = encodingCheck(description);
      const ingredientsElement = document.getElementById("ingredients");
      for (const ingredient of ingredients) {
        ingredientsElement.appendChild(
          createElementByTag(encodingCheck(ingredient), "li")
        );
      }
      createMap(type);
    })
    .catch((error) => {
      console.log(error);
      window.location.replace("error.html");
    });
}

function redirectToSimilar() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const pageId = urlParams.get("id") ?? 0;
  fetch(`/meal/similar?id=${pageId.toString()}`)
    .then((response) => response.json())
    .then((id) => {
      const url = `/meal.html?id=${id.toString()}`;
      window.location.replace(url);
    });
}

/** Creates a map and adds it to the page. */
function createMap(type) {
  const userLocation = new google.maps.LatLng(55.746514, 37.627022);
  const mapOptions = {
    zoom: 14,
    center: userLocation,
  };
  const map = new google.maps.Map(document.getElementById("map"), mapOptions);
  const locationMarker = new google.maps.Marker({
    position: userLocation,
    map,
    title: "You are here (probably)",
    label: "You",
    animation: google.maps.Animation.DROP,
  });

  getCurrentPositionPromise()
    .then((position) => {
      const location = new google.maps.LatLng(
        position.coords.latitude,
        position.coords.longitude
      );
      map.setCenter(location);
      locationMarker.setPosition(location);
    })
    .catch((err) => {
      if (err === "NO_GEOLOCATION") {
        displayWarning("Your browser doesn't support geolocation.");
      } else if (err === "GET_POSITION_FAILED") {
        displayWarning(
          "The Geolocation service failed. Share your location, please."
        );
      }
    })
    .then(() => {
      const request = {
        location: map.getCenter(),
        radius: "1500",
        type: ["restaurant", "cafe", "meal_takeaway", "bar", "bakery"],
        query: type,
      };
      const service = new google.maps.places.PlacesService(map);
      return getSearchPromise(service, request, map).then((results) => {
        addRestaurants(results, map);
      });
    })
    .catch((err) => {
        console.log(`Caught error: ${err}`);
    });
}

function addRestaurants(results, map) {
  for (const result of results) {
    createMarker(result, map);
  }
}

function getSearchPromise(service, request) {
  return new Promise((resolve, reject) => {
    const onSuccess = (results, status) => {
      if (status !== "OK") {
        reject("FAILED");
      } else {
        resolve(results);
      }
    };
    const onError = () => reject("FAILED");
    service.textSearch(request, onSuccess, onError);
  });
}

function getCurrentPositionPromise() {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject("NO_GEOLOCATION");
    } else {
      const onSuccess = (position) => resolve(position);
      const onError = () => reject("GET_POSITION_FAILED");
      navigator.geolocation.getCurrentPosition(onSuccess, onError);
    }
  });
}

function createMarker(place, map) {
  const marker = new google.maps.Marker({
    map,
    position: place.geometry.location,
    title: place.name,
  });
  const infoWindow = new google.maps.InfoWindow({ content: place.name });
  marker.addListener("click", () => {
    infoWindow.open(map, marker);
  });
}

function displayWarning(message) {
  const warningElement = document.getElementById("warning");
  warningElement.innerText = "";
  warningElement.appendChild(createElementByTag("Warning: ", "b"));
  warningElement.appendChild(createElementByTag(message, "span"));
}
