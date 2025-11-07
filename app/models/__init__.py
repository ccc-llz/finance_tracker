# models/__init__.py
"""
Simplified data models module

Core models for AI consumption analysis:
- Base enums and types
- Transaction and category models
- Analysis result models
"""

# Base enums and types
from .base import (
    TransactionType, PeriodType, DeviationType, SignificanceLevel
)

# Transaction models
from .transaction import (
    Category, Transaction
)

# Analysis models
from .analysis import (
    CategorySpendSummary, TransactionAnomaly, ConsumptionAnalysisResult,
    CategoryPattern, ConsumptionPattern, DeviationAnalysis, PatternComparison,
    PatternLearningConfig
)

# Export core models for easy importing
__all__ = [
    # Base types
    "TransactionType", "PeriodType", "DeviationType", "SignificanceLevel",

    # Transaction
    "Category", "Transaction",

    # Analysis
    "CategorySpendSummary", "TransactionAnomaly", "ConsumptionAnalysisResult",
    "CategoryPattern", "ConsumptionPattern", "DeviationAnalysis", "PatternComparison",
    "PatternLearningConfig"
]