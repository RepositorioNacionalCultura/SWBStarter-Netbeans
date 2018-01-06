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
public class Stats implements Serializable {

    private static final long serialVersionUID = -4123818797879141222L;
    
    private int views;

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }
}
