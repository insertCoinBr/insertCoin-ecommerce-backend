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

-----------------------------------------
-- TABELAS PARA PRODUCT E CURRENCY
-----------------------------------------

CREATE TABLE category (
    id integer PRIMARY KEY,
    name varchar(100) NOT NULL
);

CREATE TABLE currency (
    id bigint PRIMARY KEY,
    source_currency varchar(10) NOT NULL,
    target_currency varchar(10) NOT NULL,
    conversion_rate double precision NOT NULL
);

CREATE TABLE platform (
    id integer PRIMARY KEY,
    name varchar(100) NOT NULL
);

CREATE TABLE product (
    id_product uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    name varchar(150) NOT NULL,
    description text,
    price numeric(10,2) NOT NULL,
    rating integer DEFAULT 0,
    id_category integer,
    id_platform integer,
    image_url text,
    stock integer DEFAULT 0
);

CREATE TABLE product_categories (
    id_product uuid NOT NULL,
    id_category integer NOT NULL,
    PRIMARY KEY (id_product, id_category)
);

CREATE TABLE product_rating (
    id UUID PRIMARY KEY,
    id_product UUID NOT NULL,
    rating NUMERIC(3,1) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),

    CONSTRAINT fk_product_rating_product
        FOREIGN KEY (id_product) REFERENCES product(id_product)
);


-----------------------------------------
-- RELACIONAMENTOS (FOREIGN KEYS)
-----------------------------------------

ALTER TABLE product_categories
    ADD FOREIGN KEY (id_category) REFERENCES category(id) ON DELETE CASCADE;

ALTER TABLE product_categories
    ADD FOREIGN KEY (id_product) REFERENCES product(id_product) ON DELETE CASCADE;

ALTER TABLE product
    ADD FOREIGN KEY (id_category) REFERENCES category(id);

ALTER TABLE product
    ADD FOREIGN KEY (id_platform) REFERENCES platform(id);
	
-----------------------------------------
-- INSERT DAS TABELAS PRODUCT/CURRENCY/CATEGORY
-----------------------------------------
	
INSERT INTO category (id, name) VALUES
(1, 'Ação'),
(2, 'Aventura'),
(3, 'RPG'),
(4, 'Esporte'),
(5, 'FPS');

 INSERT INTO currency (
    id, source_currency, target_currency, conversion_rate
) VALUES
(1, 'USD', 'BRL', 5.3524),
(3, 'BRL', 'USD', 0.18683207533069277);

INSERT INTO platform (id, name) VALUES
(1, 'PlayStation 5'),
(2, 'Xbox Series X'),
(3, 'PC'),
(4, 'Nintendo Switch');

INSERT INTO product (
    id_product, name, description, price, rating,
    id_category, id_platform, image_url, stock
) VALUES
('4255c5b3-6179-43e3-ab66-185aef8ee119',
 'Halo Infinite',
 'Shooter futurista exclusivo da Microsoft',
 279.90,
 4,
 1,
 2,
 'https://example.com/halo.jpg',
 30),

('016b3838-b85e-4265-bd83-0f1f243343bb',
 'Elden Ring',
 'RPG de mundo aberto desenvolvido pela FromSoftware',
 299.90,
 5,
 3,
 3,
 'https://example.com/eldenring.jpg',
 100),

('89415868-c7c3-413d-85f6-317cbd77df96',
 'Zelda: Tears of the Kingdom',
 'Nova aventura da série Zelda com exploração livre',
 399.90,
 5,
 2,
 4,
 'https://example.com/zelda.jpg',
 20),

('ee38b412-9960-483f-8030-9a044f0cad99',
 'FIFA 24',
 'Jogo de futebol com modo carreira e multiplayer online',
 349.90,
 5,
 4,
 1,
 'https://example.com/fifa24.jpg',
 50);

INSERT INTO product_categories (
    id_product, id_category
) VALUES
('016b3838-b85e-4265-bd83-0f1f243343bb', 1),
('016b3838-b85e-4265-bd83-0f1f243343bb', 2),

('ee38b412-9960-483f-8030-9a044f0cad99', 4),

('4255c5b3-6179-43e3-ab66-185aef8ee119', 1),
('4255c5b3-6179-43e3-ab66-185aef8ee119', 5),

('89415868-c7c3-413d-85f6-317cbd77df96', 2),
('89415868-c7c3-413d-85f6-317cbd77df96', 3);

-- ============================================
-- TABELAS DE ORDER
-- ============================================

CREATE TABLE orders (
    id_order UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,
    order_number VARCHAR(20) UNIQUE NOT NULL,
    status VARCHAR(50) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_item (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_order UUID NOT NULL,
    id_product UUID NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    sku VARCHAR(100),
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
	image_url text,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE order_item
    ADD CONSTRAINT order_item_id_order_fk
        FOREIGN KEY (id_order) REFERENCES orders(id_order);

ALTER TABLE order_item
    ADD CONSTRAINT order_item_id_product_fk
        FOREIGN KEY (id_product) REFERENCES product(id_product);

-- ============================================
-- TABELAS DE PROMOTION
-- ============================================

CREATE TABLE promotion (
    id_promotion UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255),
    discount_percentage DECIMAL(8,2),
    start_date DATE,
    end_date DATE,
    quantity INTEGER,
    status VARCHAR(255),
    coupon_code VARCHAR(255)
);

CREATE TABLE promotion_product (
    id_promotion_product UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_promotion UUID NOT NULL,
    id_product UUID NOT NULL,
    UNIQUE (id_promotion, id_product)
);

CREATE TABLE promotion_category (
    id_promotion_category UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_promotion UUID NOT NULL,
    id_category INTEGER NOT NULL,
    UNIQUE (id_promotion, id_category)
);

ALTER TABLE promotion_product
    ADD CONSTRAINT promotion_product_id_promotion_fk
        FOREIGN KEY (id_promotion) REFERENCES promotion(id_promotion);

ALTER TABLE promotion_product
    ADD CONSTRAINT promotion_product_id_product_fk
        FOREIGN KEY (id_product) REFERENCES product(id_product);

ALTER TABLE promotion_category
    ADD CONSTRAINT promotion_category_id_promotion_fk
        FOREIGN KEY (id_promotion) REFERENCES promotion(id_promotion);

ALTER TABLE promotion_category
    ADD CONSTRAINT promotion_category_id_category_fk
        FOREIGN KEY (id_category) REFERENCES category(id);

-- ============================================
-- TABELA PAYMENT
-- ============================================

CREATE TABLE payment (
    id_payment UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_order UUID NOT NULL,
    payment_method VARCHAR(50) NOT NULL,   -- 'PIX' ou 'CARD'
    amount DECIMAL(12,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING_PAYMENT',  -- pending, approved, rejected
    transaction_id VARCHAR(255),
    pix_key TEXT,
    pix_payload TEXT,
    paid_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT payment_order_fk
        FOREIGN KEY (id_order) REFERENCES orders(id_order)
);
