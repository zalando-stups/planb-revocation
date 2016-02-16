CREATE SCHEMA revocation
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};

CREATE TABLE revocation.revocation (
    bucket_date TEXT,
    bucket_interval INT,    -- 0: 0-8 1: 8-16 2:16-24
    revocation_type TEXT,
    revocation_data TEXT,
    revoked_at TIMESTAMP,
    revoked_by TEXT,
    PRIMARY KEY ((bucket_date, bucket_interval), revoked_at)
)
WITH CLUSTERING ORDER BY (revoked_at DESC);

CREATE TABLE revocation.force_refresh (
    forced_year int, -- only bucket we need
    forced_ts timestamp,
    forced_at timestamp,
    forced_by TEXT,
    PRIMARY KEY(forced_year, forced_at)
)
WITH CLUSTERING ORDER BY (forced_at DESC);
