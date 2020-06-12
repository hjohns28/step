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

package com.google.sps.listener;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;  
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class IdListener implements ServletContextListener {

  static final String NEXT_AVAILABLE_ID_ATTRIBUTE = "nextAvailableCommentId";

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    
    ServletContext sc = sce.getServletContext();
	
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
    
    String id = Integer.toString(idNum);
    sc.setAttribute(NEXT_AVAILABLE_ID_ATTRIBUTE, id);
  }

  @Override
	public void contextDestroyed(ServletContextEvent sce) {
	  ServletContext sc = sce.getServletContext();
	  sc.removeAttribute(NEXT_AVAILABLE_ID_ATTRIBUTE);
	}
}

