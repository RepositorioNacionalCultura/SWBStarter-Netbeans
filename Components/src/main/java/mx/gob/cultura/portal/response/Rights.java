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
public class Rights  implements Serializable {

    private static final long serialVersionUID = 5428656041865451107L;
    
    private String url;
    private String rightstitle;
    //private String holder;
    //private String description;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRightstitle() {
        return rightstitle;
    }

    public void setRightstitle(String rightstitle) {
        this.rightstitle = rightstitle;
    }
}
