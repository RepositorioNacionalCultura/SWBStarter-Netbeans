package mx.gob.cultura.portal.resources;

import java.util.GregorianCalendar;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.gob.cultura.portal.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticwb.Logger;
import org.semanticwb.SWBPortal;
import org.semanticwb.SWBUtils;
import org.semanticwb.model.WebSite;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;
import sun.misc.BASE64Encoder;

/**
 * Facilita la autenticacion de usuarios ante la plataforma de Twitter
 * @author jose.jimenez
 */
public class OAuthTwitter extends GenericResource {
    
    //datos de prueba
    //  swbsocial/twitterAppKey=V5Xp0RYFuf3N0WsHkqSOIQ
    //  swbsocial/twitterSecretKey=4DZ9UrE4X5VavUjXzBcGFTvEsHVsCGOgIuLVSZMA8
    
    private static final Logger LOG = SWBUtils.getLogger(OAuthTwitter.class);
    
    private final static String ALPHABET = "abcdefghijklmnopqrstuvwxyz1234567890";
    
    private final String POST_METHOD = "POST";
    
    private final static String GET_METHOD = "GET";
    
    private final static String SIGN_METHOD = "HMAC-SHA1";
    
    private final static String OAUTH_VERSION = "1.0";
    
    
    public static JSONObject verifyUserCredentials(String consumerKey, String consumerSecret,
            String token, String tokenSecret) {
        
        String reqValidateCredResponse = null;
        JSONObject ret = null;
        if (null != consumerKey && !consumerKey.isEmpty() &&
                null != consumerSecret && !consumerSecret.isEmpty()) {
            
            String signature;
            //Se recaban los datos para la firma de la peticion, junto con los parametros de la misma
            String timestamp = Long.toString(GregorianCalendar.getInstance(
                    TimeZone.getTimeZone("GMT")).getTimeInMillis() / 1000); //en segundos, por ser NTC no UTC
            String nonce = getNonce();
            String reqCredentialsUrl = "https://api.twitter.com/1.1/account/verify_credentials.json";
            String trueString = "true";
            String falseString = "false";
            TreeMap<String, String> params2Sign = new TreeMap();
            
//            params2Sign.put("include_email", trueString);
//            params2Sign.put("include_entities", falseString);
            params2Sign.put("oauth_consumer_key", consumerKey);
            params2Sign.put("oauth_nonce", nonce);
            params2Sign.put("oauth_signature_method", OAuthTwitter.SIGN_METHOD);
            params2Sign.put("oauth_timestamp", timestamp);
            params2Sign.put("oauth_token", token);
            params2Sign.put("oauth_version", OAuthTwitter.OAUTH_VERSION);
//            params2Sign.put("skip_status", trueString);
            
            //Se obtiene la firma de la peticion
            signature = generateSignature(params2Sign, OAuthTwitter.GET_METHOD,
                    reqCredentialsUrl, consumerSecret, tokenSecret);
            params2Sign.put("oauth_signature", percentageEncode(signature));
            
            //se agregan parametros del query string
            HashMap<String, String> urlParams = new HashMap(8);
//            urlParams.put("include_email", trueString);
//            urlParams.put("include_entities", falseString);
//            urlParams.put("skip_status", trueString);
            //y se eliminan de los que forman el encabezado
//            params2Sign.remove("include_email");
//            params2Sign.remove("include_entities");
//            params2Sign.remove("skip_status");
            
            //Se preparan los datos para la peticion
            String oauthHeader = getAuthorizationReqHeader(params2Sign);
            HashMap<String, String> reqHeaders = new HashMap(8);
            reqHeaders.put("Authorization", oauthHeader);
//            System.out.println("URL: " + reqCredentialsUrl);
//            System.out.println("Authorization header: " + oauthHeader);

            //Se ejecuta la peticion
            try {
                reqValidateCredResponse = getRequest(urlParams, reqHeaders,
                        reqCredentialsUrl);
            } catch (IOException ioe) {}
            
//            System.out.println("validating credentials:\n" + reqValidateCredResponse);
        }
        if (reqValidateCredResponse != null && !reqValidateCredResponse.isEmpty()) {
            try {
                ret = new JSONObject(reqValidateCredResponse);
            } catch (JSONException je) {}
        }
        return ret;
    }
    
    /**
     * Autentica los datos proporcionados por el usuario para obtener un access token
     * @param request
     * @param response
     * @param paramRequest
     * @throws SWBResourceException
     * @throws IOException 
     */
    public void doAuthToken(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        
        String oauthToken = request.getParameter("oauth_token");
        String oauthVerifier = request.getParameter("oauth_verifier");
        String consumerKey = paramRequest.getWebPage().getWebSite()
                .getModelProperty("twitter_consumerKey");
        String consumerSecret = paramRequest.getWebPage().getWebSite()
                .getModelProperty("twitter_consumerSecret");
        String token = null; //TODO: guardarlas
        String tokenSecret = null;
        String actionUrl = null;
        String userId = null;
        String screenName = null;
        //System.out.println("   ***** Autenticando para obtener Access Token *****");
        
        if (null != oauthToken && null != oauthVerifier && !oauthToken.isEmpty() &&
                !oauthVerifier.isEmpty()) {
            if (oauthToken.equals(request.getSession().getAttribute("t_token"))) {
                String signature;
                //Se recaban los datos para la firma de la peticion, junto con los parametros de la misma
                String timestamp = Long.toString(GregorianCalendar.getInstance(
                        TimeZone.getTimeZone("GMT")).getTimeInMillis() / 1000);
                String nonce = this.getNonce();
                String requestTokenUrl = "https://api.twitter.com/oauth/access_token";
                
                TreeMap<String, String> params2Sign = new TreeMap();
                params2Sign.put("oauth_consumer_key", consumerKey);
                params2Sign.put("oauth_nonce", nonce);
                params2Sign.put("oauth_signature_method", this.SIGN_METHOD);
                params2Sign.put("oauth_timestamp", timestamp);
                params2Sign.put("oauth_token", oauthToken);
                params2Sign.put("oauth_version", this.OAUTH_VERSION);
                params2Sign.put("oauth_verifier", oauthVerifier);
                //Se obtiene la firma de la peticion
                signature = generateSignature(params2Sign, this.POST_METHOD,
                        requestTokenUrl, consumerSecret, null);
                params2Sign.put("oauth_signature", signature);
                
                //parametros de la peticion (cuerpo)
                HashMap<String, String> bodyParams = new HashMap(8);
                bodyParams.put("oauth_verifier", params2Sign.get("oauth_verifier"));
                params2Sign.remove("oauth_verifier");
                
                //Se preparan los datos para la peticion
                String oauthHeader = this.getAuthorizationReqHeader(params2Sign);
                HashMap<String, String> reqHeaders = new HashMap(8);
                reqHeaders.put("user-agent", request.getHeader("User-Agent"));
                reqHeaders.put("Authorization", oauthHeader);
                //System.out.println("URL: " + requestTokenUrl);
                //System.out.println("Authorization header: " + oauthHeader);
                
                //Se ejecuta la peticion
                String reqTokenSecretResponse = this.postRequest(bodyParams, reqHeaders,
                        requestTokenUrl, this.POST_METHOD);
                
                //se parsea la respuesta para obtener los datos necesarios para la segunda peticion
                if (null != reqTokenSecretResponse && !reqTokenSecretResponse.isEmpty()) {
                    if (reqTokenSecretResponse.contains("&")) {
                        String[] paramsResp = reqTokenSecretResponse.split("&");
                        for (String part : paramsResp) {
                            if (part.contains("=")) {
                                String[] pair = part.split("=");
                                switch (pair[0]) {
                                    case "oauth_token":
                                            token = pair[1];
                                            break;
                                    case "oauth_token_secret":
                                            tokenSecret = pair[1];
                                            break;
                                    case "user_id":
                                            userId = pair[1];
                                            break;
                                    case "screen_name":
                                            screenName = pair[1];
                                            break;
                                }
                            }
                        }
                    }
                }
                //System.out.println("Respuesta token secret: \n" + reqTokenSecretResponse);
            }
        }
        if (null != token && null != tokenSecret) {
            JSONObject credentials = verifyUserCredentials(consumerKey, consumerSecret, token, tokenSecret);
            if (null != credentials && !credentials.has("errors")) {
//                System.out.println("UserCredentials: " + credentials.toString(4));
                actionUrl = Utils.getResourceURL(
                        paramRequest.getWebPage().getWebSite(),
                        SessionInitializer.class,
                        paramRequest.getActionUrl().setCallMethod(
                                SWBParamRequest.Call_DIRECT)
                                .setAction("openSession")
                                .setParameter("source", "twitter")
                                .setParameter("id", credentials.getString("id_str"))
                                .setParameter("name", credentials.getString("name"))
                                .setParameter("email", credentials.getString("email")).toString());
            } else if (null == credentials || credentials.has("errors")) {
                actionUrl = Utils.getResourceURL(
                        paramRequest.getWebPage().getWebSite(),
                        SessionInitializer.class,
                        paramRequest.getActionUrl().setCallMethod(
                                SWBParamRequest.Call_DIRECT)
                                .setAction("openSession")
                                .setParameter("source", "twitter")
                                .setParameter("id", userId)
                                .setParameter("name", screenName).toString());
            }
        }
        //System.out.println("Url con accion: " + actionUrl);
        if (null != actionUrl && !actionUrl.isEmpty()) {
            request.getSession().setAttribute("tw_tkn", token);
            request.getSession().setAttribute("tw_tknScrt", tokenSecret);
            response.setStatus(302);
            response.setHeader("Location", actionUrl);
        }
        
    }
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        
        String signature;
        String consumerKey = paramRequest.getWebPage().getWebSite()
                .getModelProperty("twitter_consumerKey");
        String consumerSecret = paramRequest.getWebPage().getWebSite()
                .getModelProperty("twitter_consumerSecret");
        final String oauthVersion = "1.0";
        String token = null;
        String tokenSecret = null;
        boolean callbackConfirmed = false;
        
        if (null != consumerKey && !consumerKey.isEmpty() &&
                null != consumerSecret && !consumerSecret.isEmpty()) {
            GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
            
            String timestamp = Long.toString(cal.getTimeInMillis() / 1000);
            String nonce = this.getNonce();
            String requestTokenUrl = "https://api.twitter.com/oauth/request_token";
            String reqHost = request.getRequestURL().toString();
            int indexColon = reqHost.indexOf(":", 7);
            String pathBegining = null;
            WebSite website = paramRequest.getWebPage().getWebSite();
            if (indexColon != -1) {
                pathBegining = reqHost.substring(0, reqHost.indexOf("/", indexColon));
            } else {
                pathBegining = reqHost.substring(0, reqHost.indexOf("/", 10));
            }
            String callbackTmp = pathBegining + paramRequest.getRenderUrl().setCallMethod(
                    SWBParamRequest.Call_DIRECT).setMode("authenticateToken").toString();
            int indexSection = callbackTmp.indexOf(website.getId()) + website.getId().length() + 1;
            String callback = callbackTmp.substring(0, indexSection) +
                     "home" + callbackTmp.substring(callbackTmp.indexOf("/", indexSection + 1));
            
            OAuthTwitter.LOG.debug("Callback: " + callback);
            OAuthTwitter.LOG.debug("oauth_consumer_key: " + consumerKey);
            OAuthTwitter.LOG.debug("oauth_nonce: " + nonce);
            OAuthTwitter.LOG.debug("oauth_signature_method: " + this.SIGN_METHOD);
            OAuthTwitter.LOG.debug("oauth_timestamp: " + timestamp);
            request.getSession().setAttribute("returnPoint", reqHost);
            //Se recaban los datos para la firma de la peticion
            TreeMap<String, String> params2Sign = new TreeMap();
            params2Sign.put("oauth_callback", callback);
            params2Sign.put("oauth_consumer_key", consumerKey);
            params2Sign.put("oauth_nonce", nonce);
            params2Sign.put("oauth_signature_method", this.SIGN_METHOD);
            params2Sign.put("oauth_timestamp", timestamp);
            params2Sign.put("oauth_version", oauthVersion);
            
            //Se obtiene la firma de la peticion
            signature = generateSignature(params2Sign, this.POST_METHOD,
                    requestTokenUrl, consumerSecret, null);
            params2Sign.put("oauth_signature", signature);
            
            //Se preparan los datos para la peticion
            HashMap<String, String> reqHeaders = new HashMap(8);
            reqHeaders.put("user-agent", request.getHeader("User-Agent"));
            
            String oauthHeader = this.getAuthorizationReqHeader(params2Sign);
            //System.out.println("URL: " + requestTokenUrl);
            //System.out.println("Authorization header: \n" + oauthHeader);
            reqHeaders.put("Authorization", oauthHeader);
            String reqTokenResponse = null;
            
            //Se ejecuta la peticion
            reqTokenResponse = this.postRequest(null, reqHeaders,
                    requestTokenUrl, this.POST_METHOD);
            
            //se parsea la respuesta para obtener los datos necesarios para la segunda peticion
            if (null != reqTokenResponse && !reqTokenResponse.isEmpty()) {
                if (reqTokenResponse.contains("&")) {
                    String[] paramsResp = reqTokenResponse.split("&");
                    for (String part : paramsResp) {
                        if (part.contains("=")) {
                            String[] pair = part.split("=");
                            switch (pair[0]) {
                                case "oauth_token":
                                        token = pair[1];
                                        break;
                                case "oauth_token_secret":
                                        tokenSecret = pair[1];
                                        break;
                                case "oauth_callback_confirmed":
                                        callbackConfirmed = Boolean.parseBoolean(pair[1]);
                                        break;
                            }
                        }
                    }
                }
            }
            //System.out.println("Respuesta token secret:\n" + reqTokenResponse);
        }
        
        //System.out.println("    ***** Datos del request token *****\ncallbackConfirmed: " + callbackConfirmed);
        //System.out.println("tokenSecret: " + tokenSecret);
        //System.out.println("token: " + token);
        if (callbackConfirmed && null != token) {
            String urlRedirect = "https://api.twitter.com/oauth/authenticate?oauth_token=" +
                    token;
            response.setStatus(302);
            response.setHeader("Location", urlRedirect);
            request.getSession().setAttribute("t_token", token);
        } else {
            try {
                PrintWriter out = response.getWriter();
                out.println("<html>");
                out.println("  <head><title>Login with Twitter</title></head>");
                out.println("  <body>");
                out.println("    <h3>Hubo un problema al crear la conexi&oacute;n con Twitter, por favor int&eacute;ntalo m&aacute;s tarde</h3>");
                out.println("    <p>Puedes regresar a la p&aacute;gina anterior haciendo clic <a href=\"#\" onclick=\"history.go(-1);\">aqu&iacute;</a></p>");
                out.println("  </body>");
                out.println("</html>");
            } catch (Exception se) {
                se.printStackTrace(System.err);
            }
        }
    }

    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        
        String mode = paramRequest.getMode();
        
        if (mode.equals("authenticateToken")) {
            this.doAuthToken(request, response, paramRequest);
        } else {
            super.processRequest(request, response, paramRequest);
        }
    }
    
    /**
     * Genera la firma de la peticion a realizar, con base en los parametros que forman la peticion
     * @param params
     * @param method
     * @param baseUrl
     * @param consumerSecret
     * @return 
     */
    public static String generateSignature(TreeMap<String, String> params, String method,
            String baseUrl, String consumerSecret, String tokenSecret) {
        
        //System.out.println("SIGNING request ----------");
        StringBuilder paramsString = new StringBuilder(64);
        int count = 0;
        Mac mac;
        byte[] macData = null;
        
        for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            if (count > 0) {
                paramsString.append('&');
            }
            paramsString.append(percentageEncode(key));
            paramsString.append('=');
            paramsString.append(percentageEncode(params.get(key)));
            count++;
        }
        
        StringBuilder baseSignature = new StringBuilder(128);
        baseSignature.append(method.toUpperCase());
        baseSignature.append('&');
        baseSignature.append(percentageEncode(baseUrl));
        baseSignature.append('&');
        baseSignature.append(percentageEncode(paramsString.toString()));
//        System.out.println(" -- baseSignature: " + baseSignature);
        
        String signingKey = percentageEncode(consumerSecret) + '&' +
                (null != tokenSecret ? percentageEncode(tokenSecret) : "");
//        System.out.println(" -- signingKey: " +  signingKey);
        
        SecretKeySpec secretKey = new SecretKeySpec(signingKey.getBytes(), "HmacSHA1");
        try {
            mac = Mac.getInstance("HmacSHA1");
        } catch (NoSuchAlgorithmException nsae) {
            mac = null;
        }
        if (null != mac) {
            try {
                mac.init(secretKey);
                macData = mac.doFinal(baseSignature.toString().getBytes()); // o getBytes("UTF-8")
            } catch (InvalidKeyException ike) {
                OAuthTwitter.LOG.error("Using key: " + signingKey, ike);
            }
        }
        BASE64Encoder base64 = new BASE64Encoder();
//        System.out.println("/n con SWBUtils, Signature: " + SWBUtils.TEXT.encodeBase64(new String(macData)));
        //System.out.println("    -- con BASE64: " + base64.encode(macData));
        return base64.encode(macData);
    }
    
    /**
     * Realiza peticiones a la plataforma de Twitter que deban ser enviadas por
     * alg&uacute;n m&eacute;todo en particular
     *
     * @param params contiene los par&aacute;metros a enviar en la petici&oacute;n para
     * realizar la operaci&oacute;n deseada
     * @param headers contiene los par&aacute;metros a enviar en el encabezado de la petici&oacute;n
     * @param url especifica la ruta con la que se desea interactuar
     * @param method indica el m&eacute;todo de la petici&oacute; HTTP requerido
     *  para realizar una operaci&oacute;n, como: {@literal POST} o {@literal DELETE}
     * @return un {@code String} que representa la respuesta recibida
     * @throws IOException en caso de que se produzca un error al generar la
     * petici&oacute;n o recibir la respuesta
     */
    public String postRequest(HashMap<String, String> params, HashMap<String, String> headers, String url,
            String method) throws IOException {
        
        //System.out.println("url:"+url);
        URL serverUrl = new URL(url);
        CharSequence paramString = (null == params) ? "" : delimit(params.entrySet(), "&", "=", true);
        //System.out.println(" +++ Url: " + url + " params: " + paramString.toString());
        HttpURLConnection conex = null;
        OutputStream out = null;
        InputStream in = null;
        String response = null;
        int status = 0;
        String statusMsg = null;
        
        if (method == null) {
            method = "POST";
        }
        try {
            conex = (HttpURLConnection) serverUrl.openConnection();
            conex.setConnectTimeout(5000);
            conex.setReadTimeout(10000);
            conex.setRequestMethod(method);
            for (String key : headers.keySet()) {
                conex.setRequestProperty(key, headers.get(key));
            }
            conex.setDoOutput(true);
            conex.connect();
            out = conex.getOutputStream();
            out.write(paramString.toString().getBytes("UTF-8"));
            in = conex.getInputStream();
            response = getResponse(in);
            status = conex.getResponseCode();
            if (status != 200) {
                statusMsg = conex.getResponseMessage();
                OAuthTwitter.LOG.debug("Twitter request: " + url + "\nstatus: " +
                        status + " - " + statusMsg);
//                System.out.println("Twitter request: " + url + "\nstatus: " +
//                        status + " - " + statusMsg);
            }
        } catch (java.io.IOException ioe) {
            //response = getResponse(conex.getErrorStream());
            if (conex != null && null != conex.getErrorStream()) {
                for (String header : conex.getHeaderFields().keySet()) {
                    StringBuilder resp = new StringBuilder(128);
                    resp.append(header);
                    resp.append(": ");
                    for (String value : conex.getHeaderFields().get(header)) {
                        resp.append(value);
                        resp.append("; ");
                    }
                    OAuthTwitter.LOG.error(resp.toString());
                }
                response = getResponse(conex.getErrorStream());
                OAuthTwitter.LOG.error("Twitter response: " + response);
            }
            response = null;
            OAuthTwitter.LOG.error("Sending POST to Twitter", ioe);
        } finally {
            close(in);
            close(out);
            if (conex != null) {
                conex.disconnect();
            }
        }
//        System.out.println("RESPONSE unformatted: " + response);
        if (response == null) {
            response = "";
        }
        return response;
    }

    /**
     * Con base en el contenido de la colecci&oacute;n recibida, arma una secuencia
     * de caracteres compuesta de los pares:
     * <p>{@code Entry.getKey()}<{@code equals}>{@code Entry.getValue()} </p> Si
     * en la colecci&oacute;n hay m&aacute;s de una entrada, los pares (como el
     * anterior), se separan por {@code delimiter}.
     *
     * @param entries la colecci&oacute;n con la que se van a formar los pares
     * @param delimiter representa el valor con que se van a separar los pares a
     * representar
     * @param equals representa el valor con el que se van a relacionar los
     * elementos de cada par a representar
     * @param doEncode indica si el valor representado en cada par, debe ser
     * codificado (UTF-8) o no
     * @return la secuencia de caracteres que representa el conjunto de pares
     * @throws UnsupportedEncodingException en caso de ocurrir algun problema en
     * la codificaci&oacute;n a UTF-8 del valor de alg&uacute;n par, si
     * as&iacute; se indica en {@code doEncode}
     */
    private static CharSequence delimit(Collection<Map.Entry<String, String>> entries,
            String delimiter, String equals, boolean doEncode)
            throws UnsupportedEncodingException {

        if (entries == null || entries.isEmpty()) {
            return null;
        }
        StringBuilder buffer = new StringBuilder(64);
        boolean notFirst = false;
        for (Map.Entry<String, String> entry : entries) {
            if (notFirst) {
                buffer.append(delimiter);
            } else {
                notFirst = true;
            }
            CharSequence value = entry.getValue();
            buffer.append(entry.getKey());
            buffer.append(equals);
            buffer.append(doEncode ? encode(value) : value);
        }
        return buffer;
    }

    /**
     * Codifica el valor de {@code target} de acuerdo al c&oacute;digo de
     * caracteres UTF-8
     *
     * @param target representa el texto a codificar
     * @return un {@code String} que representa el valor de {@code target}, de
     * acuerdo al c&oacute;digo de caracteres UTF-8
     * @throws UnsupportedEncodingException en caso de ocurrir alg&uacute;n problema en
     * la codificaci&oacute;n a UTF-8
     */
    private static String encode(CharSequence target) throws UnsupportedEncodingException {

        String result = "";
        if (target != null) {
            result = target.toString();
            result = URLEncoder.encode(result, "UTF8");
        }
        return result;
    }

    /**
     * Lee un flujo de datos y lo convierte en un {@code String} con su
     * contenido codificado en UTF-8
     *
     * @param data el flujo de datos a convertir
     * @return un {@code String} que representa el contenido del flujo de datos
     * especificado, codificado en UTF-8
     * @throws IOException si ocurre un problema en la lectura del flujo de
     * datos
     */
    private static String getResponse(InputStream data) throws IOException {
        
        Reader in = new BufferedReader(new InputStreamReader(data, "UTF-8"));
        StringBuilder response = new StringBuilder(256);
        char[] buffer = new char[1000];
        int charsRead = 0;
        
        while (charsRead >= 0) {
            response.append(buffer, 0, charsRead);
            charsRead = in.read(buffer);
        }
        in.close();
        return response.toString();
    }

    /**
     * Cierra el objeto recibido
     *
     * @param c cualquier objeto que tenga la factultad de cerrarse
     */
    private static void close(Closeable c) {
        
        if (c != null) {
            try {
                c.close();
            } catch (IOException ex) {
                OAuthTwitter.LOG.error("Error at closing object: " + c.getClass().getName(),
                        ex);
            }
        }
    }
    
    /**
     * Genera la codificacion con porcentaje de la cadena recibida
     * @param value
     * @return 
     */
    public static String percentageEncode(String value) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            OAuthTwitter.LOG.error("Percentage encoding: " + value, e);
        }
        StringBuilder sb = new StringBuilder(64);
        char focus;
        for (int i = 0; i < encoded.length(); i++) {
            focus = encoded.charAt(i);
            if (focus == '*') {
                sb.append("%2A");
            } else if (focus == '+') {
                sb.append("%20");
            } else if (focus == '%' && i + 1 < encoded.length()
                    && encoded.charAt(i + 1) == '7' && encoded.charAt(i + 2) == 'E') {
                sb.append('~');
                i += 2;
            } else {
                sb.append(focus);
            }
        }
        return sb.toString();
    }
    
    /**
     * Genera una cadena aleatoria de 32 bytes a manera de Nonce
     * @return una cadena de 32 bytes codificada en base 64 para utilizarse como 
     *      indicador de unicidad en cada petición firmada
     */
    public static String getNonce() {
        
        String base2nonce = SWBPortal.UTIL.getRandString(32, OAuthTwitter.ALPHABET);
        base2nonce = SWBUtils.TEXT.encodeBase64(base2nonce);
        StringBuilder ret = new StringBuilder(64);
        char oneChar;
        
        for (int i = 0; i < base2nonce.length(); i++) {
            oneChar = base2nonce.charAt(i);
            if (oneChar != '+' && oneChar != '/') {
                ret.append(oneChar);
            }
        }
        
        return ret.toString();
    }
    
    /**
     * Genera el valor del encabezado {@literal Authorization para las peticiones}
     * @param oauthParams
     * @return 
     */
    public static String getAuthorizationReqHeader(TreeMap<String, String> oauthParams) {
        
        StringBuilder oauthHeader = new StringBuilder(128);
        int count = 0;
        oauthHeader.append("OAuth ");
        for (String signKey : oauthParams.keySet()) {
            if (count > 0) {
                oauthHeader.append(',');
            }
            oauthHeader.append(signKey);
            oauthHeader.append("=\"");
            oauthHeader.append(percentageEncode(oauthParams.get(signKey)));
            oauthHeader.append('\"');
            count++;
        }
        return oauthHeader.toString();
    }
    
    /**
     * Realiza una peticion get a la URL especificada
     * @param params contiene las parejas par&aacute;metro-valor necesarias para la petici&oacute;n
     * @param headers contiene los par&aacute;metros a enviar en el encabezado de la petici&oacute;n
     * @param url especifica la ruta con la que se desea interactuar
     * @return la respuesta del servidor o el mensaje de error obtenido de la
     * petici&oacute;n
     * @throws IOException en caso de que se produzca un error al generar la
     * petici&oacute;n o recibir la respuesta
     */
    public static String getRequest(HashMap<String, String> params,
            HashMap<String, String> headers, String url)
            throws IOException {

        CharSequence paramString = (null == params || params.isEmpty())
                ? "" : delimit(params.entrySet(), "&", "=", true);
        URL serverUrl = new URL(paramString.length() > 0
                                ? (url + "?" + paramString) : url);
//        System.out.println("URL: \n" + serverUrl);
        HttpURLConnection conex = null;
        String response = null;

        try {
            conex = (HttpURLConnection) serverUrl.openConnection();
            conex.setConnectTimeout(5000);
            conex.setReadTimeout(10000);
            conex.setRequestMethod(OAuthTwitter.GET_METHOD);
            for (String key : headers.keySet()) {
                conex.setRequestProperty(key, headers.get(key));
            }
            conex.setDoOutput(true);
            conex.connect();
            response = getResponse(conex.getInputStream());
//            System.out.println("  +++++ GET response: \n" + response);
        } catch (java.io.IOException ioe) {
            if (null != conex) {
                response = getResponse(conex.getErrorStream());
                OAuthTwitter.LOG.error("Unsuccessful request to: " +
                        serverUrl.toString() + "\n" + response, ioe);
            }
        } finally {
            if (conex != null) {
                close(conex.getInputStream());
                conex.disconnect();
            }
        }
        if (response == null) {
            response = "";
        }
        return response;
    }
    
}
