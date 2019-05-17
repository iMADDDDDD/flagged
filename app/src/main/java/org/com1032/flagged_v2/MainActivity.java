package org.com1032.flagged_v2;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.location.LocationListener;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    /** Defining the map for the fragment */
    private GoogleMap map;

    /** Defining 2 buttons
     *  --> mLocateButton is used to locate the user
     *  --> mChooseWhere is used for the user to see where is the nearest place he wants to go
     */
    private Button mLocateButton, mChooseWhere;

    private LocationManager locationManager;

    /** Defining latitude and longitude used to determine user's location */
    private double latitudes, longitudes;

    /** Defining an intent service */
    private IntentService mIntentService;

    /** Defining a List where all distances to all hospitals are stored */
    private List<Double> distanceHospital = new ArrayList<>();

    /** Defining a List where all distances to all restaurants are stored */
    private List<Double> distanceRestaurant = new ArrayList<>();

    /** Defining a List where all distances to all museums are stored */
    private List<Double> distanceMuseum = new ArrayList<>();

    /** Defining a List where all distances to all towns are stored */
    private List<Double> distanceTown = new ArrayList<>();

    /** Defining a database which will store all markers and their details */
    private MarkersDB markersDatabase = null;
    private SQLiteDatabase db;

    /** Defining some global variables to hold markers details */
    private String markerType = null;
    private String markerName = null;
    private String markerDescription = null;
    private String markerRating = null;

    /** Defining an ArrayList of type LatLng which holds every marker's position */
    private List<LatLng> markersPos = new ArrayList<>();

    /** Defining views to design dialog boxes for the user */
    private View mView, mWhereView;

    /** As well as inflaters since some designs have been made in .xml files */
    private LayoutInflater mInflater, mWhereInflater;

    /** Defining radio buttons when user choose the type of the place */
    private RadioButton mHospitalButton, mTownButton, mRestaurantButton, mMuseumButton;

    /** Defining radio buttons when user choose the type of the place where to go */
    private RadioButton mWhereHospital, mWhereTown, mWhereRestaurant, mWhereMuseum;

    /** Grouping radio buttons */
    private RadioGroup mTypesButton;
    private RadioGroup mWhereTypesButton;

    /** Defining EditTexts when user decides to add a place */
    private EditText mAddMarkerName, mAddMarkerDesc, mAddMarkerRating;

    /** Defining Lists to hold database information */
    private List<String> markerNames = new ArrayList<>();
    private List<String> markerDescriptions = new ArrayList<>();
    private List<Double> markerLatitudes = new ArrayList<>();
    private List<Double> markerLongitudes = new ArrayList<>();
    private List<String> markerRatings = new ArrayList<>();
    private List<String> markerTypes = new ArrayList<>();

    /** Defining Lists to store details of marker if type of marker is a RESTAURANT */
    private List<String> markerNamesRestaurants = new ArrayList<>();
    private List<String> markerDescriptionsRestaurants = new ArrayList<>();
    private List<String> markerRatingsRestaurants = new ArrayList<>();
    private List<Double> markerLatitudesRestaurants = new ArrayList<>();
    private List<Double> markerLongitudesRestaurants = new ArrayList<>();
    private List<LatLng> markerRestaurantsPos = new ArrayList<>();

    /** Defining Lists to store details of marker if type of marker is a HOSPITAL */
    private List<String> markerNamesHospitals = new ArrayList<>();
    private List<String> markerDescriptionsHospitals = new ArrayList<>();
    private List<String> markerRatingsHospitals = new ArrayList<>();
    private List<Double> markerLatitudesHospitals = new ArrayList<>();
    private List<Double> markerLongitudesHospitals = new ArrayList<>();
    private List<LatLng> markerHospitalsPos = new ArrayList<>();

    /** Defining Lists to store details of marker if type of marker is a TOWN */
    private List<String> markerNamesTowns = new ArrayList<>();
    private List<String> markerDescriptionsTowns = new ArrayList<>();
    private List<String> markerRatingsTowns = new ArrayList<>();
    private List<Double> markerLatitudesTowns = new ArrayList<>();
    private List<Double> markerLongitudesTowns = new ArrayList<>();
    private List<LatLng> markerTownsPos = new ArrayList<>();

    /** Defining Lists to store details of marker if type of marker is a MUSEUM */
    private List<String> markerNamesMuseums = new ArrayList<>();
    private List<String> markerDescriptionsMuseums = new ArrayList<>();
    private List<String> markerRatingsMuseums = new ArrayList<>();
    private List<Double> markerLatitudesMuseums = new ArrayList<>();
    private List<Double> markerLongitudesMuseums = new ArrayList<>();
    private List<LatLng> markerMuseumsPos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Instantiating the database */
        markersDatabase = new MarkersDB(MainActivity.this);

        /** Making it readable */
        db = markersDatabase.getReadableDatabase();

        /** Clearing data */
        markersDatabase.clearData();

        /** Creating table again */
        /** Basically, it is made just to make sure that I have no duplicate data in my database */
        markersDatabase.createTable(db);

        /** Initializing map into my fragment */
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /** Inflating buttons when application is launched */
        mLocateButton = (Button) findViewById(R.id.button_locate);
        mChooseWhere = (Button) findViewById(R.id.button_choose_location);

        /** Just making the map as a global variable, thus to use it later */
        map = mapFragment.getMap();

        /** Some minimum data has been stored */
        markersDatabase.preInsertData();

        /** Starting a service which gets all data from my remote database */
        /** Just check FirebaseService.java to read my comments on this class */
        Intent intent = new Intent(this, FirebaseService.class);
        startService(intent);

        /** Setting the map on a click listener */
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng point) {

                /** If map is clicked, a new marker is added */
                /** An alert dialog is shown inviting user to add details about the place */
                AlertDialog.Builder newMarkerDetails = new AlertDialog.Builder(MainActivity.this);
                newMarkerDetails.setTitle(R.string.location_details);

                /** Setting the view of the alert dialog */
                mInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                mView = mInflater.inflate(R.layout.add_marker, null);

                /** Inflating the group of Radio buttons */
                mTypesButton = (RadioGroup) mView.findViewById(R.id.types_button);

                /** Inflating radio buttons */
                mRestaurantButton = (RadioButton) mView.findViewById(R.id.restaurant_button);
                mHospitalButton = (RadioButton) mView.findViewById(R.id.hospital_button);
                mTownButton = (RadioButton) mView.findViewById(R.id.town_button);
                mMuseumButton = (RadioButton) mView.findViewById(R.id.museum_button);

                /** Inflating EditTexts so the user can input his data */
                mAddMarkerName = (EditText) mView.findViewById(R.id.add_marker_name);
                mAddMarkerDesc = (EditText) mView.findViewById(R.id.add_marker_description);
                mAddMarkerRating = (EditText) mView.findViewById(R.id.add_marker_rating);

                /** Only to avoid multiple lines bug */
                mAddMarkerName.setInputType(InputType.TYPE_CLASS_TEXT);
                mAddMarkerDesc.setInputType(InputType.TYPE_CLASS_TEXT);
                mAddMarkerRating.setInputType(InputType.TYPE_CLASS_NUMBER);

                /** Setting the view to the alert dialog */
                newMarkerDetails.setView(mView);

                /** Showing the keyboard just for the ease of the user */
                InputMethodManager imm = (InputMethodManager)getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                /** Setting an "Add" button, to store information into local and remote database */
                newMarkerDetails.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        /** Getting what user has input */
                        markerName = mAddMarkerName.getText().toString();
                        markerDescription = mAddMarkerDesc.getText().toString();
                        markerRating = mAddMarkerRating.getText().toString();

                        markerLatitudes.add(point.latitude);

                        /** Getting what user has selected as a type of place */
                        int selectedType = mTypesButton.getCheckedRadioButtonId();
                        RadioButton whichButton = (RadioButton) mView.findViewById(selectedType);

                        /** Some validation code
                         *  Making sure nothing is null or if user has forgotten something
                         */
                        if (markerName.equals("") || markerName == null || markerDescription.equals("") || markerDescription == null
                                 || markerRating.equals("") || markerRating == null || selectedType == -1 || whichButton.getText().toString() == null) {
                            Toast.makeText(MainActivity.this, R.string.input_error, Toast.LENGTH_SHORT).show();
                        } else {
                            markerType = whichButton.getText().toString();

                            /** Connecting to the database */
                            Firebase fire = new Firebase("https://blistering-torch-6232.firebaseio.com/").child(markerName);

                            /** Inserting data into the remote database */
                            /** It is done through a HashMap */
                            Map<String, String> map2 = new HashMap<String, String>();
                            map2.put("Description", markerDescription);
                            map2.put("Latitude", String.valueOf(point.latitude));
                            map2.put("Longitude", String.valueOf(point.longitude));
                            map2.put("Rating", markerRating);
                            map2.put("Type", markerType);
                            fire.setValue(map2);

                            /** Inserting data into local database */
                            markersDatabase.insertData(markerType, markerName, point.latitude, point.longitude, markerRating, markerDescription);

                            /** Adding marker to the map with the input details */
                            MarkerOptions marker = new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title(markerName);
                            map.addMarker(marker);

                            /** Calling getData()
                             *  --> It iterates through the local database
                             *  --> And stores all information into arrayLists
                             *  --> Duplicates are removed
                             */
                            getData();
                        }


                    }
                });

                newMarkerDetails.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // DO NOTHING
                    }
                });

                /** Showing the alert dialog */
                newMarkerDetails.show();
            }
        });

    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {

        /** If map is ready, just update user's location */
        getLocation();

        /** Setting the location button on listener */
        mLocateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Gets the location of the user */
                getLocation();

                /** Waiting 3 seconds to get the user's location, just to be sure that isn't null */
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // At the end of 3 seconds, dismiss box and user is sent to his current position
                        if (latitudes != 0 && longitudes != 0) {
                            Toast.makeText(MainActivity.this, R.string.successful_location, Toast.LENGTH_SHORT).show();
                            map.clear();

                            /** Centers the map into user's location */
                            getMyPosition();

                        } else {
                            Toast.makeText(MainActivity.this, R.string.please_gps, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 3000);
            }

        });

        /** Setting the button on a listener */
        mChooseWhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder whereToGo = new AlertDialog.Builder(MainActivity.this);
                whereToGo.setTitle(R.string.where_to_go);

                /** Removing every single marker */
                map.clear();

                /** 'Updating' data into ArrayLists */
                getData();

                /** Initializing inflater */
                mWhereInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

                /** setting the view as the where_to_go.xml view */
                mWhereView = mWhereInflater.inflate(R.layout.where_to_go, null);

                /** Inflating the radio group buttons */
                mWhereTypesButton = (RadioGroup) mWhereView.findViewById(R.id.types_button_where);

                /** Inflating all radio buttons */
                mWhereHospital = (RadioButton) mWhereView.findViewById(R.id.hospital_button_where);
                mWhereRestaurant = (RadioButton) mWhereView.findViewById(R.id.restaurant_button_where);
                mWhereMuseum = (RadioButton) mWhereView.findViewById(R.id.museum_button_where);
                mWhereTown = (RadioButton) mWhereView.findViewById(R.id.town_button_where);

                /** Setting the view of the alert dialog as the where_to_go.xml file */
                whereToGo.setView(mWhereView);

                /** Centering the camera into user's location */
                getMyPosition();


                whereToGo.setPositiveButton("Go", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        /** Getting what user's has chosen */
                        int selectedWhereType = mWhereTypesButton.getCheckedRadioButtonId();
                        RadioButton whereButton = (RadioButton) mWhereView.findViewById(selectedWhereType);

                        /** Doing some verification (if user did not choose anything) */
                        if (selectedWhereType == -1 || whereButton.getText().toString() == null) {

                            Toast.makeText(MainActivity.this, R.string.valid_location, Toast.LENGTH_SHORT).show();

                        } else {
                            /** Checking the type of place chosen */
                            /** We will explain only for this type, since it will follow the same logic */
                            if (whereButton.getText().toString().equals("Hospital")) {

                                /** Getting all possible markers with type 'HOSPITAL' */
                                for (LatLng distance : markerHospitalsPos) {

                                    /** We calculate distances and add them into an ArrayList */
                                    distanceHospital.add(calculateDistance(distance.latitude, distance.longitude));
                                }

                                /** We sort the arrayList from the biggest to the smallest distance */
                                /** Could be done from the smallest to the biggest as well */
                                Collections.sort(distanceHospital, Collections.reverseOrder());

                                /** Going through all possible distances
                                 *  Calculating distance between user's location and every possible 'HOSPITAL' place
                                 */
                                for (LatLng pos : markerHospitalsPos) {

                                    /** We check which one is equal to the last value of the ArrayList storing 'HOSPITAL' distances
                                     *  Since the last one is the smallest one
                                     */
                                    if (calculateDistance(pos.latitude, pos.longitude) == (distanceHospital.get(distanceHospital.size() - 1))) {
                                        /** We get the index of that position of hospital */
                                        /** Very helpful since markers are unique by their latitudes and longitudes */
                                        int firstMarkerIndex = markerHospitalsPos.indexOf(pos);

                                        /** We add the marker into the map */
                                        map.addMarker(new MarkerOptions().position(pos).title(markerNamesHospitals.get(firstMarkerIndex)));

                                        /** Showing the 'path' from user's location to nearest HOSPITAL */
                                        map.addPolyline(new PolylineOptions().add(new LatLng(latitudes, longitudes), new LatLng(pos.latitude, pos.longitude)).width(5).color(Color.RED));

                                        /** Centering camera to the nearest HOSPITAL */
                                        CameraUpdate center = CameraUpdateFactory.newLatLng(pos);
                                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                                        map.moveCamera(center);
                                        map.animateCamera(zoom);

                                        Toast.makeText(MainActivity.this, distanceHospital.get(distanceHospital.size() - 1).toString() + " meters from you", Toast.LENGTH_LONG).show();

                                    } else {
                                        // Do nothing, possibly developer messed up here ;)
                                    }
                                }

                                /** Now we check for the restaurant, if user has chosen RESTAURANT */
                            } else if (whereButton.getText().toString().equals("Restaurant")) {
                                for (LatLng distance : markerRestaurantsPos) {
                                    distanceRestaurant.add(calculateDistance(distance.latitude, distance.longitude));
                                }
                                Collections.sort(distanceRestaurant, Collections.reverseOrder());
                                for (LatLng pos : markerRestaurantsPos) {
                                    if (calculateDistance(pos.latitude, pos.longitude) == distanceRestaurant.get(distanceRestaurant.size() - 1)) {
                                        int firstMarkerIndex = markerRestaurantsPos.indexOf(pos);
                                        map.addMarker(new MarkerOptions().position(pos).title(markerNamesRestaurants.get(firstMarkerIndex)));
                                        map.addPolyline(new PolylineOptions().add(new LatLng(latitudes, longitudes), new LatLng(pos.latitude, pos.longitude)).width(5).color(Color.RED));
                                        CameraUpdate center = CameraUpdateFactory.newLatLng(pos);
                                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                                        map.moveCamera(center);
                                        map.animateCamera(zoom);
                                        Toast.makeText(MainActivity.this, distanceRestaurant.get(distanceRestaurant.size() - 1).toString() + " meters from you", Toast.LENGTH_LONG).show();
                                    } else {

                                    }
                                }
                            } else if (whereButton.getText().toString().equals("Museum")) {
                                for (LatLng distance : markerMuseumsPos) {
                                    distanceMuseum.add(calculateDistance(distance.latitude, distance.longitude));
                                }
                                Collections.sort(distanceMuseum, Collections.reverseOrder());
                                for (LatLng pos : markerMuseumsPos) {
                                    if (calculateDistance(pos.latitude, pos.longitude) == distanceMuseum.get(distanceMuseum.size() - 1)) {
                                        int firstMarkerIndex = markerMuseumsPos.indexOf(pos);
                                        map.addMarker(new MarkerOptions().position(pos).title(markerNamesMuseums.get(firstMarkerIndex)));
                                        map.addPolyline(new PolylineOptions().add(new LatLng(latitudes, longitudes), new LatLng(pos.latitude, pos.longitude)).width(5).color(Color.RED));
                                        CameraUpdate center = CameraUpdateFactory.newLatLng(pos);
                                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                                        map.moveCamera(center);
                                        map.animateCamera(zoom);
                                        Toast.makeText(MainActivity.this, distanceMuseum.get(distanceMuseum.size() - 1).toString() + " meters from you", Toast.LENGTH_LONG).show();
                                    } else {

                                    }
                                }
                            } else {
                                for (LatLng distance : markerTownsPos) {
                                    distanceTown.add(calculateDistance(distance.latitude, distance.longitude));
                                }
                                Collections.sort(distanceTown, Collections.reverseOrder());
                                for (LatLng pos : markerTownsPos) {
                                    if (calculateDistance(pos.latitude, pos.longitude) == distanceTown.get(distanceTown.size() - 1)) {
                                        int firstMarkerIndex = markerTownsPos.indexOf(pos);
                                        map.addMarker(new MarkerOptions().position(pos).title(markerNamesTowns.get(firstMarkerIndex)));
                                        map.addPolyline(new PolylineOptions().add(new LatLng(latitudes, longitudes), new LatLng(pos.latitude, pos.longitude)).width(5).color(Color.RED));
                                        CameraUpdate center = CameraUpdateFactory.newLatLng(pos);
                                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                                        map.moveCamera(center);
                                        map.animateCamera(zoom);
                                        Toast.makeText(MainActivity.this, distanceTown.get(distanceTown.size() - 1).toString() + " meters from you", Toast.LENGTH_LONG).show();
                                    } else {

                                    }
                                }
                            }
                        }
                    }
                });

                /** Showing the Alert dialog */
                whereToGo.show();

            }
        });

        /** Setting marker on click listener */
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                /** We get the index of the latitude
                 *  EXAMPLE: markerNames.get(0) and markerLatitudes.get(0) with markerLongitudes(0)
                 *  are correlated
                 */
                int markerIndex = markerLatitudes.indexOf(marker.getPosition().latitude);

                /** If it is -1, then IT MUST BE user's current location marker */
                if (markerIndex == -1) {
                    Toast.makeText(MainActivity.this, R.string.your_position, Toast.LENGTH_SHORT).show();
                } else {

                    /** Otherwise, showing a new alert dialog */
                    AlertDialog.Builder markerInfo = new AlertDialog.Builder(MainActivity.this);

                    /** Inflating TextViews */
                    TextView marker_name = new TextView(MainActivity.this);
                    TextView marker_desc = new TextView(MainActivity.this);
                    TextView marker_rate = new TextView(MainActivity.this);

                    /** Only for design purpose */
                    TextView blank = new TextView(MainActivity.this);
                    TextView blank2 = new TextView(MainActivity.this);
                    blank.setText("");
                    blank2.setText("");

                    /** Designing the textViews */
                    marker_name.setGravity(Gravity.CENTER);
                    marker_name.setTextSize(25);
                    marker_name.setTypeface(marker_name.getTypeface(), Typeface.BOLD);

                    marker_desc.setGravity(Gravity.CENTER);
                    marker_desc.setTextSize(15);

                    marker_rate.setGravity(Gravity.CENTER);
                    marker_rate.setTextSize(15);

                    /** Setting the view */
                    View view = new View(MainActivity.this);
                    Context context = view.getContext();

                    /** Setting the layout programmatically */
                    LinearLayout layout = new LinearLayout(context);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setGravity(Gravity.CENTER);

                    /** Adding textViews to the layout */
                    layout.addView(marker_name);
                    layout.addView(blank);
                    layout.addView(marker_desc);
                    layout.addView(blank2);
                    layout.addView(marker_rate);

                    int markerIndex2 = markerLatitudes.indexOf(marker.getPosition().latitude);

                    if (markerIndex2 == -1) {
                        Toast.makeText(MainActivity.this, R.string.your_position, Toast.LENGTH_SHORT).show();
                    } else {

                        /** Displaying details of that previously added marker */
                        marker_name.setText(markerNames.get(markerIndex2));
                        marker_desc.setText("Description: " + markerDescriptions.get(markerIndex2));
                        marker_rate.setText("Rating: " + markerRatings.get(markerIndex2) + "/10");

                        /** Setting the view of the Alert dialog */
                        markerInfo.setView(layout);
                    }

                    /** Setting 2 buttons, one is calculate distance and the other one is change details */
                    markerInfo.setItems(new CharSequence[]{"Calculate distance", "Change details"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:

                                    /** Being sure that user's location isn't null */
                                    if (latitudes == 0 || longitudes == 0) {
                                        Toast.makeText(MainActivity.this, R.string.please_locate, Toast.LENGTH_SHORT).show();
                                    } else {
                                        /** Just make use of the distance calculator method */

                                        double myPosLatitude = latitudes;
                                        double myPosLongitude = longitudes;
                                        double markerLatitude = marker.getPosition().latitude;
                                        double markerLongitude = marker.getPosition().longitude;

                                        double l1 = Math.toRadians(myPosLatitude);
                                        double l2 = Math.toRadians(markerLatitude);
                                        double g1 = Math.toRadians(myPosLongitude);
                                        double g2 = Math.toRadians(markerLongitude);

                                        double distance = Math.acos(Math.sin(l1) * Math.sin(l2) + Math.cos(l1) * Math.cos(l2) * Math.cos(g1 - g2));
                                        if (distance < 0) {
                                            distance = distance + Math.PI;
                                        }

                                        double goodDistance = Math.round(distance * 6378100);

                                        if (goodDistance > 10000) {
                                            double goodDistanceKm = goodDistance / 1000;
                                            Toast.makeText(MainActivity.this, goodDistanceKm + " km from you", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(MainActivity.this, goodDistance + " meters from you", Toast.LENGTH_SHORT).show();

                                        }
                                    }

                                    break;

                                case 1:
                                    /** Setting a new Alert dialog which will allow to change details of the marker */
                                    AlertDialog.Builder amendDetails = new AlertDialog.Builder(MainActivity.this);

                                    /** Only for design purposes */
                                    TextView blank = new TextView(MainActivity.this);
                                    TextView blank2 = new TextView(MainActivity.this);

                                    /** Same logic as before */
                                    final int markerInd = markerLatitudes.indexOf(marker.getPosition().latitude);

                                    amendDetails.setTitle(markerNames.get(markerInd));

                                    /** Inflating EditText and displaying data of the marker */
                                    final EditText descriptionInput = new EditText(MainActivity.this);
                                    descriptionInput.setText(markerDescriptions.get(markerInd));
                                    descriptionInput.setInputType(InputType.TYPE_CLASS_TEXT);

                                    /** Same */
                                    final EditText ratingInput = new EditText(MainActivity.this);
                                    ratingInput.setText(markerRatings.get(markerInd));
                                    ratingInput.setInputType(InputType.TYPE_CLASS_TEXT);

                                    /** Initializing the view */
                                    View mapView = new View(MainActivity.this);
                                    Context context = mapView.getContext();

                                    /** Initializing the layout */
                                    LinearLayout layout = new LinearLayout(context);
                                    layout.setOrientation(LinearLayout.VERTICAL);

                                    /** Just designing the alert dialog */
                                    layout.addView(blank);
                                    layout.addView(descriptionInput);
                                    layout.addView(blank2);
                                    layout.addView(ratingInput);

                                    /** Setting the view of the Alert Dialog */
                                    amendDetails.setView(layout);

                                    amendDetails.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            /** Getting what user has changed (or not) */
                                            markerDescription = descriptionInput.getText().toString();
                                            markerRating = ratingInput.getText().toString();

                                            /** Replacing data into the arrayLists */
                                            markerDescriptions.set(markerInd, markerDescription);
                                            markerRatings.set(markerInd, markerRating);

                                            /** Updating the local database */
                                            markersDatabase.updateMarker(markerNames.get(markerInd), markerDescription, markerRating);

                                            Firebase fire = new Firebase("https://blistering-torch-6232.firebaseio.com/");

                                            /** Updating the remote database */
                                            fire.child(markerNames.get(markerInd)).child("Description").setValue(markerDescription);
                                            fire.child(markerNames.get(markerInd)).child("Rating").setValue(markerRating);

                                        }
                                    });

                                    /** Showing the Alert dialog */
                                    amendDetails.show();

                                    break;
                            }
                        }
                    });

                    markerInfo.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // DO NOTHING
                        }

                    });

                    /** Showing the Alert Dialog */
                    markerInfo.show();
                    return false;
                }

                return false;
            }
        });

        /** Removing VISUALLY all markers from the map */
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                map.clear();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /** Stopping the service when user exits the application */
        /** Makes an economy in term of resources used by the phone */
        /** Thus performance UP */
        Intent intent = new Intent(this, FirebaseService.class);
        stopService(intent);
    }


    @Override
    public void onLocationChanged(Location location) {
        /** If location has changed, just update it to the global variable
         *  latitudes and longitudes
         */
        getLocation();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Do nothing
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        /** If provider is UP, get location updates from that provider */
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }



    public void getData() {

        /** Initializing cursor */
        Cursor cursor = markersDatabase.getReadableDatabase().rawQuery("SELECT * FROM markers", null);
        cursor.moveToFirst();

        /** Clearing all Lists */
        /** To make sure no duplicates are inside */
        markerNames.clear();
        markerLatitudes.clear();
        markerLongitudes.clear();
        markerRatings.clear();
        markerDescriptions.clear();
        markerTypes.clear();


        if (cursor != null) {
            // Moving to the first element
            cursor.moveToFirst();
            startManagingCursor(cursor);
            for (int i = 0; i < cursor.getCount(); i++) {
                String type = cursor.getString(0);
                String name = cursor.getString(1);
                Double latitude = cursor.getDouble(2);
                Double longitude = cursor.getDouble(3);

                /** Storing to relevant ArrayLists depending on the type of the place */
                if (type.equals("Hospital")) {
                    markerHospitalsPos.add(new LatLng(latitude, longitude));
                } else if (type.equals("Restaurant")) {
                    markerRestaurantsPos.add(new LatLng(latitude, longitude));
                } else if (type.equals("Museum")) {
                    markerMuseumsPos.add(new LatLng(latitude, longitude));
                } else {
                    markerTownsPos.add(new LatLng(latitude, longitude));
                }

                markersPos.add(new LatLng(latitude, longitude));

                String rating = cursor.getString(4);
                String desc = cursor.getString(5);

                if (type.equals("Hospital")) {

                    markerNamesHospitals.add(name);
                    markerDescriptionsHospitals.add(desc);
                    markerRatingsHospitals.add(rating);
                    markerLatitudesHospitals.add(latitude);
                    markerLongitudesHospitals.add(longitude);

                } else if (type.equals("Restaurant")) {

                    markerNamesRestaurants.add(name);
                    markerDescriptionsRestaurants.add(desc);
                    markerRatingsRestaurants.add(rating);
                    markerLatitudesRestaurants.add(latitude);
                    markerLongitudesRestaurants.add(longitude);

                } else if (type.equals("Museum")) {

                    markerNamesMuseums.add(name);
                    markerDescriptionsMuseums.add(desc);
                    markerRatingsMuseums.add(rating);
                    markerLatitudesMuseums.add(latitude);
                    markerLongitudesMuseums.add(longitude);

                } else {

                    markerNamesTowns.add(name);
                    markerDescriptionsTowns.add(desc);
                    markerRatingsTowns.add(rating);
                    markerLatitudesTowns.add(latitude);
                    markerLongitudesTowns.add(longitude);

                }


                markerNames.add(name);
                markerLatitudes.add(latitude);
                markerLongitudes.add(longitude);
                markerRatings.add(rating);
                markerDescriptions.add(desc);

                cursor.moveToNext();

            }
        }


        stopManagingCursor(cursor);
        displayAllMarkers();

    }


    public void displayAllMarkers() {

        /** Simply displays all markers from local and remote database */
        for (int a = 0; a < markerNames.size(); a++) {
            MarkerOptions marker = new MarkerOptions().position(markersPos.get(a)).title(markerNames.get(a));
            map.addMarker(marker);
        }

    }

    /** Method which calculates distance between user's location and maker's location */
    public double calculateDistance(Double latitude, Double longitude) {

        double myPosLatitude = latitudes;
        double myPosLongitude = longitudes;
        double markerLatitude = latitude;
        double markerLongitude = longitude;

        double l1 = Math.toRadians(myPosLatitude);
        double l2 = Math.toRadians(markerLatitude);
        double g1 = Math.toRadians(myPosLongitude);
        double g2 = Math.toRadians(markerLongitude);

        double distance = Math.acos(Math.sin(l1) * Math.sin(l2) + Math.cos(l1) * Math.cos(l2) * Math.cos(g1 - g2));
        if (distance < 0) {
            distance = distance + Math.PI;
        }

        double goodDistance = Math.round(distance * 6378100);

        return goodDistance;

    }

    /** This method makes use of a custom marker
     *  and centers the camera to the user's location
     */
    public void getMyPosition() {
        LatLng myPos = new LatLng(latitudes, longitudes);
        map.addMarker(new MarkerOptions().position(myPos).title("Your current position").icon(BitmapDescriptorFactory.fromResource(R.drawable.myposition)));
        CameraPosition camera = new CameraPosition.Builder()
                .target(myPos)
                .zoom(11.0f)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(camera));

    }

    /** Method used to take the best provider to determine user's location */
    public Location getLocation() {
        Location location = null;
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            /** Taking location from NETWORK PROVIDER if enabled*/
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (locationManager != null) {
                        if (location != null) {
                            latitudes = location.getLatitude();
                            longitudes = location.getLongitude();
                        }
                    }
                }

            /** Taking location from GPS if enabled */
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, MainActivity.this);

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }

                        if (location != null) {
                            latitudes = location.getLatitude();
                            longitudes = location.getLongitude();
                        }
                    }
                }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

}

/** Congratulations for reading at least 900 lines of code!
 *  There are more in the different classes!
 *  Good luck ;)
 */

