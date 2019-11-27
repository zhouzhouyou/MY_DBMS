package util.file;

import core.table.block.DefineBlock;
import core.table.collection.TableDefineCollection;
import util.pair.Pair;
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
        getEmptyFilePointers(emptyFilePointersPath);
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
        if (recordNumber > raf.length() / recordLength - 1)
            return ResultFactory.buildObjectNotExistsResult();
        raf.seek(recordNumber * recordLength);
        delete(recordNumber);
        writeData(list, raf);
        raf.close();
        updateEmptyFilePointers();
        return ResultFactory.buildSuccessResult(null);
    }

    public List<List<Object>> selectUpdate() throws IOException {
        List<List<Object>> resultSet = new ArrayList<>();
        RandomAccessFile raf = new RandomAccessFile(recordFilePath, "rw");
        if(raf.length() == 0){
            return resultSet;
        }
        for (int i = 0; i < raf.length() / recordLength; i++) {
            List<Object> result = new ArrayList<>();
            readData(result, raf);
            resultSet.add(result);
        }
        raf.close();
        return resultSet;
    }


    public List<List<Object>> select() throws IOException {
        List<List<Object>> resultSet = new ArrayList<>();
        RandomAccessFile raf = new RandomAccessFile(recordFilePath, "rw");
        if(raf.length() == 0){
            return resultSet;
        }
        for (int i = 0; i < raf.length() / recordLength; i++) {
            List<Object> result = new ArrayList<>();
            readData(result, raf);
            if(emptyFilePointers.contains(i))
                continue;
            resultSet.add(result);
        }
        raf.close();
        return resultSet;
    }

    public List<List<Object>> select(List<Integer> recordNumbers) throws IOException {
        List<List<Object>> resultSet = new ArrayList<>();
        RandomAccessFile raf = new RandomAccessFile(recordFilePath, "rw");
        if(raf.length() == 0){
            return resultSet;
        }
        for (int recordNumber : recordNumbers) {
            List<Object> result = new ArrayList<>();
            raf.seek(recordNumber * recordLength);
            readData(result, raf);
            resultSet.add(result);
        }
        raf.close();
        return resultSet;
    }

    public List<Object> select(int recordNumber) throws IOException {
        List<Object> result = new ArrayList<>();
        RandomAccessFile raf = new RandomAccessFile(recordFilePath, "rw");
        if(raf.length() == 0){
            return result;
        }
        raf.seek(recordNumber * recordLength);
        readData(result, raf);
        raf.close();
        return result;
    }

    /**
     * 将某一列的数据全部提取出来
     *
     * @param fieldName 域名
     * @return 某一列数据
     * @throws IOException 文件读写错误
     */
    public List<Object> selectField(String fieldName) throws IOException {
        List<Object> result = new ArrayList<>();
        RandomAccessFile raf;

        raf = new RandomAccessFile(recordFilePath, "rw");

        if(raf.length() == 0){
            return result;
        }
        Pair<DefineBlock, Integer> pair = collection.getPreInfo(fieldName);
        if (pair.getFirst() == null) return null;

        int fieldRecordPointer = pair.getLast();
        DefineBlock resultBlock = pair.getFirst();

        for (int i = 0; i < raf.length() / recordLength; i++) {
            raf.seek(i * recordLength + fieldRecordPointer);
            byte[] originData = new byte[resultBlock.getDataLength()];
            raf.readFully(originData);
            String resultData = new String(originData).trim();
            if (resultData.length() == 0) {
                result.add(null);
                continue;
            }
            switch (resultBlock.fieldType) {
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
        raf.close();
        return result;
    }


    public Result delete(int recordNumber) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(recordFilePath, "rw");
        if (recordNumber > raf.length() / recordLength)
            return ResultFactory.buildObjectNotExistsResult();
        raf.seek(recordNumber * recordLength);
        emptyFilePointers.add(recordNumber);
        Collections.sort(emptyFilePointers);
        char[] tempContent = new char[recordLength];
        raf.writeBytes(new String(tempContent));
        raf.close();
        updateEmptyFilePointers();
        return ResultFactory.buildSuccessResult(null);
    }

    public Result delete()  {
        File recordFile = new File(recordFilePath);
        File emptyLineFile = new File(emptyFilePointersPath);
        try {
            FileWriter recordCleaner = new FileWriter(recordFile);
            FileWriter emptyCleaner = new FileWriter(emptyLineFile);
            recordCleaner.write("");
            emptyCleaner.write("");
            recordCleaner.flush();
            emptyCleaner.flush();
            recordCleaner.close();
            emptyCleaner.close();
        } catch (IOException e) {
            return ResultFactory.buildFailResult("Delete fail.");
        }

        return ResultFactory.buildSuccessResult("Delete success");

    }

    public void addColumnData(Object defaultData, TableDefineCollection collection) {
        try {
            List<List<Object>> recordSet = select();
            updateTableCollection(collection);
            recordLength = collection.getTotalDataLength();
            recordSet.forEach(list -> list.add(defaultData));
            FileWriter cleaner = new FileWriter(new File(recordFilePath));
            cleaner.write("");
            cleaner.flush();
            cleaner.close();

            for (List<Object> list : recordSet) {
                insert(list);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
//        String data = null;
//        if (defaultData == null) {
//            data = formatData("", dataLength);
//        } else {
//            if (defaultData instanceof String) {
//                data = (String) defaultData;
//            } else if (defaultData instanceof Integer) {
//                String origin = String.valueOf(defaultData);
//                data = formatData(origin, dataLength);
//            } else if (defaultData instanceof Double) {
//                String origin = String.valueOf(defaultData);
//                data = formatData(origin, dataLength);
//            } else if (defaultData instanceof Date) {
//                long timeStamp = ((Date) defaultData).getTime() / 1000;
//                data = String.valueOf(timeStamp);
//            } else if (defaultData instanceof Boolean) {
//                data = ((Boolean) defaultData) ? "1" : "0";
//            }
//        }
    }

    public void dropColumnData(int fieldOrder, TableDefineCollection collection) {
        try {
            List<List<Object>> recordSet = select();
            updateTableCollection(collection);
            recordLength = collection.getTotalDataLength();
            recordSet.forEach(list -> list.remove(fieldOrder));
            FileWriter cleaner = new FileWriter(new File(recordFilePath));
            cleaner.write("");
            cleaner.flush();
            cleaner.close();

            for (List<Object> list : recordSet) {
                insert(list);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeColumnData(int fieldOrder, int dataLength, TableDefineCollection defineCollection) {
        try {
            List<List<Object>> recordSet = select();
            updateTableCollection(defineCollection);
            recordLength = collection.getTotalDataLength();
            for (List<Object> list : recordSet) {
                String data = (String) list.get(fieldOrder);
                data = formatData(data, dataLength);
                list.remove(fieldOrder);
                list.add(fieldOrder, data);
            }
            FileWriter cleaner = new FileWriter(new File(recordFilePath));
            cleaner.write("");
            cleaner.flush();
            cleaner.close();

            for (List<Object> list : recordSet) {
                insert(list);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeData(List<Object> list, RandomAccessFile raf) throws IOException {
        for (int i = 0; i < collection.list.size(); i++) {
            DefineBlock block = collection.list.get(i);
            Object recordItem = list.get(i);
            String data = null;
            if (recordItem == null) {
                data = formatData("", block.getDataLength());
            } else {
                switch (block.fieldType) {
                    case FieldTypes.BOOL:
                        data = ((Boolean) recordItem) ? "1" : "0";
                        break;
                    case FieldTypes.DOUBLE:
                    case FieldTypes.INTEGER:
                        String originDouble = String.valueOf(recordItem);
                        data = formatData(originDouble, block.getDataLength());
                        break;
                    case FieldTypes.DATETIME:
                        long timeStamp = ((Date) recordItem).getTime() / 1000;
                        data = String.valueOf(timeStamp);
                        break;
                    case FieldTypes.VARCHAR:
                        data = formatData((String) recordItem, block.getDataLength());
                        break;
                    default:
                        break;
                }
            }
            raf.writeBytes(data);

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
            } else {
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
    private void getEmptyFilePointers(String emptyFilePointersPath) throws IOException, ClassNotFoundException {
        File file = new File(emptyFilePointersPath);
        if (!file.exists()) {
            FileUtils.createParentPath(emptyFilePointersPath);
            file.createNewFile();
            emptyFilePointers = new ArrayList<>();
            updateEmptyFilePointers();
            return;
        }
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        emptyFilePointers = (List<Integer>) ois.readObject();
        ois.close();
    }

    private void updateEmptyFilePointers() throws IOException {
        File file = new File(emptyFilePointersPath);
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(emptyFilePointers);
        oos.close();
    }

    private String formatData(String origin, int length) {

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

    private void updateTableCollection(TableDefineCollection collection) {
        this.collection = collection;
    }
}
