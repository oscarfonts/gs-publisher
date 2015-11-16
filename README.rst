===============================
GeoServer Geodatabase Publisher
===============================

Publisher
=========

This utility automatically publishes some geodatabase tables as GeoServer layers. It needs:

GeoServer access parameters:

* GeoServer base URL, defaults to ``http://localhost:8080/geoserver``,
* GeoServer admin username, defaults to ``admin``,
* GeoServer admin password, defaults to ``geoserver``.

Mandatory parameters:

* The workspace name,
* The datastore name,
* Some table (layer) names.

And, if the datastore has to be created:

* The database connection parameters, provided as a properties file.

The workspace and datastore will be created if they don't exist.


Supported databases are:

* PostGIS
* Oracle Spatial (add propietary Oracle ojdbc7.jar driver to your classpath)
* H2

Usage
-----

::

./publish.sh --help

::

./publish.sh --workspace test --datastore tmb_api --db_params src/test/resources/h2.properties --layers LINIES_METRO,ESTACIONS,ACCESSOS


Updater
=======

Similar to the Publisher, but it updates an existing DataStore with a new set of connection parameters.

This time, the "db_params" parameter is mandatory, and the "layers" parameter doesn't exist.

Usage
-----

Suposing a "tmb_api" datastore exists under the "test" workspace, and the new database connection parameters are in a "new_h2_params" properties file::

./update.sh --workspace test --datastore tmb_api --db_params new_h2_params.properties
