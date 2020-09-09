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

package com.google.sps.data;

import com.google.appengine.api.datastore.Entity;  
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.sps.data.Meal;
import java.util.ArrayList;
import java.util.List;

public final class DataConverter {

    /**
     * Creates an entity for Datastore with properties of class Meal.
     * @param meal object of class Meal for which the entity is creating.
     * @return new Entity object with necessary properties.
     */
    public static Entity createMealEntity(Meal meal) {
        Entity mealEntity = new Entity("Meal");
        mealEntity.setProperty("id", meal.getId());
        mealEntity.setProperty("title", meal.getTitle());
        mealEntity.setProperty("description", meal.getDescription());
        mealEntity.setProperty("ingredients", meal.getIngredients());
        mealEntity.setProperty("type", meal.getType());

        return mealEntity;
    }

    public static List<Meal> getDataFromDatastore(PreparedQuery query) {
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
} 