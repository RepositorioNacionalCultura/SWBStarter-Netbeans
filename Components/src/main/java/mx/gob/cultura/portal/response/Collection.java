/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sergio.tellez
 */
public class Collection implements Serializable {

    private static final long serialVersionUID = 3088878990949611260L;
    
    private Integer id;
    
    private String title;
    
    private String userid;
    
    private Boolean status;
    
    private String description;
    
    private List<String> elements;
    
    public Collection(String title, Boolean status, String description) {
        this.title = title;
        this.status = status;
        this.description = description;
        this.elements = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<String> getElements() {
        return elements;
    }

    public void setElements(List<String> elements) {
        this.elements = elements;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @Override
    public String toString() {
        return "Collection{" + "id=" + id + ", title=" + title + ", elements=" + elements + '}';
    }
}