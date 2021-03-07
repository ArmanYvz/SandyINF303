//espresso test for delete notification operation

package sandy.android.assistant;

import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.button.MaterialButton;

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
import static androidx.test.espresso.contrib.PickerActions.setDate;
import static androidx.test.espresso.contrib.PickerActions.setTime;
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
import static org.hamcrest.Matchers.instanceOf;
import static sandy.android.assistant.CreateNoteTest.clickXY;
import static sandy.android.assistant.CreateNoteTest.typeString;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeleteNotificationTest {
    String TEST_TITLE = "notification test";
    String TEST_BODY = "Body\nof\ntext.";

    @Test
    public void test1_deleteNotification() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);

        // click notifications
        onView(withId(R.id.showNotification)).perform(click());

        // delete notification
        try {
            onView(allOf(withId(R.id.deleteNotification), withParent(hasSibling(hasDescendant(withText(TEST_TITLE)))))).perform(click());
        } catch (Exception e) {
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

            // select note
            onView(withId(R.id.listOfNotes)).perform(actionOnItem(hasDescendant(withText(TEST_TITLE)), click()));

            // click add content button
            onView(withId(R.id.fab_noteeditor_options)).perform(click());

            // click add notification button
            onView(withId(R.id.fab_noteeditor_options_timer)).perform(click());

            // click set date
            onView(withId(R.id.datePickerButton)).perform(click());

            // set date
            onView(instanceOf(DatePicker.class)).perform(setDate(2025, 1, 30));
            onView(allOf(instanceOf(MaterialButton.class), anyOf(withText("OK"), withText("TAMAM")))).perform(click());

            // click set time
            onView(withId(R.id.timePickerButton)).perform(click());

            // set time
            onView(instanceOf(TimePicker.class)).perform(setTime(9, 30));
            onView(allOf(instanceOf(MaterialButton.class), anyOf(withText("OK"), withText("TAMAM")))).perform(click());

            // save notification
            onView(withId(R.id.saveNotificationButton)).perform(click());
            onView(withId(R.id.imageView_save_note)).perform(click());

            activityScenario = ActivityScenario.launch(MainActivity.class);

            onView(withId(R.id.showNotification)).perform(click());

            onView(allOf(withId(R.id.deleteNotification), withParent(hasSibling(hasDescendant(withText(TEST_TITLE)))))).perform(click());
        }

    }

    @Test
    public void test2_checkNotification() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);

        // click notifications
        onView(withId(R.id.showNotification)).perform(click());

        // check that does not exist
        onView(hasDescendant(withText(TEST_TITLE))).check(doesNotExist());

    }

    @Test
    public void test3_checkNote() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);

        //Click on the note with title text
        onView(allOf(withParent(withParent(withParent(allOf(withId(R.id.cardView), withChild(withChild(withChild(allOf(withId(R.id.noteTitle), anyOf(withText(TEST_TITLE)))))))))), withId(R.id.deleteNote))).perform(click());

        //Check if NoteEditorActivity is in view
        onView(withId(R.id.noteEditorActivityConstraintLayout));

    }
}
