===============================
GeoServer Geodatabase Publisher
===============================

This utility automatically publishes some geodatabase tables as GeoServer layers. It needs:

GeoServer access parameters:

* GeoServer base URL, defaults to ``http://localhost:8080/geoserver`,
* GeoServer admin username, defaults to ``admin``,
* GeoServer admin password, defaults to ``geoserver``.

Mandatory parameters:

* The workspace name,
* The datastore name,
* Some table names.

And, if the datastore has to be created:

* The database connection parameters

The workspace and datastore are created if they don't already exist.


Supported databases are:

* PostGIS
* Oracle Spatial (add ojdbc7.jar driver from Oracle to classpath)
* H2

Usage
=====

::

./run.sh --help

::

./run.sh --workspace test --datastore tmb_api --db_params src/test/resources/h2.properties --layers LINIES_METRO,ESTACIONS,ACCESSOS
