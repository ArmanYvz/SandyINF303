//Main Activity Class that initializes app and
//checks permissions and controls connection between user actions and application responds
// by calls between DatabaseManagement.java Model Class and activity_main.xml View Component

package sandy.android.assistant.Controller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import sandy.android.assistant.Adapter.MainActivityNavigationDrawerAdapter;
import sandy.android.assistant.Adapter.NoteAdapter;
import sandy.android.assistant.Listener.OnSwipeTouchListener;
import sandy.android.assistant.Model.DatabaseManagement;
import sandy.android.assistant.Model.Note;
import sandy.android.assistant.Model.Notebook;
import sandy.android.assistant.R;
import sandy.android.assistant.Service.CalendarNotificationListenerService;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class MainActivity extends AppCompatActivity {

    final int callbackId = 22;
    DatabaseManagement db;
    LinearLayoutManager linearLayoutManager;
    LinearLayoutManager notebooksLinearLayoutManager;
    ConstraintLayout mainActivityConstraintLayout;
    RecyclerView listOfNotes;
    RecyclerView listOfNotebooks;
    NavigationView notebookNavigationView;
    ImageView mainActivityNavigationViewImageView;
    FloatingActionButton fab_create_new_note;
    ImageView buttonShowNotification;
    ImageButton buttonChangeLanguage;

    public View popupView;     //notebook popup attributes
    public EditText notebookPopupEditText;
    public Button notebookPopupCreateButton;
    public Button notebookPopupCancelButton;
    LayoutInflater inflater;
    PopupWindow popupWindow;

    Button buttonAddNotebook;
    ArrayList<Note> notes;
    ArrayList<Notebook> notebooks;
    NoteAdapter noteAdapter;
    MainActivityNavigationDrawerAdapter mainActivityNavigationDrawerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        checkNotificationListenerServicePermissions();      //checks notification permission

        Intent calendarNotificationListenerService = new Intent(this, CalendarNotificationListenerService.class);
        this.startService(calendarNotificationListenerService);     //starts notification listener service at startup

        createNotificationChannel();
        //calls function that grants permissions for device interactions.
        checkPermission(callbackId, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);     //calendar permission check

        db = new DatabaseManagement(this);      //database access object instance

        fab_create_new_note = (FloatingActionButton) findViewById(R.id.fab_create_new_note);        //initializations for activity_main.xml views

        mainActivityConstraintLayout = (ConstraintLayout) findViewById(R.id.mainActivityConstraintLayout);
        mainActivityNavigationViewImageView = (ImageView) findViewById(R.id.buttonMainActivityNavigationDrawer);
        notebookNavigationView = (NavigationView) findViewById(R.id.mainActivityNavigationView);

        buttonShowNotification = findViewById(R.id.showNotification);
        buttonAddNotebook = findViewById(R.id.buttonAddNotebook);
        buttonChangeLanguage = findViewById(R.id.button_change_language);

        listOfNotes = findViewById(R.id.listOfNotes);
        listOfNotebooks = findViewById(R.id.mainActivityListOfNotebooks);


        notes = db.getAllNotes();
        noteAdapter = new NoteAdapter(this, notes, db, this);       //create new Adapter to fetch Notes from DB and to show them in Cardview inside Recycleview
        listOfNotes.setAdapter(noteAdapter);

        notebooks = db.getAllNotebooks();
        mainActivityNavigationDrawerAdapter = new MainActivityNavigationDrawerAdapter(this, notebooks, db);
        listOfNotebooks.setAdapter(mainActivityNavigationDrawerAdapter);

        linearLayoutManager = new LinearLayoutManager(this);        //defines LinearLayoutManager for vertical Notes Recycleview orientation
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listOfNotes.setLayoutManager(linearLayoutManager);

        notebooksLinearLayoutManager = new LinearLayoutManager(this);
        notebooksLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listOfNotebooks.setLayoutManager(notebooksLinearLayoutManager);

        ConstraintLayout mainActivityConstraintLayout = findViewById(R.id.mainActivityConstraintLayout);

        buttonAddNotebook.setOnClickListener(new View.OnClickListener() {           //if add notebook button is clicked
            @Override
            public void onClick(View view) {
                // inflate the layout of the popup window
                inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                popupView = inflater.inflate(R.layout.notebook_popup_view, null);       //inflates notebook popup to create notebooks

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;
                popupWindow = new PopupWindow(popupView, width, height, focusable);

                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                notebookPopupEditText = (EditText) popupView.findViewById(R.id.notebookPopupEditText);              //initialize popup windows views
                notebookPopupCreateButton = (Button) popupView.findViewById(R.id.notebookPopupCreateButton);
                notebookPopupCancelButton = (Button) popupView.findViewById(R.id.notebookPopupCancelButton);

                notebookPopupCreateButton.setOnClickListener(new View.OnClickListener() {       //onClick listener for notebook popup create button
                    @Override
                    public void onClick(View v) {
                        if (!notebookPopupEditText.getText().toString().equals("")) {           //if notebook title input isn't empty
                            if (notebookPopupEditText.getText().toString().length() <= 10) {
                                Notebook newNotebook = new Notebook(notebookPopupEditText.getText().toString());
                                db.insertNotebook(newNotebook);         //insert a new notebook into database with input title
                                notebookNavigationView.setVisibility(View.INVISIBLE);       //close navigation view
                                refreshNotebookNavigationView();        //refresh navigation view
                                popupWindow.dismiss();                  //dispose popup
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.notebook_title_char_limit_error), Toast.LENGTH_LONG).show();      //don't proceed if notebook title is longer than 10 characters.
                            }

                        }
                    }
                });

                notebookPopupCancelButton.setOnClickListener(new View.OnClickListener() {            //if notebook popup cancel button is clicked
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();          //dispose popup
                    }
                });
            }
        });

        buttonShowNotification.setOnClickListener(new View.OnClickListener() {      //button listener to open notifications menu
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NotificationViewActivity.class);
                startActivity(intent);
            }
        });

        buttonChangeLanguage.setOnClickListener(l -> {            //button listener that pops up change language screen
            Intent intent = new Intent(getApplicationContext(), ChangeLanguageActivity.class);
            startActivityForResult(intent, 0);
        });

        fab_create_new_note.setOnClickListener(new View.OnClickListener() {     //floating action button listener that creates a blank note editor screen.
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NoteEditorActivity.class);        //creates new intent that opens up note_editor.xml screen and runs NoteEditorActivity.java
                startActivity(intent);
            }
        });

        listOfNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notebookNavigationView.getVisibility() == View.VISIBLE) {
                    notebookNavigationView.setVisibility(View.INVISIBLE);
                }
            }
        });

        listOfNotes.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {            //creates a set on swipe touch listener for main menu
            @Override
            public void onSwipeRight() {
                if (notebookNavigationView.getVisibility() == View.INVISIBLE) {
                    notebookNavigationView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSwipeLeft() {
                if (notebookNavigationView.getVisibility() == View.VISIBLE) {
                    notebookNavigationView.setVisibility(View.INVISIBLE);
                }
            }
        });

        mainActivityConstraintLayout.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {          //creates a set on swipe touch listener for main menu
            @Override
            public void onSwipeRight() {
                if (notebookNavigationView.getVisibility() == View.INVISIBLE) {
                    notebookNavigationView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSwipeLeft() {
                if (notebookNavigationView.getVisibility() == View.VISIBLE) {
                    notebookNavigationView.setVisibility(View.INVISIBLE);
                }
            }
        });

        mainActivityNavigationViewImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notebookNavigationView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {     //on activity resume
        super.onResume();

        notes = db.getAllNotes();
        noteAdapter = new NoteAdapter(this, notes, db, this);       //create a new Note Adapter
        listOfNotes.setAdapter(noteAdapter);

        refreshNotebookNavigationView();        //refresh Navigation View with new Notebook data
    }

    @Override
    public void onBackPressed() {       //overrides back button behavior
        if (notebookNavigationView.getVisibility() == View.VISIBLE) {       //if navigation drawer is opened
            notebookNavigationView.setVisibility(View.INVISIBLE);           //simply close it
        } else {
            super.onBackPressed();      //if navigation drawer is already closed, apply primary activity behavior
        }
    }

    @Override
    protected void onDestroy() {        //on application close
        super.onDestroy();
        Intent calendarNotificationListenerService = new Intent(this, CalendarNotificationListenerService.class);       //create notification listener service
        this.startService(calendarNotificationListenerService);     //start notification listener service
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {       //on change language activity result
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                switch (resultCode) {
                    case RESULT_OK:         //if language change operation is saved,
                        Intent intent = getIntent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);         //refresh the application screen
                        finish();
                        startActivity(intent);
                        break;
                    case RESULT_CANCELED:       //if language change operation is canceled
                        break;                  //simply do nothing
                }
                break;
        }
    }

    public void startNoteEditorActivity() {        //function that starts note editor activity
        Intent intent = new Intent(getApplicationContext(), NoteEditorActivity.class);        //creates new intent that opens up note_editor.xml screen and runs NoteEditorActivity.java
        startActivity(intent);
    }

    public void refreshNotebookNavigationView() {      //function to refresh notebook navigation view. Useful to refresh list data
        notebooks = db.getAllNotebooks();
        mainActivityNavigationDrawerAdapter = new MainActivityNavigationDrawerAdapter(this, notebooks, db);
        listOfNotebooks.setAdapter(mainActivityNavigationDrawerAdapter);

    }

    private void checkPermission(int callbackId, String... permissionsId) {     //checks if selected application permissions exist, requests if missed.
        boolean permissions = true;
        for (String p : permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(this, p) == PERMISSION_GRANTED;
        }

        if (!permissions)
            ActivityCompat.requestPermissions(this, permissionsId, callbackId);
    }

    public void createNotificationChannel() {       //function to create a notification channel for notification events
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_name), name, importance);
            channel.setDescription(description);
            channel.setLightColor(Color.BLUE);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void checkNotificationListenerServicePermissions() {        //function that checks notification listener permissions requests if required
        if (isPermissionRequired()) {
            requestNotificationPermission();
        }
    }

    public boolean isPermissionRequired() {         //function that enables notification listener service permissions.
        ComponentName cn = new ComponentName(this, CalendarNotificationListenerService.class);
        String flat = Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners");
        final boolean enabled = flat != null && flat.contains(cn.flattenToString());
        return !enabled;
    }

    private void requestNotificationPermission() {          //function that requests notification listener permission
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivityForResult(intent, 101);
    }
}
