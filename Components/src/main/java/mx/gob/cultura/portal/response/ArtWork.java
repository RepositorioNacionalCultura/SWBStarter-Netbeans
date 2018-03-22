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
public class ArtWork implements Serializable {
    
    private Entry entry;
    
    private Boolean hidden;
    
    private Boolean favorite;
    
    private static final long serialVersionUID = 168536605411786439L;
    
    public ArtWork(Entry entry) {
        this.entry = entry;
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    public Boolean isHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }
   
    @Override
    public String toString() {
        return "ArtWork{" + "entry=" + entry + ", hidden=" + hidden + ", favorite=" + favorite + '}';
    }
}
