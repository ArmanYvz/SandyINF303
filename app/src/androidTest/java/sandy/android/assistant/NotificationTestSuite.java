//notification test suite that performs all notification espresso tests in order

package sandy.android.assistant;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CreateNotificationTest.class,
        EditNotificationTest.class,
        DeleteNotificationTest.class
})
public class NotificationTestSuite {
}
