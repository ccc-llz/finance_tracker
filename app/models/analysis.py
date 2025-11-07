# models/analysis.py
from pydantic import BaseModel
from typing import List, Optional, Dict
from datetime import datetime
from decimal import Decimal

from .base import PeriodType, DeviationType, SignificanceLevel

# ---------------------------
# Analysis Models
# ---------------------------
class CategorySpendSummary(BaseModel):
    """Category spending summary"""
    category_id: int
    category_name: str
    total_spent: Decimal
    spend_ratio: float

class TransactionAnomaly(BaseModel):
    """Transaction anomaly detection"""
    transaction_id: int
    reason: str

class ConsumptionAnalysisResult(BaseModel):
    """Basic consumption analysis result"""
    total_spent: Decimal
    category_summary: List[CategorySpendSummary]
    anomalies: List[TransactionAnomaly]
    insights: List[str]

# ---------------------------
# Pattern Learning Models
# ---------------------------
class CategoryPattern(BaseModel):
    """Single category consumption pattern"""
    category_id: int
    category_name: str
    average_amount: Decimal
    average_ratio: float
    frequency: float  # Consumption frequency (0-1)
    variance: Decimal  # Consumption amount variance
    trend: str  # "increasing", "decreasing", "stable"
    last_updated: datetime

class ConsumptionPattern(BaseModel):
    """User consumption pattern"""
    user_id: int
    period_type: PeriodType
    category_patterns: Dict[int, CategoryPattern]  # category_id -> CategoryPattern
    total_average_spent: Decimal
    pattern_confidence: float  # Pattern confidence (0-1)
    last_updated: datetime
    data_points_count: int  # Number of data points used for learning

# ---------------------------
# Pattern Comparison Models
# ---------------------------
class DeviationAnalysis(BaseModel):
    """Consumption deviation analysis"""
    category_id: int
    category_name: str
    deviation_type: DeviationType
    deviation_magnitude: float  # Deviation degree (0-1)
    significance: SignificanceLevel
    current_amount: Decimal
    historical_average: Decimal
    current_ratio: float
    historical_ratio: float
    explanation: str
    recommendation: Optional[str] = None

class PatternComparison(BaseModel):
    """Pattern comparison analysis result"""
    user_id: int
    current_period: ConsumptionAnalysisResult
    historical_pattern: ConsumptionPattern
    deviations: List[DeviationAnalysis]
    overall_assessment: str
    significant_changes_count: int
    comparison_confidence: float  # Comparison confidence (0-1)
    analysis_timestamp: datetime

# ---------------------------
# Configuration Models
# ---------------------------
class PatternLearningConfig(BaseModel):
    """Pattern learning configuration"""
    min_data_points: int = 10  # Minimum number of data points
    learning_period_days: int = 90  # Learning period (days)
    confidence_threshold: float = 0.7  # Confidence threshold
