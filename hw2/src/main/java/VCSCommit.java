import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a commit in VCS.
 */
class VCSCommit extends VCSObject implements Serializable, Comparable<VCSCommit> {

    private String message;
    private String author;
    private Date date;
    private List<String> parents;
    private VCSTree tree;

    private VCSCommit(@NotNull Path root, @NotNull String message, @NotNull String author, @NotNull Date date, @NotNull List<String> parents, @NotNull VCSTree tree) throws IOException {
        this.root = root.toString();
        this.message = message;
        this.author = author;
        this.date = date;
        this.parents = parents;
        this.tree = tree;
        updateHash();
        VCSObject.write(this, root);
    }

    VCSCommit(@NotNull Path root, @NotNull String message, @NotNull List<String> parents, @NotNull VCSTree tree) throws IOException {
        this(root, message, System.getProperty("user.name"), new Date(), parents, tree);
    }

    VCSCommit(@NotNull Path root, @NotNull String message, @NotNull List<String> parents) throws IOException {
        this(root, message, System.getProperty("user.name"), new Date(), parents, new VCSTree(root, root.getName(root.getNameCount() - 1).toString(), new ArrayList<>()));
    }

    @Override
    protected String getType() {
        return COMMIT;
    }

    protected void updateHash() {
        StringBuilder content = new StringBuilder();
        content.append(message);
        content.append(author);
        content.append(date);
        content.append(parents);
        content.append(tree.getHash());
        parents.forEach(content::append);
        hash = DigestUtils.sha1Hex(content.toString().getBytes());
    }

    @Override
    public int compareTo(@NotNull VCSCommit that) {
        return this.getDate().compareTo(that.getDate());
    }

    String getMessage() {
        return message;
    }

    String getAuthor() {
        return author;
    }

    Date getDate() {
        return date;
    }

    VCSTree getTree() {
        return tree;
    }

    List<VCSCommit> getLog() throws IOException {
        List<VCSCommit> result = new ArrayList<>();
        result.add(this);
        for (String hashParent : parents) {
            VCSCommit parent = (VCSCommit) VCSObject.read(Paths.get(root).resolve(VCSConstants.objectsDirectory).resolve(hashParent));
            result.addAll(parent.getLog());
        }
        return result;
    }
}
