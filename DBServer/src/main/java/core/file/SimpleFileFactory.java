package core.file;

import core.file.exception.IllegalNameException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class SimpleFileFactory<T extends SimpleFile> {
    /**
     * Build a {@link SimpleFile} object from file.
     * @param absolutePath file's absolute path
     * @return instance of a {@link SimpleFile}
     * @throws IOException fail to find or open the file
     * @throws ClassNotFoundException serialVersionUID changed
     * @throws IllegalNameException name is illegal
     */
    @SuppressWarnings("unchecked")
    public T build(String absolutePath) throws IOException, ClassNotFoundException, IllegalNameException {
        File file = new File(absolutePath);
        if (!SimpleFile.isValidFileName(file.getName())) throw new IllegalNameException(absolutePath);
        if (!file.exists()) throw new IOException(absolutePath + " not exists");

        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        T simpleFile = (T) ois.readObject();
        ois.close();
        return simpleFile;
    }
}
