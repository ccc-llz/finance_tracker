from typing import List, Optional
from decimal import Decimal
from models import Transaction, Category, ConsumptionAnalysisResult, PeriodType
from analysis.enhanced_analysis import EnhancedAnalysisEngine, analyze_weekly_consumption_with_history
from .client import client
from .prompts import build_simple_analysis_prompt, build_historical_comparison_prompt, get_ai_instructions


# ---------------------------
# Function: generate AI insights (simplified for frontend)
# ---------------------------
def generate_ai_insights(analysis: ConsumptionAnalysisResult) -> str:
    """Generate AI insights from ConsumptionAnalysisResult (legacy support)"""
    # Step 1: Build prompt using the prompts module
    prompt = build_simple_analysis_prompt(_convert_to_simple_format(analysis))
    instructions = get_ai_instructions()

    # Step 2: Call the Chat Completions API
    try:
        response = client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[
                {"role": "system", "content": instructions},
                {"role": "user", "content": prompt}
            ]
        )

        # Step 3: Extract text output
        ai_message = response.choices[0].message.content
        if not ai_message:
            ai_message = "AI did not return a valid response."
    except Exception as e:
        ai_message = f"AI call failed: {str(e)}"

    return ai_message


def generate_consumption_insight(transactions: List[Transaction], categories: List[Category]) -> str:
    """
    Generate consumption insight directly from transactions and categories
    Main function for frontend usage (simple version)
    """
    # Check if there are any transactions
    if not transactions or len(transactions) == 0:
        return "You don't have any transactions yet. Start your first record!"
    
    # Step 1: Simple consumption data analysis
    analysis = _analyze_consumption_simple(transactions, categories)

    # Step 2: Build AI prompt
    prompt = build_simple_analysis_prompt(analysis)

    # Step 3: Call AI to generate evaluation
    try:
        response = client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[
                {"role": "system", "content": get_ai_instructions()},
                {"role": "user", "content": prompt}
            ]
        )

        return response.choices[0].message.content or "AI did not return a valid response."

    except Exception as e:
        return f"AI analysis failed: {str(e)}"


def generate_historical_consumption_insight(
        user_id: int,
        current_transactions: List[Transaction],
        historical_transactions: List[Transaction],
        categories: List[Category],
        period_type: PeriodType = PeriodType.WEEKLY
) -> str:
    """
    Generate consumption insight based on historical patterns
    Main function for intelligent historical analysis

    Args:
        user_id: User ID
        current_transactions: Current period transactions
        historical_transactions: Historical transactions for learning
        categories: Category data
        period_type: Analysis period type

    Returns:
        str: AI-generated text evaluation based on historical patterns
    """
    try:
        # Step 1: Enhanced analysis with historical context
        comparison = analyze_weekly_consumption_with_history(
            user_id=user_id,
            current_week_transactions=current_transactions,
            historical_transactions=historical_transactions,
            categories=categories
        )

        # Step 2: Build historical comparison prompt
        prompt = build_historical_comparison_prompt(comparison)

        # Step 3: Call AI to generate evaluation
        response = client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[
                {"role": "system", "content": get_ai_instructions()},
                {"role": "user", "content": prompt}
            ]
        )

        return response.choices[0].message.content or "AI did not return a valid response."

    except Exception as e:
        return f"Historical analysis failed: {str(e)}"


def _analyze_consumption_simple(transactions: List[Transaction], categories: List[Category]) -> dict:
    """Simple consumption analysis for frontend usage"""
    # Calculate total spending
    total_spent = sum(txn.amount for txn in transactions)

    # Group by category
    category_spending = {}
    category_map = {cat.id: cat.name for cat in categories}

    for txn in transactions:
        if txn.category_id in category_map:
            cat_name = category_map[txn.category_id]
            if cat_name not in category_spending:
                category_spending[cat_name] = Decimal("0")
            category_spending[cat_name] += txn.amount

    # Create category summary
    category_summary = []
    for cat_name, amount in category_spending.items():
        ratio = float(amount / total_spent) if total_spent > 0 else 0
        category_summary.append({
            "name": cat_name,
            "amount": amount,
            "ratio": ratio
        })

    # Sort by amount descending
    category_summary.sort(key=lambda x: x["amount"], reverse=True)

    # Detect anomalies (transactions > 2x average)
    avg_transaction = total_spent / len(transactions) if transactions else Decimal("0")
    has_anomalies = any(txn.amount > avg_transaction * 2 for txn in transactions)

    return {
        "total_spent": total_spent,
        "transaction_count": len(transactions),
        "category_summary": category_summary,
        "has_anomalies": has_anomalies
    }


def _convert_to_simple_format(analysis: ConsumptionAnalysisResult) -> dict:
    """Convert ConsumptionAnalysisResult to simple format for prompt"""
    category_summary = []
    for cat in analysis.category_summary:
        category_summary.append({
            "name": cat.category_name,
            "amount": cat.total_spent,
            "ratio": cat.spend_ratio
        })

    return {
        "total_spent": analysis.total_spent,
        "transaction_count": len(analysis.category_summary),
        "category_summary": category_summary,
        "has_anomalies": len(analysis.anomalies) > 0
    }


# ---------------------------
# Example usage (for testing with real database)
# ---------------------------
if __name__ == "__main__":
    """
    Note: This module is designed to be used through the API endpoint.
    For testing, use the API endpoint with real database queries.
    Example: GET /api/v1/analyze/consumption?user_id=1&start_date=2024-01-01&end_date=2024-01-31
    """
    print("AI Insight module loaded. Use the API endpoint for testing.")