//espresso test for create note operation

package sandy.android.assistant;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
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
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CreateNoteTest {
    String TEST_TITLE = "Title text";
    String TEST_BODY = "Body\nof\ntext.";

    //Check if MainActivity is in view
    @Test
    public void test1_isMainActivityInView() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.mainActivityConstraintLayout)).check(matches(isDisplayed()));
    }

    //Create new note and check if in view
    @Test
    public void test2_clickNewNote() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);

        //Check if new note button is visible
        onView(withId(R.id.fab_create_new_note)).check(matches(isDisplayed()));

        //Click the new note button
        onView(withId(R.id.fab_create_new_note)).perform(click());

        //Check if NoteEditorActivity is in view
        onView(withId(R.id.noteEditorActivityConstraintLayout));
    }

    //Fills fields and adds a new note
    @Test
    public void test3_addNewNote() {
        ActivityScenario activityScenario = ActivityScenario.launch(NoteEditorActivity.class);

        //Type text in title
        onView(withId(R.id.noteeditor_title_text)).perform(click()).perform(typeText(TEST_TITLE));

        //Type text in body
        pressBack();

        onView(withId(R.id.noteEditorActivityConstraintLayout)).perform(clickXY(100, 600));

        typeString(TEST_BODY, R.id.editor);

        //Click save note
        onView(withId(R.id.imageView_save_note)).perform(click());
    }

    //Checks if note was added
    @Test
    public void test4_checkNote() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);

        //Click on the note with title text
        onView(withId(R.id.listOfNotes)).perform(actionOnItem(hasDescendant(withText(TEST_TITLE)), click()));

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

    //type string one character at a time with single keypresses
    public static void typeString(String string, int id) {
        string = string.toLowerCase();
        for (int i = 0; i < string.length(); i++) {
            if (Character.isLetter(string.charAt(i))) {
                onView(withId(id)).perform(pressKey(string.charAt(i) - 68));             // +28(espresso keycode) -96(ascii)
            } else if (string.charAt(i) == 10) {
                onView(withId(id)).perform(pressKey(66));                              //newline
            }
        }
    }
}