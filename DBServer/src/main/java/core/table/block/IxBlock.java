package core.table.block;

import util.file.Block;
import util.file.FileUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//TODO: 这个是实现你数据结构的地方，Block实现了序列化接口，可以调用SerializeFiles.serialize(absolutePath)


/**
 * 这个类基于特定的数据结构（B+树），这个文件会在需要的时候从文件反序列化。
 * <p>
 * 在本文档中出现的所有索引都指的是在trd文件中，该条数据是第几个。
 * </p>
 */
public class IxBlock extends Block {
    //如果你要声明一个不应该被序列化的属性，请加上transient关键字，例如: private transient int age;
    // TODO: 写一个可序列化的结构，让反序列化时也能知道每个Comparable中的数据
    //B+树的阶
    private Integer bTreeOrder = 3;


    //B+树的非叶子节点最大拥有的节点数量（同时也是键的最大数量）
    private Integer maxNumber = bTreeOrder + 1;

    private Node root;

    private LeafNode left = null;

    //查询
    public List<Integer> find(Comparable key){
        List<Integer> t = this.root.find(key);
        System.out.println();
        if(t == null){
            System.out.println("不存在");
        }
        return t;
    }

    //插入
    @SuppressWarnings("null")
    public void insert(Comparable value, Comparable key){
        if(key == null)
            return;
        Node t = this.root.insert(value, key);
        //root、left来建立新树叉 32
        if(t != null)
            this.root = t;
        this.left = (LeafNode)this.root.refreshLeft();


        System.out.print("插入完成,当前根节点为:");
        for(int j = 0; j < this.root.number; j++) {
            System.out.print((Comparable) this.root.keys[j] + " ");
        }
        System.out.println();
        System.out.println("=======================================");
    }

    public List<Integer> find_overall() {
        List<Integer> result = new ArrayList<>();
        Node child = this.root.childs[0];
        while (!child.getClass().equals(LeafNode.class)) {
            child = child.childs[0];
        }
        LeafNode start = (LeafNode) child;
        for(int i = 0; i < start.number; i++) {
            result.add((Integer)start.values[i]);
        }
        start = start.right;
        while (start != null) {
            for(int i = 0; i < start.number; i++) {
                result.add((Integer)start.values[i]);
            }
            start = start.right;
        }
        return result;
    }

    public List<Integer> find_lager_than(Comparable key) {
        List<Integer> result = new ArrayList<>();
        LeafNode leafNode = this.root.find_larger_than(key);
        int i = 0;
        for(i = 0; i < leafNode.number; i++) {
            if((Integer)leafNode.values[i] >= (Integer) key) {
                break;
            }
        }
        for(int j = i; j < leafNode.number; j++) result.add((Integer) leafNode.values[j]);
        leafNode = leafNode.right;
        while (leafNode != null) {
            for(int m = 0; m < leafNode.number; m++) {
                result.add((Integer) leafNode.values[m]);
            }
            leafNode = leafNode.right;
        }
        return result;
    }


    /**
     * 节点父类，因为在B+树中，非叶子节点不用存储具体的数据，只需要把索引作为键就可以了
     * 所以叶子节点和非叶子节点的类不太一样，但是又会公用一些方法，所以用Node类作为父类,
     * 而且因为要互相调用一些公有方法，所以使用抽象类
     *

     */
    abstract class Node implements Serializable {
        //父节点
        protected Node parent;
        //子节点
        protected Node[] childs;
        //键（子节点）数量
        protected Integer number;  //一个节点最多有number个子节点（指针数）
        //键
        protected Object keys[];

        //构造方法
        public Node(){
            this.keys = new Object[maxNumber];
            this.childs = new Node[maxNumber];
            this.number = 0;
            this.parent = null;
        }

        //查找
        abstract List<Integer> find(Comparable key);
        abstract LeafNode find_larger_than(Comparable key);


        //插入
        abstract Node insert(Comparable value, Comparable key);

        abstract LeafNode refreshLeft();
    }


    /**
     * 非叶节点类

     */

    class BPlusNode extends Node {

        public BPlusNode() {
            super();
        }

        /**
         * 递归查找,这里只是为了确定值究竟在哪一块,真正的查找到叶子节点才会查
         * @param key
         * @return
         */
        @SuppressWarnings("unchecked")
        @Override
        List<Integer> find(Comparable key) {
            int i = 0;
            while(i < this.number){
                if(this.keys[i] != null && key.compareTo((Comparable) this.keys[i]) <= 0)
                    break;
                i++;
            }
            if(this.number == i)
                return null;
            // System.out.print(this.keys[i] + " ");
            return this.childs[i].find(key);
        }

        LeafNode find_larger_than(Comparable key) {

            int i = 0;
            while(i < this.number){
                if(this.keys[i] != null && key.compareTo((Comparable) this.keys[i]) <= 0)
                    break;
                i++;
            }
            if(this.number == i)
                return null;
            // System.out.print(this.keys[i] + " ");
            return this.childs[i].find_larger_than(key);
        }

        /**
         * 递归插入,先把值插入到对应的叶子节点,最终讲调用叶子节点的插入类
         * @param value
         * @param key
         */
        @SuppressWarnings("unchecked")
        @Override
        Node insert(Comparable value, Comparable key) {
            int i = 0;
            while(i < this.number){
                if((Comparable) this.keys[i]!=null && key.compareTo((Comparable) this.keys[i]) < 0)
                    break;
                i++;
            }
            if( (Comparable)this.keys[this.number - 1] != null&& key.compareTo((Comparable) this.keys[this.number - 1]) >= 0) {
                i--;

            }


            return this.childs[i].insert(value, key);
        }

        @Override
        LeafNode refreshLeft() {

            return this.childs[0].refreshLeft();
        }

        /**
         * 当叶子节点插入成功完成分解时,递归地向父节点插入新的节点以保持平衡
         * @param node1  裂开之后的左节点
         * @param node2  裂开之后的右节点
         * @param key    原始存在的父节点的key
         */
        Node insertNode(Node node1, Node node2, Comparable key){

            System.out.print("拆分后左节点:");
            for(int j = 0; j < node1.number; j++) {
                System.out.print((Comparable) node1.keys[j] + " ");
            }
            System.out.println();
            System.out.print("拆分后右节点:");
            for(int j = 0; j < node2.number; j++) {
                System.out.print((Comparable) node2.keys[j] + " ");
            }
            System.out.println();


            Comparable oldKey = null;
            if(this.number > 0)
                oldKey = (Comparable) this.keys[this.number - 1];
            //如果原有key为null,（节点裂开时往上没有父节点此时oldkey = null）说明这个非叶子节点是空的,直接放入两个节点即可
            if(key == null || this.number <= 0){
                //               System.out.println("177非叶子节点,插入左右节点: " + node1.keys[node1.number - 1] + " " + node2.keys[node2.number - 1] + "直接插入");
                this.keys[0] = node1.keys[node1.number - 1];
                this.keys[1] = node2.keys[node2.number - 1];
                this.childs[0] = node1;
                this.childs[1] = node2;
                this.number += 2;

                System.out.print("新产生的父节点值为:");
                for(int j = 0; j < this.number; j++)
                    System.out.print(this.keys[j] + " ");
                System.out.println();
                return this;

            }


            //原有节点不为空,则应该先寻找原有节点的位置,然后将新的节点插入到原有节点中
            int i = 0;
            while((Comparable)this.keys[i]!=null && key.compareTo((Comparable)this.keys[i]) != 0){
                i++;
            }
            System.out.println("i = "+i);

            int oldparentmax = Integer.parseInt(this.keys[this.number-1].toString());
            Object oldparentmax1 = this.keys[this.number-1];

            Object tempKeys[] = new Object[maxNumber];
            Object tempChilds[] = new Node[maxNumber];
            if(i+1<this.number){
                for(int j=i;j<this.number;j++){
                    tempKeys[j-i] = this.keys[j];
                    tempChilds[j-i] =  this.childs[j];

                    System.out.println(" tempKeys[j-i] = "+ tempKeys[j-i]);
                }
                int i1 = i+1;
                System.out.println("i+1 = "+i1+" this.number = "+this.number);
                System.arraycopy(tempKeys, 0, this.keys, i1, this.number+1);
                System.arraycopy(tempChilds, 0, this.childs, i1, this.number+1);
            }
            //左边节点的最大值可以直接插入,右边的要挪一挪再进行插入
            this.keys[i] = node1.keys[node1.number - 1];
            this.childs[i] = node1;

            System.arraycopy(this.keys, 0, tempKeys, 0, this.number);
            System.arraycopy(this.childs, 0, tempChilds, 0, this.number);

            if(Integer.parseInt(node2.keys[node2.number - 1].toString()) >= oldparentmax){
                tempKeys[this.number] = node2.keys[node2.number - 1];
                tempChilds[this.number] = node2;
                //                System.out.println("if");
            }
            else{
                tempKeys[this.number] = oldparentmax;
                tempChilds[this.number] = node2;
            }


            this.number++;


            //判断是否需要拆分
            //如果不需要拆分,把数组复制回去,直接返回
            if(this.number <= bTreeOrder){
                System.arraycopy(tempKeys, 0, this.keys, 0, this.number);
                System.arraycopy(tempChilds, 0, this.childs, 0, this.number);

                // System.out.println("212非叶子节点,插入左右节点: " + node1.keys[node1.number - 1] + " " + node2.keys[node2.number - 1] + ", 不需要拆分");

                System.out.print("新产生的父节点值为:");
                for(int j = 0; j < this.number; j++)
                    System.out.print(this.keys[j] + " ");
                System.out.println();
                return null;
            }

            System.out.println("非叶子节点需要拆分");

            //如果需要拆分,和拆叶子节点时类似,从中间拆开
            Integer middle = this.number / 2;

            //新建非叶子节点,作为拆分的右半部分
            BPlusNode tempNode = new BPlusNode();
            //非叶节点拆分后应该将其子节点的父节点指针更新为正确的指针
            tempNode.number = this.number - middle;
            tempNode.parent = this.parent;
            //如果父节点为空,则新建一个非叶子节点作为父节点,并且让拆分成功的两个非叶子节点的指针指向父节点
            if(this.parent == null) {


                BPlusNode tempBPlusNode = new BPlusNode();
                tempNode.parent = tempBPlusNode;
                this.parent = tempBPlusNode;
                oldKey = null;
            }
            System.arraycopy(tempKeys, middle, tempNode.keys, 0, tempNode.number);
            System.arraycopy(tempChilds, middle, tempNode.childs, 0, tempNode.number);
            for(int j = 0; j < tempNode.number; j++){
                tempNode.childs[j].parent = tempNode;
            }
            //让原有非叶子节点作为左边节点
            this.number = middle;
            this.keys = new Object[maxNumber];
            this.childs = new Node[maxNumber];
            System.arraycopy(tempKeys, 0, this.keys, 0, middle);
            System.arraycopy(tempChilds, 0, this.childs, 0, middle);

            //叶子节点拆分成功后,需要把新生成的节点插入父节点
            BPlusNode parentNode = (BPlusNode)this.parent;
            return parentNode.insertNode(this, tempNode, oldKey);
        }

    }

    /**
     * 叶节点类
     * 左半部分，右半部分，方便存储使用

     */
    class LeafNode extends Node {

        protected Object values[];
        protected LeafNode left;
        protected LeafNode right;

        public LeafNode(){
            super();
            this.values = new Object[maxNumber];
            this.left = null;
            this.right = null;
        }

        /**
         * 进行查找,经典二分查找,不多加注释
         * @param key
         * @return
         */
        @Override
        List <Integer> find(Comparable key) {
            if(this.number <=0)
                return null;

//            System.out.println("284叶子节点查找");

            Integer left = 0;
            Integer right = this.number;
            List<Integer> list = new ArrayList<Integer>();

            Integer middle = (left + right) / 2;

            while(left < right){
                Comparable middleKey = (Comparable) this.keys[middle];
                System.out.print(middleKey+" ");
                if(middleKey != null && key.compareTo(middleKey) == 0) {
                    list.add((Integer) this.values[middle]);
                    if(unique) return list;
                    else {
                        Integer p = middle + 1;
                        while (this.keys[p] == key) {
                            list.add((Integer) this.values[p]);
                            p = middle + 1;
                        }
                        return list;
                    }
                }

                else if(middleKey!=null && key.compareTo(middleKey) < 0)
                    right = middle;
                else
                    left = middle;
                middle = (left + right) / 2;
            }
            return null;
        }

        LeafNode find_larger_than(Comparable key) {
            if(this.number <=0)
                return null;

//            System.out.println("284叶子节点查找");

            Integer left = 0;
            Integer right = this.number;


            Integer middle = (left + right) / 2;

            while(left < right){
                Comparable middleKey = (Comparable) this.keys[middle];
                System.out.print(middleKey+" ");
                if(middleKey != null && key.compareTo(middleKey) == 0) {
                    return this;
                }

                else if(middleKey!=null && key.compareTo(middleKey) < 0)
                    right = middle;
                else
                    left = middle;
                middle = (left + right) / 2;
            }
            return null;
        }

        /**
         *
         * @param value
         * @param key
         */
        @Override
        Node insert(Comparable value, Comparable key) {

//            System.out.println("313叶子节点,插入key: " + key);

            //保存原始存在父节点的key值
            Comparable oldKey = null;
            if(this.number > 0)
                oldKey = (Comparable) this.keys[this.number - 1];
            //先插入数据
            int i = 0;
            //插入索引大于当前集合。集合元素升序排列，确定插入的位置
            while(i < this.number){
                if((Comparable) this.keys[i]!=null && key.compareTo((Comparable) this.keys[i]) < 0)
                    break;
                i++;
            }


            //复制数组,完成添加————原数组i位置添加新key进去
            //public static void arraycopy(原数组, 原数组起始位置, 目标数组, 目标数组起始位置, 目标数组长度)
            Object tempKeys[] = new Object[maxNumber];
            Object tempValues[] = new Object[maxNumber];
            System.arraycopy(this.keys, 0, tempKeys, 0, i);
            System.arraycopy(this.values, 0, tempValues, 0, i);
            System.arraycopy(this.keys, i, tempKeys, i + 1, this.number - i);
            System.arraycopy(this.values, i, tempValues, i + 1, this.number - i);
            tempKeys[i] = key;
            tempValues[i] = value;

            this.number++;  //此时节点中包含的索引数+1

            System.out.println("叶节点插入完成后的节点值:");
            for(int j = 0; j < this.number; j++)
                System.out.print(tempKeys[j] + " ");
            System.out.println();

            //判断是否需要拆分————当前节点索引大小小于树的要求
            //如果不需要拆分完成复制后直接返回
            if(this.number <= bTreeOrder){
                //更新当前节点的key信息
                System.arraycopy(tempKeys, 0, this.keys, 0, this.number);
                System.arraycopy(tempValues, 0, this.values, 0, this.number);

                //有可能虽然没有节点分裂，但是实际上插入的值大于了原来的最大值，所以所有父节点的边界值都要进行更新
                Node node = this;
                while (node.parent != null){
                    Comparable tempkey = (Comparable)node.keys[node.number - 1];   //当前节点最右边的值，最大值
                    //当前节点的最大值大于了父节点的最大值
                    if((Comparable)node.parent.keys[node.parent.number - 1]!=null &&tempkey.compareTo((Comparable)node.parent.keys[node.parent.number - 1]) > 0){
                        node.parent.keys[node.parent.number - 1] = tempkey;
                        node = node.parent;
                        //System.out.println("361叶子节点,插入key: " + key + ",不需要拆分&&更新父节点边界值");
                    }
                    else
                    {
                        break;
                    }
                }

                return null;
            }


            System.out.println("叶子节点需要拆分");

            //如果需要拆分,则从中间把节点拆分差不多的两部分
            Integer middle = this.number / 2;

            //新建叶子节点,作为拆分的右半部分
            LeafNode tempNode = new LeafNode();
            tempNode.number = this.number - middle;
            tempNode.parent = this.parent;
            //如果父节点为空,则新建一个非叶子节点作为父节点,并且让拆分成功的两个叶子节点的指针指向父节点
            if(this.parent == null) {

                //System.out.println("380叶子节点,插入key: " + key + ",父节点为空 新建父节点");

                BPlusNode tempBPlusNode = new BPlusNode();
                tempNode.parent = tempBPlusNode;
                this.parent = tempBPlusNode;
                oldKey = null;
            }
            //复制，建成右半部分
            System.arraycopy(tempKeys, middle, tempNode.keys, 0, tempNode.number);
            System.arraycopy(tempValues, middle, tempNode.values, 0, tempNode.number);

            //让原有叶子节点作为拆分的左半部分
            this.number = middle;
            this.keys = new Object[maxNumber];
            this.values = new Object[maxNumber];
            System.arraycopy(tempKeys, 0, this.keys, 0, middle);
            System.arraycopy(tempValues, 0, this.values, 0, middle);

            this.right = tempNode;
            tempNode.left = this;

            //叶子节点拆分成功后,需要把新生成的节点（左节点、右节点）插入父节点
            BPlusNode parentNode = (BPlusNode)this.parent;
            //System.out.println("411 oldkey = "+oldKey);
            return parentNode.insertNode(this, tempNode, oldKey);
        }

        @Override
        LeafNode refreshLeft() {
            if(this.number <= 0){
                return null;
            }
            return this;
        }
    }


    /**
     * 这个索引是否有unique属性。一般来说，primary Comparable对应的属性和unique属性都会自动建立一个unique的索引。
     */
    private boolean unique;
    /**
     * 索引名，也就是域名，最后这个类会保存在name.ix
     */
    private String name;
    /**
     * IndexBlock
     */
    private transient IndexBlock indexBlock;

    public IxBlock(IndexBlock indexBlock) {
        this.indexBlock = indexBlock;
    }

    /**
     * 返回与某个具体值相等或不等的索引的集合。
     * <pre>
     *     get(5, true)将会得到所有key = 5的值的索引的集合，
     *     get(6, false)将会得到所有key != 6的值的索引的集合。
     * </pre>
     *
     * @param key   值
     * @param equal 是否等于这个值
     * @return 与某个具体值相等或不等的索引的集合
     */
    public List<Integer> get(Comparable key, boolean equal) {
        //TODO: 返回某个具体值相等或不等的索引的集合
        if(equal == true) {
            List<Integer> result = this.find(key);

            return result;
        }
        else {
            //此处未实现
        }
        return null;
    }

    /**
     * 返回小于某个范围的值的索引的集合。
     *
     * @param maxKey    最大值
     * @param exclusive 是否去除最大值
     * @return 返回小于某个范围的值的索引的集合
     */
    public List<Integer> lower(Comparable maxKey, boolean exclusive) {
        //TODO: 返回小于（等于）索引，这取决于是否为exclusive
        List<Integer> result = new ArrayList<>();
        List<Integer> tem = this.find_lager_than(maxKey);
        if(!exclusive) tem.remove(0);
        List<Integer> all = this.find_overall();
        for(int m = 0; m < all.size(); m++) {
            int label = 1;
            for(int n = 0; n < tem.size(); n++) {
                if(all.get(m) == tem.get(n)) label = 0;
            }
            if(label == 1) result.add(all.get(m));
        }

        return result;
    }

    /**
     * 返回大于某个范围的值的索引的集合。
     *
     * @param minKey    最小值
     * @param exclusive 是否去除最小值
     * @return 返回大于某个范围的值的索引的集合
     */
    public List<Integer> larger(Comparable minKey, boolean exclusive) {
        //TODO: 返回大于（等于）这个值的索引，这取决于是否为exclusive
        List<Integer> result = this.find_lager_than(minKey);
        if(exclusive) result.remove(0);
        return result;
    }



    /**
     * 返回存在于list中的值的索引的集合。
     * <p>
     * 例如，select * from student where age in (5, 18, 19, 100)，需要返回5岁，18岁，19岁和100岁的学生的索引
     * </p>
     *
     * @param list 值的集合
     * @return 存在于list中的值的索引的集合
     */
    public List<Integer> in(List<Comparable> list) {
        //TODO: 存在于list中的值的索引的集合
        List<Integer> result = new ArrayList<>();
        for(int i = 0; i < list.size(); i++) {
            List<Integer> tem = this.find(list.get(i));
            for(int j = 0; j < tem.size(); j++) result.add(tem.get(j));
        }

        return result;
    }

    /**
     * 返回不存在于list中的值的索引的集合。
     * <p>
     * 例如，select * from student where age not in (8, 19)，需要返回不是18，19岁的学生的索引
     * </p>
     *
     * @param list 值的集合
     * @return 不存在于list中的值的索引的集合
     */
    public List<Integer> notIn(List<Comparable> list) {
        //TODO: 不存在于list中的值的索引的集合
        List<Integer> in = new ArrayList<>();
        List<Integer> result = new ArrayList<>();
        List<Integer> all = this.find_overall();

        for(int i = 0; i < list.size(); i++) {
            List<Integer> tem = new ArrayList<>();
            tem = this.find(list.get(i));
            for(int j = 0; j < tem.size(); j++) {
                in.add(tem.get(j));
            }
        }

        for(int m = 0; m < all.size(); m++) {
            int label = 1;
            for(int n = 0; n < in.size(); n++) {
                if(all.get(m) == in.get(n)) label = 0;
            }
            if(label == 1) result.add(all.get(m));
        }
        return result;
    }

    /**
     * 插入一条数据进入索引
     *
     * @param comparable 数据
     * @param index      数据的索引
     * @return 插入是否成功（尤其是对于Unique索引而言）
     */
    public boolean insert(Comparable comparable, int index) {
        //TODO: 插入一条数据进入索引
        if(unique == false) {
            this.insert(comparable, index);
        }
        else {
            List<Integer> check = this.find(index);
            if(check == null || check.size() == 0) {
                this.insert(comparable, index);
            }
            else {
                return false;
            }
        }
        return true;
    }

    /**
     * 将所有数据插入索引
     *
     * @param list 所有数据
     * @return 插入是否成功
     */
    public boolean insert(List<Comparable> list) {
        //TODO: 插入所有数据进入索引，这发生事后建立索引的情况下。

        return true;
    }

    /**
     * 删除一组索引（这是因为表中删除了一组记录，那么也需要删除对应的索引）
     *
     * @param index 索引
     * @return 删除是否成功
     */
    public boolean delete(List<Integer> index) {
        //TODO: 删除一组索引
        return false;
    }

    /**
     * 更新了一条数据后，移动原有索引的位置
     *
     * @param index  索引
     * @param newKey 新数据
     * @return 更新是否成功
     */
    public boolean update(int index, Comparable newKey) {
        //TODO: 更新了一条数据，需要移动索引的位置
        return false;
    }

    public void saveInstance() {
        try {
            FileUtils.serialize(this, indexBlock.indexFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
