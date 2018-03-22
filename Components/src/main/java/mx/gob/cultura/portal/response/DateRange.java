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
public class DateRange implements Serializable {
    
    private Integer upperLimit;
    
    private Integer lowerLimit;
    
    private Integer selectedUpper;
    
    private Integer selectedLower;

    private static final long serialVersionUID = 8619062306163882107L;

    public Integer getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(Integer upperLimit) {
        this.upperLimit = upperLimit;
    }

    public Integer getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(Integer lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public Integer getSelectedUpper() {
        return selectedUpper;
    }

    public void setSelectedUpper(Integer selectedUpper) {
        this.selectedUpper = selectedUpper;
    }

    public Integer getSelectedLower() {
        return selectedLower;
    }

    public void setSelectedLower(Integer selectedLower) {
        this.selectedLower = selectedLower;
    }
}
