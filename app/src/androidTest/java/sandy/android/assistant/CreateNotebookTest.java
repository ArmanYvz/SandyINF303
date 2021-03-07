//espresso test for create notebook operation

package sandy.android.assistant;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import sandy.android.assistant.Controller.MainActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CreateNotebookTest {
    String NOTEBOOK_TITLE = "notebook1";

    @Test
    public void test1_createNotebook() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);

        // check if main activity is displayed
        onView(withId(R.id.mainActivityConstraintLayout)).check(matches(isDisplayed()));

        // click notebook drawer
        onView(withId(R.id.buttonMainActivityNavigationDrawer)).perform(click());

        // click new notebook
        onView(withId(R.id.buttonAddNotebook)).perform(click());

        // type title
        onView(withId(R.id.notebookPopupEditText)).perform(click()).perform(typeText(NOTEBOOK_TITLE));

        // click create
        pressBack();
        onView(withId(R.id.notebookPopupCreateButton)).perform(click());
    }

    @Test
    public void test2_checkNotebook() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);

        // check if main activity is displayed
        onView(withId(R.id.mainActivityConstraintLayout)).check(matches(isDisplayed()));

        // click notebook drawer
        onView(withId(R.id.buttonMainActivityNavigationDrawer)).perform(click());

        // check if notebook exists
        onView(allOf(withId(R.id.notebookTitle), withText(NOTEBOOK_TITLE))).check(matches(isDisplayed()));

    }


}
