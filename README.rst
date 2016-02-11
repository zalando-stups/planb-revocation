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
    $ docker build -t planb-revocation .