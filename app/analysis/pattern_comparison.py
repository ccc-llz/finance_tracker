# pattern_comparison.py
from typing import List, Dict, Tuple
from datetime import datetime
from decimal import Decimal

from models import (
    ConsumptionAnalysisResult, ConsumptionPattern, PatternComparison,
    DeviationAnalysis, DeviationType, SignificanceLevel, CategorySpendSummary
)


class PatternComparisonEngine:
    """Pattern comparison analysis engine"""
    
    def __init__(self):
        self.significance_thresholds = {
            "high": 0.3,    # 30%+ changes are high importance
            "medium": 0.15,  # 15-30% changes are medium importance
            "low": 0.05     # 5-15% changes are low importance
        }
    
    def compare_with_historical_pattern(
        self,
        current_analysis: ConsumptionAnalysisResult,
        historical_pattern: ConsumptionPattern
    ) -> PatternComparison:
        """
        Compare current consumption with historical patterns
        
        Args:
            current_analysis: Current period analysis result
            historical_pattern: Historical consumption pattern
            
        Returns:
            PatternComparison: Comparison analysis result
        """
        # Analyze deviations
        deviations = self._analyze_deviations(current_analysis, historical_pattern)
        
        # Calculate significant changes count
        significant_changes = len([
            d for d in deviations 
            if d.significance in [SignificanceLevel.HIGH, SignificanceLevel.MEDIUM]
        ])
        
        # Generate overall assessment
        overall_assessment = self._generate_overall_assessment(deviations, significant_changes)
        
        # CalculateComparison confidence
        comparison_confidence = self._calculate_comparison_confidence(
            historical_pattern.pattern_confidence,
            len(deviations),
            significant_changes
        )
        
        return PatternComparison(
            user_id=historical_pattern.user_id,
            current_period=current_analysis,
            historical_pattern=historical_pattern,
            deviations=deviations,
            overall_assessment=overall_assessment,
            significant_changes_count=significant_changes,
            comparison_confidence=comparison_confidence,
            analysis_timestamp=datetime.now()
        )
    
    def _analyze_deviations(
        self,
        current_analysis: ConsumptionAnalysisResult,
        historical_pattern: ConsumptionPattern
    ) -> List[DeviationAnalysis]:
        """Analyze consumption deviations"""
        deviations = []
        
        # Create lookup table for current category summary
        current_categories = {
            cat.category_id: cat for cat in current_analysis.category_summary
        }
        
        # Analyze each historical category
        for category_id, pattern in historical_pattern.category_patterns.items():
            current_cat = current_categories.get(category_id)
            
            if current_cat:
                # Category exists, analyze changes
                deviation = self._analyze_category_deviation(
                    pattern, current_cat, current_analysis.total_spent
                )
                if deviation:
                    deviations.append(deviation)
            else:
                # Category missing
                deviation = self._create_missing_category_deviation(pattern)
                deviations.append(deviation)
        
        # Analyze new categories
        for category_id, current_cat in current_categories.items():
            if category_id not in historical_pattern.category_patterns:
                deviation = self._create_new_category_deviation(current_cat)
                deviations.append(deviation)
        
        return deviations
    
    def _analyze_category_deviation(
        self,
        historical_pattern,
        current_category: CategorySpendSummary,
        current_total: Decimal
    ) -> DeviationAnalysis:
        """Analyze single category deviation"""
        # Calculate historical average amount (based on historical total consumption)
        historical_average = historical_pattern.average_amount
        
        # Calculate deviation type and degree
        amount_diff = current_category.total_spent - historical_average
        ratio_diff = current_category.spend_ratio - historical_pattern.average_ratio
        
        # Determine deviation type
        if abs(amount_diff) < historical_average * Decimal("0.05"):  # 5% considered as no change
            deviation_type = DeviationType.NO_CHANGE
        elif amount_diff > 0:
            deviation_type = DeviationType.INCREASE
        else:
            deviation_type = DeviationType.DECREASE
        
        # Calculate deviation degree
        if historical_average > 0:
            deviation_magnitude = float(abs(amount_diff) / historical_average)
        else:
            deviation_magnitude = 1.0 if current_category.total_spent > 0 else 0.0
        
        # Determine importance level
        significance = self._determine_significance(deviation_magnitude)
        
        # Generate explanation and recommendation
        explanation = self._generate_deviation_explanation(
            historical_pattern, current_category, deviation_type, deviation_magnitude
        )
        recommendation = self._generate_recommendation(
            deviation_type, deviation_magnitude, significance
        )
        
        return DeviationAnalysis(
            category_id=current_category.category_id,
            category_name=current_category.category_name,
            deviation_type=deviation_type,
            deviation_magnitude=deviation_magnitude,
            significance=significance,
            current_amount=current_category.total_spent,
            historical_average=historical_average,
            current_ratio=current_category.spend_ratio,
            historical_ratio=historical_pattern.average_ratio,
            explanation=explanation,
            recommendation=recommendation
        )
    
    def _create_missing_category_deviation(self, historical_pattern) -> DeviationAnalysis:
        """Create deviation analysis for missing category"""
        return DeviationAnalysis(
            category_id=historical_pattern.category_id,
            category_name=historical_pattern.category_name,
            deviation_type=DeviationType.MISSING_CATEGORY,
            deviation_magnitude=1.0,
            significance=SignificanceLevel.HIGH,
            current_amount=Decimal("0"),
            historical_average=historical_pattern.average_amount,
            current_ratio=0.0,
            historical_ratio=historical_pattern.average_ratio,
            explanation=f"This week there was no spending on{historical_pattern.category_name} spending, while the usual average spending is{historical_pattern.average_amount}",
            recommendation="Check if related consumption records were missed, or if such spending was indeed reduced"
        )
    
    def _create_new_category_deviation(self, current_category: CategorySpendSummary) -> DeviationAnalysis:
        """Create deviation analysis for new category"""
        return DeviationAnalysis(
            category_id=current_category.category_id,
            category_name=current_category.category_name,
            deviation_type=DeviationType.NEW_CATEGORY,
            deviation_magnitude=1.0,
            significance=SignificanceLevel.MEDIUM,
            current_amount=current_category.total_spent,
            historical_average=Decimal("0"),
            current_ratio=current_category.spend_ratio,
            historical_ratio=0.0,
            explanation=f"This week added new{current_category.category_name} spending with amount{current_category.total_spent}",
            recommendation="This is a new spending category, suggest monitoring if it will affect overall budget"
        )
    
    def _determine_significance(self, deviation_magnitude: float) -> SignificanceLevel:
        """Determine deviation importance level"""
        if deviation_magnitude >= self.significance_thresholds["high"]:
            return SignificanceLevel.HIGH
        elif deviation_magnitude >= self.significance_thresholds["medium"]:
            return SignificanceLevel.MEDIUM
        else:
            return SignificanceLevel.LOW
    
    def _generate_deviation_explanation(
        self,
        historical_pattern,
        current_category: CategorySpendSummary,
        deviation_type: DeviationType,
        deviation_magnitude: float
    ) -> str:
        """Generate deviation explanation"""
        percentage = deviation_magnitude * 100
        
        if deviation_type == DeviationType.INCREASE:
            return (f"This week in{current_category.category_name} spending increased by{percentage:.1f}%，"
                   f"from the usual{historical_pattern.average_amount}increased to{current_category.total_spent}")
        elif deviation_type == DeviationType.DECREASE:
            return (f"This week in{current_category.category_name} spending decreased by{percentage:.1f}%，"
                   f"from the usual{historical_pattern.average_amount}decreased to{current_category.total_spent}")
        else:
            return f"This week in{current_category.category_name} spending is basically consistent with usual"
    
    def _generate_recommendation(
        self,
        deviation_type: DeviationType,
        deviation_magnitude: float,
        significance: SignificanceLevel
    ) -> str:
        """Generate recommendation"""
        if significance == SignificanceLevel.HIGH:
            if deviation_type == DeviationType.INCREASE:
                return "Suggest monitoring the reasons for this spending increase, consider if budget adjustment is needed"
            elif deviation_type == DeviationType.DECREASE:
                return "Spending reduction is good, consider using saved funds for other goals"
        elif significance == SignificanceLevel.MEDIUM:
            if deviation_type == DeviationType.INCREASE:
                return "Moderately monitor such spending changes, maintain budget balance"
            elif deviation_type == DeviationType.DECREASE:
                return "Spending reduction helps budget control, continue maintaining"
        
        return "Consumption pattern is normal, continue maintaining"
    
    def _generate_overall_assessment(
        self,
        deviations: List[DeviationAnalysis],
        significant_changes: int
    ) -> str:
        """Generate overall assessment"""
        if significant_changes == 0:
            return "This week consumption pattern is basically consistent with historical pattern, no significant changes"
        elif significant_changes <= 2:
            return f"This week consumption pattern has{significant_changes} significant changes, overall pattern relatively stable"
        else:
            return f"This week consumption pattern has{significant_changes} significant changes, suggest monitoring overall consumption trends"
    
    def _calculate_comparison_confidence(
        self,
        pattern_confidence: float,
        deviation_count: int,
        significant_changes: int
    ) -> float:
        """Calculate comparison confidence"""
        # Based onPattern confidence
        base_confidence = pattern_confidence
        
        # Adjustment based on deviation count
        deviation_factor = max(0.5, 1.0 - (deviation_count * 0.1))
        
        # Adjustment based on significant changes
        change_factor = max(0.7, 1.0 - (significant_changes * 0.05))
        
        # Comprehensive confidence
        confidence = base_confidence * deviation_factor * change_factor
        
        return min(confidence, 1.0)
    
    def has_significant_changes(self, comparison: PatternComparison) -> bool:
        """Check if there are significant changes"""
        return comparison.significant_changes_count > 0
    
    def get_top_deviations(
        self, 
        comparison: PatternComparison, 
        limit: int = 3
    ) -> List[DeviationAnalysis]:
        """Get most important deviation"""
        # Sort by importance and deviation degree
        sorted_deviations = sorted(
            comparison.deviations,
            key=lambda d: (
                d.significance.value == "high",
                d.significance.value == "medium", 
                d.deviation_magnitude
            ),
            reverse=True
        )
        
        return sorted_deviations[:limit]
