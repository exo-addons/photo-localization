package org.exoplatform.addons.photolocalization;

import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.impl.EnvironmentContext;

import javax.jcr.Node;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Romain Dénarié (romain.denarie@exoplatform.com) on 02/03/17.
 */
public class AddImageListener extends Listener<Object, Node> {

    PhotoLocalizationService photoLocalizationService;
    private static final Log LOG = ExoLogger.getLogger(AddImageListener.class.getName());


    public AddImageListener(PhotoLocalizationService photoLocalizationService) {
        this.photoLocalizationService=photoLocalizationService;
    }

    @Override
    public void onEvent(Event<Object, Node> event) throws Exception {
        if (event.getData().getPath().startsWith(photoLocalizationService.getPhotoFolder())) {
            //photoLocalizationService.generateRSS(event)
            LOG.info("Listener launched");

            EnvironmentContext context = EnvironmentContext.getCurrent();
            HttpServletRequest request = (HttpServletRequest) context.get(HttpServletRequest.class);
            String baseURL = request.getScheme() + "://" + request.getServerName();
            if (request.getServerPort() != -1 && request.getServerPort() != 80 && request.getServerPort() != 443) {
                baseURL += ":" + Integer.toString(request.getServerPort());
            }

            List<String> pathToIgnore = new ArrayList<String>();
            photoLocalizationService.generateRSS(baseURL, pathToIgnore);
        }
    }
}
