# enhanced_analysis.py
from typing import List, Optional
from datetime import datetime, date, timedelta
from decimal import Decimal

from models import (
    Transaction, Category, ConsumptionAnalysisResult, ConsumptionPattern,
    PatternComparison, PeriodType, PatternLearningConfig, CategorySpendSummary, TransactionAnomaly
)
from utils import calculate_statistical_threshold
from .pattern_learning import PatternLearningEngine
from .pattern_comparison import PatternComparisonEngine


class EnhancedAnalysisEngine:
    """Consumption analysis engine"""
    
    def __init__(self, config: PatternLearningConfig = None):
        self.learning_engine = PatternLearningEngine(config)
        self.comparison_engine = PatternComparisonEngine()
    
    def analyze_with_historical_context(
        self,
        user_id: int,
        current_transactions: List[Transaction],
        historical_transactions: List[Transaction],
        categories: List[Category],
        period_type: PeriodType = PeriodType.WEEKLY
    ) -> PatternComparison:
        """
        Perform consumption analysis based on historical context
        
        Args:
            user_id: User ID
            current_transactions: Current period transaction data
            historical_transactions: Historical transaction data
            categories: Category data
            period_type: Analysis period type
            
        Returns:
            PatternComparison: Analysis result containing historical comparison
        """
        # 1. Learn historical consumption patterns
        historical_pattern = self.learning_engine.learn_user_consumption_pattern(
            user_id=user_id,
            historical_transactions=historical_transactions,
            categories=categories,
            period_type=period_type
        )
        
        # 2. Analyze current consumption
        current_analysis = self._analyze_current_consumption(
            current_transactions, categories
        )
        
        # 3. Comparison analysis
        comparison = self.comparison_engine.compare_with_historical_pattern(
            current_analysis, historical_pattern
        )
        
        return comparison
    
    def _analyze_current_consumption(
        self,
        transactions: List[Transaction],
        categories: List[Category]
    ) -> ConsumptionAnalysisResult:
        """Analyze current consumption"""
        from collections import defaultdict
        
        # Filter expense transactions
        expense_transactions = [
            txn for txn in transactions 
            if txn.type.value == "EXPENSE"
        ]
        
        # CalculateTotal spending
        total_spent = sum(txn.amount for txn in expense_transactions)
        
        # Group by category
        category_totals = defaultdict(Decimal)
        for txn in expense_transactions:
            category_totals[txn.category_id] += txn.amount
        
        # Create category lookup table
        category_lookup = {cat.id: cat for cat in categories}
        
        # Build category summary
        category_summary = []
        for cat_id, spent in category_totals.items():
            cat = category_lookup.get(cat_id)
            if cat:
                ratio = float(spent / total_spent) if total_spent > 0 else 0.0
                category_summary.append(CategorySpendSummary(
                    category_id=cat.id,
                    category_name=cat.name,
                    total_spent=spent,
                    spend_ratio=ratio
                ))
        
        # Intelligent anomaly detection using statistical method
        anomalies = []
        amounts = [txn.amount for txn in expense_transactions]
        
        # Calculate statistical threshold using utils function
        threshold = calculate_statistical_threshold(
            amounts=amounts,
            multiplier=2.0,  # 2 standard deviations for 95% coverage
            min_threshold=Decimal("500"),
            max_threshold=Decimal("10000")
        )
        
        for txn in expense_transactions:
            if txn.amount > threshold:
                anomalies.append(TransactionAnomaly(
                    transaction_id=txn.id or 0,
                    reason=f"Statistical anomaly: {txn.amount} (threshold: {threshold:.2f})"
                ))
        
        # Generate basic insights
        insights = []
        if category_summary:
            top_category = max(category_summary, key=lambda x: x.total_spent)
            insights.append(
                f"Highest spending category: {top_category.category_name} "
                f"({top_category.total_spent}, {top_category.spend_ratio*100:.1f}%)"
            )
        
        return ConsumptionAnalysisResult(
            total_spent=total_spent,
            category_summary=category_summary,
            anomalies=anomalies,
            insights=insights
        )
    
    def get_learning_summary(self, pattern: ConsumptionPattern) -> str:
        """Get learning pattern summary"""
        summary_parts = []
        
        summary_parts.append(f"Learned {len(pattern.category_patterns)}  consumption category patterns")
        summary_parts.append(f"Based on {pattern.data_points_count}  historical transaction records")
        summary_parts.append(f"Pattern confidence: {pattern.pattern_confidence:.1%}")
        
        # Main consumption categories
        if pattern.category_patterns:
            top_categories = sorted(
                pattern.category_patterns.values(),
                key=lambda x: x.average_amount,
                reverse=True
            )[:3]
            
            summary_parts.append("Main consumption categories:")
            for cat in top_categories:
                summary_parts.append(
                    f"- {cat.category_name}: Average {cat.average_amount}, "
                    f"Frequency {cat.frequency:.1%}, Trend {cat.trend}"
                )
        
        return "\n".join(summary_parts)
    
    def get_comparison_summary(self, comparison: PatternComparison) -> str:
        """Get comparison analysis summary"""
        summary_parts = []
        
        summary_parts.append(f"Comparison analysis result:")
        summary_parts.append(f"- Found {len(comparison.deviations)}  consumption changes")
        summary_parts.append(f"- Among which {comparison.significant_changes_count}  Significant changes")
        summary_parts.append(f"- Comparison confidence: {comparison.comparison_confidence:.1%}")
        
        # Main changes
        top_deviations = self.comparison_engine.get_top_deviations(comparison, 3)
        if top_deviations:
            summary_parts.append("Main changes:")
            for dev in top_deviations:
                summary_parts.append(
                    f"- {dev.category_name}: {dev.deviation_type.value} "
                    f"({dev.deviation_magnitude:.1%}), {dev.significance.value}Importance"
                )
        
        return "\n".join(summary_parts)
    
    def should_update_pattern(
        self,
        pattern: ConsumptionPattern,
        new_transactions_count: int
    ) -> bool:
        """Judge if pattern needs updating"""
        # Based on time
        days_since_update = (datetime.now() - pattern.last_updated).days
        time_threshold = 7  # 7days
        
        # Based on new data volume
        data_threshold = 10  # 10 new transactions
        
        return (
            days_since_update >= time_threshold or 
            new_transactions_count >= data_threshold
        )


# Convenience functions
def analyze_weekly_consumption_with_history(
    user_id: int,
    current_week_transactions: List[Transaction],
    historical_transactions: List[Transaction],
    categories: List[Category]
) -> PatternComparison:
    """Analyze this week consumption and compare with history"""
    engine = EnhancedAnalysisEngine()
    return engine.analyze_with_historical_context(
        user_id=user_id,
        current_transactions=current_week_transactions,
        historical_transactions=historical_transactions,
        categories=categories,
        period_type=PeriodType.WEEKLY
    )


def learn_user_pattern(
    user_id: int,
    historical_transactions: List[Transaction],
    categories: List[Category],
    period_type: PeriodType = PeriodType.WEEKLY
) -> ConsumptionPattern:
    """Learn user consumption patterns"""
    engine = PatternLearningEngine()
    return engine.learn_user_consumption_pattern(
        user_id=user_id,
        historical_transactions=historical_transactions,
        categories=categories,
        period_type=period_type
    )


def compare_patterns(
    current_analysis: ConsumptionAnalysisResult,
    historical_pattern: ConsumptionPattern
) -> PatternComparison:
    """Compare current consumption with historical patterns"""
    engine = PatternComparisonEngine()
    return engine.compare_with_historical_pattern(
        current_analysis, historical_pattern
    )
