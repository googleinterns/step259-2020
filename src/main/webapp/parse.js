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
/** 
 * parse.js
 *
 * MediaWiki API Demos
 * Demo of `Parse` module: Parse content of a page
 *
 * MIT License
 */

function sendData() {
    var url;
    try {
        url = "https://en.wikibooks.org/w/api.php?" +
        new URLSearchParams({
            origin: "*",
            action: "parse",
            page: `Cookbook:${document.getElementById("input-text").value}`,
            format: "json",
            prop: "text",
        });
    } catch(error) {
        console.log(error);
    }

    fetch(url)
        .then(response => response.json())
        .then((jsonData) => {
            const container = document.getElementById("data-container");
            container.innerHTML = jsonData.parse.text['*'];
            try {
                const ingrHTML = document.getElementsByTagName("ul").item(0).children;
                const list = document.getElementsByClassName("mw-parser-output")[0];
                const descriptionHTML = list.getElementsByTagName("p")[1];
                const title = jsonData.parse.title.replace("Cookbook:", "").toLowerCase();
                const description = descriptionHTML.textContent;
                const ingredients = Array.from(ingrHTML).map(el => el.innerText);
                // Create JSON String for Meal object.
                // By default id of object is 0, before putting object to Datastore
                // id is initialized.
                data = JSON.stringify(
                    {
                        id: 0, 
                        title: title,
                        description: description,
                        type: "", // TODO: find the way to fill this field
                        ingredients: ingredients
                    });
                
                return fetch("/import", {
                    method: 'POST',
                    body: data,
                    headers: { 'Content-type': 'application/json' }
                    });
            } catch (error) {
                console.log(error);
            }
        });    
}

