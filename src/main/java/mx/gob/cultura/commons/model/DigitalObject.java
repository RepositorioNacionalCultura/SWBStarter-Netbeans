package mx.gob.cultura.commons.model;

import java.util.List;

/**
 * POJO Class that represents a digital object related to a BIC.
 * @author Hasdai Pacheco
 */
public class DigitalObject {
    private String title;
    private String description;
    private String url;
    private String mediaType;
    private List<Right> rights;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public List<Right> getRights() {
        return rights;
    }

    public void setRights(List<Right> rights) {
        this.rights = rights;
    }
}
