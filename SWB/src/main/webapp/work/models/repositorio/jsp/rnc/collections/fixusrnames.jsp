<%-- 
    Document   : fixusrnames
    Created on : 28-nov-2018, 19:16:14
    Author     : juan.fernandez
--%>

<%@page import="com.mongodb.BasicDBObject"%>
<%@page import="org.semanticwb.model.User"%>
<%@page import="org.semanticwb.model.UserRepository"%>
<%@page import="org.semanticwb.model.SWBContext"%>
<%@page import="org.semanticwb.model.WebSite"%>
<%@page import="com.mongodb.DBObject"%>
<%@page import="com.mongodb.DBCursor"%>
<%@page import="mx.gob.cultura.commons.Util"%>
<%@page import="com.mongodb.DBCollection"%>
<%@page import="com.mongodb.DB"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    WebSite ws = SWBContext.getWebSite("repositorio");
    UserRepository usrrep = ws.getUserRepository();
    int proc = 0;
    int updt = 0;

    try {
        DB db = Util.MONGODB.getMongoClient().getDB("front");
        DBCollection cols = db.getCollection("composing");
        DBCursor cursor = cols.find();
        BasicDBObject dbQuery = null;

        while (null != cursor && cursor.hasNext()) {
            proc++;
            DBObject next = cursor.next();
            dbQuery = new BasicDBObject("_id", next.get("_id"));
            String usrname = (String)next.get("username");
            if (null != usrname && !usrname.trim().isEmpty()) {
                System.out.println("tiene nombre....");
                continue;
            }
            System.out.println("No tiene nombre de usuario....");
            String usrid = (String)next.get("userid");
            if (null != usrid && !usrid.trim().isEmpty()) {
                // id del usuario creador
                System.out.println("ID usuario creador."+usrid);
                User usr = usrrep.getUser(usrid);
                if (null != usr) {
                    usrname = usr.getFullName();
                    System.out.println("Nombre:"+usrname);
                }
                if (null != usrname && !usrname.trim().isEmpty()) {
                    next.put("username", usrname);
                    cols.update(dbQuery,next);
                    updt++;
                }
            }
        }
        cursor.close();


    } catch (Exception e) {
        System.out.println("Error al actualizar nombre del usuario en la colección\n");
        e.printStackTrace(System.out);
    }


%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Actualizando nombre de usuarios en colección</title>
    </head>
    <body>
        <h1>Colecciones Revisadas <%=proc%></h1
        <h1>Colecciones Actualizadas <%=updt%></h1>
    </body>
</html>
