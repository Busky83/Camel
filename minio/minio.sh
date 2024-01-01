docker pull yenchou/camel.minio

docker run  -p 9000:9000 -p 9090:9090 --name minio \
 -d --restart=always \
 -e MINIO_ACCESS_KEY=minio \
 -e MINIO_SECRET_KEY=minio@123 \
 -v ./minio_data:/data \
 -v ./minio_config:/root/.minio \
  yenchou/camel.minio server /data  --console-address ":9000" --address ":9090"