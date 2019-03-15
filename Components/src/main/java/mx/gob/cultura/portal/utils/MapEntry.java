/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author sergio.tellez
 */
public class MapEntry  implements Comparator {
    
    Map entry;
    String key;
    String title;
    Boolean visible;
    Boolean required;
    Integer order;
    
    public MapEntry() {
        
    }
    
    public MapEntry(Map entry) {
        this.entry = entry;
        if (null != entry) {
            if (null != entry.get("title")) {
                if (entry.get("title") instanceof String) this.title = (String)entry.get("title");
            }else this.title = "";
            this.order = null != entry.get("order") ? (Integer)entry.get("order") : new Integer("0");
            this.required = null != entry.get("required") ? (Boolean)entry.get("required") : false;
            this.visible = null != entry.get("visible") ? (Boolean)entry.get("visible") : false;
        }else {
            throw new UnsupportedOperationException("Map entry is null.");
        }
    }
    
     public static List<MapEntry> values(Map unsorted) {
        List<MapEntry> entries = new ArrayList<>();
        if (null != unsorted && !unsorted.isEmpty()) {
            Iterator it = unsorted.keySet().iterator();
            while (it.hasNext()) {
                String key = (String)it.next();
                if (unsorted.get(key) instanceof Map) {
                    Map entry = (Map)unsorted.get(key);
                    MapEntry mapEntry = new MapEntry(entry);
                    mapEntry.setKey(key);
                    entries.add(mapEntry);
                }else if (unsorted.get(key) instanceof ArrayList) {
                    Map<String, Object> auxmap = new HashMap<>();
                    auxmap.put(key, unsorted.get(key));
                    MapEntry mapEntry = new MapEntry(auxmap);
                    mapEntry.setKey(key);
                    entries.add(mapEntry);
                }
            }
        }
        return entries;
    }

    public Map getEntry() {
        return entry;
    }

    public void setEntry(Map entry) {
        this.entry = entry;
    }
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
    
     @Override
    public int compare(Object o1, Object o2) {
        MapEntry e1 = (MapEntry)o1;
        MapEntry e2 = (MapEntry)o2;
        if (e1.getOrder().intValue() == e2.getOrder().intValue()) return e1.getTitle().compareToIgnoreCase(e2.getTitle());
        else return e1.getOrder().compareTo(e2.getOrder());
    }

    @Override
    public String toString() {
        return "MapEntry{" + "key=" + key + ", value=" + title + ", order=" + order + ", visibility=" + visible + '}';
    }
}