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

/**
 * parse.js
 *
 * MediaWiki API Demos
 * Demo of `Parse` module: Parse content of a page
 *
 * MIT License
 */

async function parseData() {
    // Parse data from single page.
    const url = "https://en.wikibooks.org/w/api.php?" +
    new URLSearchParams({
        origin: "*",
        action: "parse",
        page: "Cookbook:Racuszki",
        format: "json",
        prop: "text",
    });

    const request = await fetch(url);
    const json = await request.json();
    const container = document.getElementById("data-container");
    container.innerHTML = json.parse.text['*'];

    const ingredients = [];
    const ingrHTML = document.getElementsByTagName("ul").item(0).children;
    console.log(ingrHTML);
    Array.from(ingrHTML).forEach((el) => {
        ingredients.push(el.innerText);
        });
    
    // Create JSON String for Meal object.
    const data = JSON.stringify(
        {
            id: 25,
            title: "racuszki",
            description: "Racuszki are pancakes made from batter fried on a hot griddle or a pan. They are a traditional Polish meal.",
            type: "Pancake",
            ingredients: ingredients
        });
     
    window.location.replace("/import?input="+encodeURIComponent(data));
}
