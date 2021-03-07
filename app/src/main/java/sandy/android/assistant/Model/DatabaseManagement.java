//SQLiteOpenHelper Database Access Class. This Class handles business logic, provides connection to SQLite internal DB and
// requests queries from SQLite Database

package sandy.android.assistant.Model;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;


public class DatabaseManagement extends SQLiteOpenHelper {      //DatabaseManagement class that inherits SQLiteOpenHelper Functions

    public static final String DATABASE_NAME = "san.db";      //defining of note attributes that will be used while fetching and storing note data to database.

    public static final String NOTES_TABLE_NAME = "notes";
    public static final String NOTES_COLUMN_ID = "id";
    public static final String NOTES_COLUMN_TITLE = "title";
    public static final String NOTES_COLUMN_CONTENT = "content";
    public static final String NOTES_COLUMN_SAVEDATE = "savedate";
    public static final String NOTES_COLUMN_NOTIFICATION_ID = "notification_id";
    public static final String NOTES_COLUMN_NOTEBOOK_ID = "notebook_id";

    public static final String NOTIFICATIONS_TABLE_NAME = "notifications"; //defining of notification attributes that will be used while fetching and storing notification data to database.
    public static final String NOTIFICATIONS_COLUMN_ID = "id";
    public static final String NOTIFICATIONS_COLUMN_DATE = "date";

    public static final String NOTEBOOKS_TABLE_NAME = "notebooks";  //defining of notebook attributes that will be used while fetching and storing notebook data to database.
    public static final String NOTEBOOKS_COLUMN_ID = "id";
    public static final String NOTEBOOKS_COLUMN_TITLE = "title";

    public DatabaseManagement(Context context) {        //DatabaseManagement constructor method
        super(context, DATABASE_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {       //SQLiteOpenHelper class-dependent method to create database tables
        String fk = "PRAGMA foreign_keys = 1";
        db.execSQL(fk);


        String notifications_sql = "create table " + NOTIFICATIONS_TABLE_NAME +
                " (" + NOTIFICATIONS_COLUMN_ID + " integer primary key AUTOINCREMENT, " +
                NOTIFICATIONS_COLUMN_DATE + " text" + ")";
        db.execSQL(notifications_sql);

        String notes_sql = "create table " + NOTES_TABLE_NAME +
                " (" + NOTES_COLUMN_ID + " integer primary key AUTOINCREMENT, " +
                NOTES_COLUMN_TITLE + " text, " +
                NOTES_COLUMN_CONTENT + " text, " +
                NOTES_COLUMN_SAVEDATE + " text, " +
                NOTES_COLUMN_NOTIFICATION_ID + " integer, " +
                NOTES_COLUMN_NOTEBOOK_ID + " integer, " +
                "FOREIGN KEY" + "(" + NOTES_COLUMN_NOTIFICATION_ID + ") " +
                "REFERENCES " + NOTIFICATIONS_TABLE_NAME + "(" + NOTIFICATIONS_COLUMN_ID + ")" + ")";
        db.execSQL(notes_sql);

        String notebooks_sql = "create table " + NOTEBOOKS_TABLE_NAME +
                " (" + NOTEBOOKS_COLUMN_ID + " integer primary key AUTOINCREMENT, " +
                NOTEBOOKS_COLUMN_TITLE + " text" + ")";
        db.execSQL(notebooks_sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {      //SQLiteOpenHelper class-dependent method to refresh database tables
        db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NOTIFICATIONS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NOTEBOOKS_TABLE_NAME);
        onCreate(db);
    }

    /**************************************************************************************************/

    /*methods below are for manipulating note data in database*/
    public boolean insertNote(Note n) {    //method to insert a new note to database
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(NOTES_COLUMN_TITLE, n.getTitle());
        contentValues.put(NOTES_COLUMN_CONTENT, n.getContent());
        contentValues.put(NOTES_COLUMN_SAVEDATE, n.getSaveDate());

        if (n.getNotification() != null) {
            insertNotification(n.getNotification());
            contentValues.put(NOTES_COLUMN_NOTIFICATION_ID, getLastAddedNotification().getId());
        }

        db.insert(NOTES_TABLE_NAME, NOTES_COLUMN_NOTIFICATION_ID, contentValues);

        return true;
    }

    public boolean updateNote(Note n, Note key) {    //method to update a note from database //key is the note to be updated #serdar
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(NOTES_COLUMN_TITLE, n.getTitle());
        contentValues.put(NOTES_COLUMN_CONTENT, n.getContent());
        contentValues.put(NOTES_COLUMN_SAVEDATE, n.getSaveDate());

        if (n.getNotification() != null) {                   //if new notification isn't null
            if (key.getNotification() == null) {            //if old notification is null
                insertNotification(n.getNotification());    //insert new notification
                contentValues.put(NOTES_COLUMN_NOTIFICATION_ID, getLastAddedNotification().getId());    //put id of last added notification to notes notification id column
            } else {                                                                  //if old notification isn't null
                updateNotification(n.getNotification(), key.getNotification());     //update old notification with new one
            }

        }

        db.update(NOTES_TABLE_NAME,
                contentValues,
                "id = ? ",
                new String[]{Integer.toString(key.getId())});

        return true;
    }

    public Boolean deleteNote(Note n) {        //method to delete a note from database
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            if (n.getNotification() != null) //delete the note's notification if it has one
                deleteNotification(n.getNotification()); // n must carry the notification id

            db.delete(NOTES_TABLE_NAME,
                    NOTES_COLUMN_ID + " = ? ",
                    new String[]{Integer.toString(n.getId())});

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public Note getNoteFromNoteId(int note_id) {       //method to fetch note data from given note id from database
        SQLiteDatabase db = this.getReadableDatabase();
        Notification notification = new Notification();

        Cursor res = db.rawQuery("select * from " + NOTES_TABLE_NAME + " where " + NOTES_COLUMN_ID + "= " + note_id,
                null);
        res.moveToFirst();

        if (!res.isNull(res.getColumnIndex(NOTES_COLUMN_NOTIFICATION_ID))) {
            notification = getNotificationFromNotificationID(res.getInt(res.getColumnIndex(NOTES_COLUMN_NOTIFICATION_ID)));
        } else {
            notification = null;
        }

        return new Note(res.getInt(res.getColumnIndex(NOTES_COLUMN_ID)),
                res.getString(res.getColumnIndex(NOTES_COLUMN_TITLE)),
                res.getString(res.getColumnIndex(NOTES_COLUMN_CONTENT)),
                notification,
                res.getString(res.getColumnIndex(NOTES_COLUMN_SAVEDATE)));
    }

    public Note getNoteFromNotificationId(int notification_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("select * from " + NOTES_TABLE_NAME + " where " + NOTES_COLUMN_NOTIFICATION_ID + "= " + notification_id, null);
        res.moveToFirst();

        return new Note(res.getInt(res.getColumnIndex(NOTES_COLUMN_ID)),
                res.getString(res.getColumnIndex(NOTES_COLUMN_TITLE)),
                res.getString(res.getColumnIndex(NOTES_COLUMN_CONTENT)),
                getNotificationFromNotificationID(notification_id),
                res.getString(res.getColumnIndex(NOTES_COLUMN_SAVEDATE)));

    }

    public int getNoteCount() {      //method to fetch number of notes that exist in database
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, NOTES_TABLE_NAME);

        return numRows;
    }

    public ArrayList<Note> getAllNotes() {        //method to fetch data of all notes from database
        SQLiteDatabase db = this.getReadableDatabase();
        Notification foundNotification = new Notification();
        ArrayList<Note> array_list = new ArrayList<Note>();

        Cursor resNotes = db.rawQuery("select * from " + NOTES_TABLE_NAME, null);
        resNotes.moveToFirst();

        while (resNotes.isAfterLast() == false) {
            // gets the notification if the note has one
            if (!resNotes.isNull(resNotes.getColumnIndex(NOTES_COLUMN_NOTIFICATION_ID))) {
                foundNotification = getNotificationFromNotificationID(resNotes.getInt(resNotes.getColumnIndex(NOTES_COLUMN_NOTIFICATION_ID)));
            } else
                foundNotification = null;

            // creates and adds note to the array to be returned
            Note foundNote = new Note(resNotes.getInt(resNotes.getColumnIndex(NOTES_COLUMN_ID)),
                    resNotes.getString(resNotes.getColumnIndex(NOTES_COLUMN_TITLE)),
                    resNotes.getString(resNotes.getColumnIndex(NOTES_COLUMN_CONTENT)),
                    foundNotification,
                    resNotes.getString(resNotes.getColumnIndex(NOTES_COLUMN_SAVEDATE)));

            array_list.add(foundNote);
            resNotes.moveToNext();

        }

        return array_list;
    }

    /**************************************************************************************************/

    /*methods below are for manipulating notification data in database*/
    public boolean insertNotification(Notification n) {    //method to insert a new notification to database
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(NOTIFICATIONS_COLUMN_DATE, n.getDate());

        db.insert(NOTIFICATIONS_TABLE_NAME, null, contentValues);

        return true;
    }

    public boolean updateNotification(Notification n, Notification key) {    //method to update a notification from database
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(NOTIFICATIONS_COLUMN_DATE, n.getDate());

        db.update(NOTIFICATIONS_TABLE_NAME,
                contentValues,
                NOTIFICATIONS_COLUMN_ID + "= ? ",
                new String[]{Integer.toString(key.getId())});

        return true;
    }

    public boolean deleteNotification(Notification n) {        //method to delete a notification from database
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(NOTES_COLUMN_NOTIFICATION_ID, (Integer) null);

            db.update(NOTES_TABLE_NAME, contentValues, NOTES_COLUMN_NOTIFICATION_ID + "= ?", new String[]{Integer.toString(n.getId())});

            db.delete(NOTIFICATIONS_TABLE_NAME,
                    NOTIFICATIONS_COLUMN_ID + " = ? ",
                    new String[]{Integer.toString(n.getId())});

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public Notification getNotificationFromNotificationID(int notificationId) {       //method to fetch notification data from given notification id from database
        SQLiteDatabase db = this.getReadableDatabase();
        Notification n = new Notification();

        Cursor res = db.rawQuery("select * from " + NOTIFICATIONS_TABLE_NAME + " where " + NOTIFICATIONS_COLUMN_ID + "= " + notificationId,
                null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            if (!res.isNull(res.getColumnIndex(NOTIFICATIONS_COLUMN_ID))) {
                n.setId(res.getInt(res.getColumnIndex(NOTIFICATIONS_COLUMN_ID)));
                n.setDate(res.getString(res.getColumnIndex(NOTIFICATIONS_COLUMN_DATE)));
                break;
            }
            res.moveToNext();
        }

        return n;
    }

    public int getNotificationCount() {      //method to fetch number of notifications that exist in database
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, NOTIFICATIONS_TABLE_NAME);
        db.close();

        return numRows;
    }

    public Notification getLastAddedNotification() {        //method that returns last added notification
        String selectQuery = "SELECT * FROM " + NOTIFICATIONS_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToLast();

        Notification lastNotification = new Notification();

        if (!cursor.isNull(cursor.getColumnIndex(NOTIFICATIONS_COLUMN_ID))) {       //if notification id isn't null
            lastNotification.setId(cursor.getInt(cursor.getColumnIndex(NOTIFICATIONS_COLUMN_ID)));
            lastNotification.setDate(cursor.getString(cursor.getColumnIndex(NOTIFICATIONS_COLUMN_DATE)));
        }
        //else notification is null

        return lastNotification;
    }

    /**************************************************************************************************/

    /*methods below are for manipulating notebook data in database*/
    public void insertNotebook(Notebook n) {     //method to insert Notebook inside SQLite Database
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(NOTEBOOKS_COLUMN_TITLE, n.getTitle());

        db.insert(NOTEBOOKS_TABLE_NAME, null, contentValues);
    }

    public void updateNotebook(Notebook n, Notebook key) {       //method to update an existing Notebook in SQLite Database
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(NOTEBOOKS_COLUMN_TITLE, n.getTitle());

        db.update(NOTEBOOKS_TABLE_NAME,
                contentValues,
                NOTEBOOKS_COLUMN_ID + "= ? ",
                new String[]{Integer.toString(key.getId())});
    }

    public boolean deleteNotebook(Notebook n) {          //method to delete a Notebook from SQLite Database
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(NOTES_COLUMN_NOTEBOOK_ID, (Integer) null);

            db.update(NOTES_TABLE_NAME, contentValues, NOTES_COLUMN_NOTEBOOK_ID + "= ?", new String[]{Integer.toString(n.getId())});

            db.delete(NOTEBOOKS_TABLE_NAME,
                    NOTEBOOKS_COLUMN_ID + " = ? ",
                    new String[]{Integer.toString(n.getId())});

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public Notebook getNotebookFromNotebookId(int id) {      //method that returns Notebook object with given ID parameter
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Integer> noteIds = new ArrayList<Integer>();
        String title = "";

        Cursor notebook = db.rawQuery("select * from " + NOTEBOOKS_TABLE_NAME + " where " + NOTEBOOKS_COLUMN_ID + "= " + id, null);
        notebook.moveToFirst();
        title = notebook.getString(notebook.getColumnIndex(NOTEBOOKS_COLUMN_TITLE));

        //adds notes' ids to the corresponding notebook
        Cursor notes = db.rawQuery("select * from " + NOTES_TABLE_NAME + " where " + NOTES_COLUMN_NOTEBOOK_ID + "= " + id, null);
        notes.moveToFirst();
        while (!notes.isAfterLast()) {
            noteIds.add(notes.getInt(notes.getColumnIndex(NOTES_COLUMN_ID)));
            notes.moveToNext();
        }

        return new Notebook(id, title, noteIds);
    }

    public ArrayList<Notebook> getAllNotebooks() {           //method that returns all notebooks with their notes' ids
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Notebook> notebooks = new ArrayList<Notebook>();

        Cursor resNotebook = db.rawQuery("select * from " + NOTEBOOKS_TABLE_NAME, null);
        resNotebook.moveToFirst();

        while (resNotebook.isAfterLast() == false) {
            //adds notebook to the array to be returned
            notebooks.add(getNotebookFromNotebookId(resNotebook.getInt(resNotebook.getColumnIndex(NOTEBOOKS_COLUMN_ID))));
            resNotebook.moveToNext();
        }

        return notebooks;
    }

    public ArrayList<Note> getNotesFromNotebook(Notebook n) {    //this function is called to display the notes inside of a notebook
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Note> notes = new ArrayList<Note>();

        //if the notebook has the note ids it should be a bit faster to run this way
        if (n.getNoteIds() != null) {
            if (n.getNoteIds().size() > 0) {
                for (Integer id : n.getNoteIds()) {
                    notes.add(getNoteFromNoteId(id));
                }
            }
        } else {
            Cursor resNotes = db.rawQuery("select * from " + NOTES_TABLE_NAME + " where " + NOTES_COLUMN_NOTEBOOK_ID + "= " + n.getId(), null);
            resNotes.moveToFirst();
            while (!resNotes.isAfterLast()) {
                notes.add(getNoteFromNoteId(resNotes.getInt(resNotes.getColumnIndex(NOTES_COLUMN_ID))));
                resNotes.moveToNext();
            }
        }
        return notes;
    }

    public void addNoteToNotebook(Note note, Notebook notebook) {        //method that adds note to edited Notebook
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTES_COLUMN_NOTEBOOK_ID, notebook.getId());

        db.update(NOTES_TABLE_NAME,
                contentValues,
                "id= ?",
                new String[]{"" + note.getId()});
    }

    public void removeNoteFromNotebook(Note note) {          //method that removes note from edited Notebook.
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTES_COLUMN_NOTEBOOK_ID, (Integer) null);

        db.update(NOTES_TABLE_NAME,
                contentValues,
                "id= ?",
                new String[]{"" + note.getId()});
    }

    public ArrayList<Note> getAllNotesExceptCurrentNotebook(Notebook n) {       //this method returns an ArrayList with notes except the ones that are inside given Notebook
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Note> notes = new ArrayList<Note>();

        Cursor resNotes = db.rawQuery("select * from " + NOTES_TABLE_NAME + " except " +
                "select * from " + NOTES_TABLE_NAME + " where " + NOTES_COLUMN_NOTEBOOK_ID + "= " + n.getId(), null);

        resNotes.moveToFirst();
        while (!resNotes.isAfterLast()) {
            notes.add(getNoteFromNoteId(resNotes.getInt(resNotes.getColumnIndex(NOTES_COLUMN_ID))));
            resNotes.moveToNext();
        }
        return notes;
    }
}