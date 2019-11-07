package core.file;

import core.file.exception.EmptyNameException;
import core.file.exception.IllegalNameException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static core.file.BlockCollections.isValidFileName;

public class BlockCollection<T extends Block> implements Serializable {
    public List<T> list;

    public transient String prefix;
    public transient String postfix;
    public transient String filename;
    public transient String relativePath;
    public transient String absolutePath;

    /**
     *
     * @param relativePath path of the file
     * @param prefix name of the file like "student"
     * @param postfix like "db"
     * @throws EmptyNameException prefix or postfix is empty
     * @throws IllegalNameException filename is illegal
     */
    public BlockCollection(String relativePath, String prefix, String postfix) throws EmptyNameException, IllegalNameException {
        if (prefix == null || prefix.length() == 0) throw new EmptyNameException("prefix");
        if (postfix == null || postfix.length() == 0) throw new EmptyNameException("postfix");
        filename = prefix + "." + postfix;
        if (!isValidFileName(filename)) throw new IllegalNameException(filename);
        this.relativePath = relativePath;
        this.prefix = prefix;
        this.postfix = postfix;
        absolutePath = relativePath + filename;
        list = new ArrayList<>();
    }

    public BlockCollection(String absolutePath) {
        this.absolutePath = absolutePath;
        list = new ArrayList<>();
    }



    /**
     * Get the absolute path the file.
     * @return absolute path of the file
     */
    public String getAbsolutePath() {
        return absolutePath;
    }

    /**
     * Check if the file exists.
     * @return true if exists
     */
    public boolean exists() {
        File file = new File(absolutePath);
        return file.exists();
    }

    public void add(T block) {
        list.add(block);
    }

    public void remove(T t) {
        list.remove(t);
    }
}
