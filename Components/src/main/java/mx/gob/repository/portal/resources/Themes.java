
package mx.gob.repository.portal.resources;

import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mx.gob.cultura.portal.response.Entry;
import mx.gob.cultura.portal.response.Document;
import mx.gob.cultura.portal.response.CountName;
import mx.gob.cultura.portal.response.DateRange;
import mx.gob.cultura.portal.response.Aggregation;

import org.semanticwb.Logger;
import org.semanticwb.SWBUtils;
import org.semanticwb.model.WebSite;
import mx.gob.cultura.portal.utils.Utils;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;

import static mx.gob.cultura.portal.utils.Constants.PARAM_REQUEST;
import static mx.gob.cultura.portal.resources.SearchCulturalProperty.setThumbnail;
/**
 *
 * @author sergio.tellez
 */
public class Themes extends GenericResource {
    
    private static final int SEGMENT = 8;
    private static final Logger LOG = SWBUtils.getLogger(Themes.class);
    
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        String path = "/work/models/"+paramRequest.getWebPage().getWebSite().getId()+"/jsp/rnc/themes/themes.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            Document document = SearchCulturalProperty.getReference(request, paramRequest.getWebPage().getWebSite());
            setType(request, document,  paramRequest.getWebPage().getWebSite(), 0);
            request.setAttribute(PARAM_REQUEST, paramRequest);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    private void setType(HttpServletRequest request, Document document, WebSite site, int page) {
        int i = 0;
        int p = page > 0 ? page-1 : 0;
        if (null != document.getRecords() && !document.getRecords().isEmpty()) {
            for (Entry e : document.getRecords()) {
                e.setPosition(i + (p)*SEGMENT);
                setThumbnail(e, site, 0);
                i++;
            }
        }
        request.setAttribute("aggs", getAggregation(document.getAggs()));
        request.setAttribute("pdf", getAgg(document.getAggs(), "pdf"));
        request.setAttribute("zip", getAgg(document.getAggs(), "zip"));
        request.setAttribute("image", getAgg(document.getAggs(), "image"));
        request.setAttribute("audio", getAgg(document.getAggs(), "audio"));
        request.setAttribute("video", getAgg(document.getAggs(), "video"));
    }
    
    private Aggregation getAggregation(List<Aggregation> aggs) {
        DateRange interval = new DateRange();
        Calendar cal = Calendar.getInstance();
        Aggregation aggregation = new Aggregation();
        interval.setUpperLimit(0);
        cal.setTime(new Date());
        interval.setLowerLimit(cal.get(Calendar.YEAR));
        aggregation.setInterval(interval);
        if (null != aggs && !aggs.isEmpty()) {
            aggregation.setDates(new ArrayList<>());
            aggregation.setRights(new ArrayList<>());
            aggregation.setHolders(new ArrayList<>());
            aggregation.setLanguages(new ArrayList<>());
            aggregation.setMediastype(new ArrayList<>());
            aggregation.setResourcetypes(new ArrayList<>());
            for (Aggregation a : aggs) {
                if (null !=  a.getDates()) aggregation.getDates().addAll(a.getDates());
                if (null !=  a.getRights()) aggregation.getRights().addAll(a.getRights());
                if (null !=  a.getHolders()) aggregation.getHolders().addAll(a.getHolders());
                if (null !=  a.getLanguages()) aggregation.getLanguages().addAll(a.getLanguages());
                if (null !=  a.getResourcetypes()) aggregation.getResourcetypes().addAll(a.getResourcetypes());
                if (null !=  a.getMediastype()) aggregation.getMediastype().addAll(getTypes(a.getMediastype()));
            }
            for (CountName date : aggregation.getDates()) {
                cal.setTime(Utils.convert(date.getName(), "yyyy-MM-dd'T'HH:mm:ss"));
                if (interval.getUpperLimit() < cal.get(Calendar.YEAR)) interval.setUpperLimit(cal.get(Calendar.YEAR));
                if (interval.getLowerLimit() > cal.get(Calendar.YEAR)) interval.setLowerLimit(cal.get(Calendar.YEAR));
            }
        }
        return aggregation;
    }
    
    private List<CountName> getAgg(List<Aggregation> aggs, String type) {
        List<CountName> list= new ArrayList<>();
        if (null != aggs && !aggs.isEmpty()) {
            for (Aggregation a : aggs) {
                if (null !=  a.getMediastype()) {
                    for (CountName c : a.getMediastype()) {
                        if (type.equals("image") && (c.getName().startsWith("image") || c.getName().equalsIgnoreCase("jpg") || c.getName().equalsIgnoreCase("png"))) list.add(c);
                        else if (type.equals("pdf") && (c.getName().startsWith("pdf") || c.getName().equalsIgnoreCase("application/pdf"))) list.add(c);
                        else if (type.equals("audio") && (c.getName().startsWith("audio") || c.getName().equalsIgnoreCase("aiff") || c.getName().equalsIgnoreCase("wav") || c.getName().equalsIgnoreCase("mp3"))) list.add(c);
                        else if (type.equals("video") && (c.getName().startsWith("video") || c.getName().equalsIgnoreCase("avi") || c.getName().equalsIgnoreCase("mp4") || c.getName().equalsIgnoreCase("mov"))) list.add(c);
                        else if (type.equals("zip") && (c.getName().startsWith("zip") || c.getName().equalsIgnoreCase("application/zip"))) list.add(c);
                    }
                }
            }
        }
        return list;
    }
    
    private List<CountName> getTypes(List<CountName> media) {
        List<CountName> types = new ArrayList<>();
        CountName pdf = new CountName("PDF", 0);
        CountName zip = new CountName("ZIP", 0);
        CountName three = new CountName("3D", 0);
        CountName eBook = new CountName("EPUB", 0);
        CountName image = new CountName("Imagen", 0);
        CountName audio = new CountName("Audio", 0);
        CountName video = new CountName("Video", 0);
        for (CountName c : media) {
            if (c.getName().equalsIgnoreCase("jpg") || c.getName().equalsIgnoreCase("png") || c.getName().startsWith("image")) image.setCount(image.getCount() + c.getCount());
            if (c.getName().equalsIgnoreCase("aiff") || c.getName().equalsIgnoreCase("wav") || c.getName().equalsIgnoreCase("mp3") || c.getName().startsWith("audio")) audio.setCount(audio.getCount() + c.getCount());
            if (c.getName().equalsIgnoreCase("avi") || c.getName().startsWith("video") || c.getName().equalsIgnoreCase("mp4") || c.getName().equalsIgnoreCase("mov")) video.setCount(video.getCount() + c.getCount());
            if (c.getName().equalsIgnoreCase("pdf") || c.getName().equalsIgnoreCase("application/pdf")) pdf.setCount(pdf.getCount() + c.getCount());
            if (c.getName().startsWith("model/x3d")) three.setCount(three.getCount() + c.getCount());
            if (c.getName().equalsIgnoreCase("zip") || c.getName().equalsIgnoreCase("application/zip")) zip.setCount(zip.getCount() + c.getCount());
            if (c.getName().equalsIgnoreCase("application/epub+zip")) eBook.setCount(eBook.getCount() + c.getCount());
        }
        if (image.getCount() > 0) types.add(image);
        if (audio.getCount() > 0) types.add(audio);
        if (video.getCount() > 0) types.add(video);
        if (pdf.getCount() > 0) types.add(pdf);
        if (eBook.getCount() > 0) types.add(eBook);
        if (three.getCount() > 0) types.add(three);
        if (zip.getCount() > 0) types.add(zip);
        return types;
    }
}