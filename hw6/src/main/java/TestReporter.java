import org.jetbrains.annotations.NotNull;

/**
 * Report to the test.
 */
public class TestReporter {
    private final String testName;
    private final String result;
    private final long time;

    public TestReporter(@NotNull String testName, @NotNull String result, long time) {
        this.testName = testName;
        this.result = result;
        this.time = time;
    }

    public TestReporter(@NotNull String testName, @NotNull String result) {
        this.testName = testName;
        this.result = result;
        this.time = -1;
    }

    /**
     * Returns name of the test.
     * @return name
     */
    public String getTestName() {
        return testName;
    }

    /**
     * Returns result of the test.
     * @return result
     */
    public String getResult() {
        return result;
    }

    /**
     * Gets all the information from the report.
     * @return information
     */
    public String getInfo() {
        String response = testName + ": " + result;
        if (time != -1) {
            response += " in time " + Long.toString(time) + "\n";
        }
        return response;
    }
}
