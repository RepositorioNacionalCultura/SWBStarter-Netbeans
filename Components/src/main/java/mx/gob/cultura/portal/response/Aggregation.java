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

    private static final long serialVersionUID = 7895964432008759515L;
    
    List<CountName> dates;
    
    List<CountName> holders;
    
    List<CountName> resourcetypes;

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
}