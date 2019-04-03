
package mx.gob.cultura.portal.persist;

import org.bson.Document;
import com.mongodb.Block;
import org.bson.types.ObjectId;

import org.semanticwb.Logger;
import org.semanticwb.SWBUtils;
import org.semanticwb.SWBPlatform;

import mx.gob.cultura.portal.utils.Utils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.result.DeleteResult;

import java.util.List;
import java.util.ArrayList;

import mx.gob.cultura.portal.response.UserCollection;
import org.bson.conversions.Bson;
/**
 *l
 * @author sergio.tellez
 */
public class UserCollectionMgr {
    
    private static final UserCollectionMgr INSTANCE;
    
    private int mPort = 27017;
    private String mHost = "localhost";
    private String mSource = "front";
    private String mCollection = "user";
    
    private static final Logger LOG = SWBUtils.getLogger(UserCollectionMgr.class);
    
    private UserCollectionMgr() {
        String env = SWBPlatform.getEnv("rnc/envComposing");
        if (null != env) {
            String [] props = env.split(":");
            if (props.length == 6) {
                mHost = props[0];
                mPort = Utils.toInt(props[1]);
                mSource = props[4];
                mCollection = "usercollection";
            }
        }
    }
    
    static {
        INSTANCE = new UserCollectionMgr();
    }
    
    public static UserCollectionMgr getInstance() {
        return INSTANCE;
    }
    
    public String insertUserCollection(UserCollection uc) {
        try {
            MongoCollection<Document> mongoCollection = MongoAccess.getCollection(mHost, mPort, mSource, mCollection);
            if (null != mongoCollection) {
                Document document = getBson(uc);
                mongoCollection.insertOne(document);
                ObjectId id = (ObjectId)document.get("_id");
                return id.toString();
            }
        }catch (Exception u) {
            LOG.error(u);
        }
        return null;
    }
    
    public Long updateUser(UserCollection user) {
        UpdateResult result = null;
        try {
            MongoCollection<Document> mongoCollection = MongoAccess.getCollection(mHost, mPort, mSource, mCollection);
            Document prev = MongoAccess.findById(mongoCollection, user.getId());
            Document actl = getBson(user);
            result = mongoCollection.replaceOne(prev, actl);
        }catch (Exception u) {
            LOG.error(u);
        }
        return null != result ? result.getMatchedCount() : -1;
    }
    
    public boolean exist(String userid, String collectionid) {
        List<Document> list = new ArrayList<>();
        try {
            Block<Document> c = new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    list.add(document);
                }
            };
            MongoCollection<Document> mongoCollection = MongoAccess.getCollection(mHost, mPort, mSource, mCollection);
            if (null != collectionid)
                mongoCollection.find(and(eq("userid", userid), eq("collectionid", collectionid))).forEach(c);
        }catch (Exception u) {
            LOG.error(u);
        }
        return !list.isEmpty();
    }
    
    public UserCollection findUserCollection(String userid, String collectionid) {
        List<UserCollection> list = new ArrayList<>();
        try {
            Block<Document> c = new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    list.add(getUserCollection(document));
                }
            };
            MongoCollection<Document> mongoCollection = MongoAccess.getCollection(mHost, mPort, mSource, mCollection);
            if (null != collectionid)
                mongoCollection.find(and(eq("userid", userid), eq("collectionid", collectionid))).forEach(c);
        }catch (Exception u) {
            LOG.error(u);
        }
        if (!list.isEmpty()) return list.get(0);
        return null;
    }
    
    public Long deleteUserCollection(String _id) {
        DeleteResult result = null;
        Document prev = find(_id);
        MongoCollection<Document> mongoCollection = MongoAccess.getCollection(mHost, mPort, mSource, mCollection);
        if (null != prev) result = mongoCollection.deleteOne(prev);
        return null != result ? result.getDeletedCount() : -1;
    }
    
    public Long deleteCollectionRecs(String collectionid) {
        DeleteResult result = null;
        MongoCollection<Document> mongoCollection = MongoAccess.getCollection(mHost, mPort, mSource, mCollection);
        result = mongoCollection.deleteMany(getDeleteAll(collectionid));
        return null != result ? result.getDeletedCount() : -1;
    }
    
    public UserCollection findById(String _id) {
        return getUserCollection(find(_id));
    }
    
    public Long countByUser(String siteid, String userid) {
        MongoCollection<Document> mongoCollection = MongoAccess.getCollection(mHost, mPort, mSource, mCollection);
        return mongoCollection.count(collectionBySiteUser(siteid, userid));
    }
    
    public Long countByCollection(String collectionid) {
        MongoCollection<Document> mongoCollection = MongoAccess.getCollection(mHost, mPort, mSource, mCollection);
        return mongoCollection.count(Filters.eq("collectionid", collectionid));
    }
    
    public List<UserCollection> collectionsByUserLimit(String siteid, String userid, Integer from, Integer leap) {
        List<UserCollection> list = new ArrayList<>();
        try {
            Block<Document> c = new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    list.add(getUserCollection(document));
                }
            };
            MongoCollection<Document> mongoCollection = MongoAccess.getCollection(mHost, mPort, mSource, mCollection);
            if (null != from && null != leap)
                mongoCollection.find(collectionBySiteUser(siteid, userid)).skip(leap*(from-1)).limit(leap).forEach(c);
            else mongoCollection.find(collectionBySiteUser(siteid, userid)).forEach(c);
        }catch (Exception u) {
            LOG.error(u);
        }
        return list;
    }
    
    private Document getDeleteAll(String collectionid) {
        Document bson = new Document("collectionid", collectionid);
        return bson;
    }
    
    private Document find(String _id) {
        Document record = null;
        try {
            MongoCollection<Document> mongoCollection = MongoAccess.getCollection(mHost, mPort, mSource, mCollection);
            record = mongoCollection.find(eq("_id", new ObjectId(_id))).first();
        }catch (Exception u) {
            LOG.error(u);
        }
        return record;
    }
    
    private Document getBson(UserCollection uc) {
        Document bson = new Document("userid", uc.getUserid()).append("siteid", uc.getSiteid()).append("collectionid", uc.getCollectionid());
        return bson;
    }
    
    private UserCollection getUserCollection(Document bson) {
        if (null == bson) return null;
        UserCollection uc = new UserCollection(bson.getString("userid"), bson.getString("siteid"), bson.getString("collectionid"));
        ObjectId id = (ObjectId)bson.get("_id");
        uc.setId(id.toString());
        return uc;
    }
    
    private Bson collectionBySiteUser(String siteid, String userid) {
        if (null != siteid && !siteid.trim().isEmpty())
            return and(eq("siteid", siteid), eq("userid", userid));
        return eq("userid", userid);
    }
    
     /**public Long addElement2User(String userid, String _id) {
        UpdateResult result = null;
        MongoCollection<Document> mongoCollection = MongoAccess.getCollection(mHost, mPort, mSource, mCollection);
        Document document = find(userid);
        if (null != document)
            result = mongoCollection.updateOne(document, new Document("$addToSet", new Document("favorites", _id)));
        return null != result ? result.getModifiedCount() : 0L;
    }**/
}
