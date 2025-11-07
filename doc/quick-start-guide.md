# [Back to README](../README.md)

# Quick Start Guide

## Prerequisites

Before running the application, ensure you have the following installed:

- **Docker** (version 20.10 or higher)
- **Docker Compose** (version 2.0 or higher)
- **OpenAI API Key** (required for AI features)

## Running the Application

### Docker Compose

This is the easiest way to run the entire application.

1. **Clone the repository** (if not already done):
   ```bash
   git clone https://github.sydney.edu.au/2025S2-Object-Oriented-App-Frameworks/ELEC5619_Practical05_Group_1.git
   cd ELEC5619_Practical05_Group_1
   ```

2. **Set up environment variables** (optional):
   
   Create a `.env` file in the root directory or set environment variables:
   ```bash
   export OPENAI_API_KEY=your_openai_api_key_here
   export CORS_DOMAINS=http://localhost:5620
   ```
   
   Note: Default values will be used if not set. The application will work without OpenAI API key, but AI features will not function.

3. **Start all services**:
   ```bash
   docker compose up --build
   ```

4. **Wait for services to start**:
   
   The services will start in this order:
   - MySQL database
   - Redis cache
   - Backend API
   - AI service
   - Frontend
   - Seed data (demo user creation)

   Watch the logs to see when all services are healthy. It usually takes 1-2 minutes for all services to be ready.

5. **Verify services are running**:
   ```bash
   docker compose ps
   ```

## Accessing the Application

1. **Open your browser** and navigate to:
   ```
   http://localhost:5620
   ```

2. **Login with demo account** (if seed data was created):
   - Username: `demo`
   - Password: `demo123`

3. **Or create a new account**:
   - Click "Register" on the login page
   - Fill in email, username, and password
   - Start using the application

### Health Checks

You can verify services are running:

- **Backend**: http://localhost:5619/actuator/health
- **AI Service**: http://localhost:5623/health
- **Frontend**: http://localhost:5620

### Troubleshooting

**Services won't start:**
- Check if ports are already in use
- Ensure Docker has enough resources allocated
- Check logs: `docker compose logs [service-name]`

**Database connection errors:**
- Wait for MySQL to be fully ready (health check passes)
- Verify MySQL container is running: `docker compose ps mysql`

**AI features not working:**
- Check if OPENAI_API_KEY is set correctly
- Verify AI service is running: `docker compose logs model`
- Check API connectivity: `curl http://localhost:5623/health`

**Frontend can't connect to backend:**
- Verify backend is running and healthy
- Check CORS_DOMAINS matches frontend URL
- Verify network connectivity between containers

## Stopping the Application

To stop all services:
```bash
docker compose down
```

To stop and remove volumes (data will be lost):
```bash
docker compose down -v
```
