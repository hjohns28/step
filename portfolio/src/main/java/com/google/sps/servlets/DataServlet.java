// Copyright 2019 Google LLC
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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map; 
import java.util.Set; 
import java.util.AbstractMap;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Map.Entry<String,String>> commentSection = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      String name = (String) entity.getProperty("name");
      String text = (String) entity.getProperty("text");
      Map.Entry<String,String> nameAndComment=new AbstractMap.SimpleEntry<>(name,text);
      commentSection.add(nameAndComment);
    }

    int numberComments = getNumberComments(request);
    
    List<Map.Entry<String,String>> limitedComments = new ArrayList<>();

    if (numberComments > commentSection.size()) {
      numberComments = commentSection.size();
    }

    for (int i = 0; i < numberComments; i++) {
      String name = commentSection.get(i).getKey();
      String text = commentSection.get(i).getValue();
      Map.Entry<String,String> nameAndComment=new AbstractMap.SimpleEntry<>(name,text);
      limitedComments.add(nameAndComment);
    }
    
    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(limitedComments));
  }

  private int getNumberComments(HttpServletRequest request) {
    String numberCommentsString = request.getParameter("number-comments");
    int numberComments = 0;
    try {
      numberComments = Integer.parseInt(numberCommentsString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + numberCommentsString);
      return -1;
    }
    return numberComments;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String userComment = request.getParameter("comment");
    String userName = request.getParameter("name");
    long timestamp = System.currentTimeMillis();

    String id = userName + userComment;

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name", userName);
    commentEntity.setProperty("text", userComment);
    commentEntity.setProperty("timestamp", timestamp);
    commentEntity.setProperty("id", id);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.sendRedirect("/index.html");
  }
}