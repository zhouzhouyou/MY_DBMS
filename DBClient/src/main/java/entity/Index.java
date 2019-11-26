package entity;

import java.io.Serializable;

public class Index implements Serializable {
    public String indexName;
    public boolean unique;
    public boolean asc;
    public String field;

    public Index(String indexName, boolean unique, boolean asc, String field) {
        this.indexName = indexName;
        this.unique = unique;
        this.asc = asc;
        this.field = field;
    }

    public IndexProperty indexProperty(){
        return new IndexProperty(indexName,field,String.valueOf(asc),String.valueOf(unique));
    }
}
