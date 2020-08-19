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

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
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

    private final HashMap<Long, Meal> dishes = new HashMap<Long, Meal> ();

    @Override
    public void init() {
        // TODO(sandatsian): implement uploading of dataset from website here
        dishes.put(0L, new Meal(0L, "Fried potato", "Fried potato with mushrooms and onion.",
            new ArrayList<>(Arrays.asList("Potato", "Onion", "Mushrooms", "Oil")), "main"));
        dishes.put(1L, new Meal(1L, "Italian pizza", "Pizza with pineaple, sausage and tomato.",
            new ArrayList<>(Arrays.asList("Flour", "Water", "Sausage", "Tomato", "Pineaple", "Cheese")), "pizza"));
        dishes.put(2L, new Meal(2L, "Vegetable soup", "Vegetable soup with onion.",
            new ArrayList<>(Arrays.asList("Potato", "Onion", "Cabbage", "Mushrooms", "Water", "Carrot", "Pumpkin")), "soup"));
        dishes.put(3L, new Meal(3L, "Chocolate cake", "Chocolate cake with butter cream and strawberry.",
            new ArrayList<>(Arrays.asList("Flour", "Water", "Butter", "Cacao", "Chocolate", "Sugar", "Strawberry", "Eggs")), "dessert"));
        dishes.put(4L, new Meal(4L, "Hot chocolate", "Hot chocolate with sugar and caramel.",
            new ArrayList<>(Arrays.asList("Cacao", "Sugar", "Milk", "Caramel", "Vanil")), "drinks"));
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();

        // Get meal
        // GET meal/
        if (pathInfo == null || pathInfo.equals("/")) {
            searchMeal(request, response);
            return;
        }

        
        if (pathInfo.split("/").length == 2) {
            String idString = pathInfo.replaceAll("/", "");
            if (idString.equals("similar")) {
                // GET meal/similar
                returnUrlToSimilar(request, response);
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

    private void searchMeal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Fuction returns hardcoded result
        // TODO(sandatsian): implement search algorithm here for MVP
        String gson = new Gson().toJson(dishes);
        response.setContentType("application/json");
        response.getWriter().println(gson);
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

        Meal meal = dishes.get(id);

        if (meal != null) {
            String gson = new Gson().toJson(meal);
            response.setContentType("application/json");
            response.getWriter().println(gson);
            return;
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
    }

     private void returnUrlToSimilar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Fuction redirect to random page
        // TODO(grenlayk): implement suggestions algorithm here for Product Alpha
        Random rand = new Random(); 
        int randomId = rand.nextInt(dishes.size()); 
        String url = "/meal.html" + "?id=" + Integer.toString(randomId);
        
        String gson = new Gson().toJson(url);
        response.setContentType("application/json");
        response.getWriter().println(gson);
    }


    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}