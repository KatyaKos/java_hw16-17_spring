import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * VCS files and directories.
 */
class VCSConstants {
    static final Path myVCSDirectory = Paths.get(".myvcs");
    static final Path objectsDirectory = myVCSDirectory.resolve("VCSObjects");
    static final Path branchesDirectory = myVCSDirectory.resolve("branches");
    static final Path logsDirectory = myVCSDirectory.resolve("logs");
    static final Path index = myVCSDirectory.resolve("index");
    static final Path head = myVCSDirectory.resolve("HEAD");
}
