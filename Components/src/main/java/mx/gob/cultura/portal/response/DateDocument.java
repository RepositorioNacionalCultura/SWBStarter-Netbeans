/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.response;

import java.util.Date;
import java.io.Serializable;


/**
 *
 * @author sergio.tellez
 */
public class DateDocument implements Serializable {

    private static final long serialVersionUID = 5823180062173479772L;
    private String value;
    private String format;
    private Date datevalue;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getDatevalue() {
        return datevalue;
    }

    public void setDatevalue(Date datevalue) {
        this.datevalue = datevalue;
    }

    @Override
    public String toString() {
        return "DateDocument{" + "value=" + value + ", format=" + format + ", datevalue=" + datevalue + '}';
    }
}
