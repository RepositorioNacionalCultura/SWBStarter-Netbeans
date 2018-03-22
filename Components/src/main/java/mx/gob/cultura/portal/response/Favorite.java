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
public class Favorite implements Serializable {

    private static final long serialVersionUID = -5101229081363609483L;
    
    private String _id;
    
    private Integer idCollection;
    
    public Favorite(String idEntry, Integer idCollection) {
        this._id = idEntry;
        this.idCollection = idCollection;
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public Integer getIdCollection() {
        return idCollection;
    }

    public void setIdCollection(Integer idCollection) {
        this.idCollection = idCollection;
    }
}
