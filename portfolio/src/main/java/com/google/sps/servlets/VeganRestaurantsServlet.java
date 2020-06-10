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

import com.google.gson.Gson;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Returns vegan restaurants data as a JSON object, e.g. {"1600 Fulton Ave": "Joe's Cafe", "1212 River Dr": "Domino's"}] */
@WebServlet("/vegan-restaurants")
public class VeganRestaurantsServlet extends HttpServlet {

  private LinkedHashMap<String, String> veganRestaurants = new LinkedHashMap<>();

  @Override
  public void init() {
    Scanner scanner = new Scanner(getServletContext().getResourceAsStream(
        "/WEB-INF/vegan-restaurants.csv"));
    
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] cells = line.split(",");

      String address = cells[0];
      String name = cells[1];

      veganRestaurants.put(address, name);
    }
    scanner.close();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException { 
    response.setContentType("application/json");
    Gson gson = new Gson();
    response.getWriter().println(gson.toJson(veganRestaurants));
  }
}