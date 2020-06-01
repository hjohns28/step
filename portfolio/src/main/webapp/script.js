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

function nextExperience() {
  const expContainer = document.getElementById('exp-container');
  const exps = [
    {title: 'WORK EXPERIENCE:', dets: ''}, 
    {title: 'STEP INTERN AT GOOGLE', dets: 'May 2020-Present'}, 
    {title: 'RESEARCH ASSISTANT AT LMU', dets: 'Sep 2019-Mar 2019'},
    {title: 'SUMMER INTERN AT ELECTRO SCAN INC.', dets: 'Jun 2018-Aug 2018'},
    {title: 'SUMMER LIFEGUARD AT YMCA', dets: 'May 2017-Jun 2017'},
    {title: 'EDUCATION:', dets: ''},
    {title: 'LOYOLA MARYMOUNT UNIVERSITY', dets: ' Pursuing Civil Engineering, Computer Science'},
    {title: 'SACRAMENTO COUNTRY DAY SCHOOL', dets: 'High School Diploma (2019)'}
  ]
  for(i = 0; i < exps.length; i++) {
    //see which experience slide is currently displayed, and display the succeeding one
    //substring used so that bold tags, <b>, not included in comparing the strings
    const pureCurrentTextString = expContainer.innerHTML.substring(3,12);
    const correspondingTitleString = exps[i].title.substring(0,9);
    if (pureCurrentTextString.localeCompare(correspondingTitleString) == 0) {
      if (i < exps.length-1) {
        expContainer.innerHTML = exps[i+1].title.bold() + "<br />" + exps[i+1].dets;
        break;
      } else {
        expContainer.innerHTML = exps[0].title.bold() + "<br />" + exps[0].dets;
        break;
      }
    }
  }
}

function previousExperience() {
  const expContainer = document.getElementById('exp-container');
  const exps = [
    {title: 'WORK EXPERIENCE:', dets: ''}, 
    {title: 'STEP INTERN AT GOOGLE', dets: 'May 2020-Present'}, 
    {title: 'RESEARCH ASSISTANT AT LMU', dets: 'Sep 2019-Mar 2019'},
    {title: 'SUMMER INTERN AT ELECTRO SCAN INC.', dets: 'Jun 2018-Aug 2018'},
    {title: 'SUMMER LIFEGUARD AT YMCA', dets: 'May 2017-Jun 2017'},
    {title: 'EDUCATION:', dets: ''},
    {title: 'LOYOLA MARYMOUNT UNIVERSITY', dets: ' Pursuing Civil Engineering, Computer Science'},
    {title: 'SACRAMENTO COUNTRY DAY SCHOOL', dets: 'High School Diploma (2019)'}
  ]
  for(i = 0; i < exps.length; i++) {
    //see which experience slide is currently displayed, and display the previous one
    //substring used so that bold tags, <b>, not included in comparing the strings
    const pureCurrentTextString = expContainer.innerHTML.substring(3,12);
    const correspondingTitleString = exps[i].title.substring(0,9);
    if (pureCurrentTextString.localeCompare(correspondingTitleString) == 0) {
      if (i != 0) {
        expContainer.innerHTML = exps[i-1].title.bold() + "<br />" + exps[i-1].dets;
        break;
      } else {
        expContainer.innerHTML = exps[exps.length-1].title.bold() + "<br />" + exps[exps.length-1].dets;
        break;
      }
    }
  }
}
