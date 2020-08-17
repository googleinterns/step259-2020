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
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.sps.data.Meal;

@WebServlet("/meal/*")
public class MealServlet extends HttpServlet {

    private ArrayList<Meal> dishes;

    @Override
    public void init() {
        dishes = new ArrayList<>(Arrays.asList());
        dishes.add(new Meal(0, "fried potato", "fried potato with mushrooms and onion",
            new ArrayList<>(Arrays.asList("potato", "onion", "mushrooms", "oil")), "Main"));
        dishes.add(new Meal(1, "Italian pizza", "Pizza with pineaple, sausage and tomato",
            new ArrayList<>(Arrays.asList("flour", "water", "sausage", "tomato", "pineaple", "cheese")), "pizza"));
        dishes.add(new Meal(2, "vegetable soup", "vegetable soup with onion",
            new ArrayList<>(Arrays.asList("potato", "onion", "cabbage", "mushrooms", "water", "carrot", "pumpkin")), "soup"));
        dishes.add(new Meal(3, "chocolate cake", "chocolate cake with butter cream and strawberry",
            new ArrayList<>(Arrays.asList("flour", "water", "butter", "cacao", "chocolate", "sugar", "strawberry", "eggs")), "dessert"));
        dishes.add(new Meal(4, "hot chocolate", "hot chocolate with sugar and caramel",
            new ArrayList<>(Arrays.asList("cacao", "sugar", "milk", "caramel", "vanil")), "drinks"));
    }

    /*@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String gson = new Gson().toJson(result);

        response.setContentType("application/json");
        response.getWriter().println(gson);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        result = searchDishByKeyWord(getParameter(request, "text", ""));
        
        response.setContentType("application/json;");
        response.getWriter().println(result);
        response.sendRedirect("/index.html");
    }
    
    private ArrayList<String> searchDishByKeyWord(String requestedDish) {
        ArrayList<String> result = new ArrayList<String>();

        for (String dish : this.dishes) {
            if (dish.contains(requestedDish)) {
                result.add(dish);
            }
        }
        return result;
    }
*/

    // Get meal
	// GET/meal/
	// GET/meal/Id
    @Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String pathInfo = request.getPathInfo();

		if(pathInfo == null || pathInfo.equals("/")){
			String gson = new Gson().toJson(dishes);
            response.setContentType("application/json");
            response.getWriter().println(gson);
			return;
		}
		
		if(pathInfo.split("/").length != 2) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

        String Id = pathInfo.replaceAll("/", "");

        List<Meal> meal = dishes
            .stream()
            .filter(dish -> String.valueOf(dish.getId()).equals(Id))
            .collect(Collectors.toList());
        
        if (meal.size() > 0) {
            String gson = new Gson().toJson(meal);
            response.setContentType("application/json");
            response.getWriter().println(gson);
			return;
        }
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
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