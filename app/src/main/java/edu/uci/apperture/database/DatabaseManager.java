package edu.uci.apperture.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * SQL Lite Manager for storing text, image, sound, and color info for each chat user
 * <p/>
 * There are currently two tables
 * <p/>
 * User - Contains all the users/devices that the application has encountered
 * |userid|username|
 * <p/>
 * Chat - Flat layout for the questions that are encountered
 * |userid|date|color_id|sound_id|image|blob_text|
 * <p/>
 * <p/>
 * Created by Sonny on 4/15/2015.
 */
public class DatabaseManager {
    private static final String TAG = DatabaseManager.class.getSimpleName();
    private static final String DB_NAME = "apperture.db";
    private static final String DB_TABLE_CHAT = "CHAT";
    private static final String DATE = "date";
    private static final String SOUND = "sound_url";
    private static final String COLOR = "color_value";
    private static final String IMAGE = "image";
    private static final String QUESTION = "question";
    private static final String RESPONSE = "response";

    private static final String DB_TABLE_USERS = "USERS";
    private static final String USER_ID = "user_id";
    private static final String USER_NAME = "username";

    private final String[] userColumns = new String[]{USER_ID, USER_NAME};
    private final String[] chatColumns = new String[]{USER_ID, DATE, COLOR, SOUND, IMAGE, QUESTION, RESPONSE};

    private static final String DB_CREATE_USERS =
            "CREATE TABLE IF NOT EXISTS " + DB_TABLE_USERS + " (" +
                    USER_ID + " INTEGER PRIMARY KEY, " +
                    USER_NAME + " TEXT);";


    private static final String DB_CREATE_CHAT =
            "CREATE TABLE IF NOT EXISTS " + DB_TABLE_CHAT + " (" +
                    USER_ID + " TEXT PRIMARY KEY, " +
                    DATE + " INTEGER, " +
                    COLOR + " INTEGER, " +
                    SOUND + " TEXT, " +
                    IMAGE + " BLOB, " +
                    QUESTION + " TEXT, " +
                    RESPONSE + " TEXT);";


    private static final int DB_VERSION = 1;
    private final Context mContext;
    private DBHelper mHelper;
    private SQLiteDatabase db;

    public DatabaseManager(Context context) {
        mContext = context;
    }

    public void open() {
        mHelper = new DBHelper(mContext);
        db = mHelper.getWritableDatabase();
    }

    /**
     * Inserts the response of the user
     *
     * @param userId   - user that responded
     * @param date     - long value time
     * @param color
     * @param sound
     * @param image    - byte[] of the image
     * @param question
     * @param response
     * @return
     */
    public long insertRecord(int userId, long date, int color, String sound, byte[] image, String question, String response) {
        ContentValues cv = new ContentValues();
        cv.put(USER_ID, userId);
        cv.put(DATE, date);
        cv.put(COLOR, color);
        cv.put(IMAGE, image);
        cv.put(SOUND, sound);
        cv.put(QUESTION, question);
        cv.put(RESPONSE, response);
        return db.insert(DB_TABLE_CHAT, null, cv);
    }

    public long insertUser(int userId, String username) {
        ContentValues cv = new ContentValues();
        cv.put(USER_ID, userId);
        cv.put(USER_NAME, username);
        return db.insert(DB_TABLE_USERS, null, cv);
    }

    public ArrayList<Record> getRecords(int userId) {
        ArrayList<Record> results = new ArrayList<Record>();
        Cursor cursorQuery = db.query(DB_TABLE_USERS, chatColumns, USER_ID + " = " + userId, null, null, null, null);
        for (cursorQuery.moveToFirst(); !cursorQuery.isAfterLast(); cursorQuery
                .moveToNext()) {
            results.add(parseRecordData(cursorQuery));
        }
        return results;
    }

    public ArrayList<User> getUsers() {
        ArrayList<User> results = new ArrayList<User>();
        Cursor cursorQuery = db.query(DB_TABLE_USERS, userColumns, null, null, null, null, null);
        for (cursorQuery.moveToFirst(); !cursorQuery.isAfterLast(); cursorQuery
                .moveToNext()) {
            results.add(parseUserData(cursorQuery));
        }
        return results;
    }


    public User getUser(int id) {
        Cursor query = db.query(DB_TABLE_USERS, userColumns, USER_ID + " = " + id, null, null, null, null);
        query.moveToFirst();
        return parseUserData(query);
    }

    public void close() {
        mHelper.close();
        db.close();
    }

    /**
     * Converts the image file or text file into byte[] to be stored into the database
     *
     * @param filePath
     * @return
     */
    public static byte[] convertImageData(String filePath) {
        byte[] byteData = null;
        try {
            FileInputStream instream = new FileInputStream(filePath);
            BufferedInputStream bif = new BufferedInputStream(instream);
            byteData = new byte[bif.available()];
            bif.read(byteData);
        } catch (IOException e) {
            Log.w(TAG, e);
        }
        return byteData;
    }

    private Record parseRecordData(Cursor cursor) {
        int userId = cursor.getColumnIndex(USER_ID);
        int date = cursor.getColumnIndex(DATE);
        int color = cursor.getColumnIndex(COLOR);
        int imageUrl = cursor.getColumnIndex(IMAGE);
        int question = cursor.getColumnIndex(QUESTION);
        int response = cursor.getColumnIndex(RESPONSE);
        return new Record.Builder()
                .setUserId(cursor.getInt(userId))
                .setDate(cursor.getLong(date))
                .setImage(cursor.getBlob(imageUrl))
                .setColor(cursor.getInt(color))
                .setQuestion(cursor.getString(question))
                .setResponse(cursor.getString(response))
                .createRecord();
    }

    private User parseUserData(Cursor cursor) {
        int userId = cursor.getColumnIndex(USER_ID);
        int userName = cursor.getColumnIndex(USER_NAME);
        return new User(cursor.getInt(userId), cursor.getString(userName));
    }


    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(DB_CREATE_CHAT);
            sqLiteDatabase.execSQL(DB_CREATE_USERS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
            Log.i(TAG, "Dropping Database ");
            db.execSQL("DROP DATABASE IF EXISTS " + DB_NAME);
            onCreate(sqLiteDatabase);
        }
    }
}
