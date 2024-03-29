package core.table.block;

import util.file.Block;
import util.file.FileUtils;
import util.file.RandomAccessFiles;
import util.file.exception.IllegalNameException;
import util.result.Result;
import util.result.ResultFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 存储着一个索引
 */
public class IndexBlock extends Block {
    /**
     * 索引名
     */
    public String indexName;
    /**
     * 是否为unique索引（对于primary key和unique属性的域，会自动创建unique索引）
     */
    public boolean unique;
    /**
     * 是否为升序
     */
    public boolean asc;
    /**
     * 域名，即列名
     */
    public String field;
    /**
     * 文件随机读写工具
     */
    public transient RandomAccessFiles raf;
    /**
     * 索引文件的绝对路径
     */
    public String indexFilePath;
    /**
     * 索引文件的数据结构
     * <p>
     * 初次创建时，需要将现有的数据写入IxBlock
     * 系统关闭时，需要将IxBlock序列化
     * 系统重启时，需要从文件反序列化得到IxBlock
     * </p>
     */
    private transient IxBlock ixBlock;


    public IndexBlock(String indexName, boolean unique, boolean asc, String field, RandomAccessFiles raf, String indexFilePath) {
        this.indexName = indexName;
        this.unique = unique;
        this.asc = asc;
        this.field = field;
        this.raf = raf;
        this.indexFilePath = indexFilePath;
    }

    /**
     * 创建当前索引
     *
     * @return 创建结果
     */
    public Result create() {
        ixBlock = new IxBlock(this);
        //创建一个IxBlock，创建失败立即返回
        List<Object> list = null;
        try {
            list = raf.selectField(field);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Comparable> comparableList = list.stream().map(object -> (Comparable) object).collect(Collectors.toList());
        //把现有的所有数据插入IxBlock
        boolean result = ixBlock.insert(comparableList);
        ixBlock.saveInstance();
        if (result) return ResultFactory.buildSuccessResult(null);
        else return ResultFactory.buildFailResult("fail to insert into b+ tree");
    }

    public Result delete() {
        //TODO: 删除当前索引
        if (FileUtils.delete(indexFilePath)) return ResultFactory.buildSuccessResult(null);
        return ResultFactory.buildFailResult("fail to delete");
    }

    /**
     * 如果IxBlock不存在，那就从文件反序列化然后返回
     *
     * @return 这个Index对应的IxBlock
     */
    public IxBlock getIxBlock() {
        if (ixBlock == null) {
            try {
                ixBlock = FileUtils.deserialize(indexFilePath);
            } catch (IOException | ClassNotFoundException | IllegalNameException e) {
                e.printStackTrace();
            }
        }
        return ixBlock;
    }

    /**
     * 插入一条记录进入当前索引
     *
     * @param comparable 值
     * @param index      索引
     * @return 插入是否成功
     */
    public Result insertRecord(Comparable comparable, int index) {
        boolean result = ixBlock.insert(comparable, index);
        if (result) return ResultFactory.buildSuccessResult(comparable);
        return ResultFactory.buildFailResult(comparable);
    }

    /**
     * 删除一条记录
     *
     * @param index 记录的索引
     * @return 删除是否成功
     */
    public Result deleteRecord(int index) {
        return deleteRecord(Collections.singletonList(index));
    }

    /**
     * 删除一组记录
     *
     * @param indexes 记录的索引列表
     * @return 删除是否成功
     */
    public Result deleteRecord(List<Integer> indexes) {
        boolean result = ixBlock.delete(indexes);
        if (result) return ResultFactory.buildSuccessResult(null);
        return ResultFactory.buildFailResult("fail to delete from b+ tree");
    }

    public void deleteAll() {
        //ixBlock.deleteAll();
    }

    public List<Object> getInfo(){
        List<Object> info = new ArrayList<>();
        info.add(indexName);
        info.add(field);
        info.add(asc);
        info.add(unique);
        return info;
    }
}
