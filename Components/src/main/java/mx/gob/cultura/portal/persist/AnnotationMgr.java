/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.persist;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Consumer;
import mx.gob.cultura.commons.Util;
import org.semanticwb.Logger;
import org.semanticwb.SWBUtils;
import mx.gob.cultura.portal.response.Annotation;
import org.semanticwb.SWBPlatform;
import mx.gob.cultura.portal.utils.Utils;
import org.bson.Document;
import org.bson.types.ObjectId;




/**
 *
 * @author rene.jara
 */
public class AnnotationMgr {
    
    private static final AnnotationMgr INSTANCE;
    
    private int mPort = 27017;
    private String mHost = "localhost";
    private String mSource = "front";
    private String mCollection = "annotations";
    
    private static final Logger LOG = SWBUtils.getLogger(AnnotationMgr.class);
  
    private AnnotationMgr() {
        String env = SWBPlatform.getEnv("rnc/envAnnotations");
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
        INSTANCE = new AnnotationMgr();
    }
    
    public static AnnotationMgr getInstance() {
        return INSTANCE;
    }

    public Annotation addAnnotation(Annotation annotation){
        try {
            MongoCollection<Document> mongoCollection = getCollection();
            if (null != mongoCollection) {
                Date now= new Date();
                Document document = new Document("bodyValue", annotation.getBodyValue())
                .append("target",annotation.getTarget())
                .append("creator", annotation.getCreator())
                .append("created", now)  
                .append("modified", now );                
                mongoCollection.insertOne(document);
                //ObjectId id = (ObjectId)document.get("_id");
                return new Annotation(document);
            }
        }catch (Exception u) {
            LOG.error(u);
        }
        return null;
    
    }
    public Annotation acceptAnnotation(String id, String moderator){

        Annotation ret=null;
        Date now= new Date();
        BasicDBObject query = new BasicDBObject("_id", new ObjectId(id));   
        BasicDBObject update;
        BasicDBObject doc = new BasicDBObject("moderator",moderator);      
        doc.append("modified", now );      
        update=new BasicDBObject("$set",doc);
        FindOneAndUpdateOptions opt = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);
        try {
            MongoCollection<Document> mongoCollection = getCollection();
            Document docAnnotation = mongoCollection.findOneAndUpdate(query,update,opt);
            ret=new Annotation(docAnnotation);
        }catch (Exception u) {
            LOG.error(u);
        }
        return ret;    
    }
    public Annotation rejectAnnotation(String id){
        Annotation ret=null;
        Date now= new Date();
        BasicDBObject query = new BasicDBObject("_id", new ObjectId(id));      
        BasicDBObject update = new BasicDBObject("$set", new BasicDBObject("modified", now ) );      
        update.append("$unset",new BasicDBObject("moderator","")); 
        FindOneAndUpdateOptions opt = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);
        try {
            MongoCollection<Document> mongoCollection = getCollection();
            Document docAnnotation = mongoCollection.findOneAndUpdate(query,update,opt);
            ret=new Annotation(docAnnotation);
        }catch (Exception u) {
            LOG.error(u);
        }
        return ret;      
    }
    public Annotation updateAnnotation(String id, String bodyValue){
        Annotation ret=null;
        Date now= new Date();
        BasicDBObject query = new BasicDBObject("_id", new ObjectId(id));   
        BasicDBObject update;
        BasicDBObject doc = new BasicDBObject("bodyValue",bodyValue); 
        FindOneAndUpdateOptions opt = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);
        doc.append("modified", now );      
        update=new BasicDBObject("$set",doc);
        try {
            MongoCollection<Document> mongoCollection = getCollection();
            Document docAnnotation = mongoCollection.findOneAndUpdate(query,update,opt);
            ret=new Annotation(docAnnotation);
        }catch (Exception u) {
            LOG.error(u);
        }
        return ret;    
    }
    public boolean deleteAnnotation(String id){
        boolean ret = false;
        BasicDBObject query = new BasicDBObject("_id", new ObjectId(id));      
        try {
            MongoCollection<Document> mongoCollection = getCollection();
            long count = mongoCollection.deleteOne(query).getDeletedCount();
            ret = count>0;
        }catch (Exception u) {
            LOG.error(u);
        }
        return ret;      
    }

    public Annotation findById(String id, String user, boolean isAdmin){   
        BasicDBObject query = new BasicDBObject("_id", new ObjectId(id));  
        Annotation annotation=null;        
        if(user==null || user.isEmpty()){ //  y públicas 
            query.put("moderator",new BasicDBObject("$exists",true).append("$ne",""));
        }else if(!isAdmin){ //  y sean del usuario o públicas 
            BasicDBList orClause = new BasicDBList();
            orClause.add(new BasicDBObject("creator",user));
            orClause.add(new BasicDBObject("moderator",new BasicDBObject("$exists",true).append("$ne","")));
            query.put("$or",orClause);
        }
        try {
            MongoCollection<Document> mongoCollection = getCollection();
            Document doc=mongoCollection.find(query).first();  
            annotation = new Annotation(doc);
        }catch (Exception u) {
            LOG.error("findById",u);
        }
        return annotation;
    }
        
    public List<Annotation> findByTarget(String target, String creator){
        ArrayList<Annotation> list = new ArrayList<>();        
        BasicDBObject query = new BasicDBObject("target",target);      
        BasicDBObject order = new BasicDBObject("created",-1);      
        if(creator==null || creator.isEmpty()){ // del BIC y sean públicas
            query.put("moderator",new BasicDBObject("$exists",true).append("$ne",""));
        }else{ // del BIC y sean del usuario o públicas 
            BasicDBList orClause = new BasicDBList();
            orClause.add(new BasicDBObject("creator",creator));
            orClause.add(new BasicDBObject("moderator",new BasicDBObject("$exists",true).append("$ne","")));
            query.put("$or",orClause);
        }
        try {
            MongoCollection<Document> mongoCollection = getCollection();
            mongoCollection.find(query).sort(order).forEach((Consumer<Document>) doc -> list.add(new Annotation(doc)));
        }catch (Exception u) {
            LOG.error("findByTarget",u);
        }
        return list;
    }
    
    public List<Annotation> findByPaged(String target, String creator,int pagSize, int pag, String orderBy,int direction ){                
        int offset=0;
        int page=pag-1;
        int pageSize=pagSize;
        ArrayList<Annotation> list = new ArrayList<>(); 
        BasicDBObject query = new BasicDBObject();      
        BasicDBObject order = new BasicDBObject();      
        if (target!=null && !target.isEmpty()){
            query.append("target",target);      
        }
        if (orderBy!=null && !orderBy.isEmpty() && direction != 0 ){
            order.append(orderBy,direction);      
        }
        if (pageSize<0){
            pageSize=1;
        }
        if (page<0){
            page=0;
        }
        offset = pageSize*page;
        if(creator!=null && !creator.isEmpty()){ 
            query.append("creator",creator);
        }
        try {
            MongoCollection<Document> mongoCollection = getCollection();
            mongoCollection.find(query).sort(order).skip(offset).limit(pageSize).forEach((Consumer<Document>) doc -> list.add(new Annotation(doc)));
        }catch (Exception u) {
            LOG.error("findByPaged",u);
        }
        return list;
    }
    public int countPages(String target, String creator,int pagSize){                
        Long pages = 1l; // 1 long
        ArrayList<Annotation> list = new ArrayList<>(); 
        BasicDBObject query = new BasicDBObject();           
        if (target!=null && !target.isEmpty()){
            query.append("target",target);      
        }

        if(creator!=null && !creator.isEmpty()){ 
            query.append("creator",creator);
        }
        try {
            MongoCollection<Document> mongoCollection = getCollection();
            long total= mongoCollection.count(query);
            pages= total/pagSize;
            if( total%pagSize>0){
                pages+=1;
            }            
        }catch (Exception u) {
            LOG.error("countPages",u);
        }
        return pages.intValue();
    }    
    private MongoCollection<Document> getCollection() {
        MongoClient client = Util.MONGODB.getMongoClient(mHost, mPort);
        MongoDatabase db = client.getDatabase(mSource);
        return db.getCollection(mCollection);
    }
}