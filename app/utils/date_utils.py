# utils/date_utils.py
from typing import List, Dict
from decimal import Decimal
from datetime import date

from models import Transaction, Category


def format_currency(amount: Decimal) -> str:
    """Format a decimal amount as currency string."""
    return f"${amount:.2f}"


def calculate_percentage(value: Decimal, total: Decimal) -> float:
    """Calculate percentage of value relative to total."""
    if total == 0:
        return 0.0
    return float((value / total) * 100)


def filter_transactions_by_date_range(
    transactions: List[Transaction], 
    start_date: date, 
    end_date: date
) -> List[Transaction]:
    """Filter transactions by date range."""
    return [txn for txn in transactions if start_date <= txn.transaction_date <= end_date]


def group_transactions_by_category(transactions: List[Transaction]) -> Dict[int, List[Transaction]]:
    """Group transactions by category_id."""
    grouped = {}
    for txn in transactions:
        if txn.category_id not in grouped:
            grouped[txn.category_id] = []
        grouped[txn.category_id].append(txn)
    return grouped


def create_category_lookup(categories: List[Category]) -> Dict[int, Category]:
    """Create a lookup dictionary for categories by ID."""
    return {cat.id: cat for cat in categories}


def validate_date_range(start_date: date, end_date: date) -> bool:
    """Validate that start_date is before or equal to end_date."""
    return start_date <= end_date
