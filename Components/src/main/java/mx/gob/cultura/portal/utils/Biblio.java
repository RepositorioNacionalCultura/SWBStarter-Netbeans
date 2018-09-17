
package mx.gob.cultura.portal.utils;

import java.util.HashMap;
import java.util.Map;
import static mx.gob.cultura.portal.utils.Constants.COMPLEMENTARY;
import static mx.gob.cultura.portal.utils.Constants.REQUIRED;

/**
 *
 * @author sergio.tellez
 */
public class Biblio {
    
    public static final Map inba = new HashMap();
    public static final Map inali = new HashMap();
    public static final Map redu = new HashMap();
    public static final Map fona = new HashMap();
    public static final Map ceim = new HashMap();
    public static final Map cl22 = new HashMap();
    public static final Map bcna = new HashMap();
    public static final Map cenart = new HashMap();
    public static final Map dgp = new HashMap();
    public static final Map dgb = new HashMap();
    public static final Map bv = new HashMap();
    public static final Map imcine = new HashMap();
    public static final Map dgcp = new HashMap();
    public static final Map inehrm = new HashMap();
    public static final Map munal = new HashMap();
    public static final Map mnsc  = new HashMap();
    public static final Map cidab  = new HashMap();
    public static final Map inbal = new HashMap();
    
    static {
        inba.put("recordtitle", REQUIRED);
        inba.put("creator", REQUIRED);
        inba.put("datecreated", REQUIRED);
        inba.put("reccollection", COMPLEMENTARY);
        inba.put("holder", REQUIRED);
        inba.put("resourcetype", REQUIRED);
        inba.put("reference", COMPLEMENTARY);
        inba.put("oaiid/identifier", REQUIRED);
        inba.put("rights.rightstitle", REQUIRED);
        inba.put("rights.description+rights.url", REQUIRED);
        inba.put("description", COMPLEMENTARY);
        inba.put("keywords", COMPLEMENTARY);
    }
    
    static {
        inali.put("oaiid/identifier", REQUIRED);
        inali.put("recordtitle", REQUIRED);
        inali.put("creator", REQUIRED);
        inali.put("datecreated", REQUIRED);
        inali.put("lugar+state", COMPLEMENTARY);
        inali.put("reccollection", COMPLEMENTARY);
	inali.put("lang", COMPLEMENTARY);
	inali.put("resourcetype", REQUIRED);
	inali.put("holder", REQUIRED);
        inali.put("rights.rightstitle", REQUIRED);
        inali.put("rights.description+rights.url", REQUIRED);
        inali.put("keywords", COMPLEMENTARY);
        inali.put("description", COMPLEMENTARY);
    }
    
    static {
        redu.put("oaiid/identifier", REQUIRED);
        redu.put("recordtitle", REQUIRED);
        redu.put("creator", REQUIRED);
        redu.put("dimension+unidad", REQUIRED);
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
        fona.put("dimension+unidad", REQUIRED);
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
        ceim.put("serie", COMPLEMENTARY);
        ceim.put("dimension+unidad", REQUIRED);
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
    
    static {
        dgcp.put("oaiid/identifier", REQUIRED);
        dgcp.put("recordtitle", REQUIRED);
        dgcp.put("serie", COMPLEMENTARY);
        dgcp.put("creator", REQUIRED);
        dgcp.put("unidad", REQUIRED);
        dgcp.put("datecreated", REQUIRED);
        dgcp.put("resourcetype", REQUIRED);
        dgcp.put("holder", REQUIRED);
        dgcp.put("keywords", COMPLEMENTARY);
        dgcp.put("rights.rightstitle", REQUIRED);
        dgcp.put("rights.description+rights.url", REQUIRED);
        dgcp.put("lugar+state", COMPLEMENTARY);
    }
    
    static {
        inehrm.put("oaiid/identifier", REQUIRED);
        inehrm.put("recordtitle", REQUIRED);
        inehrm.put("reccollection", COMPLEMENTARY);
        inehrm.put("creator", REQUIRED);
        inehrm.put("resourcetype", REQUIRED);
        inehrm.put("datecreated", REQUIRED);
        inehrm.put("lang", REQUIRED);
        inehrm.put("dimension+unidad", REQUIRED);
        inehrm.put("holder", REQUIRED);
        inehrm.put("keywords", COMPLEMENTARY);
        inehrm.put("rights.rightstitle", REQUIRED);
        inehrm.put("rights.description+rights.url", REQUIRED);
        inehrm.put("director", COMPLEMENTARY);
        inehrm.put("producer", COMPLEMENTARY);
        inehrm.put("screenplay", COMPLEMENTARY);
        inehrm.put("credits", COMPLEMENTARY);
        inehrm.put("distribution", COMPLEMENTARY);
    }
    
    static {
        munal.put("oaiid/identifier", REQUIRED);
        munal.put("resourcetype", REQUIRED);
        munal.put("recordtitle", REQUIRED);
        munal.put("creator", REQUIRED);
        munal.put("lang", REQUIRED);
        munal.put("dimension+tipo_de_dimension", REQUIRED);
        munal.put("unidad+tipo_de_unidad", REQUIRED);
        munal.put("holder", REQUIRED);
        munal.put("keywords", COMPLEMENTARY);
        munal.put("rights.rightstitle", REQUIRED);
        munal.put("rights.description+rights.url", REQUIRED);
    }
    
    static {
        mnsc.put("recordtitle", REQUIRED);
        mnsc.put("creator", REQUIRED);
        mnsc.put("datecreated", REQUIRED);
        mnsc.put("reccollection", COMPLEMENTARY);
        mnsc.put("holder", REQUIRED);
        mnsc.put("resourcetype", REQUIRED);
        mnsc.put("oaiid/identifier", REQUIRED);
        mnsc.put("rights.rightstitle", REQUIRED);
        mnsc.put("rights.description+rights.url", REQUIRED);
        mnsc.put("description", COMPLEMENTARY);
        mnsc.put("keywords", COMPLEMENTARY);
    }
    
    static {
        cidab.put("recordtitle", REQUIRED);
        cidab.put("creator", REQUIRED);
        cidab.put("datecreated", REQUIRED);
        cidab.put("reccollection", COMPLEMENTARY);
        cidab.put("holder", REQUIRED);
        cidab.put("resourcetype", REQUIRED);
        cidab.put("oaiid/identifier", REQUIRED);
        cidab.put("rights.rightstitle", REQUIRED);
        cidab.put("rights.description+rights.url", REQUIRED);
        cidab.put("description", COMPLEMENTARY);
        cidab.put("keywords", COMPLEMENTARY);
    }
    
    static {
        inbal.put("recordtitle", REQUIRED);
        inbal.put("creator", REQUIRED);
        inbal.put("datecreated", REQUIRED);
        inbal.put("reccollection", COMPLEMENTARY);
        inbal.put("holder", REQUIRED);
        inbal.put("resourcetype", REQUIRED);
        inbal.put("oaiid/identifier", REQUIRED);
        inbal.put("rights.rightstitle", REQUIRED);
        inbal.put("rights.description+rights.url", REQUIRED);
        inbal.put("description", COMPLEMENTARY);
        inbal.put("keywords", COMPLEMENTARY);
    }
    
    public static boolean isRequired(String property, String holder) {
        boolean isRequired;
        if (null == holder || null == property) return false;
        switch (holder) {
            case  "Instituto Nacional de Bellas Artes" : isRequired = inba.containsKey(property); break;
            case  "Instituto Nacional de Bellas Artes y Literatura" : isRequired = inbal.containsKey(property); break;
            case  "Instituto Nacional de Lenguas Indígenas" : isRequired = inali.containsKey(property); break;
            case  "Radio Educación" : isRequired = redu.containsKey(property); break;
            case  "Fonoteca Nacional" : isRequired = fona.containsKey(property); break;
            case  "Centro de la Imagen" : isRequired = ceim.containsKey(property); break;
            case  "Televisión Metropolitana S.A. de C.V." : isRequired = cl22.containsKey(property); break;
            case  "Centro Nacional de las Artes" : isRequired = cenart.containsKey(property); break;
            case  "Biblioteca de las Artes del Centro Nacional de las Artes" : isRequired = bcna.containsKey(property); break;
            case  "Dirección General de Publicaciones" : isRequired = dgp.containsKey(property); break;
            case  "Dirección General de Bibliotecas" : isRequired = dgb.containsKey(property); break;
            case  "Biblioteca Vasconcelos" : isRequired = bv.containsKey(property); break;
            case  "Instituto Mexicano de Cinematografía" : isRequired = imcine.containsKey(property); break;
            case  "Dirección General de Culturas Populares e Indígenas": isRequired = dgcp.containsKey(property); break;
            case  "Instituto Nacional de Estudios Históricos de las Revoluciones de México" : isRequired = inehrm.containsKey(property); break;
            case  "Museo Nacional de Arte" : isRequired = munal.containsKey(property); break;
            case  "Museo Nacional de San Carlos" : isRequired = mnsc.containsKey(property); break;
            case  "Centro de Información y Documentación Alberto Beltrán" : isRequired = cidab.containsKey(property); break;
            default: isRequired = false;
        }
        return isRequired;
    }
}