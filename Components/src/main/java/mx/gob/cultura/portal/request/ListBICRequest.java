package mx.gob.cultura.portal.request;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import mx.gob.cultura.portal.response.Document;
import org.semanticwb.SWBUtils;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Encapsulates a request to search endpoint to get several BIC based on search query params.
 * @author Hasdai Pacheco
 */
public class ListBICRequest {
    String uri;
    URL url;

    /**
     * Constructor. Creates a new {@link ListBICRequest}.
     * @param url Search URL to use.
     */
    public ListBICRequest(String url) {
        this.uri = url;
    }

    /**
     * Makes get request to search endpoint to get BICs.
     * @return Document object with search results.
     */
    public Document makeRequest() {
        URL url = null;
        Document doc = null;
        try {
            url = new URL(uri);
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        }

        if (null != url) {
            System.out.println("making request to: "+url);
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                if (connection.getResponseCode() == 200) {
                    InputStream is = connection.getInputStream();
                    String jsonText = SWBUtils.IO.readInputStream(is, "UTF-8");
                    Gson gson = new Gson();
                    Type documentType = new TypeToken<Document>(){}.getType();
                    doc = gson.fromJson(jsonText, documentType);
                }
            }catch (JsonSyntaxException | IOException e) {
                e.printStackTrace();
            } finally{
                if(null!=connection) connection.disconnect();
            }
        }

        return doc;
    }
}
