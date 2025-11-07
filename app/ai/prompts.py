"""
AI prompt generator
Supports simple consumption analysis and historical pattern comparison for frontend usage
"""
from typing import List


def build_simple_analysis_prompt(analysis: dict) -> str:
    """Build simple analysis prompt"""
    prompt_parts = []
    
    # Total spending information
    prompt_parts.append(f"Total spending: ${analysis['total_spent']}")
    prompt_parts.append(f"Transaction count: {analysis['transaction_count']}")
    prompt_parts.append("")

    # Category details
    if analysis['category_summary']:
        prompt_parts.append("Spending categories:")
        for cat in analysis['category_summary'][:5]:  # Show only top 5
            prompt_parts.append(f"- {cat['name']}: ${cat['amount']} ({cat['ratio']*100:.1f}%)")
    
    # Anomaly alert
    if analysis['has_anomalies']:
        prompt_parts.append("")
        prompt_parts.append("Detected unusual large transactions")
    
    # Analysis instructions
    prompt_parts.append("")
    prompt_parts.append(
        "Please comment on user's consumption behavior using 2-3 short sentences "
        "in a warm and cute tone. Focus on spending categories, total amount, and anomalies."
    )
    
    return "\n".join(prompt_parts)


def build_historical_comparison_prompt(comparison) -> str:
    """Build historical comparison prompt"""
    prompt_parts = []
    
    # Current period summary (same format as simple analysis)
    current = comparison.current_period
    prompt_parts.append(f"Total spending: ${current.total_spent}")
    prompt_parts.append(f"Transaction count: {len(current.category_summary)}")
    prompt_parts.append("")

    # Category details (same format as simple analysis)
    if current.category_summary:
        prompt_parts.append("Spending categories:")
        for cat in current.category_summary[:5]:  # Show only top 5
            prompt_parts.append(f"- {cat.category_name}: ${cat.total_spent} ({cat.spend_ratio*100:.1f}%)")
    
    # Historical context (brief)
    historical = comparison.historical_pattern
    prompt_parts.append("")
    prompt_parts.append(f"Historical average: ${historical.total_average_spent} (confidence: {historical.pattern_confidence:.1%})")
    
    # Key changes (brief)
    if comparison.deviations:
        significant_deviations = [
            d for d in comparison.deviations 
            if d.significance.value in ["high", "medium"]
        ]
        if significant_deviations:
            prompt_parts.append("")
            prompt_parts.append("Key changes:")
            for deviation in significant_deviations[:2]:  # Show only top 2
                prompt_parts.append(f"- {deviation.category_name}: {deviation.deviation_type.value} by {deviation.deviation_magnitude:.1%}")
    
    # Analysis instructions
    prompt_parts.append("")
    prompt_parts.append(
        "Please comment on user's consumption behavior using 2-3 short sentences "
        "in a warm and cute tone. Focus on spending categories, total amount, and key changes compared to historical "
        "patterns."
    )
    
    return "\n".join(prompt_parts)


def get_ai_instructions() -> str:
    """Get AI system instructions"""
    return (
        "You are a warm and cute financial agent. Analyze users' consumption habits "
        "in a friendly and caring tone, providing short and practical advice. Use emojis "
        "to make the tone more cute, and keep sentences natural and fluent."
    )
