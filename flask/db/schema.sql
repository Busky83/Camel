CREATE TABLE if not exists "alterData" (
  "No" serial4 not null primary key,
  "UUID" varchar not null,
  "Temperature" int4,
  "CreateDt" timestamp with time zone default NOW()
);