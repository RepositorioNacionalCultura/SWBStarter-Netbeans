package mx.gob.cultura.portal.resources;

import java.util.List;
import java.util.HashMap;
import java.util.Iterator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.net.URLEncoder;
import java.security.Principal;
import javax.security.auth.Subject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.semanticwb.Logger;
import org.semanticwb.SWBPlatform;
import org.semanticwb.SWBPortal;
import org.semanticwb.SWBUtils;
import org.semanticwb.model.User;
import org.semanticwb.model.UserRepository;
import org.semanticwb.model.WebPage;
import org.semanticwb.platform.SemanticObject;
import org.semanticwb.portal.SWBSessionObject;
import org.semanticwb.portal.api.GenericAdmResource;
import org.semanticwb.portal.api.SWBActionResponse;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;
import org.semanticwb.portal.api.SWBResourceURL;
import org.semanticwb.portal.api.SWBResourceURLImp;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Realiza la visualizacion y cambios en la informacion del perfil de usuarios
 *
 * @author jose.jimenez / rene.jara
 */
public class UserRegistry extends GenericAdmResource {

    private static final Logger LOG = SWBUtils.getLogger(UserRegistry.class);

    public static final String ACTION_REGISTER = "creating";
    public static final String ACTION_ASYN_REGISTER = "creatingAsyn";
    public static final String ACTION_UPLOAD_PHOTO = "uploadPhoto";

    public static final String REGISTER_MODE = "confirmRegistry";
    public static final String REGISTER_ASYN_MODE = "confirmRegistryAsyn";

    public static final String REDIRECT_HOME_MODE = "homeRedirect";

    public static final String ACTION_BE_ANNOTATOR = "beannotator";

    public static final String ACTION_CONFIRMING = "confirming";

    private String confirmationActionUrl = null;

    private static final String DATACRED_URI
            = "http://www.semanticwebbuilder.org/swb4/ontology#plainCredential";
    public static final String ORGANITATION_NAME_URI
            = "http://www.semanticwebbuilder.org/swb4/ontology#organitationName";
    public static final String POSITION_TITLE_URI
            = "http://www.semanticwebbuilder.org/swb4/ontology#positionTitle";
    public static final String TELEPHONE_NUMBER_URI
            = "http://www.semanticwebbuilder.org/swb4/ontology#telephoneNumber";
    public static final String AREA_INTEREST_URI
            = "http://www.semanticwebbuilder.org/swb4/ontology#areaInterest";
    public static final String PHOTO_FILE_URI
            = "http://www.semanticwebbuilder.org/swb4/ontology#photoFile";

    private UserRepository userRepository;

    @Override
    public void init() throws SWBResourceException {
        super.init();
        userRepository = getResourceBase().getWebSite().getUserRepository();
    }

    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws IOException {
        String url = "/swbadmin/jsp/rnc/" + this.getClass().getSimpleName() + "/view.jsp";
        //String url = "/work/models/" + paramRequest.getWebPage().getWebSite().getId() + "/jsp/rnc/" + this.getClass().getSimpleName()+"/view.jsp";
        User user = paramRequest.getUser();
        boolean isAnnotator = false;
        if (user != null && user.isSigned()) {
            isAnnotator = user.hasRole(userRepository.getRole(this.getResourceBase().getAttribute("AnnRol", "")));
        }
        RequestDispatcher rd = request.getRequestDispatcher(url);
        try {
            request.setAttribute("paramsRequest", paramRequest);
            request.setAttribute("isAnnotator", isAnnotator);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.error(se.getMessage());
        }
    }

    @Override
    public void processAction(HttpServletRequest request, SWBActionResponse response) throws SWBResourceException, IOException {

        String alert = null;
        String nextMode = null;
        String action = response.getAction();

        if (ACTION_REGISTER.equals(action) || ACTION_ASYN_REGISTER.equals(action)) {
            User created = this.createProfile(request);
            if (null == created) {
                response.setRenderParameter("condition", (String) request.getAttribute("condition"));
                System.out.println("USUARIO NO CREADO");
            } else {
                if (null == this.confirmationActionUrl) {
                    String serverUrl = request.getScheme() + "://" + request.getServerName() + ((request.getServerPort() != 80) ? (":" + request.getServerPort()) : "");
                    SWBResourceURLImp urlAcc = new SWBResourceURLImp(request, this.getResourceBase(), response.getWebPage(), SWBResourceURL.UrlType_ACTION);
                    this.confirmationActionUrl = serverUrl + urlAcc.setAction("confirming").toString();
                }
                this.sendConfirmationEmail(created);
                System.out.println("Se creo, el usuario  >>>");
                System.out.println(request.getParameter(ACTION_BE_ANNOTATOR));
                if (request.getParameter(ACTION_BE_ANNOTATOR) != null) {
                    this.sendBeAnnotatorEmail(created);
                }
            }
            if (ACTION_ASYN_REGISTER.equals(action)) {
                nextMode = REGISTER_ASYN_MODE;
            } else {
                nextMode = REGISTER_MODE;
            }
        } else if (response.Action_EDIT.equals(action)) {
            User updated = this.updateProfile(request, response.getUser());
            if (null == updated) {
                response.setRenderParameter("condition",
                        (String) request.getAttribute("condition"));
                System.out.println("USUARIO NO actualizado");
            } else {
                if (request.getParameter(ACTION_BE_ANNOTATOR) != null) {
                    this.sendBeAnnotatorEmail(updated);
                }
            }
            nextMode = response.Mode_VIEW;
        } else if (ACTION_CONFIRMING.equals(action)) {
            User user = this.activateUser(request);
            StringBuilder credential = new StringBuilder(16);

            if (null != user) {
                if (SWBPlatform.getSecValues().isMultiple()) {
                    String login = user.getLogin();
                    Iterator<SWBSessionObject> llist = SWBPortal.getUserMgr().listSessionObjects();
                    while (llist.hasNext()) {
                        SWBSessionObject so = llist.next();
                        Iterator<Principal> lpri = so.getSubjectByUserRep(userRepository.getId()).getPrincipals().iterator();
                        if (lpri.hasNext() && login.equalsIgnoreCase(((User) lpri.next()).getLogin())) {
                            alert = "msg_alreadyLoggedin";
                        }
                    }
                }

                try {
                    OntModel ont = SWBPlatform.getSemanticMgr().getSchema().getRDFOntModel();
                    credential.append(user.getSemanticObject().getRDFResource()
                            .getProperty(ont.createDatatypeProperty(
                                    UserRegistry.DATACRED_URI)).getString());

                    user.checkCredential(credential.toString().toCharArray());
                    Subject subject = SWBPortal.getUserMgr().getSubject(request,
                            response.getWebPage().getWebSiteId());
                    subject.getPrincipals().clear();
                    subject.getPrincipals().add(user);
                    if (null == user.getLanguage()) {
                        user.setLanguage("es");   //forzar lenguaje si no se dio de alta.
                    }
                    user.getSemanticObject().getRDFResource().
                            removeAll(ont.createDatatypeProperty(UserRegistry.DATACRED_URI));
                } catch (Exception ex) {
                    LOG.error("Actualizando usuario", ex);
                }

            }

            if (null != alert) {
                response.setRenderParameter("condition", alert);
            }
            nextMode = REDIRECT_HOME_MODE;
        } else if (ACTION_UPLOAD_PHOTO.equals(action)) {
            final Percentage percentageSaved = new Percentage();
            try {
                User user = response.getUser();
                boolean isMultipart = ServletFileUpload.isMultipartContent(request);
                HashMap<String, String> params = new HashMap<>(8);
                // Create a factory for disk-based file items
                File tmpwrk = new File(SWBPortal.getWorkPath() + "/tmp");
                if (!tmpwrk.exists()) {
                    tmpwrk.mkdirs();
                }
                FileItemFactory factory = new DiskFileItemFactory(1 * 1024 * 1024, tmpwrk);
                // Create a new file upload handler
                ServletFileUpload upload = new ServletFileUpload(factory);
                //Create a progress listener
                ProgressListener progressListener = new ProgressListener() {

                    private long kBytes = -1;

                    public void update(long pBytesRead, long pContentLength, int pItems) {
                        long mBytes = pBytesRead / 10000;
                        if (kBytes == mBytes) {
                            return;
                        }
                        kBytes = mBytes;
                        int percent = (int) (pBytesRead * 100 / pContentLength);
                        percentageSaved.setValue(percent);
                    }
                };
                upload.setProgressListener(progressListener);
                // Parse the request
                List items = upload.parseRequest(request); /* FileItem */
                FileItem currentFile = null;
                // Process the uploaded items
                Iterator iter = items.iterator();
                while (iter.hasNext()) {
                    FileItem item = (FileItem) iter.next();

                    if (item.isFormField()) {
                        String name = item.getFieldName();
                        String value = item.getString();
                        params.put(name, value);
                    } else {
                        currentFile = item;
//                        String fieldName = item.getFieldName();
//                        String fileName = item.getName();
//                        String contentType = item.getContentType();
//                        boolean isInMemory = item.isInMemory();
//                        long sizeInBytes = item.getSize();
//                        File uploadedFile = new File();
//                        item.write(uploadedFile);
                    }
                }
                if (null != currentFile) {
                    request.getSession(true).setAttribute(currentFile.getFieldName(), percentageSaved);
                }

                String path = SWBPortal.getWorkPath() + user.getWorkPath();
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String name = user.getId() + currentFile.getName().substring(currentFile.getName().lastIndexOf("."));
                String photoName = path + "/" + name;
                currentFile.write(new File(photoName));
                path = user.getWorkPath();
                //SWBPlatform.getWebWorkPath() +
                user.setPhoto(path + "/" + name);
                percentageSaved.setValue(100);
//                File f = new File(photoName);
                /*                BufferedImage bi = ImageIO.read(f);
                int calcHeight = (150 * bi.getHeight() / bi.getWidth());
                ImageIO.write(createResizedCopy(bi, 150, calcHeight), name.substring(name.lastIndexOf(".")+1), f);*/
//              ImageResizer.resizeCrop(f, 150, f, name.substring(name.lastIndexOf(".") + 1).toLowerCase());
            } catch (Exception ex) {
                UserRegistry.LOG.error(ex);
            }
            
        }
        if (null != nextMode) {
            response.setMode(nextMode);
        }
    }

    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) throws SWBResourceException, IOException {

        if (REDIRECT_HOME_MODE.equals(paramRequest.getMode())) {
            redirect2Home(request, response, paramRequest);
        } else if (REGISTER_MODE.equals(paramRequest.getMode())) {
            confirmRegistry(request, response, paramRequest);
        } else if (REGISTER_ASYN_MODE.equals(paramRequest.getMode())) {
            confirmAsyncRegistry(request, response, paramRequest);
        } else {
            super.processRequest(request, response, paramRequest);
        }
    }

    private void redirect2Home(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) throws SWBResourceException, IOException {

        try {
            WebPage home = WebPage.ClassMgr.getWebPage("home", paramRequest.getWebPage().getWebSite());
            String url = home.getRealUrl(paramRequest.getUser().getLanguage());
            response.setContentType("Text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><head><meta http-equiv=\"Refresh\" CONTENT=\"0; URL="
                    + url + "\" /><script>alert(\"" + paramRequest.getLocaleString("msg_welcome")
                    + "\");window.location='" + url
                    + "';</script></head></html>");
            out.flush();
        } catch (IOException e) {
            LOG.error("Redirecting user", e);
        }
    }

    private void confirmRegistry(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramsRequest) throws SWBResourceException, IOException {
        try {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            if (request.getParameter("condition") == null || request.getParameter("condition").isEmpty()) {
                out.print(paramsRequest.getLocaleString("msgSuccess"));
            } else {
                out.print("<h3 class=\"oswM center\">" + paramsRequest.getLocaleString(request.getParameter("condition")) + "</h3>");
            }
            out.flush();
        } catch (IOException ioe) {
            LOG.error("Confirming registry", ioe);
        }
    }

    private void confirmAsyncRegistry(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramsRequest) throws SWBResourceException, IOException {
        try {
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            if (request.getParameter("condition") == null || request.getParameter("condition").isEmpty()) {
                out.print("msgSent");
            } else {
                out.print(paramsRequest.getLocaleString(request.getParameter("condition")));
            }
            out.flush();
        } catch (IOException ioe) {
            LOG.error("Confirming async registry", ioe);
        }
    }

    private User createProfile(HttpServletRequest request) {
        User newUser = null;
        String firstName = request.getParameter("usrFirstName");
        String lastName = request.getParameter("usrLastName");
        String email = request.getParameter("email");
        String password = request.getParameter("pass2");
        String passwordConfirm = request.getParameter("passConf");
        String termsPrivacy = request.getParameter("termsPrivacy");
        String condition = null;
        //anotador
        String organ = request.getParameter("usrOrg");
        String title = request.getParameter("usrTitle");
        String inter = request.getParameter("usrInter");
        String tel = request.getParameter("usrTel");
        //boolean profCreated = true;         
        if (null != password && null != email && !password.isEmpty()
                && password.equals(passwordConfirm) && !email.isEmpty()
                && null != termsPrivacy && termsPrivacy.equals("true")) {

            User user = userRepository.getUserByLogin(email);
            if (null == user) {
                //try {                     
                //String encryptdPwd;
                //encryptdPwd = SWBUtils.CryptoWrapper.passwordDigest(password);
                newUser = userRepository.createUser();
                newUser.setLogin(email);
                newUser.setPassword(password);  //encryptdPwd
                newUser.setLanguage("es");
                if (firstName != null) {
                    newUser.setFirstName(firstName);
                }
                if (lastName != null) {
                    newUser.setLastName(lastName);
                }
                newUser.setEmail(email);
                System.out.println("Contraseña para nuevo usuario: " + password);
                SemanticObject obj = newUser.getSemanticObject();
                OntModel ont = SWBPlatform.getSemanticMgr().getSchema().getRDFOntModel();
                obj.getRDFResource().addLiteral(ont.createDatatypeProperty(
                        UserRegistry.DATACRED_URI), password);
                if (organ != null) {
                    obj.getRDFResource().addLiteral(ont.createDatatypeProperty(
                            UserRegistry.ORGANITATION_NAME_URI), organ);
                }
                if (title != null) {
                    obj.getRDFResource().addLiteral(ont.createDatatypeProperty(
                            UserRegistry.POSITION_TITLE_URI), title);
                }
                if (inter != null) {
                    obj.getRDFResource().addLiteral(ont.createDatatypeProperty(
                            UserRegistry.AREA_INTEREST_URI), inter);
                }
                if (tel != null) {
                    obj.getRDFResource().addLiteral(ont.createDatatypeProperty(
                            UserRegistry.TELEPHONE_NUMBER_URI), tel);
                }
            } else {
                condition = "msgUserExists";
            }
        } else if (null != password && null != passwordConfirm && !password.equals(passwordConfirm)) {
            condition = "msg_pwdMismatch";
        }
        if (null != newUser || null != condition) {
            request.setAttribute("condition", condition);
        }
        return newUser;
    }

    private User updateProfile(HttpServletRequest request, User user) {
        String firstName = request.getParameter("usrFirstName");
        String lastName = request.getParameter("usrLastName");
        String email = request.getParameter("email");
        String password = request.getParameter("pass2");
        String passwordConfirm = request.getParameter("passConf");
        //String termsPrivacy = request.getParameter("termsPrivacy");
        String condition = null;
        //anotador
        String organ = request.getParameter("usrOrg");
        String title = request.getParameter("usrTitle");
        String inter = request.getParameter("usrInter");
        String tel = request.getParameter("usrTel");
        //boolean profCreated = true;

        if (null != email && !email.isEmpty()
                && null != firstName && !firstName.isEmpty()
                && null != lastName && !lastName.isEmpty()) {

            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            SemanticObject obj = user.getSemanticObject();
            OntModel ont = SWBPlatform.getSemanticMgr().getSchema().getRDFOntModel();

            if (null != password && !password.isEmpty() && password.equals(passwordConfirm)) {
                user.setPassword(password);
                System.out.println("Contraseña para nuevo usuario: " + password);

                Property prprt = ont.createDatatypeProperty(UserRegistry.DATACRED_URI);
                user.getSemanticObject().getRDFResource().removeAll(prprt);
                obj.getRDFResource().addLiteral(prprt, password);
            }
            if (organ != null) {
                Property prprt = ont.createDatatypeProperty(UserRegistry.ORGANITATION_NAME_URI);
                user.getSemanticObject().getRDFResource().removeAll(prprt);
                obj.getRDFResource().addLiteral(prprt, organ);
            }
            if (title != null) {
                Property prprt = ont.createDatatypeProperty(UserRegistry.POSITION_TITLE_URI);
                user.getSemanticObject().getRDFResource().removeAll(prprt);
                obj.getRDFResource().addLiteral(prprt, title);
            }
            if (inter != null) {
                Property prprt = ont.createDatatypeProperty(UserRegistry.AREA_INTEREST_URI);
                user.getSemanticObject().getRDFResource().removeAll(prprt);
                obj.getRDFResource().addLiteral(prprt, inter);
            }
            if (tel != null) {
                Property prprt = ont.createDatatypeProperty(UserRegistry.TELEPHONE_NUMBER_URI);
                user.getSemanticObject().getRDFResource().removeAll(prprt);
                obj.getRDFResource().addLiteral(prprt, tel);
            }
        } else {
            condition = "msg_missingData";
        }
        if (null != user || null != condition) {
            request.setAttribute("condition", condition);
        }
        return user;
    }

    private void sendConfirmationEmail(User user) {

        StringBuilder body = new StringBuilder(256);
        StringBuilder linkUrl = new StringBuilder(128);
        boolean noProblem = false;

        body.append(this.getResourceBase().getAttribute("emailMsgRegAck", "{link}"));

        replaceString(body, "{username}", user.getFullName());

        linkUrl.append(this.confirmationActionUrl);
        linkUrl.append("?account=");
        try {
            linkUrl.append(URLEncoder.encode(user.getEmail(), "UTF-8"));
            linkUrl.append("&thru=");
            linkUrl.append(
                    URLEncoder.encode(SWBUtils.CryptoWrapper.passwordDigest(user.getEmail()
                            + Long.toString(user.getCreated().getTime())), "UTF-8"));
            noProblem = true;
        } catch (Exception e) {
            LOG.error("Error al encriptar parametro en correo");
        }

        System.out.println("Confirmation registry mail:\n" + linkUrl);
        replaceString(body, "{link}", linkUrl.toString());

        try {
            if (noProblem) {
                SWBUtils.EMAIL.sendBGEmail(user.getEmail(),
                        this.getResourceBase().getAttribute("emailSubRegAck", "Confirmacion de registro de usuario"),
                        body.toString());
            }
        } catch (SocketException se) {
            LOG.error("Enviando el correo de confirmacion del registro");
        }
    }

    private void sendBeAnnotatorEmail(User user) {
        StringBuilder body = new StringBuilder(256);
        OntModel ont = SWBPlatform.getSemanticMgr().getSchema().getRDFOntModel();

        body.append(this.getResourceBase().getAttribute("emailMsgAnnNtf", "{link}"));

        replaceString(body, "{username}", user.getFullName());
        replaceString(body, "{titlejob}", user.getSemanticObject().getRDFResource().getProperty(ont.createDatatypeProperty(UserRegistry.POSITION_TITLE_URI)).getString());
        replaceString(body, "{institution}", user.getSemanticObject().getRDFResource().getProperty(ont.createDatatypeProperty(UserRegistry.ORGANITATION_NAME_URI)).getString());
        replaceString(body, "{email}", user.getEmail());
        replaceString(body, "{telefono}", user.getSemanticObject().getRDFResource().getProperty(ont.createDatatypeProperty(UserRegistry.TELEPHONE_NUMBER_URI)).getString());
        replaceString(body, "{areainteres}", user.getSemanticObject().getRDFResource().getProperty(ont.createDatatypeProperty(UserRegistry.AREA_INTEREST_URI)).getString());

        try {
            SWBUtils.EMAIL.sendBGEmail(this.getResourceBase().getAttribute("email", "anotador@cultura.gob.mx"),
                    this.getResourceBase().getAttribute("emailSubAnnNtf", "Notificación"),
                    body.toString());
        } catch (SocketException se) {
            LOG.error("Usuario desea ser anotador");
        }
    }

    /**
     * Activa el registro del usuario, siempre que la cuenta de correo
     * proporcionada corresponda con el dato de validacion contenido en el hash
     * recibido
     *
     * @param request la peticion del usuario para validar su cuenta de correo y
     * activar el registro del usuario
     * @param userRepo el repositorio de usuarios en que se encuentra el
     * registro del usuario
     * @return true, siempre que se verifique que la cuenta de correo indicada
     * esta asociada a un usuario registrado, y que la peticion contiene datos
     * validos.
     */
    private User activateUser(HttpServletRequest request) {

        String eMail = request.getParameter("account");
        String hash = request.getParameter("thru");
        User fulanito = null;

        if (null != eMail && null != hash && !eMail.isEmpty() && !hash.isEmpty()) {
            fulanito = userRepository.getUserByLogin(eMail);
            if (null != fulanito) {
                String legitHash = null;
                try {
                    legitHash = SWBUtils.CryptoWrapper.passwordDigest(fulanito.getEmail()
                            + Long.toString(fulanito.getCreated().getTime()));
                } catch (Exception e) {
                    LOG.error("Error al encriptar parametro para activar usuario");
                }
                if (hash.equals(legitHash)) {
                    fulanito.setActive(true);
                } else {
                    fulanito = null;
                }
            }
        }
        return fulanito;
    }

    public void replaceString(StringBuilder sb, String toReplace, String replacement) {
        int index;
        while ((index = sb.lastIndexOf(toReplace)) != -1) {
            sb.replace(index, index + toReplace.length(), replacement);
        }
    }
}

class Percentage {
    
    
    //valor representado por este objeto
    int value = 0;
    
    
    public Percentage() {}
    
    public void setValue(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
}
