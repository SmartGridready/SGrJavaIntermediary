# SmartGridready (SGr) Intermediary - Using Docker

This project demonstrates how to run the SGr Intermediary using Docker (Compose).

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/) installed.

## Use Docker directly

### Get the image

**Pull the Docker image**:

```bash
docker pull ghcr.io/smartgridready/sgr-intermediary:master
```

### Run

**Run the Docker container** on port 8080:

```bash
docker run -d -p 8080:8080 --name sgr-intermediary ghcr.io/smartgridready/sgr-intermediary:master
```

The SGr Intermediary service will be accessible at `http://localhost:8080`.

### Run with persistent volume

**Run the Docker container** on port 8080 with a persistent volume:

```bash
docker volume create sgr-intermediary-data
docker run -d -p 8080:8080 -v sgr-intermediary-data:/data:rw -e DB_PATH=/data/sgr_intermediary --name sgr-intermediary ghcr.io/smartgridready/sgr-intermediary:master
```

The environment variable `DB_PATH` defines where the EIDs and configuration are stored.
Setting it to a path where the persistent volume is mounted allows you to keep the data even after the container is destroyed.

### Stop

**Stop the Docker container**:

```bash
docker stop sgr-intermediary
docker container rm sgr-intermediary

# optional - destroy data
docker volume rm sgr-intermediary-data
```

## Use Docker Compose

### Run

**Run the Docker container** and compose stack using the example configuration:
```bash
docker compose -f docker-compose.yml up -d
```

### Stop

Docker compose automatically pulls images and creates volumes.

**Stop the Docker container** and cleans up the compose stack:

```bash
docker compose -f docker-compose.yml down
```

## Troubleshooting

- **Port Conflicts**: Ensure port 8080 is free or modify the Docker command to use an alternate port.
- **Docker Permissions**: Make sure Docker has the necessary permissions to pull and run containers.
