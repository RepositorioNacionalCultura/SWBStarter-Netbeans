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
public class Period implements Serializable {

    private static final long serialVersionUID = 2553986509134716648L;
    
    private DateDocument dateend;
    private DateDocument datestart;
    private String format;

    public DateDocument getDateend() {
        return dateend;
    }

    public void setDateend(DateDocument dateend) {
        this.dateend = dateend;
    }

    public DateDocument getDatestart() {
        return datestart;
    }

    public void setDatestart(DateDocument datestart) {
        this.datestart = datestart;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
