=========================
Plan B Revocation Service
=========================

.. image:: https://travis-ci.org/zalando/planb-revocation.svg?branch=master
    :target: https://travis-ci.org/zalando/planb-revocation

.. image:: https://codecov.io/github/zalando/planb-revocation/coverage.svg?branch=master
    :target: https://codecov.io/github/zalando/planb-revocation?branch=master

Building:

.. code-block:: bash

    $ ./mvnw verify

Docker Image
============

.. code-block:: bash

    $ ./mvnw clean package
    $ sudo pip3 install scm-source
    $ scm-source
    $ docker build -t planb-revocation .
