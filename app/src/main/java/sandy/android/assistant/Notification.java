package sandy.android.assistant;

import android.annotation.SuppressLint;

import java.time.LocalDateTime;

public class Notification {
    private Integer id;
    private LocalDateTime date;

    public Integer getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getDate() {
        return date.toString();
    }

    @SuppressLint("NewApi")
    //min. API Level_26 required for parse function
    public void setDate(String date) {
        this.date = LocalDateTime.parse(date.subSequence(0,date.length()));
    }

}