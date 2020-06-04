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

/**
 * Adds a random fact to the page.
 */
function addRandomFact() {
  const facts =
    ['I can juggle', 'My arms are double-jointed', 'I did the triple jump in high school', 'My major is Civil Engineering',
    'I love! thai food', 'I go to Loyola Marymount University'];

  var imgs = new Array ("images/juggling.png", "images/arms.jpg", "images/triplejump2.png", "images/civil.png", "images/thai.png", "images/lmu.png");

  // Pick a random fact.
  const index = Math.floor(Math.random() * facts.length);
  const fact = facts[index];
  const image = imgs[index];
  

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;

  document.getElementById('img-container').src = image;
}

const experiences = [
  {title: 'WORK EXPERIENCE:', details: ''}, 
  {title: 'STEP INTERN AT GOOGLE', details: 'May 2020-Present'}, 
  {title: 'RESEARCH ASSISTANT AT LMU', details: 'Sep 2019-Mar 2019'},
  {title: 'SUMMER INTERN AT ELECTRO SCAN INC.', details: 'Jun 2018-Aug 2018'},
  {title: 'SUMMER LIFEGUARD AT YMCA', details: 'May 2017-Jun 2017'},
  {title: 'EDUCATION:', details: ''},
  {title: 'LOYOLA MARYMOUNT UNIVERSITY', details: ' Pursuing Civil Engineering, Computer Science'},
  {title: 'SACRAMENTO COUNTRY DAY SCHOOL', details: 'High School Diploma (2019)'}
];

var selectedExperience = 0;

function selectExperience(index) {
  const expContainer = document.getElementById('exp-container');
  expContainer.innerHTML = experiences[index].title.bold() + "<br />" + experiences[index].details;
}

function nextExperience() {
  selectExperience((selectedExperience+1) % experiences.length);
  selectedExperience++;
}

function previousExperience() {
  selectExperience((selectedExperience-1) % experiences.length);
  selectedExperience--;
}

function getNumCommentsSelected() {
  var comments = document.getElementById('number-comments');
  var numCommentsSelected = comments.options[comments.selectedIndex].value;
  getServerComments(numCommentsSelected);
}

function getServerComments(numCommentsSelected) {
  fetch('/data?number-comments='+numCommentsSelected).then(response => response.json()).then((commentSection) => {
    populateComments(commentSection);
  });
}

function populateComments(commentSection) {
  const individualComments = document.getElementById('comments-container');
  individualComments.innerHTML = '';
  commentSection.forEach((element) => {
      individualComments.appendChild(createComment(element));
  });
}

function createComment(text) {
  const liComment = document.createElement('li');
  liComment.innerText = text;
  return liComment;
}

function deleteComments() {
<<<<<<< HEAD
  fetch('/delete-data', {method: 'POST'}).then(getServerComments(0));
}
=======
    numCommentsSelected = 0;
    fetch('/delete-data', {method: 'POST'}).then(getServerComments());
}
>>>>>>> 5d48a2158af2d35ec4f6bfd9233f58220271592f
