/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.persist;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import com.mongodb.Block;
import org.bson.Document;
import org.semanticwb.Logger;
import org.semanticwb.SWBUtils;
import org.bson.types.ObjectId;

import org.semanticwb.SWBPlatform;
import mx.gob.cultura.commons.Util;
import mx.gob.cultura.portal.utils.Utils;

import com.mongodb.MongoClient;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Filters;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import mx.gob.cultura.portal.response.Collection;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.not;
import mx.gob.cultura.portal.response.UserCollection;
import org.bson.conversions.Bson;

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
    
    UserCollectionMgr umr = UserCollectionMgr.getInstance();
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
    
    public List<Collection> collections(String siteid, String userid) {
        return collectionsByUserLimit(siteid, userid, null, null);
    }
    
    public List<Collection> collectionsByStatus(String siteid, Boolean status) {
        return collectionsByStatusLimit(siteid, status, null, null);
    }
    
    public List<Collection> collectionsByUserLimit(String siteid, String userid, Integer from, Integer leap) {
        List<Collection> list = new ArrayList<>();
        try {
            Block<Document> c = new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    list.add(getCollection(document));
                }
            };
            MongoCollection<Document> mongoCollection = getCollection();
            if (null != from && null != leap)
                mongoCollection.find(and(eq("siteid", siteid), eq("userid", userid))).sort(getSort()).skip(leap*(from-1)).limit(leap).forEach(c);
            else mongoCollection.find(and(eq("siteid", siteid), eq("userid", userid))).sort(getSort()).forEach(c);
        }catch (Exception u) {
            LOG.error(u);
        }
        return list;
    }
    
    public List<Collection> collectionsByStatusLimit(String siteid, Boolean status, Integer from, Integer leap) {
        List<Collection> list = new ArrayList<>();
        try {
            Block<Document> c = new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    list.add(getCollection(document));
                }
            };
            MongoCollection<Document> mongoCollection = getCollection();
            if (null != from && null != leap)
                mongoCollection.find(and(eq("siteid", siteid), eq("status", status))).sort(getSort()).skip(leap*(from-1)).limit(leap).forEach(c);
            else mongoCollection.find(and(eq("siteid", siteid), eq("status", status))).sort(getSort()).forEach(c);
        }catch (Exception u) {
            LOG.error(u);
        }
        return list;
    }
    
    public List<Collection> collectionsByFilteredLimit(Boolean status, List<String> dictionary, Integer from, Integer leap) {
        List<Collection> list = new ArrayList<>();
        try {
            Block<Document> c = new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    list.add(getCollection(document));
                }
            };
            MongoCollection<Document> mongoCollection = getCollection();
            if (null != from && null != leap)
                mongoCollection.find(eq("status", status)).sort(getSort()).skip(leap*(from-1)).limit(leap).forEach(c);
            else mongoCollection.find(eq("status", status)).sort(getSort()).forEach(c);
        }catch (Exception u) {
            LOG.error(u);
        }
        return list;
    }
    
    private Bson exclude(List<String> dictionary) {
        for (String word : dictionary) {
            Document bson = new Document("title", "/^Ching.*/");
        }
        return not(eq("title", "/^Ching.*/"));
    }
    
    public List<Collection> findByCriteria(String criteria, Boolean status, Integer from, Integer leap) {
        List<Collection> list = new ArrayList<>();
        if (null == criteria || criteria.trim().isEmpty()) return list;
        try {
            Block<Document> c = new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    list.add(getCollection(document));
                }
            };
            MongoCollection<Document> mongoCollection = getCollection();
            if (null != from && null != leap)
                mongoCollection.find(and(Filters.text(criteria),eq("status", status))).projection(Projections.metaTextScore("score")).sort(Sorts.metaTextScore("score")).skip(leap*(from-1)).limit(leap).forEach(c);
            else mongoCollection.find(Filters.text(criteria)).projection(Projections.metaTextScore("score")).sort(Sorts.metaTextScore("score")).forEach(c);
        }catch (Exception u) {
            LOG.error(u);
        }
        return list;
    }
    
    public List<Collection> favorites(String siteid, String userid, Integer from, Integer leap) {
        List<Collection> list = new ArrayList<>();
        if (null != userid && !userid.trim().isEmpty()) {
            List<UserCollection> lst = umr.collectionsByUserLimit(siteid, userid, from, leap);
            if (null != lst && !lst.isEmpty()) {
                for (UserCollection uc : lst) {
                    list.add(this.findById(uc.getCollectionid()));
                }
            }
        }
        return list;
    }

    public Long countByUser(String siteid, String userid) {
        MongoCollection<Document> mongoCollection = getCollection();
        return mongoCollection.count(Filters.and(eq("siteid", siteid), eq("userid", userid)));
    } 
    
    public Long countAllByStatus(String siteid, Boolean status) {
        MongoCollection<Document> mongoCollection = getCollection();
        return mongoCollection.count(Filters.and(eq("siteid", siteid), eq("status", status)));
    }
    
    public Long countByCriteria(String criteria, Boolean status) {
        if (null == criteria || criteria.trim().isEmpty()) return 0L;
        MongoCollection<Document> mongoCollection = getCollection();
        return mongoCollection.count(and(Filters.text(criteria),eq("status", status)));
    }
    
    public Long deleteCollection(String _id) {
        Long count = -1L;
        DeleteResult result = null;
        Document prev = find(_id);
        MongoCollection<Document> mongoCollection = getCollection();
        if (null != prev) result = mongoCollection.deleteOne(prev);
        if (null != result) {
            count = result.getDeletedCount();
            if (count > 0)
                count = umr.deleteCollectionRecs(_id);
        }
        return count;
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
            .append("siteid", collection.getSiteid()).append ("favorites", collection.getFavorites()).append("username", collection.getUserName()).append("elements", collection.getElements());
        return bson;
    }
    
    private Document getSort() {
        Document bson = new Document("_id", -1);
        return bson;
    }
    
    private Collection getCollection(Document bson) {
        Collection collection = new Collection(bson.getString("title"), bson.getBoolean("status"), bson.getString("description"));
        ObjectId id = (ObjectId)bson.get("_id");
        collection.setId(id.toString());
        collection.setDate(id.getDate());
        collection.setSiteid(bson.getString("siteid"));
        collection.setUserid(bson.getString("userid"));
        collection.setUserName(bson.getString("username"));
        collection.setFavorites(bson.getInteger("favorites"));
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
    
    public boolean exist(String siteid, String title, String _id) {
        List<Document> list = new ArrayList<>();
        try {
            MongoCollection<Document> mongoCollection = getCollection();
            mongoCollection.find(and(eq("siteid", siteid), eq("title", title))).forEach((Block<Document>) record -> {
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