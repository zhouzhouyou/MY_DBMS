package util.file;

import core.table.block.DefineBlock;
import core.table.collection.TableDefineCollection;
import util.result.Result;
import util.result.ResultFactory;
import util.table.FieldTypes;


import java.io.IOException;
import java.io.RandomAccessFile;

import java.util.Date;
import java.util.List;

public class RandomAccessFiles {

    private int recordLength;
    private TableDefineCollection collection;
    private List<Integer> emptyFilePointers;

    public RandomAccessFiles(TableDefineCollection collection) {
        this.collection = collection;
        recordLength = collection.getTotalDataLength();
    }

    public void write(String path, List<Object> list) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(path, "rw");
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
    }

    public Result delete(String path, int recordNumber) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(path, "rw");
        if (recordNumber > raf.length() / recordLength)
            return ResultFactory.buildObjectNotExistsResult();
        raf.seek((recordNumber - 1) * recordLength);
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
                tem.append(" ");
            }
            return origin + tem;
        } else {
            return origin.substring(0, length);
        }

    }
}
