/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.response;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.Serializable;

/**
 *
 * @author sergio.tellez
 */
public class Document implements Serializable {

    private static final long serialVersionUID = 9122010588538798702L;
    
    private String took;
    private Integer total;
    private List<Entry> records;
    private List<Aggregation> aggs;
    private Map<String, List<CountName>> facets;
    
    public Document() {
        this.took = "";
        this.total = 0;
        this.facets = new HashMap<>();
        this.records = new ArrayList<>();
    }

    public List<Entry> getRecords() {
        return records;
    }

    public void setRecords(List<Entry> records) {
        this.records = records;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
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

    public Map<String, List<CountName>> getFacets() {
        return facets;
    }

    public void setFacets(Map<String, List<CountName>> facets) {
        this.facets = facets;
    }

    @Override
    public String toString() {
        return "Document{" + "facets=" + facets + '}';
    }
}
