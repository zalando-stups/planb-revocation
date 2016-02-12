=========================
Plan B Revocation Service
=========================

.. image:: https://travis-ci.org/zalando/planb-revocation.svg?branch=master
    :target: https://travis-ci.org/zalando/planb-revocation

.. image:: https://codecov.io/github/zalando/planb-revocation/coverage.svg?branch=master
    :target: https://codecov.io/github/zalando/planb-revocation?branch=master

Revocation service for JWT tokens issued by the `Plan B OpenID Connect Provider`_.

(Planned) Features:

* Provide HTTP endpoint to revoke one or more JWT tokens
* Store revocation lists in Cassandra
* Provide HTTP endpoint to the `Plan B Agent`_ in order to periodically poll for revocation lists (deltas).

Building
========

.. code-block:: bash

    $ ./mvnw clean verify

Docker Image
============

.. code-block:: bash

    $ ./mvnw clean package
    $ sudo pip3 install scm-source
    $ scm-source
    $ docker build -t planb-revocation .

.. _Plan B OpenID Connect Provider: https://github.com/zalando/planb-provider
.. _Plan B Agent: https://github.com/zalando/planb-agent
