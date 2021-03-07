//Activity Class that works as a Controller to handle user actions by calls between DatabaseManagement.java Model Class
//and notebook_recycler_view.xml View Component

package sandy.android.assistant.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.irshulx.Editor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import sandy.android.assistant.Adapter.NotebookAdapter;
import sandy.android.assistant.Model.DatabaseManagement;
import sandy.android.assistant.Model.Note;
import sandy.android.assistant.Model.Notebook;
import sandy.android.assistant.R;

public class NotebookActivity extends AppCompatActivity {
    private static final int REQUEST_NOTEBOOK = 0;

    DatabaseManagement db;
    ConstraintLayout constraintLayout;
    LinearLayoutManager linearLayoutManager;
    RecyclerView listOfNotesOfNotebook;
    ArrayList<Notebook> notebookArrayList;
    ArrayList<Note> notebookNotes;
    NotebookAdapter notebookAdapter;

    ConstraintLayout notebookEditorViewConstraintLayout;
    Editor notebookEditorView;
    ImageView backButton;
    ImageView editTitleButton;

    FloatingActionButton fabAddNewNoteToNotebook;
    Spinner spinnerSetNotebookViewType;

    Notebook selectedNotebook;

    String notebookEditorViewHtmlString;
    TextView notebookTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notebook_recycler_view);

        db = new DatabaseManagement(this);          //database access object initialization
        constraintLayout = findViewById(R.id.notebookConstraintLayout);             //initializations for view components.
        fabAddNewNoteToNotebook = findViewById(R.id.fabAddNewNoteToNotebook);
        notebookTitle = findViewById(R.id.textviewNotebookViewNotebookTitle);
        backButton = findViewById(R.id.buttonBackFromNotebookRecyclerView);
        editTitleButton = findViewById(R.id.imageViewTitleEditButton);

        notebookEditorViewConstraintLayout = findViewById(R.id.notebookEditorViewConstraintLayout);

        listOfNotesOfNotebook = findViewById(R.id.listOfNotesOfNotebook);

        linearLayoutManager = new LinearLayoutManager(this);        //defines LinearLayoutManager for vertical Recycleview orientation
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listOfNotesOfNotebook.setLayoutManager(linearLayoutManager);

        spinnerSetNotebookViewType = findViewById(R.id.spinnerSetNotebookViewType);
        spinnerSetNotebookViewType.setVisibility(View.VISIBLE);

        Bundle b = getIntent().getExtras();     //get Bundles from other activities
        if (b != null) {
            if (b.get("NOTEBOOK_ID") != null) {
                selectedNotebook = db.getNotebookFromNotebookId(b.getInt("NOTEBOOK_ID"));
                this.setNotebookViewContent(selectedNotebook);      //update notebookviewcontent with returned information
            } else {
                finish();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.notebook_open_fail_error), Toast.LENGTH_LONG).show();     //toast notebook open fail message
                return;
            }
        } else {
            finish();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.notebook_open_fail_error), Toast.LENGTH_LONG).show();
            return;
        }

        //onItemSelected method to change window layout between spinner choice (notebookview and listview)
        spinnerSetNotebookViewType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                selectedNotebook = db.getNotebookFromNotebookId(selectedNotebook.getId());
                if (selectedItem.equals(getResources().getString(R.string.notebook_list_view_text))) {         //if list view is selected,
                    if (notebookEditorViewConstraintLayout.getVisibility() == View.VISIBLE) {       //make notebookView invisible and listview visible
                        notebookEditorViewConstraintLayout.setVisibility(View.INVISIBLE);
                        fabAddNewNoteToNotebook.setVisibility(View.VISIBLE);
                        listOfNotesOfNotebook.setVisibility(View.VISIBLE);

                        constraintLayout.setBackgroundColor(getResources().getColor(R.color.darkBlue));     //set background color to dark blue
                    }
                } else if (selectedItem.equals(getResources().getString(R.string.notebook_notebook_view_text))) {        //if note editor view is selected
                    notebookEditorView = findViewById(R.id.notebookEditorView);
                    notebookEditorView.clearAllContents();          //clear all contents of notebookeditorview

                    ArrayList<String> notebookEditorViewFragments = new ArrayList<String>();
                    ArrayList<Note> notesFromNotebook = new ArrayList<Note>();
                    notesFromNotebook = db.getNotesFromNotebook(selectedNotebook);          //get all attached notes from notebook

                    if (notesFromNotebook.size() > 0) {     //if notebook has notes in it
                        constraintLayout.setBackgroundColor(getResources().getColor(R.color.white));        //change background color to white
                        for (int index = 0; index < notesFromNotebook.size(); index++) {
                            notebookEditorViewFragments.add(notesFromNotebook.get(index).getContent());
                            String lineDivider = "<hr data-tag=" + '"' + "hr" + '"' + "/>";     //insert line divider between adjacent notes.
                            if (index < notesFromNotebook.size() - 1) {           //don't insert a line divider after last note.
                                notebookEditorViewFragments.add(lineDivider);
                            }
                        }

                        for (int index = 0; index < notebookEditorViewFragments.size(); index++) {      //concatenate all html strings to build a notebook view inside editor.
                            notebookEditorViewHtmlString = notebookEditorViewHtmlString + notebookEditorViewFragments.get(index);
                        }
                        fabAddNewNoteToNotebook.setVisibility(View.INVISIBLE);          //set listview to invisible
                        listOfNotesOfNotebook.setVisibility(View.INVISIBLE);
                        notebookEditorViewConstraintLayout.setVisibility(View.VISIBLE);     //and notebookview to visible
                        notebookEditorView.render(notebookEditorViewHtmlString);        //render all concatenated html strings as editor content.
                        notebookEditorViewHtmlString = "";
                    } else {
                        //notebook has no notes in it.
                    }


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fabAddNewNoteToNotebook.setOnClickListener(new View.OnClickListener() {         //onClick listener for attach note to notebook button
            @Override
            public void onClick(View v) {
                //initialization of note selection screen for notebook
                Intent intent = new Intent(getApplicationContext(), NotebookAttachNoteActivity.class);
                intent.putExtra("NOTEBOOK_ID", selectedNotebook.getId());
                startActivityForResult(intent, REQUEST_NOTEBOOK);           //request notebook info

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editTitleButton.setOnClickListener(v -> {           //onClick listener for edit notebook title
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.notebook_edit_title_popup_view, null);       //inflate notebook edit title popup

            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            EditText notebookEditTitleEditText = (EditText) popupView.findViewById(R.id.notebookEditTitleEditText);     //initialize view components.
            Button notebookEditTitleSaveButton = (Button) popupView.findViewById(R.id.notebookEditTitleSaveButton);
            Button notebookEditTitleCancelButton = (Button) popupView.findViewById(R.id.notebookEditTitleCancelButton);

            notebookEditTitleSaveButton.setOnClickListener(v1 -> {      //onClick button for notebook edit title save button inside notebook popup
                if (!notebookEditTitleEditText.getText().toString().isEmpty()) {           //if notebook title input isn't empty
                    if (notebookEditTitleEditText.getText().toString().length() <= 10) {        //if edited text length is shorter than 11
                        db.updateNotebook(new Notebook(notebookEditTitleEditText.getText().toString()),     //update Notebook data with edited Title
                                selectedNotebook);
                        onResume();                             //to update the current screen
                        popupWindow.dismiss();                  //dispose popup
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.notebook_title_char_limit_error), Toast.LENGTH_LONG).show();
                    }

                }
            });

            notebookEditTitleCancelButton.setOnClickListener(v2 -> {        //if cancel button is clicked
                popupWindow.dismiss();          //dispose popup
            });
        });

    }

    @Override
    protected void onResume() {         //on activity resume,
        super.onResume();
        selectedNotebook = db.getNotebookFromNotebookId(selectedNotebook.getId());      //update notebookview content.
        this.setNotebookViewContent(selectedNotebook);
    }

    public void setNotebookViewContent(Notebook n) {            //function to refresh notebook view content.
        notebookNotes = db.getNotesFromNotebook(n);
        notebookAdapter = new NotebookAdapter(this, notebookNotes, n, constraintLayout, db);
        listOfNotesOfNotebook.setAdapter(notebookAdapter);
        notebookTitle.setText(n.getTitle());
    }
}
