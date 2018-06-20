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
public class DigitalObject implements Serializable {

    private static final long serialVersionUID = -6559537162627861962L;
    
    private String url;
    private MediaType mediatype;
    private Rights rights;
    //private String rights;
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public MediaType getMediatype() {
        return mediatype;
    }

    public void setMediatype(MediaType mediatype) {
        this.mediatype = mediatype;
    }

    public Rights getRights() {
        return rights;
    }

    public void setRights(Rights rights) {
        this.rights = rights;
    }

    @Override
    public String toString() {
        return "DigitalObject{" + "url=" + url + ", mediatype=" + mediatype + '}';
    }
}