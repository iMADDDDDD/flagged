package org.com1032.flagged_v2;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by ImadEddine on 27/04/2016.
 */
public class MarkersDB extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    /** Defining database name */
    private static final String DATABASE_NAME = "markersDB";

    /** Defining table name */
    private static final String TABLE_NAME = "markers";

    /** Defining column names of the local database */
    private static final String KEY_TYPE = "type";
    private static final String KEY_NAME = "name";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_REVIEW = "review";
    private static final String KEY_DESCRIPTION = "description";


    public MarkersDB(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }


    /** N.B: WILL NOT BE CALLED BECAUSE I DID NOT ADDED THE EXTENSION OF THE DATABASE */
    @Override
    public void onCreate(SQLiteDatabase dbMarkers) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " + KEY_TYPE + " TEXT, " + KEY_NAME
                + " TEXT, " + KEY_LATITUDE + " DOUBLE, " + KEY_LONGITUDE + " DOUBLE, " + KEY_REVIEW + " TEXT, " + KEY_DESCRIPTION + " TEXT " + ")";
        dbMarkers.execSQL(CREATE_TABLE);
    }

    /** Creates the table */
    public void createTable(SQLiteDatabase dbMarkers) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " + KEY_TYPE + " TEXT, " + KEY_NAME
                + " TEXT, " + KEY_LATITUDE + " DOUBLE, " + KEY_LONGITUDE + " DOUBLE, " + KEY_REVIEW + " TEXT, " + KEY_DESCRIPTION + " TEXT " + ")";
        dbMarkers.execSQL(CREATE_TABLE);
        Log.v("TABLE ", "CREATED");
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v("TABLE ", "UPGRADED");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /** Pre-inserting data just not to have an empty database */
    public void preInsertData() {
        SQLiteDatabase db = this.getWritableDatabase();

        String sql1 = "INSERT INTO " + TABLE_NAME + " (" + KEY_NAME + ", " + KEY_LATITUDE + ", "
                + KEY_LONGITUDE + ", " + KEY_DESCRIPTION + ", " + KEY_REVIEW + ", " + KEY_TYPE + ") VALUES " +
                "('Henri 1er', '50.808995', '4.344095', 'Very fancy and tasty restaurant', '8.8', 'Restaurant');";

        String sql2 = "INSERT INTO " + TABLE_NAME + " (" + KEY_NAME + ", " + KEY_LATITUDE + ", "
                + KEY_LONGITUDE + ", " + KEY_DESCRIPTION + ", " + KEY_REVIEW + ", " + KEY_TYPE + ") VALUES " +
                "('La Rucola', '50.805764', '4.357688', 'Excellent restaurant but expensive', '7.4', 'Restaurant');";

        String sql3 = "INSERT INTO " + TABLE_NAME + " (" + KEY_NAME + ", " + KEY_LATITUDE + ", "
                + KEY_LONGITUDE + ", " + KEY_DESCRIPTION + ", " + KEY_REVIEW + ", " + KEY_TYPE + ") VALUES " +
                "('Cote Sushi', '50.814225', '4.347174', 'Only for sushi lovers', '6.8', 'Restaunrant');";

        String sql4 = "INSERT INTO " + TABLE_NAME + " (" + KEY_NAME + ", " + KEY_LATITUDE + ", "
                + KEY_LONGITUDE + ", " + KEY_DESCRIPTION + ", " + KEY_REVIEW + ", " + KEY_TYPE + ") VALUES " +
                "('Brugmann Hotel', '50.830980', '4.356959', 'Sufficient for only one night', '6.2', 'Hotel');";

        String sql5 = "INSERT INTO " + TABLE_NAME + " (" + KEY_NAME + ", " + KEY_LATITUDE + ", "
                + KEY_LONGITUDE + ", " + KEY_DESCRIPTION + ", " + KEY_REVIEW + ", " + KEY_TYPE + ") VALUES " +
                "('Best Western Hotel', '50.803649', '4.342325', 'Very good hotel', '9.4', 'Hotel');";

        String sql6 = "INSERT INTO " + TABLE_NAME + " (" + KEY_NAME + ", " + KEY_LATITUDE + ", "
                + KEY_LONGITUDE + ", " + KEY_DESCRIPTION + ", " + KEY_REVIEW + ", " + KEY_TYPE + ") VALUES " +
                "('Edith Cavell Hospital', '50.813885', '4.356514', 'Everything is treated', '7.8', 'Hospital');";

        String sql7 = "INSERT INTO " + TABLE_NAME + " (" + KEY_NAME + ", " + KEY_LATITUDE + ", "
                + KEY_LONGITUDE + ", " + KEY_DESCRIPTION + ", " + KEY_REVIEW + ", " + KEY_TYPE + ") VALUES " +
                "('Moliere Hospital', '50.816102', '4.341697', 'Very expensive hospital', '6.0', 'Hospital');";

        String sql8 = "INSERT INTO " + TABLE_NAME + " (" + KEY_NAME + ", " + KEY_LATITUDE + ", "
                + KEY_LONGITUDE + ", " + KEY_DESCRIPTION + ", " + KEY_REVIEW + ", " + KEY_TYPE + ") VALUES " +
                "('Van Buuren Museum', '50.810163', '4.354055', 'Paints museum', '9.4', 'Museum');";

        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
        db.execSQL(sql4);
        db.execSQL(sql5);
        db.execSQL(sql6);
        db.execSQL(sql7);
        db.execSQL(sql8);
    }


    /** Clears the table */
    public void clearData() {
        Log.v("TABLE ", "DELETED");
        SQLiteDatabase dbMarkers = this.getWritableDatabase();
        String dropSQL = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        dbMarkers.execSQL(dropSQL);
    }

    /** Updates the marker into the database */
    public void updateMarker(String name, String description, String rating) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, name);
        values.put(KEY_DESCRIPTION, description);
        values.put(KEY_REVIEW, rating);

        String where = KEY_NAME + "= '" + name + "'";
        db.update(TABLE_NAME, values, where, null);
        db.close();

    }



    /** Inserts marker's details into the local database */
    public void insertData(String type, String name, double latitude, double longitude, String rating, String description) {

        SQLiteDatabase dbMarkers = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("type", type);
        values.put("name", name);
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        values.put("review", rating);
        values.put("description", description);
        dbMarkers.insert(TABLE_NAME, null, values);
        this.close();
    }

    /** Deleting a specific data from the local database
     *  --> WILL WORK ONLY IF DEVELOPER DELETES MANUALLY DATA FROM FIREBASE
     *  --> BUT STILL SOME BUGS
     * @param name
     */
    public void deleteSpecificData(String name) {
        SQLiteDatabase dbMarkers = this.getWritableDatabase();
        try {
            dbMarkers.delete(TABLE_NAME, "name = ?", new String[] {name});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbMarkers.close();
        }
    }

}
