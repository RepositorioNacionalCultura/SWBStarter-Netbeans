/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.persist;

import java.util.List;
import java.util.ArrayList;

import org.bson.Document;
import org.semanticwb.Logger;
import org.semanticwb.SWBUtils;
import org.bson.types.ObjectId;
import com.mongodb.MongoClient;
import mx.gob.cultura.commons.Util;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import static com.mongodb.client.model.Filters.eq;


/**
 *
 * @author sergio.tellez
 */
public final class MongoAccess {
    
    private static final Logger LOG = SWBUtils.getLogger(MongoAccess.class);
    
    public static MongoCollection<Document> getCollection(String mHost, int mPort, String mSource, String mCollection) {
        MongoClient client = Util.MONGODB.getMongoClient(mHost, mPort);
        MongoDatabase db = client.getDatabase(mSource);
        return db.getCollection(mCollection);
    }
    
    public static String insertDocument(Document document, MongoCollection<Document> mCollection) {
        try {
            if (null != mCollection) {
                mCollection.insertOne(document);
                ObjectId id = (ObjectId)document.get("_id");
                return id.toString();
            }
        }catch (Exception u) {
            LOG.error(u);
        }
        return null;
    }
    
    public static long updateDocument(MongoCollection<Document> mCollection, String _id, Document actl) {
        UpdateResult result = null;
        try {
            Document prev = findById(mCollection, _id);
            result = mCollection.replaceOne(prev, actl);
        }catch (Exception u) {
            LOG.error(u);
        }
        return null != result ? result.getMatchedCount() : -1;
    }
    
    public static Document findById(MongoCollection<Document> mCollection, String _id) {
        Document record = null;
        try {
            record = mCollection.find(eq("_id", new ObjectId(_id))).first();
        }catch (Exception u) {
            LOG.error(u);
        }
        return record;
    }
    
    public static Long deleteById(MongoCollection<Document> mCollection, String _id) {
        DeleteResult result = null;
        Document prev = MongoAccess.findById(mCollection, _id);
        if (null != prev) result = mCollection.deleteOne(prev);
        return null != result ? result.getDeletedCount() : -1;
    }
    
    public static List<Object> find(MongoCollection<Document> mCollection, MongoData bean) {
        List<Object> list = new ArrayList<>();
        try {
            for (Document bson : mCollection.find()) {
                list.add(bean.getCollection(bson));
            }
        }catch (Exception u) {
            LOG.error(u);
        }
        return list;
    }
}
