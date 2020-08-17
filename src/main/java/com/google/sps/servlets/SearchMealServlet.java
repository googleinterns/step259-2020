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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

@WebServlet("/search-meal")
public class SearchMealServlet extends HttpServlet {

    // TODO: use Meal class instead of String
    private ArrayList<String> dishes;
    private ArrayList<String> result;

    @Override
    public void init() {
        dishes = new ArrayList<>();
        dishes.add("fried potato with mushrooms and onion");
        dishes.add("pizza with pineaple, sausage and tomato");
        dishes.add("vegetable soup with onion");
        dishes.add("chocolate cake with butter cream and strawberry");
        dishes.add("hot chocolate with sugar and caramel");
    }

    @Override
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

    private String getParameter(HttpServletRequest request, String name, String defaultValue) {	    
        String value = request.getParameter(name);	        
        if (value == null) {	        
            return defaultValue;	            
        }	     
        return value;	        
    }
}