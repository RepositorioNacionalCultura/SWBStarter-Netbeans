/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.resources;

import java.util.List;
import org.bson.Document;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import com.google.gson.Gson;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.gob.cultura.portal.persist.CollectionMgr;
import mx.gob.cultura.portal.persist.UserCollectionMgr;

import mx.gob.cultura.portal.response.Entry;
import mx.gob.cultura.portal.response.Collection;
import mx.gob.cultura.portal.request.GetBICRequest;
import mx.gob.cultura.portal.response.UserCollection;
import static mx.gob.cultura.portal.utils.Constants.COUNT_BY_REST;
import static mx.gob.cultura.portal.utils.Constants.COUNT_BY_STAT;
import static mx.gob.cultura.portal.utils.Constants.COUNT_BY_USER;
import static mx.gob.cultura.portal.utils.Constants.COUNT_BY_FAVS;

import static mx.gob.cultura.portal.utils.Constants.COLLECTION;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION_PUBLIC;

import static mx.gob.cultura.portal.utils.Constants.COLLECTION_TYPE;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION_TYPE_ALL;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION_TYPE_FAV;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION_TYPE_FND;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION_TYPE_OWN;

import org.semanticwb.model.User;
import org.semanticwb.SWBPlatform;
import org.semanticwb.portal.api.SWBResourceURL;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBActionResponse;
import org.semanticwb.portal.api.SWBResourceException;

import mx.gob.cultura.portal.utils.Utils;
import static mx.gob.cultura.portal.utils.Constants.FULL_LIST;
import static mx.gob.cultura.portal.utils.Constants.MODE_PAGE;
import static mx.gob.cultura.portal.utils.Constants.NUM_PAGE_JUMP;
import static mx.gob.cultura.portal.utils.Constants.NUM_PAGE_LIST;
import static mx.gob.cultura.portal.utils.Constants.PARAM_REQUEST;

import static mx.gob.cultura.portal.utils.Constants.NUM_ROW;
import static mx.gob.cultura.portal.utils.Constants.NUM_RECORDS_TOTAL;
import static mx.gob.cultura.portal.utils.Constants.NUM_RECORDS_VISIBLE;

import static mx.gob.cultura.portal.utils.Constants.PAGE_LIST;
import static mx.gob.cultura.portal.utils.Constants.TOTAL_PAGES;
import static mx.gob.cultura.portal.utils.Constants.PAGE_NUM_ROW;
import static mx.gob.cultura.portal.utils.Constants.PAGE_JUMP_SIZE;
import static mx.gob.cultura.portal.utils.Constants.THEME;


/**
 *
 * @author sergio.tellez
 */
public class MyCollections extends GenericResource {
    
    public static final String ENTRY = "entry";
    public static final String IDENTIFIER = "id";
    
    public static final String MODE_ADD = "ADD";
    public static final String MODE_EDIT = "EDIT";
    public static final String ACTION_STA = "STA";
    public static final String ACTION_ADD = "SAVE";
    public static final String MODE_RES_ADD = "RES_ADD";
    public static final String MODE_VIEW_USR = "VIEW_USR";
    public static final String MODE_VIEW_ALL = "VIEW_ALL";
    public static final String ACTION_DEL_FAV = "DEL_FAV";
    public static final String ACTION_ADD_CLF = "ADD_CLF";
    public static final String MODE_VIEW_FIND = "VIEW_FIND";
    public static final String MODE_VIEW_MYALL = "VIEW_MYALL";
    public static final String MODE_VIEW_MYFAV = "VIEW_MYFAV";
    public static final String MODE_VIEW_THEME = "VIEW_THEME";
   
    public static final String COLLECTION_RENDER = "_collection";
    
    private static final Logger LOG = Logger.getLogger(MyCollections.class.getName());
    
    CollectionMgr mgr = CollectionMgr.getInstance();
    UserCollectionMgr umr = UserCollectionMgr.getInstance();
    
    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        String mode = paramRequest.getMode();
        if (MODE_ADD.equals(mode)) {
            doAdd(request, response, paramRequest);
        }else if (MODE_EDIT.equals(mode)) {
            doEdit(request, response, paramRequest);
        }else if (MODE_PAGE.equals(mode)) {
            doPage(request, response, paramRequest);
        }else if (MODE_VIEW_USR.equals(mode)) {
            collectionById(request, response, paramRequest);
        }else if (MODE_RES_ADD.equals(mode)) {
            redirectJsonResponse(request, response);
        }else if (MODE_VIEW_ALL.equals(mode)) {
            doViewAll(request, response, paramRequest);
        }else if (MODE_VIEW_MYALL.equals(mode)) {
            doViewMyAll(request, response, paramRequest);
        }else if (MODE_VIEW_FIND.equals(mode)) {
            doSearch(request, response, paramRequest);
        }else if (MODE_VIEW_MYFAV.equals(mode)) {
            doViewMyFav(request, response, paramRequest);
        }else if (MODE_VIEW_THEME.equals(mode)) {
            doViewTheme(request, response, paramRequest);
        }else
            super.processRequest(request, response, paramRequest);
    }
    
    public void doViewMyAll(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        Integer favs = 0;
        Integer total = 0;
        User user = paramRequest.getUser();
        response.setContentType("text/html; charset=UTF-8");
        String path = "/swbadmin/jsp/rnc/collections/mycollections.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            if (null != user && user.isSigned()) {
                total = count(user.getId(), null).intValue();
                favs = umr.countByUser(paramRequest.getUser().getId()).intValue();
            }
            List<Collection> collectionList = collectionList(paramRequest.getUser(), 1, NUM_ROW);
            setAuthors(paramRequest, collectionList);
            setCovers(paramRequest, collectionList, 3);
            request.setAttribute(FULL_LIST, collectionList);
            request.setAttribute(PARAM_REQUEST, paramRequest);
            //request.setAttribute("mycollections", collectionList);
            request.setAttribute(NUM_RECORDS_TOTAL, total);
            request.setAttribute(COUNT_BY_FAVS, favs);
            request.setAttribute(COUNT_BY_USER, total);
            request.setAttribute(COUNT_BY_STAT, count(null, COLLECTION_PUBLIC).intValue());
            init(request);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        Integer favs = 0;
        Integer total = 0;
        User user = paramRequest.getUser();
        response.setContentType("text/html; charset=UTF-8");
        String path = "/swbadmin/jsp/rnc/collections/init.jsp";
        try {
            request.setAttribute(PARAM_REQUEST, paramRequest);
            if (null != user && user.isSigned()) {
                total = count(user.getId(), null).intValue();
                favs = umr.countByUser(paramRequest.getUser().getId()).intValue();
            }
            if (total > 0) {
                path = "/swbadmin/jsp/rnc/collections/mycollections.jsp";
                List<Collection> collectionList = collectionList(paramRequest.getUser(), 1, NUM_ROW);
                setAuthors(paramRequest, collectionList);
                setCovers(paramRequest, collectionList, 3);
                request.setAttribute(FULL_LIST, collectionList);
                request.setAttribute(PARAM_REQUEST, paramRequest);
                request.setAttribute(NUM_RECORDS_TOTAL, total);
                request.setAttribute(COUNT_BY_USER, total);
                init(request);
            }
            request.setAttribute(NUM_RECORDS_TOTAL, total);
            request.setAttribute(COUNT_BY_FAVS, favs);
            request.setAttribute(COUNT_BY_STAT, count(null, COLLECTION_PUBLIC).intValue());
            RequestDispatcher rd = request.getRequestDispatcher(path);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    public void doViewAll(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        Integer favs = 0;
        Integer total = 0;
        response.setContentType("text/html; charset=UTF-8");
        String path = "/swbadmin/jsp/rnc/collections/mycollections.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            total = count(null, COLLECTION_PUBLIC).intValue();
            favs = umr.countByUser(paramRequest.getUser().getId()).intValue();
            int count = count(paramRequest.getUser().getId(), null).intValue();
            List<Collection> collectionList = collectionList(null, 1, NUM_ROW);
            setAuthors(paramRequest, collectionList);
            setCovers(paramRequest, collectionList, 3);
            request.setAttribute(FULL_LIST, collectionList);
            request.setAttribute(PARAM_REQUEST, paramRequest);
            request.setAttribute(NUM_RECORDS_TOTAL, total);
            request.setAttribute(COUNT_BY_STAT, total);
            request.setAttribute(COUNT_BY_USER, count);
            request.setAttribute(COUNT_BY_FAVS, favs);
            request.setAttribute(COLLECTION_TYPE, COLLECTION_TYPE_ALL);
            init(request);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    public void doPage(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, java.io.IOException {
        int pagenum = 0;
        Integer total = 0;
        String p = request.getParameter("p");
        List<Collection> collectionList = null;
        if (null != p)  pagenum = Integer.parseInt(p);
        if (pagenum<=0) pagenum = 1;
        request.setAttribute(NUM_PAGE_LIST, pagenum);
        request.setAttribute(PAGE_NUM_ROW, NUM_ROW);
        if (Utils.toInt(request.getParameter(COLLECTION_TYPE)) == COLLECTION_TYPE_ALL) {
            total = count(null, COLLECTION_PUBLIC).intValue();
            collectionList = collectionList(null, pagenum, NUM_ROW);
            request.setAttribute(COLLECTION_TYPE, COLLECTION_TYPE_ALL);
        } else if (Utils.toInt(request.getParameter(COLLECTION_TYPE)) == COLLECTION_TYPE_FND) {
            Document reference = getReference(request, paramRequest, pagenum);
            total = (Integer)reference.get("records");
            collectionList = (List<Collection>)reference.get("references");
            request.setAttribute(COLLECTION_TYPE, COLLECTION_TYPE_FND);
        } else if (Utils.toInt(request.getParameter(COLLECTION_TYPE)) == COLLECTION_TYPE_FAV) {
            total = umr.countByUser(paramRequest.getUser().getId()).intValue();
            collectionList = mgr.favorites(paramRequest.getUser().getId(), pagenum, NUM_ROW);
            request.setAttribute(COLLECTION_TYPE, COLLECTION_TYPE_FAV);
        }  else {
            total = count(paramRequest.getUser().getId(), null).intValue();
            collectionList = collectionList(paramRequest.getUser(), pagenum, NUM_ROW);
            request.setAttribute(COLLECTION_TYPE, COLLECTION_TYPE_OWN);
        }
        setAuthors(paramRequest, collectionList);
        setCovers(paramRequest, collectionList, 3);
        request.setAttribute(FULL_LIST, collectionList);
        
        request.setAttribute(NUM_RECORDS_TOTAL, total);
        page(pagenum, request);
        String url = "/swbadmin/jsp/rnc/collections/rows.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(url);
        try {
            request.setAttribute(PARAM_REQUEST, paramRequest);
            rd.include(request, response);
        }catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    public void doSearch(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        String path = "/swbadmin/jsp/rnc/collections/mycollections.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            Document reference = getReference(request, paramRequest, 1);
            Integer results = (Integer)reference.get("records");
            List<Collection> collectionList = (List<Collection>)reference.get("references");
            request.setAttribute(FULL_LIST, collectionList);
            request.setAttribute(PARAM_REQUEST, paramRequest);
            request.setAttribute(NUM_RECORDS_TOTAL, results);
            request.setAttribute(COUNT_BY_REST, results);
            request.setAttribute(COUNT_BY_STAT, count(null, COLLECTION_PUBLIC).intValue());
            request.setAttribute(COUNT_BY_USER, count(paramRequest.getUser().getId(), null).intValue());
            request.setAttribute(COUNT_BY_FAVS, umr.countByUser(paramRequest.getUser().getId()).intValue());
            request.setAttribute(COLLECTION_TYPE, COLLECTION_TYPE_FND);
            init(request);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    public void doViewMyFav(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        Integer total = 0;
        response.setContentType("text/html; charset=UTF-8");
        String path = "/swbadmin/jsp/rnc/collections/mycollections.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            total = umr.countByUser(paramRequest.getUser().getId()).intValue();
            List<Collection> collectionList = mgr.favorites(paramRequest.getUser().getId(), 1, NUM_ROW);
            setAuthors(paramRequest, collectionList);
            setCovers(paramRequest, collectionList, 3);
            request.setAttribute(FULL_LIST, collectionList);
            request.setAttribute(PARAM_REQUEST, paramRequest);
            request.setAttribute(NUM_RECORDS_TOTAL, total);
            request.setAttribute(COUNT_BY_FAVS, total);
            request.setAttribute(COUNT_BY_STAT, count(null, COLLECTION_PUBLIC).intValue());
            request.setAttribute(COUNT_BY_USER, count(paramRequest.getUser().getId(), null).intValue());
            request.setAttribute(COLLECTION_TYPE, COLLECTION_TYPE_FAV);
            init(request);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    public void doViewTheme(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        String themes = "00000000";
        response.setContentType("text/html; charset=UTF-8");
        String path = "/swbadmin/jsp/rnc/collections/mycollections.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        List<Collection> collectionList = getThemes(paramRequest);
        String uels = "/"+paramRequest.getUser().getLanguage()+"/"+paramRequest.getWebPage().getWebSiteId()+"/resultados?word=*";
        try {
            //Retrieve themes from user properties;
            request.setAttribute("uels", uels);
            request.setAttribute(THEME, themes);
            request.setAttribute(NUM_RECORDS_TOTAL, 0);
            request.setAttribute(FULL_LIST, collectionList);
            request.setAttribute(PARAM_REQUEST, paramRequest);
            request.setAttribute(COUNT_BY_STAT, count(null, COLLECTION_PUBLIC).intValue());
            request.setAttribute(COUNT_BY_USER, count(paramRequest.getUser().getId(), null).intValue());
            request.setAttribute(COUNT_BY_FAVS, umr.countByUser(paramRequest.getUser().getId()).intValue());
            init(request);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    private List<Collection> getThemes(SWBParamRequest paramRequest) {
        Collection c = null;
        List<String> covers = null;
        List<Collection> themes = new ArrayList<>();
        String memorysound = "&resourcetype=Programa radiofónico::Grabación sonora::Concierto::Ópera::Partitura::cantos::Entrevista&holder=Radio Educación::Fonoteca Nacional::Centro Nacional de las Artes::Biblioteca Vasconcelos::Instituto Nacional de Estudios Históricos de las Revoluciones de México::Instituto Nacional de Bellas Artes::Instituto Nacional de Bellas Artes y Literatura::INBA Digital&theme=Memoria Sonora";
        String memoryvisual = "&resourcetype=Fotografía::Programa de televisión::Documental::Cartel::Fotomontaje::Dibujo::Mapa::Video::Cápsula de video::Programa::Pintura::Obra gráfica&holder=Centro de la Imagen::Televisión Metropolitana S.A. de C.V.::Biblioteca de las Artes::Instituto Mexicano de Cinematografía::Dirección General de Culturas Populares::Instituto Nacional de Estudios Históricos de las Revoluciones de México::Instituto Nacional de Bellas Artes::Instituto Nacional de Bellas Artes y Literatura::INBA Digital::Instituto Nacional de Lenguas Indígenas::INALI::Museo Nacional del Virreinato::Instituto Nacional de Antropología e Historia::Mediateca::Museo Nacional de Historia::Museo de Arte Moderno&theme=Memoria Visual";
        String release = "&resourcetype=Conferencia::Foro::Presentación::Mesa de diálogo::Seminario::Cápsula de video::Entrevista::Curso::Coloquio::Artículo de Revista::Capítulo de libro::Conversación::Guía turística::Cuaderno de trabajo::Estudio de público::Expediente técnico de denominación::Manual::Proyecto de restauración::Proyecto museográfico::Reporte de prácticas::Catálogo digital::Interactivo&holder=Centro Nacional de las Artes::Biblioteca Vasconcelos::Instituto Nacional de Estudios Históricos de las Revoluciones de México::Instituto Nacional de Bellas Artes::Instituto Nacional de Bellas Artes y Literatura::INBA Digital::Instituto Nacional de Lenguas Indígenas::INALI::Instituto Nacional de Antropología e Historia::Mediateca&theme=Memoria Visual";
        String art = "&resourcetype=Danza::Busto::Cartel::Escultura::Estampa::Fotografía::Impreso::Manuscrito::Libro::Matriz::Ornamento::Pintura::Relieve::Alcancía::Ánfora::Capítulo de libro::Bastón::Batea::Baúl::Bolsa::Botellón::Candelero::Cartel::Cinturón::Falda::Florero::Fuete::Huarache::Jarra::Jícara::Juguete::Lazo::Mantel::Mecapal::Olla::Paragüero::Plato::Platón::Rebozo::Sarape::Sillón::Sombrero::Sopera::Taza::Tinaja::Trastero::Utensilio::Vasija::Animación::Documento::Grabación::Libro::Partitura::Presentación::Programa::Tesis::Video::Pintura de caballete::Pintura mural::Pintura mural moderna::Pintura mural novohispana::Elemento arquitectónico::Numismática::Pieza arqueológica::Pintura de caballete::Exvoto::Dibujo::Arte plumario::Cristalería::Enconchados::Vidrio::Instrumento musical::Obra gráfica::Gráfica::Obra escultórica&holder=Centro Nacional de las Artes::Museo Nacional de Arte::Instituto Nacional de Bellas Artes::Instituto Nacional de Bellas Artes y Literatura::INBA Digital::Instituto Nacional de Lenguas Indígenas::INALI::Instituto Nacional de Antropología e Historia::Mediateca::Museo Nacional del Virreinato::Museo Nacional de Historia::Museo Nacional de la Estampa::Museo de Arte Moderno::Museo Nacional de San Carlos&theme=Arte";
        String documents ="&resourcetype=Libro::Revista::Manuscrito::Borrador::Capítulo de Libro::Documento::Tesis::Narrativa::Cuadernos de trabajo::Catálogo de exposición::Reseña::Artículo de revista::Editorial::Plano::Códice::Mapa::Número de revista::Número de revista&holder=Biblioteca de las Artes::Dirección General de Bibliotecas::Dirección General de Publicaciones::Museo Nacional de Arte::Instituto Nacional de Bellas Artes::Instituto Nacional de Bellas Artes y Literatura::INBA Digital::Instituto Nacional de Lenguas Indígenas::INALI::Instituto Nacional de Antropología e Historia::Mediateca&theme=Libros, textos y documentos";
        String history = "&resourcetype=Curso::Coloquio::Fotografía::Programa de radio::Conferencia::Entrevista::Exposición::Foro::Premiación::Presentación::Seminario::Archivo histórico::Libro de coro::Acuerdo::Armas::Bandera::Cerámica::Códice::Contenedor::Elemento arquitectónico::Escultura::Heráldica::Herramienta::Indumentaria::Instrumento musical::Libro::Litografía::Mapa::Mapoteca::Maqueta::Máscara::Miniatura::Mobiliario::Numismática::Objeto de uso cotidiano::Objeto etnográfico::Objeto funerario::Objeto litúrgico::Ornamento::Pieza arqueológica::Pintura de caballete::Pintura mural novohispana::Plano::Resto geológico::Resto óseo::Textil::Vehículo::Vidrio::Arma::Elemento Arquitectónico::Equipo de oficina::Indumentaria::Instrumento musical::Joyería::Juguete::Miniatura::Mobiliario::Técnica Mixta::Utensilio::Vasija::Votivo::Costa del Golfo::Maya::Occidente::Aceitera::Árbol::Banca::Barreta::Barril::Baúl::Bieldo::Boletero::Bolsa::Cámara::Campana::Candado::Carrete::Coche::Cofre::Compás::Corneta::Cucharilla::Cucharón::Escatillón::Escudo::Extintor::Farola::Granada::Hielera::Inventario::Lámpara::Lechera::Llave::Locomotora::Machuelo::Manómetro::Microscopio::Modelo::Nivel::Parihuela::Pizarrón::Placa::Porta::Reloj::Resonador::Sello::Señal::Silbato::Taladro::Telegráfono::Tenaza::Teodolito::Velocímetro::Verificadora::Voltímetro&holder=Instituto Nacional de Estudios Históricos de las Revoluciones de México::Instituto Nacional de Antropología e Historia::Mediateca::Museo Nacional de Historia::Museo Nacional de Antropología::Museo Nacional de los Ferrocarriles&theme=Historia";
        String anthropology = "&resourcetype=Ceremonia::Etnografía::Cuestionario::Elicitación::Léxico::Lista de Palabras::Transcripción::Gramática::Pintural mural prehispánica::Armas::Cerámica::Contenedor::Elemento arquitectónico::Escultura::Heráldica::Herramienta::Indumentaria::Instrumento musical::Maqueta::Mobiliario::Numismática::Objeto de uso cotidiano::Objeto de uso personal::Ornamento::Pieza arqueológica::Pintura mural prehispánica::Resto óseo::Resto vegetal::Textil::Fósil::Resto geológico::Costa del Golfo::Maya::Occidente&holder=Instituto Nacional de Lenguas Indígenas::INALI::Instituto Nacional de Antropología e Historia::Mediateca::Museo Nacional de Historia::Museo Nacional de Antropología&theme=Antropología";
	String archeology = "&resourcetype=Ceremonia::Pieza arqueológica::Fósil::Resto geológico::Resto óseo::Herramienta::Arqueología&holder=Instituto Nacional de Lenguas Indígenas::INALI::Instituto Nacional de Antropología e Historia::Mediateca::Museo Nacional de Historia::Museo Nacional de Antropología&theme=Arqueología";
        try {
            c = new Collection(paramRequest.getLocaleString("usrmsg_view_search_memory_sound"), false, "");
            c.setId(memorysound);
            covers = new ArrayList<>();
            covers.add(0, "/work/models/"+paramRequest.getWebPage().getWebSiteId()+"/img/temas/ms.jpg");
            c.setCovers(covers);
            c.setDescription("1");
            themes.add(c);
            c = new Collection(paramRequest.getLocaleString("usrmsg_view_search_memory_visual"), false, "");
            c.setId(memoryvisual);
            covers = new ArrayList<>();
            covers.add(0, "/work/models/"+paramRequest.getWebPage().getWebSiteId()+"/img/temas/visual.jpg");
            c.setCovers(covers);
            c.setDescription("2");
            themes.add(c);
            c = new Collection(paramRequest.getLocaleString("usrmsg_view_search_divulgation"), false, "");
            c.setId(release);
            covers = new ArrayList<>();
            covers.add(0, "/work/models/"+paramRequest.getWebPage().getWebSiteId()+"/img/temas/divulgacion.jpg");
            c.setCovers(covers);
            c.setDescription("3");
            themes.add(c);
            c = new Collection(paramRequest.getLocaleString("usrmsg_view_search_art"), false, "");
            c.setId(art);
            covers = new ArrayList<>();
            covers.add(0, "/work/models/"+paramRequest.getWebPage().getWebSiteId()+"/img/temas/arte2.jpg");
            c.setCovers(covers);
            c.setDescription("4");
            themes.add(c);
            c = new Collection(paramRequest.getLocaleString("usrmsg_view_search_documents"), false, "");
            c.setId(documents);
            covers = new ArrayList<>();
            covers.add(0, "/work/models/"+paramRequest.getWebPage().getWebSiteId()+"/img/temas/libros3.jpg");
            c.setCovers(covers);
            c.setDescription("5");
            themes.add(c);
            c = new Collection(paramRequest.getLocaleString("usrmsg_view_search_history"), false, "");
            c.setId(history);
            covers = new ArrayList<>();
            covers.add(0, "/work/models/"+paramRequest.getWebPage().getWebSiteId()+"/img/temas/historia2.jpg");
            c.setCovers(covers);
            c.setDescription("6");
            themes.add(c);
            c = new Collection(paramRequest.getLocaleString("usrmsg_view_search_anthropology"), false, "");
            c.setId(anthropology);
            covers = new ArrayList<>();
            covers.add(0, "/work/models/"+paramRequest.getWebPage().getWebSiteId()+"/img/temas/antropologia2.jpg");
            c.setCovers(covers);
            c.setDescription("7");
            themes.add(c);
            c = new Collection(paramRequest.getLocaleString("usrmsg_view_search_archeology"), false, "");
            c.setId(archeology);
            covers = new ArrayList<>();
            covers.add(0, "/work/models/"+paramRequest.getWebPage().getWebSiteId()+"/img/temas/arqueologia.jpg");
            c.setCovers(covers);
            c.setDescription("8");
            themes.add(c);
        } catch (SWBResourceException ex) {
            LOG.info(ex.getMessage());
        }
        return themes;
    }
    
    private Document getReference(HttpServletRequest request, SWBParamRequest paramRequest, int page) {
        Integer records = 0;
        Document reference = new Document();
        List<Collection> collectionList = new ArrayList<>();
        try {
            String criteria = null != request.getParameter("criteria") ? request.getParameter("criteria").trim() : "";
            request.setAttribute("criteria", criteria);
            if (!criteria.isEmpty()) records = mgr.countByCriteria(criteria).intValue();
            if (records > 0) {
                collectionList = mgr.findByCriteria(criteria, page, NUM_ROW);
                setAuthors(paramRequest, collectionList);
                setCovers(paramRequest, collectionList, 3);
            }
            reference.append("references", collectionList).append("records", records);
        }catch (Exception se) {
            LOG.info(se.getMessage());
        }
        return reference;
    }
    
    public void collectionById(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        User user = paramRequest.getUser();
        Collection collection = null;
        List<Collection> list = new ArrayList<>();
        List<Entry> favorites = new ArrayList<>();
        String path = "/swbadmin/jsp/rnc/collections/elements.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            if (null != user && user.isSigned() && null != request.getParameter(IDENTIFIER)) {
                collection = mgr.findById(request.getParameter(IDENTIFIER));
            }
            if (null != collection && null != collection.getElements()) {
                list.add(collection);
                setAuthors(paramRequest, list);
                collection.setFavorites(umr.countByCollection(collection.getId()).intValue());
                for (String _id : collection.getElements()) {
                    Entry entry = getEntry(paramRequest, _id);
                    if (null != entry) {
                        favorites.add(entry);
                        SearchCulturalProperty.setThumbnail(entry, paramRequest.getWebPage().getWebSite(), 0);
                    }
                }
            }
            request.setAttribute("myelements", favorites);
            request.setAttribute(COLLECTION, collection);
            request.setAttribute(PARAM_REQUEST, paramRequest);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    @Override
    public void doEdit(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        String path = "/swbadmin/jsp/rnc/collections/collection.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        if (null != request.getParameter(IDENTIFIER) /**&& null != request.getSession().getAttribute("mycollections")**/) {
            //Integer id = Integer.valueOf(request.getParameter(IDENTIFIER));
            //List<Collection> collectionList = (List<Collection>)request.getSession().getAttribute("mycollections")
            //for (Collection c : collectionList) {
                //if (c.getId().equals(request.getParameter(IDENTIFIER))) {
            Collection c = find(request.getParameter(IDENTIFIER));
            if (null != c) request.setAttribute(COLLECTION, c);
                    //break;
                //}
            //}
        }
        try {
            request.setAttribute(PARAM_REQUEST, paramRequest);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    @Override
    public void processAction(HttpServletRequest request, SWBActionResponse response) throws SWBResourceException, IOException {
        User user = response.getUser();
        request.setCharacterEncoding("UTF-8");
        List<Collection> collectionList = new ArrayList<>();
        if (null != request.getSession().getAttribute("mycollections"))
            collectionList = (List<Collection>)request.getSession().getAttribute("mycollections");
        if (SWBResourceURL.Action_REMOVE.equals(response.getAction())) {
            if (null != user && user.isSigned() && null != request.getParameter(IDENTIFIER)) {
                mgr.deleteCollection(request.getParameter(IDENTIFIER));
                collectionList = collectionList(user);
                Gson gson = new Gson();
                response.setRenderParameter(COLLECTION_RENDER, gson.toJson(new Collection("", false, "")));
                response.setMode(MODE_RES_ADD);
                response.setCallMethod(SWBParamRequest.Call_DIRECT);
                request.getSession().setAttribute("mycollections", collectionList);
            }
        }else if (SWBResourceURL.Action_EDIT.equals(response.getAction())) {
            Collection collection = setCollection(request, false);
            if (null != user && user.isSigned() && !collection.isEmpty() && null != request.getParameter(IDENTIFIER)) {
                collectionList = collectionList(user);
                if (!exist(collection.getTitle(), request.getParameter(IDENTIFIER))) {
                    for (Collection c : collectionList) {
                        if (c.getId().equals(request.getParameter(IDENTIFIER))) {
                            c.setUserid(user.getId());
                            c.setTitle(collection.getTitle());
                            c.setDescription(collection.getDescription());
                            c.setUserName(getAuthor(user.getId(), null, response));
                            c.setStatus(Utils.getStatus(request.getParameter("status"), c.getStatus()));
                            Gson gson = new Gson();
                            response.setRenderParameter(COLLECTION_RENDER, gson.toJson(c));
                            update(c);
                            break;
                        }
                    }
                }else {
                    Gson gson = new Gson();
                    response.setRenderParameter(COLLECTION_RENDER, gson.toJson(collection));
                }
                response.setMode(MODE_RES_ADD);
                response.setCallMethod(SWBParamRequest.Call_DIRECT);
                request.getSession().setAttribute("mycollections", collectionList);
            }
        }else if (ACTION_STA.equals(response.getAction())) {
            if (null != request.getParameter(IDENTIFIER) && null != request.getSession().getAttribute("mycollections")) {
                //Integer id = Integer.valueOf(request.getParameter(IDENTIFIER));
                collectionList = (List<Collection>)request.getSession().getAttribute("mycollections");
                for (Collection c : collectionList) {
                    if (c.getId().equals(request.getParameter(IDENTIFIER))) {
                        c.setStatus(!c.getStatus());
                        break;
                    }
                }
            }
        }else if (ACTION_DEL_FAV.equals(response.getAction())) {
            if (null != request.getParameter(IDENTIFIER) && null != request.getParameter(ENTRY) /**&& null != request.getSession().getAttribute("mycollections")**/) {
                //Integer id = Integer.valueOf(request.getParameter(IDENTIFIER));
                //collectionList = (List<Collection>)request.getSession().getAttribute("mycollections");
                Collection c = mgr.findById(request.getParameter(IDENTIFIER));
                if (null != c && !c.getElements().isEmpty() && !request.getParameter(ENTRY).trim().isEmpty() && c.getElements().contains(request.getParameter(ENTRY))) {
                    c.getElements().remove(request.getParameter(ENTRY));
                    mgr.updateCollection(c);
                }
                Gson gson = new Gson();
                response.setRenderParameter(COLLECTION_RENDER, gson.toJson(c));
                response.setMode(MODE_RES_ADD);
                response.setCallMethod(SWBParamRequest.Call_DIRECT);
            }
        }else if (ACTION_ADD.equals(response.getAction())) {
            Collection c = setCollection(request, false);
            if (null != user && user.isSigned() && !c.isEmpty()) {
                if (!exist(c.getTitle(), null)) {
                    c.setUserid(user.getId());
                    c.setUserName(getAuthor(user.getId(), null, response));
                    String id = create(c);
                    if (null != id) c.setId(id);
                    collectionList.add(c);
                    request.getSession().setAttribute("mycollections", collectionList);
                }
                Gson gson = new Gson();
                response.setRenderParameter(COLLECTION_RENDER, gson.toJson(c));
                response.setMode(MODE_RES_ADD);
                response.setCallMethod(SWBParamRequest.Call_DIRECT);
            }
        }else if (ACTION_ADD_CLF.equals(response.getAction()) && null != request.getParameter(IDENTIFIER) && !request.getParameter(IDENTIFIER).trim().isEmpty()) {
            Gson gson = new Gson();
            Collection c = find(request.getParameter(IDENTIFIER));
            if (!umr.exist(user.getId(), request.getParameter(IDENTIFIER))) {
                UserCollection uc = new UserCollection(user.getId(), request.getParameter(IDENTIFIER));
                String _id = umr.insertUserCollection(uc);
                if (null != _id) {
                    c.setId(_id);
                    c.setFavorites(umr.countByCollection(request.getParameter(IDENTIFIER)).intValue());
                }
            }else {
                UserCollection uc = umr.findUserCollection(user.getId(), request.getParameter(IDENTIFIER));
                if (null != uc) {
                    Long result = umr.deleteUserCollection(uc.getId());
                    if (result > 0) c.setFavorites(umr.countByCollection(request.getParameter(IDENTIFIER)).intValue());
                }
            }
            response.setRenderParameter(COLLECTION_RENDER, gson.toJson(c));
            response.setMode(MODE_RES_ADD);
            response.setCallMethod(SWBParamRequest.Call_DIRECT);
        }else
            super.processAction(request, response);
    }
    
    public void redirectJsonResponse(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
        String json = request.getParameter(COLLECTION_RENDER);
        response.setContentType("application/json");
	response.setHeader("Cache-Control", "no-cache");
	PrintWriter pw = response.getWriter();
	pw.write(json);
	pw.flush();
	pw.close();
	response.flushBuffer();
    }
    
    public void doAdd(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws java.io.IOException {
        String path = "/swbadmin/jsp/rnc/collections/collection.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            request.setAttribute(PARAM_REQUEST, paramRequest);
            request.setAttribute(COLLECTION, new Collection("", false, ""));
            rd.include(request, response);
        }catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    public static List<String> getCovers(SWBParamRequest paramRequest, List<String> elements, String baseUri, int size) {
        Entry entry = null;
        List<String> covers = new ArrayList<>();
        if (null == elements || elements.isEmpty()) return covers;
        Iterator it = elements.iterator();
        while (it.hasNext()) {
            String uri = baseUri + "/api/v1/search?identifier=" + it.next();
            GetBICRequest req = new GetBICRequest(uri);
            try {
                entry = req.makeRequest();
            }catch (Exception e) {
                entry = null;
                LOG.info(e.getMessage());
            }
            if (null != entry) {
                if (null != entry.getResourcethumbnail() && !entry.getResourcethumbnail().trim().isEmpty())
                    covers.add(entry.getResourcethumbnail());
                else {
                    SearchCulturalProperty.setThumbnail(entry, paramRequest.getWebPage().getWebSite(), size);
                    covers.add(entry.getResourcethumbnail());
                }
            }
            if (covers.size() >= size) break;
        }
        return covers;
    }
    
    private List<String> getCovers(SWBParamRequest paramRequest, List<String> elements, String baseUri, int size, Collection c) {
        Entry entry = null;
        List<String> covers = new ArrayList<>();
        if (elements.isEmpty()) return covers;
        Iterator it = elements.iterator();
        while (it.hasNext()) {
            String itnext = (String)it.next();
            String uri = baseUri + "/api/v1/search?identifier=" + itnext;
            GetBICRequest req = new GetBICRequest(uri);
            try {
                entry = req.makeRequest();
            }catch (Exception e) {
                entry = null;
                removeDeletedBicFromCollection(c,itnext);
                LOG.info(e.getMessage());
            }
            if (null != entry) {
                if (null != entry.getResourcethumbnail() && !entry.getResourcethumbnail().trim().isEmpty())
                    covers.add(entry.getResourcethumbnail());
                else {
                    SearchCulturalProperty.setThumbnail(entry, paramRequest.getWebPage().getWebSite(), size);
                    covers.add(entry.getResourcethumbnail());
                }
            }
            if (covers.size() >= size) break;
        }
        return covers;
    }
    
    private void removeDeletedBicFromCollection(Collection c, String bicId){
        c.getElements().remove(bicId);
        mgr.updateCollection(c);
    }
    
     private List<Collection> collectionList(User user, Integer from, Integer leap) {
         List<Collection> collection = new ArrayList<>();
        if (null != user && user.isSigned())
            collection = mgr.collectionsByUserLimit(user.getId(), from, leap);
        else {
            collection = mgr.collectionsByStatusLimit(COLLECTION_PUBLIC, from, leap);
        }
        return collection;
     }
    
    private List<Collection> collectionList(User user) {
        List<Collection> collection = new ArrayList<>();
        if (null != user && user.isSigned())
            collection = mgr.collections(user.getId());
        else {
            collection = mgr.collectionsByStatus(COLLECTION_PUBLIC);
        }
        return collection;
    }
    
    protected Entry getEntry(SWBParamRequest paramRequest,String _id) {
        String baseUri = paramRequest.getWebPage().getWebSite().getModelProperty("search_endPoint");
        if (null == baseUri || baseUri.isEmpty()) {
            baseUri = SWBPlatform.getEnv("rnc/endpointURL", getResourceBase().getAttribute("url", "http://localhost:8080")).trim();
        }
        String uri = baseUri + "/api/v1/search?identifier=";
        uri += _id;
        GetBICRequest req = new GetBICRequest(uri);
        return req.makeRequest();
    }
    
    protected void init(HttpServletRequest request) throws SWBResourceException, java.io.IOException {
        int pagenum = 0;
        String p = request.getParameter("p");
        if (null != p) pagenum = Integer.parseInt(p);
        if (pagenum<=0) pagenum = 1;
        request.setAttribute(NUM_PAGE_LIST, pagenum);
        request.setAttribute("PAGE_NUM_ROW", PAGE_NUM_ROW);
        page(pagenum, request);
    }
    
    protected void page(int pagenum, HttpServletRequest request) {
        ArrayList<?> rows = (ArrayList<?>)request.getAttribute(FULL_LIST);
        Integer total = (Integer)request.getAttribute(NUM_RECORDS_TOTAL);
        if (null==total) total = 0;
        if (null==rows) rows = new ArrayList();
        try {
            Integer totalPages = total/NUM_ROW;
            if (total%NUM_ROW != 0)
                totalPages ++;
            request.setAttribute(TOTAL_PAGES, totalPages);
            Integer currentLeap = (pagenum-1)/PAGE_JUMP_SIZE;
            request.setAttribute(NUM_PAGE_JUMP, currentLeap);
            request.setAttribute("PAGE_JUMP_SIZE", PAGE_JUMP_SIZE);
            request.setAttribute(PAGE_LIST, rows);
            request.setAttribute(NUM_RECORDS_VISIBLE, rows.size());
        }catch(Exception e) {
            LOG.info(e.getMessage());
        }
    }
    
    /**private void page(int pagenum, HttpServletRequest request) {
        List<?> rows = (List<?>)request.getAttribute(FULL_LIST);
        Integer total = (Integer)request.getAttribute("NUM_RECORDS_TOTAL");
        if (null==rows) rows = new ArrayList();
        try {
            Integer totalPages = total/NUM_ROW;
            if (total%NUM_ROW != 0)
                totalPages ++;
            request.setAttribute(TOTAL_PAGES, totalPages);
            Integer currentLeap = (pagenum-1)/PAGE_JUMP_SIZE;
            request.setAttribute(NUM_PAGE_JUMP, currentLeap);
            request.setAttribute(STR_JUMP_SIZE, PAGE_JUMP_SIZE);
            ArrayList rowsPage = getRows(pagenum, rows);
            request.setAttribute(PAGE_LIST, rowsPage);
            request.setAttribute(NUM_RECORDS_TOTAL, total);
            request.setAttribute(NUM_RECORDS_VISIBLE, rowsPage.size());
        }catch(Exception e) {
            LOG.info(e.getMessage());
        }
    }
    
    private ArrayList getRows(int page, List<?> rows) {
        int pageCount = 1;
        if (rows==null || rows.isEmpty()) return new ArrayList();
        Map<Integer, ArrayList<?>> pagesRows = new HashMap<>();
        ArrayList pageRows = new ArrayList();
        pagesRows.put(pageCount, pageRows);
        for (int i=0; i<rows.size(); i++) {
            pageRows.add(rows.get(i));
            if (i+1 < rows.size() && ((i+1) % NUM_ROW) == 0) {
                pageCount++;
                pageRows = new ArrayList();
                pagesRows.put(pageCount, pageRows);
            }
        }
        ArrayList rowsPage = pagesRows.get(page);
        if (rowsPage==null) rowsPage = new ArrayList();
        return rowsPage;
    }**/
    
    private String create(Collection c) {
        String id = mgr.insertCollection(c);
        return id;
    }
    
    private long update(Collection c) {
        return mgr.updateCollection(c);
    }
    
    private Long count(String userid, Boolean status) {
        Long count = 0L;
        try {
            if (null != userid) count = mgr.countByUser(userid);
            else count = mgr.countAllByStatus(status);
        }catch(Exception e) {
            LOG.info(e.getMessage());
        }
        return count; 
    }
    
    private Collection find(String _id) {
        return mgr.findById(_id);
    }
    
    private boolean exist(String title, String _id) {
        return mgr.exist(title, _id);
    }
    
    private Collection setCollection(HttpServletRequest request, boolean prevst) {
        Collection collection = new Collection(request.getParameter("title").trim(), Utils.getStatus(request.getParameter("status"), prevst), request.getParameter("description").trim());
        return collection;
    }
     
    private void setAuthors(SWBParamRequest paramRequest, List<Collection> list) {
        if (null == list || list.isEmpty()) return;
        for (Collection c : list) {
            if (null != c && null != c.getUserid()) {
                c.setUserName(getAuthor(c.getUserid(), paramRequest, null));
            }
        }
    }
     
    private String getAuthor(String userid, SWBParamRequest paramRequest, SWBActionResponse response) {
        User user = null;
        if (null != paramRequest)
            user = paramRequest.getWebPage().getWebSite().getUserRepository().getUser(userid);
        else if (null != response)
            user = response.getWebPage().getWebSite().getUserRepository().getUser(userid);
        return null != user && null != user.getFullName() ? user.getFullName() : "";
    }
     
    protected void setCovers(SWBParamRequest paramRequest, List<Collection> list,  int size) {
        String baseUri = paramRequest.getWebPage().getWebSite().getModelProperty("search_endPoint");
        if (null == baseUri || baseUri.isEmpty())
            baseUri = SWBPlatform.getEnv("rnc/endpointURL", getResourceBase().getAttribute("url", "http://localhost:8080")).trim();
        for (Collection c : list) {
            c.setCovers(getCovers(paramRequest, c.getElements(), baseUri, size, c));
        }
    }
}
