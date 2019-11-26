package entity;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class IndexProperty {
    private StringProperty indexName;
    private StringProperty indexField;
    private StringProperty indexAsc;
    private StringProperty indexUnique;

    public IndexProperty(String indexName, String indexField, String indexAsc, String indexUnique) {
        this.indexName = new SimpleStringProperty(indexName);
        this.indexField = new SimpleStringProperty(indexField);
        this.indexAsc = new SimpleStringProperty(indexAsc);
        this.indexUnique = new SimpleStringProperty(indexUnique);
    }

    public String getIndexName() {
        return indexName.get();
    }

    public StringProperty indexNameProperty() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName.set(indexName);
    }

    public String getIndexField() {
        return indexField.get();
    }

    public StringProperty indexFieldProperty() {
        return indexField;
    }

    public void setIndexField(String indexField) {
        this.indexField.set(indexField);
    }

    public String getIndexAsc() {
        return indexAsc.get();
    }

    public StringProperty indexAscProperty() {
        return indexAsc;
    }

    public void setIndexAsc(String indexAsc) {
        this.indexAsc.set(indexAsc);
    }

    public String getIndexUnique() {
        return indexUnique.get();
    }

    public StringProperty indexUniqueProperty() {
        return indexUnique;
    }

    public void setIndexUnique(String indexUnique) {
        this.indexUnique.set(indexUnique);
    }
}
