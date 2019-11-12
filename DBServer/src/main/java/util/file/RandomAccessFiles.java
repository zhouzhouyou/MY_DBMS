package util.file;

import core.table.block.DefineBlock;
import core.table.collection.TableDefineCollection;
import util.result.Result;
import util.result.ResultFactory;
import util.table.FieldTypes;


import java.io.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RandomAccessFiles {

    private int recordLength;
    private TableDefineCollection collection;
    private List<Integer> emptyFilePointers;
    private String emptyFilePointersPath;
    private String recordFilePath;

    public RandomAccessFiles(TableDefineCollection collection) throws IOException, ClassNotFoundException {
        this.collection = collection;
        recordLength = collection.getTotalDataLength();
        emptyFilePointersPath = collection.absolutePath.substring(0, collection.absolutePath.length() - 3) + "emLine";
        recordFilePath = collection.absolutePath.substring(0, collection.absolutePath.length() - 3) + "trd";
        emptyFilePointers = getEmptyFilePointers(emptyFilePointersPath);
    }

    public int insert(List<Object> list) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(recordFilePath, "rw");
        setFilePointer(raf);
        for (int i = 0; i < collection.list.size(); i++) {
            DefineBlock block = collection.list.get(i);
            switch (block.fieldType) {
                case FieldTypes.BOOL:
                    raf.writeBoolean((Boolean) list.get(i));
                    break;
                case FieldTypes.DOUBLE:
                    raf.writeDouble((Double) list.get(i));
                    break;
                case FieldTypes.INTEGER:
                    raf.writeInt((Integer) list.get(i));
                    break;
                case FieldTypes.DATETIME:
                    long timeStamp = ((Date) list.get(i)).getTime() / 1000;
                    raf.writeLong(timeStamp);
                    break;
                case FieldTypes.VARCHAR:
                    String varcharData = formatVarcharData((String) list.get(i), block.param);
                    raf.writeBytes(varcharData);
                    break;
                default:
                    break;
            }
        }
        return (int) (raf.getFilePointer() / recordLength);
    }

    public Result update(int recordNumber, List<Object> list) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(recordFilePath, "rw");
        if (recordNumber > raf.length() / recordLength)
            return ResultFactory.buildObjectNotExistsResult();
        raf.seek((recordNumber - 1) * recordLength);
        for (int i = 0; i < collection.list.size(); i++) {
            DefineBlock block = collection.list.get(i);
            switch (block.fieldType) {
                case FieldTypes.BOOL:
                    raf.writeBoolean((Boolean) list.get(i));
                    break;
                case FieldTypes.DOUBLE:
                    raf.writeDouble((Double) list.get(i));
                    break;
                case FieldTypes.INTEGER:
                    raf.writeInt((Integer) list.get(i));
                    break;
                case FieldTypes.DATETIME:
                    long timeStamp = ((Date) list.get(i)).getTime() / 1000;
                    raf.writeLong(timeStamp);
                    break;
                case FieldTypes.VARCHAR:
                    String varcharData = formatVarcharData((String) list.get(i), block.param);
                    raf.writeBytes(varcharData);
                    break;
                default:
                    break;
            }
        }
        return ResultFactory.buildSuccessResult(null);
    }

    public List<List<Object>> select() throws IOException {
        List<List<Object>> resultSet = new ArrayList<>();
        RandomAccessFile raf = new RandomAccessFile(recordFilePath, "rw");
        for (int i = 0; i < raf.length() / recordLength; i++) {
            List<Object> result = new ArrayList<>();
            for (int j = 0; j < collection.list.size(); j++) {
                DefineBlock block = collection.list.get(j);
                switch (block.fieldType) {
                    case FieldTypes.BOOL:
                        boolean boolData = raf.readBoolean();
                        result.add(boolData);
                        break;
                    case FieldTypes.DOUBLE:
                        double doubleData = raf.readDouble();
                        result.add(doubleData);
                        break;
                    case FieldTypes.INTEGER:
                        int intData = raf.readInt();
                        result.add(intData);
                        break;
                    case FieldTypes.DATETIME:
                        long timeData = raf.readLong();
                        Date dateData = new Date(timeData);
                        result.add(dateData);
                        break;
                    case FieldTypes.VARCHAR:
                        byte[] originVarchar = new byte[block.param];
                        raf.readFully(originVarchar);
                        String resultVarchar = new String(originVarchar).trim();
                        result.add(resultVarchar);
                        break;
                    default:
                        break;
                }
            }
            resultSet.add(result);
        }
        return resultSet;
    }

    public List<Object> select(int recordNumber) throws IOException{
        List<Object> result = new ArrayList<>();
        RandomAccessFile raf = new RandomAccessFile(recordFilePath, "rw");
        raf.seek((recordNumber - 1) * recordLength);
        for (int j = 0; j < collection.list.size(); j++) {
            DefineBlock block = collection.list.get(j);
            switch (block.fieldType) {
                case FieldTypes.BOOL:
                    boolean boolData = raf.readBoolean();
                    result.add(boolData);
                    break;
                case FieldTypes.DOUBLE:
                    double doubleData = raf.readDouble();
                    result.add(doubleData);
                    break;
                case FieldTypes.INTEGER:
                    int intData = raf.readInt();
                    result.add(intData);
                    break;
                case FieldTypes.DATETIME:
                    long timeData = raf.readLong();
                    Date dateData = new Date(timeData);
                    result.add(dateData);
                    break;
                case FieldTypes.VARCHAR:
                    byte[] originVarchar = new byte[block.param];
                    raf.readFully(originVarchar);
                    String resultVarchar = new String(originVarchar).trim();
                    result.add(resultVarchar);
                    break;
                default:
                    break;
            }
        }
        return result;
    }


    public Result delete(int recordNumber) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(recordFilePath, "rw");
        if (recordNumber > raf.length() / recordLength)
            return ResultFactory.buildObjectNotExistsResult();
        raf.seek((recordNumber - 1) * recordLength);
        emptyFilePointers.add(recordNumber);
        Collections.sort(emptyFilePointers);
        char[] tempContent = new char[recordLength];
        raf.writeBytes(new String(tempContent));
        return ResultFactory.buildSuccessResult(null);
    }


    private void setFilePointer(RandomAccessFile raf) throws IOException {
        if (!emptyFilePointers.isEmpty()) {
            int pointer = emptyFilePointers.get(0);
            raf.seek(pointer);
            emptyFilePointers.remove(0);
        } else {
            raf.seek(raf.length());
        }
    }


    @SuppressWarnings("unchecked")
    private List<Integer> getEmptyFilePointers(String emptyFilePointersPath) throws IOException, ClassNotFoundException {
        File file = new File(emptyFilePointersPath);
        if (!file.exists()) {
            file.createNewFile();
            return new ArrayList<>();
        }
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        List<Integer> emptyFilePointers = (List<Integer>) ois.readObject();
        ois.close();
        return emptyFilePointers;
    }

    public void updateEmptyFilePointers() throws IOException {
        File file = new File(emptyFilePointersPath);
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(emptyFilePointers);
        oos.close();
    }

    private String formatVarcharData(String origin, int length) {

        if (origin == null) {
            origin = "";
        }
        int originLen = origin.getBytes().length;
        if (originLen == length) {
            return origin;
        } else if (originLen < length) {
            int temp = length - originLen;
            StringBuilder tem = new StringBuilder();
            for (int i = 0; i < temp; i++) {
                tem.append((char) 0);
            }
            return origin + tem;
        } else {
            return origin.substring(0, length);
        }

    }
}
