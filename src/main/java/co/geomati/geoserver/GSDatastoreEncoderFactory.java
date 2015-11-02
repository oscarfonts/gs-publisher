package co.geomati.geoserver;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;

import it.geosolutions.geoserver.rest.encoder.datastore.GSOracleNGDatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSPostGISDatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSAbstractDatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSH2DatastoreEncoder;

public class GSDatastoreEncoderFactory {

    public static GSAbstractDatastoreEncoder create(String namespace,
            String storename, Map<String, Object> parameters)
            throws IllegalAccessException, InvocationTargetException {
        String dbType = parameters.get("dbtype").toString();
        GSAbstractDatastoreEncoder store = null;
        switch (dbType) {
        case "postgis":
            GSPostGISDatastoreEncoder postgis = new GSPostGISDatastoreEncoder(
                    storename);
            postgis.setNamespace(namespace);
            store = postgis;
            break;
        case "oracle":
            String databasename = parameters.get("database").toString();
            GSOracleNGDatastoreEncoder oracle = new GSOracleNGDatastoreEncoder(
                    storename, databasename);
            oracle.setNamespace(namespace);
            store = oracle;
            break;
        case "h2":
            GSH2DatastoreEncoder h2 = new GSH2DatastoreEncoder(storename);
            h2.setNamespace(namespace);
            store = h2;
            break;
        default:
            System.err.println("Datastore type '" + dbType
                    + "' not implemented");
            break;
        }

        for (Entry<String, Object> param : parameters.entrySet()) {
            BeanUtils.setProperty(store, param.getKey(), param.getValue());
        }

        return store;
    }
}
