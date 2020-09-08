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

package com.google.sps.wikiimport;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Entity;
import java.io.IOException;  
import java.net.URLDecoder;  
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.sps.data.Meal;

@WebServlet("/import")
public class WikiImportServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String json = request.getQueryString().replaceAll("input=", "");
        try {
            json = URLDecoder.decode(json);
        } catch (Exception e) {  
            System.out.println("Issue while decoding" +e.getMessage());
            return;  
        } 
        // Checking if JSON String is correct.
        System.out.println(json);
        Meal meal = new Gson().fromJson(json, Meal.class);
        System.out.println(meal.getTitle());

        // Put new Meal Entity to Datastore.
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(createMealEntity(meal));
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
}