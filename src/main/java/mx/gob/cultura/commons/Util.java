package mx.gob.cultura.commons;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.log4j.Logger;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.UUIDs;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.semanticwb.datamanager.DataList;
import org.semanticwb.datamanager.DataObject;
import org.semanticwb.datamanager.SWBDataSource;
import org.semanticwb.datamanager.SWBScriptEngine;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Utility class with common methods.
 *
 * @author Hasdai Pacheco
 */
public final class Util {
    private static final Logger logger = Logger.getLogger(Util.class);
    public static final String ENV_DEVELOPMENT = "DEV";
    public static final String ENV_TESTING = "TEST";
    public static final String ENV_QA = "QA";
    public static final String ENV_PRODUCTION = "PROD";

    private Util() { }

    public static String makeRequest(URL theUrl, boolean XMLSupport) {
        HttpURLConnection con = null;
        StringBuilder response = new StringBuilder();
        String errorMsg = null;
        int retries = 0;
        boolean isConnOk = false;

        do {
            logger.trace("Trying to make request to URL " + theUrl.toString());
            try {
                con = (HttpURLConnection) theUrl.openConnection();
                isConnOk = true;
                //AQUI SE PIDE EN XML
                if (XMLSupport) {
                    logger.trace("Setting XML request header");
                    con.setRequestProperty("accept", "application/xml");
                }
                con.setRequestMethod("GET");
                //System.out.println("content type:" + con.getContentType());

                int statusCode = con.getResponseCode();

                if (statusCode == 200) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                        String inputLine;

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                    } catch (IOException ioex) {
                        ioex.printStackTrace();
                    }
                } else {
                    //throw new ServerErrorException();
                }

            } catch (IOException e) {
                logger.trace("Failed connection to URL "+theUrl+". Retrying");
                if (null != con) {
                    con.disconnect();
                }
                retries++;
                try {
                    Thread.sleep(5000);
                } catch (Exception te) {
                    te.printStackTrace();
                }

                //e.printStackTrace();
                isConnOk = false;
                if(retries==5){
                    logger.trace("Max number of retries reached ("+retries+")");
                    errorMsg="#Error: No se puede conectar al servidor#";
                }
            }
        } while (isConnOk == false && retries < 5);
        return errorMsg!=null?errorMsg:response.toString();
    }

    /**
     * Gets environment configuration.
     * @return Value of REPO_DEVENV environment property. Defaults to production.
     */
    public static String getEnvironmentName() {
        String env = System.getenv("REPO_DEVENV");
        if (null == env) env = ENV_PRODUCTION;
        return env;
    }

    /**
     * Inner class to encapsulate methods related to ElasticSearch actions.
     */
    public static final class ELASTICSEARCH {
        public static final String REPO_INDEX = "cultura";
        public static final String REPO_INDEX_TEST = "cultura_test";
        private static HashMap<String, RestHighLevelClient> elasticClients = new HashMap<>();

        /**
         * Gets a {@link RestHighLevelClient} instance with default host and port.
         *
         * @return RestHighLevelClient instance object.
         */
        public static RestHighLevelClient getElasticClient() {
            return getElasticClient("localhost", 9200);
        }

        /**
         * Gets a {@link RestHighLevelClient} instance with given host and port.
         *
         * @param host ElasticSearch node host name.
         * @param port ElasticSearch node port.
         * @return RestHighLevelClient instance object.
         */
        public static RestHighLevelClient getElasticClient(String host, int port) {
            RestHighLevelClient ret = elasticClients.get(host + ":" + String.valueOf(port));
            if (null == ret) {
                ret = new RestHighLevelClient(
                        RestClient.builder(new HttpHost(host, port)));

                elasticClients.put(host + ":" + String.valueOf(port), ret);
            }
            return ret;
        }

        /**
         * Closes an ElasticSearch {@link RestHighLevelClient} associated with @host and @port.
         * @param host Hostname of client
         * @param port Port number of client
         */
        public static void closeElasticClient(String host, int port) {
            RestHighLevelClient ret = elasticClients.get(host + ":" + String.valueOf(port));
            if (null != ret) {
                try {
                    ret.close();
                    elasticClients.remove(host + ":" + String.valueOf(port), ret);
                } catch (IOException ioex) {
                    logger.error("Error while closing ES client", ioex);
                }
            }
        }

        /**
         * Closes all instances of {@link RestHighLevelClient}s.
         */
        public static void closeElasticClients() {
            for (RestHighLevelClient c : elasticClients.values()) {
                try {
                    c.close();
                } catch (IOException ioex) {
                    logger.error("Error while closing ES clients", ioex);
                }
            }
        }

        /**
         * Gets time-based UUID for indexing objects.
         * @return String representation of a time-based UUID.
         */
        public static String getUUID() {
            return UUIDs.base64UUID();
        }

        /**
         * Indexes an object in ElasticSearch.
         * @param client {@link RestHighLevelClient} object.
         * @param indexName Name of index to use.
         * @param typeName Name of type in index.
         * @param objectId ID for object, autogenerated if null.
         * @param objectJson Object content in JSON String format.
         * @return ID of indexed object or null if indexing fails.
         */
        public static String indexObject(RestHighLevelClient client, String indexName, String typeName, String objectId, String objectJson) {
            String ret = null;
            String id = objectId;

            if (null == objectId || objectId.isEmpty()) {
                id = Util.ELASTICSEARCH.getUUID();
            }

            IndexRequest req = new IndexRequest(indexName, typeName, id);
            req.source(objectJson, XContentType.JSON);

            try {
                IndexResponse resp = client.index(req);
                if (resp.status().getStatus() == RestStatus.CREATED.getStatus() ||
                        resp.status().getStatus() == RestStatus.OK.getStatus()) {
                    ret = resp.getId();
                }
            } catch (IOException ioex) {
                logger.error("Error making index request for object with id "+objectId, ioex);
            }

            return ret;
        }

        /**
         * Indexes a list of objects in ElasticSearch using bulk API.
         * @param objects List of objects Strings in JSON format.
         * @param client {@link RestHighLevelClient} object.
         * @param indexName Name of index to use.
         * @param typeName Name of type in index.
         * @return List of identifiers of indexed objects.
         */
        public static ArrayList<String> indexObjects(RestHighLevelClient client, String indexName, String typeName, ArrayList<String> objects) {
            ArrayList<String> ret = new ArrayList<>();
            BulkRequest request = new BulkRequest();

            for (String obj : objects) {
                String id = ELASTICSEARCH.getUUID();
                IndexRequest req = new IndexRequest(indexName, typeName, id);
                req.source(obj, XContentType.JSON);
                request.add(req);

                try {
                    BulkResponse resp = client.bulk(request);
                    for (BulkItemResponse itemResponse : resp) {
                        DocWriteResponse r = itemResponse.getResponse();
                        if (itemResponse.getOpType() == DocWriteRequest.OpType.INDEX || itemResponse.getOpType() == DocWriteRequest.OpType.CREATE) {
                            IndexResponse indexResponse = (IndexResponse) r;
                            if (indexResponse.status().getStatus() == RestStatus.CREATED.getStatus() ||
                                    indexResponse.status().getStatus() == RestStatus.OK.getStatus()) {
                                ret.add(indexResponse.getId());
                            }
                        }
                    }
                } catch (IOException ioex) {
                    logger.error("Error indexing objects in bulk request", ioex);
                }
            }
            return ret;
        }

        /**
         * Gets index name to work with according to environment configuration.
         * @return Name of index to use.
         */
        public static String getIndexName() {
            return Util.ENV_DEVELOPMENT.equals(Util.getEnvironmentName()) ? REPO_INDEX_TEST : REPO_INDEX;
        }

        /**
         * Creates an index in ElasticSearch.
         * @param client {@link RestHighLevelClient} object.
         * @param indexName Name of index to use.
         * @param mapping JSON String of index mapping.
         * @return true if index is created, false otherwise.
         */
        public static boolean createIndex(RestHighLevelClient client, String indexName, String mapping) {
            boolean ret = false;
            HttpEntity body = new NStringEntity(mapping, ContentType.APPLICATION_JSON);
            HashMap<String, String> params = new HashMap<>();

            try {
                Response resp = client.getLowLevelClient().performRequest("PUT", "/"+ indexName, params, body);
                ret = resp.getStatusLine().getStatusCode() == RestStatus.OK.getStatus();
            } catch (IOException ioex) {
                logger.error("Error creating index "+indexName, ioex);
            }
            return ret;
        }
    }

    /**
     * Inner class to encapsulate methods related to MongoDB actions.
     */
    public static final class MONGODB {
        private static HashMap<String, MongoClient> mongoClients = new HashMap<>();

        /**
         * Gets a {@link MongoClient} instance with default host and port.
         *
         * @return MongoClient instance object.
         */
        public static MongoClient getMongoClient() {
            return getMongoClient("localhost", 27017);
        }

        /**
         * Gets a {@link MongoClient} instance with given host and port.
         *
         * @param host MongoDB server host.
         * @param port MongoDB server port.
         * @return MongoClient instance object.
         */
        public static MongoClient getMongoClient(String host, int port) {
            MongoClient ret = mongoClients.get(host + ":" + String.valueOf(port));
            if (null == ret) {
                ret = new MongoClient(host, port);
                mongoClients.put(host + ":" + String.valueOf(port), ret);
            }

            return ret;
        }
    }

    /**
     * Inner class to encapsulate methods related to SWBForms DataManager actions.
     */
    public static final class SWBForms {
        /**
         * Carga la colección de Replace a un HashMap<ocurrencia, reemplazo>
         *
         * @param engine Utilizado para poder cargar la colección de Replace en un HashMap
         * @return HashMap con DataSource cargado en memoria.
         */
        public static HashMap<String,String> loadOccurrences( SWBScriptEngine engine) {
            SWBDataSource datasource = null;
            HashMap<String,String> hm = new HashMap();

            if (null != engine) {
                try {
                    datasource = engine.getDataSource("Replace");
                    DataObject r = new DataObject();
                    DataObject data = new DataObject();
                    r.put("data", data);

                    DataObject ret = datasource.fetch(r);
                    String occurrence = "";
                    String replace = "";

                    DataList rdata = ret.getDataObject("response").getDataList("data");
                    DataObject dobj = null;
                    if (!rdata.isEmpty()) {
                        for (int i = 0; i < rdata.size(); i++) {
                            dobj = rdata.getDataObject(i);  // DataObject de Replace
                            occurrence = dobj.getString("occurrence");
                            replace = dobj.getString("replace");
                            hm.put(occurrence, replace);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error al cargar el DataSource. " + e.getMessage());
                }
            } else {
                logger.error("Error al cargar el DataSource al HashMap, falta inicializar el engine.");
                return null;
            }

            return hm;
        }

        /**
         * Converts a {@link BasicDBObject} into a MongoDB {@link com.mongodb.DBObject}
         * @param obj {@link DataObject} to transform
         * @return BasicDBObject
         */
        public static BasicDBObject toBasicDBObject(DataObject obj) {
            BasicDBObject ret = new BasicDBObject();
            Iterator<Map.Entry<String, Object>> it = obj.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                ret.put(entry.getKey(), toBasicDB(entry.getValue()));
            }
            return ret;
        }

        /**
         * Converts an Object into a MongoDB generic object.
         * @param obj {@link Object} to convert
         * @return MongoDB generic object
         */
        public static Object toBasicDB(Object obj) {
            if (obj instanceof DataObject) {
                return toBasicDBObject((DataObject) obj);
            } else if (obj instanceof DataList) {
                return toBasicDBList((DataList) obj);
            }
            return obj;
        }

        /**
         * Converts a {@link DataList} into a MongoDB {@link BasicDBList}
         * @param obj {@link DataList} to convert
         * @return BasicDBList
         */
        public static BasicDBList toBasicDBList(DataList obj) {
            BasicDBList ret = new BasicDBList();
            Iterator it = obj.iterator();
            while (it.hasNext()) {
                ret.add(toBasicDB(it.next()));
            }
            return ret;
        }
    }

    /**
     * Inner class to encapsulate methods related with File manipulation
     */
    public static final class FILE {

        /**
         * Reads {@link java.io.InputStream} content as a {@link String}
         * @param fis {@link FileInputStream} to read from
         * @param encoding Name of encoding to use on content read.
         * @return String wirh {@link InputStream} content.
         */
        public static String readFromStream(InputStream fis, String encoding) {
            StringBuilder ret = new StringBuilder();
            String enc = StandardCharsets.UTF_8.name();

            if (null != encoding && !enc.isEmpty()) {
                enc = encoding;
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(fis, enc))) {
                String line;
                while ((line = br.readLine()) != null) {
                    ret.append(line);
                }
            } catch (IOException ioex) {
                logger.error("Error reading file", ioex);
            }

            return ret.toString();
        }
    }

    /**
     * Inner class to encapsulate methods related with String manipulation
     */
    public static final class TEXT {
        /**
         * Reemplaza las ocurrencias en el string recibido
         *
         * @param hm HashMap con las ocurrencias y su reemplazo previamente cargado
         * @param oaistr Stream del registro OAI a revisar
         * @return String con todas las ocurrencias reemplazadas.
         */
        public static String replaceOccurrences(HashMap<String,String> hm, String oaistr) {
            if (null != hm && null!=oaistr) {
                String occurrence = "";
                String replace = "";
                Iterator<String> it = hm.keySet().iterator();
                while (it.hasNext()) {
                    occurrence = it.next();
                    replace = hm.get(occurrence);
                    oaistr = oaistr.replace(occurrence, replace);
                }
            }
            return oaistr;
        }
    }
}