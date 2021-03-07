//Notebook Model Class

package sandy.android.assistant.Model;

import java.util.ArrayList;

public class Notebook {

    private Integer id;
    private String title = "";
    private ArrayList<Integer> noteIds = new ArrayList<Integer>();


    public Notebook() {

    }

    public Notebook(String title) {
        this.title = title;
    }

    public Notebook(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public Notebook(Integer id, String title, ArrayList<Integer> noteIds) {
        this.id = id;
        this.title = title;
        this.noteIds = noteIds;
    }

    public Integer getId() {                //getter setter methods to access private variables
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Integer> getNoteIds() {
        return noteIds;
    }

    public void setNoteIds(ArrayList<Integer> noteIds) {
        this.noteIds = noteIds;
    }
}
