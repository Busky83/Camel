docker run --name flask_db -p 5432:5432 -e POSTGRES_PASSWORD=123456 -d postgres:16-bullseye -c shared_buffers=2048MB -c max_connections=1000
sleep 5
docker exec flask_db psql -U postgres -c "create database alter"
docker exec flask_db psql -U postgres -c "CREATE TABLE if not exists "alterData" (
    "No" serial4 not null primary key,
    "UUID" varchar not null,
    "Temperature" int4,
    "CreateDt" timestamp with time zone default NOW()
);"
