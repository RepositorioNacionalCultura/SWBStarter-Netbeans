/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.utils;

import org.bson.Document;
import java.io.Serializable;
import org.bson.types.ObjectId;
import mx.gob.cultura.portal.persist.MongoData;

/**
 *
 * @author sergio.tellez
 */
public class EditorTemplate implements Serializable, MongoData {
    
    private String url;
    
    private String _id;
    
    private String title;
    
    private String preview;
    
    private Boolean active;
    
    private String fileName;
    
    private Boolean hasExhibition;

    private static final long serialVersionUID = 6295505990289706155L;
    
    public EditorTemplate(String _id, String url, String fileName, String title, String preview) {
        this._id = _id;
        this.url = url;
        this.title = title;
        this.preview = preview;
        this.fileName = fileName;
        this.active = Boolean.TRUE;
        this.hasExhibition = Boolean.FALSE;
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

    public Boolean hasExhibition() {
        return hasExhibition;
    }

    public void setHasExhibition(Boolean hasExhibition) {
        this.hasExhibition = hasExhibition;
    }
    
     @Override
    public Document getBson() {
        Document bson = new Document("title", getTitle())
                .append("active", getActive()).append("url", getUrl()).append("preview", getPreview())
                .append("fileName", getFileName());
        return bson;
    }
    
    @Override
    public Object getCollection(Document bson) {
        EditorTemplate collection = new EditorTemplate(null, bson.getString("url"), bson.getString("fileName"), bson.getString("title"), bson.getString("preview"));
        ObjectId id = (ObjectId)bson.get("_id");
        collection.setId(id.toString());
        collection.setActive(bson.getBoolean("active"));
        return collection;
    }

    @Override
    public String toString() {
        return "EditorTemplate{" + "url=" + url + ", _id=" + _id + ", title=" + title + ", preview=" + preview + ", active=" + active + ", fileName=" + fileName + ", hasExhibition=" + hasExhibition + '}';
    }
}
