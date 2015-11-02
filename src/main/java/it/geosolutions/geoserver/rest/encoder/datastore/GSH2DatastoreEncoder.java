package it.geosolutions.geoserver.rest.encoder.datastore;

/**
 * Encoder for a {@value #TYPE} datastore.
 * 
 * @author Oscar Fonts
 */
public class GSH2DatastoreEncoder extends GSAbstractDatastoreEncoder {

    static final String TYPE = "H2";

    static final int DEFAULT_MIN_CONNECTIONS = 1;
    static final int DEFAULT_MAX_CONNECTIONS = 10;
    static final int DEFAULT_FETCH_SIZE = 1000;
    static final int DEFAULT_CONNECTION_TIMEOUT = 20;
    static final int DEFAULT_MAX_CONNECTION_IDLE_TIME = 300;
    static final int DEFAULT_EVICTOR_TEST_PER_RUN = 3;
    static final int DEFAULT_EVICTOR_RUN_PERIODICITY = 300;
    static final boolean DEFAULT_TEST_WHILE_IDLE = true;
    static final boolean DEFAULT_EXPOSE_PRIMARY_KEYS = false;
    static final boolean DEFAULT_ASSOCIATIONS = false;

    public GSH2DatastoreEncoder(String name) {
        super(name);

        // Set mandatory parameter
        setType(TYPE);
        setDatabaseType("h2");

        setMinConnections(DEFAULT_MIN_CONNECTIONS);
        setMaxConnections(DEFAULT_MAX_CONNECTIONS);
        setFetchSize(DEFAULT_FETCH_SIZE);
        setConnectionTimeout(DEFAULT_CONNECTION_TIMEOUT);
        setMaxConnectionIdleTime(DEFAULT_MAX_CONNECTION_IDLE_TIME);
        setEvictorTestsPerRun(DEFAULT_EVICTOR_TEST_PER_RUN);
        setEvictorRunPeriodicity(DEFAULT_EVICTOR_RUN_PERIODICITY);
        setTestWhileIdle(DEFAULT_TEST_WHILE_IDLE);
        setExposePrimaryKeys(DEFAULT_EXPOSE_PRIMARY_KEYS);
        setAssociations(DEFAULT_ASSOCIATIONS);
    }

    protected void setDatabaseType(String dbtype) {
        connectionParameters.set("dbtype", dbtype);
    }

    public void setNamespace(String namespace) {
        connectionParameters.set("namespace", namespace);
    }

    public void setDatabase(String database) {
        connectionParameters.set("database", database);
    }

    public void setSchema(String schema) {
        connectionParameters.set("schema", schema);
    }

    public void setHost(String host) {
        connectionParameters.set("host", host);
    }

    public void setPort(int port) {
        connectionParameters.set("port", Integer.toString(port));
    }

    public void setUser(String user) {
        connectionParameters.set("user", user);
    }

    public void setPasswd(String passwd) {
        connectionParameters.set("passwd", passwd);
    }

    public void setMinConnections(int minConnections) {
        connectionParameters.set("min connections",
                Integer.toString(minConnections));
    }

    public void setMaxConnections(int maxConnections) {
        connectionParameters.set("max connections",
                Integer.toString(maxConnections));
    }

    public void setFetchSize(int fetchSize) {
        connectionParameters.set("fetch size", Integer.toString(fetchSize));
    }

    public void setConnectionTimeout(int seconds) {
        connectionParameters.set("Connection timeout",
                Integer.toString(seconds));
    }

    public void setMaxConnectionIdleTime(int maxConnectionIdleTime) {
        connectionParameters.set("Max connection idle time",
                Integer.toString(maxConnectionIdleTime));
    }

    public void setEvictorTestsPerRun(int evictorTestsPerRun) {
        connectionParameters.set("Evictor tests per run",
                Integer.toString(evictorTestsPerRun));
    }

    public void setEvictorRunPeriodicity(int evictorRunPeriodicity) {
        connectionParameters.set("Evictor run periodicity",
                Integer.toString(evictorRunPeriodicity));
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        connectionParameters.set("Test while idle",
                Boolean.toString(testWhileIdle));
    }

    public void setExposePrimaryKeys(boolean exposePrimaryKeys) {
        connectionParameters.set("Expose primary keys",
                Boolean.toString(exposePrimaryKeys));
    }

    public void setAssociations(boolean associations) {
        connectionParameters
                .set("Associations", Boolean.toString(associations));
    }

    protected String getValidType() {
        return TYPE;
    }

}
