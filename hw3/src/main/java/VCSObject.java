import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Object in VCS (most common).
 * Serializes/deserializes, avoids duplications.
 */
abstract class VCSObject implements Serializable {

    String hash;
    String root;

    static String BLOB = "blob";
    static String TREE = "tree";
    static String COMMIT = "commit";
    static String BRANCH = "branch";

    protected abstract String getType();

    protected abstract void updateHash();

    String getHash() {
        return hash;
    }

    /**
     * Writes instance of VCSObject to file.
     * @param object what to write
     * @param path were to write
     * @throws IOException if IO went wrong
     */
    static void write(@NotNull VCSObject object, @NotNull Path path) throws IOException {
        OutputStream out;
        if (object.getType().equals(BRANCH)) {
            out = Files.newOutputStream(path.resolve(VCSConstants.branchesDirectory).resolve(((VCSBranch) object).getName()));
        } else {
            out = Files.newOutputStream(path.resolve(VCSConstants.objectsDirectory).resolve(object.getHash()));
        }
        ObjectOutputStream objectOut = new ObjectOutputStream(out);
        objectOut.writeObject(object);
        objectOut.close();
        out.close();
    }

    /**
     * Reads instance of VCSObject from file.
     * @param path where to read from
     * @return instance of VCSObject
     * @throws IOException if IO went wrong
     */
    static VCSObject read(@NotNull Path path) throws IOException {
        try {
            InputStream fileInputStream = Files.newInputStream(path);
            ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
            VCSObject object = (VCSObject) inputStream.readObject();
            inputStream.close();
            fileInputStream.close();
            return object;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to read from file " + path.toString());
            throw e;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}

