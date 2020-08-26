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
import static org.mockito.Mockito.*;

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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.io.*;
import javax.servlet.http.*;
import org.junit.Test;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.sps.servlets.MealServlet;
import com.google.sps.data.Meal;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetMealTest{
    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

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
    public void servletTest() throws IOException, ServletException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(createMealEntity(new Meal(
            0L, "Fried potato", "Fried potato with mushrooms and onion.",
                new ArrayList<>(Arrays.asList("potato", "onion", "mushrooms", "oil")), "Main")));
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getPathInfo()).thenReturn("meal/0");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        MealServlet servlet = new MealServlet();
        try {
            servlet.doGet(request, response);
        } catch(Exception e) {
        }
        
        assertEquals("application/json", response.getContentType());
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
}