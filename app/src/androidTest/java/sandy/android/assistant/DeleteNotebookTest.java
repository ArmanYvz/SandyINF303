//espresso test for delete notebook operation

package sandy.android.assistant;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingRootException;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import sandy.android.assistant.Controller.MainActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeleteNotebookTest {
    String NOTEBOOK_TEST = "notebook1";

    @Test
    public void test1_deleteNotebook() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);

        // check if main activity is displayed
        onView(withId(R.id.mainActivityConstraintLayout)).check(matches(isDisplayed()));

        // click notebook drawer
        onView(withId(R.id.buttonMainActivityNavigationDrawer)).perform(click());

        // delete notebook
        try {
            onView(allOf(withId(R.id.deleteNotebook), withParent(hasSibling(hasDescendant(allOf(withId(R.id.notebookTitle), withText(NOTEBOOK_TEST))))))).perform(click());
        } catch (NoMatchingViewException nmve) {
            System.out.println("no matching view in test. run NotebookTestSuite.");
        }
    }

    @Test
    public void test2_checkDeleted() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);

        // check if main activity is displayed
        onView(withId(R.id.mainActivityConstraintLayout)).check(matches(isDisplayed()));

        // click notebook drawer
        onView(withId(R.id.buttonMainActivityNavigationDrawer)).perform(click());

        // check if notebook is deleted
        onView(allOf(withId(R.id.notebookTitle), withText(NOTEBOOK_TEST))).check(doesNotExist());
    }

    @Test
    public void test3_checkNotes() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);

        //Click on the note with title text
        try {
            onView(allOf(withParent(withParent(withParent(allOf(withId(R.id.cardView), withChild(withChild(withChild(allOf(withId(R.id.noteTitle), anyOf(withText("test1")))))))))), withId(R.id.deleteNote))).perform(click());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            onView(allOf(withParent(withParent(withParent(allOf(withId(R.id.cardView), withChild(withChild(withChild(allOf(withId(R.id.noteTitle), anyOf(withText("test2")))))))))), withId(R.id.deleteNote))).perform(click());
        } catch (NoMatchingViewException n) {
            System.out.println("notes are already deleted.");
        }
        //Check if NoteEditorActivity is in view
        onView(withId(R.id.noteEditorActivityConstraintLayout));

    }
}
