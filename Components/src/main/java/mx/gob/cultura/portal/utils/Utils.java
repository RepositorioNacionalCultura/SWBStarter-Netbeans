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
import java.text.DecimalFormat;
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

import mx.gob.cultura.portal.response.Entry;
import mx.gob.cultura.portal.response.Title;
import mx.gob.cultura.portal.response.CountName;
import mx.gob.cultura.portal.response.Aggregation;
import static mx.gob.cultura.portal.utils.Constants.COMPLEMENTARY;
import static mx.gob.cultura.portal.utils.Constants.REQUIRED;

/**
 *
 * @author sergio.tellez
 */
public class Utils {
    
    protected static final Map m = new HashMap();
    
    protected static final Map inba = new HashMap();
    protected static final Map inali = new HashMap();
    protected static final Map redu = new HashMap();
    protected static final Map fona = new HashMap();
    protected static final Map ceim = new HashMap();
    protected static final Map cl22 = new HashMap();
    protected static final Map bcna = new HashMap();
    protected static final Map cenart = new HashMap();
    protected static final Map dgp = new HashMap();
    protected static final Map dgb = new HashMap();
    protected static final Map bv = new HashMap();
    protected static final Map imcine = new HashMap();
    
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
    
    static {
        inali.put("creator", REQUIRED);
        inali.put("lugar", COMPLEMENTARY);
        inali.put("datecreated", REQUIRED);
	inali.put("recordtitle", REQUIRED);
	inali.put("resourcetype", REQUIRED);
	inali.put("lang", COMPLEMENTARY);
        inali.put("reccollection", COMPLEMENTARY);
        inali.put("keywords", COMPLEMENTARY);
        inali.put("description", COMPLEMENTARY);
        inali.put("oaiid/identifier", REQUIRED);
        inali.put("holder", REQUIRED);
        inali.put("rights.rightstitle", REQUIRED);
        inali.put("rights.description+rights.url", REQUIRED);
    }
    
    static {
        inba.put("recordtitle", REQUIRED);
        inba.put("description", COMPLEMENTARY);
        inba.put("creator", REQUIRED);
        inba.put("resourcetype", REQUIRED);
        inba.put("datecreated", REQUIRED);
        inba.put("reccollection", COMPLEMENTARY);
        inba.put("keywords", COMPLEMENTARY);
        inba.put("oaiid/identifier", REQUIRED);
        inba.put("holder", REQUIRED);
        inba.put("rights.rightstitle", REQUIRED);
        inba.put("rights.description+rights.url", REQUIRED);
        inba.put("reference", COMPLEMENTARY);
	inba.put("lang", COMPLEMENTARY);
    }
    
    static {
        redu.put("oaiid/identifier", REQUIRED);
        redu.put("recordtitle", REQUIRED);
        redu.put("creator", REQUIRED);
        redu.put("dimension", REQUIRED);
        redu.put("unidad", REQUIRED);
        redu.put("datecreated", REQUIRED);
        redu.put("holder", REQUIRED);
        redu.put("resourcetype", REQUIRED);
        redu.put("rights.rightstitle", REQUIRED);
        redu.put("rights.description+rights.url", REQUIRED);
        redu.put("keywords", COMPLEMENTARY);
        redu.put("serie", COMPLEMENTARY);
        redu.put("chapter", COMPLEMENTARY);
        redu.put("credits", COMPLEMENTARY);
        redu.put("description", COMPLEMENTARY);
        redu.put("availableformats", COMPLEMENTARY);
    }
    
    static {
        fona.put("oaiid/identifier", REQUIRED);
        fona.put("recordtitle", REQUIRED);
        fona.put("creator", REQUIRED);
        fona.put("holder", REQUIRED);
        fona.put("dimension", REQUIRED);
        fona.put("unidad", REQUIRED);
        fona.put("datecreated", REQUIRED);
        fona.put("resourcetype", REQUIRED);
        fona.put("reccollection", COMPLEMENTARY);
        fona.put("keywords", COMPLEMENTARY);
        fona.put("rights.rightstitle", REQUIRED);
        fona.put("rights.description+rights.url", REQUIRED);
        fona.put("credits", COMPLEMENTARY);
    }
    
    static {
        ceim.put("oaiid/identifier", REQUIRED);
        ceim.put("recordtitle", REQUIRED);
        ceim.put("creator", REQUIRED);
        ceim.put("holder", REQUIRED);
        ceim.put("dimension", REQUIRED);
        ceim.put("unidad", REQUIRED);
        ceim.put("datecreated", REQUIRED);
        ceim.put("resourcetype", REQUIRED);
        ceim.put("keywords", COMPLEMENTARY);
        ceim.put("rights.rightstitle", REQUIRED);
        ceim.put("rights.description+rights.url", REQUIRED);
        ceim.put("documentalfund", COMPLEMENTARY);
    }
    
    static {
        cl22.put("oaiid/identifier", REQUIRED);
        cl22.put("recordtitle", REQUIRED);
        cl22.put("creator", REQUIRED);
        cl22.put("holder", REQUIRED);
        cl22.put("dimension", REQUIRED);
        cl22.put("unidad", REQUIRED);
        cl22.put("datecreated", REQUIRED);
        cl22.put("resourcetype", REQUIRED);
        cl22.put("holdernote", REQUIRED);
        cl22.put("keywords", COMPLEMENTARY);
        cl22.put("rights.rightstitle", REQUIRED);
        cl22.put("rights.description+rights.url", REQUIRED);
        cl22.put("episode", COMPLEMENTARY);
    }
    
    static {
        cenart.put("oaiid/identifier", REQUIRED);
        cenart.put("recordtitle", REQUIRED);
        cenart.put("creator", REQUIRED);
        cenart.put("holder", REQUIRED);
        cenart.put("dimension", REQUIRED);
        cenart.put("unidad", REQUIRED);
        cenart.put("datecreated", REQUIRED);
        cenart.put("resourcetype", REQUIRED);
        cenart.put("holdernote", REQUIRED);
        cenart.put("keywords", COMPLEMENTARY);
        cenart.put("rights.rightstitle", REQUIRED);
        cenart.put("rights.description+rights.url", REQUIRED);
        cenart.put("serie", COMPLEMENTARY);
        cenart.put("credits", COMPLEMENTARY);
        cenart.put("direction", COMPLEMENTARY);
        cenart.put("production", COMPLEMENTARY);
        cenart.put("music", COMPLEMENTARY);
        cenart.put("libreto", COMPLEMENTARY);
        cenart.put("musicaldirection", COMPLEMENTARY);
    }
    
    static {
        bcna.put("oaiid/identifier", REQUIRED);
        bcna.put("recordtitle", REQUIRED);
        bcna.put("chapter", REQUIRED);
        bcna.put("creator", REQUIRED);
        bcna.put("holder", REQUIRED);
        bcna.put("dimension", REQUIRED);
        bcna.put("unidad", REQUIRED);
        bcna.put("datecreated", REQUIRED);
        bcna.put("resourcetype", REQUIRED);
        bcna.put("keywords", COMPLEMENTARY);
        bcna.put("rights.rightstitle", REQUIRED);
        bcna.put("rights.description+rights.url", REQUIRED);
        bcna.put("credits", COMPLEMENTARY);
    }
    
    static {
        dgp.put("oaiid/identifier", REQUIRED);
        dgp.put("recordtitle", REQUIRED);
        dgp.put("number", REQUIRED);
        dgp.put("subtile", COMPLEMENTARY);
        dgp.put("creator", REQUIRED);
        dgp.put("holder", REQUIRED);
        dgp.put("dimension", REQUIRED);
        dgp.put("unidad", REQUIRED);
        dgp.put("datecreated", REQUIRED);
        dgp.put("resourcetype", REQUIRED);
        dgp.put("reccollection", COMPLEMENTARY);
        dgp.put("keywords", COMPLEMENTARY);
        dgp.put("rights.rightstitle", REQUIRED);
        dgp.put("rights.description+rights.url", REQUIRED);
        dgp.put("editorial", COMPLEMENTARY);
        dgp.put("serie", COMPLEMENTARY);
        dgp.put("credits", COMPLEMENTARY);
    }
    
    static {
        dgb.put("oaiid/identifier", REQUIRED);
        dgb.put("recordtitle", REQUIRED);
        dgb.put("creator", REQUIRED);
        dgb.put("holder", REQUIRED);
        dgb.put("dimension", REQUIRED);
        dgb.put("unidad", REQUIRED);
        dgb.put("datecreated", REQUIRED);
        dgb.put("resourcetype", REQUIRED);
        dgb.put("keywords", COMPLEMENTARY);
        dgb.put("rights.rightstitle", REQUIRED);
        dgb.put("rights.description+rights.url", REQUIRED);
        dgb.put("lugar+state", COMPLEMENTARY);
        dgb.put("press", COMPLEMENTARY);
    }
    
    static {
        bv.put("oaiid/identifier", REQUIRED);
        bv.put("recordtitle", REQUIRED);
        bv.put("holder", REQUIRED);
        bv.put("dimension", REQUIRED);
        bv.put("unidad", REQUIRED);
        bv.put("datecreated", REQUIRED);
        bv.put("resourcetype", REQUIRED);
        bv.put("keywords", COMPLEMENTARY);
        bv.put("rights.rightstitle", REQUIRED);
        bv.put("rights.description+rights.url", REQUIRED);
        bv.put("lugar", COMPLEMENTARY);
    }
    
    static {
        imcine.put("oaiid/identifier", REQUIRED);
        imcine.put("recordtitle", REQUIRED);
        imcine.put("creator", REQUIRED);
        imcine.put("holder", REQUIRED);
        imcine.put("datecreated", REQUIRED);
        imcine.put("resourcetype", REQUIRED);
        imcine.put("keywords", COMPLEMENTARY);
        imcine.put("rights.rightstitle", REQUIRED);
        imcine.put("rights.description+rights.url", REQUIRED);
        imcine.put("dimension+tipo_de_dimension", REQUIRED);
        imcine.put("unidad+tipo_de_unidad", REQUIRED);
        imcine.put("director", COMPLEMENTARY);
        imcine.put("producer", COMPLEMENTARY);
        imcine.put("screenplay", COMPLEMENTARY);
        imcine.put("credits", COMPLEMENTARY);
        imcine.put("distribution", COMPLEMENTARY);
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
    
    public static String decimalFormat(String format, int value) {
        if (null == format || format.isEmpty()) return "";
        DecimalFormat  formatter = new DecimalFormat (format);
        return formatter.format(value);
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
        org.bson.Document bson = new org.bson.Document("size", description.length());
        if (description.length() > 240) {
            bson.append("short", description.substring(0, 240)+"...");
            bson.append("full", description);
        }else
            bson.append("short", description);
        return bson;
    }
    
    public static String getRights(Entry entry) {
        if (null == entry || null == entry.getRights() || null == entry.getRights().getRightstitle() || entry.getRights().getRightstitle().isEmpty()) return "";
        return entry.getRights().getRightstitle();
    }
    
    public static String getParamSearch(String words) {
        StringBuilder parameters = new StringBuilder();
        String fix = words.replaceAll(",", " ").replaceAll(" ,", " ").replaceAll(", ", " ");
        String[] search = fix.split(" ");
        if (search.length > 0) {
            for (int i=0; i<search.length; i++) {
                parameters.append("%2B");
                String param  = search[i].trim();
                parameters.append(param);
            }
            if (parameters.length() > 1)
                parameters.delete(0, 3);
            return parameters.toString();
        }else
            return words;
    }
    
    public static String replaceSpecialChars(String txt) {
        if (null == txt || txt.isEmpty()) return "";
        return HtmlEntities.encode(txt);
    }
    
    public static boolean chdFtr(String filter, String type, String value) {
        if (null == filter || filter.isEmpty() || null == type || type.isEmpty() || null == value || value.isEmpty()) return false;
        if (type.equalsIgnoreCase("selfecha")) return filter.equalsIgnoreCase(value);
        String [] params = filter.split(",");
        for (int i=0; i<params.length; i++) {
            String [] pair = params[i].split(":");
            if (pair[0].equalsIgnoreCase(type) && pair[1].equals(value)) return true;
        }
        return false;
    }
    
    public static String getIvl(String filter, String type, Aggregation aggs) {
        String interval = "";
        if (null == filter) {
            if (type.equalsIgnoreCase("datestart")) interval =  aggs.getInterval().getLowerLimit().toString();
            else interval =  aggs.getInterval().getUpperLimit().toString();
        }else {
            String [] params = filter.split(",");
            for (int i=0; i<params.length; i++) {
                String [] pair = params[i].split(":");
                if (pair[0].equalsIgnoreCase(type)) interval = pair[1];
            }
        }
        return interval;
    }
    
    public static String getRowData(List<String> list, int size, boolean all) {
        StringBuilder builder = new StringBuilder();
        if (null == list || list.isEmpty()) return "";
        if (all) {
            for (String s : list) {
                if (null != s && !s.trim().isEmpty()) builder.append(s.trim()).append(", ");
            }
            if (builder.length() > 0) builder.deleteCharAt(builder.length() - 2);
        }else {
            if (null != list.get(0) && !list.get(0).trim().isEmpty()) {
                String data = list.get(0).trim();
                if (size > 0 && data.length() > size) data = data.substring(0, size)+"...";
                builder.append(data);
            }
        }
        return builder.toString();
    }
    
    public static String getTechData(String property, String holder, String key, String locale) {
        if (null == key || key.trim().isEmpty()) return "";
        if (!isRequired(property, holder)) return "";
        StringBuilder data = new StringBuilder();
        data.append("<tr>")
            .append("   <td>").append(locale).append("</td>")
            .append("   <td>").append(key).append("</td>")
            .append("</tr>");
        return data.toString();
    }
    
    private static boolean isRequired(String property, String holder) {
        if (null == holder || null == property) return false;
        if (holder.equals("Instituto Nacional de Bellas Artes")) return inba.containsKey(property);
        if (holder.equals("Instituto Nacional de Lenguas Indígenas")) return inali.containsKey(property);
        if (holder.equals("Radio Educación")) return redu.containsKey(property);
        if (holder.equals("Fonoteca Nacional")) return fona.containsKey(property);
        if (holder.equals("Centro de la Imagen")) return ceim.containsKey(property);
        if (holder.equals("Televisión Metropolitana S.A. de C.V.")) return cl22.containsKey(property);
        if (holder.equals("Centro Nacional de las Artes")) return cenart.containsKey(property);
        if (holder.equals("Biblioteca de las Artes del Centro Nacional de las Artes")) return bcna.containsKey(property);
        if (holder.equals("Dirección General de Publicaciones")) return dgp.containsKey(property);
        if (holder.equals("Dirección General de Bibliotecas")) return dgb.containsKey(property);
        if (holder.equals("Biblioteca Vasconcelos")) return bv.containsKey(property);
        if (holder.equals("Instituto Mexicano de Cinematografía")) return imcine.containsKey(property);
        return true;
    }
    
    public static String getTitle(List<Title> titles, int size) {
        List<String> list = new ArrayList<>();
        if (null == titles || titles.isEmpty()) return "";
        for (Title t : titles) {
            if (null != t && null != t.getValue() && !t.getValue().isEmpty()) {
                list.add(t.getValue());
            }
        }
        return getRowData(list, size, false);
    }
    
    public static String chdFtrList(List<CountName> resourcetypes, String filter, String resourcetype, String moretypes) {
        int i = 0;
        StringBuilder cde = new StringBuilder();
        for (CountName r : resourcetypes) {
            cde.append("<li><label class=\"form-check-label\"><input class=\"form-check-input\" type=\"checkbox\" onclick=\"filter()\" name=\"").append(resourcetype).append("\" value=\"").append(r.getName()).append("\"");
            if (chdFtr(filter, resourcetype, r.getName())) 
                cde.append("checked");
            cde.append("><span>").append(r.getName()).append("</span><span> ").append(Utils.decimalFormat("###,###", r.getCount())).append("</span></label></li>");
            if (i>3) break; 
            else i++; 
        }
	if (i<resourcetypes.size()) {
            cde.append("<div class=\"collapse\" id=\"").append(moretypes).append("\">");
            int j=0;
            for (CountName r : resourcetypes) {
		if (j<=i) {j++;} 
		else { 
                    cde.append("<li><label class=\"form-check-label\"><input class=\"form-check-input\" type=\"checkbox\" onclick=\"filter()\" name=\"").append(resourcetype).append("\" value=\"").append(r.getName()).append("\"");
                    if (chdFtr(filter, resourcetype, r.getName()))
                        cde.append("checked");
                    cde.append("><span>").append(r.getName()).append("</span><span> ").append(Utils.decimalFormat("###,###", r.getCount())).append("</span></label></li>");
                }
            }
            cde.append("</div>");
	}
        return cde.toString();
    }
    
    public static String getClientBrowser(HttpServletRequest request) {
        String browser = "";
        final String browserDetails = request.getHeader("User-Agent");
        final String user = browserDetails.toLowerCase();
        if (user.contains("msie")) {
            String substring = browserDetails.substring(browserDetails.indexOf("MSIE")).split(";")[0];
            browser = substring.split(" ")[0].replace("MSIE", "IE") + "-" + substring.split(" ")[1];
        } else if (user.contains("safari") && user.contains("version")) {
            browser = (browserDetails.substring(browserDetails.indexOf("Safari")).split(" ")[0]).split(
                    "/")[0] + "-" + (browserDetails.substring(
                            browserDetails.indexOf("Version")).split(" ")[0]).split("/")[1];
        } else if (user.contains("opr") || user.contains("opera")) {
            if (user.contains("opera")) {
                browser = (browserDetails.substring(browserDetails.indexOf("Opera")).split(" ")[0]).split(
                        "/")[0] + "-" + (browserDetails.substring(
                                browserDetails.indexOf("Version")).split(" ")[0]).split("/")[1];
            } else if (user.contains("opr")) {
                browser = ((browserDetails.substring(browserDetails.indexOf("OPR")).split(" ")[0]).replace("/",
                        "-")).replace(
                                "OPR", "Opera");
            }
        } else if (user.contains("chrome")) {
            browser = (browserDetails.substring(browserDetails.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
        } else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1) || (user.indexOf(
                "mozilla/4.7") != -1) || (user.indexOf("mozilla/4.78") != -1) || (user.indexOf(
                "mozilla/4.08") != -1) || (user.indexOf("mozilla/3") != -1)) {
            //browser=(userAgent.substring(userAgent.indexOf("MSIE")).split(" ")[0]).replace("/", "-");
            browser = "Netscape-?";

        } else if (user.contains("firefox")) {
            browser = (browserDetails.substring(browserDetails.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
        } else if (user.contains("rv")) {
            browser = "IE";
        } else {
            browser = "UnKnown, More-Info: " + browserDetails;
        }
        return browser;
    }
    
    public static String getFilterTypes(HttpServletRequest request, String filter) {
        StringBuilder types = new StringBuilder();
        List<CountName> filters = (List<CountName>)request.getAttribute(filter);
        if (null != filters && !filters.isEmpty()) {
            for (CountName c : filters) {
                types.append("::").append(c.getName());
            }
        }
        return types.toString();
    }
    
    public static String urlEmbedYT(String urlwatch) {
        StringBuilder watch = new StringBuilder();
        if (null == urlwatch || urlwatch.isEmpty()) return "";
        if (urlwatch.startsWith("https://youtu.be/")) return "https://www.youtube.com/embed/" + urlwatch.substring(17, urlwatch.length());
        if (urlwatch.startsWith("https://www.youtube.com/watch?v=")) {
            int index = urlwatch.indexOf("&", 32);
            if (index == -1) {
                watch.append("https://www.youtube.com/embed/")
                    .append(urlwatch.substring(32,urlwatch.length()));
                return watch.toString();
            }
            if (urlwatch.length() < 40) return urlwatch;
            String id = urlwatch.substring(32, index);
            watch.append("https://www.youtube.com/embed/")
                .append(id);
        }else return urlwatch;
        return watch.toString();
    }
    
    public static String c(String value) {
        if (null == value) return "";
        return value.trim();
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