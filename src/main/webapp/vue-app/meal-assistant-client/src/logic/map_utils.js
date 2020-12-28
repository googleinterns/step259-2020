// Creates a map and adds it to the page.
//
// NOTE(zajonc): I've reused the existing code for simplicity.
// However, since we're using a modern framework, we could've simply used an existing library
// that solves all the manual stuff and exposes an easy to use API.
// [vue2-google-maps](https://www.npmjs.com/package/vue2-google-maps) seems like an interesting option.
//
// The JS ecosystem is massive. Whenever you try to use some popular tool (like Google Maps) from
// within a popular framework (like Vue), there's a huge chance you can reuse some library to make your
// life easier.
// This is possible because of the added build step - we can simply download dependencies from npm and bundle
// them into our final script.
//
// NOTE(zajonc): I've added an `element` argument to make the function a bit easier to use. In general, it's
// a good idea to avoid looking things up in the global state (here: `document.getElementById`) in helper functions.
// You can always pass things as arguments.
export function createMap(type, element) {
  const userLocation = new google.maps.LatLng(55.746514, 37.627022);
  const mapOptions = {
    zoom: 14,
    center: userLocation,
  };
  const map = new google.maps.Map(element, mapOptions);
  const locationMarker = new google.maps.Marker({
    position: userLocation,
    map,
    title: "You are here (probably)",
    label: "You",
    animation: google.maps.Animation.DROP,
  });

  // Geolocation warning will be retujrned from the promise.
  // If is not a critical error, therefore we do not reject the promise.
  let geoWarning = null;

  return getCurrentPositionPromise()
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
        geoWarning = "Your browser doesn't support geolocation.";
      } else if (err === "GET_POSITION_FAILED") {
        geoWarning =
          "The Geolocation service failed. Share your location, please.";
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
    })
    .finally(() => geoWarning);
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
