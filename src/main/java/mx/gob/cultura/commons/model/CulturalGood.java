package mx.gob.cultura.commons.model;

import java.util.Date;
import java.util.List;

/**
 * Class that represents a Cultural Good.
 * @author Hasdai Pacheco
 */
public class CulturalGood {
    private String _id;
    private List<Identifier> identifier;
    private List<Title> title;
    private String description;
    private List<String> creator;
    private List<DigitalObject> digitalObject;
    private List<Right> rights;
    private Date dateCreated;
    private Period periodCreated;
    private String holder;
    private String type;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public List<Title> getTitle() {
        return title;
    }

    public void setTitle(List<Title> title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getCreator() {
        return creator;
    }

    public void setCreator(List<String> creator) {
        this.creator = creator;
    }

    public List<DigitalObject> getDigitalObject() {
        return digitalObject;
    }

    public void setDigitalObject(List<DigitalObject> digitalObject) {
        this.digitalObject = digitalObject;
    }

    public List<Right> getRights() {
        return rights;
    }

    public void setRights(List<Right> rights) {
        this.rights = rights;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Period getPeriodCreated() {
        return periodCreated;
    }

    public void setPeriodCreated(Period periodCreated) {
        this.periodCreated = periodCreated;
    }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}