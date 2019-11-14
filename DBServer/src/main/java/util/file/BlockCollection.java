package util.file;

import util.file.exception.EmptyNameException;
import util.file.exception.IllegalNameException;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 存储Block的数据结构
 *
 * @param <T> 继承{@link Block}的类
 */
public class BlockCollection<T extends Block> implements Serializable {
    /**
     * 存储Block的List
     */
    public List<T> list;

    /**
     * 前缀，例如student.trd的前缀是student
     */
    public transient String prefix;
    /**
     * 后缀，例如student.trd的后缀是trd
     */
    public transient String postfix;
    /**
     * 前缀.后缀
     */
    public transient String filename;
    /**
     * 相对路径位置，和filename组成绝对路径
     */
    public transient String relativePath;
    /**
     * 绝对路径
     */
    public String absolutePath;

    /**
     * @param relativePath path of the file
     * @param prefix       name of the file like "student"
     * @param postfix      like "db"
     * @throws EmptyNameException   prefix or postfix is empty
     * @throws IllegalNameException filename is illegal
     */
    public BlockCollection(String relativePath, String prefix, String postfix) throws EmptyNameException, IllegalNameException {
        if (prefix == null || prefix.length() == 0) throw new EmptyNameException("prefix");
        if (postfix == null || postfix.length() == 0) throw new EmptyNameException("postfix");
        filename = prefix + "." + postfix;
        if (!FileUtils.isValidFileName(filename)) throw new IllegalNameException(filename);
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
     *
     * @return absolute path of the file
     */
    public String getAbsolutePath() {
        return absolutePath;
    }

    /**
     * Check if the file exists.
     *
     * @return true if exists
     */
    public boolean exists() {
        File file = new File(absolutePath);
        return file.exists();
    }

    public void add(T block) {
        list.add(block);
    }

    public void remove(T block) {
        list.remove(block);
    }
}
