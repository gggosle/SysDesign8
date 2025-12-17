-- =====================================================
-- PERFORMANCE TWEAKS (safe for test environments)
-- =====================================================
SET synchronous_commit = OFF;

-- =====================================================
-- 1. ACCOUNTS (100,000)
-- =====================================================
INSERT INTO accounts (
    user_id,
    account_number,
    account_type,
    balance,
    currency,
    is_locked,
    created_at
)
SELECT
    (random() * 50000)::int + 1,                             -- users reused
    'ACC-' || LPAD(gs::text, 10, '0'),                       -- unique
    (ARRAY['checking','savings','credit'])[1 + (random()*2)::int]::account_type_enum,
    (random() * 20000)::numeric(15,2),
    'USD',
    random() < 0.05,                                         -- ~5% locked
    NOW() - (random() * INTERVAL '2 years')
FROM generate_series(1, 100000) gs;

-- =====================================================
-- 2. TRANSACTIONS (1,000,000)
-- =====================================================
INSERT INTO transactions (
    from_account_id,
    to_account_id,
    amount,
    type,
    status,
    description,
    reference_number,
    created_at,
    completed_at
)
SELECT
    (random()*99999 + 1)::int,
    (random()*99999 + 1)::int,
    (random()*5000)::numeric(15,2),
    (ARRAY['transfer','deposit','withdrawal','payment','fee'])
        [1 + (random()*4)::int]::transaction_type_enum,
    (ARRAY['pending','completed','failed','reversed'])
        [1 + (random()*3)::int]::transaction_status_enum,
    'Auto-generated transaction',
    'REF-' || gs,
    ts,
    CASE
        WHEN random() < 0.8 THEN ts + (random() * INTERVAL '2 hours')
        ELSE NULL
        END
FROM (
         SELECT
             gs,
             NOW() - (random() * INTERVAL '1 year') AS ts
         FROM generate_series(1, 1000000) gs
     ) t;

-- =====================================================
-- 3. AUDIT LOG (5,000,000)
-- =====================================================
INSERT INTO audit_log (
    transaction_id,
    action,
    user_id,
    ip_address,
    details,
    timestamp
)
SELECT
    (random()*999999 + 1)::bigint,
    (ARRAY['CREATE','UPDATE','STATUS_CHANGE','ROLLBACK'])
        [1 + (random()*3)::int],
    (random()*50000)::int + 1,
    inet '192.168.0.0' + (random()*65535)::int,
    jsonb_build_object(
            'field', 'status',
            'old', 'pending',
            'new', 'completed'
    ),
    NOW() - (random() * INTERVAL '1 year')
FROM generate_series(1, 5000000);

-- =====================================================
-- 4. RECURRING PAYMENTS (10,000)
-- =====================================================
INSERT INTO recurring_payments (
    account_id,
    amount,
    recipient_account,
    frequency,
    next_payment_date,
    is_active
)
SELECT
    (random()*99999 + 1)::int,
    (random()*1000)::numeric(15,2),
    'ACC-' || LPAD((random()*99999 + 1)::int::text, 10, '0'),
    (ARRAY['daily','weekly','monthly','yearly'])
        [1 + (random()*3)::int]::frequency_enum,
    CURRENT_DATE + ((random()*60)::int),
    random() < 0.9
FROM generate_series(1, 10000);

-- =====================================================
-- UPDATE STATISTICS (IMPORTANT FOR QUERY PLANS)
-- =====================================================
ANALYZE accounts;
ANALYZE transactions;
ANALYZE audit_log;
ANALYZE recurring_payments;
