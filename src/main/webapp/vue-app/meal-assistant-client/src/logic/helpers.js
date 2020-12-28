export function createElementByTag(text, tag) {
  const element = document.createElement(tag);
  element.innerText = text;
  return element;
}

export function capitalizeItems(string) {
  return string
    .split(" ")
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join(" ");
}

// solution from https://stackoverflow.com/questions/20174280/nodejs-convert-string-into-utf-8
export function encodingCheck(string) {
  return JSON.parse(JSON.stringify(string));
}

export function getQueryParam(param) {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  return urlParams.get(param) ?? "";
}
