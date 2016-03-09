CREATE SCHEMA revocation
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};

CREATE TABLE revocation.revocation (
    bucket_date TEXT,
    bucket_interval BIGINT,    -- 0: 0-8 1: 8-16 2:16-24
    revocation_type TEXT,
    revocation_data TEXT,
    revoked_at BIGINT,
    revoked_by TEXT,
    PRIMARY KEY ((bucket_date, bucket_interval), revoked_at)
)
WITH CLUSTERING ORDER BY (revoked_at DESC);

CREATE TABLE revocation.refresh (
    refresh_year INT,    -- only bucket we need
    refresh_ts BIGINT,   -- seconds since epoch
    refresh_from BIGINT, -- from when to refresh revocations. Seconds since epoch
    created_by TEXT,     -- name of the user who created this entry
    PRIMARY KEY(refresh_year, refresh_ts)
)
WITH CLUSTERING ORDER BY (refresh_ts DESC);
