sh flask/db/db.sh
nohup python flask/main.py &
sh minio/minio.sh
docker-compose -f docker-compose-camel.yml up --build
