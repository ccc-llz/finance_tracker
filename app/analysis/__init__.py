# analysis/__init__.py
"""
Analysis engine module

Provides core functionality for consumption behavior analysis, including:
- Pattern learning engine
- Pattern comparison engine
- Enhanced analysis engine
"""

from .pattern_learning import PatternLearningEngine
from .pattern_comparison import PatternComparisonEngine
from .enhanced_analysis import (
    EnhancedAnalysisEngine, 
    analyze_weekly_consumption_with_history,
    learn_user_pattern,
    compare_patterns
)

__all__ = [
    "PatternLearningEngine",
    "PatternComparisonEngine", 
    "EnhancedAnalysisEngine",
    "analyze_weekly_consumption_with_history",
    "learn_user_pattern",
    "compare_patterns"
]
