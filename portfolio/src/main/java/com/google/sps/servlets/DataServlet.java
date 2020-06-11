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
import javax.servlet.ServletContext;

@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    int commentCount = getRequestedCommentCount(request);

    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    
    int readCommentCount = 0;
    List<Comment> commentSection = new ArrayList<Comment>();

    for (Entity entity : results.asIterable()) {
      if (readCommentCount >= commentCount) {
        break;
      }
      readCommentCount++;
      String name = (String) entity.getProperty("name");
      String text = (String) entity.getProperty("text");
      String id = (String) entity.getProperty("id");
      commentSection.add(new Comment(name, text, id));

    }
    
    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(commentSection));

  }

  public class Comment {
    private String name;
    private String text;
    private String id;

    public Comment(String name, String text, String id) {
      this.name = name;
      this.text = text;
      this.id = id;
    }
    public String getName(){
      return this.name;
    }
    public String getText(){
      return this.text;
    }
    public String getId(){
      return this.id;
    }
  }

  private int getRequestedCommentCount(HttpServletRequest request) {
    String numberOfCommentsString = request.getParameter("number-comments");
    int commentCount = 0;
    try {
      commentCount = Integer.parseInt(numberOfCommentsString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + numberOfCommentsString);
      return -1;
    }
    return commentCount;
  }

  private int idNum = -1;

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String userComment = request.getParameter("comment");
    String userName = request.getParameter("name");
    long timestamp = System.currentTimeMillis();

    if (idNum == -1) {
      ServletContext sc = request.getServletContext();
	  String id = sc.getAttribute("id").toString();
      idNum = Integer.parseInt(id);
    }
    
    idNum++;
    String id = Integer.toString(idNum);

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
