# models/transaction.py
from pydantic import BaseModel
from typing import Optional
from datetime import date
from decimal import Decimal

from .base import TransactionType

# ---------------------------
# Core Transaction Models
# ---------------------------
class Category(BaseModel):
    """Transaction category"""
    id: int
    name: str
    is_active: bool = True
    transaction_type: TransactionType

class Transaction(BaseModel):
    """Transaction record"""
    id: Optional[int] = None
    name: str
    type: TransactionType
    amount: Decimal
    transaction_date: date
    category_id: int
    ledger_id: int
    note: Optional[str] = None
