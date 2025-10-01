-- Criar banco
CREATE DATABASE gerenciamento_processos;
USE gerenciamento_processos;

-- Tabela processo
CREATE TABLE processo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    descricao VARCHAR(500),
    data_inicio DATE NOT NULL,
    data_termino DATE NOT NULL,
    prioridade VARCHAR(20) NOT NULL,
    feito_por VARCHAR(50) NOT NULL,
    para_quem VARCHAR(50) NOT NULL,
    data_criacao DATETIME,
    concluido BOOLEAN DEFAULT FALSE
);

-- Tabela Sub Passos
CREATE TABLE sub_passo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descricao VARCHAR(300),
    obrigatorio BOOLEAN NOT NULL DEFAULT TRUE,
    concluido BOOLEAN NOT NULL DEFAULT FALSE,
    data_conclusao DATETIME,
    data_criacao DATETIME,
    ordem INTEGER NOT NULL,
    processo_id BIGINT NOT NULL,
    FOREIGN KEY (processo_id) REFERENCES processo(id) ON DELETE CASCADE
);

-- Índices
CREATE INDEX idx_sub_passo_processo_id ON sub_passo(processo_id);
CREATE INDEX idx_sub_passo_ordem ON sub_passo(processo_id, ordem);
CREATE INDEX idx_processo_prioridade ON processo(prioridade);
CREATE INDEX idx_processo_data_termino ON processo(data_termino);


USE gerenciamento_processos;

-- Adicionar a coluna titulo
ALTER TABLE sub_passo 
ADD COLUMN titulo VARCHAR(200) NOT NULL AFTER id;

-- Atualizar os registros existentes com um título padrão
UPDATE sub_passo 
SET titulo = COALESCE(descricao, 'Subpasso sem título');
