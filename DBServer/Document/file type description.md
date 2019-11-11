PS: **DBMS_ROOT** 为 **DBMS**程序安装的根目录，所有文件均为二进制存储。

# **DBMS_ROOT/database.db**

此文件用于保存当前数据库：数据库类型（sys,user）；数据库名称，数据库存放路径。

其中”database“可换为别的名字。

文件结构：

| 数据库基本信息 (DatabaseBlock) | structure   | structure    |
| ------------------------------ | ----------- | ------------ |
|                                | ........... | ............ |

表格信息结构：

| 结构体成员 | 数据类型  | 说明                                              |
| ---------- | --------- | ------------------------------------------------- |
| name       | CHAR[128] | 数据库名称                                        |
| type       | BOOL      | 数据库类型：系统数据库，用户数据库                |
| filename   | CHAR[256] | 数据库数据文件夹全路径，保存记录文件 与日志文件。 |
| crtime     | DATETIME  | 创建时间                                          |

​           

#   **DBMS_ROOT/data    (For every single database)**

##        **DBMS_ROOT/data/DatabaseName**

   此文件夹名与创建的数据库名同名。

##        **DBMS_ROOT/data/DatabaseName/DatabaseName.tb**

   此为当前数据库保存所有表的信息的文件。

文件结构：

| 表格信息 (TableBlock) | structure   | structure    |
| --------------------- | ----------- | ------------ |
|                       | ........... | ............ |

表格信息结构：

| 结构体成员 | 数据类型  | 说明               |
| ---------- | --------- | ------------------ |
| name       | CHAR[128] | 表格名称           |
| record_num | INTERGER  | 记录数             |
| field_num  | INTERGER  | 该表字段数         |
| tdf        | CHAR[256] | 表格定义文件路径   |
| tic        | CHAR[256] | 表格完整性文件路径 |
| trd        | CHAR[256] | 表格记录文件路径   |
| tid        | CHAR[256] | 表格索引文件路径   |
| crtime     | DATETIME  | 创建时间           |
| mtime      | INTERGER  | 最后修改时间       |

##         **DBMS_ROOT/data/DatabaseName/DatabaseName.log**

   此为日志文件。

# **DBMS_ROOT/data/DatabaseName   (For every single table)**

##         **DBMS_ROOT/data/DatabaseName/TableName.tdf** 

​    此为当前数据库中一个表的定义文件。

文件结构：

| 字段块 FieldBlock |
| ----------------- |
| 字段块 FieldBlock |
| 字段块 FieldBlock |
| ……                |



表格信息结构：

| 结构体成员  | 数据类型  | 说明                         |
| ----------- | --------- | ---------------------------- |
| order       | INTERGER  | 字段顺序                     |
| name        | CHAR[128] | 字段名称                     |
| type        | INTERGER  | 字段类型                     |
| param       | INTERGER  | 字段类型参数（VARCHAR\CHAR） |
| mtime       | DATETIME  | 最后修改时间                 |
| integrities | INTERGER  | 完整性约束信息               |

##         **DBMS_ROOT/data/DatabaseName/TableName.trd**

​    此为当前数据库中一个表的记录文件。

​      1、在 DBMS 中，一条记录，即一组数据类型的存储。由用户自定义。

​      2、基于系统数据存储的特点，所有的块和字段大小，在存储时都调整成 4 的倍数，以提高 数据  的读取效率。  

文件结构：

| 记录 1 | 记录 2 | ……   | 记录 N |
| ------ | ------ | ---- | ------ |
|        |        |      |        |

##         **DBMS_ROOT/data/DatabaseName/TableName.tic**

​    此为当前数据库中一个表的完整性描述文件。 

文件结构：

| 约束 1 | 约束 2 | ……   | 约束 N |
| ------ | ------ | ---- | ------ |
|        |        |      |        |



表格信息结构：

| 结构体成员 | 数据类型  | 说明     |
| ---------- | --------- | -------- |
| name       | CHAR[128] | 约束名称 |
| field      | CHAR[128] | 字段名称 |
| type       | INTERGER  | 约束类型 |
| param      | CHAR[256] | 参数     |

##         **DBMS_ROOT/data/DatabaseName/TableName.tid**

​    此为当前数据库中一个表的 索引描述文件  。

​    文件结构：

| 索引块 1 (IndexBlock) | 索引块 2 (IndexBlock) | 索引块 3 (IndexBlock) | ……   |
| --------------------- | --------------------- | --------------------- | ---- |
|                       |                       |                       |      |



表格信息结构：

| 结构体成员  | 数据类型     | 说明                                               |
| ----------- | ------------ | -------------------------------------------------- |
| name        | CHAR[128]    | 索引名称                                           |
| unique      | BOOLE        | 是否唯一索引，true 为唯一索引，false 为非 唯一索引 |
| asc         | BOOLE        | 排序方式：true 为升序 asc, false 为降序            |
| field_num   | INTEGER      | 字段数，最多可以保存 2 个                          |
| fields      | CHAR[128][2] | 字段值，最多可以保存 2 个                          |
| record_file | CHAR[256]    | 索引对应记录文件的路径                             |
| index_file  | CHAR[256]    | 索引数据文件的路径                                 |

​               

##       **DBMS_ROOT/data/DatabaseName/TableName/FieldNameIndex.ix**

此文件为一个表的一个字段的索引数据文件，他描述此文件的存储路径为”表格文件夹“，但

此前并未出现”表格文件夹字样“，所以其目录暂且如上。
