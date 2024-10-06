-- Copied from https://github.com/johannesocean/pgvector-demo

CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS embeddings (
  id SERIAL PRIMARY KEY,
  embedding vector,
  text text,
  created_at timestamptz DEFAULT now()
);

-- Keep an index of processed files..
CREATE TABLE IF NOT EXISTS processed (
    id SERIAL PRIMARY KEY,
    file_name text,
    file_dir text,
    is_processed boolean,
    processed_at timestamptz DEFAULT now()
);