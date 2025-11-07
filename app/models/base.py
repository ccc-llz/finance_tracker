# models/base.py
from enum import Enum

# ---------------------------
# Core Enums
# ---------------------------
class TransactionType(str, Enum):
    INCOME = "INCOME"
    EXPENSE = "EXPENSE"

class PeriodType(str, Enum):
    DAILY = "daily"
    WEEKLY = "weekly"
    MONTHLY = "monthly"

class DeviationType(str, Enum):
    INCREASE = "increase"
    DECREASE = "decrease"
    NEW_CATEGORY = "new_category"
    MISSING_CATEGORY = "missing_category"
    NO_CHANGE = "no_change"

class SignificanceLevel(str, Enum):
    HIGH = "high"
    MEDIUM = "medium"
    LOW = "low"
