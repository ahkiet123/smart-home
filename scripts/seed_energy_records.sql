-- Seed sample data for daily energy analytics
-- Prerequisite: table devices has data.
-- This script inserts energy records for the latest 30 days and skips duplicates.

WITH date_range AS (
    SELECT generate_series(current_date - interval '29 day', current_date, interval '1 day')::date AS usage_date
),
base_devices AS (
    SELECT
        d.id AS device_id,
        COALESCE(d.power_rating, 120.0) AS power_rating
    FROM devices d
),
synthetic_records AS (
    SELECT
        bd.device_id,
        dr.usage_date,
        round(
            greatest(
                0.15,
                (bd.power_rating / 1000.0) * (0.8 + random() * 2.8)
            )::numeric,
            3
        ) AS energy_consumed
    FROM base_devices bd
    CROSS JOIN date_range dr
    WHERE random() > 0.12
)
INSERT INTO energy_records (device_id, usage_date, energy_consumed, created_at)
SELECT
    sr.device_id,
    sr.usage_date,
    sr.energy_consumed,
    now()
FROM synthetic_records sr
WHERE NOT EXISTS (
    SELECT 1
    FROM energy_records er
    WHERE er.device_id = sr.device_id
      AND er.usage_date = sr.usage_date
);

-- Quick checks after running:
-- 1) Total rows
-- SELECT count(*) FROM energy_records;
-- 2) Last 7 days aggregate
-- SELECT usage_date, round(sum(energy_consumed)::numeric, 3) AS total_kwh
-- FROM energy_records
-- WHERE usage_date BETWEEN current_date - 6 AND current_date
-- GROUP BY usage_date
-- ORDER BY usage_date;
