=========================
Plan B Revocation Service
=========================

.. image:: https://travis-ci.org/zalando/planb-revocation.svg?branch=master
    :target: https://travis-ci.org/zalando/planb-revocation

.. image:: https://codecov.io/github/zalando/planb-revocation/coverage.svg?branch=master
    :target: https://codecov.io/github/zalando/planb-revocation?branch=master

.. image:: https://readthedocs.org/projects/planb/badge/?version=latest
   :target: https://readthedocs.org/projects/planb/?badge=latest
   :alt: Documentation Status

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


Setting up Local Dev Environment
================================

Run a development Cassandra node:

.. code-block:: bash

    $ docker run --name dev-cassandra -d -p 9042:9042 cassandra:2.1

Insert schema (you might need to wait a few seconds for Cassandra to boot):

.. code-block:: bash

    $ docker run -i --link dev-cassandra:cassandra --rm cassandra:2.1 cqlsh cassandra < revocation_schema.cql

General cqlsh access to your dev instance:

.. code-block:: bash

    $ docker run -it --link dev-cassandra:cassandra --rm cassandra:2.1 cqlsh cassandra
      cqlsh> DESCRIBE TABLE revocation.revocation; -- run some example query

Set up the following environment variables:

.. code-block:: bash

    $ export TOKENINFO_URL=https://example.com/oauth2/tokeninfo  # required for REST API

Run the application against your local Cassandra:

.. code-block:: bash

    $ java -jar target/planb-revocation-1.0-SNAPSHOT.jar --cassandra.contactPoints="127.0.0.1"

Testing the Endpoints
=====================

Revoking tokens by "sub" claim:

.. code-block:: bash

    $ tok=... # some valid token accepted by the configured TOKENINFO_URL
    $ curl -X POST \
         -H "Authorization: Bearer $tok" \
         -H 'Content-Type: application/json' \
         -d '{"type": "CLAIM", "data": {"name": "sub", "value_hash": ""}}' \
         "http://localhost:8080/revocations"

.. _Plan B OpenID Connect Provider: https://github.com/zalando/planb-provider
.. _Plan B Agent: https://github.com/zalando/planb-agent
