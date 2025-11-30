-- ============================================
-- DONNÉES DE TEST POUR LA TABLE client
-- ============================================

INSERT INTO client (client_id, first_name, last_name, email, password, role, is_verified, created_at, phone_number, cin_number, age, usage_purpose, identity_status)
VALUES
    (1, 'John', 'Doe', 'john.doe@test.com', 'password123', 'USER', true, CURRENT_TIMESTAMP, '+33123456789', 'AB123456', 30, 'Personal use', 'VERIFIED'),
    (2, 'Jane', 'Smith', 'jane.smith@test.com', 'password456', 'USER', false, CURRENT_TIMESTAMP, '+33987654321', 'CD789012', 25, 'Business use', 'PENDING'),
    (3, 'Admin', 'User', 'admin@test.com', 'admin123', 'ADMIN', true, CURRENT_TIMESTAMP, '+33111222333', 'EF345678', 35, 'Administration', 'VERIFIED'),
    (4, 'Test', 'User', 'test.user@test.com', 'test123', 'USER', true, CURRENT_TIMESTAMP, '+33444555666', 'GH901234', 28, 'Testing', 'VERIFIED');

-- ============================================
-- DONNÉES DE TEST POUR LA TABLE self_detail
-- ============================================

INSERT INTO self_detail (self_detail_id, usage_purpose, cin_number, phone_number, age)
VALUES
    ('SELF_DETAIL_1', 'Personal banking and investments', 'AB123456', '+33123456789', 30),
    ('SELF_DETAIL_2', 'Business account management', 'CD789012', '+33987654321', 25),
    ('SELF_DETAIL_3', 'System administration tasks', 'EF345678', '+33111222333', 35),
    ('SELF_DETAIL_4', 'Application testing purposes', 'GH901234', '+33444555666', 28);