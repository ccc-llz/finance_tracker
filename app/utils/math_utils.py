# utils/math_utils.py
from typing import List, Dict, Any
from decimal import Decimal
import statistics
import math

from models import CategorySpendSummary, CategoryPattern


def calculate_statistical_metrics(values: List[Decimal]) -> Dict[str, Any]:
    """Calculate statistical metrics for a list of values."""
    if not values:
        return {
            "mean": Decimal("0"),
            "median": Decimal("0"),
            "std": Decimal("0"),
            "variance": Decimal("0"),
            "min": Decimal("0"),
            "max": Decimal("0"),
            "count": 0
        }
    
    mean_val = sum(values) / len(values)
    median_val = statistics.median(values)
    
    if len(values) > 1:
        variance_val = statistics.variance(values)
        std_val = statistics.stdev(values)
    else:
        variance_val = Decimal("0")
        std_val = Decimal("0")
    
    return {
        "mean": mean_val,
        "median": median_val,
        "std": std_val,
        "variance": variance_val,
        "min": min(values),
        "max": max(values),
        "count": len(values)
    }


def compress_category_data(category_summary: List[CategorySpendSummary]) -> str:
    """Compress category data for AI prompt optimization."""
    if not category_summary:
        return "No categories"
    
    compressed = []
    for cat in category_summary:
        compressed.append(f"{cat.category_name[:3]}:{cat.spend_ratio:.1%}")
    
    return "|".join(compressed)


def format_pattern_summary(pattern_data: Dict[int, CategoryPattern]) -> str:
    """Format pattern data for AI prompt optimization."""
    if not pattern_data:
        return "No patterns"
    
    formatted = []
    for cat_pattern in pattern_data.values():
        formatted.append(
            f"{cat_pattern.category_name[:3]}:"
            f"{cat_pattern.average_amount:.0f}:"
            f"{cat_pattern.frequency:.1%}:"
            f"{cat_pattern.trend[:3]}"
        )
    
    return "|".join(formatted)


def calculate_statistical_threshold(
    amounts: List[Decimal], 
    multiplier: float = 2.0,
    min_threshold: Decimal = Decimal("500"),
    max_threshold: Decimal = Decimal("10000")
) -> Decimal:
    """
    Calculate statistical threshold for anomaly detection using mean + multiplier * standard deviation.
    
    Args:
        amounts: List of transaction amounts
        multiplier: Standard deviation multiplier (default 2.0 for 95% coverage)
        min_threshold: Minimum threshold value
        max_threshold: Maximum threshold value
        
    Returns:
        Calculated threshold value
    """
    if len(amounts) < 3:
        # Not enough data for statistical analysis
        return min_threshold
    
    # Calculate mean
    mean_amount = sum(amounts) / len(amounts)
    
    # Calculate variance and standard deviation
    variance = sum((amount - mean_amount) ** 2 for amount in amounts) / len(amounts)
    std_deviation = Decimal(str(math.sqrt(float(variance))))
    
    # Calculate threshold: mean + multiplier * standard deviation
    threshold = mean_amount + (std_deviation * Decimal(str(multiplier)))
    
    # Apply bounds
    return max(min_threshold, min(threshold, max_threshold))


def detect_statistical_anomalies(
    amounts: List[Decimal],
    multiplier: float = 2.0,
    min_threshold: Decimal = Decimal("100"),
    max_threshold: Decimal = Decimal("10000")
) -> List[Decimal]:
    """
    Detect statistical anomalies in a list of amounts.
    
    Args:
        amounts: List of transaction amounts
        multiplier: Standard deviation multiplier
        min_threshold: Minimum threshold value
        max_threshold: Maximum threshold value
        
    Returns:
        List of anomalous amounts
    """
    if len(amounts) < 3:
        # Fallback to simple threshold
        threshold = min_threshold
    else:
        threshold = calculate_statistical_threshold(amounts, multiplier, min_threshold, max_threshold)
    
    return [amount for amount in amounts if amount > threshold]
