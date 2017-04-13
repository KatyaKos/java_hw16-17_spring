import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Represents a directory in VCS.
 */
class VCSTree extends VCSObject implements Serializable {

    private List<String> children;
    private String directoryName;

    VCSTree(@NotNull Path root, @NotNull String directoryName, @NotNull List<String> children)
            throws IOException {
        this.root = root.toString();
        this.directoryName = directoryName;
        this.children = children;
        updateHash();
        VCSObject.write(this, root);
    }

    private VCSTree(@NotNull Path root, @NotNull String directoryName) throws IOException {
        this.root = root.toString();
        this.directoryName = directoryName;
        children = new ArrayList<>();
        updateHash();
        VCSObject.write(this, root);
    }

    @Override
    protected String getType() {
        return TREE;
    }

    protected void updateHash() {
        StringBuilder content = new StringBuilder();
        content.append(directoryName);
        children.forEach(content::append);
        hash = DigestUtils.sha1Hex(content.toString().getBytes());
    }

    private String getDirectoryName() {
        return directoryName;
    }

    /**
     * Makes VCSTree equal to the VCSTree where the method was called with added file.
     * @param path location of the file that we want to add
     * @param hash hash of the file
     * @return new VCSTree
     * @throws IOException if IO went wrong
     */
    VCSTree addPathToTree(@NotNull Path path, @NotNull String hash) throws IOException {
        if (path.getNameCount() == 0) {
            throw new IllegalArgumentException();
        } else if (path.getNameCount() == 1) {
            VCSBlob blob = (VCSBlob) getChild(hash);
            List<String> newChildren = new ArrayList<>();
            for (String childHash : children) {
                VCSObject child = getChild(childHash);
                if (!child.getType().equals(VCSObject.BLOB) ||
                        !((VCSBlob)child).getFileName().equals(blob.getFileName())) {
                    newChildren.add(childHash);
                }
            }
            newChildren.add(hash);
            return new VCSTree(Paths.get(root), directoryName, newChildren);
        } else {
            List<String> newChildren = new ArrayList<>();
            boolean foundInTree = false;
            String directory = path.getName(0).toString();
            for (String childHash : children) {
                VCSObject child = getChild(childHash);
                if (child.getType().equals(VCSObject.TREE) &&
                        ((VCSTree)child).getDirectoryName().equals(directory)) {
                    foundInTree = true;
                    newChildren.add(((VCSTree) child)
                            .addPathToTree(path.subpath(1, path.getNameCount()), hash)
                            .getHash());
                } else {
                    newChildren.add(childHash);
                }
            }
            if (!foundInTree) {
                newChildren.add(new VCSTree(Paths.get(root), directory)
                        .addPathToTree(path.subpath(1, path.getNameCount()), hash)
                        .getHash());
            }
            return new VCSTree(Paths.get(root), directoryName, newChildren);
        }
    }

    /**
     * Makes list of all paths to files contained in VCSTree, it's children and files' hashes.
     * @param currentPath way to the root VCSTree
     * @return list of pairs (path to file, it's hash)
     * @throws IOException if IO went wrong
     */
    List<VCSPair> checkoutTree(@NotNull Path currentPath) throws IOException {
        List<VCSPair> files = new ArrayList<>();
        for (String childHash : children) {
            VCSObject child = getChild(childHash);
            if (child.getType().equals(BLOB)) {
                Path filePath = currentPath.resolve(((VCSBlob) child).getFileName());
                OutputStream outputStream = Files.newOutputStream(filePath);
                outputStream.write(((VCSBlob) child).getContent());
                outputStream.close();
                files.add(new VCSPair(filePath, childHash));
            } else {
                Path nextDirectory = currentPath.resolve(((VCSTree) child).getDirectoryName());
                if (Files.notExists(nextDirectory)) {
                    Files.createDirectory(nextDirectory);
                }
                files.addAll(((VCSTree) child).checkoutTree(nextDirectory));
            }
        }
        return files;
    }

    /**
     * Checks status of files in commit tree.
     * @param currantPath path to directory of VCSTree
     * @param processedFiles files that were already processed
     * @param status VCS_Status where we add files
     * @throws IOException if IO went wrong
     */
    void updateStatus(@NotNull Path currantPath, @NotNull Set<Path> processedFiles, @NotNull VCS_Status status) throws IOException {
        for (String childHash : children) {
            VCSObject child = getChild(childHash);
            if (child.getType().equals(VCSObject.TREE)) {
                VCSTree tree = (VCSTree) child;
                tree.updateStatus(currantPath.resolve(tree.getDirectoryName()), processedFiles, status);
            } else {
                VCSBlob blob = (VCSBlob) child;
                Path path = currantPath.resolve(Paths.get(blob.getFileName()));
                if (processedFiles.contains(path)) {
                    continue;
                }
                processedFiles.add(path);
                if (Files.exists(path)) {
                    byte[] content = Files.readAllBytes(path);
                    if (Arrays.equals(content, blob.getContent())) {
                        status.addUnmodified(path);
                    } else {
                        status.addModified(path);
                    }
                } else {
                    status.addDeleted(path);
                }
            }
        }
    }

    private VCSObject getChild(String childHash) throws IOException {
        return VCSObject.read(Paths.get(root).resolve(VCSConstants.objectsDirectory).resolve(childHash));
    }
}
