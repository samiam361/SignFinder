# SignFinder
Senior Capstone Project

This repository contains the Android Studio files, mySQL database files, and php files pertaining to my senior capstone project titled
Sign Finder VA. The mySQL files are used in conjunction with an Amazon Web Services relational database. The php files are hosted on an Apache web server which is hosted by Amazon Web Services.

The php scripts are based on a php tutorial found at https://www.w3schools.com/php/php_mysql_connect.asp

# Install Instructions
This application was developed for Android version 8.0 on an LG G6 with 18:9 aspect ratio. It will work best on this device with this configuration.

To install the program, complete the following steps:
  1. Download and install Android Studio
  2. Download the zip file associated with this Git repository. Unzip the file and take note of where it is saved.
  3. In Android Studio, go to File->New->Import Project->Select the unzipped file you just downloaded.
  4. Connect your device to your computer. Run the application. This will install all of the necessary files to your device. The application will now exist on your device.

# Description of Main Java Files

LOGINACTIVITY
This activity starts on launch. When the user enters a username and password, the user may then click the Sign Up or Log In button. The sign up button checks if the username already exists in the database. If it does, a message tells the user the username is already being used. If it doesn't, an entry is made in the users table of the database with the user entered information. The log in button checks the entered username and password against the users table in the database. If the username and password combination is not found in the database, a message tells the user they have entered incorrect information. If the combination is found in the database, the MainActivity starts.

MAINACTIVITY
This activity has three bottom tabs: Nearby, Visited, and Search. On startup, the user's location is retrieved and the Nearby tab is displayed and the NearbyActivity is started. If the Visited tab is selected, the VisitActivity is started. If the Search tab is selected, the SearchActivity is started when the user enters search criteria. The MainActivity also has an options menu with the following items: Settings, About, Log Out. Settings starts the settings activty. About starts the about activity. Log Out starts the LoginActivity.

NEARBYACTIVITY
The AsyncTask gets the user's location and specified search radius and queries the database for signs within that radius. Converts the database result to a JSON string and parses this to a ListArray of Hashmaps containing strings for the signs' titles, locations, and text. This is then sent to a ListView and displayed in the main activity. The PHP associated with this activity also makes an entry of both sign data and username into the visits table of the database

VISITACTIVITY
This activity works the same way as the NearbyActivity. The difference is that the associated PHP queries the database for entries in the visits table that correspond with the user's username.

SEARCHACTIVITY
The user may search by city/county, district, or key word. Whichever is chosen by the user, the associated PHP will take the input and query the database. The rest of the activity works the same as the NearbyActivity and VisitActivity.

SETTINGSACTIVITY
This activity has three settings: notifications switch, search radius select list, and stay logged in tick box.

ABOUTACTIVITY
This activity only contains a text view that explains the purpose of the application.
