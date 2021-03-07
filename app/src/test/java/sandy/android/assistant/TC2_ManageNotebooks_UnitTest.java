package sandy.android.assistant;

import android.os.Build;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.test.core.app.ApplicationProvider;

import com.github.irshulx.Editor;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import sandy.android.assistant.Controller.MainActivity;
import sandy.android.assistant.Controller.NoteEditorActivity;
import sandy.android.assistant.Model.DatabaseManagement;
import sandy.android.assistant.Model.Note;
import sandy.android.assistant.Model.Notebook;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})

public class TC2_ManageNotebooks_UnitTest {

    DatabaseManagement db;

    @Before
    public void setUp() throws Exception {
        db = new DatabaseManagement(ApplicationProvider.getApplicationContext());
        Assert.assertNotNull(db);
    }

    @Test
    public void dbSaveCreatedNotebookTest() throws Exception {

        Notebook notebook = new Notebook("test");
        db.insertNotebook(notebook);

        db.close();

        ArrayList<Notebook> notebooksFromDB = new ArrayList<Notebook>();

        notebooksFromDB = db.getAllNotebooks();

        db.close();

        Assert.assertEquals("test", notebooksFromDB.get(0).getTitle());

        db.deleteNotebook(notebooksFromDB.get(0));

        db.close();

        notebooksFromDB = db.getAllNotebooks();

        Assert.assertTrue(notebooksFromDB.size() == 0);

    }

    @After
    public void closeDatabase() throws Exception {
        db.close();
    }

}