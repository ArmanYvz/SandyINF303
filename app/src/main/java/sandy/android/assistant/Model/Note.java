//Note Model Class

package sandy.android.assistant.Model;

import java.util.ArrayList;

public class Note {
    private int id = 0;
    private String title = "";
    private String content = "";
    private Notification notification;
    private String saveDate = null;

    public Note() {
    }

    public Note(String title, String content, Notification notification, String date) {
        setTitle(title);
        this.content = content;
        this.notification = notification;
        this.saveDate = date;
    }

    public Note(int id, String title, String content, Notification notification, String date) {
        this.id = id;
        setTitle(title);
        this.content = content;
        this.notification = notification;
        this.saveDate = date;
    }

    public String getTitle() {                   //getter setter methods to access private variables of note
        return title;
    }

    public void setTitle(String title) {
        if (title == null) {
            title = "";
        }
        this.title = title;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSaveDate() {
        return saveDate;
    }

    public void setSaveDate(String saveDate) {
        this.saveDate = saveDate;
    }
}