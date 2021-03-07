//Activity Class that works as a Controller to handle user actions by calls between DatabaseManagement.java Model Class
//and notebook_attach_note_view.xml View Component

package sandy.android.assistant.Controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.irshulx.Editor;

import java.util.ArrayList;

import sandy.android.assistant.Model.DatabaseManagement;
import sandy.android.assistant.Model.Note;
import sandy.android.assistant.Model.Notebook;
import sandy.android.assistant.Adapter.NotebookAttachNoteAdapter;
import sandy.android.assistant.R;

public class NotebookAttachNoteActivity extends AppCompatActivity {

    DatabaseManagement db;
    LinearLayoutManager linearLayoutManager;
    RecyclerView listOfNotesToAttach;
    ArrayList<Note> notesWithNoNotebookAttached;
    NotebookAttachNoteAdapter notebookAttachNoteAdapter;

    Button attachSelectedNotesButton;
    Button cancelAttachButton;

    Notebook selectedNotebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notebook_attach_note_view);

        db = new DatabaseManagement(this);      //database access object initialization

        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.get("NOTEBOOK_ID") != null) {
                selectedNotebook = db.getNotebookFromNotebookId(b.getInt("NOTEBOOK_ID"));
            } else {
                finish();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.operation_failed), Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            finish();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.operation_failed), Toast.LENGTH_LONG).show();
            return;
        }

        listOfNotesToAttach = findViewById(R.id.listOfNotesToAttach);
        attachSelectedNotesButton = findViewById(R.id.buttonAttachSelectedNotes);
        cancelAttachButton = findViewById(R.id.buttonCancelAttach);

        notesWithNoNotebookAttached = db.getAllNotesExceptCurrentNotebook(selectedNotebook);
        notebookAttachNoteAdapter = new NotebookAttachNoteAdapter(this, notesWithNoNotebookAttached, db);
        listOfNotesToAttach.setAdapter(notebookAttachNoteAdapter);

        linearLayoutManager = new LinearLayoutManager(this);        //defines LinearLayoutManager for vertical Notes Recycleview orientation
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listOfNotesToAttach.setLayoutManager(linearLayoutManager);


        attachSelectedNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ArrayList> notesWithCheckBoxesSelected = notebookAttachNoteAdapter.checkedItemsList;


                for (int index = 0; index < notesWithCheckBoxesSelected.size(); index++) {
                    for (int index2 = 0; index2 < notesWithCheckBoxesSelected.get(index).size(); index2++) {
                        db.addNoteToNotebook(notebookAttachNoteAdapter.getNotesToAttachList().get((Integer) notesWithCheckBoxesSelected.get(index).get(index2)), selectedNotebook);
                        finish();
                    }
                }
            }
        });

        cancelAttachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
