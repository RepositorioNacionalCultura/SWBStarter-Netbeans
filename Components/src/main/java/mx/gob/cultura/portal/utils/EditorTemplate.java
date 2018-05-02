/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.utils;

import java.io.Serializable;

/**
 *
 * @author sergio.tellez
 */
public class EditorTemplate implements Serializable {
    
    private String url;
    
    private String _id;
    
    private String title;
    
    private String preview;
    
    private Boolean active;
    
    private String fileName;

    private static final long serialVersionUID = 6295505990289706155L;
    
    public EditorTemplate(String _id, String url, String fileName, String title, String preview) {
        this._id = _id;
        this.url = url;
        this.title = title;
        this.preview = preview;
        this.fileName = fileName;
        this.active = Boolean.TRUE;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
