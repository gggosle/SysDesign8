-- V1__create_banking_schema.sql

-- Create custom enum types
CREATE TYPE account_type_enum AS ENUM ('checking', 'savings', 'credit');
CREATE TYPE transaction_type_enum AS ENUM ('transfer', 'deposit', 'withdrawal', 'payment', 'fee');
CREATE TYPE transaction_status_enum AS ENUM ('pending', 'completed', 'failed', 'reversed');
CREATE TYPE frequency_enum AS ENUM ('daily', 'weekly', 'monthly', 'yearly');

-- Create tables
CREATE TABLE accounts (
                                        id SERIAL PRIMARY KEY,
                                        user_id INTEGER NOT NULL,
                                        account_number VARCHAR(20) UNIQUE NOT NULL,
                                        account_type account_type_enum,
                                        balance NUMERIC(15,2) NOT NULL DEFAULT 0,
                                        currency CHAR(3) DEFAULT 'USD',
                                        is_locked BOOLEAN DEFAULT FALSE,
                                        created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
                                            id BIGSERIAL PRIMARY KEY,
                                            from_account_id INTEGER REFERENCES accounts(id),
                                            to_account_id INTEGER REFERENCES accounts(id),
                                            amount NUMERIC(15,2) NOT NULL,
                                            type transaction_type_enum,
                                            status transaction_status_enum,
                                            description TEXT,
                                            reference_number VARCHAR(50) UNIQUE,
                                            created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                            completed_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE audit_log (
                                         id BIGSERIAL PRIMARY KEY,
                                         transaction_id BIGINT REFERENCES transactions(id),
                                         action VARCHAR(50),
                                         user_id INTEGER,
                                         ip_address INET,
                                         details JSONB,
                                         timestamp TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE recurring_payments (
                                                  id SERIAL PRIMARY KEY,
                                                  account_id INTEGER REFERENCES accounts(id),
                                                  amount NUMERIC(15,2) NOT NULL,
                                                  recipient_account VARCHAR(50),
                                                  frequency frequency_enum,
                                                  next_payment_date DATE,
                                                  is_active BOOLEAN DEFAULT TRUE
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_user ON accounts (user_id);
CREATE INDEX IF NOT EXISTS idx_account_number ON accounts (account_number);

CREATE INDEX IF NOT EXISTS idx_from_account ON transactions (from_account_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_to_account ON transactions (to_account_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_reference ON transactions (reference_number);
CREATE INDEX IF NOT EXISTS idx_status ON transactions (status) WHERE status = 'pending';

CREATE INDEX IF NOT EXISTS idx_transaction ON audit_log (transaction_id);
CREATE INDEX IF NOT EXISTS idx_timestamp ON audit_log (timestamp);

CREATE INDEX IF NOT EXISTS idx_next_payment ON recurring_payments (next_payment_date) WHERE is_active = TRUE;
