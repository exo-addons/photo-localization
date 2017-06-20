package org.exoplatform.addons.photolocalization;


import javax.imageio.ImageIO;
import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.*;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.cms.impl.ImageUtils;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Romain Dénarié (romain.denarie@exoplatform.com) on 28/02/17.
 */
@Path("/photoLocalization")
public class PhotoLocalizationService implements ResourceContainer {

    private static final Log LOG = ExoLogger.getLogger(PhotoLocalizationService.class.getName());
    private final String workspace;
    private String repository;

    RepositoryService repositoryService;
    SessionProviderService sessionProviderService;
    OrganizationService organizationService;
    IdentityManager identityManager;

    SimpleDateFormat outPutFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private String photoFolder = "";

    private final static String RSS_FOLDER = "rss";
    private final static String RSS_FILENAME = "geo.xml";
    private static final String IF_MODIFIED_SINCE_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";
    private static final String LAST_MODIFIED_PROPERTY = "Last-Modified";




    public PhotoLocalizationService(RepositoryService repositoryService, SessionProviderService sessionProviderService, OrganizationService organizationService, IdentityManager identityManager) {
        LOG.info("Start Service");
        this.repositoryService = repositoryService;
        this.sessionProviderService = sessionProviderService;
        this.organizationService = organizationService;
        this.identityManager=identityManager;
        this.photoFolder = System.getProperty("photo.localization.folder","/Users/r___/ro___/roo___/root/Public");
        if (!photoFolder.startsWith("/")) {
            photoFolder="/"+photoFolder;
        };
        this.workspace = "collaboration";
        this.repository="repository";

    }

    public String getPhotoFolder() {
        return photoFolder;
    }

    @GET
    @Path("generateRSS")
    public Response generateRSS(@Context UriInfo uriInfo, @Context SecurityContext sc) {
        //generate with webservice
        if (LOG.isDebugEnabled()) {
            LOG.debug("Start GeoRss generation");
        }
        StringBuffer rssContent = new StringBuffer();
        String now = outPutFormat.format(new Date());

        try {
            NodeIterator it = getImages();
            rssContent.append("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>\n" +
                                "<feed xmlns=\"http://www.w3.org/2005/Atom\"\n" +
                                "      xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n" +
                                "      xmlns:geo=\"http://www.w3.org/2003/01/geo/wgs84_pos#\"\n" +
                                "      xmlns:georss=\"http://www.georss.org/georss\"\n" +
                                "      xmlns:woe=\"http://where.yahooapis.com/v1/schema.rng\">\n" +
                                "\n" +
                                "  <title>eXo Photo Geolocalisation</title>\n" +
                                "  <subtitle>Geolocalisation of photos in eXo</subtitle>\n" +
                                "  <updated>"+now+"</updated>\n");

            while (it.hasNext()) {
                Node node = it.nextNode();
                URI baseUri = uriInfo.getBaseUri();
                String baseURL = baseUri.getScheme() + "://" + baseUri.getHost();
                if (baseUri.getPort()!=-1) {
                    baseURL+=":" + Integer.toString(baseUri.getPort());
                }
                generateEntry(node, rssContent, baseURL);
            }

            rssContent.append("</feed>");
            storeRss(rssContent);

        } catch (RepositoryException re) {
            LOG.error("Error during geoRss generation", re.getMessage());
            re.printStackTrace();
            return renderJSON("Generation Failed");
        }
        return renderJSON(rssContent.toString());

    }
    public void generateRSS(String baseURL, List<String> pathToIgnode) {
        //generate from listener
        if (LOG.isDebugEnabled()) {
            LOG.debug("Start GeoRss generation");
        }
        StringBuffer rssContent = new StringBuffer();
        String now = outPutFormat.format(new Date());

        try {
            NodeIterator it = getImages();
            rssContent.append("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>\n" +
                    "<feed xmlns=\"http://www.w3.org/2005/Atom\"\n" +
                    "      xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n" +
                    "      xmlns:geo=\"http://www.w3.org/2003/01/geo/wgs84_pos#\"\n" +
                    "      xmlns:georss=\"http://www.georss.org/georss\"\n" +
                    "      xmlns:woe=\"http://where.yahooapis.com/v1/schema.rng\">\n" +
                    "\n" +
                    "  <title>eXo Photo Geolocalisation</title>\n" +
                    "  <subtitle>Geolocalisation of photos in eXo</subtitle>\n" +
                    "  <updated>"+now+"</updated>\n");

            while (it.hasNext()) {
                Node node = it.nextNode();
                if (!pathToIgnode.contains(node.getParent().getPath())) {
                    generateEntry(node, rssContent, baseURL);
                }
            }

            rssContent.append("</feed>");
            storeRss(rssContent);

        } catch (RepositoryException re) {
            LOG.error("Error during geoRss generation", re.getMessage());
            re.printStackTrace();

        }

    }

    private void storeRss(StringBuffer rssContent) throws RepositoryException {
        ManageableRepository manageableRepository = repositoryService.getCurrentRepository();
        SessionProvider sessionProvider = sessionProviderService.getSystemSessionProvider(null);
        Session session = sessionProvider.getSession("collaboration", manageableRepository);

        Node photoFolder = (Node) session.getItem(this.photoFolder);
        Node rssFolder;
        if (!photoFolder.hasNode(RSS_FOLDER)) {
            rssFolder = photoFolder.addNode(RSS_FOLDER, "nt:folder");
            if (rssFolder.canAddMixin("exo:hiddenable")) {
                rssFolder.addMixin("exo:hiddenable");
            }
            if (rssFolder.canAddMixin("exo:privilegeable")) {
                rssFolder.addMixin("exo:privilegeable");
            }
            ((ExtendedNode)rssFolder).setPermission("any", PermissionType.ALL);
            photoFolder.save();
        } else {
            rssFolder = photoFolder.getNode(RSS_FOLDER);
        }
        Node resource;
        if (!rssFolder.hasNode(RSS_FILENAME)) {
            resource = rssFolder.addNode(RSS_FILENAME, "nt:file").addNode("jcr:content", "nt:resource"); // ,
        } else {
            resource = rssFolder.getNode(RSS_FILENAME).getNode("jcr:content");
        }
        resource.setProperty("jcr:data", session.getValueFactory().createValue(rssContent.toString(),PropertyType.BINARY));
        resource.setProperty("jcr:mimeType", "text/xml");
        resource.setProperty("jcr:lastModified", Calendar.getInstance());
        rssFolder.save();
    }

    private void generateEntry(Node node, StringBuffer rssContent, String baseURL) {
        try {


            Node imageNode = node.getParent();

            String userId = imageNode.getProperty("exo:owner").getString();
            String displayName = userId;
            User user = organizationService.getUserHandler().findUserByName(userId);
            if (user!=null) {
                displayName=user.getDisplayName();
            }


            String baseRestURL = baseURL+"/rest";
            String originalSizeLink = baseRestURL+"/thumbnailImage/origin/"+
                                            ((ManageableRepository)imageNode.getSession().getRepository()).getConfiguration().getName()+"/"+
                                            imageNode.getSession().getWorkspace().getName()+imageNode.getPath();
            String thumbnailSizeLink = baseRestURL+"/photoLocalization/getThumbnail/"+
                    ((ManageableRepository)imageNode.getSession().getRepository()).getConfiguration().getName()+"/"+
                    imageNode.getSession().getWorkspace().getName()+imageNode.getPath();

            Identity identity = identityManager.getIdentity("organization",userId,true);
            String profileLink = "#";
            if (identity!=null) {
                profileLink=baseURL+identity.getProfile().getUrl();
            }

            GeoLocation geoLocation = getGeolocation(node);

            if (geoLocation!=null) {

                rssContent.append("  <entry>\n");
                rssContent.append("    <title>" + displayName + "</title>\n");
                rssContent.append("    <link rel=\"alternate\" type=\"text/html\" href=\""+profileLink+"\"/>\n");

                rssContent.append("    <published>" + imageNode.getProperty("exo:dateCreated").getString() + "</published>\n");
                rssContent.append("    <updated>" + imageNode.getProperty("exo:dateModified").getString() + "</updated>\n");
                rssContent.append("    <content type=\"html\">");

                rssContent.append("        &lt;p&gt;&lt;a href=&quot;" + profileLink + "&quot;&gt;" + displayName + "&lt;/a&gt; posted a photo:&lt;/p&gt;\n" +
                        "\n" +
                        "            &lt;p&gt;&lt;a href=&quot;" + originalSizeLink + "&quot; " +
                        "&gt;&lt;img src=&quot;" + thumbnailSizeLink + "&quot; width=&quot;240&quot; height=&quot;160&quot; /&gt;&lt;/a&gt;&lt;/p&gt;\n" +
                        "\n" +
                        "            &lt;/p&gt;</content>\n");

                rssContent.append("    <author>\n");
                rssContent.append("      <name>" + displayName + "</name>\n");
                rssContent.append("      <uri>" + profileLink + "</uri>\n");
                rssContent.append("    </author>\n");
                rssContent.append("    <link rel=\"enclosure\" type=\"image/jpeg\" href=\"" + thumbnailSizeLink + "\"" +
                        " />\n");

                rssContent.append("    <georss:point>"+geoLocation.getLatitude()+" "+geoLocation.getLongitude()+"</georss:point>\n");
                rssContent.append("    <geo:lat>"+geoLocation.getLatitude()+"</geo:lat>\n");
                rssContent.append("    <geo:long>"+geoLocation.getLongitude()+"</geo:long>\n");
                rssContent.append("  </entry>\n");
            }


        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private GeoLocation getGeolocation(Node node) throws RepositoryException, ImageProcessingException, IOException {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(new BufferedInputStream(node.getProperty("jcr:data").getStream()), false);


            if (metadata.containsDirectory(GpsDirectory.class)) {
                // obtain the GPS directory
                GpsDirectory directory = metadata.getDirectory(GpsDirectory.class);
                // query the tag's value
                return directory.getGeoLocation();
            }
        } catch (ImageProcessingException e) {
            LOG.warn("File Format not supported");
        }
        return null;
    }

    private NodeIterator getImages() throws RepositoryException {
        ManageableRepository manageableRepository = repositoryService.getCurrentRepository();
        SessionProvider sessionProvider = sessionProviderService.getSystemSessionProvider(null);
        Session session = sessionProvider.getSession("collaboration", manageableRepository);
        QueryManager manager = session.getWorkspace().getQueryManager();


        StringBuffer statement = new StringBuffer();
        statement.append("SELECT * FROM nt:resource WHERE " +
                "(jcr:path LIKE '"+photoFolder+"/%') " +
                "and (jcr:mimeType='image/jpeg' or jcr:mimeType='image/gif' or jcr:mimeType='image/png')");


        Query query = manager.createQuery(statement.toString(), Query.SQL);
        return query.execute().getNodes();
    }

    private Response renderJSON(Object result) {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setNoStore(true);

        return Response.ok(result, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
    }


    @Path("/getThumbnail/{repoName}/{workspaceName}/{nodePath:.*}/")
    @GET
    public Response getThumbnail(@PathParam("repoName") String repoName,
                                  @PathParam("workspaceName") String workspaceName,
                                  @PathParam("nodePath") String nodePath,
                                  @HeaderParam("If-Modified-Since") String ifModifiedSince) throws Exception {

        String path = nodePath;
        if (!path.startsWith("/")) {
            path="/"+path;
        }
        if (!path.startsWith(photoFolder)) {
            return Response.status(HTTPStatus.NOT_FOUND).build();
        }

        Node showingNode = getShowingNode(workspaceName, nodePath);
        DateFormat dateFormat = new SimpleDateFormat(IF_MODIFIED_SINCE_DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        if(showingNode.isNodeType("nt:file")) {
            Node content = showingNode.getNode("jcr:content");
            String mimeType = content.getProperty("jcr:mimeType").getString();

            if(ifModifiedSince != null) {
                // get last-modified-since from header
                Date ifModifiedSinceDate = dateFormat.parse(ifModifiedSince);

                // get last modified date of node
                Date lastModifiedDate = content.getProperty("jcr:lastModified").getDate().getTime();

                // Check if cached resource has not been modifed, return 304 code
                if (ifModifiedSinceDate.getTime() >= lastModifiedDate.getTime()) {
                    return Response.notModified().build();
                }
            }
            BufferedImage image = ImageIO.read(content.getProperty("jcr:data").getStream());
            InputStream thumbnailStream = ImageUtils.scaleImage(image, 240, 160, false);
            return Response.ok(thumbnailStream, "image").header(LAST_MODIFIED_PROPERTY,
                    dateFormat.format(new Date())).build();
        } else {
            return Response.serverError().build();
        }

    }

    private Node getShowingNode(String workspaceName, String nodePath) throws Exception {
        ManageableRepository repository = repositoryService.getCurrentRepository();
        Session session = sessionProviderService.getSystemSessionProvider(null).getSession(workspaceName, repository);
        Node showingNode = null;
        if(nodePath.equals("/")) showingNode = session.getRootNode();
        else {
            showingNode = (Node) session.getItem("/"+nodePath);
        }
        return showingNode;
    }

    public String getRssUrl() {
        return "/rest/jcr/"+repository+"/"+workspace+photoFolder+"/"+RSS_FOLDER+"/"+RSS_FILENAME;
    }

    public String getAPIKey() {
        return System.getProperty("maps.api.key","");

    }
}
