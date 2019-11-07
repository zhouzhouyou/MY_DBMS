package core.file;

import core.file.exception.EmptyNameException;
import core.file.exception.IllegalNameException;

import java.io.*;

public class SimpleFile implements Serializable {
    public String prefix;

    public transient String path;

    public transient String postfix;

    public transient String filename;

    /**
     *
     * @param path path of the file
     * @param prefix name of the file like "student"
     * @param postfix like "db"
     * @throws EmptyNameException prefix or postfix is empty
     * @throws IllegalNameException filename is illegal
     */
    public SimpleFile(String path, String prefix, String postfix) throws EmptyNameException, IllegalNameException {
        if (prefix == null || prefix.length() == 0) throw new EmptyNameException("prefix");
        if (postfix == null || postfix.length() == 0) throw new EmptyNameException("postfix");
        filename = prefix + "." + postfix;
        if (!isValidFileName(filename)) throw new IllegalNameException(filename);
        this.path = path;
        this.prefix = prefix;
        this.postfix = postfix;
    }

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
     * Get the absolute path the file.
     * @return absolute path of the file
     */
    public String getAbsolutePath() {
        return path + filename;
    }

    /**
     * Check if the file exists.
     * @return true if exists
     */
    public boolean exists() {
        File file = new File(getAbsolutePath());
        return file.exists();
    }

    /**
     * Write the file in binary form
     * @throws IOException fail to open file or write file
     */
    public void writeFile() throws IOException {
        File file = new File(getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.close();
    }
}
