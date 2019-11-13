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
        writeData(list, raf);
        int insertLine = (int) (raf.getFilePointer() / recordLength);
        raf.close();
        updateEmptyFilePointers();
        return insertLine;

    }

    public Result update(int recordNumber, List<Object> list) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(recordFilePath, "rw");
        if (recordNumber > raf.length() / recordLength)
            return ResultFactory.buildObjectNotExistsResult();
        raf.seek((recordNumber - 1) * recordLength);
        writeData(list, raf);
        raf.close();
        updateEmptyFilePointers();
        return ResultFactory.buildSuccessResult(null);
    }


    public List<List<Object>> select() throws IOException {
        List<List<Object>> resultSet = new ArrayList<>();
        RandomAccessFile raf = new RandomAccessFile(recordFilePath, "rw");
        for (int i = 0; i < raf.length() / recordLength; i++) {
            List<Object> result = new ArrayList<>();
            readData(result, raf);
            resultSet.add(result);
        }
        raf.close();
        return resultSet;
    }

    public List<List<Object>> select(List<Integer> recordNumbers) throws IOException {
        List<List<Object>> resultSet = new ArrayList<>();
        RandomAccessFile raf = new RandomAccessFile(recordFilePath, "rw");
        for (int recordNumber : recordNumbers) {
            List<Object> result = new ArrayList<>();
            raf.seek((recordNumber - 1) * recordLength);
            readData(result, raf);
            resultSet.add(result);
        }
        raf.close();
        return resultSet;
    }

    public List<Object> select(int recordNumber) throws IOException {
        List<Object> result = new ArrayList<>();
        RandomAccessFile raf = new RandomAccessFile(recordFilePath, "rw");
        raf.seek((recordNumber - 1) * recordLength);
        readData(result, raf);
        raf.close();
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
        raf.close();
        updateEmptyFilePointers();
        return ResultFactory.buildSuccessResult(null);
    }

    private void writeData(List<Object> list, RandomAccessFile raf) throws IOException {
        for (int i = 0; i < collection.list.size(); i++) {
            DefineBlock block = collection.list.get(i);
            Object recordItem = list.get(i);
            String data;
            if (recordItem == null) {
                data = formatVarcharData("", block.getDataLength());
                raf.writeBytes(data);
                break;
            }
            switch (block.fieldType) {
                case FieldTypes.BOOL:
                    if ((Boolean) recordItem)
                        data = "1";
                    else
                        data = "0";
                    raf.writeBytes(data);
                    break;
                case FieldTypes.DOUBLE:
                case FieldTypes.INTEGER:
                    String originDouble = String.valueOf(recordItem);
                    data = formatVarcharData(originDouble, block.getDataLength());
                    raf.writeBytes(data);
                    break;
                case FieldTypes.DATETIME:
                    long timeStamp = ((Date) recordItem).getTime() / 1000;
                    data = String.valueOf(timeStamp);
                    raf.writeBytes(data);
                    break;
                case FieldTypes.VARCHAR:
                    data = formatVarcharData((String) recordItem, block.getDataLength());
                    raf.writeBytes(data);
                    break;
                default:
                    break;
            }
        }
    }

    private void readData(List<Object> result, RandomAccessFile raf) throws IOException {
        for (int j = 0; j < collection.list.size(); j++) {
            DefineBlock block = collection.list.get(j);

            byte[] originData = new byte[block.getDataLength()];
            raf.readFully(originData);
            String resultData = new String(originData).trim();
            if (resultData.length() == 0) {
                result.add(null);
                break;
            }

            switch (block.fieldType) {
                case FieldTypes.BOOL:
                    boolean boolData;
                    boolData = resultData.equals("1");
                    result.add(boolData);
                    break;
                case FieldTypes.DOUBLE:
                    double doubleData = Double.parseDouble(resultData);
                    result.add(doubleData);
                    break;
                case FieldTypes.INTEGER:
                    int intData = Integer.parseInt(resultData);
                    result.add(intData);
                    break;
                case FieldTypes.DATETIME:
                    long timeData = Long.parseLong(resultData);
                    Date dateData = new Date(timeData);
                    result.add(dateData);
                    break;
                case FieldTypes.VARCHAR:
                    result.add(resultData);
                    break;
                default:
                    break;
            }
        }
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

    private void updateEmptyFilePointers() throws IOException {
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
