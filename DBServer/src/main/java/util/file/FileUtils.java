package util.file;

import util.file.exception.IllegalNameException;

import java.io.*;
import java.util.Arrays;

public class FileUtils {
    /**
     * Check if the filename is legal.
     *
     * @param filename file's name
     * @return true if legal
     */
    public static boolean isValidFileName(String filename) {
        if (filename == null || filename.length() > 255) return false;
        return filename.matches("[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$");
    }

    /**
     * Check if the collection exists.
     *
     * @param absolutePath absolute path of the collection
     * @return true if exists
     */
    public static boolean exists(String absolutePath) {
        return new File(absolutePath).exists();
    }

    /**
     * Serialize a object to file.
     *
     * @param t            object to serialize
     * @param absolutePath path of the file
     * @param <T>          class extends serializable
     * @throws IOException fail to open or write file
     * @see Serializable
     * @see ObjectOutputStream
     */
    public static <T extends Serializable> void serialize(T t, String absolutePath) throws IOException {
        File file = new File(absolutePath);
        createParentPath(absolutePath);
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(t);
        oos.close();
    }

    /**
     * Deserialize object from file.
     *
     * @param absolutePath path of the file
     * @param <T>          class extends serialize
     * @return instance of T
     * @throws IOException            fail to find or open the file
     * @throws ClassNotFoundException serialVersionUID changed
     * @throws IllegalNameException   name is illegal
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T deserialize(String absolutePath) throws IOException, ClassNotFoundException, IllegalNameException {
        File file = new File(absolutePath);
        if (!isValidFileName(file.getName())) throw new IllegalNameException(absolutePath);
        if (!file.exists()) throw new IOException(absolutePath + " not exists");
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        T t = (T) ois.readObject();
        ois.close();
        return t;
    }

    /**
     * Delete files in path or a file.
     *
     * @param path path to delete
     * @return false if file dose not exists
     */
    public static boolean delete(String path) {
        File file = new File(path);
        if (!file.exists()) return false;
        if (file.isDirectory()) {
            Arrays.asList(file.listFiles()).forEach(File::delete);
        }
        return file.delete();
    }

    public static void createParentPath(String path) {
        File file = new File(path);
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
    }
}
