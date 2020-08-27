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

package com.google.sps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.sps.servlets.MealServlet;
import com.google.sps.data.Meal;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import com.google.gson.Gson;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class MealServletTest{
    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private final Meal meal_0 = new Meal(
        1L, "Fried potato", "Fried potato with mushrooms and onion.",
            new ArrayList<>(Arrays.asList("potato", "onion", "mushrooms", "oil")), "Main");
    private final Meal meal_1 = new Meal(
        1L, "Vegetable soup", "Vegetable soup with onion.",
            new ArrayList<>(Arrays.asList("potato", "onion", "cabbage", "mushrooms", "water", "carrot", "pumpkin")), "Soup");
    private final Meal meal_2 = new Meal(
        2L, "Chocolate cake", "Chocolate cake with butter cream and strawberry.",
            new ArrayList<>(Arrays.asList("flour", "water", "butter", "cocoa powder", "chocolate", "sugar", "strawberry", "eggs")), "Dessert");
    private final Meal meal_3 = new Meal(
        3L, "", "", new ArrayList<>(), "");


    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void datastoreTest() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(new Entity("Meal"));
        ds.put(new Entity("Meal"));
        assertEquals(2, ds.prepare(new Query("Meal")).countEntities());
    }

    @Test
    public void getMealByIdTest() throws IOException, ServletException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(createMealEntity(meal_2));
        ds.put(createMealEntity(meal_1));
        
        MealServlet servlet = new MealServlet();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/meal/1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        try {
            servlet.doGet(request, response);
        } catch(Exception e) {
        }
        Gson gson = new Gson();
        String expected = gson.toJson(meal_2);
        String actual = response.getContentAsString().trim();

        assertEquals(expected, actual);
    }

    @Test
    public void getMealListTest() throws IOException, ServletException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(createMealEntity(meal_2));
        ds.put(createMealEntity(meal_1));
        
        MealServlet servlet = new MealServlet();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/meal");
        MockHttpServletResponse response = new MockHttpServletResponse();

        try {
            servlet.doGet(request, response);
        } catch(Exception e) {
        }
        HashMap<Long, Meal> meals = new HashMap();
        meals.put(0L, meal_1);
        meals.put(1L, meal_2);
        
        Gson gson = new Gson();
        String expected = gson.toJson(meals);
        String actual = response.getContentAsString().trim();
        assertEquals(expected, actual);
    }

    /**
     * create entity for Datastore with properties of class Meal
     * @param meal
     * @return
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