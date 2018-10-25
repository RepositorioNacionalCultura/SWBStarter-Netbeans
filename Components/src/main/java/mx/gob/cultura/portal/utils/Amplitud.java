package mx.gob.cultura.portal.utils;

import java.util.List;
import java.util.HashMap;
import mx.gob.cultura.portal.response.Entry;

/**
 *
 * @author sergio.tellez
 */
public final class Amplitud {
    
    private Amplitud() { }
    
    private static final HashMap<String, List<Entry>> serieList = new HashMap<>();

    public static HashMap<String, List<Entry>> getSerieList() {
        return serieList;
    }
}
