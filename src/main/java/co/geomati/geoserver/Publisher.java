package co.geomati.geoserver;

import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSAbstractDatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

    GeoServerRESTManager manager = null;

    public Publisher(String url, String user, String password)
            throws MalformedURLException {
        manager = new GeoServerRESTManager(new URL(url), user, password);
    }

    public boolean existsGeoserver() {
        return manager.getReader().existGeoserver();
    }

    public boolean existsWorkspace(String workspace) {
        return manager.getReader().getWorkspaceNames().contains(workspace);
    }

    public boolean createWorkspace(String workspace) {
        if (!existsWorkspace(workspace)) {
            return manager.getPublisher().createWorkspace(workspace);
        } else {
            System.out.println("Skipping '" + workspace
                    + "' workspace creation, it already exists");
            return true;
        }
    }

    public boolean existsDatastore(String workspace, String datastore) {
        return (manager.getReader().getDatastore(workspace, datastore) != null);
    }

    public boolean createDatastore(String workspace, String datastore,
            Map<String, Object> database) throws IllegalAccessException,
            InvocationTargetException {
        if (!existsWorkspace(workspace)) {
            System.err.println("Workspace " + workspace
                    + " doesn't exist, please create it before proceeding.");
            return false;
        }
        if (!existsDatastore(workspace, datastore)) {
            if (database == null) {
                System.err
                        .println("Datastore "
                                + datastore
                                + " doesn't exist, please provide the database parameters to create it.");
                return false;
            }
            String namespace = manager.getReader().getNamespace(workspace)
                    .getURI().toString();
            GSAbstractDatastoreEncoder store = GSDatastoreEncoderFactory
                    .create(namespace, datastore, database);
            return manager.getStoreManager().create(workspace, store);
        } else {
            System.out.println("Skipping '" + datastore
                    + "' datastore creation, it already exists");
            return true;
        }
    }

    public boolean existsLayer(String workspace, String layer) {
        return (manager.getReader().getLayer(workspace, layer) != null);
    }

    public boolean publishLayers(String workspace, String datastore,
            List<String> layers) {
        boolean sinFallo = true;

        for (String layer : layers) {
            sinFallo = sinFallo && publishLayer(workspace, datastore, layer);
        }
        return sinFallo;
    }

    public boolean publishLayer(String workspace, String datastore, String layer) {
        if (!existsLayer(workspace, layer)) {
            GSFeatureTypeEncoder featuretype = new GSFeatureTypeEncoder();
            featuretype.setName(layer);
            featuretype.setTitle(layer);

            return manager.getPublisher().publishDBLayer(workspace, datastore,
                    featuretype, new GSLayerEncoder());
        } else {
            System.out.println("Skipping '" + layer
                    + "' layer creation, it already exists");
            return true;
        }

    }

    public static void main(String[] args) throws IOException,
            IllegalAccessException, InvocationTargetException {
        // OptionParser helps define and parse the command line interface
        OptionParser parser = new OptionParser() {
            {
                accepts("help", "Print this help note").forHelp();
                accepts("url", "GeoServer base URL").withRequiredArg()
                        .defaultsTo("http://localhost:8080/geoserver");
                accepts("user", "GeoServer Administrator Username")
                        .withRequiredArg().defaultsTo("admin");
                accepts("password", "GeoServer Administrator Password")
                        .withRequiredArg().defaultsTo("geoserver");
                accepts("workspace",
                        "The workspace where data will be published")
                        .withRequiredArg();
                accepts("datastore", "Datastore name").withRequiredArg();
                accepts("layers",
                        "Comma separated list of layers to be published")
                        .withRequiredArg();
                accepts("db_params",
                        "Properties file with database connection parameters")
                        .withRequiredArg().describedAs("properties")
                        .ofType(File.class);
            }
        };

        // Read the CLI arguments
        OptionSet options = null;
        try {
            options = parser.parse(args);
            if (options.has("help")) {
                printHelp("", parser, 1);
            }
        } catch (OptionException e) {
            printHelp("Error pasrsing arguments: " + e.getMessage(), parser, 2);
        }

        if (!options.has("workspace") || !options.has("layers")
                || !options.has("datastore")) {
            printHelp(
                    "Mandatory parameters missing: provide at least a workspace name, a datastore name, and a list of layers.",
                    parser, 3);
        }

        String url = (String) options.valueOf("url");
        String user = (String) options.valueOf("user");
        String password = (String) options.valueOf("password");
        String workspace = (String) options.valueOf("workspace");
        String datastore = (String) options.valueOf("datastore");
        File db_params = (File) options.valueOf("db_params");
        String layers = (String) options.valueOf("layers");

        Publisher publisher = new Publisher(url, user, password);

        if (!publisher.existsGeoserver()) {
            printHelp("GeoServer instance at " + url + " is not accessible.",
                    parser, 4);
        }

        // Create a workspace if not exists
        if (publisher.createWorkspace(workspace)) {
            System.out.println("Workspace '" + workspace + "' created");
        } else {
            System.err.println("Error creating '" + workspace + "' workspace");
        }

        // Create a datastore if not exists
        Map<String, Object> database = null;
        if (db_params != null && db_params.isFile()) {
            database = readProperties(db_params);
        }
        if (publisher.createDatastore(workspace, datastore, database)) {
            System.out.println("Datastore '" + datastore + "' created");
            // Publish the given layers
            List<String> layerList = null;
            if (layers != null) {
                layerList = new ArrayList<String>(Arrays.asList(layers
                        .split(",")));
            }

            if (publisher.publishLayers(workspace, datastore, layerList)) {
                System.out.println("All layers published successfully");
            } else {
                System.err.println("Some layers couldn't be published");
            }
        } else {
            System.err.println("Error creating '" + datastore + "' datastore");
        }

    }

    static void printHelp(String message, OptionParser parser, int errorLevel)
            throws IOException {
        System.err.println("\n" + message);
        System.out.println("\nUsage\n=====\n");
        parser.printHelpOn(System.out);
        System.exit(errorLevel);
    }

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
