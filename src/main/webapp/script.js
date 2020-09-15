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


function createElementByTag(text, tag) {
  const element = document.createElement(tag);
  element.innerText = text;
  return element;
}

function capitalizeItems(string) {
  return string
    .split(' ')
    .map(word => word.charAt(0).toUpperCase() + word.slice(1))
    .join(' ');
}

// solution from https://stackoverflow.com/questions/20174280/nodejs-convert-string-into-utf-8
function encodingCheck(string) {
  return JSON.parse(JSON.stringify(string));
}
