# MachineLearning

This repository was used in the Text Classification using Machine Learning session at Lancaster Summer Schools in Corpus Linguistics and other Digital methods (#LancsSS16)
at Lancaster University, UK – 12th to 15th July 2016. 
http://ucrel.lancs.ac.uk/summerschool/nlp.php


Presented by Dr. Mahmoud El-Haj
http://www.lancaster.ac.uk/staff/elhaj

Slides are avialable online:

Course:
https://lancaster.box.com/s/qzvw9268mkn3b5o655u9cg8gzll4d7x9

Workspace Setup:
https://lancaster.box.com/s/rqr0oskvsk6fs29qj91ld9ncnpw46gs7

The code trains classifiers for chairman's statements, governance & remuneration sections from 1,000 annual financial reports.
Using WEKA Java the code does the following:
- Creates an ARFF File
- Train a model using different Algorithms 
- Extract n-gram features using stringToWordsVector
- Reduce features
- Classify unseen documents using the created models.
