// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.sps.data.Meal;


@WebServlet("/meal/*")
public class MealServlet extends HttpServlet {

    @Override
    public void init() {
        // TODO(sandatsian): implement uploading of dataset from website here
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); 
        // add hard coded meals
        // 0...4
        datastore.put(createMealEntity(
            new Meal(0L, "Fried potato", "Fried potato with mushrooms and onion.",
                new ArrayList<>(Arrays.asList("potato", "onion", "mushrooms", "oil")), "Main")));
        datastore.put(createMealEntity(
            new Meal(1L, "Italian pizza", "Pizza with pineaple, sausage and tomato.",
                new ArrayList<>(Arrays.asList("flour", "water", "sausage", "tomato", "pineaple", "cheese")), "Pizza")));
        datastore.put(createMealEntity(
            new Meal(2L, "Vegetable soup", "Vegetable soup with onion.",
                new ArrayList<>(Arrays.asList("potato", "onion", "cabbage", "mushrooms", "water", "carrot", "pumpkin")), "Soup")));
        datastore.put(createMealEntity(
            new Meal(3L, "Chocolate cake", "Chocolate cake with butter cream and strawberry.",
                new ArrayList<>(Arrays.asList("flour", "water", "butter", "cocoa powder", "chocolate", "sugar", "strawberry", "eggs")), "Dessert")));
        datastore.put(createMealEntity(
            new Meal(4L, "Hot chocolate", "Hot chocolate with sugar and caramel.",
                new ArrayList<>(Arrays.asList("cocoa powder", "sugar", "milk", "caramel", "vanilla")), "Drinks")));
        // 5...9
        datastore.put(createMealEntity(
            new Meal(5L, "Cheese Omelette", "This tasty omelette is full of cheese.",
                new ArrayList<>(Arrays.asList("2 eggs", "salt", "butter", "Cheddar cheese", "Mozarella cheese")), "omelette")));
        datastore.put(createMealEntity(
            new Meal(6L, "Garlic, Tomato and Cheddar Sandwich", "A delicious sandwich for breakfast and fast food!",
                new ArrayList<>(Arrays.asList("2 slices brown bread", "clove garlic", "tomato", "Cheddar cheese", "green chili",
                "olive oil", "salt", "pepper")), "Main")));
        datastore.put(createMealEntity(
            new Meal(7L, "Chocolate Chips Pancake", "So delicious and chocolate breakfast.",
                new ArrayList<>(Arrays.asList("2 eggs", "salt", "butter", "flour", "sugar", "chocolate chips", "milk")), "Pancake")));
        datastore.put(createMealEntity(
            new Meal(8L, "Seafood Pizza", "Just from the bottom of the ocean...",
                new ArrayList<>(Arrays.asList("flour", "salt", "butter", "water", "oil", "shrimp", "tuna", "onion", "ctabstick",
                "scallops", "fish sticks", "garlic", "tomato sauce")), "Pizza")));
        datastore.put(createMealEntity(
            new Meal(9L, "Almond Fudge Brownies", "There is any almond lover's prime recipe for fudge brownies.",
                new ArrayList<>(Arrays.asList("2 eggs", "sugar", "butter", "milk", "almond", "vanilla", "cocoa powder")), "Dessert")));
        // TODO: create 5 new ones
        // 10...14
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();

        // Get meal
        // GET meal/
        if (pathInfo == null || pathInfo.equals("/")) {
            getMealList(request, response);
            return;
        }

        if (pathInfo.split("/").length == 2) {
            String idString = pathInfo.replaceAll("/", "");
            if (idString.equals("similar")) {
                // GET meal/similar
                returnIdOfSimilar(request, response);
            } else if (idString.contains("search")) {
                // GET meal/search
            } else { 
                // GET meal/<meal_id>
                getMealById(request, response);
            }
            return;
        }

        // Invalid request format
        // GET meal/*/*
        if (pathInfo.split("/").length != 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
    }

    private Entity createMealEntity(Meal meal) {
        Entity mealEntity = new Entity("Meal");
        mealEntity.setProperty("id", meal.getId());
        mealEntity.setProperty("title", meal.getTitle());
        mealEntity.setProperty("description", meal.getDescription());
        mealEntity.setProperty("ingredients", meal.getIngredients());
        mealEntity.setProperty("type", meal.getType());
 
        return mealEntity;
    }

    private void getMealList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Query query = new Query("Meal").addSort("id", SortDirection.DESCENDING);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        List<Meal> meals = getDataFromDatastore(results);
        Gson gson = new Gson();
        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(meals));
        return;
    }

    private void searchMeal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Fuction returns hardcoded result
        // TODO(sandatsian): implement search algorithm here for MVP
        /*String gson = new Gson().toJson(dishes);
        response.setContentType("application/json");
        response.getWriter().println(gson);
        return;*/
    }

    private void getMealById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        String idString = pathInfo.replaceAll("/", "");

        Long id = null;
        try {
            id = Long.parseLong(idString);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Filter propertyFilter = new FilterPredicate("id", FilterOperator.EQUAL, id);
        Query query = new Query("Meal").setFilter(propertyFilter);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        ArrayList<Meal> meals = getDataFromDatastore(results); 
        Meal meal = meals.get(0);
        if (meal != null) {
            String gson = new Gson().toJson(meal);
            response.setContentType("application/json");
            response.getWriter().println(gson);
            return;
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
    }

    private ArrayList<Meal> getDataFromDatastore(PreparedQuery query) {
        ArrayList<Meal> result = new ArrayList<>();
        for (Entity entity : query.asIterable()) {
            Long id = (Long)entity.getProperty("id");
            String title = (String) entity.getProperty("title");
            String description = (String) entity.getProperty("description");
            ArrayList<String> ingredients = (ArrayList<String>) entity.getProperty("ingredients");
            String type = (String) entity.getProperty("type");

            Meal meal = new Meal(id, title, description, ingredients, type);
            result.add(meal);
        }
        return result;
    }

    private void returnIdOfSimilar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Fuction redirect to random page
        // TODO(grenlayk): implement suggestions algorithm here for Product Alpha
        /*Random rand = new Random(); 
        int randomId = rand.nextInt(dishes.size()); 
        
        String gson = new Gson().toJson(randomId);
        response.setContentType("application/json");
        response.getWriter().println(gson);*/
    }


    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}