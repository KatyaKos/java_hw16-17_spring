import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains lists for different types of files. Five possible statuses:
 * modified - file was changed since head commit
 * deleted - file was deleted from disk
 * unversioned - file that is not staged for commit or contained in head commit
 * unmodified - file was not changed since head commit
 * staged - file is staged for commit
 */
public class VCS_Status {
    private List<Path> modifiedFiles = new ArrayList<>();
    private List<Path> unmodifiedFiles = new ArrayList<>();
    private List<Path> deletedFiles = new ArrayList<>();
    private List<Path> unversionedFiles = new ArrayList<>();
    private List<Path> staged = new ArrayList<>();

    public List<Path> getModified() {
        return modifiedFiles;
    }

    public List<Path> getDeleted() {
        return deletedFiles;
    }

    public List<Path> getUnversioned() {
        return unversionedFiles;
    }

    public List<Path> getUnmodified() {
        return unmodifiedFiles;
    }

    public List<Path> getStaged() {
        return staged;
    }

    void addModified(@NotNull Path path) {
        modifiedFiles.add(path);
    }

    void addUnmodified(@NotNull Path path) {
        unmodifiedFiles.add(path);
    }


    void addDeleted(@NotNull Path path) {
        deletedFiles.add(path);
    }

    void addUnversioned(@NotNull Path path) {
        unversionedFiles.add(path);
    }

    void addStaged(@NotNull Path path) {
        staged.add(path);
    }
}
