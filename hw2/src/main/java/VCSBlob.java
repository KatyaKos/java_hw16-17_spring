import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;

/**
 * Represents a file (name + content).
 */
class VCSBlob extends VCSObject implements Serializable {

    private String fileName;
    private byte[] content;

    VCSBlob(@NotNull Path root, @NotNull byte[] content, @NotNull String name) throws IOException {
        this.content = content;
        this.fileName = name;
        updateHash();
        VCSObject.write(this, root);
    }

    @Override
    protected String getType() {
        return BLOB;
    }

    String getFileName() {
        return fileName;
    }

    byte[] getContent() {
        return content;
    }

    protected void updateHash() {
        byte[] array = new byte[content.length + fileName.getBytes().length];
        System.arraycopy(content, 0, array, 0, content.length);
        System.arraycopy(fileName.getBytes(), 0, array, content.length, fileName.getBytes().length);
        hash = DigestUtils.sha1Hex(array);
    }
}
