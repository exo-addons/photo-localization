package org.exoplatform.addons.photolocalization;

import juzu.Path;
import juzu.Response;
import juzu.View;
import juzu.request.HttpContext;
import juzu.request.RequestContext;
import juzu.template.Template;

import javax.inject.Inject;
import java.util.HashMap;

/**
 * Created by Romain Dénarié (romain.denarie@exoplatform.com) on 02/03/17.
 */
public class PhotoLocalization {
    @Inject
    @Path("photoLocalization.gtmpl")
    Template index;

    /** The populator service. */
    @Inject
    PhotoLocalizationService photoLocalizationService;

    @View
    public Response.Content index(RequestContext context) {
        HashMap parameters = new HashMap();


        HttpContext httpContext = context.getHttpContext();

        String rssUrl = httpContext.getScheme()+ "://" + httpContext.getServerName();
        if (httpContext.getServerPort()!=-1 && httpContext.getServerPort()!=80 && httpContext.getServerPort()!=443) {
            rssUrl+=":" + Integer.toString(httpContext.getServerPort());
        }
        rssUrl+=photoLocalizationService.getRssUrl();
        parameters.put("rssUrl",rssUrl);

        String mapsJsUrl = "https://maps.googleapis.com/maps/api/js?";
        if (photoLocalizationService.getAPIKey().equals("")) {
            mapsJsUrl += "callback=initMap";
        } else {
            mapsJsUrl += "key=AIzaSyC3TOM0ojudOfaTEHPfPJkN1FVBeMUNNuE&callback=initMap";
        }

        parameters.put("mapsJSUrl",mapsJsUrl);

        return index.ok(parameters);
    }
}
