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
    const id = urlParams.get("id") ?? 0;

    fetch('/meal/' + id.toString()).then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    }).then((meal) => 
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
    }).catch((error) => {
        //TODO(sandatsian): display error page
        console.log(error);
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

function redirectToSimilar() {
    fetch('/meal/similar').then(response => response.json()).then((id) => {
        const url = '/meal.html?id=' + id.toString();
        window.location.replace(url);
    });
}

/** Creates a map and adds it to the page. */
function createMap() {
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
    label: "U",
    animation: google.maps.Animation.DROP,
  });

  getCurrentPositionPromise()
  .then(position => {
    const location = new google.maps.LatLng(
        position.coords.latitude,
        position.coords.longitude
    );
    map.setCenter(location);
    locationMarker.setPosition(location);
  })
  .catch(err => {
    if (err === "NO_GEOLOCATION") {
      handleBrowserError();
    } else if (err === "GET_POSITION_FAILED") {
      handleConsentError();
    }
  })
  .then(() => {
    addRestaurants(map);
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


function addRestaurants(map) {
  // Hard coded restaurants
  const restaurants = [
    moveLocationBy(map.getCenter(), 0.001, 0.001), 
    moveLocationBy(map.getCenter(), -0.001, 0.001)
  ];

  // TODO(grenlayk): implement restaurants search here
  for (const restaurantLocation of restaurants) {
    const marker = new google.maps.Marker({
        position: restaurantLocation,
        map,
        title: "Restaurant",
        animation: google.maps.Animation.DROP
    });
  }
}


function moveLocationBy(location, latDiff, lngDiff) {
    const lat = location.lat() + latDiff;
    const lng = location.lng() + lngDiff;
    return {
        lat, 
        lng
    };
}


function handleConsentError() {
  alert(
    "Error: The Geolocation service failed.\n \
     Share your location, please."
  );
}


function handleBrowserError() {
  alert("Error: Your browser doesn't support geolocation.");
}
