# Cadence Repo Ana y Stefano

## Download docker compose Cadence Server
> curl -O https://raw.githubusercontent.com/uber/cadence/master/docker/docker-compose.yml
> docker-compose up

## Run cadence server host
> docker run --network=host --rm ubercadence/cli:master --do example domain register -rd 1