//Model Class that handles connection between Calendar Provider and Application. Executes Calendar Provider API queries.

package sandy.android.assistant.Model;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.Html;
import android.text.Spanned;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import sandy.android.assistant.R;

public class CalendarSync {
    private String eventTitle = "";
    private Notification eventNotification;
    private String eventDescription = "";

    public CalendarSync() {

    }

    public CalendarSync(String eventTitle, Notification eventNotification, String eventDescription) {
        this.eventTitle = eventTitle;
        this.eventNotification = eventNotification;
        this.eventDescription = eventDescription;
    }

    //getter setter methods to access private variables

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public Notification getEventNotification() {
        return eventNotification;
    }

    public void setEventNotification(Notification eventNotification) {
        this.eventNotification = eventNotification;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    //function to add note information as calendar event
    public void addCalendarEvent(Context context, String title, String description, Notification notification) {

        long startMillis = 0;
        long endMillis = 0;
        String eventDate = "";
        String eventTime = "";
        String date_time = notification.getDate();

        eventDate = date_time.substring(0, date_time.indexOf('T'));
        eventTime = date_time.substring(date_time.indexOf('T') + 1, date_time.indexOf('Z'));

        Integer eventYear = Integer.parseInt(eventDate.split("-")[0]);
        Integer eventMonth = Integer.parseInt(eventDate.split("-")[1]);
        Integer eventDay = Integer.parseInt(eventDate.split("-")[2]);

        Integer eventHour = Integer.parseInt(eventTime.split(":")[0]);
        Integer eventMinute = Integer.parseInt(eventTime.split(":")[1]);

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(eventYear, eventMonth - 1, eventDay, eventHour, eventMinute);
        startMillis = beginTime.getTimeInMillis();

        Calendar endTime = Calendar.getInstance();
        endTime.set(eventYear, eventMonth - 1, eventDay, eventHour + 1, eventMinute);
        endMillis = endTime.getTimeInMillis();

        Spanned eventDescription = Html.fromHtml(description);

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_ID, notification.getId())
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.DESCRIPTION, eventDescription.toString());
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.calendar_sync_no_app), Toast.LENGTH_LONG);
        }
    }


    //method that adds calendar events in background

    public void addCalendarEventInBackground(Context context, String title, String description, Notification notification) {
        long startMillis = 0;
        long endMillis = 0;
        String eventDate = "";
        String eventTime = "";
        String eventId = "";
        String date_time = notification.getDate();

        eventDate = date_time.substring(0, date_time.indexOf('T'));
        eventTime = date_time.substring(date_time.indexOf('T') + 1, date_time.indexOf('Z'));

        Integer eventYear = Integer.parseInt(eventDate.split("-")[0]);
        Integer eventMonth = Integer.parseInt(eventDate.split("-")[1]);
        Integer eventDay = Integer.parseInt(eventDate.split("-")[2]);

        Integer eventHour = Integer.parseInt(eventTime.split(":")[0]);
        Integer eventMinute = Integer.parseInt(eventTime.split(":")[1]);

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(eventYear, eventMonth - 1, eventDay, eventHour, eventMinute);
        startMillis = beginTime.getTimeInMillis();

        Calendar endTime = Calendar.getInstance();
        endTime.set(eventYear, eventMonth - 1, eventDay, eventHour + 1, eventMinute);
        endMillis = endTime.getTimeInMillis();

        Spanned eventDescription = Html.fromHtml(description);

        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();

        TimeZone timeZone = TimeZone.getDefault();
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        values.put(CalendarContract.Events._ID, notification.getId());

        String calIdValue = getCalendarId(context);
        System.out.println("selected calendar: " + calIdValue);
        if (calIdValue == null) {
            Toast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.no_calendar_alert), Toast.LENGTH_LONG).show();
            return;
        }
        values.put(CalendarContract.Events.CALENDAR_ID, calIdValue);

        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.HAS_ALARM, true);
        values.put(CalendarContract.Events.DESCRIPTION, eventDescription.toString());

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        Toast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.calendar_event_insert_success), Toast.LENGTH_LONG).show();

        ContentValues reminders = new ContentValues();
        reminders.put(CalendarContract.Reminders.EVENT_ID, notification.getId());
        reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        reminders.put(CalendarContract.Reminders.MINUTES, 0);

        Uri uri2 = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders);
    }


    public int listSelectedCalendars(Context context, String eventtitle) {     //method that lists specific events


        Uri eventUri;
        if (android.os.Build.VERSION.SDK_INT <= 7) {
            eventUri = Uri.parse("content://calendar/events");
        } else {
            eventUri = Uri.parse("content://com.android.calendar/events");
        }

        int result = 0;
        String projection[] = {"_id", "title"};
        Cursor cursor = context.getContentResolver().query(eventUri, null, null, null,
                null);

        if (cursor.moveToFirst()) {

            String calName;
            String calID;

            int nameCol = cursor.getColumnIndex(projection[1]);
            int idCol = cursor.getColumnIndex(projection[0]);
            do {
                calName = cursor.getString(nameCol);
                calID = cursor.getString(idCol);

                if (calName != null && calName.contains(eventtitle)) {
                    result = Integer.parseInt(calID);
                }

            } while (cursor.moveToNext());
            cursor.close();
        }

        return result;

    }

    @SuppressLint("InlinedApi")
    public int updateCalendarEntry(Context context, int entryID, Note note) {         //method that updates calendar entry with given event id

        long startMillis = 0;
        long endMillis = 0;
        String eventDate = "";
        String eventTime = "";
        String eventId = "";
        if (note.getNotification() != null) {
            String date_time = note.getNotification().getDate();

            eventDate = date_time.substring(0, date_time.indexOf('T'));
            eventTime = date_time.substring(date_time.indexOf('T') + 1, date_time.indexOf('Z'));

            Integer eventYear = Integer.parseInt(eventDate.split("-")[0]);
            Integer eventMonth = Integer.parseInt(eventDate.split("-")[1]);
            Integer eventDay = Integer.parseInt(eventDate.split("-")[2]);

            Integer eventHour = Integer.parseInt(eventTime.split(":")[0]);
            Integer eventMinute = Integer.parseInt(eventTime.split(":")[1]);

            Calendar beginTime = Calendar.getInstance();
            beginTime.set(eventYear, eventMonth - 1, eventDay, eventHour, eventMinute);
            startMillis = beginTime.getTimeInMillis();

            Calendar endTime = Calendar.getInstance();
            endTime.set(eventYear, eventMonth - 1, eventDay, eventHour + 1, eventMinute);
            endMillis = endTime.getTimeInMillis();
        }

        Spanned eventDescription = Html.fromHtml(note.getContent());

        int iNumRowsUpdated = 0;

        Uri eventUri;

        if (android.os.Build.VERSION.SDK_INT <= 7) {
            eventUri = Uri.parse("content://calendar/events");
        } else {
            eventUri = Uri.parse("content://com.android.calendar/events");
        }

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.TITLE, note.getTitle());
        values.put(CalendarContract.Events.DESCRIPTION, eventDescription.toString());

        if (note.getNotification() != null) {
            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.DTEND, endMillis);
        }

        Uri updateUri = ContentUris.withAppendedId(eventUri, entryID);
        iNumRowsUpdated = context.getContentResolver().update(updateUri, values, null, null);

        return iNumRowsUpdated;
    }

    public int deleteCalendarEntry(Context context, int entryID) {     //function that deletes a calendar entry with given event id
        int iNumRowsDeleted = 0;

        Uri eventUri = ContentUris.withAppendedId(getCalendarUriBase(), entryID);
        System.out.println("deleted event URI : " + eventUri);
        iNumRowsDeleted = context.getContentResolver().delete(eventUri, null, null);

        return iNumRowsDeleted;
    }

    public Uri getCalendarUriBase() {       //method that returns calendar uri base
        Uri eventUri;
        if (android.os.Build.VERSION.SDK_INT <= 7) {
            eventUri = Uri.parse("content://calendar/events");
        } else {
            eventUri = Uri.parse("content://com.android.calendar/events");
        }

        return eventUri;
    }

    public String getCalendarId(Context context) {       //method that returns a specific calendar id
        String returnval = null;
        List<String> projection = Arrays.asList(CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL);
        String[] projectionString = projection.toArray(new String[3]);

        Cursor cursor = context.getContentResolver().query(
                CalendarContract.Calendars.CONTENT_URI,
                projectionString,
                CalendarContract.Calendars.VISIBLE + " = 1 AND " + CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " = 700",
                null,
                CalendarContract.Calendars._ID + " ASC"
        );

        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                String calName;
                String calId;
                String calAccessLevel;
                Integer accessLevel = cursor.getColumnIndex(projectionString[2]);
                Integer nameCol = cursor.getColumnIndex(projectionString[1]);
                Integer idCol = cursor.getColumnIndex(projectionString[0]);

                calName = cursor.getString(nameCol);
                calId = cursor.getString(idCol);
                calAccessLevel = cursor.getString(accessLevel);

                System.out.println("calendar name = " + calName + " calendar id = " + calId + " access level = " + calAccessLevel);
                if (calName.equals("sandy personal assistant calendar")) {
                    returnval = calId;
                    cursor.close();
                    break;
                }
                cursor.moveToNext();
            }
            cursor.close();
        } else {
            returnval = null;
        }
        return returnval;
    }

    public static String getCalendarName(Context context) {      //method that returns specific calendar Name
        String returnval = null;
        List<String> projection = Arrays.asList(CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL);
        String[] projectionString = projection.toArray(new String[3]);

        Cursor cursor = context.getContentResolver().query(
                CalendarContract.Calendars.CONTENT_URI,
                projectionString,
                CalendarContract.Calendars.VISIBLE + " = 1 AND " + CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " = 700",
                null,
                CalendarContract.Calendars._ID + " ASC"
        );

        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                String calName;
                String calId;
                String calAccessLevel;
                Integer accessLevel = cursor.getColumnIndex(projectionString[2]);
                Integer nameCol = cursor.getColumnIndex(projectionString[1]);
                Integer idCol = cursor.getColumnIndex(projectionString[0]);

                calName = cursor.getString(nameCol);
                calId = cursor.getString(idCol);
                calAccessLevel = cursor.getString(accessLevel);

                System.out.println("calendar name = " + calName + " calendar id = " + calId + " access level = " + calAccessLevel);
                if (calName.equals("sandy personal assistant calendar")) {
                    returnval = calName;
                    cursor.close();
                    break;
                }
                cursor.moveToNext();
            }
            cursor.close();
        } else {
            returnval = null;
        }
        return returnval;
    }

    public static String createNewCalendar(Context context) {       //method that creates a new calendar with name "sandy personal assistant"
        Uri calUri = CalendarContract.Calendars.CONTENT_URI
                .buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, "sandy")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                .build();

        String dispName = "sandy personal assistant calendar";
        ContentValues contentValues = new ContentValues();

        contentValues.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, Integer.toString(CalendarContract.Calendars.CAL_ACCESS_OWNER));
        contentValues.put(CalendarContract.Calendars.NAME, "sandy");
        contentValues.put(CalendarContract.Calendars.ACCOUNT_NAME, "sandy");
        contentValues.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        contentValues.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, dispName);
        contentValues.put(CalendarContract.Calendars.VISIBLE, 1);
        contentValues.put(CalendarContract.Calendars.OWNER_ACCOUNT, "sandy");
        contentValues.put(CalendarContract.Calendars.CALENDAR_COLOR, R.color.orange);
        contentValues.put(CalendarContract.Calendars.SYNC_EVENTS, 1);

        ContentResolver cr = context.getContentResolver();
        Uri returnUri;
        returnUri = cr.insert(calUri, contentValues);
        long eventID = Long.parseLong(returnUri.getLastPathSegment());
        String createdCalendarID = String.valueOf(eventID);
        System.out.println("created calendar id : " + createdCalendarID + " inserted URI : " + returnUri);
        return createdCalendarID;


    }
}
