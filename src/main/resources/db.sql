-- Criação do schema
CREATE SCHEMA IF NOT EXISTS financred;

-- Tabela: cliente
CREATE TABLE financred.cliente (
                                   id SERIAL PRIMARY KEY,
                                   nome_completo VARCHAR(255),
                                   cpf VARCHAR(14) NOT NULL UNIQUE,
                                   data_nascimento DATE,
                                   email VARCHAR(255),
                                   senha VARCHAR(255),
                                   role VARCHAR(50),
                                   renda_mensal NUMERIC(10,2),
                                   score BIGINT NOT NULL CHECK (score BETWEEN 0 AND 1000)
);

-- Tabela: emprestimo
CREATE TABLE financred.emprestimo (
                                      id SERIAL PRIMARY KEY,
                                      valor_solicitado NUMERIC(12,2) NOT NULL,
                                      data_solicitacao DATE NOT NULL,
                                      status_emprestimo VARCHAR(50) NOT NULL,
                                      cliente_id BIGINT REFERENCES financred.cliente(id) ON DELETE CASCADE,
                                      numero_parcelas INTEGER,
                                      total_com_juros NUMERIC(12,2),
                                      data_inicio DATE,
                                      data_fim DATE,
                                      valor_juros NUMERIC(5,4)
);

-- Tabela: emprestimo_parcelas
CREATE TABLE financred.emprestimo_parcelas (
                                               id SERIAL PRIMARY KEY,
                                               valor_parcela NUMERIC(10,2) NOT NULL,
                                               emprestimo_id BIGINT REFERENCES financred.emprestimo(id) ON DELETE CASCADE,
                                               status_parcela VARCHAR(50) NOT NULL,
                                               valor_parcela_com_juros NUMERIC(10,2) NOT NULL,
                                               valor_juros NUMERIC(5,4),
                                               dias_atraso INTEGER
);
