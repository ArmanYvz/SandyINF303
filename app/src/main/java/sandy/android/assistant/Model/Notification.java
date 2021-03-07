//Notification Model Class

package sandy.android.assistant.Model;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Notification {
    private int id;
    private String date = "";

    public Notification() {

    }

    public Notification(String date) {
        this.setDate(date);
    }

    public Integer getId() {                     //getter setter methods to access private variables
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {

        this.date = date;

    }

}
