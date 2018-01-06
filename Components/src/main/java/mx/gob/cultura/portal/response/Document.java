/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.response;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author sergio.tellez
 */
public class Document implements Serializable {

    private static final long serialVersionUID = 9122010588538798702L;
    
    private int total;
    private String took;
    private List<Entry> records;
    private List<Aggregation> aggs;

    public List<Entry> getRecords() {
        return records;
    }

    public void setRecords(List<Entry> records) {
        this.records = records;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTook() {
        return took;
    }

    public void setTook(String took) {
        this.took = took;
    }

    public List<Aggregation> getAggs() {
        return aggs;
    }

    public void setAggs(List<Aggregation> aggs) {
        this.aggs = aggs;
    }

    @Override
    public String toString() {
        return "Document{" + "aggs=" + aggs + '}';
    }
}
