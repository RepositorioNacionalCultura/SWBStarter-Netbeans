/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.response;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
/**
 *
 * @author sergio.tellez
 */
public class Entry implements Serializable {

    private static final long serialVersionUID = 7680915584896844702L;

    private String _id;
    private List<String> holder;
    private List<String> description;
    
    private String lugar;
    private Stats resourcestats;
    private List<String> creator;
    private Period periodcreated;
    private List<Title> recordtitle;
    private DateDocument datecreated;
    private List<String> resourcetype;
    private List<Identifier> identifier;
    private List<DigitalObject> digitalObject;
    
    private String type;
    private Integer position;
    
    private List<String> lang;
    private List<String> keywords;
    private List<String> collection;
    
    private Rights rights;
    private List<String> generator;
    private String resourcethumbnail;
    
    

    public Stats getResourcestats() {
        return resourcestats;
    }

    public void setResourcestats(Stats resourcestats) {
        this.resourcestats = resourcestats;
    }

    public List<Title> getRecordtitle() {
        return recordtitle;
    }

    public void setRecordtitle(List<Title> recordtitle) {
        this.recordtitle = recordtitle;
    }

    public List<String> getResourcetype() {
        return resourcetype;
    }

    public void setResourcetype(List<String> resourcetype) {
        this.resourcetype = resourcetype;
    }
    
    public Rights getRights() {
        return rights;
    }

    public void setRights(Rights rights) {
        this.rights = rights;
    }
    
    public Entry() {
        init();
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public List<String> getCreator() {
        return creator;
    }

    public void setCreator(List<String> creator) {
        this.creator = creator;
    }

    public Period getPeriodcreated() {
        return periodcreated;
    }

    public void setPeriodcreated(Period periodcreated) {
        this.periodcreated = periodcreated;
    }

    public DateDocument getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(DateDocument datecreated) {
        this.datecreated = datecreated;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public List<DigitalObject> getDigitalObject() {
        return digitalObject;
    }

    public void setDigitalObject(List<DigitalObject> digitalObject) {
        this.digitalObject = digitalObject;
    }

    public List<String> getLang() {
        return lang;
    }

    public void setLang(List<String> lang) {
        this.lang = lang;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getCollection() {
        return collection;
    }

    public void setCollection(List<String> collection) {
        this.collection = collection;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResourcethumbnail() {
        return resourcethumbnail;
    }

    public void setResourcethumbnail(String resourcethumbnail) {
        this.resourcethumbnail = resourcethumbnail;
    }

    public List<String> getHolder() {
        return holder;
    }

    public void setHolder(List<String> holder) {
        this.holder = holder;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public List<String> getGenerator() {
        return generator;
    }

    public void setGenerator(List<String> generator) {
        this.generator = generator;
    }
    
    private void init() {
        DateDocument date = new DateDocument();
        date.setValue("");
        this.type = "";
        this.datecreated = date;
        periodcreated = new Period();
        periodcreated.setDateend(date);
        periodcreated.setDatestart(date);
        this.resourcestats = new Stats();
        this.keywords = new ArrayList<>();
        this.description = new ArrayList<>();
        this.recordtitle = new ArrayList<>();
        this.rights = new Rights();
        this.rights.setMedia(new MediaType());
        this.rights.getMedia().setMime("");
    }
    
    public String getIdentifiers() {
        StringBuilder identifiers = new StringBuilder();
        if (null != this.identifier) {
            for (Identifier ide : this.identifier) {
		identifiers.append(ide.getValue()).append(" |");
            }
            if (identifiers.length() > 0)
                identifiers.delete(identifiers.length()-2, identifiers.length());
	}
        return identifiers.toString();
    }
    
    public String getResourcetypes() {
        StringBuilder builder = new StringBuilder();
        for (String t : this.getResourcetype()) {
            builder.append(t).append(", ");
        }
	if (builder.length() > 0) builder.deleteCharAt(builder.length() - 2);
        return builder.toString();
    }

    @Override
    public String toString() {
        return "Entry{" + "_id=" + _id + ", description=" + description + ", creator=" + creator + ", recordtitle=" + recordtitle + ", identifier=" + identifier + ", digitalObject=" + digitalObject + '}';
    }
}
