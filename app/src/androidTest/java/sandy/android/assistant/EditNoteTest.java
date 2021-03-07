//espresso test for edit note operation

package sandy.android.assistant;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;
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
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static android.view.KeyEvent.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static sandy.android.assistant.CreateNoteTest.typeString;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EditNoteTest {
    String TEST_TITLE = "Title text";
    String TEST_BODY = "Body\nof\ntext.";
    String EDIT_TITLE = "Edited title";
    String EDIT_BODY = "Text\nwas\nedited.";

    //Check if MainActivity is in view
    @Test
    public void test1_isMainActivityInView() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.mainActivityConstraintLayout)).check(matches(isDisplayed()));
    }

    //Edits note
    @Test
    public void test2_editNote() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }

        try {
            onView(allOf(withParent(withParent(withParent(allOf(withId(R.id.cardView), withChild(withChild(withChild(allOf(withId(R.id.noteTitle), anyOf(withText(EDIT_TITLE)))))))))), withId(R.id.deleteNote))).perform(click());
        } catch (Exception exc) {

        }
        //Click on the note with title text
        try {
            onView(withId(R.id.listOfNotes)).perform(actionOnItem(hasDescendant(withText(TEST_TITLE)), click()));
        } catch (Exception e) {
            //Check if new note button is visible
            onView(withId(R.id.fab_create_new_note)).check(matches(isDisplayed()));

            //Click the new note button
            onView(withId(R.id.fab_create_new_note)).perform(click());

            //Check if NoteEditorActivity is in view
            onView(withId(R.id.noteEditorActivityConstraintLayout));

            activityScenario = ActivityScenario.launch(NoteEditorActivity.class);

            //Type text in title
            onView(withId(R.id.noteeditor_title_text)).perform(click()).perform(typeText(TEST_TITLE));

            //Type text in body
            pressBack();

            onView(withId(R.id.noteEditorActivityConstraintLayout)).perform(clickXY(100, 600));

            typeString(TEST_BODY, R.id.editor);

            //Click save note
            onView(withId(R.id.imageView_save_note)).perform(click());

            activityScenario = ActivityScenario.launch(MainActivity.class);

            //Click on the note with title text
            onView(withId(R.id.listOfNotes)).perform(actionOnItem(hasDescendant(withText(TEST_TITLE)), click()));

            //Check if NoteEditorActivity is in view
            onView(withId(R.id.noteEditorActivityConstraintLayout));
            try {
                Thread.sleep(1000);
            } catch (Exception ex) {

            }
            activityScenario = ActivityScenario.launch(MainActivity.class);

            onView(withId(R.id.mainActivityConstraintLayout)).check(matches(isDisplayed()));
            onView(withId(R.id.listOfNotes)).perform(actionOnItem(hasDescendant(withText(TEST_TITLE)), click()));
        }


        //Check if NoteEditorActivity is in view
        onView(withId(R.id.noteEditorActivityConstraintLayout));

        //Edit text in title
        onView(withId(R.id.noteeditor_title_text)).perform(replaceText(EDIT_TITLE));

        //Edit text in body
        onView(withId(R.id.noteEditorActivityConstraintLayout)).perform(clickXY(100, 600));

        clearText(R.id.editor);
        typeString(EDIT_BODY, R.id.editor);

        //Click save note
        onView(withId(R.id.imageView_save_note)).perform(click());
    }

    //Checks if note was edited
    @Test
    public void test3_checkNote() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);

        //Click on the note with edited title text
        onView(withId(R.id.listOfNotes)).perform(actionOnItem(hasDescendant(withText(EDIT_TITLE)), click()));

        //Check if NoteEditorActivity is in view
        onView(withId(R.id.noteEditorActivityConstraintLayout));
    }

    // https://stackoverflow.com/a/22798043
    public static ViewAction clickXY(final int x, final int y) {
        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);

                        final float screenX = screenPos[0] + x;
                        final float screenY = screenPos[1] + y;
                        float[] coordinates = {screenX, screenY};

                        return coordinates;
                    }
                },
                Press.FINGER);
    }

    //clear text in focus
    private void clearText(int id) {
        onView(withId(id)).perform(pressKey(KEYCODE_SYSTEM_NAVIGATION_DOWN));
        onView(withId(id)).perform(pressKey(KEYCODE_SYSTEM_NAVIGATION_DOWN));
        onView(withId(id)).perform(pressKey(KEYCODE_MOVE_END));
        for (int i = 0; i < 30; i++) {
            onView(withId(id)).perform(pressKey(KEYCODE_DEL));
        }
    }
}
