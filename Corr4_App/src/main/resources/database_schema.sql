-- CORR4 Bank Application Database Schema
-- Execute this in Supabase SQL Editor to initialize tables

-- 1. Create registrations table (user accounts)
CREATE TABLE IF NOT EXISTS registrations (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    dob DATE,
    status VARCHAR(50) DEFAULT 'active',
    received_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP
);

-- 2. Create user_credentials table (password storage)
CREATE TABLE IF NOT EXISTS user_credentials (
    id SERIAL PRIMARY KEY,
    registration_id INTEGER UNIQUE NOT NULL REFERENCES registrations(id) ON DELETE CASCADE,
    password_hash BYTEA NOT NULL,
    salt BYTEA NOT NULL,
    iterations INTEGER DEFAULT 65536,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Create accounts table (bank accounts)
CREATE TABLE IF NOT EXISTS accounts (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES registrations(id) ON DELETE CASCADE,
    account_number VARCHAR(50) UNIQUE NOT NULL,
    account_type VARCHAR(50) NOT NULL,
    balance NUMERIC(19,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(50) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Create transactions table (audit trail)
CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL PRIMARY KEY,
    account_id INTEGER NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    description VARCHAR(255),
    balance_after NUMERIC(19,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Optional: Create customers table for profile information (if needed separately)
-- CREATE TABLE IF NOT EXISTS customers (
--     id SERIAL PRIMARY KEY,
--     name VARCHAR(255),
--     first_name VARCHAR(100),
--     last_name VARCHAR(100),
--     email VARCHAR(255),
--     phone VARCHAR(20),
--     address VARCHAR(255),
--     dob DATE,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_registrations_email ON registrations(email);
CREATE INDEX IF NOT EXISTS idx_accounts_customer_id ON accounts(customer_id);
CREATE INDEX IF NOT EXISTS idx_transactions_account_id ON transactions(account_id);
CREATE INDEX IF NOT EXISTS idx_user_credentials_registration_id ON user_credentials(registration_id);

-- Grant permissions (if needed)
-- GRANT SELECT, INSERT, UPDATE, DELETE ON registrations TO postgres;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON user_credentials TO postgres;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON accounts TO postgres;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON transactions TO postgres;
