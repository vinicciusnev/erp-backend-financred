-- Criação do schema
CREATE SCHEMA IF NOT EXISTS financred;

-- Tabela: cliente
CREATE TABLE financred.cliente (
                                   id serial4 NOT NULL,
                                   nome_completo varchar(255) NOT NULL,
                                   cpf varchar(14) NULL,
                                   data_nascimento date NULL,
                                   email varchar(255) NOT NULL,
                                   telefone varchar(20) NULL,
                                   senha varchar(255) NULL,
                                   "role" varchar(50) NOT NULL,
                                   rua varchar(255) NULL,
                                   numero varchar(20) NULL,
                                   complemento varchar(255) NULL,
                                   bairro varchar(100) NULL,
                                   cidade varchar(100) NULL,
                                   estado varchar(100) NULL,
                                   cep varchar(20) NULL,
                                   renda_mensal numeric(10, 2) NULL,
                                   profissao varchar(100) NULL,
                                   empresa varchar(255) NULL,
                                   banco varchar(100) NULL,
                                   agencia varchar(20) NULL,
                                   conta varchar(30) NULL,
                                   score int8 NULL,
                                   CONSTRAINT cliente_cpf_key UNIQUE (cpf),
                                   CONSTRAINT cliente_pkey PRIMARY KEY (id),
                                   CONSTRAINT cliente_renda_mensal_check CHECK ((renda_mensal >= (0)::numeric)),
                                   CONSTRAINT cliente_score_check CHECK (((score >= 0) AND (score <= 1000)))
);

-- Tabela: emprestimo
CREATE TABLE financred.emprestimo (
                                      id SERIAL PRIMARY KEY,
                                      valor_solicitado NUMERIC(12,2) NOT NULL,
                                      total_com_juros NUMERIC(12,2),
                                      valor_juros NUMERIC(10, 4),
                                      taxa_multa_atraso NUMERIC(10, 4),
                                      numero_parcelas INTEGER,
                                      taxa_juros NUMERIC(10, 4),
                                      status_emprestimo VARCHAR(50) NOT NULL,
                                      tipo_emprestimo VARCHAR(50),
                                      observacao TEXT,
                                      data_solicitacao DATE NOT NULL,
                                      data_aprovacao DATE,
                                      data_inicio DATE,
                                      data_fim DATE,
                                      aprovado_por VARCHAR(100),
                                      cliente_id BIGINT REFERENCES financred.cliente(id) ON DELETE CASCADE,
                                      created_at TIMESTAMP DEFAULT now(),
                                      updated_at TIMESTAMP DEFAULT now()
);

-- Tabela: emprestimo_parcelas
CREATE TABLE financred.emprestimo_parcelas (
                                               id serial4 PRIMARY KEY,
                                               numero_parcela int NOT NULL,
                                               valor_parcela numeric(10, 2) NOT NULL,
                                               valor_juros numeric(10, 4),
                                               multa numeric(10, 2),
                                               dias_atraso int,
                                               status_parcela varchar(50) NOT NULL,
                                               data_vencimento date NOT NULL,
                                               data_pagamento date,
                                               observacao text,
                                               created_at timestamp DEFAULT now(),
                                               updated_at timestamp DEFAULT now(),
                                               emprestimo_id int8,
                                               CONSTRAINT emprestimo_parcelas_emprestimo_id_fkey FOREIGN KEY (emprestimo_id) REFERENCES financred.emprestimo(id) ON DELETE CASCADE
);

-- Criando trigger
CREATE OR REPLACE FUNCTION calcular_dias_atraso()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.data_pagamento IS NULL THEN
        IF CURRENT_DATE > NEW.data_vencimento THEN
            NEW.dias_atraso := CURRENT_DATE - NEW.data_vencimento;
        ELSE
            NEW.dias_atraso := 0;
        END IF;
    ELSE
        IF NEW.data_pagamento > NEW.data_vencimento THEN
            NEW.dias_atraso := NEW.data_pagamento - NEW.data_vencimento;
        ELSE
            NEW.dias_atraso := 0;
        END IF;
    END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Setando trigger
CREATE TRIGGER trg_calcular_dias_atraso
BEFORE INSERT OR UPDATE ON financred.emprestimo_parcelas
FOR EACH ROW
EXECUTE FUNCTION calcular_dias_atraso();
