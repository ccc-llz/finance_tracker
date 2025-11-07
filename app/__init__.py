# __init__.py
"""
Enhanced AI consumption behavior analysis system

This is an enhanced consumption behavior analysis system that can:
1. Learn user historical consumption patterns
2. Compare current consumption with historical patterns
3. Generate personalized AI insights

Main modules:
- models: Data model definitions
- analysis: Analysis engines
- ai: AI-related functionality
- utils: Utility functions
- tests: Test files
"""

# Import main functionality for easy access
from main_api import generate_ai_insight_for_frontend
from models import Transaction, Category, TransactionType

__version__ = "1.0.0"
__author__ = "ELEC5619 Group 1"

__all__ = [
    # Main API function
    "generate_ai_insight_for_frontend",
    
    # Core models
    "Transaction", "Category", "TransactionType"
]
