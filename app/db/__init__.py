# db/__init__.py
"""
Database module for AI analysis service
Provides database connection and query functions
"""

from .connection import get_db_connection, close_db_connection
from .queries import get_transactions_for_user, get_categories_for_user

__all__ = [
    "get_db_connection",
    "close_db_connection",
    "get_transactions_for_user",
    "get_categories_for_user",
]

