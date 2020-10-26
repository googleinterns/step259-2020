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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.google.sps.data.Meal;
import com.google.sps.data.DataConverter;
import com.google.sps.servlets.MealServlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class MealServletTest{
    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private static final Meal MEAL_EMPTY = new Meal(
        0L, "", "", new ArrayList<>(), "");
    private static final Meal MEAL_1 = new Meal(
        1L, "Fried potato", "Fried potato with mushrooms and onion.",
            new ArrayList<>(Arrays.asList("potato", "onion", "oil")), "Main");
    private static final Meal MEAL_1_DUPLICATE = new Meal(
        1L, "Vegetable soup", "Vegetable soup with onion.",
            new ArrayList<>(Arrays.asList("potato", "onion")), "Soup");
    private static final Meal MEAL_2 = new Meal(
        2L, "Chocolate cake", "Chocolate cake with butter cream and strawberry.",
            new ArrayList<>(Arrays.asList("flour", "water", "butter", "strawberry")), "Dessert");
    private static final Meal PIZZA_1 = new Meal(
        3L, "Italian pizza", "Pizza with pineaple, sausage and tomato.",
            new ArrayList<>(Arrays.asList("flour", "water", "sausage", "tomato", "pineaple", "cheese")), "Pizza");
    private static final Meal PIZZA_2 = new Meal(
        4L, "Seafood Pizza", "Just from the bottom of the ocean...",
            new ArrayList<>(Arrays.asList("flour", "salt", "butter", "water", "oil", "shrimp", "tuna", "onion", "ctabstick",
                "scallops", "fish sticks", "garlic", "tomato sauce")), "Pizza");


    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    // Get one of the existing objects Meal by id.
    // Expected result: JSON String with one object Meal.
    @Test
    public void getMealByIdTest() throws IOException, ServletException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(DataConverter.createMealEntity(MEAL_1));
        ds.put(DataConverter.createMealEntity(MEAL_2));
        
        MealServlet servlet = new MealServlet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setPathInfo("/2");
        servlet.doGet(request, response);
        Gson gson = new Gson();
        String expected = gson.toJson(MEAL_2);
        String actual = response.getContentAsString().trim();

        assertEquals(expected, actual);
    }

    // Get an object from empty datastore.
    // Expected result: Response Status NOT FOUND.
    @Test
    public void getMealByIdFromEmptyDsTest() throws IOException, ServletException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        
        MealServlet servlet = new MealServlet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setPathInfo("/2");
        servlet.doGet(request, response);

        int expected = HttpServletResponse.SC_NOT_FOUND;
        int actual = response.getStatus();
        assertEquals(expected, actual);
    }

    // From datastore, where two entities with same id exist, get an object by this id.
    // Expected result: Response Status INTERNAL SERVER ERROR.
    @Test
    public void getMealByIdForMultipleEntitiesTest() throws IOException, ServletException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(DataConverter.createMealEntity(MEAL_1));
        ds.put(DataConverter.createMealEntity(MEAL_1_DUPLICATE));
        
        MealServlet servlet = new MealServlet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setPathInfo("/1");
        servlet.doGet(request, response);

        int expected = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        int actual = response.getStatus();
        assertEquals(expected, actual);
    }

    // Get a Meal object from datastore, that can't be created (invalid field value).
    // Expected result: Response Status SC_NOT_FOUND.
    @Test
    public void getEmptyMealByIdTest() throws IOException, ServletException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(DataConverter.createMealEntity(MEAL_EMPTY));
        ds.put(DataConverter.createMealEntity(MEAL_1));

        MealServlet servlet = new MealServlet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setPathInfo("/0");
        servlet.doGet(request, response);

        int expected = HttpServletResponse.SC_NOT_FOUND;
        int actual = response.getStatus();
        assertEquals(expected, actual);
    }

    // Get a full list of objects Meal from datastore.
    // Expected result: a JSON String list with two Meal objects.
    @Test
    public void getMealListTest() throws IOException, ServletException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(DataConverter.createMealEntity(MEAL_1));
        ds.put(DataConverter.createMealEntity(MEAL_2));
        
        MealServlet servlet = new MealServlet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doGet(request, response);

        List<Meal> meals = new ArrayList<>();
        meals.add(MEAL_1);
        meals.add(MEAL_2);
        Gson gson = new Gson();
        String expected = gson.toJson(meals);
        String actual = response.getContentAsString().trim();
        assertEquals(expected, actual);
    }

    // Get a list of objects Meal from datastore by search request "potato onion".
    // (Check if meal that has both occurrences of searching keywords would be added only once).
    // Expected result: a JSON String of list with objects in order: MEAL_1, PIZZA_2.
    @Test
    public void getMealListWithTwoKeyWordsTest() throws IOException, ServletException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(DataConverter.createMealEntity(PIZZA_2));
        ds.put(DataConverter.createMealEntity(MEAL_1));
        ds.put(DataConverter.createMealEntity(MEAL_2));
        
        MealServlet servlet = new MealServlet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setParameter("query", "potato onion");
        servlet.doGet(request, response);

        List<Meal> meals = new ArrayList<>();
        meals.add(MEAL_1);
        meals.add(PIZZA_2);
        Gson gson = new Gson();
        String expected = gson.toJson(meals);
        String actual = response.getContentAsString().trim();
        assertEquals(expected, actual);
    }

    // Get a list of objects Meal from datastore by search request "meat".
    // Expected result: a JSON String of empty list.
    @Test
    public void getMealListWithEmptyResultTest() throws IOException, ServletException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(DataConverter.createMealEntity(MEAL_1));
        ds.put(DataConverter.createMealEntity(MEAL_2));
        
        MealServlet servlet = new MealServlet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setParameter("query", "meat");
        servlet.doGet(request, response);

        List<Meal> meals = new ArrayList<>();
        Gson gson = new Gson();
        String expected = gson.toJson(meals);
        String actual = response.getContentAsString().trim();
        assertEquals(expected, actual);
    }

    // Request with invalid pathInfo ""meal/1/"
    // Expected result: Response Status BAD REQUEST
    @Test
    public void getInvalidPathInfoTest() throws IOException, ServletException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(DataConverter.createMealEntity(MEAL_1));

        MealServlet servlet = new MealServlet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setPathInfo("/1/");
        servlet.doGet(request, response);

        int expected = HttpServletResponse.SC_BAD_REQUEST;
        int actual = response.getStatus();
        assertEquals(expected, actual);
    }

    // Get a similar meal from Datastore
    // Expected result: Long in JSON format with id of second meal.
    @Test
    public void noSameTypeTest() throws IOException, ServletException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(DataConverter.createMealEntity(MEAL_1));
        ds.put(DataConverter.createMealEntity(MEAL_2));
        
        MealServlet servlet = new MealServlet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setPathInfo("/similar");
        request.addParameter("id", MEAL_1.getId().toString());
        servlet.doGet(request, response);

        // returnIdOfSimilar() should not return id of the current meal page
        Long similarId = MEAL_2.getId();
        Gson gson = new Gson();
        String expected = gson.toJson(similarId);
        String actual = response.getContentAsString();
        assertEquals(expected, actual);
    }

    // Get a similar meal from Datastore
    // Expected result: Long in JSON format with id of second pizza.
    @Test
    public void returnSameTypeTest() throws IOException, ServletException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(DataConverter.createMealEntity(PIZZA_1));
        ds.put(DataConverter.createMealEntity(PIZZA_2));
        ds.put(DataConverter.createMealEntity(MEAL_1));
        
        MealServlet servlet = new MealServlet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setPathInfo("/similar");
        request.addParameter("id", PIZZA_1.getId().toString());
        servlet.doGet(request, response);

        // returnIdOfSimilar() should  return id of the same category meel
        Long similarId = PIZZA_2.getId();
        Gson gson = new Gson();
        String expected = gson.toJson(similarId);
        String actual = response.getContentAsString();
        assertEquals(expected, actual);
    }

    // Get a similar meal from Datastore
    // Expected result: Long in JSON format with id of same meal.
    @Test
    public void onlyOneEntity() throws IOException, ServletException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(DataConverter.createMealEntity(MEAL_1));
        
        MealServlet servlet = new MealServlet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setPathInfo("/similar");
        request.addParameter("id", MEAL_1.getId().toString());
        servlet.doGet(request, response);

        // returnIdOfSimilar() should return id of the current meal page if there are no other meals
        Long similarId = MEAL_1.getId();
        Gson gson = new Gson();
        String expected = gson.toJson(similarId);
        String actual = response.getContentAsString();
        assertEquals(expected, actual);
    }
}