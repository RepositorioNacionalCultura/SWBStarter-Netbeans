/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.persist;

import org.bson.Document;

/**
 *
 * @author sergio.tellez
 */
public interface MongoData {
    
    public Document getBson();
    
    public Object getCollection(Document bson);
}
