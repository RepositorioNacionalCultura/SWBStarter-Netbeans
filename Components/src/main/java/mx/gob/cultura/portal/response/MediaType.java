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
public class MediaType implements Serializable {

    private static final long serialVersionUID = -6808742909214377954L;

    private String mime;

    private String name;

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MediaType{" + "mime=" + mime + ", name=" + name + '}';
    }
}
