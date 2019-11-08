package core.table.factory;

import core.file.Block;
import core.file.BlockCollections;
import core.file.exception.IllegalNameException;
import core.table.block.TableBlock;
import core.table.collection.TableComponentCollection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class TableComponentFactory<T extends Block, V extends TableComponentCollection<T>> {
    protected V collection;
    protected Map<String, T> map = new HashMap<>();

    @SuppressWarnings("unchecked")
    public TableComponentFactory(TableBlock tableBlock) {
        String absolutePath = getAbsolutePath(tableBlock);
        if(BlockCollections.exists(absolutePath)){
            try {
                collection = (V) BlockCollections.deserialize(absolutePath);
                collection.list.forEach(tableComponent -> map.put(tableBlock.tableName, tableComponent));
            } catch (IOException | ClassNotFoundException | IllegalNameException e) {
                e.printStackTrace();
            }
        }else {
            collection = getInstance(tableBlock);
        }

    }

    /**
     * Get a new TableComponentCollection's instance by tableBlock.
     *
     * @param tableBlock tableBlock this component is referred
     * @return a Table Component
     */

    protected abstract V getInstance(TableBlock tableBlock);

    /**
     * Get the absolute path of this table component file.
     * Invoked by the constructor.
     *
     * @param tableBlock tableBlock this component is referred
     * @return absolute path of this table component file
     */
    protected abstract String getAbsolutePath(TableBlock tableBlock);

    /**
     *
     * @return absolute path of this table component file.
     */
    public String getAbsolutePath(){
        return collection.absolutePath;
    }
    /**
     * Check if the table component file exists.
     *
     * @param tableName name of the table component is related to
     * @return true if exists
     */
    public boolean exists(String tableName){
        return map.containsKey(tableName);
    }

    /**
     * Save current table component file.
     */
    public void saveInstance() {
        try {
            BlockCollections.serialize(collection);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
