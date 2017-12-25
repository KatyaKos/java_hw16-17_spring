import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * Created by KatyaKos on 30.03.2017.
 */
public interface InterfaceMD5 {
    default byte[] getMD5(String name) {
        File file = new File(name);
        try {
            if (file.isDirectory()) {
                return getFileMD5(name);
            } else {
                return getDirMD5(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    };

    byte[] getDirMD5(String dir) throws Exception;

    default byte[] getFileMD5(String file) throws Exception {
        InputStream fin = null;
        try {
            fin = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return null;
        }

        byte[] buffer = new byte[1024];
        MessageDigest result = MessageDigest.getInstance("MD5");
        int readable;
        do {
            readable = fin.read(buffer);
            if (readable > 0) {
                result.update(buffer, 0, readable);
            }
        } while (readable != -1);

        fin.close();
        return result.digest();
    }
}
