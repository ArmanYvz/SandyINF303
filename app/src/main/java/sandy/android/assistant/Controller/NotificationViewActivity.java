//Activity Class that works as a Controller to handle user actions by calls between DatabaseManagement.java Model Class
//and notification_recycler_view.xml View Component

package sandy.android.assistant.Controller;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sandy.android.assistant.Model.DatabaseManagement;
import sandy.android.assistant.Model.Note;
import sandy.android.assistant.Adapter.NotificationAdapter;
import sandy.android.assistant.R;

public class NotificationViewActivity extends AppCompatActivity {

    DatabaseManagement db;
    LinearLayoutManager linearLayoutManager;
    RecyclerView listOfNotifications;
    ArrayList<Note> notes;
    ArrayList<Note> notesWithNotification;
    NotificationAdapter notificationAdapter;

    ImageView buttonBackToMainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_recycler_view);

        buttonBackToMainActivity = findViewById(R.id.buttonBackToMainActivity);     //initialization of view components
        listOfNotifications = findViewById(R.id.listOfNotifications);

        db = new DatabaseManagement(this);       //database access object initialization


        linearLayoutManager = new LinearLayoutManager(this);        //defines LinearLayoutManager for vertical Recycleview orientation
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listOfNotifications.setLayoutManager(linearLayoutManager);

        buttonBackToMainActivity.setOnClickListener(new View.OnClickListener() {        //if back button is clicked
            @Override
            public void onClick(View v) {
                finish();           //finish activity and go back to main activity screen
            }
        });

    }

    @Override
    protected void onResume() {     //onActivityresume
        super.onResume();

        notes = db.getAllNotes();       //retrieve db data from database access object
        notesWithNotification = new ArrayList<Note>();

        for (int index = 0; index < notes.size(); index++) {
            if (notes.get(index).getNotification() != null) {
                notesWithNotification.add(notes.get(index));
            }
        }

        notificationAdapter = new NotificationAdapter(this, notesWithNotification, db);
        listOfNotifications.setAdapter(notificationAdapter);            //refresh notification view
    }
}
