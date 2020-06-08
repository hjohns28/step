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

var commentCount = 0;

function storeNumberComments() {
  var comments = document.getElementById('number-comments');
  commentCount = comments.options[comments.selectedIndex].value;
  getServerComments();
}

function getServerComments() {
  fetch('/data?number-comments='+commentCount).then(response => response.json()).then((commentSection) => {
    if (commentSection.length < commentCount){
      displayTotalComments(commentSection);
      populateComments(commentSection);
    } else if (commentCount == 0) {
      const commentContainer = document.getElementById('comments-container');
      commentContainer.innerHTML = '';
    } else {
      populateComments(commentSection);
    }
  });
}

function displayTotalComments(commentSection) { 
  const commentContainer = document.getElementById('comments-container');
  commentContainer.innerHTML = '';
  const liTotalComments = document.createElement('li');
  liTotalComments.innerText = "(only " + commentSection.length + " comment(s))";
  liTotalComments.id = "totalCommentsLi";
  commentContainer.appendChild(liTotalComments);
}

function populateComments(commentSection) {
  const individualComments = document.getElementById('comments-container');
  commentSection.forEach((element) => {
    individualComments.appendChild(createComment(element));
  });
}

function createComment(element) {
  const liComment = document.createElement('li');
  liComment.innerText = element.key + ": " + element.value;
  liComment.className = "commentli";
  liComment.id = element.key + element.value;

  const deleteComment = createDeleteButton();
  liComment.appendChild(deleteComment);
  
  deleteComment.addEventListener("click", function() {
    deleteSingleComment(liComment.id);
  });
  
  const reactToComment = createReactionDropDown();
  liComment.appendChild(reactToComment);
  
  const reactionContainer = document.createElement('div');
  reactionContainer.className = "reactionContainer";
  liComment.appendChild(reactionContainer);
  
  reactToComment.addEventListener("change", function() {
    const reaction = createReactionElement(reactToComment.options[reactToComment.selectedIndex].innerHTML);
    reactionContainer.appendChild(reaction);
  });
  
  return liComment;
}

function createReactionElement(emoji) {
  const reaction = document.createElement('p');
  reaction.innerHTML = emoji;
  reaction.className = "reaction";
  return reaction;
}

function createDeleteButton(){
  const deleteComment = document.createElement('button');
  deleteComment.innerText = "X";
  deleteComment.className = "singleDelete";
  return deleteComment;
}

function createReactionDropDown(){
  const addReaction = document.createElement('select');
  addReaction.className = "reactionButton";
  addReaction.id = "reactionButton";
  
  const cover = document.createElement('option');
  cover.innerText = "!!";
  cover.disabled = true;
  cover.selected = true;
  
  const happy = document.createElement('option');
  happy.innerHTML = String.fromCodePoint(0x1F601);
  
  const sad = document.createElement('option');
  sad.innerHTML = String.fromCodePoint(0x1F622);
  
  addReaction.appendChild(cover);
  addReaction.appendChild(happy);
  addReaction.appendChild(sad);
  
  return addReaction;
}

function deleteSingleComment(id) {
  fetch('/delete-data?comment-id='+id, {method: 'POST'}).then(location.reload());
}

function deleteAllComments(){
  fetch('/delete-data?comment-id=all', {method: 'POST'}).then(location.reload());
}