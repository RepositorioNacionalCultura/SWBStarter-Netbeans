package mx.gob.cultura.portal.request;

import com.google.gson.Gson;
import mx.gob.cultura.portal.response.Entry;
import org.semanticwb.SWBUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Encapsulates a request to search endpoint to get  BIC.
 * @author Hasdai Pacheco.
 */
public class GetBICRequest {
    String uri;

    /**
     * Constructor. Creates a new instance of {@link GetBICRequest}.
     * @param url Search URL to use.
     */
    public GetBICRequest (String url) {
        uri = url;
    }

    /**
     * Makes get request to search endpoint to get a BIC.
     * @return Entry object with BIC information.
     */
    public Entry makeRequest() {
        URL url = null;
        Entry entry = null;

        try {
            url = new URL(uri);
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        }
        if (null != url) {
            //System.out.println("making request to: "+url);
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                InputStream is = connection.getInputStream();
                String jsonText = SWBUtils.IO.readInputStream(is, "UTF-8");
                Gson gson = new Gson();
                entry = gson.fromJson(jsonText, Entry.class);
            } catch (Exception ex) {
                // ex.printStackTrace();
            } finally{
                if(connection!=null) connection.disconnect();
            }
        }

        return entry;
    }
}
