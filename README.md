# Search-Engine

This is a search engine I built from scratch while taking a class in Software Development

**This project does the following:** 
- Processes command-line parameters to determine whether to use multithreading and if so, how many threads to use in the work queue
- Uses a single work queue with finish and shutdown features
- Displays a webpage with a text box where users may enter a multi-word search query and click a button that submits that query to a servlet in your search engine.
- Builds an inverted index from a seed URL instead of a directory using a web crawler.
- Converts all processed URLs to a consistent absolute form
- ... and much more

**In order to use it, one must have the following: **
- A directory the user wants to traverse through
- An argument String that tells the Driver to traverse through said directory
  - argument example: -html https://www.cs.usfca.edu/~cs212/ -server 8080 -max 100 -threads 3

*Disclaimer: Engine is titled 'KevSearch' per the request of my cousin 'Kevin'*
