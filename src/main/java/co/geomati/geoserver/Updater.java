package co.geomati.geoserver;

import it.geosolutions.geoserver.rest.encoder.datastore.GSAbstractDatastoreEncoder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Map;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class Updater extends Publisher {

    public Updater(String url, String user, String password)
            throws MalformedURLException {
        super(url, user, password);
    }

    public boolean updateDatastore(String workspace, String datastore,
            Map<String, Object> database) throws IllegalAccessException,
            InvocationTargetException {
        if (!existsWorkspace(workspace)) {
            System.err.println("Workspace " + workspace + " doesn't exist.");
            return false;
        }
        if (!existsDatastore(workspace, datastore)) {
            System.err.println("Datastore " + workspace + " doesn't exist.");
            return false;
        }

        if (database == null) {
            System.err.println("Please provide the new database parameters.");
            return false;
        }
        String namespace = manager.getReader().getNamespace(workspace).getURI()
                .toString();
        GSAbstractDatastoreEncoder store = GSDatastoreEncoderFactory.create(
                namespace, datastore, database);
        store.setEnabled(true);
        return manager.getStoreManager().update(workspace, store);
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

        if (!options.has("workspace") || !options.has("datastore")
                || !options.has("db_params")) {
            printHelp(
                    "Mandatory parameters missing: provide at least a workspace name, a datastore name, and a new set of connection parameters.",
                    parser, 3);
        }

        String url = (String) options.valueOf("url");
        String user = (String) options.valueOf("user");
        String password = (String) options.valueOf("password");
        String workspace = (String) options.valueOf("workspace");
        String datastore = (String) options.valueOf("datastore");
        File db_params = (File) options.valueOf("db_params");

        Updater updater = new Updater(url, user, password);

        if (!updater.existsGeoserver()) {
            printHelp("GeoServer instance at " + url + " is not accessible.",
                    parser, 4);
        }

        // Update datastore connection parameters
        Map<String, Object> database = null;
        if (db_params != null && db_params.isFile()) {
            database = readProperties(db_params);
        }
        if (updater.updateDatastore(workspace, datastore, database)) {
            System.out.println("Datastore '" + datastore + "' updated");
        } else {
            System.err.println("Error updating '" + datastore + "' datastore");
        }
    }
}
