function searchDishes() {
    fetch('/search-dishes').then(response => response.json()).then((dishes) => 
    {
        let container = document.getElementById("dishes-container");
        container.innerText = "";
        dishes = dishes || [""];
        dishes.forEach((dish) => {
            container.appendChild(createListElement(dish));
        });
    });
}

function createListElement(dish) {
    const listElement = document.createElement('p');
    listElement.innerText = dish;
    return listElement;
}