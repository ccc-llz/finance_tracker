# utils/validation.py
from typing import List, Optional
from decimal import Decimal
from datetime import date

from models import (
    Transaction, Category, ConsumptionAnalysisResult, 
    ConsumptionPattern, PatternComparison
)


def validate_transaction_data(transaction: Transaction) -> List[str]:
    """Validate transaction data and return list of errors."""
    errors = []
    
    if not transaction.name or transaction.name.strip() == "":
        errors.append("Transaction name cannot be empty")
    
    if transaction.amount <= 0:
        errors.append("Transaction amount must be positive")
    
    if transaction.category_id <= 0:
        errors.append("Category ID must be positive")
    
    if transaction.ledger_id <= 0:
        errors.append("Ledger ID must be positive")
    
    if transaction.transaction_date > date.today():
        errors.append("Transaction date cannot be in the future")
    
    return errors


def validate_analysis_result(analysis: ConsumptionAnalysisResult) -> List[str]:
    """Validate analysis result data."""
    errors = []
    
    if analysis.total_spent < 0:
        errors.append("Total spent cannot be negative")
    
    if not analysis.category_summary:
        errors.append("Category summary cannot be empty")
    
    # Validate category summary ratios
    total_ratio = sum(cat.spend_ratio for cat in analysis.category_summary)
    if abs(total_ratio - 1.0) > 0.01:  # Allow small floating point errors
        errors.append(f"Category ratios should sum to 1.0, got {total_ratio}")
    
    return errors


def validate_pattern_data(pattern: ConsumptionPattern) -> List[str]:
    """Validate pattern data."""
    errors = []
    
    if pattern.user_id <= 0:
        errors.append("User ID must be positive")
    
    if pattern.pattern_confidence < 0 or pattern.pattern_confidence > 1:
        errors.append("Pattern confidence must be between 0 and 1")
    
    if pattern.data_points_count < 0:
        errors.append("Data points count cannot be negative")
    
    if pattern.total_average_spent < 0:
        errors.append("Total average spent cannot be negative")
    
    return errors
