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
    private static final String GAME = "game_log";
    private static final String PLAYER_1 = "player_1";
    private static final String PLAYER_2 = "player_2";

    private static final String DB_TABLE_USERS = "USERS";

    private final String[] chatColumns = new String[]{DATE, SOUND, GAME, PLAYER_1, PLAYER_2};


    private static final String DB_CREATE_CHAT =
            "CREATE TABLE IF NOT EXISTS " + DB_TABLE_CHAT + " (" +
                    DATE + " INTEGER PRIMARY KEY, " +
                    SOUND + " TEXT, " +
                    GAME + " BLOB, " +
                    PLAYER_1 + " TEXT, " +
                    PLAYER_2 + " TEXT);";


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
     * @param date     - long value time
     * @param sound
     * @param gameData    - byte[] of the image
     * @param player1
     * @param player2
     * @return
     */
    public long insertRecord(long date, String sound, byte[] gameData, String player1, String player2) {
        ContentValues cv = new ContentValues();
        cv.put(DATE, date);
        cv.put(GAME, gameData);
        cv.put(SOUND, sound);
        cv.put(PLAYER_1, player1);
        cv.put(PLAYER_2, player2);
        return db.insert(DB_TABLE_CHAT, null, cv);
    }


    public ArrayList<Record> getRecords() {
        ArrayList<Record> results = new ArrayList<Record>();
        Cursor cursorQuery = db.query(DB_TABLE_USERS, chatColumns, null, null, null, null, null);
        for (cursorQuery.moveToFirst(); !cursorQuery.isAfterLast(); cursorQuery
                .moveToNext()) {
            results.add(parseRecordData(cursorQuery));
        }
        return results;
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
        int date = cursor.getColumnIndex(DATE);
        int imageUrl = cursor.getColumnIndex(GAME);
        int sound = cursor.getColumnIndex(SOUND);
        int question = cursor.getColumnIndex(PLAYER_1);
        int response = cursor.getColumnIndex(PLAYER_2);
        return new Record.Builder()
                .setDate(cursor.getLong(date))
                .setSound(cursor.getString(sound))
                .setImage(cursor.getBlob(imageUrl))
                .setQuestion(cursor.getString(question))
                .setResponse(cursor.getString(response))
                .createRecord();
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(DB_CREATE_CHAT);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
            Log.i(TAG, "Dropping Database ");
            db.execSQL("DROP DATABASE IF EXISTS " + DB_NAME);
            onCreate(sqLiteDatabase);
        }
    }
}
