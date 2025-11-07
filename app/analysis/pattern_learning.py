# pattern_learning.py (Simplified)
from typing import List, Dict, Tuple
from datetime import datetime, date, timedelta
from decimal import Decimal
from collections import defaultdict
import statistics
import math

from models import (
    Transaction, Category, ConsumptionPattern, CategoryPattern, 
    PeriodType, PatternLearningConfig
)


class PatternLearningEngine:
    """Simplified user consumption pattern learning engine"""
    
    def __init__(self, config: PatternLearningConfig = None):
        self.config = config or PatternLearningConfig()
    
    def learn_user_consumption_pattern(
        self, 
        user_id: int,
        historical_transactions: List[Transaction],
        categories: List[Category],
        period_type: PeriodType = PeriodType.WEEKLY
    ) -> ConsumptionPattern:
        """Learn user historical consumption patterns"""
        # Filter expense transactions
        expense_transactions = [
            txn for txn in historical_transactions 
            if txn.type.value == "EXPENSE"
        ]
        
        if len(expense_transactions) < self.config.min_data_points:
            raise ValueError(f"Insufficient data points, need at least {self.config.min_data_points} transaction records")
        
        # Create category lookup table
        category_lookup = {cat.id: cat for cat in categories}
        
        # Group transactions by category
        transactions_by_category = self._group_transactions_by_category(expense_transactions)
        
        # Learn patterns for each category
        category_patterns = {}
        
        for category_id, transactions in transactions_by_category.items():
            if category_id not in category_lookup:
                continue
                
            category = category_lookup[category_id]
            pattern = self._learn_category_pattern(category, transactions, period_type)
            
            category_patterns[category_id] = pattern
        
        # Calculate overall statistics
        total_spent = sum(txn.amount for txn in expense_transactions)
        total_average_spent = total_spent / len(expense_transactions)
        
        # Calculate pattern confidence (simplified)
        pattern_confidence = self._calculate_pattern_confidence(
            len(expense_transactions), 
            len(category_patterns),
            0.1  # Simplified volatility
        )
        
        return ConsumptionPattern(
            user_id=user_id,
            period_type=period_type,
            category_patterns=category_patterns,
            total_average_spent=total_average_spent,
            pattern_confidence=pattern_confidence,
            last_updated=datetime.now(),
            data_points_count=len(expense_transactions)
        )
    
    def _group_transactions_by_category(self, transactions: List[Transaction]) -> Dict[int, List[Transaction]]:
        """Group transactions by category"""
        grouped = defaultdict(list)
        for txn in transactions:
            grouped[txn.category_id].append(txn)
        return dict(grouped)
    
    def _learn_category_pattern(
        self, 
        category: Category, 
        transactions: List[Transaction], 
        period_type: PeriodType
    ) -> CategoryPattern:
        """Learn pattern for a single category"""
        if not transactions:
            return CategoryPattern(
                category_id=category.id,
                category_name=category.name,
                average_amount=Decimal("0"),
                average_ratio=0.0,
                frequency=0.0,
                variance=Decimal("0"),
                trend="stable",
                last_updated=datetime.now()
            )
        
        # Calculate basic statistics
        amounts = [txn.amount for txn in transactions]
        average_amount = sum(amounts) / len(amounts)
        
        # Calculate frequency (simplified)
        frequency = min(len(transactions) / 30, 1.0)  # Assume 30-day period
        
        # Calculate variance
        variance = self._calculate_variance(amounts)
        
        # Calculate trend (simplified)
        trend = self._calculate_trend(transactions)
        
        return CategoryPattern(
            category_id=category.id,
            category_name=category.name,
            average_amount=average_amount,
            average_ratio=0.0,  # Will be calculated later
            frequency=frequency,
            variance=variance,
            trend=trend,
            last_updated=datetime.now()
        )
    
    def _calculate_variance(self, amounts: List[Decimal]) -> Decimal:
        """Calculate variance of amounts"""
        if len(amounts) < 2:
            return Decimal("0")
        
        mean_amount = sum(amounts) / len(amounts)
        variance = sum((amount - mean_amount) ** 2 for amount in amounts) / len(amounts)
        return variance
    
    def _calculate_trend(self, transactions: List[Transaction]) -> str:
        """Calculate simple trend"""
        if len(transactions) < 2:
            return "stable"
        
        # Sort by date
        sorted_transactions = sorted(transactions, key=lambda x: x.transaction_date)
        
        # Simple trend calculation
        first_half = sorted_transactions[:len(sorted_transactions)//2]
        second_half = sorted_transactions[len(sorted_transactions)//2:]
        
        first_avg = sum(txn.amount for txn in first_half) / len(first_half)
        second_avg = sum(txn.amount for txn in second_half) / len(second_half)
        
        if second_avg > first_avg * Decimal("1.1"):
            return "increasing"
        elif second_avg < first_avg * Decimal("0.9"):
            return "decreasing"
        else:
            return "stable"
    
    def _calculate_pattern_confidence(
        self, 
        data_points: int, 
        category_count: int, 
        volatility: float
    ) -> float:
        """Calculate pattern confidence"""
        # Confidence based on data volume
        data_confidence = min(data_points / 50, 1.0)  # Max confidence at 50+ data points
        
        # Confidence based on category count
        category_confidence = min(category_count / 5, 1.0)
        
        # Confidence based on volatility (lower volatility, higher confidence)
        volatility_confidence = max(0, 1.0 - volatility)
        
        # Comprehensive confidence
        confidence = (data_confidence * 0.4 + category_confidence * 0.3 + volatility_confidence * 0.3)
        
        return min(confidence, 1.0)