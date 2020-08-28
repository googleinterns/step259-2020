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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();

        // Get meal
        // GET meal/
        if (pathInfo == null || pathInfo.equals("/")) {
            getMealList(request, response);
            return;
        }

        if (pathInfo.split("/", -1).length == 2) {
            String requestType = pathInfo.replaceAll("/", "");
            if (requestType.equals("similar")) {
                // GET meal/similar
                returnIdOfSimilar(request, response);
            } else { 
                // GET meal/<meal_id>
                getMealById(request, response);
            }
            return;
        }

        // Invalid request format
        // GET meal/*/*
        if (pathInfo.split("/", -1).length != 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
    }

    /**
     * Creates an entity for Datastore with properties of class Meal.
     * @param meal object of class Meal for which the entity is creating.
     * @return new Entity object with necessary properties.
     */
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
        Query query = new Query("Meal").addSort("id", SortDirection.ASCENDING);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        List<Meal> meals = getDataFromDatastore(results);
        Gson gson = new Gson();
        response.setContentType("application/json;");
        response.getWriter().print(gson.toJson(meals));
        return;
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
        List<Meal> meals = getDataFromDatastore(results); 
        
        if (meals.size() > 1) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        if (meals.size() == 1) {
            Meal meal = (Meal)meals.get(0);
            if (meal != null) {
                String gson = new Gson().toJson(meal);
                response.setContentType("application/json;");
                response.getWriter().print(gson);
                return;
            }
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
    }

    private List<Meal> getDataFromDatastore(PreparedQuery query) {
        List<Meal> result = new ArrayList<>();
        for (Entity entity : query.asIterable()) {
            try {
                Long id = (Long)entity.getProperty("id");
                String title = (String) entity.getProperty("title");
                String description = (String) entity.getProperty("description");
                ArrayList<String> ingredients = (ArrayList<String>) entity.getProperty("ingredients");
                String type = (String) entity.getProperty("type");
                if (id == null || title.isEmpty() || ingredients.isEmpty()) {
                    continue;
                }
                Meal meal = new Meal(id, title, description, ingredients, type);
                result.add(meal);
            } catch(ClassCastException e) {
                System.out.println(e);
                continue;
            }
        }
        return result;
    }

    private void returnIdOfSimilar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Fuction redirect to random page
        // TODO(grenlayk): implement suggestions algorithm here for Product Alpha
        Query query = new Query("Meal");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        ArrayList<Long> idList = new ArrayList<>();
        for (Entity entity : results.asIterable()) {
            Long id = (Long)entity.getProperty("id");
            idList.add(id);
        }
        Random rand = new Random(); 
        int index = rand.nextInt(getDataFromDatastore(results).size());
        Long randomId = idList.get(index);
        Gson gson = new Gson();
        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(randomId));
        return;
    }

    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}