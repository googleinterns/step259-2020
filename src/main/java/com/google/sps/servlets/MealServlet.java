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
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.sps.data.DataConverter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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
        // GET meal?query=<smth>
        if (pathInfo == null || pathInfo.equals("/")) {
            getMealList(request, response);
            return;
        }

        if (pathInfo.split("/", -1).length == 2) {
            String requestType = pathInfo.replaceAll("/", "");
            if (requestType.equals("similar")) {
                // GET meal/similar?id=<meal_id>
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

    private void getMealList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<String> params = Arrays.asList(getParameter(request, "query", "").trim().replaceAll("\\s+", " ").split(" "));
        params.removeAll(Arrays.asList("\\"));
        Query query = new Query("Meal").addSort("id", SortDirection.ASCENDING);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        List<Meal> meals = DataConverter.getDataFromDatastore(results);

        // In case if search request is empty.
        if (params.isEmpty()) {
            response.setContentType("application/json");
            response.getWriter().print(new Gson().toJson(meals));
            return;
        }

        // Every object Meal adds if it contains at least one keyword in at least one field 
        // (type, title, description, ingredients).
        List<Meal> searchedMeal = new ArrayList<>();
        HashMap<Integer, List<Meal>> sortedMeal = new HashMap<>();
        for (Meal meal : meals) {
            int count = isResultOfSearch(meal, params);
            if (count > 0) {
                List<Meal> list = new ArrayList<>();
                if (sortedMeal.get(count) != null) {
                    list = sortedMeal.get(count);
                }
                list.add(meal);
                sortedMeal.put(count, list);
            }
        }
        for (int i = params.size(); i > 0; i --) {
            if (sortedMeal.get(i) == null) {
                continue;
            }
            List<Meal> list = sortedMeal.get(i);
            for (Meal meal : list) {
                searchedMeal.add(meal);
            }
        }
        response.setContentType("application/json");
        response.getWriter().print(new Gson().toJson(searchedMeal));
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
        List<Meal> meals = DataConverter.getDataFromDatastore(results); 
        
        if (meals.size() > 1) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        if (meals.size() == 1) {
            Meal meal = (Meal)meals.get(0);
            if (meal != null) {
                String gson = new Gson().toJson(meal);
                response.setContentType("application/json");
                response.getWriter().print(gson);
                return;
            }
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
    }

    private void returnIdOfSimilar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Fuction redirect to random page of the same type
        Long mealId = Long.parseLong(getParameter(request, "id", "0"));
        Meal meal = null;
        Filter idFilter = new FilterPredicate("id", FilterOperator.EQUAL, mealId);
        Query query = new Query("Meal").setFilter(idFilter);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<Meal> meals = DataConverter.getDataFromDatastore(datastore.prepare(query)); 
        if (meals.size() > 1) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        } else if (!meals.isEmpty()) {
            meal = (Meal)meals.get(0);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Filter anotherIdFilter = new FilterPredicate("id", FilterOperator.NOT_EQUAL, meal.getId());
        Filter typeFilter = new FilterPredicate("type", FilterOperator.EQUAL, meal.getType());
        CompositeFilter sameTypeFilter = CompositeFilterOperator.and(anotherIdFilter, typeFilter);
        Query typeQuery = new Query("Meal").setFilter(sameTypeFilter);
        List<Meal> sameTypeList = DataConverter.getDataFromDatastore(datastore.prepare(typeQuery));

        Meal randomMeal = meal;
        if (!sameTypeList.isEmpty()) {
            randomMeal = pickRandomMeal(sameTypeList);
        } else {
            Query idQuery = new Query("Meal").setFilter(anotherIdFilter);
            List<Meal> idList = DataConverter.getDataFromDatastore(datastore.prepare(idQuery));
            if (!idList.isEmpty()) {
                randomMeal = pickRandomMeal(idList);
            }
        }
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().print(gson.toJson(randomMeal.getId()));
        return;
    }

    private Meal pickRandomMeal(List<Meal> mealList) {
        Random rand = new Random(); 
        int index = rand.nextInt(mealList.size());
        return (Meal)mealList.get(index);
    }

    private int isResultOfSearch(Meal meal, List<String> params) {
        int counter = 0;
        for (String param : params) {
            if (meal.getTitle().contains(param) ||
                meal.getDescription().contains(param) ||
                meal.getType().contains(param)) {
                counter ++;
                break;
            } 
            for (String ingredient : meal.getIngredients()) {
                if (ingredient.contains(param)) {
                    counter ++;
                    break;
                }
            }
        }
        return counter;
    }
    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}