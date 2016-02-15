-- suggest to use 31 days TTL on insert
CREATE TABLE revocation.revocation (
    bucket_date TEXT,
    bucket_interval varchar(1),    -- 0: 0-8 1: 8-16 2:16-24
    revocation_type TEXT,
    revocation_data TEXT,
    revoked_at TIMESTAMP,
    revoked_by TEXT,
    PRIMARY KEY ((bucket_date, bucket_interval), revoked_at)
);
WITH CLUSTERING ORDER BY (revoked_at DESC);

-- force refresh if forced_at is greater than last forced at
CREATE TABLE revocation.force_refresh (
    forced_year int, -- only bucket we need
    forced_ts timestamp,
    forced_at timestamp,
    forced_by TEXT,
    PRIMARY KEY(forced_year, forced_at)
) WITH CLUSTERING ORDER BY (forced_at DESC);
