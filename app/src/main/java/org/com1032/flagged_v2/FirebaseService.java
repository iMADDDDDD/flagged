package org.com1032.flagged_v2;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * Created by ImadEddine on 22/05/2016.
 */
public class FirebaseService extends IntentService {

    /** Defining global variables for storing into local database */
    private MarkersDB markersDatabase = null;
    private SQLiteDatabase db;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * While user has launched the application:
     *  --> This service is called
     *  --> It the remote database automatically invoke the .OnChildAdded method
     *  --> So basically, getting the data from the remote database is always
     *      done in the background
     */
    public FirebaseService() {
        super("FirebaseService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        /** Instantiating the database */
        markersDatabase = new MarkersDB(FirebaseService.this);
        db = markersDatabase.getReadableDatabase();

        Firebase.setAndroidContext(FirebaseService.this);
        Firebase fire = new Firebase("https://blistering-torch-6232.firebaseio.com/");

        fire.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                /** Getting the path of every value from firebase */
                String descPath = "/Description";
                String latPath = "/Latitude";
                String lonPath = "/Longitude";
                String ratePath = "/Rating";
                String typePath = "/Type";

                markersDatabase.insertData(dataSnapshot.child(typePath).getValue().toString(), dataSnapshot.getKey(),
                        Double.parseDouble(dataSnapshot.child(latPath).getValue().toString()), Double.parseDouble(dataSnapshot.child(lonPath).getValue().toString()),
                        dataSnapshot.child(ratePath).getValue().toString(), dataSnapshot.child(descPath).getValue().toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                /** If developer removes data manually from the remote database
                 *  --> Remove that value from the local database
                 */
                markersDatabase.deleteSpecificData(dataSnapshot.toString());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
}
