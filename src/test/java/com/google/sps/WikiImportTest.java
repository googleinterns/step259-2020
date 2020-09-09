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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.google.sps.data.Meal;
import com.google.sps.data.DataConverter;
import com.google.sps.wikiimport.WikiImportServlet;
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

public class WikiImportTest{
    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private static final Meal MEAL = new Meal(
        0L, "Breakfast Burrito", "Breakfast Burrito", new ArrayList<>(Arrays.asList("egg")), "");
    private static final String VALID_JSON = new Gson().toJson(MEAL);
    private static final String INVALID_JSON = VALID_JSON.replace("}", "");
    

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    // Put one object Meal to empty Datastore, created from valid JSON String.
    // Expected result: Datastore has one entity for this object Meal with id = 0L.
    @Test
    public void putValidObjectTest() throws IOException, ServletException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        
        WikiImportServlet servlet = new WikiImportServlet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setContent(VALID_JSON.getBytes());
        servlet.doPost(request, response);
        Long id = 0L;
        Filter propertyFilter = new FilterPredicate("id", FilterOperator.EQUAL, id);
        Query query = new Query("Meal").setFilter(propertyFilter);
        PreparedQuery results = ds.prepare(query);
        List<Meal> meals = DataConverter.getDataFromDatastore(results);
        String expected = MEAL.getTitle();
        String actual = meals.get(0).getTitle();

        assertEquals(expected, actual);
    }

    // Put one object Meal to empty Datastore, created from invalid JSON String.
    // Expected result: Response Status BAD REQUEST.
    @Test
    public void putInvalidObjectTest() throws IOException, ServletException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        
        WikiImportServlet servlet = new WikiImportServlet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setContent(INVALID_JSON.getBytes());
        servlet.doPost(request, response);
        int expected = HttpServletResponse.SC_BAD_REQUEST;
        int actual = response.getStatus();

        assertEquals(expected, actual);
    }
}