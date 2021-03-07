//Activity Class that works as a Controller to handle user actions by calls between DatabaseManagement.java Model Class
//and note_preview_view.xml View Component

package sandy.android.assistant.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.irshulx.Editor;

import sandy.android.assistant.R;

public class NotePreviewActivity extends AppCompatActivity {
    EditText notePreviewTitle;
    Editor notePreviewRenderer;
    ImageView buttonBackToMainFromPreview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_preview_view);

        buttonBackToMainFromPreview = findViewById(R.id.buttonBackToMainActivityFromPreview);       //initialization of view components.
        notePreviewTitle = findViewById(R.id.notePreviewTitle);
        notePreviewRenderer = findViewById(R.id.notePreviewRenderer);

        Bundle b = getIntent().getExtras();         //get bundle extras from other activities
        if (b != null) {
            if (b.get("calendar_notification_note_title") != null) {              //if notification note title is returned
                notePreviewTitle.setText(b.getString("calendar_notification_note_title"));      //fill note popup view with this data
                notePreviewRenderer.render(b.getString("calendar_notification_note_content"));
            }
        }

        buttonBackToMainFromPreview.setOnClickListener(new View.OnClickListener() {         //if back button is pressed
            @Override
            public void onClick(View v) {
                finish();
            }           //finish activity
        });
    }

    @Override
    public void onBackPressed() {       //if device back button is pressed
        super.onBackPressed();
        finish();                   //finish activity
    }

    @Override
    public void onDestroy() {           //on activity close
        super.onDestroy();
        Intent intent = new Intent(this, MainActivity.class);           //start main activity
        startActivity(intent);
    }
}
