# main_api.py
"""
Main API interface for AI consumption analysis
Supports both simple analysis and historical pattern-based analysis
"""
from typing import List, Optional
from models import Transaction, Category, PeriodType
from ai.insight import generate_consumption_insight, generate_historical_consumption_insight


def generate_ai_insight_for_frontend(
    transactions: List[Transaction],
    categories: List[Category]
) -> str:
    """
    Generate AI consumption insight for frontend (simple version)
    
    Args:
        transactions: List of transaction data
        categories: List of category data
        
    Returns:
        str: AI-generated text evaluation
    """
    try:
        # Call simplified AI insight generator
        insight = generate_consumption_insight(transactions, categories)
        return insight
        
    except Exception as e:
        return f"Error generating AI insight: {str(e)}"


def generate_historical_ai_insight_for_frontend(
    user_id: int,
    current_transactions: List[Transaction],
    historical_transactions: List[Transaction],
    categories: List[Category],
    period_type: PeriodType = PeriodType.WEEKLY
) -> str:
    """
    Generate AI consumption insight based on historical patterns
    
    Args:
        user_id: User ID
        current_transactions: Current period transactions
        historical_transactions: Historical transactions for learning
        categories: List of category data
        period_type: Analysis period type
        
    Returns:
        str: AI-generated text evaluation based on historical patterns
    """
    try:
        # Call historical AI insight generator
        insight = generate_historical_consumption_insight(
            user_id=user_id,
            current_transactions=current_transactions,
            historical_transactions=historical_transactions,
            categories=categories,
            period_type=period_type
        )
        return insight
        
    except Exception as e:
        return f"Error generating historical AI insight: {str(e)}"


# Note: For testing, use the API endpoint with real database queries
# Example: GET /api/v1/analyze/consumption?user_id=1&start_date=2024-01-01&end_date=2024-01-31
