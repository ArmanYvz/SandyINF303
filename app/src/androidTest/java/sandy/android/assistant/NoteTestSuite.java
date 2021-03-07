//note test suite that performs all note espresso tests in order

package sandy.android.assistant;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CreateNoteTest.class,
        EditNoteTest.class,
        DeleteNoteTest.class
})
public class NoteTestSuite {
}
