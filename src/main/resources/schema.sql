CREATE TABLE IF NOT EXISTS quantity_measurement_entity (
                                                           id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                           operand1_value  DOUBLE,
                                                           operand1_unit   VARCHAR(50),
    operand1_type   VARCHAR(50),
    operand2_value  DOUBLE,
    operand2_unit   VARCHAR(50),
    operand2_type   VARCHAR(50),
    operation       VARCHAR(50)  NOT NULL,
    result          VARCHAR(255),
    has_error       BOOLEAN      DEFAULT FALSE,
    error_message   VARCHAR(500),
    timestamp_ms    BIGINT
    );

CREATE TABLE IF NOT EXISTS quantity_measurement_history (
                                                            id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                            entity_id       BIGINT,
                                                            operation       VARCHAR(50),
    summary         VARCHAR(500),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );