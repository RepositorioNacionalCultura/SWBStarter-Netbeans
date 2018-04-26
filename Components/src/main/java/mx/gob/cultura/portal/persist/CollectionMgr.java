/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.persist;

import java.util.List;
import java.util.ArrayList;

import com.mongodb.Block;
import org.bson.Document;
import org.semanticwb.Logger;
import org.semanticwb.SWBUtils;
import mx.gob.cultura.portal.response.Collection;

import org.bson.types.ObjectId;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import static com.mongodb.client.model.Filters.eq;

import java.util.Iterator;
import mx.gob.cultura.commons.Util;
import org.semanticwb.SWBPlatform;
import mx.gob.cultura.portal.utils.Utils;



/**
 *
 * @author sergio.tellez
 */
public class CollectionMgr {
    
    private static final CollectionMgr INSTANCE;
    
    private int mPort = 27017;
    private String mHost = "localhost";
    private String mSource = "front";
    private String mCollection = "composing";
    
    private static final Logger LOG = SWBUtils.getLogger(CollectionMgr.class);
  
    private CollectionMgr() {
        String env = SWBPlatform.getEnv("rnc/envComposing");
        if (null != env) {
            String [] props = env.split(":");
            if (props.length == 6) {
                mHost = props[0];
                mPort = Utils.toInt(props[1]);
                mSource = props[4];
                mCollection = props[5];
            }
        }
    }
    
    static {
        INSTANCE = new CollectionMgr();
    }
    
    public static CollectionMgr getInstance() {
        return INSTANCE;
    }
    
    public String insertCollection(Collection collection) {
        try {
            MongoCollection<Document> mongoCollection = getCollection();
            if (null != mongoCollection) {
                Document document = getBson(collection);
                document.append("userid", collection.getUserid());
                mongoCollection.insertOne(document);
                ObjectId id = (ObjectId)document.get("_id");
                return id.toString();
            }
        }catch (Exception u) {
            LOG.error(u);
        }
        return null;
    }
    
    public long updateCollection(Collection collection) {
        UpdateResult result = null;
        try {
            MongoCollection<Document> mongoCollection = getCollection();
            Document prev = find(collection.getId());
            Document actl = getBson(collection);
            result = mongoCollection.replaceOne(prev, actl);
        }catch (Exception u) {
            LOG.error(u);
        }
        return null != result ? result.getMatchedCount() : -1;
    }
    
    public Collection findById(String _id) {
        return getCollection(find(_id));
    }
    
    public List<Collection> collections(String userId) {
        List<Collection> list = new ArrayList<>();
        try {
            Block<Document> c = new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    list.add(getCollection(document));
                }
           };
            MongoCollection<Document> mongoCollection = getCollection();
            mongoCollection.find(eq("userid", userId)).forEach(c);
        }catch (Exception u) {
            LOG.error(u);
        }
        return list;
    }
    
    public Long countByUser(String userId) {
        MongoCollection<Document> mongoCollection = getCollection();
        return mongoCollection.count(Filters.eq("userid", userId));
    }
    
    public Long deleteCollection(String _id) {
        DeleteResult result = null;
        Document prev = find(_id);
        MongoCollection<Document> mongoCollection = getCollection();
        if (null != prev) result = mongoCollection.deleteOne(prev);
        return null != result ? result.getDeletedCount() : -1;
    }
    
    public Long addElement2Collection(String _id, String entry) {
        UpdateResult result = null;
        Document document = find(_id);
        MongoCollection<Document> mongoCollection = getCollection();
        if (null != document)
            result = mongoCollection.updateOne(document, new Document("$addToSet", new Document("elements", entry)));
        return null != result ? result.getModifiedCount() : 0L;
    }
    
    private MongoCollection<Document> getCollection() {
        MongoClient client = Util.MONGODB.getMongoClient(mHost, mPort);
        MongoDatabase db = client.getDatabase(mSource);
        return db.getCollection(mCollection);
    }
    
    private Document getBson(Collection collection) {
        Document bson = new Document("title", collection.getTitle())
                .append("status", collection.getStatus()).append("description", collection.getDescription()).append("userid", collection.getUserid())
                .append("elements", collection.getElements());
        return bson;
    }
    
    private Collection getCollection(Document bson) {
        Collection collection = new Collection(bson.getString("title"), bson.getBoolean("status"), bson.getString("description"));
        ObjectId id = (ObjectId)bson.get("_id");
        collection.setId(id.toString());
        collection.setDate(id.getDate());
        collection.setUserid(bson.getString("userid"));
        List<String> elements = new ArrayList<>();
        if (bson.get("elements") instanceof String)
            elements.add((String)bson.get("elements"));
        else if (bson.get("elements") instanceof java.util.ArrayList) {
            List list = (ArrayList)bson.get("elements");
            Iterator it = list.iterator();
            while (it.hasNext()) {
                elements.add((String)it.next());
            }
        }
        collection.setElements(elements);
        return collection;
    }
    
    private Document find(String _id) {
        Document record = null;
        try {
            MongoCollection<Document> mongoCollection = getCollection();
            record = mongoCollection.find(eq("_id", new ObjectId(_id))).first();
        }catch (Exception u) {
            LOG.error(u);
        }
        return record;
    }
    
    public boolean exist(String title, String _id) {
        List<Document> list = new ArrayList<>();
        try {
            MongoCollection<Document> mongoCollection = getCollection();
            mongoCollection.find(eq("title", title)).forEach((Block<Document>) record -> {
                if (record.getString("title").equalsIgnoreCase(title)) {
                    ObjectId id = (ObjectId)record.get("_id");
                    if (null == _id || _id.isEmpty() || !id.toString().equals(_id)) {
                        list.add(record);
                    }
                }
            });
        }catch (Exception u) {
            LOG.error(u);
        }
        return !list.isEmpty();
    }
}