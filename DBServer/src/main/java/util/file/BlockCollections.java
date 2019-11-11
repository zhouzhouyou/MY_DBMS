package util.file;

import util.file.exception.IllegalNameException;

import java.io.*;
import java.util.Arrays;

public class BlockCollections {
    /**
     * Check if the filename is legal.
     * @param filename file's name
     * @return true if legal
     */
    public static boolean isValidFileName(String filename) {
        if (filename == null || filename.length() > 255) return false;
        return filename.matches("[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$");
    }

    /**
     * Check if the collection exists.
     * @param absolutePath absolute path of the collection
     * @return true if exists
     */
    public static boolean exists(String absolutePath) {
        return new File(absolutePath).exists();
    }

    /**
     * Serial a collection into binary file.
     * @param blockCollection collection to serial
     * @throws IOException fail to open collection
     */
    public static void serialize(BlockCollection blockCollection) throws IOException {
        File file = new File(blockCollection.absolutePath);
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(blockCollection);
        oos.close();
    }

    /**
     * Build a {@link BlockCollection} object from file.
     * @param absolutePath file's absolute path
     * @return instance of a {@link BlockCollection}
     * @throws IOException fail to find or open the file
     * @throws ClassNotFoundException serialVersionUID changed
     * @throws IllegalNameException name is illegal
     */
    public static BlockCollection deserialize(String absolutePath) throws IOException, ClassNotFoundException, IllegalNameException{
        File file = new File(absolutePath);
        if (!isValidFileName(file.getName())) throw new IllegalNameException(absolutePath);
        if (!file.exists()) throw new IOException(absolutePath + " not exists");
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        BlockCollection blockCollection = (BlockCollection) ois.readObject();
        ois.close();
        return blockCollection;
    }

    public static boolean delete(String path) {
        File file = new File(path);
        if (!file.exists()) return false;
        if (file.isDirectory()) {
            Arrays.asList(file.listFiles()).forEach(File::delete);
        }
        return file.delete();
    }
}
