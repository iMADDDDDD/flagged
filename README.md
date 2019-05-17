# Flagged
Version 2.1.37
Theoritical storage taken: 
- 14.47 MB as Asus Memopad 7 shows.
- 14.42 MB as Samsung Galaxy A3 shows.	

## Main features

 - This application makes use of Google Play Services, more precisely
   Google Maps API.
 - Through the map, user is able to add markers as long as he fills
   details for the markers:
    Details include marker name, latitude, longitude, description, rating and type 			  of place.
 - User is able to amend details of markers.
 - User is able to locate himself as well through 2 providers: GPS and
   Network Provider.
 - Another main feature is the capacity of the application to compute
   the shortest path between many places depending on user's location

## Storing markers
There are basically 2 ways of storing markers and their details:

 - Local database
 - Remote database as known as Firebase

User, without knowing it, when adding a new marker, will add the latter on the remote database and the local database as well.
Only developer has access to the remote database and thus, managing markers' details.

## Application details

### Adding a marker
When the application has been launched, the map fragment is set on a click listener
User is able to add a marker and its details through an alert dialog box.
All this information will be stored then within both databases, and finally 
application displays the marker with the relevant information on the map.

### Updating a marker
The only way to have all markers visible on the map is to click on "Go To" button.
At this stage, user is able to click on a marker.
An alert dialog is shown with the relevant information and a menu:
 - Calculate distance: between user's location and marker's location
 - Change details: user is able to change the description and the rating of the marker only

### User's location
The application makes us of 2 location providers (GPS and NETWORK_PROVIDER).
Through the "Get your location" button, program checks whether one of the providers is enabled
Ultimately, it takes location from that provider.
However, user will be gently asked to switch on GPS since NETWORK provider is not precise and the application tends to not give the location of the user.

### Shortest path feature
Through the "Go To" button, user is able to check which place is the nearest from him:
for example, when choosing the type of the place (Hospital, Town, Museum...), the algorithm of the application calculates all distances between all possible hospitals to determine after which one is the closest one. A "path" is drawn after between user's location and place's location.

### Other secondary features
This application makes us of a service which consist of connecting to the remote database and retrieves all data there.
Basically, every time a data is added onto the remote database,
the latter calls an event-based method which is executed thus in the background.
The application will thus not freeze.

### Extra features
- Once installed for the first time, 2 full-screen activities will make their appearance when launching the application (you will discover it)

© iMAD. All rights reserved.
