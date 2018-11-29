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
public class Aggregation implements Serializable {
    
    private DateRange interval;
    
    private List<CountName> dates;
    
    private List<CountName> holders;
    
    private List<CountName> resourcetypes;
    
    private List<CountName> rights;
    
    private List<CountName> mediastype;
    
    private List<CountName> languages;
    
    private List<CountName> rightsmedia;
    
    private static final long serialVersionUID = 7895964432008759515L;

    public List<CountName> getHolders() {
        return holders;
    }

    public void setHolders(List<CountName> holders) {
        this.holders = holders;
    }

    public List<CountName> getResourcetypes() {
        return resourcetypes;
    }

    public void setResourcetypes(List<CountName> resourcetypes) {
        this.resourcetypes = resourcetypes;
    }

    public List<CountName> getDates() {
        return dates;
    }

    public void setDates(List<CountName> dates) {
        this.dates = dates;
    }
    
    public DateRange getInterval() {
        return interval;
    }

    public void setInterval(DateRange interval) {
        this.interval = interval;
    }

    public List<CountName> getRights() {
        return rights;
    }

    public void setRights(List<CountName> rights) {
        this.rights = rights;
    }

    public List<CountName> getMediastype() {
        return mediastype;
    }

    public void setMediastype(List<CountName> mediastype) {
        this.mediastype = mediastype;
    }

    public List<CountName> getLanguages() {
        return languages;
    }

    public void setLanguages(List<CountName> languages) {
        this.languages = languages;
    }

    public List<CountName> getRightsmedia() {
        return rightsmedia;
    }

    public void setRightsmedia(List<CountName> rightsmedia) {
        this.rightsmedia = rightsmedia;
    }

    @Override
    public String toString() {
        return "Aggregation{" + "holders=" + holders + '}';
    }
}