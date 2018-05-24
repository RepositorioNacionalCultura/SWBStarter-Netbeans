/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.response;

import java.io.Serializable;
import java.util.Date;
import org.bson.Document;
import org.bson.types.ObjectId;
/**
 *
 * @author rene.jara
 */
public class Annotation implements Serializable{

    private static final long serialVersionUID = 7764008805699729714L;
   
    //private String context="http://www.w3.org/ns/anno.jsonld";
    private String id; //"http://example.org/anno1",
    //private String type; //"type": "Annotation",
    //private String body; //"body": "http://example.org/post1",
    private String bodyValue; //"bodyValue": "Comment text"
    private String target; //"target": "http://example.com/page1"
    private String creator; //"creator": "http://example.org/user1",
    private Date created; //"created": "2015-01-28T12:00:00Z",
    private Date modified; //"modified": "2015-01-29T09:00:00Z",
    private String moderator; //"creator": "http://example.org/user1",
  
    
    public Annotation(String bodyValue, String target, String creator) {
        this.id = null;
        this.bodyValue = bodyValue;
        this.target = target;
        this.creator = creator;
        this.created = null;
        this.modified = null;
        this.moderator = null;
    }
    
    public Annotation(Document doc) {
        ObjectId oid = (ObjectId)doc.get("_id");
        this.id = oid.toString();
        this.bodyValue = doc.getString("bodyValue").replaceAll("[^a-zA-ZñÑáéíóúÁÉÍÓÚ\\s:,;.\\/()]","");
        this.target = doc.getString("target");
        this.creator = doc.getString("creator");
        this.created = doc.getDate("created");
        this.modified = doc.getDate("modified");
        this.moderator = doc.getString("moderator");
    }
    
    public String getId() {
        return id;
    }

    /*public void setId(String id) {
        this.id = id;
    }*/

    public String getBodyValue() {
        return bodyValue;
    }

    public void setBodyValue(String bodyValue) {
        this.bodyValue = bodyValue;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreated() {
        return new Date(created.getTime());
    }

    public void setCreated(Date created) {
        this.created = new Date(created.getTime());
    }

    public Date getModified() {
        return new Date(modified.getTime());
    }

    public void setModified(Date modified) {
        this.modified = new Date(modified.getTime());
    }

    public String getModerator() {
        return moderator;
    }

    public void setModerator(String moderator) {
        this.moderator = moderator;
    }
    
    public Boolean isModerated() {
        return this.moderator!=null && !this.moderator.isEmpty();
    }
    
    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder("{");
        if (id != null) {
            sb.append("\"id\":\"").append(id).append("\",");
        }
        if (bodyValue != null) {
            sb.append("\"bodyValue\":\"").append(bodyValue).append("\",");
        }
        if (target != null) {
            sb.append("\"target\":\"").append(target).append("\",");
        }
        if (creator != null) {
            sb.append("\"creator\":\"").append(creator).append("\",");
        }
        if (created != null) {
            sb.append("\"created\":\"").append(created).append("\",");
        }
        if (modified != null) {
            sb.append("\"modified\":\"").append(modified).append("\",");
        }
        if (moderator != null) {
            sb.append("\"moderator\":\"").append(moderator).append("\"");
        }
        if(',' == sb.charAt(sb.length()-1)){
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append("}");
        return sb.toString();
    }
}