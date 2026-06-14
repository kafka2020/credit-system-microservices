CREATE TABLE IF NOT EXISTS credit_applications
(
    id               BIGSERIAL PRIMARY KEY,
    amount           NUMERIC(15, 2) NOT NULL,
    term_months      INTEGER        NOT NULL,
    monthly_income   NUMERIC(15, 2) NOT NULL,
    current_debt_load NUMERIC(15, 2) NOT NULL,
    credit_score     INTEGER        NOT NULL,
    status           VARCHAR(20)    NOT NULL DEFAULT 'PROCESSING',
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP
);

COMMENT ON COLUMN credit_applications.status IS 'Статус заявки: PROCESSING - в обработке, APPROVED - одобрено, REJECTED - отказано';
