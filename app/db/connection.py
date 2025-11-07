# db/connection.py
"""
Database connection management
Handles MySQL connection with proper configuration and error handling
"""
import os
import pymysql
from typing import Optional
from dotenv import load_dotenv

load_dotenv()


def get_db_config():
    """
    Get database configuration from environment variables
    Supports both Docker and local development
    
    In Docker: uses service name 'mysql' on port 3306
    Locally: uses 'localhost' on port 5621 (matching backend configuration)
    """
    # Get DB_HOST from environment, default logic follows backend pattern
    db_host = os.getenv("DB_HOST")
    db_port = os.getenv("DB_PORT")
    
    # If DB_HOST is explicitly set, use it (typically 'mysql' in Docker)
    if db_host:
        # If host is 'mysql', we're in Docker, use port 3306
        if db_host == "mysql":
            default_port = "3306"
        else:
            # For localhost or other hosts, use port 5621 (matching backend)
            default_port = "5621"
    else:
        # No DB_HOST set, try to detect environment
        import socket
        try:
            # Try to resolve 'mysql' hostname (Docker Compose service name)
            socket.gethostbyname("mysql")
            db_host = "mysql"  # Docker environment
            default_port = "3306"
        except socket.gaierror:
            # Can't resolve 'mysql', we're likely in local development
            db_host = "localhost"
            default_port = "5621"  # Match backend's default port for local dev
    
    # Use explicit port if set, otherwise use detected default
    port = int(db_port) if db_port else int(default_port)
    
    return {
        "host": db_host,
        "port": port,
        "user": os.getenv("DB_USER", "elec5619p5g1"),
        "password": os.getenv("DB_PASSWORD", "financetrackeristhebestprojectforelec5619"),
        "database": os.getenv("DB_NAME", "finance_tracker"),
        "charset": "utf8mb4",
        "cursorclass": pymysql.cursors.DictCursor,
        "autocommit": False,
    }


def get_db_connection():
    """
    Create and return a database connection
    Returns:
        pymysql.Connection: Database connection object
    Raises:
        ConnectionError: If connection fails with detailed error message
    """
    config = get_db_config()
    # Remove password from config for logging (security)
    config_for_logging = {k: v for k, v in config.items() if k != "password"}
    config_for_logging["password"] = "***"
    
    try:
        connection = pymysql.connect(**config)
        return connection
    except pymysql.Error as e:
        error_msg = (
            f"Failed to connect to database: {str(e)}\n"
            f"Connection config: {config_for_logging}\n"
            f"Tip: In Docker, use DB_HOST=mysql and DB_PORT=3306. "
            f"In local development, use DB_HOST=localhost and DB_PORT=5621"
        )
        raise ConnectionError(error_msg)


def close_db_connection(connection: Optional[pymysql.Connection]):
    """
    Close database connection safely
    Args:
        connection: Database connection to close
    """
    if connection:
        try:
            connection.close()
        except Exception:
            pass  # Ignore errors on close

