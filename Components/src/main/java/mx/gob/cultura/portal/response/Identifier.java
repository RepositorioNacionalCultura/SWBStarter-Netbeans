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
public class Identifier implements Serializable {
    private static final long serialVersionUID = 5541337472802206271L;
    private String type;
    private String value;
    private boolean preferred;
    
    public Identifier() {
        
    }
    
    public Identifier(String type, String value) {
        this.type = null != type ? type : "";
        this.value = null != value ? value : "";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isPreferred() {
        return preferred;
    }

    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }
}
