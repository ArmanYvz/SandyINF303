//espresso test for edit notebook operation

package sandy.android.assistant;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import sandy.android.assistant.Controller.MainActivity;
import sandy.android.assistant.Controller.NoteEditorActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static sandy.android.assistant.CreateNoteTest.clickXY;
import static sandy.android.assistant.CreateNoteTest.typeString;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EditNotebookTest {
    String NOTEBOOK_TITLE = "notebook1";
    String TEST_TITLE1 = "test1";
    String TEST_TITLE2 = "test2";
    String TEST_BODY = "Body\nof\ntext.";

    @Test
    public void test1_addNewNote() {

        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);

        // check if main activity is displayed
        onView(withId(R.id.mainActivityConstraintLayout)).check(matches(isDisplayed()));

        try {
            onView(withId(R.id.listOfNotes)).perform(actionOnItem(hasDescendant(withText(TEST_TITLE1)), click()));
            activityScenario = ActivityScenario.launch(MainActivity.class);
            //onView(allOf(withParent(withParent(withParent(allOf(withId(R.id.cardView), withChild(withChild(withChild(allOf(withId(R.id.noteTitle), anyOf(withText(TEST_TITLE1)))))))))), withId(R.id.deleteNote))).perform(click());
        } catch (Exception exc) {
            //Check if new note button is visible
            onView(withId(R.id.fab_create_new_note)).check(matches(isDisplayed()));

            //Click the new note button
            onView(withId(R.id.fab_create_new_note)).perform(click());

            //Check if NoteEditorActivity is in view
            onView(withId(R.id.noteEditorActivityConstraintLayout));

            //Type text in title
            onView(withId(R.id.noteeditor_title_text)).perform(click()).perform(typeText(TEST_TITLE1));

            //Type text in body
            pressBack();

            onView(withId(R.id.noteEditorActivityConstraintLayout)).perform(clickXY(100, 600));

            typeString(TEST_BODY, R.id.editor);

            //Click save note
            onView(withId(R.id.imageView_save_note)).perform(click());
        }

        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }

        // check if main activity is displayed
        onView(withId(R.id.mainActivityConstraintLayout)).check(matches(isDisplayed()));

        try {
            onView(withId(R.id.listOfNotes)).perform(actionOnItem(hasDescendant(withText(TEST_TITLE2)), click()));
        } catch (Exception excep) {
            //Check if new note button is visible
            onView(withId(R.id.fab_create_new_note)).check(matches(isDisplayed()));

            //Click the new note button
            onView(withId(R.id.fab_create_new_note)).perform(click());

            //Check if NoteEditorActivity is in view
            onView(withId(R.id.noteEditorActivityConstraintLayout));

            //Type text in title
            onView(withId(R.id.noteeditor_title_text)).perform(click()).perform(typeText(TEST_TITLE2));

            //Type text in body
            pressBack();

            onView(withId(R.id.noteEditorActivityConstraintLayout)).perform(clickXY(100, 600));

            typeString(TEST_BODY, R.id.editor);

            //Click save note
            onView(withId(R.id.imageView_save_note)).perform(click());
        }

    }

    @Test
    public void test2_attachNotes() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);

        // check if main activity is displayed
        onView(withId(R.id.mainActivityConstraintLayout)).check(matches(isDisplayed()));

        // click notebook drawer
        onView(withId(R.id.buttonMainActivityNavigationDrawer)).perform(click());

        // select notebook
        try {
            onView(allOf(withId(R.id.cardView), hasDescendant(allOf(withId(R.id.notebookTitle), withText(NOTEBOOK_TITLE))))).perform(click());
        } catch (NoMatchingViewException nm) {
            // click new notebook
            onView(withId(R.id.buttonAddNotebook)).perform(click());

            // type title
            onView(withId(R.id.notebookPopupEditText)).perform(click()).perform(typeText(NOTEBOOK_TITLE));

            // click create
            pressBack();
            onView(withId(R.id.notebookPopupCreateButton)).perform(click());

            // click notebook drawer
            onView(withId(R.id.buttonMainActivityNavigationDrawer)).perform(click());

            onView(allOf(withId(R.id.cardView), hasDescendant(allOf(withId(R.id.notebookTitle), withText(NOTEBOOK_TITLE))))).perform(click());

        }

        // click attach note
        onView(withId(R.id.fabAddNewNoteToNotebook)).perform(click());
        // check note1
        onView(allOf(withId(R.id.noteToAttachCheckBox), withParent(hasSibling(hasDescendant(allOf(withId(R.id.noteToAttachTitle), withText(TEST_TITLE1))))))).perform(click());
        // check note2
        onView(allOf(withId(R.id.noteToAttachCheckBox), withParent(hasSibling(hasDescendant(allOf(withId(R.id.noteToAttachTitle), withText(TEST_TITLE2))))))).perform(click());
        // click attach
        onView(withId(R.id.buttonAttachSelectedNotes)).perform(click());
    }

    @Test
    public void test3_checkAttached() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);

        // check if main activity is displayed
        onView(withId(R.id.mainActivityConstraintLayout)).check(matches(isDisplayed()));

        // click notebook drawer
        onView(withId(R.id.buttonMainActivityNavigationDrawer)).perform(click());

        // select notebook
        onView(allOf(withId(R.id.cardView), hasDescendant(allOf(withId(R.id.notebookTitle), withText(NOTEBOOK_TITLE))))).perform(click());

        // check note1
        onView(allOf(withId(R.id.noteOfNotebookTitle), withText(TEST_TITLE1))).check(matches(isDisplayed()));

        // check note2
        onView(allOf(withId(R.id.noteOfNotebookTitle), withText(TEST_TITLE2))).check(matches(isDisplayed()));
    }

    @Test
    public void test4_removeNotes() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);

        // check if main activity is displayed
        onView(withId(R.id.mainActivityConstraintLayout)).check(matches(isDisplayed()));

        // click notebook drawer
        onView(withId(R.id.buttonMainActivityNavigationDrawer)).perform(click());

        // select notebook
        onView(allOf(withId(R.id.cardView), hasDescendant(allOf(withId(R.id.notebookTitle), withText(NOTEBOOK_TITLE))))).perform(click());

        // remove note 1
        onView(allOf(withId(R.id.notesOfNotebookDelete), withParent(hasSibling(hasDescendant(allOf(withId(R.id.noteOfNotebookTitle), withText(TEST_TITLE1))))))).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(allOf(withId(R.id.notesOfNotebookDelete), withParent(hasSibling(hasDescendant(allOf(withId(R.id.noteOfNotebookTitle), withText(TEST_TITLE2))))))).perform(click());
    }

    @Test
    public void test5_checkNotesRemoved() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);

        // check if main activity is displayed
        onView(withId(R.id.mainActivityConstraintLayout)).check(matches(isDisplayed()));

        // click notebook drawer
        onView(withId(R.id.buttonMainActivityNavigationDrawer)).perform(click());

        // select notebook
        onView(allOf(withId(R.id.cardView), hasDescendant(allOf(withId(R.id.notebookTitle), withText(NOTEBOOK_TITLE))))).perform(click());

        onView(allOf(withId(R.id.noteOfNotebookTitle), withText(TEST_TITLE1))).check(doesNotExist());
    }
}
