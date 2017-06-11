import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Running application.
 */
public class Main {
    public static void main(@NotNull String[] args) throws IOException, ClassNotFoundException {
        List<Class> classes = parse(args);
        try {
            for (Class clazz : classes) {
                TestRunner tester = new TestRunner();
                tester.testClass(clazz);
                List<TestReporter> reports = tester.getReports();
                for (TestReporter report : reports) {
                    System.out.println(report.getInfo());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<Class> parse(@NotNull String[] args) throws IOException, ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        for (String path : args) {
            Enumeration<JarEntry> entries = new JarFile(path).entries();
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{
                    new URL("jar:file:" + path + "!/")
            });
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")) {
                    continue;
                }
                String className = jarEntry.getName();
                className = className.substring(0, jarEntry.getName().length() - ".class".length()).replace('/', '.');
                classes.add(classLoader.loadClass(className));
            }
        }
        return classes;
    }
}
