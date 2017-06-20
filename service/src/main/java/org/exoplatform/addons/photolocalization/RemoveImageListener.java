package org.exoplatform.addons.photolocalization;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.Node;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Romain Dénarié (romain.denarie@exoplatform.com) on 02/03/17.
 */
public class RemoveImageListener extends Listener<Object, Node> {

    PhotoLocalizationService photoLocalizationService;
    private static final Log LOG = ExoLogger.getLogger(RemoveImageListener.class.getName());


    public RemoveImageListener(PhotoLocalizationService photoLocalizationService) {
        this.photoLocalizationService=photoLocalizationService;
    }

    @Override
    public void onEvent(Event<Object, Node> event) throws Exception {



        if (event.getData().getPath().startsWith(photoLocalizationService.getPhotoFolder())) {

            LOG.info("Listener launched");
            PortalRequestContext context = Util.getPortalRequestContext();

            HttpServletRequest request = context.getRequest();
            String baseURL = request.getScheme() + "://" + request.getServerName();
            if (request.getServerPort()!=-1 && request.getServerPort()!=80 && request.getServerPort()!=443) {
                baseURL+=":" + Integer.toString(request.getServerPort());
            }


            //in case of node remove, the listener is launched BEFORE the session.save. So we need to ignore the removed node
            List<String> pathToIgnore = new ArrayList<String>();
            pathToIgnore.add(event.getData().getPath());
            photoLocalizationService.generateRSS(baseURL, pathToIgnore);
        }
    }
}
