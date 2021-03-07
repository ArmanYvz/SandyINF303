//Activity Class that works as a Controller to handle user actions by calls between DatabaseManagement.java Model Class
//and notification_editor.xml View Component

package sandy.android.assistant.Controller;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.DialogFragment;

import sandy.android.assistant.Fragment.DatePickerFragment;
import sandy.android.assistant.Fragment.TimePickerFragment;
import sandy.android.assistant.Model.CalendarSync;
import sandy.android.assistant.Model.DatabaseManagement;
import sandy.android.assistant.Model.Notification;
import sandy.android.assistant.Adapter.NotificationAdapter;
import sandy.android.assistant.R;

public class NotificationEditorActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    public static String CLASS_NOTE_EDITOR_ACTIVITY_NAME = NoteEditorActivity.class.toString();
    public static String CLASS_NOTIFICATION_ADAPTER_NAME = NotificationAdapter.class.toString();

    DatabaseManagement db;

    String date, time;

    Button cancelNotificationButton;
    Button datePickerButton;
    Button timePickerButton;
    Button saveNotificationButton;

    CalendarSync calendarSync;
    Context context;

    DialogFragment timePicker;
    DialogFragment datePicker;

    TextView datePickerTextView;
    TextView timePickerTextView;

    Notification editNotification;

    String year, month, day, hour, minute;
    String selectedDateString = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_editor);

        calendarSync = new CalendarSync();
        context = getApplicationContext();

        cancelNotificationButton = findViewById(R.id.cancelNotificationButton);     //initialization of view components
        datePickerButton = (Button) findViewById(R.id.datePickerButton);
        timePickerButton = (Button) findViewById(R.id.timePickerButton);
        saveNotificationButton = (Button) findViewById(R.id.saveNotificationButton);

        datePickerTextView = (TextView) findViewById(R.id.datePickertextView);
        timePickerTextView = (TextView) findViewById(R.id.timePickertextView);

        db = new DatabaseManagement(this);      //database access object initialization


        //getting notification of the note if it has one and updates the screen
        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.get("NOTIFICATION_ID") != null) {       //if a notification id is returned from bundle extra,
                //set editedNotification value to this value by sending returned notification id to database access object.
                editNotification = db.getNotificationFromNotificationID(b.getInt("NOTIFICATION_ID"));
                updateNotificationActivity(editNotification);       //refresh notification view
            }
        }

        saveNotificationButton.setOnClickListener(new View.OnClickListener() {  //if save notification button is clicked
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                //if no date or time is selected, do not complete the action and return
                if (date == null || time == null) {
                    return;
                }
                //if both of date and time values are set,
                String currentDateString = date + "T" + time + ":00" + "Z";     //default date time save format

                //code stack below examines if only notification is edited or note is edited.

                //if this activity is started from noteEditorActivity, note is being edited.
                if (getCallingActivity().getClassName().equals(NoteEditorActivity.class.toString().replace("class ", ""))) {
                    Intent data = new Intent(getApplicationContext(), NoteEditorActivity.class);
                    data.putExtra("NOTIFICATION_DATE", currentDateString);
                    setResult(Activity.RESULT_OK, data);
                }
                //if this activity is started from notificationViewActivity, only notification is being edited.
                else if (getCallingActivity().getClassName().equals(NotificationViewActivity.class.toString().replace("class ", ""))) {
                    Notification newNotification = new Notification(currentDateString);
                    //update Notification at database
                    db.updateNotification(newNotification, editNotification);
                    int calendarReadPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALENDAR);
                    int calendarWritePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_CALENDAR);

                    if (calendarReadPermission == PermissionChecker.PERMISSION_GRANTED && calendarWritePermission == PermissionChecker.PERMISSION_GRANTED) {
                        int numberOfRowsAffected = 0;
                        //update calendar event that is attached to edited Notification
                        numberOfRowsAffected = calendarSync.updateCalendarEntry(context, editNotification.getId(), db.getNoteFromNotificationId(editNotification.getId()));
                        //if at least 1 row is affected, that means event is updated. Send toast message.
                        if (numberOfRowsAffected > 0) {
                            Toast.makeText(context, context.getResources().getString(R.string.calendar_event_updated), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                finish();       //finish activity
                return;
            }

        });

        timePickerButton.setOnClickListener(new View.OnClickListener() {        //if set time button is clicked
            @Override
            public void onClick(View v) {
                if (editNotification != null)        //if notification is being edited
                    //inflate timePicker with returned time information from bundle extra
                    timePicker = new TimePickerFragment(new Integer(hour), new Integer(minute));
                else
                    timePicker = new TimePickerFragment();      //else just inflate a timePicker with blank selection

                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        datePickerButton.setOnClickListener(new View.OnClickListener() {        //if set date button is clicked
            @Override
            public void onClick(View v) {
                if (editNotification != null)        //if notification is being edited
                    //inflate datePicker with returned date information from bundle extra
                    datePicker = new DatePickerFragment(new Integer(year), new Integer(month) - 1, new Integer(day));
                else
                    datePicker = new DatePickerFragment();      //else just inflate a datePicker with blank selection

                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        cancelNotificationButton.setOnClickListener(new View.OnClickListener() {        //if cancel button is clicked
            @Override
            public void onClick(View v) {           //cancel button
                finish();       //notification screen cancelled. go back to note editor.
            }
        });
    }

    //onDateSet method that handles time selection logic and  updates view when date on dateFragment is set by the user.
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);              //set year, month and day values to the date information that set on dateFragment
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        month = month + 1;      //just a fix for bugged android month selection

        selectedDateString = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());        //get current date
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.FULL);
        DateFormat formatter1 = new SimpleDateFormat("dd-MM-yyyy");
        try {
            datePickerTextView.setText("Date: " + formatter1.format(formatter.parse(selectedDateString)));
            selectedDateString = formatter1.format(formatter.parse(selectedDateString));        //format selected date string to simpleDateFormat selected above
        } catch (ParseException e) {
            e.printStackTrace();
        }

        date = year + "-" + month + "-" + dayOfMonth;       //set date variable with selected date information

    }

    //onTimeSet method that handles time selection logic and  updates view when time on timeFragment is set by the user.
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        Calendar datetime = Calendar.getInstance();
        Calendar current = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);      //set hour and minute values to the time information that set on timeFragment
        datetime.set(Calendar.MINUTE, minute);

        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat formatter1 = DateFormat.getDateInstance(DateFormat.FULL);
        Date currentDate = Calendar.getInstance().getTime();        //get current time

        String currentDateString = formatter.format(currentDate);       //format current time to simple date format

        if (selectedDateString == null) {           //if no new date is selected
            if (currentDateString.equals(day + "-" + month + "-" + year)) {
                if (datetime.getTimeInMillis() >= current.getTimeInMillis()) {          //if selected date and time is future date than now,
                    timePickerTextView.setText("Hour: " + hourOfDay + " Minute: " + minute);        //set timepicker text view to selected time
                    time = hourOfDay + ":" + minute;        //update time information with selected time value
                } else {
                    //it's before current'
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.notification_past_time_error), Toast.LENGTH_LONG).show();
                }
            } else {
                timePickerTextView.setText("Hour: " + hourOfDay + " Minute: " + minute);
                time = hourOfDay + ":" + minute;        //set time variable with selected hour and minute information
            }
        } else {      //if date is changed
            if (currentDateString.equals(selectedDateString)) {
                if (datetime.getTimeInMillis() >= current.getTimeInMillis()) {
                    timePickerTextView.setText("Hour: " + hourOfDay + " Minute: " + minute);
                    time = hourOfDay + ":" + minute;        //set time variable with selected hour and minute information
                } else {
                    //it's before current'
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.notification_past_time_error), Toast.LENGTH_LONG).show();
                }
            } else {
                timePickerTextView.setText("Hour: " + hourOfDay + " Minute: " + minute);
                time = hourOfDay + ":" + minute;        //set time variable with selected hour and minute information
            }
        }


    }

    public void updateNotificationActivity(Notification n) {     //method that refreshes timePicker and datePicker date/time informations

        parse();
        timePickerTextView.setText(getResources().getString(R.string.hour_text) + ": " + hour + " " + getResources().getString(R.string.minute_text) + ": " + minute);
        datePickerTextView.setText(getResources().getString(R.string.date_text) + ": " + day + "-" + month + "-" + year);

    }

    public void parse() {        //method that parses notification date/time to a cleaner format
        String date = "";
        String time = "";
        String date_time = editNotification.getDate();


        date = date_time.substring(0, date_time.indexOf('T'));       //create substrings
        time = date_time.substring(date_time.indexOf('T') + 1, date_time.indexOf('Z'));

        year = date.split("-")[0];      //split date values
        month = date.split("-")[1];
        day = date.split("-")[2];

        hour = time.split(":")[0];      //split time values
        minute = time.split(":")[1];

        //set date and time values to formatted type (will be used while setting date and time information textboxes
        this.date = year + "-" + month + "-" + day;
        this.time = hour + ":" + minute;
    }
}
