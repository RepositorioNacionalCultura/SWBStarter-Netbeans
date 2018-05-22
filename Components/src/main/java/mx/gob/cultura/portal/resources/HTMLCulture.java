/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.resources;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.gob.cultura.portal.utils.EditorTemplate;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.semanticwb.Logger;
import org.semanticwb.SWBUtils;
import org.semanticwb.SWBException;
import org.semanticwb.SWBPlatform;
import org.semanticwb.SWBPortal;
import org.semanticwb.model.Resource;
import org.semanticwb.model.SWBContext;
import org.semanticwb.model.User;
import org.semanticwb.model.VersionInfo;
import org.semanticwb.model.WebPage;
import org.semanticwb.model.WebSite;
import org.semanticwb.portal.util.ContentUtils;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;
import static org.semanticwb.portal.resources.sem.HTMLContent.cleanHTML;
/**
 *
 * @author sergio.tellez
 */
public class HTMLCulture extends GenericResource {
    
    public static final String SAVE = "SAVE";
    public static final String ACTION = "act";
    public static final String TYPE_ALL = "all";
    public static final String TYPE_ZIP = "zip";
    public static final String TYPE_FLASH = "flash";
    public static final String TYPE_DOCS = "document";
    public static final String TYPE_IMAGES = "image";
    public static final String ATTR_FILES = "filesMap";
    public static final String MODE_VIEW_EDIT = "vEdit";
    public static final String ACT_UPLOADFILE = "uploadFile";
    public static final String MOD_UPLOADFILE = "mUploadFile";
    public static String[] swfType = { "swf" };
    public static String[] imgTypes = { "tiff", "tif", "gif", "jpeg", "jpg", "jif", "jfif", "jp2", "jpx", "j2k", "j2c", "fpx", "pcd", "png", "svg", "bmp" };
    public static String[] docTypes = { "pdf", "doc", "dot", "docx", "docm", "dotx", "dotm", "xls", "xlt", "xlm", "xlsx", "xlsm", "xltx", "xltm", "ppt", "pot", "pps", "pptx", "pptm", "potx", "potm", "ppsx", "ppsm", "pub", "xml", "rtf", "txt", "csv", "odt", "ods", "odp", "odg", "pdf", "eps" };
    public static String[] zipTypes = { "a", "ar", "tar", "bz2", "gz", "7z", "rar", "zip", "zipx" };
    
    private int snpages = 15;
    private int position = 1;
    private String stxtant = "Anterior";
    private String stxtsig = "Siguiente";
    private String stfont = "font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\" color=\"#000000\"";
  
    /** Ruta relativa para carpeta de archivos asociados a cada version. */
    protected  static final String FOLDER = "images";
    
    /** Objeto utilizado para generacion de mensajes en el log. */
    private static Logger log = SWBUtils.getLogger(HTMLCulture.class);
    
    /**
     * Determina el metodo a ejecutar en base al modo que se envia en el objeto
     * HttpServletRequest recibido.
     * 
     * @param request the request
     * @param response the response
     * @param paramRequest the param request
     * @throws SWBResourceException the sWB resource exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        if (null != request.getParameter(ACTION) && MODE_VIEW_EDIT.equals(request.getParameter(ACTION))) {
            doViewFix(request, response, paramRequest);
        }else if (paramRequest.getMode().equals(SAVE)) {
            saveContent(request, response, paramRequest);
        } else if (paramRequest.getMode().equals("uploadNewVersion")) {
            //uploadNewVersion(request, response, paramRequest);
            response.sendError(405);
        } else if (paramRequest.getMode().equals("selectFileInterface")) {
            selectFileInterface(request, response, paramRequest);
        } else if (MOD_UPLOADFILE.equals(paramRequest.getMode())) {
            doUploadFiles(request, response, paramRequest);
        } else if (MODE_VIEW_EDIT.equals(paramRequest.getMode())) {
            doViewEdit(request, response, paramRequest);
        }else {
            super.processRequest(request, response, paramRequest);
        }
    }
    
    public void doViewFix(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        String content = "";
        //Rename this method to doViewEdit
        try {
            String numversion = getResourceBase().getAttribute("numversion","");
            if (!numversion.isEmpty()) content = getContent(request, paramRequest);
            else {
                EditorTemplate template = getEditorTemplate(getResourceBase().getAttribute("template"), getResourceBase().getAttribute("idGroupTemplate"), paramRequest);
                if (null != template) {
                    content = getContentTempl(request, paramRequest, template.getUrl(), template.getFileName());
                    content = parseTemplate(content, template.getUrl());
                }
            }
            request.setAttribute("numversion", 1);
            request.setAttribute("fileContent", content);
            request.setAttribute("paramRequest", paramRequest);
            response.setContentType("text/html;charset=ISO-8859-1");
            RequestDispatcher rd = request.getRequestDispatcher("/swbadmin/jsp/rnc/exhibitions/htmlExContent.jsp");
            rd.include(request, response);
        } catch (ServletException se) {
            log.error(se);
        }
    }
    
    private String parseTemplate(String content, String path) {
        return content.replaceAll("images/", "/work/" + path+"images/");
    }
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest)throws SWBResourceException, IOException {
        int vnum = 1;
        //String numversion = null;
        Resource resource = getResourceBase();
        WebPage page = paramRequest.getWebPage();
        //VersionInfo vi = getActualVersion();
        //if (null == vi) {
            //GenericObject go = SWBPlatform.getSemanticMgr().getOntology().getGenericObject(resource.getResourceData().getURI());
            //SWBResource swres = (SWBResource) go;
            //vi = swres.getResourceBase().getWebSite().createVersionInfo();
            //vi.setVersionFile("index.html");
            //vi.setVersionNumber(vnum);
            //vi.setVersionComment("Versión Inicial");

            //Versionable vswres = (Versionable) go;
            //vswres.setActualVersion(vi);
            //vswres.setLastVersion(vi);

            /**String rutaFS_target_path = SWBPortal.getWorkPath() + resource.getWorkPath() + "/" + vnum + "/";
            File f = new File(rutaFS_target_path);
            if (!f.exists()) {
                f.mkdirs();
            }
            File ftmpl = new File(SWBPortal.getWorkPath() + resource.getWorkPath() + "/" + vnum + "/index.html");
            Writer output = null;
            try {
                output = new BufferedWriter(new FileWriter(ftmpl));
                output.write(" ");
                try {
                    output.close();
                } catch (IOException le) {
                    log.error(le);
                }
                //numversion = request.getParameter("numversion");
            } catch (IOException le) {
                log.error(le);
            } finally {
                try {
                    if (null != output) output.close();
                } catch (IOException le) {
                    log.error(le);
                }
            }
        //}
        if ((numversion != null) && (numversion.length() > 0)) {
            vi = findVersion(Integer.parseInt(numversion));
        }**/
        //int versionNumber = vi.getVersionNumber();
        String fileName = "index.html";
        String resourceWorkPath = SWBPortal.getWorkPath() + resource.getWorkPath() + "/" + vnum + "/" + fileName;
        String fileContent = SWBUtils.IO.getFileFromPath(resourceWorkPath);
        if (fileContent != null) {
            boolean paginated = true;
            /**try {
                paginated = isContentPaginated();
            } catch (Exception e) {
                log.error("HTMLContent - Getting \"paginated\" property", e);
            }**/
            if (fileContent.indexOf("content=\"Microsoft Word") > 0) {
                fileContent = SWBUtils.TEXT.replaceAll(fileContent, "<workpath/>", SWBPortal.getWebWorkPath() + resource.getWorkPath() + "/" + vnum + "/");
                boolean deleteStyles = true;
                /**try {
                    deleteStyles = isDeleteStyles();
                } catch (Exception e) {
                    log.error("HTMLContent - Getting \"deleteStyles\" property.", e);
                }**/
                ContentUtils contentUtils = new ContentUtils();
                fileContent = contentUtils.predefinedStyles(fileContent, resource, true);
                if (paginated)
                    fileContent = contentUtils.paginationMsWord(request, fileContent, page, request.getParameter("page"), resource, this.snpages, this.stxtant, this.stxtsig, this.stfont, this.position);
                fileContent = cleanHTML(fileContent, deleteStyles);
            } else {
                fileContent = SWBUtils.TEXT.replaceAll(fileContent, "<workpath/>", SWBPortal.getWebWorkPath() + resource.getWorkPath() + "/" + vnum + "/");
                /**if ((getFormerLinkText() != null) && (!"".equalsIgnoreCase(getFormerLinkText()))) {
                    this.stxtant = getFormerLinkText();
                } else if (paramRequest.getLocaleString("txtant") != null) {
                    this.stxtant = paramRequest.getLocaleString("txtant");
                }
                if ((getNextLinkText() != null) && (!"".equalsIgnoreCase(getNextLinkText()))) {
                    this.stxtsig = getNextLinkText();
                } else if (paramRequest.getLocaleString("txtsig") != null) {
                    this.stxtsig = paramRequest.getLocaleString("txtsig");
                }**/
                if (paginated)
                    fileContent = new ContentUtils().paginationHtmlContent(request, fileContent, page, request.getParameter("page"), resource, this.snpages, this.stxtant, this.stxtsig, this.stfont, this.position, request.getParameter("uri"));
            }
            fileContent = SWBUtils.TEXT.replaceAll(fileContent, "<webpath/>", SWBPortal.getContextPath());
        }
        response.getWriter().println(fileContent);
    }
    
    public void doViewEdit(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest)throws SWBResourceException, IOException {
        doEdit(request, response, paramRequest);
    }
    
    private String getContent(HttpServletRequest request, SWBParamRequest paramRequest) throws SWBResourceException{
        String content = "";
        String fileName = "index.html";
        Resource resource = paramRequest.getResourceBase();
        int versionNumber = null != request.getParameter("numversion") ? Integer.parseInt(request.getParameter("numversion")) : 1;
        String action = paramRequest.getAction();
        StringBuilder pathToRead = new StringBuilder(64);
        //comentar siguiente linea
        StringBuilder pathToWrite = new StringBuilder(64);
        String tmpPath = request.getParameter("tmpPath");
        pathToRead.append(resource.getWorkPath()).append("/");
        String webWorkpath = SWBPortal.getWebWorkPath();
        pathToWrite.append(webWorkpath).append(resource.getWorkPath()).append("/");
        if (action.equalsIgnoreCase(SWBParamRequest.Action_EDIT) && versionNumber == 0 && tmpPath == null)
            action = SWBParamRequest.Action_ADD;
        pathToRead.append(versionNumber).append("/");
        pathToRead.append(fileName);
        pathToWrite.append(String.valueOf(versionNumber)).append("/");
        request.setAttribute("directory", pathToWrite.toString());
        if (action.equals(SWBParamRequest.Action_EDIT)) {
            try {
                if (null == tmpPath || "".equals(tmpPath)) {//Cuando se carga el archivo normalmente
                    content = SWBUtils.IO.readInputStream(SWBPortal.getFileFromWorkPath(pathToRead.toString()));
                    //Se sustituye el tag insertado por el metodo saveContent en lugar de la ruta logica del archivo
                    content = SWBUtils.TEXT.replaceAll(content, "<workpath/>", SWBPortal.getWebWorkPath() + resource.getWorkPath() + "/" + (versionNumber) + "/");
                }else
                    content = SWBUtils.IO.readInputStream(SWBPortal.getFileFromWorkPath(tmpPath + "index.html"));
            } catch (SWBException | IOException e) {
                content = paramRequest.getLocaleString("msgFileReadError");
                log.error("Error to try read: " + resource.getId(), e);
            }
        }
        return content;
    } 
    
    @Override
    public void doEdit(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        int versionNumber = Integer.parseInt(request.getParameter("numversion"));
        String content = getContent(request, paramRequest);
        /**String fileName = "index.html";
        Resource resource = paramRequest.getResourceBase();
        String action = paramRequest.getAction();
        StringBuilder pathToRead = new StringBuilder(64);
        //comentar siguiente linea
        StringBuilder pathToWrite = new StringBuilder(64);
        String tmpPath = request.getParameter("tmpPath");
        pathToRead.append(resource.getWorkPath()).append("/");
        String webWorkpath = SWBPortal.getWebWorkPath();
        pathToWrite.append(webWorkpath).append(resource.getWorkPath()).append("/");
        if (action.equalsIgnoreCase(SWBParamRequest.Action_EDIT) && versionNumber == 0 && tmpPath == null)
            action = SWBParamRequest.Action_ADD;
        pathToRead.append(versionNumber).append("/");
        pathToRead.append(fileName);
        pathToWrite.append(String.valueOf(versionNumber)).append("/");
        request.setAttribute("directory", pathToWrite.toString());
        if (action.equals(SWBParamRequest.Action_EDIT)) {
            try {
                if (null == tmpPath || "".equals(tmpPath)) {//Cuando se carga el archivo normalmente
                    content = SWBUtils.IO.readInputStream(SWBPortal.getFileFromWorkPath(pathToRead.toString()));
                    //Se sustituye el tag insertado por el metodo saveContent en lugar de la ruta logica del archivo
                    content = SWBUtils.TEXT.replaceAll(content, "<workpath/>", SWBPortal.getWebWorkPath() + resource.getWorkPath() + "/" + (versionNumber) + "/");
                }else
                    content = SWBUtils.IO.readInputStream(SWBPortal.getFileFromWorkPath(tmpPath + "index.html"));
            } catch (SWBException e) {
                content = paramRequest.getLocaleString("msgFileReadError");
                log.error("Error to try read: " + resource.getId(), e);
            }
        }**/
        try {
            response.setContentType("text/html;charset=ISO-8859-1");
            request.setAttribute("fileContent", content);
            request.setAttribute("paramRequest", paramRequest);
            request.setAttribute("numversion", versionNumber);
            RequestDispatcher rd = request.getRequestDispatcher("/swbadmin/jsp/rnc/exhibitions/htmlExContent.jsp");
            rd.include(request, response);
        } catch (ServletException e) {
            log.debug(e);
        }
    }
    
    /**
     * Procesa las peticiones de carga de archivos en el componente.
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse
     * @param paramRequest the SWBParamRequest object
     * @throws SWBResourceException
     * @throws IOException 
     */
    public void doUploadFiles(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        String msg = "";
        String pathToFile = "";
        boolean success = false;
        String action = paramRequest.getAction();
        String version = request.getParameter("numversion");
        String type = request.getParameter("type");
        String CKEditor = request.getParameter("CKEditor");
        String CKEditorFuncNum = request.getParameter("CKEditorFuncNum");
        
        if (null == version || version.isEmpty()) version = "1";
        
        String actualcontext = (!"".equals(SWBPlatform.getContextPath()) ? SWBPlatform.getContextPath() : "");
        String workPath = actualcontext+SWBPortal.getWorkPath()+getResourceBase().getWorkPath()+"/"+version+"/images/";
        String webWorkPath = actualcontext+SWBPortal.getWebWorkPath()+getResourceBase().getWorkPath()+"/"+version+"/images/";
        
        ArrayList<String> extensions = HTMLCultureUtils.getFileTypes(type);
        
        File wp = new File(workPath);
        if (!wp.exists()) wp.mkdirs();
        System.out.println("isEnabledForFileUpload: " + HTMLCultureUtils.isEnabledForFileUpload(paramRequest));
        if (HTMLCultureUtils.isEnabledForFileUpload(paramRequest) && ACT_UPLOADFILE.equals(action) && ServletFileUpload.isMultipartContent(request)) {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload sfu = new ServletFileUpload(factory);
            try {
                List<FileItem> items = sfu.parseRequest(request);
                Iterator<FileItem> iter = items.iterator();
                System.out.println("items: " + items.size());
                while (iter.hasNext()) {
                    FileItem item = iter.next();
                    if (!item.isFormField()) {
                        String itemName = item.getName();
                        System.out.println("itemName: " + itemName);
                        if (HTMLCultureUtils.isValidFileType(itemName, extensions)) {
                            String fileName = HTMLCultureUtils.sanitizeFileName(itemName);
                            item.write(new File(workPath+fileName));
                            pathToFile = webWorkPath+fileName;
                            break;
                        }
                    }
                }
                success = true;
            } catch (FileUploadException fue) {
                msg = paramRequest.getLocaleString("msgFileUploadError");
                log.error(msg, fue);
            } catch (Exception ex) {
                msg = paramRequest.getLocaleString("msgFileStorageError");;
                log.error(msg, ex);
            }
        }
        PrintWriter out = response.getWriter();
        if (null == CKEditor) { //Para el caso de llamada directa, mediante el fileBrowser
            out.write("<!DOCTYPE HTML><html><head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\"></head><body><textarea name=\"uploadStatus\">"+(success?"SUCCESS":"FAIL")+"</textarea></body></html>");
        } else if (!CKEditor.isEmpty()) {
            //Escribir el resultado para CKEditor
            out.write("<!DOCTYPE HTML><html><head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\"></head><body><script type='text/javascript'>window.parent.CKEDITOR.tools.callFunction("+CKEditorFuncNum+", '"+pathToFile+"', '"+msg+"');</script>");
        }
        out.flush();
        out.close();
    }
    
    /**
     * Almacena en un archivo con extensi&oacute;n .html el contenido mostrado
     * en el editor, sustituyendo el contenido anterior de la versi&oacute;n
     * indicada por el parametro numversion.
     * 
     * @param request the request
     * @param response the response
     * @param paramRequest the param request
     * @throws SWBResourceException the sWB resource exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void saveContent(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest)throws SWBResourceException, IOException {
        String filename = null;
        boolean textSaved = false;
        Resource resource = paramRequest.getResourceBase();
        String contentPath = resource.getWorkPath() + "/";
        String textToSave = request.getParameter("EditorDefault");
        boolean deleteTmp = (request.getParameter("operation") != null && !"".equals(request.getParameter("operation")));
        int versionNumber = Integer.parseInt(request.getParameter("numversion"));
        contentPath+=versionNumber;
        int versionToDelete = versionNumber;
        String directoryToRemove = SWBPortal.getWorkPath() + resource.getWorkPath() + "/" + (versionToDelete > 1 ? versionToDelete : 1) + "/tmp";
        String directoryToCreate = SWBPortal.getWorkPath() + resource.getWorkPath() + "/" + (versionNumber) + "/" + FOLDER;
        String workingDirectory = SWBPortal.getWebWorkPath() + resource.getWorkPath();
        String message = null;
        VersionInfo vio = null;
        //vio = findVersion(versionNumber);
        if (null != vio) filename = vio.getVersionFile();
        else filename = "index.html";
        if (null != textToSave) {
            try {
                File filePath = new File(SWBPortal.getWorkPath() + contentPath);
                if (!filePath.exists()) filePath.mkdirs();
                filePath = new File(directoryToCreate);
                if (!filePath.exists()) filePath.mkdirs();
                File file = new File(SWBPortal.getWorkPath() + contentPath + "/" + filename);
                filename = file.getName();
                FileWriter writer = new FileWriter(file);
                if (deleteTmp) {
                    //modifica las rutas de los archivos asociados si se acaba de cargar un archivo HTML antes de guardar
                    String directorioSinTmp = SWBPortal.getWebWorkPath() + resource.getWorkPath() + "/" + (versionToDelete > 1 ? versionToDelete : 1) + "/";
                    String directorioConTmp = directorioSinTmp + "tmp/";
                    textToSave = SWBUtils.TEXT.replaceAll(textToSave, directorioConTmp, directorioSinTmp);
                }
                //Remplaza del contenido las rutas de los archivos relacionados por <workpath/>
                textToSave = SWBUtils.TEXT.replaceAll(textToSave, workingDirectory+"/"+versionNumber+"/", "<workpath/>");
                int i = textToSave.indexOf("<workpath/>");
                //Parsea el contenido en busca de imagenes que no tengan ruta absoluta desde /work...
                while (i > -1) {
                    try {
                        String delimiter = "" + textToSave.charAt(i - 1);
                        if (!delimiter.equalsIgnoreCase("\"") && !delimiter.equalsIgnoreCase("'"))
                            delimiter = " ";
                        String fileName = textToSave.substring(i + 11, textToSave.indexOf(delimiter, i + 11));
                        String s = SWBPortal.getWorkPath() + resource.getWorkPath() + "/" + versionNumber + "/" + fileName;
                        int ls = fileName.indexOf("/");
                        if (ls > -1)
                            fileName = fileName.substring(ls + 1);
                        //Solo copia archivos de versiones anteriores
                        if (!s.contains(resource.getWorkPath() + "/" + versionNumber)) {
                            SWBUtils.IO.copy(s, directoryToCreate + "/" + fileName, false, "", "");
                        }
                    } catch (IOException e) {
                        log.error("Error to try save content", e);
                    }
                    i = textToSave.indexOf("<workpath/>", i + 11);
                }
                writer.write(textToSave);
                writer.flush();
                writer.close();
                textSaved = true;
                if (deleteTmp) {
                    File imagesDirectory = new File(directoryToRemove + "/" + FOLDER);
                    //eliminar el directorio tmp de la version anterior
                    if (imagesDirectory.exists() && SWBUtils.IO.createDirectory(directoryToCreate)) {
                        //Copia los archivos del directorio tmp al de la nueva version
                        for (String strFile : imagesDirectory.list()) {
                            SWBUtils.IO.copy(imagesDirectory.getPath() + "/" + strFile, directoryToCreate + "/" + strFile, false, "", "");
                        }
                    }
                    SWBUtils.IO.removeDirectory(directoryToRemove);
                }
                if (textSaved) {
                    WebPage wp = paramRequest.getWebPage();
                    StringBuilder poster = new StringBuilder();
                    String resourcePath = resource.getWorkPath() + "/" + (versionNumber) + "/" + FOLDER;
                    File imagesDirectory = new File(directoryToCreate);
                    if (imagesDirectory.exists() && SWBUtils.IO.createDirectory(directoryToCreate)) {
                        for (String strFile : imagesDirectory.list()) {
                            if (strFile.endsWith(".jpg") || strFile.endsWith(".png") || strFile.endsWith(".gif"))
                                poster.append("#").append("/work").append(resourcePath).append("/").append(strFile);  
                        }
                        if (poster.length() > 1)
                            wp.setProperty("posters", poster.substring(1));
                    }
                }
            } catch (IOException e) {
                textSaved = false;
                log.error("Error to try save content", e);
            }
        }
        //Remove cache
        SWBPortal.getResourceMgr().getResourceCacheMgr().removeResource(resource);
        //SWBPortal.getServiceMgr().updateTraceable(resource.getSemanticObject(), paramRequest.getUser()); This resource is not traceable
        if (textSaved) {
            message = paramRequest.getLocaleString("msgContentSaved");
            try {
                getResourceBase().setAttribute("numversion", String.valueOf(versionNumber));
                getResourceBase().updateAttributesToDB();
            } catch (SWBException swb) {
                log.error("Error to try upddate attribute", swb);
            }
        }else
            message = paramRequest.getLocaleString("msgContentSaveFailed");
        request.setAttribute("message", message);
        request.setAttribute("numversion", versionNumber);
        //this.doViewEdit(request, response, paramRequest);
        response.sendRedirect(paramRequest.getWebPage().getUrl());
    }

    /**
     * Muestra un explorador de archivos al insertar elementos mediante el editor HTML.
     * 
     * @param request the request
     * @param response the response
     * @param paramRequest the param request
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void selectFileInterface(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws IOException {
        String currentcontext = (!"".equals(SWBPlatform.getContextPath()) ? SWBPlatform.getContextPath() : "");
        String version = (request.getParameter("numversion") != null && !"".equals(request.getParameter("numversion"))) ? request.getParameter("numversion") : "1";
        String jsp = currentcontext + "/swbadmin/jsp/HTMLContentUploadDialog.jsp";//maybe this use is not recommended
        String fileType = null != request.getParameter("type") ? request.getParameter("type") : "";
        RequestDispatcher rd = request.getRequestDispatcher(jsp);
        ArrayList types = HTMLCultureUtils.getFileTypes(fileType);
        try {
            response.setContentType("text/html;charset=ISO-8859-1");//Forced because of encoding problems
            request.setAttribute("paramRequest", paramRequest);
            request.setAttribute(ATTR_FILES, getFileList(request, version, types));
            rd.include(request, response);
        } catch (ServletException ex) {
            log.error(ex);
        }
    }
    
    private Map<String, Long> getFileList(HttpServletRequest hsr, String version, ArrayList<String> allowedTypes) {
        Resource base = getResourceBase();
        Map<String, Long> files = new TreeMap<>();
        String resPath = SWBPlatform.getContextPath()+SWBPortal.getWorkPath()+base.getWorkPath()+"/"+version+"/images/";
        if (HTMLCultureUtils.isEnabledForFileBrowsing(hsr)) {
            final File resourcePath = new File(resPath);
            if (resourcePath.exists() && resourcePath.isDirectory()) {
                for (final File fileEntry : resourcePath.listFiles(new ExtFilter(allowedTypes))) {
                    files.put(fileEntry.getName(), fileEntry.length());
                }
            }
        }
        return files;
    }
    
    private String getContentTempl(HttpServletRequest request, SWBParamRequest paramRequest, String pathToRead, String fileName) throws SWBResourceException{
        String content = "";
        Resource resource = paramRequest.getResourceBase();
        int versionNumber = null != request.getParameter("numversion") ? Integer.parseInt(request.getParameter("numversion")) : 1;
        //String action = paramRequest.getAction();
        StringBuilder path2Read = new StringBuilder(64);
        StringBuilder pathToWrite = new StringBuilder(64);
        //String tmpPath = request.getParameter("tmpPath");
        String webWorkpath = SWBPortal.getWebWorkPath();
        pathToWrite.append(webWorkpath).append(resource.getWorkPath()).append("/");
        /**if (action.equalsIgnoreCase(SWBParamRequest.Action_EDIT) && versionNumber == 0 && tmpPath == null)
            action = SWBParamRequest.Action_ADD;**/
        //pathToRead.append(versionNumber).append("/");
        path2Read.append("/").append(pathToRead);
        path2Read.append(fileName);
        pathToWrite.append(String.valueOf(versionNumber)).append("/");
        request.setAttribute("directory", pathToWrite.toString());
        try {
            content = SWBUtils.IO.readInputStream(SWBPortal.getFileFromWorkPath(path2Read.toString()));
            //Se sustituye el tag insertado por el metodo saveContent en lugar de la ruta logica del archivo
            content = SWBUtils.TEXT.replaceAll(content, "<workpath/>", SWBPortal.getWebWorkPath() + "/" + pathToRead);
        } catch (IOException | SWBException e) {
            content = paramRequest.getLocaleString("msgFileReadError");
            log.error("Error to try read: " + resource.getId(), e);
        }
        return content;
    }
    
    private EditorTemplate getEditorTemplate(String id, String idGroupTemplate, SWBParamRequest paramRequest) {
        if (null == id || id.isEmpty()) return null;
        List<EditorTemplate> editorTemplateList = ExhibitionResource.editorTemplateList(paramRequest.getWebPage().getWebSite(), idGroupTemplate);
        for (EditorTemplate tpl : editorTemplateList) {
            if (tpl.getId().equals(id)) return tpl;
        }
        return null;
    }
    
    /**
     * Filtro para el listado de archivos en la vista de navegación.
     * Por defecto omite subdirectorios pues no es necesario listarlos en el contexto del componente.
     */
    private class ExtFilter implements FileFilter {
        private ArrayList<String> types = new ArrayList<>();
        private boolean showHidden = false;
        
        /**
         * Constructor por defecto. 
         * Crea una instancia de {@code ExtFileFilter}.
         * No debe ser usado pues no listarÃ¡ ningÃºn archivo.
         */
        public ExtFilter () {}
        
        /**
         * Constructor.
         * Crea una instancia de {@code ExtFileFilter} con la lista de extensiones vÃ¡lidas en el listado.
         * @param allowedTypes ArrayList con las extensiones a considerar en el listado.
         */
        public ExtFilter(ArrayList<String> allowedTypes) {
            types = allowedTypes;
        }
        
        /**
         * Constructor.
         * Crea una instancia de {@code ExtFileFilter} con la lista de extensiones vÃ¡lidas en el listado.
         * @param allowedTypes ArrayList con las extensiones a considerar en el listado.
         * @param showHiddenFiles Indica si se deben mostrar los archivos marcados como ocultos.
         */
        public ExtFilter(ArrayList<String> allowedTypes, boolean showHiddenFiles) {
            showHidden = showHiddenFiles;
            types = allowedTypes;
        }
        
        @Override
        public boolean accept(File pathname) {
            if (pathname.isDirectory()) return false;
            if (!showHidden && pathname.isHidden()) return false;
            
            String path = pathname.getAbsolutePath().toLowerCase();
            for (int i = 0, n = types.size(); i < n; i++) {
              String extension = types.get(i);
              if ((path.endsWith(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.')) {
                return true;
              }
            }
            return false;
        }
        
        /**
         * Establece las extensiones vÃ¡lidas para el listado.
         * @param allowedTypes Array de cadenas con las extensiones vÃ¡lidas.
         */
        public void setAllowedTypes(String []allowedTypes) {
            if (null != allowedTypes && allowedTypes.length > 0) {
                types.addAll(Arrays.asList(allowedTypes));
            }
        }
        
        /**
         * Obtiene la lista de extensiones vÃ¡lidas para el listado.
         * @return Arreglo con las extensiones vÃ¡lidas.
         */
        public String[] getAllowedTypes () {
            return types.toArray(new String[types.size()]);
        }
    }
    
    public static class HTMLCultureUtils {
        /**
         * Valida si el usuario en sesión tiene permisos suficientes para subir archivos mediante el explorador del widget.
         * @param paramRequest SWBParamRequest.
         * @return true si el usuario tiene permisos para subir archivos, false en otro caso.
         */
        public static boolean isEnabledForFileUpload(SWBParamRequest paramRequest) {
            User user = paramRequest.getUser();
            return (null != user && user.isSigned());
        }

        /**
         * Valida si el usuario en sesión tiene permisos suficientes para navegar por la estructura de archivos mediante el explorador del widget.
         * @param hsr The request.
         * @return true si el usuario tiene permisos para navegar por la estructura de archivos, false en otro caso.
         */
        public static boolean isEnabledForFileBrowsing(HttpServletRequest hsr) {
            WebSite site = SWBContext.getAdminWebSite();
            User user = SWBPortal.getUserMgr().getUser(hsr, site);
            SWBContext.setSessionUser(user);
            return user.haveAccess(site.getHomePage());
        }
        
        /**
         * Elimina caracteres especiales del nombre del archivo para evitar conflicto con rutas directas.
         * @param fileName Nombre original del archivo.
         * @return Nombre de archivo sanitizado, sin caracteres especiales.
         */
        public static String sanitizeFileName(String fileName) {
            String ret = fileName;
            
            if (ret.lastIndexOf('.') > -1) {
                String tfname = ret.substring(0, ret.lastIndexOf('.'));
                String tfext = ret.substring(ret.lastIndexOf('.'), ret.length());
                ret = SWBUtils.TEXT.replaceSpecialCharactersForFile(tfname, ' ', true) + tfext;
            } else {
                ret = SWBUtils.TEXT.replaceSpecialCharactersForFile(ret, ' ', true);
            }
            return ret;
        }
        
        /**
         * Verifica si un archivo cuenta con la extensión determinada en una lista de exensiones vÃ¡lidas.
         * @param fileName Nombre de archivo a comprobar.
         * @param types Lista de extensiones permitidas.
         * @return true si el archivo contiene alguna de las extensiones permitidas, false en otro caso.
         */
        public static boolean isValidFileType(String fileName, ArrayList<String> types) {
            String path = fileName.toLowerCase();
            for (int i = 0, n = types.size(); i < n; i++) {
                String extension = types.get(i);
                if ((path.endsWith(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.')) {
                    return true;
                }
            }
            return false;
        }
        
        /**
         * Obtiene la lista de extensiones dependiendo del filtro seleccionado.
         * @param typeFilter Alguno entre image|document|flash|zip|all 
         * @return Lista de extensiones filtradas por tipo.
         */
        public static ArrayList<String> getFileTypes(String typeFilter) {
            ArrayList<String> types = new ArrayList<>();
            if (TYPE_ALL.equals(typeFilter) || TYPE_FLASH.equals(typeFilter))
                types.addAll(Arrays.asList(swfType));
            if (TYPE_ALL.equals(typeFilter) || TYPE_DOCS.equals(typeFilter))
                types.addAll(Arrays.asList(docTypes));
            if (TYPE_ALL.equals(typeFilter) || TYPE_IMAGES.equals(typeFilter))
                types.addAll(Arrays.asList(imgTypes));
            if (TYPE_ALL.equals(typeFilter) || TYPE_ZIP.equals(typeFilter))
                types.addAll(Arrays.asList(zipTypes));
            return types;
        }
    }
}