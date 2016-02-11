=========================
Plan B Revocation Service
=========================

Building:

.. code-block:: bash

    $ ./mvnw clean install

Docker Image
============

.. code-block:: bash

    $ ./mvnw clean package
    $ sudo pip3 install scm-source
    $ scm-source
    $ docker build -t docker build -t registry.opensource.zalan.do/stups/planb-revocation:1.0-SNAPSHOT .