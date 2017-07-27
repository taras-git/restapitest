PROPERTY FINDER TEST


These test are based on Java, using Jsoup library  ( https://jsoup.org/ ).


Jsoup is a Java library for working with real-world HTML.
It provides a very convenient API for extracting and manipulating data,
using the best of DOM, CSS, and jquery-like methods.


With Jsoup it is possible to collect info from webpages without loading it to browser,
which saves a lot of time (API testing).



Test case "testPropertyFinderVilla3Beds7Beds"
(src/test/java/PropertyFinderTester.java)

This testcase:
 1. collects info about villas to buy 
    (using link https://www.propertyfinder.qa/search?l=&q=&c=1&t=35&pf=&pt=&bf=&bt=&af=&at=&kw=), 

    than using Jsoup API,
    it extracts info about address, number of bedrooms, and price.

 2. after with help of Java Streams, the info is filtered according next conditions:
     - location: THE PEARL
     - bedrooms: minimum 3BEDS and maximum 7BEDS
     - results are sorted in reverse order

 3. final results are save it in a csv file (villa.csv, in root), in format : listing title - price


Test case can be run from any Java IDE.
(please check clip csv_video.mov)
