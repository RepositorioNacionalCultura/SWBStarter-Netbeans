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

import java.text.Format;
import java.text.ParseException;

import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

import java.util.logging.Logger;
import org.semanticwb.model.WebSite;
import org.semanticwb.model.Resource;
import javax.servlet.http.HttpServletRequest;

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
    
    public static Date convert(String sDate, String format) {
        if (null == sDate || sDate.isEmpty()) return null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(sDate);
        }catch (ParseException e) {
            LOG.info(e.getMessage());
            return new Date();
        }
    }
    
    public static String esDate(String sDate) {
        if (null == sDate || sDate.isEmpty()) return "";
        Date date2format = convert(sDate, "yyyy-MM-dd");
        Locale local = new Locale("es");
        Format formatter = new SimpleDateFormat("d' de 'MMMM' de 'yyyy", local);
        return formatter.format(date2format);
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
    
    public static org.bson.Document getDescription(List<String> descriptions) {
        if (null == descriptions || descriptions.isEmpty()) return null;
        String description = descriptions.get(0);
        if (null == description || description.isEmpty()) return null;
        org.bson.Document bson = new org.bson.Document("full", description);
        if (description.length() > 240)
            bson.append("short", description.substring(0, 240)+"...");
        else
            bson.append("short", description);
        return bson;
    }
    
    /**
     * Genera un tag script para disponer de la funcionalidad para compartir contenido de un sitio Web.
     * Se requiere que el recurso que incluya esta funcion, se ejecute en una plantilla de SWB que
     * tambien haga uso de una instancia del recurso {@link mx.gob.cultura.portal.resources.SessionInitializer}
     * por la dependencia que existe en cuanto a las funciones del API de Facebook.
     * @param request la peticion {@link javax.servlet.http.HttpServletRequest} en atencion
     * @return un tag de HTML con el script necesario para 
     */
    public static String getScriptFBShare(HttpServletRequest request) {
        
        StringBuilder ret = new StringBuilder(128);
        ret.append("<script>\n");
        ret.append("  function fbShare() {\n");
        ret.append("    FB.ui({\n");
        ret.append("      method: 'share',\n");
        ret.append("      display: 'popup',\n");
        ret.append("      href: '");
        ret.append(request.getRequestURL());
        ret.append("?");
        ret.append(request.getQueryString());
        ret.append("',\n");
        ret.append("    }, function(response){});\n");
        ret.append("  }\n");
        ret.append("  </script>\n");
        
        return ret.toString();
    }
    
    /**
     * Sustituye en la URL recibida, el identificador del recurso a ejecutar
     * con el identificador de un recurso activo cuya clase sea la indicada por {@code objectClass}
     * @param website el sitio en que se buscar&aacute; una instancia activa del 
     *          recurso con clase {@code objectClass}
     * @param objectClass  la clase del recurso del cual se busca una instancia activa
     * @param urlExample la URL que se quiere ejecutar, en la que se indicar&aacute; 
     *          la instancia del tipo de recurso con clase {@code objectClass}
     * @return la URL para que se ejecute el el recurso que crea la sesion del usuario
     */
    public static String getResourceURL(WebSite website, Class objectClass, String urlExample) {
        
        //con urlExample como: "/es/cultura/portada2018/_aid/80/_mto/3/_act/openSession"
        String resourceId = null;
        final String delimiterAction = "_aid/";
        final String delimiterMode = "_rid/";
        StringBuilder actionPath = new StringBuilder(64);
        Iterator<Resource> resourceIt = website.listResources();
        boolean isActionUrl = true;
        while (resourceIt.hasNext()) {
            Resource resource = resourceIt.next(); 
            if (resource.getResourceType()!=null && objectClass.getCanonicalName().equals(resource.getResourceType().getResourceClassName()) && resource.isActive()) {
                resourceId = resource.getId();
                break;
            }
        }
        if (null != resourceId) {
            String[] pathParts;
            if (urlExample.contains(delimiterAction)) {
                pathParts = urlExample.split(delimiterAction);
            } else {
                pathParts = urlExample.split(delimiterMode);
                isActionUrl = false;
            }
            if (pathParts.length > 1) {
                int index = pathParts[1].indexOf("/");
                actionPath.append(pathParts[0]);
                actionPath.append(isActionUrl ? delimiterAction : delimiterMode);
                actionPath.append(resourceId);
                actionPath.append(pathParts[1].substring(index));
            }
        }
        return actionPath.toString();
    }
}