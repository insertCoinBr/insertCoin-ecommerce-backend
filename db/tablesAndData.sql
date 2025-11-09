-- ============================================
-- 0. CRIAÇÃO DE TABELAS PARA USUÁRIOS
-- ============================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
  id_user UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name VARCHAR(255),
  email VARCHAR(255) UNIQUE,
  password VARCHAR(255),
  point INTEGER,
  active BOOLEAN DEFAULT true
);

CREATE TABLE "roles" (
  "id_role" UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  "name" VARCHAR(255),
  "is_public" BOOLEAN DEFAULT '0'
);

CREATE TABLE "user_role" (
  "id_user" UUID,
  "id_role" UUID,
  PRIMARY KEY ("id_user", "id_role")
);

CREATE TABLE "permission" (
  "id_permission" UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  "name" VARCHAR(255) NOT NULL
);

CREATE TABLE "roles_permission" (
  "id_role" UUID,
  "id_permission" UUID,
  PRIMARY KEY ("id_role", "id_permission")
);

CREATE TABLE email_verification (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  email VARCHAR(255) NOT NULL,
  code VARCHAR(6) NOT NULL,
  type VARCHAR(50) NOT NULL DEFAULT 'VERIFY_EMAIL',
  verified BOOLEAN DEFAULT false,
  created_at TIMESTAMP DEFAULT NOW(),
  expires_at TIMESTAMP
);

CREATE INDEX idx_email_verification_email_type ON email_verification (email, type);

ALTER TABLE "roles_permission" ADD CONSTRAINT "roles_permission_id_role_foreign" FOREIGN KEY ("id_role") REFERENCES "roles" ("id_role");
ALTER TABLE "user_role" ADD CONSTRAINT "user_role_id_user_foreign" FOREIGN KEY ("id_user") REFERENCES "users" ("id_user");
ALTER TABLE "user_role" ADD CONSTRAINT "user_role_id_role_foreign" FOREIGN KEY ("id_role") REFERENCES "roles" ("id_role");
ALTER TABLE "roles_permission" ADD CONSTRAINT "roles_permission_id_permission_foreign" FOREIGN KEY ("id_permission") REFERENCES "permission" ("id_permission");


-- ============================================
-- 1. INSERIR PERMISSÕES (Funcionalidades do sistema)
-- ============================================
INSERT INTO permission (name)
VALUES 
  ('EMPLOYEES_ADMIN'),      -- Gerenciar funcionários
  ('CLIENTS_ADMIN'),        -- Gerenciar clientes
  ('ORDERS_ADMIN'),         -- Gerenciar pedidos
  ('PRODUCTS_ADMIN'),       -- Gerenciar produtos
  ('PROMOTIONS_ADMIN'),     -- Gerenciar promoções
  ('SHOPPING_ACCESS');      -- Realizar compras

-- ============================================
-- 2. INSERIR roles (Perfis de acesso)
-- ============================================
INSERT INTO roles (name, is_public)
VALUES 
  ('MANAGER_STORE', FALSE),   -- Acesso total ao sistema
  ('COMMERCIAL', FALSE), -- Gerencia produtos, pedidos e clientes
  ('CLIENT', TRUE);         -- Realiza compras

-- ============================================
-- 3. ASSOCIAR PERMISSÕES ÀS roles
-- ============================================

-- role_super_admin: TEM TODAS AS PERMISSÕES
INSERT INTO roles_permission (id_role, id_permission)
SELECT r.id_role, p.id_permission
FROM roles r, permission p
WHERE r.name = 'MANAGER_STORE'
  AND p.name IN ('EMPLOYEES_ADMIN','CLIENTS_ADMIN','ORDERS_ADMIN', 'PRODUCTS_ADMIN', 'PROMOTIONS_ADMIN');

-- role_manager_store: Gerencia produtos, pedidos, clientes e promoções
INSERT INTO roles_permission (id_role, id_permission)
SELECT r.id_role, p.id_permission
FROM roles r, permission p
WHERE r.name = 'COMMERCIAL' 
  AND p.name IN ('CLIENTS_ADMIN','ORDERS_ADMIN','PRODUCTS_ADMIN', 'PROMOTIONS_ADMIN');

-- role_client: Apenas realiza compras
INSERT INTO roles_permission (id_role, id_permission)
SELECT r.id_role, p.id_permission
FROM roles r, permission p
WHERE r.name = 'CLIENT' 
  AND p.name = 'SHOPPING_ACCESS';

-- ============================================
-- 4. USUÁRIOS
-- ============================================
INSERT INTO users (name, email, password, point, active)
VALUES 
  ('ManagerStore', 'manager@email.com', '$2a$10$E3VgfaSRWZuT..2fQ8oNcOxsZLISqh6NIscwOQ.cWjhutWoPewarK', 0, TRUE),
  ('Commercial', 'commercial@email.com', '$2a$10$E3VgfaSRWZuT..2fQ8oNcOxsZLISqh6NIscwOQ.cWjhutWoPewarK', 0, TRUE),
  ('Client', 'client@email.com', '$2a$10$E3VgfaSRWZuT..2fQ8oNcOxsZLISqh6NIscwOQ.cWjhutWoPewarK', 0, TRUE);

-- ============================================
-- 5. users_roles
-- ============================================
INSERT INTO user_role (id_user, id_role)
SELECT u.id_user, r.id_role
FROM users u, roles r
WHERE (u.name = 'ManagerStore' AND r.name = 'MANAGER_STORE')
   OR (u.name = 'Commercial' AND r.name = 'COMMERCIAL')
   OR (u.name = 'Client' AND r.name = 'CLIENT');

-- ============================================
-- 5. email_verification
-- ============================================
INSERT INTO email_verification (email, code, type, verified, created_at, expires_at)
VALUES (
    'client@email.com',
    '123456',
    'VERIFY_EMAIL',
    TRUE,
    NOW(),
    NOW() + INTERVAL '10 minutes'
);