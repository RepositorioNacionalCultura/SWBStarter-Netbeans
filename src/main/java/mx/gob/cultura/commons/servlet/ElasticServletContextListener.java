package mx.gob.cultura.commons.servlet;

import mx.gob.cultura.commons.Util;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * {@link ServletContextListener} that manages ElasticSearch client creation and index initialization.
 * @author Hasdai Pacheco
 */
public class ElasticServletContextListener implements ServletContextListener {
    private RestHighLevelClient c;
    private String indexName;
    private String envName;

    /**
     * Constructor. Creates a new instance of {@link ElasticServletContextListener}.
     */
    public ElasticServletContextListener () {
        c = Util.ELASTICSEARCH.getElasticClient();
        indexName = Util.ELASTICSEARCH.getIndexName();
        envName = Util.getEnvironmentName();
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("Starting ElasticSearch index...");

        try {
            Response resp = c.getLowLevelClient().performRequest("HEAD", indexName);
            if(resp.getStatusLine().getStatusCode() != RestStatus.OK.getStatus()) {
                createESIndex();
            } else {
                System.out.println("Index "+ indexName +" already exists...");
            }
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }

        try {
            //Remove test index if env is production
            if (Util.ENV_PRODUCTION.equals(envName)) {
                System.out.println("Removing test index...");
                Response resp = c.getLowLevelClient().performRequest("DELETE", Util.ELASTICSEARCH.REPO_INDEX_TEST);
            }
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //Close clients
        Util.ELASTICSEARCH.closeElasticClients();
    }

    /**
     * Creates default ElasticSearch index for cultural objects.
     * @return true if creation succeeds, false otherwise
     */
    private boolean createESIndex() {
        boolean ret = false;
        System.out.println("Creating index "+ indexName +"...");
        InputStream is = getClass().getClassLoader().getResourceAsStream("indexmapping_cultura.json");
        if (null != is) {
            String mapping = Util.FILE.readFromStream(is, StandardCharsets.UTF_8.name());
            HttpEntity body = new NStringEntity(mapping, ContentType.APPLICATION_JSON);
            HashMap<String, String> params = new HashMap<>();

            try {
                Response resp = c.getLowLevelClient().performRequest("PUT", "/"+ indexName, params, body);
                System.out.println("Index " + indexName + " created...");
                ret = resp.getStatusLine().getStatusCode() == RestStatus.OK.getStatus();
            } catch (IOException ioex) {
                ioex.printStackTrace();
            }
        }

        //Load test data
        if (ret && Util.ENV_DEVELOPMENT.equals(envName)) {
            System.out.println("Loading test data");
            InputStream datas = getClass().getClassLoader().getResourceAsStream("data.json");
            if (null != datas) {
                ArrayList<String> objs = new ArrayList<>();
                String jsonString = Util.FILE.readFromStream(datas, StandardCharsets.UTF_8.name());

                JSONArray data = new JSONArray(jsonString);
                for (Object ob : data.toList()) {
                    JSONObject o = (JSONObject) ob;
                    o.put("indexcreated", System.currentTimeMillis());
                    objs.add(o.toString());
                }

                List<String> indexed = Util.ELASTICSEARCH.indexObjects(c, indexName, "bic", objs);
            }
        }
        return ret;
    }
}
