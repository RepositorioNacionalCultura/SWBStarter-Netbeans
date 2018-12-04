/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.utils;

import java.io.Writer;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;

import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.text.Format;
import java.text.DecimalFormat;
import java.text.ParseException;

import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;
import java.util.Iterator;

import java.util.Arrays;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

import org.semanticwb.Logger;
import org.semanticwb.model.WebSite;
import org.semanticwb.model.Resource;
import javax.servlet.http.HttpServletRequest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mx.gob.cultura.portal.response.Entry;
import mx.gob.cultura.portal.response.Title;
import mx.gob.cultura.portal.response.Rights;
import mx.gob.cultura.portal.response.CountName;
import mx.gob.cultura.portal.response.Aggregation;
import mx.gob.cultura.portal.response.DigitalObject;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION_PRIVATE;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION_PUBLIC;
import org.semanticwb.SWBUtils;

import org.w3c.dom.Element;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author sergio.tellez
 */
public class Utils {
    
    protected static final Map m = new HashMap();
    
    private static final Logger LOG = SWBUtils.getLogger(Utils.class);
	
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
    
    public static String getTechData(String property, String holder, String key, String locale, boolean basic, boolean notNull) {
        if (notNull && (null == key || key.trim().isEmpty())) return "";
        if (!basic && !Biblio.isRequired(property, holder)) return "";
        StringBuilder data = new StringBuilder();
        key = null != key ? key.trim() : "";
        data.append("<tr>")
            .append("   <td>").append(locale).append("</td>")
            .append("   <td>").append(key).append("</td>")
            .append("</tr>");
        return data.toString();
    }
    
    public static String getTechRepl(String property, String holder, String key, String locale, boolean basic, boolean notNull) {
        if (notNull && (null == key || key.trim().isEmpty())) return "";
        if (!basic && !Biblio.isRequired(property, holder)) return "";
        StringBuilder data = new StringBuilder();
        key = null != key ? key.trim() : "";
        data.append("<tr>")
            .append("   <td>").append(Utils.replaceSpecialChars(locale)).append("</td>")
            .append("   <td>").append(Utils.replaceSpecialChars(key)).append("</td>")
            .append("</tr>");
        return data.toString();
    }
    
    public static String concatLink(String userLang, String site, boolean all, String... args) {
        StringBuilder link = new StringBuilder();
        if (null != args && null != args[0] && !args[0].trim().isEmpty()) {
            String url = "<a href=\"/" + userLang + "/" + site + "/resultados?word=";
            if (all) {
                for (int i=0; i<args.length; i++) {
                    if (null != args[i] && !args[i].trim().isEmpty()) {
                        link.append(", ").append(url).append(args[i]).append("\">").append(args[i].trim()).append("</a>");
                    }
                }
            }else {
                link.append(" ").append(url).append(args[0]).append("\">").append(args[0]);
                if (null != args[1] && !args[1].trim().isEmpty())
                    link.append(" ").append(args[1]);
                link.append("</a>");
            }
            if (link.length() > 0) link.deleteCharAt(0);
        }
        return link.toString();
    }
   
    public static String concatFilter(String userLang, String site, String attribute, List<String> args) {
        StringBuilder link = new StringBuilder();
        if (null == args || args.isEmpty()) return "";
        String url = "<a href=\"/" + userLang + "/" + site + "/resultados?word=*&theme=";
        for (String arg : args) {
            if (null != arg && !arg.trim().isEmpty())
                link.append(", ").append(url).append(arg.trim()).append("&filter=").append(attribute).append(":").append(arg).append("\">").append(arg.trim()).append("</a>");
        }
        if (link.length() > 0) link.deleteCharAt(0);
        return link.toString();
    }
    
    public static String concatReplace(String userLang, String site, String attribute, List<String> args, String regex, String replacement) {
        String filter = concatFilter(userLang, site, attribute, args);
        if (!filter.isEmpty() && null != regex && !regex.isEmpty() && null != replacement && !replacement.isEmpty() ) {
            filter = filter.replaceAll(regex, replacement);
        }
        return filter;
    }
    
    public static String getCreator(List<String> names) {
        int i = 0;
        if (null == names) return "";
        StringBuilder creator = new StringBuilder();
        for (String name : names) {
            creator.append(" ").append(name);
            if (i == 1) break;
            i++;
        }
        if (creator.length() > 1) creator.deleteCharAt(0);
        return creator.toString();
    }
    
    public static List<String> getMedia(Rights rights) {
        if (null != rights && null != rights.getMedia()) {
            if (null != rights.getMedia().getName()) return getList(rights.getMedia().getName());
            return getList(rights.getMedia().getMime());
        }
        return new ArrayList();
    }
    
    public static List<String> getList(String item) {
        if (null != item && !item.isEmpty()) {
            return Arrays.asList(mediaLbl(item));
        }
        return new ArrayList();
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
        if (sDate.length() <= 4 && toInt(sDate) > -1) return sDate;
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
            try {
                result = Integer.parseInt((String)obj);
            }catch (Exception e) {
                result = 0;
            }
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
    
    public static org.bson.Document getDescription(List<String> descriptions, int size) {
        if (null == descriptions || descriptions.isEmpty()) return null;
        String description = descriptions.get(0);
        if (null == description || description.isEmpty()) return null;
        org.bson.Document bson = new org.bson.Document("size", description.length());
        if (description.length() > size) {
            bson.append("short", description.substring(0, size)+"...");
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
        String [] params = filter.split(";;");
        for (int i=0; i<params.length; i++) {
            String [] pair = params[i].split(":");
            if (type.equalsIgnoreCase("rightsmedia") && pair[0].equalsIgnoreCase(type) && isMedia(pair[1], value)) return true;
            if (pair[0].equalsIgnoreCase(type) && pair[1].equals(value)) return true;
        }
        return false;
    }
    
    public static boolean isMedia(String media, String value) {
        if (null == media || null == value) return false;
        if (media.equalsIgnoreCase("jpg") || media.equalsIgnoreCase("png") || media.startsWith("image")) return value.equalsIgnoreCase("Imagen");
        if (media.equalsIgnoreCase("aiff") || media.equalsIgnoreCase("wav") || media.equalsIgnoreCase("mp3") || media.startsWith("audio")) return value.equalsIgnoreCase("Audio");
        if (media.equalsIgnoreCase("avi") || media.startsWith("video") || media.equalsIgnoreCase("mp4") || media.equalsIgnoreCase("mov")) return value.equalsIgnoreCase("Video");
        if (media.equalsIgnoreCase("pdf") || media.equalsIgnoreCase("application/pdf")) return value.equalsIgnoreCase("PDF");
        if (media.startsWith("model/x3d") || media.equalsIgnoreCase("3d")) return value.equalsIgnoreCase("3D");
        if (media.equalsIgnoreCase("zip") || media.equalsIgnoreCase("application/zip")) return value.equalsIgnoreCase("ZIP");
        if (media.equalsIgnoreCase("application/epub+zip")) return value.equalsIgnoreCase("EPUB");
        return false;
    }
    
    public static String mediaLbl(String media) {
        if (null == media || media.isEmpty()) return "";
        if (media.equalsIgnoreCase("jpg") || media.equalsIgnoreCase("png") || media.startsWith("image")) return "Imagen";
        if (media.equalsIgnoreCase("aiff") || media.equalsIgnoreCase("wav") || media.equalsIgnoreCase("mp3") || media.startsWith("audio")) return "Audio";
        if (media.equalsIgnoreCase("avi") || media.startsWith("video") || media.equalsIgnoreCase("mp4") || media.equalsIgnoreCase("mov")) return "Video";
        if (media.equalsIgnoreCase("pdf") || media.equalsIgnoreCase("application/pdf")) return "Texto";
        if (media.startsWith("model/x3d") || media.equalsIgnoreCase("3d")) return "3D";
        if (media.equalsIgnoreCase("zip") || media.equalsIgnoreCase("application/zip")) return "ZIP";
        if (media.equalsIgnoreCase("application/epub+zip")) return "Texto";
        if (media.equalsIgnoreCase("texto")) return "Texto";
        if (media.equalsIgnoreCase("multimedia")) return "Multimedia";
        if (media.equalsIgnoreCase("Conjunto de archivos")) return "Conjunto de archivos";
        return media;
    }
    
    public static String getIvl(String filter, String type, Aggregation aggs) {
        String interval = "";
        if (null == filter || !filter.contains("datestart")) {
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
    
    public static String getRowHold(List<String> list, int size, boolean all) {
        StringBuilder builder = new StringBuilder();
        if (null == list || list.isEmpty()) return "";
        if (all) {
            for (String s : list) {
                if (null != s && !s.trim().isEmpty()) builder.append(Utils.clder(s)).append(", ");
            }
            if (builder.length() > 0) builder.deleteCharAt(builder.length() - 2);
        }else {
            if (null != list.get(0) && !list.get(0).trim().isEmpty()) {
                String data = Utils.clder(list.get(0));
                if (size > 0 && data.length() > size) data = data.substring(0, size)+"...";
                builder.append(data);
            }
        }
        return builder.toString();
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
    
    public static String chdFtrList(List<CountName> resourcetypes, String filter, String resourcetype, String moretypes, boolean showcount) {
        int i = 0;
        StringBuilder cde = new StringBuilder();
        for (CountName r : resourcetypes) {
            cde.append("<li><label class=\"form-check-label\"><input class=\"form-check-input\" type=\"checkbox\" onclick=\"filter()\" name=\"").append(resourcetype).append("\" value=\"").append(r.getName()).append("\"");
            if (chdFtr(filter, resourcetype, r.getName())) 
                cde.append("checked");
            cde.append("><span>").append(r.getName()).append("</span><span> ");
            if (showcount) cde.append(Utils.decimalFormat("###,###", r.getCount()));
            cde.append("</span><span class=\"checkmark\"></span></label></li>");
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
                    cde.append("><span>").append(r.getName()).append("</span><span> ");
                    if (showcount) cde.append(Utils.decimalFormat("###,###", r.getCount()));
                    cde.append("</span><span class=\"checkmark\"></span></label></li>");
                }
            }
            cde.append("</div>");
	}
        return cde.toString();
    }
    
    public static String chdFtrHold(List<CountName> resourcetypes, String filter, String resourcetype, String moretypes, boolean showcount) {
        int i = 0;
        StringBuilder cde = new StringBuilder();
        for (CountName r : resourcetypes) {
            cde.append("<li><label class=\"form-check-label\"><input class=\"form-check-input\" type=\"checkbox\" onclick=\"filter()\" name=\"").append(resourcetype).append("\" value=\"").append(r.getName()).append("\"");
            if (chdFtr(filter, resourcetype, r.getName())) 
                cde.append("checked");
            cde.append("><span>").append(Utils.clder(r.getName())).append("</span><span> ");
            if (showcount) cde.append(Utils.decimalFormat("###,###", r.getCount()));
            cde.append("</span><span class=\"checkmark\"></span></label></li>");
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
                    cde.append("><span>").append(Utils.clder(r.getName())).append("</span><span> ");
                    if (showcount) cde.append(Utils.decimalFormat("###,###", r.getCount()));
                    cde.append("</span><span class=\"checkmark\"></span></label></li>");
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
        } else if (user.contains("edge")) {
            browser = (user.substring(user.indexOf("edge")).split(" ")[0]).replace("/", "-");
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
        if (urlwatch.startsWith("https://vimeo.com/")) return "https://player.vimeo.com/video/" + urlwatch.substring(18, urlwatch.length());
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
    
    public static List<DigitalObject> getAudio(Entry entry, List<Entry> series) {
        List<DigitalObject> audios = new ArrayList<>();
        if (null != entry) {
            if (null != entry.getDigitalObject()) {
                audios.addAll(entry.getDigitalObject());
                if (null != series && !series.isEmpty()) {
                    for (Entry serie : series) {
                        List<DigitalObject> list = serie.getDigitalObject();
                        if (null != list) {
                            for (DigitalObject digital : list) {
                                String type = (null != digital.getMediatype() && null != digital.getMediatype().getMime()) ?  digital.getMediatype().getMime() : "";
                                if (!type.isEmpty() && type.equalsIgnoreCase("wav") || type.equalsIgnoreCase("mp3")) {
                                    if (!entry.getDigitalObject().contains(digital)) audios.add(digital);
                                }
                            }
                        }
                    }
                }
            }
        }
        return audios;
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
    
    public static String scriptFBCln(HttpServletRequest request) {
        
        StringBuilder ret = new StringBuilder(128);
        ret.append("<script>\n");
        ret.append("  function fbCln(qs) {\n");
        ret.append("    FB.ui({\n");
        ret.append("      method: 'share',\n");
        ret.append("      display: 'popup',\n");
        ret.append("      href: '");
        ret.append(request.getRequestURL());
        ret.append("?'+qs");
        ret.append(",\n");
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
    
    public static Element getOSDProps(String path) {
        Element eElement = null;
        try {
            URL url = new URL(path);
            URLConnection urlConnection = url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(in);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("IMAGE_PROPERTIES");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    eElement = (Element) nNode;
                }
            }
            in.close();
        } catch (Exception ex) {
           ex.printStackTrace();
        }
        return eElement;
    }
    
    public static String getFormat(Entry entry) {
        String format = "";
        if (null != entry && null != entry.getDigitalObject() && !entry.getDigitalObject().isEmpty()) {
            DigitalObject o = entry.getDigitalObject().get(0);
            if (null != o && null != o.getMediatype() && null != o.getMediatype().getMime() && !o.getMediatype().getMime().trim().isEmpty())
                format = o.getMediatype().getMime();
        }
        return format.toUpperCase();
    }
    
    public static Boolean getStatus(String status, boolean prevst) {
         if (null == status || status.trim().isEmpty()) return !prevst;
         if (status.equalsIgnoreCase("true")) return COLLECTION_PUBLIC;
         return COLLECTION_PRIVATE;
     }
    
    public static Boolean isThmChk(String id, String prop) {
        if (null == id || null == prop || id.trim().isEmpty() || prop.trim().isEmpty()) return false;
        int fromIndex = toInt(id)-1;
        if (fromIndex >=0 && fromIndex < prop.length()) {
            String seq = prop.substring(fromIndex, fromIndex+1);
            return seq.equalsIgnoreCase("1");
        }
        return false;
    }
    
    public static String clder(String holder) {
        if (null == holder || holder.trim().isEmpty()) return "";
        if (!holder.equalsIgnoreCase("Instituto Mexicano de Cinematografía")) return holder;
        String cl = "";
        try {
            cl += URLEncoder.encode(holder, StandardCharsets.UTF_8.name());
            if (cl.contains("%C2%A0")) {
                cl = cl.replaceAll("%C2%A0", " ");
                cl = URLDecoder.decode(cl, StandardCharsets.UTF_8.name());
            }
            else return holder;
        } catch (UnsupportedEncodingException uex) {
            cl = holder;
        }
        return cl;
    }
}