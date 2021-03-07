//notebook test suite that performs all notebook espresso tests in order

package sandy.android.assistant;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CreateNotebookTest.class,
        EditNotebookTest.class,
        DeleteNotebookTest.class
})
public class NotebookTestSuite {
}
