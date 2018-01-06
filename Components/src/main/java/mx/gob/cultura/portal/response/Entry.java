/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.response;

import java.util.List;
import java.io.Serializable;
/**
 *
 * @author sergio.tellez
 */
public class Entry implements Serializable {

    private static final long serialVersionUID = 7680915584896844702L;

    private String _id;
    private String holder;
    private String description;
    
    private Stats resourcestats;
    private List<Rights> rights;
    private List<String> creator;
    private Period periodcreated;
    private List<Title> recordtitle;
    private DateDocument datecreated;
    private List<String> resourcetype;
    private List<Identifier> identifier;
    private List<DigitalObject> digitalobject;

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
    
    public Entry() {
        init();
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public List<Rights> getRights() {
        return rights;
    }

    public void setRights(List<Rights> rights) {
        this.rights = rights;
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

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DigitalObject> getDigitalobject() {
        return digitalobject;
    }

    public void setDigitalobject(List<DigitalObject> digitalobject) {
        this.digitalobject = digitalobject;
    }

    private void init() {
        DateDocument date = new DateDocument();
        date.setValue("");
        this.description = "";
        this.datecreated = date;
        periodcreated = new Period();
        periodcreated.setDateend(date);
        periodcreated.setDatestart(date);
    }
    
    public String getIdentifiers() {
        StringBuilder identifiers = new StringBuilder();
        if (null != this.identifier) {
            for (Identifier ide : this.identifier) {
		identifiers.append(ide.getValue()).append("|");
            }
            if (identifiers.length() > 0)
                identifiers.delete(identifiers.length()-1, identifiers.length());
	}
        return identifiers.toString();
    }

    @Override
    public String toString() {
        return "Entry{" + "recordtitle=" + recordtitle + ", identifier=" + identifier + ", digitalobject=" + digitalobject + '}';
    }
}
