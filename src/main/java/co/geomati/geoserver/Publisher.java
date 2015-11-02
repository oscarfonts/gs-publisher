package co.geomati.geoserver;

import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSH2DatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtils;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Publishes data to a GeoServer instance.
 * 
 * It can be run from command line or instantiated as a class.
 * 
 * @author Oscar Fonts <oscar.fonts@geomati.co>
 */
public class Publisher {

    public static void main(String[] args) throws IOException,
            IllegalAccessException, InvocationTargetException {
        // OptionParser helps define and parse the command line interface
        OptionParser parser = new OptionParser() {
            {
                accepts("help", "Print this help note").forHelp();
                accepts("url", "GeoServer base URL").withOptionalArg()
                        .defaultsTo("http://localhost:8080/geoserver");
                accepts("user", "Username").withOptionalArg().defaultsTo(
                        "admin");
                accepts("password", "Password").withOptionalArg().defaultsTo(
                        "geoserver");
                accepts("workspace", "Workspace").withOptionalArg().defaultsTo(
                        "test");
            }
        };

        // Read the CLI arguments
        OptionSet options = null;
        try {
            options = parser.parse(args);
        } catch (OptionException e) {
            System.err.println("Error pasrsing arguments: " + e.getMessage());
            System.out.println("\nUsage\n=====\n");
            parser.printHelpOn(System.out);
            System.exit(2);
        }

        String url = (String) options.valueOf("url");
        String user = (String) options.valueOf("user");
        String password = (String) options.valueOf("password");

        Publisher publisher = new Publisher(url, user, password);

        // Some param values
        String WORKSPACE = "test";
        String STORENAME = "hachedos";
        String LAYERNAME = "ACCESSOS";

        Map<String, Object> DB = new HashMap<String, Object>();
        DB.put("database", "H2/H2_API");
        DB.put("host", "h2");
        DB.put("port", 1521);

        // Create a workspace
        if (publisher.createWorkspace(WORKSPACE)) {
            System.out.println("Workspace created");
        } else {
            System.err.println("Workspace couldn't be created");
        }

        // Create a datastore
        if (publisher.createH2DataStore(WORKSPACE, STORENAME, DB)) {
            System.out.println("Datastore created");
        } else {
            System.err.println("Datastore couldn't be created");
        }

        // Publish a layer
        if (publisher.publishLayer(WORKSPACE, STORENAME, LAYERNAME)) {
            System.out.println("Layer published");
        } else {
            System.err.println("Layer couldn't be published");
        }

    }

    GeoServerRESTManager manager = null;

    public Publisher(String url, String user, String password)
            throws MalformedURLException {
        manager = new GeoServerRESTManager(new URL(url), user, password);
    }

    public boolean createWorkspace(String name) {
        return manager.getPublisher().createWorkspace(name);
    }

    public boolean createH2DataStore(String workspace, String storename,
            Map<String, Object> connectionParams)
            throws IllegalAccessException, InvocationTargetException {

        GSH2DatastoreEncoder store = new GSH2DatastoreEncoder(storename);
        store.setNamespace(manager.getReader().getNamespace(workspace).getURI()
                .toString());
        for (Entry<String, Object> param : connectionParams.entrySet()) {
            BeanUtils.setProperty(store, param.getKey(), param.getValue());
        }
        return manager.getStoreManager().create(workspace, store);
    }

    public boolean publishLayer(String workspace, String storename,
            String layername) {
        GSFeatureTypeEncoder fte = new GSFeatureTypeEncoder();
        fte.setName(layername);
        fte.setTitle(layername);
        // fte.setSRS(srs); // srs=null?"EPSG:4326":srs);
        // fte.setProjectionPolicy(ProjectionPolicy.REPROJECT_TO_DECLARED);

        GSLayerEncoder layerEncoder = new GSLayerEncoder();
        return manager.getPublisher().publishDBLayer(workspace, storename, fte,
                layerEncoder);
    }

    /**
     * Given a properties file, returns a Map with the property collection
     * 
     * @param file
     *            The file to read
     * @return A key-value pair Map
     */
    protected static Map<String, Object> readProperties(File file) {
        Map<String, Object> map = new HashMap<String, Object>();
        InputStream input = null;
        try {
            input = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(input);
            for (final String name : properties.stringPropertyNames()) {
                map.put(name, properties.getProperty(name));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

}
