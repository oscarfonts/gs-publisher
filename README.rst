===============================
GeoServer Geodatabase Publisher
===============================

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
=====

::

./run.sh --help

::

./run.sh --workspace test --datastore tmb_api --db_params src/test/resources/h2.properties --layers LINIES_METRO,ESTACIONS,ACCESSOS
