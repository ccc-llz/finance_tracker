# db/queries.py
"""
Database query functions
Converts database results to Pydantic models
"""
from typing import List, Optional
from datetime import date, datetime
from decimal import Decimal
import pymysql

from models import Transaction, Category, TransactionType
from .connection import get_db_connection, close_db_connection


def _convert_transaction_type(value: int) -> TransactionType:
    """
    Convert database enum value to TransactionType
    In Java, TransactionType is stored as ORDINAL (0=INCOME, 1=EXPENSE)
    """
    # TransactionType enum in Java: INCOME=0, EXPENSE=1
    if value == 0:
        return TransactionType.INCOME
    elif value == 1:
        return TransactionType.EXPENSE
    else:
        # Default to EXPENSE if unknown
        return TransactionType.EXPENSE


def get_transactions_for_user(
    user_id: int, 
    start_date: date, 
    end_date: date,
    ledger_id: Optional[int] = None
) -> List[Transaction]:
    """
    Query transactions for a user within date range
    
    Args:
        user_id: User ID
        start_date: Start date (inclusive)
        end_date: End date (inclusive)
        ledger_id: Optional ledger ID to filter by
        
    Returns:
        List of Transaction objects
    """
    connection = None
    try:
        connection = get_db_connection()
        
        # Build query with optional ledger filter
        # Note: Table names and column names are lowercase with underscores in actual database
        query = """
            SELECT 
                t.id,
                t.name,
                t.type,
                t.amount,
                t.transaction_date,
                t.category_id,
                t.ledger_id
            FROM transactions t
            INNER JOIN users u ON t.user_id = u.id
            WHERE u.id = %s
              AND t.transaction_date >= %s
              AND t.transaction_date <= %s
        """
        
        params = [user_id, start_date, end_date]
        
        if ledger_id is not None:
            query += " AND t.ledger_id = %s"
            params.append(ledger_id)
        
        query += " ORDER BY t.transaction_date DESC"
        
        with connection.cursor() as cursor:
            cursor.execute(query, params)
            results = cursor.fetchall()
        
        # Convert database results to Transaction models
        transactions = []
        for row in results:
            # Convert date string or date object to date
            if isinstance(row['transaction_date'], str):
                transaction_date = datetime.strptime(row['transaction_date'], '%Y-%m-%d').date()
            elif isinstance(row['transaction_date'], datetime):
                transaction_date = row['transaction_date'].date()
            else:
                transaction_date = row['transaction_date']
            
            # Convert amount to Decimal
            amount = Decimal(str(row['amount']))
            
            # Convert type enum
            transaction_type = _convert_transaction_type(row['type'])
            
            transaction = Transaction(
                id=row['id'],
                name=row['name'],
                type=transaction_type,
                amount=amount,
                transaction_date=transaction_date,
                category_id=row['category_id'],
                ledger_id=row['ledger_id'],
                note=None  # Transaction table doesn't have note field
            )
            transactions.append(transaction)
        
        return transactions
        
    except pymysql.Error as e:
        raise RuntimeError(f"Database query error: {str(e)}")
    finally:
        close_db_connection(connection)


def get_categories_for_user(user_id: int) -> List[Category]:
    """
    Query categories accessible by a user
    Uses UserCategoryAvailability table to determine user's accessible categories
    
    Args:
        user_id: User ID
        
    Returns:
        List of Category objects
    """
    connection = None
    try:
        connection = get_db_connection()
        
        # Query categories through UserCategoryAvailability
        # Note: Table names and column names are lowercase with underscores in actual database
        query = """
            SELECT DISTINCT
                c.id,
                c.name,
                c.is_active,
                c.type AS transaction_type
            FROM categories c
            INNER JOIN user_category_availability uca ON c.id = uca.category_id
            WHERE uca.user_id = %s
              AND c.is_active = 1
            ORDER BY c.name
        """
        
        with connection.cursor() as cursor:
            cursor.execute(query, [user_id])
            results = cursor.fetchall()
        
        # Convert database results to Category models
        categories = []
        for row in results:
            # Convert type enum
            transaction_type = _convert_transaction_type(row['transaction_type'])
            
            category = Category(
                id=row['id'],
                name=row['name'],
                is_active=bool(row['is_active']),
                transaction_type=transaction_type
            )
            categories.append(category)
        
        return categories
        
    except pymysql.Error as e:
        raise RuntimeError(f"Database query error: {str(e)}")
    finally:
        close_db_connection(connection)

