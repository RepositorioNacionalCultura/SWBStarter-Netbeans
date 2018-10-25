/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.utils;

import java.util.Comparator;
import mx.gob.cultura.portal.response.Entry;

/**
 *
 * @author sergio.tellez
 */
public class Serie implements Comparator {
    
    String root;
    String title;
    String value;
    
    public Serie() {
        
    }
    
    public Serie(Entry entry) {
        this.title = Utils.getTitle(entry.getRecordtitle(), 0);
    }

    public String getRoot() {
        StringBuilder builder = new StringBuilder();
        if (null != title && !title.isEmpty()) {
            for (int i =0; i < title.length(); i++) {
                if (!isDigit(title.charAt(i))) {
                    builder.append(title.charAt(i));
                }else break;
            }
            root = builder.toString();
        } 
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        StringBuilder builder = new StringBuilder();
        if (null != title && !title.isEmpty()) {
            if (null != root && !root.isEmpty()) {
                String tmp = title.substring(root.length(), title.length());
                for (int i =0; i < tmp.length(); i++) {
                    if (isDigit(tmp.charAt(i))) {
                        builder.append(tmp.charAt(i));
                    }else break;
            }
            }
            value = builder.toString();
        } 
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public int compare(Object o1, Object o2) {
        Serie s1 = new Serie((Entry)o1);
        Serie s2 = new Serie((Entry)o2);
        if (!s1.getRoot().equalsIgnoreCase(s2.getRoot())) return s1.getRoot().compareToIgnoreCase(s2.getRoot());
        else return Utils.toInt(s1.getValue()) - Utils.toInt(s2.getValue());
    }
    
    private boolean isDigit(char i) {
        boolean isDigit = false;
        switch (i) {
            case '0' : isDigit = true; break;
            case '1' : isDigit = true; break;
            case '2' : isDigit = true; break;
            case '3' : isDigit = true; break;
            case '4' : isDigit = true; break;
            case '5' : isDigit = true; break;
            case '6' : isDigit = true; break;
            case '7' : isDigit = true; break;
            case '8' : isDigit = true; break;
            case '9' : isDigit = true; break;
            default: isDigit = false;
        }
        return isDigit;
    }
}
