/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.response;

import java.io.Serializable;

/**
 *
 * @author sergio.tellez
 */
public class CountName implements Serializable {

    private static final long serialVersionUID = 1741437275185405566L;
    
    private int count;
    
    private String name;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCapitalizeName() {
        if (null == this.name) return "";
        this.name = this.name.trim();
        if (this.name.isEmpty() || this.name.length() < 2) return this.name;
	this.name = this.name.toLowerCase();
	this.name = Character.toUpperCase(this.name.charAt(0)) + this.name.substring(1);
        return this.name;
    }
    
    @Override
    public String toString() {
        return "Holder{" + "count=" + count + ", name=" + name + '}';
    }
}
