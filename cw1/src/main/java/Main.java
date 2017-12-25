import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by KatyaKos on 30.03.2017.
 */
public class Main {
    public static void main(String[] args) {
        MD5 simpleMD = new MD5();
        byte[] res1 = null;
        try {
            res1 = simpleMD.getMD5("C:\\Temp\\test.txt");
        } catch (Exception e) {
            System.out.println("ups");
        }
        System.out.println(Arrays.toString(res1));
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update("Hello!".getBytes());
        res1 = md.digest();
        System.out.println(Arrays.toString(res1));

        byte[] res2 = null;
        try {
            res2 = simpleMD.getMD5("C:\\Temp\\test");
        } catch (Exception e) {
            System.out.println("ups");
        }
        System.out.println(Arrays.toString(res2));
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update("test".getBytes());
        md.update(res1);
        res2 = md.digest();
        System.out.println(Arrays.toString(res2));
    }
}
