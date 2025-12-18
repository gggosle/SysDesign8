# SysDesign8
To run the docker container for the db, use the following command:

```bash
docker compose -f docker/docker-compose.yml up -d
```
docker compose -f docker/docker-compose.yml up -d --force-restart
make sure to delete the port 5432 processes before running the docker container