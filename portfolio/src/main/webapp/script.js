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
  const facts = [
    {text: 'I can juggle', imageSrc: 'images/juggling.png'}, {text: 'I know how to unicycle', imageSrc: 'images/unicycle.png'}, 
    {text: 'I did the triple jump in high school', imageSrc: 'images/triplejump.png'}, 
    {text: 'My major is Civil Engineering', imageSrc: 'images/civil.png'}];

  // Pick a random fact.
  const index = Math.floor(Math.random() * facts.length);
  const fact = facts[index].text;
  const image = imgs[index].imageSrc;
  

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;

  document.getElementById('img-container').src = image;
}
