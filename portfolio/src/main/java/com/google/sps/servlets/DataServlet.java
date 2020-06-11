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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

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

      String nickname = (String) entity.getProperty("nickname");
      String email = (String) entity.getProperty("email");
      String text = (String) entity.getProperty("text");
      String id = (String) entity.getProperty("id");

      commentSection.add(new Comment(nickname, email, text, id));
    }
    
    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(commentSection));

  }

  public class Comment {
    private String nickname;
    private String email;
    private String text;
    private String id;

    public Comment(String nickname, String email, String text, String id) {
      this.nickname = nickname;
      this.email = email;
      this.text = text;
      this.id = id;
    }
    public String getNickname(){
      return this.nickname;
    }
    public String getEmail(){
      return this.email;
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

  public int getIdNum() {
    int idNum = 0;
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      String idString = (String) entity.getProperty("id");
      int id = Integer.parseInt(idString);
      if (id > idNum) {
        idNum = id;
      }
    }
    idNum++;
    return idNum;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    UserService userService = UserServiceFactory.getUserService();
    String email = userService.getCurrentUser().getEmail();
    String nickname = getUserNickname(userService.getCurrentUser().getUserId());
    
    if (nickname == null) {
      String url = "/nickname.html";
      response.sendRedirect(url);
      return;
    }

    int idNum = getIdNum();
    String userComment = request.getParameter("comment");
    long timestamp = System.currentTimeMillis();

    String id = Integer.toString(idNum);

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("nickname", nickname);
    commentEntity.setProperty("text", userComment);
    commentEntity.setProperty("timestamp", timestamp);
    commentEntity.setProperty("id", id);
    commentEntity.setProperty("email", email);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.sendRedirect("/index.html");
  }

  /** Returns the nickname of the user with id, or null if the user has not set a nickname. */
  private String getUserNickname(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
        new Query("UserInfo")
            .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      return null;
    }
    String nickname = (String) entity.getProperty("nickname");
    return nickname;
  }
}
