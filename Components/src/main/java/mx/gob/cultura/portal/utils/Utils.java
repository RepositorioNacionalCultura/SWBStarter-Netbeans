/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.utils;

import java.io.Writer;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;

import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

import java.util.logging.Logger;
import mx.gob.cultura.portal.response.DigitalObject;
import mx.gob.cultura.portal.response.Entry;
import org.semanticwb.model.WebSite;

/**
 *
 * @author sergio.tellez
 */
public class Utils {
    
    protected static final Map m = new HashMap();
    
    private static final Logger LOG = Logger.getLogger(Utils.class.getName());
	
    static {
	m.put(34, "");   // ""
	m.put(40, "");   // (
	m.put(41, "");   // )
	m.put(47, "");   // /
	m.put(60, "");   // <
	m.put(61, "");   // =
	m.put(62, "");   // >
        m.put(123, "");  // {
        m.put(125, "");  // }
    }
    
    public static String suprXSS(String str) {
	try {
            StringWriter writer = new StringWriter((int)(str.length() * 1.5));
            dispersion(writer, str);
            return writer.toString();
	}catch (IOException ioe) {
            LOG.info(ioe.getMessage());
            return null;  
	}
    }

    public static void dispersion(Writer writer, String str) throws IOException {
    	int len = str.length();
    	for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            int ascii = (int) c;
            String entityName = (String) m.get(ascii);
            if (entityName == null) {
                if (c > 0x7F) {
                    writer.write("&#");
                    writer.write(Integer.toString(c, 10));
                    writer.write(';');
    		} else {
                    writer.write(c);
    		}
            } else {
    		writer.write(entityName);
            }
    	}
    }
    
    public static Date convert(String sDate) {
        if (null == sDate || sDate.isEmpty()) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            return sdf.parse(sDate);
        }catch (ParseException e) {
            LOG.info(e.getMessage());
            return new Date();
        }
    }
    
    public static int toInt(Object obj) {
        if (null == obj) return -1;
        int result = 0;	
        if (obj instanceof Long){
            result = ((Long)obj).intValue();
        } else if (obj instanceof Integer){
            result = ((Integer)obj);
        } else if (obj instanceof BigDecimal){
            result = ((BigDecimal)obj).intValue();
        } else if (obj instanceof BigInteger){
            result = ((BigInteger)obj).intValue();
        } else if (obj instanceof Double){
            result = ((Double)obj).intValue();
        } else if (obj instanceof String){
            result = Integer.parseInt((String)obj);
        } else if (obj instanceof Number){		
            result = ((Number)obj).intValue();
        }	
        return result;
    }
    
    public static List<String> getElements(org.bson.Document bson, String key) {
        List<String> elements = new ArrayList<>();
        if (null == bson || !bson.containsKey(key)) return elements;
        if (bson.get(key) instanceof String)
            elements.add((String)bson.get(key));
        else if (bson.get(key) instanceof java.util.ArrayList) {
            List list = (ArrayList)bson.get(key);
            Iterator it = list.iterator();
            while (it.hasNext()) {
                elements.add((String)it.next());
            }
        }
        return elements;
    }
}