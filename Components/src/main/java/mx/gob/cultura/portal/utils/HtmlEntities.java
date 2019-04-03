
package mx.gob.cultura.portal.utils;

import java.util.*;
import java.util.regex.*;
/**
 *
 * @author sergio.tellez
 */
public class HtmlEntities {
    private static final Map<String, Character> mapChars = new LinkedHashMap<String, Character>();
    static {
    	mapChars.put("&quot;", (char) 34);
    	mapChars.put("&amp;", (char) 38);
    	mapChars.put("&lt;", (char) 60);
    	mapChars.put("&gt;", (char) 62);
        mapChars.put("&nbsp;", (char) 160);
        mapChars.put("&iexcl;", (char) 161);
        mapChars.put("&cent;", (char) 162);
        mapChars.put("&pound;", (char) 163);
        mapChars.put("&curren;", (char) 164);
        mapChars.put("&yen;", (char) 165);
        mapChars.put("&brvbar;", (char) 166);
        mapChars.put("&sect;", (char) 167);
        mapChars.put("&uml;", (char) 168);
        mapChars.put("&copy;", (char) 169);
        mapChars.put("&ordf;", (char) 170);
        mapChars.put("&laquo;", (char) 171);
        mapChars.put("&not;", (char) 172);
        mapChars.put("&shy;", (char) 173);
        mapChars.put("&reg;", (char) 174);
        mapChars.put("&macr;", (char) 175);
        mapChars.put("&deg;", (char) 176);
        mapChars.put("&plusmn;", (char) 177);
        mapChars.put("&sup2;", (char) 178);
        mapChars.put("&sup3;", (char) 179);
        mapChars.put("&acute;", (char) 180);
        mapChars.put("&micro;", (char) 181);
        mapChars.put("&para;", (char) 182);
        mapChars.put("&middot;", (char) 183);
        mapChars.put("&cedil;", (char) 184);
        mapChars.put("&sup1;", (char) 185);
        mapChars.put("&ordm;", (char) 186);
        mapChars.put("&raquo;", (char) 187);
        mapChars.put("&frac14;", (char) 188);
        mapChars.put("&frac12;", (char) 189);
        mapChars.put("&frac34;", (char) 190);
        mapChars.put("&iquest;", (char) 191);
        mapChars.put("&times;", (char) 215);
        mapChars.put("&divide;", (char) 247);
        mapChars.put("&Agrave;", (char) 192);
        mapChars.put("&Aacute;", (char) 193);
        mapChars.put("&Acirc;", (char) 194);
        mapChars.put("&Atilde;", (char) 195);
        mapChars.put("&Auml;", (char) 196);
        mapChars.put("&Aring;", (char) 197);
        mapChars.put("&AElig;", (char) 198);
        mapChars.put("&Ccedil;", (char) 199);
        mapChars.put("&Egrave;", (char) 200);
        mapChars.put("&Eacute;", (char) 201);
        mapChars.put("&Ecirc;", (char) 202);
        mapChars.put("&Euml;", (char) 203);
        mapChars.put("&Igrave;", (char) 204);
        mapChars.put("&Iacute;", (char) 205);
        mapChars.put("&Icirc;", (char) 206);
        mapChars.put("&Iuml;", (char) 207);
        mapChars.put("&ETH;", (char) 208);
        mapChars.put("&Ntilde;", (char) 209);
        mapChars.put("&Ograve;", (char) 210);
        mapChars.put("&Oacute;", (char) 211);
        mapChars.put("&Ocirc;", (char) 212);
        mapChars.put("&Otilde;", (char) 213);
        mapChars.put("&Ouml;", (char) 214);
        mapChars.put("&Oslash;", (char) 216);
        mapChars.put("&Ugrave;", (char) 217);
        mapChars.put("&Uacute;", (char) 218);
        mapChars.put("&Ucirc;", (char) 219);
        mapChars.put("&Uuml;", (char) 220);
        mapChars.put("&Yacute;", (char) 221);
        mapChars.put("&THORN;", (char) 222);
        mapChars.put("&szlig;", (char) 223);
        mapChars.put("&agrave;", (char) 224);
        mapChars.put("&aacute;", (char) 225);
        mapChars.put("&acirc;", (char) 226);
        mapChars.put("&atilde;", (char) 227);
        mapChars.put("&auml;", (char) 228);
        mapChars.put("&aring;", (char) 229);
        mapChars.put("&aelig;", (char) 230);
        mapChars.put("&ccedil;", (char) 231);
        mapChars.put("&egrave;", (char) 232);
        mapChars.put("&eacute;", (char) 233);
        mapChars.put("&ecirc;", (char) 234);
        mapChars.put("&euml;", (char) 235);
        mapChars.put("&igrave;", (char) 236);
        mapChars.put("&iacute;", (char) 237);
        mapChars.put("&icirc;", (char) 238);
        mapChars.put("&iuml;", (char) 239);
        mapChars.put("&eth;", (char) 240);
        mapChars.put("&ntilde;", (char) 241);
        mapChars.put("&ograve;", (char) 242);
        mapChars.put("&oacute;", (char) 243);
        mapChars.put("&ocirc;", (char) 244);
        mapChars.put("&otilde;", (char) 245);
        mapChars.put("&ouml;", (char) 246);
        mapChars.put("&oslash;", (char) 248);
        mapChars.put("&ugrave;", (char) 249);
        mapChars.put("&uacute;", (char) 250);
        mapChars.put("&ucirc;", (char) 251);
        mapChars.put("&uuml;", (char) 252);
        mapChars.put("&yacute;", (char) 253);
        mapChars.put("&thorn;", (char) 254);
        mapChars.put("&yuml;", (char) 255);
    }
 
    /**
     * Find the Html Entity and convert it back to a regular character if the
     * entity exists, otherwise return the same string.
     * @param str
     * @return Character represented by HTML Entity or the same string if unknown entity.
     */
    private static String fromHtmlEntity(String str) {
    	Character ch = mapChars.get(str);
    	return ( ch != null ) ? ch.toString() : str;
    }
    
    /**
     * Finds the value and returns the key that corresponds to that value. If value not found
     * returns null.
     * @param value The value to be found.
     * @return The key corresponding to the value that was found or null if value not found.
     */
    private static String findValue(char value) {
        boolean found = false;
    	String result = null;
    	Set<String> keySet = mapChars.keySet();
    	Iterator<String> i = keySet.iterator();
    	String key = i.next(); // key
    	while(i.hasNext() && !found) {
            if (mapChars.get(key) == value) {
    		found = true;
    		result = key;
            }
            key = i.next();
    	}
    	return result;
    }
    
    /**
     * Converts special characters in ASCII into html entities (e.g. & -> &amp;)
     * @param encode The string to be encoded.
     * @return The encoded string with HTML entities.
     */
    public static String encode(String encode) {
        int i = 0;
        String key;
    	StringBuilder str = new StringBuilder(encode);
    	// loop over all the characters in the string
    	while (i < str.length()) {
            // try matching a character to an entity
            key = findValue(str.charAt(i));
            if (key != null) {
                str.replace(i, i + 1, key);
                i += key.length();
            }else
                i++;
        }
    	return str.toString();
    }
    
    public static String chartExpected(String badEncoding) {
        if (null == badEncoding || badEncoding.isEmpty()) return "";
        if (badEncoding.contains("Ã¡"))
            badEncoding = badEncoding.replace("Ã¡", "á");
        if (badEncoding.contains("Ã©"))
            badEncoding = badEncoding.replace("Ã©", "é");
        if (badEncoding.contains("Ã­"))
            badEncoding = badEncoding.replace("Ã­", "í");
        if (badEncoding.contains("Ã³"))
            badEncoding = badEncoding.replace("Ã³", "ó");
        if (badEncoding.contains("Ãº"))
            badEncoding = badEncoding.replace("Ãº", "ú");
        if (badEncoding.contains("Ã±"))
            badEncoding = badEncoding.replace("Ã±", "ñ");
        if (badEncoding.contains("Ã"))
            badEncoding = checkAscii(badEncoding);
        System.out.println(badEncoding);
        return badEncoding;
    }
    
    private static String checkAscii(String badEncoding) {
        String ascii = "";
        for (int i=0; i<badEncoding.length(); i++) {
            if (badEncoding.codePointAt(i) == 195 && badEncoding.codePointAt(i+1) == 147)  {
                ascii += "Ó";
                i++;
            }else ascii += badEncoding.charAt(i);
        }
        return ascii;
    }
    
    /**
     * Converts html entities (e.g. &amp;) into real characters (ASCII characters, e.g. &amp; -> &)
     * @param decode A string to be decoded.
     * @return The string decoded with no HTML entities.
     */
    public static String decode(String decode) {
        int matchPointer = 0;
        String replaceStr = null;
        StringBuilder str = new StringBuilder(decode);
        Matcher m = Pattern.compile("&[A-Za-z]+;").matcher(str);
        while (m.find(matchPointer)) {
            // check if we have a corresponding key in our mapChars
            replaceStr = fromHtmlEntity(m.group());
            str.replace(m.start(), m.end(), replaceStr);
            matchPointer = m.start() + replaceStr.length();
        }
        return str.toString();
    }
}
