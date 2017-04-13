import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Represents a branch in VCS.
 */
class VCSBranch extends VCSObject implements Serializable {

    private String name;
    private String commit;

    VCSBranch(@NotNull Path root, @NotNull String name, @NotNull String commit) throws IOException {
        this.root = root.toString();
        this.name = name;
        this.commit = commit;
        updateHash();
        VCSObject.write(this, root);
    }

    @Override
    protected String getType() {
        return BRANCH;
    }

    String getName() {
        return name;
    }

    String getCommit() {
        return commit;
    }

    void setCommit(@NotNull String commit) throws IOException {
        this.commit = commit;
        updateHash();
        VCSObject.write(this, Paths.get(root));
    }

    protected void updateHash() {
        hash = DigestUtils.sha1Hex((name + commit).getBytes());
    }
}
