```markdown
# REST API Swarm Deployment (restapiapp)

This repository contains the configuration to deploy a high-availability, three-tier application stack using Docker Swarm.  
The stack consists of:

1. **app**: The Spring Boot REST API (scaled to 3 replicas).  
2. **mysql-db**: A MySQL 8.0 database with persistent storage.  
3. **loadbalancer**: An Nginx instance for external traffic routing and load balancing.

## 1. Prerequisites
Before deploying, ensure you have the following in the same directory:  
- `docker-compose.yml` (The provided stack definition).  
- A `Dockerfile` (for building the Spring Boot application image).  
- An `nginx.conf` file (for the Nginx load balancer configuration).

## 2. Deployment Steps
The application must be deployed using Docker Swarm Mode.

### Step 1: Initialize Docker Swarm (If not already initialized)
You must designate your current node as a Swarm Manager. You need to explicitly specify the IP address the node will use to communicate with the rest of the cluster (even if it's just a single node).  
**Action:** Replace `<YOUR_NODE_IP>` with the correct address found in your sandbox console (e.g., 192.168.0.34).

```bash
docker swarm init --advertise-addr <YOUR_NODE_IP>
# Example: docker swarm init --advertise-addr 192.168.0.34
```

### Step 2: Build the Application Image (Force Rebuild)
Docker Swarm requires a pre-tagged image reference, and the build process sometimes uses cached layers, ignoring changes to the CMD instruction. We must force a clean rebuild of the image with the correct 180-second timeout baked into the CMD.

```bash
docker build --no-cache -t restapiapp_app:latest .
```

### Step 3: Deploy the Stack
Since the Compose file uses Swarm features (deploy, overlay network), you must use the `docker stack deploy` command. This creates and manages the services, volumes, and networks defined in the file, pulling the locally built image.

```bash
docker stack deploy -c docker-compose.yml restapiapp
```

The stack name used here is `restapiapp`.

## 3. Monitoring and Scaling
Once the stack is deployed, use the following commands to monitor its health and status.

### Monitoring Service Status and Health
Check the status of all services in your stack, including which nodes they are running on and their current health.

```bash
# View the running services and their status
docker stack ps restapiapp

# Check the health status of the services
docker service ls
```

### Monitoring Database Health
The mysql-db healthcheck is defined to run every 5 seconds. You can specifically check the health status of the database:

```bash
# Get the full name of a running mysql-db container
DB_CONTAINER=$(docker ps -qf "name=restapiapp_mysql-db")

# Inspect the health status of the container
docker inspect --format '{{json .State.Health}}' $DB_CONTAINER
```

### Monitoring Application Health
The app service uses the `/actuator/health` endpoint for its healthcheck. You can monitor the health state of the application service and its replicas.

```bash
# View all service tasks (replicas) for the app
docker service ps restapiapp_app

# View logs for the application service (good for troubleshooting)
docker service logs restapiapp_app
```

### Scaling the Application
Because the stack is deployed with Swarm, you can easily adjust the number of replicas for high availability on the fly.  
To scale the Spring Boot application replicas from 3 to 5:

```bash
docker service scale restapiapp_app=5
```

## 4. Cleanup
When development or testing is complete, use the following commands to safely tear down the entire stack.

### Remove the Stack
This command stops all containers, removes the services, and deletes the network. It does not remove the persistent volume (mysql-data).

```bash
docker stack rm restapiapp
```

### Remove the Persistent Volume
If you want to delete the stored MySQL data (only do this when you are absolutely sure you don't need the data anymore):

```bash
docker volume rm restapiapp_mysql-data
```

The volume name is automatically prefixed with the stack name (`restapiapp`).

## 5. Troubleshooting: Restarting Services
If you encounter issues where the app service is running but NGINX is not correctly routing or load balancing (often due to internal DNS caching), performing a forced restart of the services can resolve the issue.

### Service Restart Sequence
**Step 1:** Restart app to lock in DNS

```bash
docker service update --force restapiapp_app
```

**Step 2:** Wait 10s, then restart NGINX

```bash
sleep 10
docker service update --force restapiapp_loadbalancer
```

### Verify
```bash
docker stack ps restapiapp  # Both Running, no recent failures
docker service logs --tail 5 restapiapp_loadbalancer  # No [emerg] errors
curl http://localhost:80/actuator/health  # Internal test: UP
```

## 6. Public Access and Endpoints
To access your application from a public URL (like in a Play-with-Docker environment),
open the port 80 you use the host address on port 80, which is handled by the NGINX load balancer.  
Public URLs (Ensure you replace the host name with your actual environment host):
```
- **Health Check:** http://ip172-18-0-20-d4m30ci91nsg009mfud0-80.direct.labs.play-with-docker.com/actuator/health  
- **API Docs:** http://ip172-18-0-20-d4m30ci91nsg009mfud0-80.direct.labs.play-with-docker.com/swagger-ui/index.html
```

## 7. Shortcut commands in Play with docker labs

```
ctrl+ insert + enter  = copy
ctrl+shift+v = paste
alt+enter = maximise /mimimise  screen
tab =  autocomplete

```
