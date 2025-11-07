# test_pattern_learning.py
"""
Pattern learning functionality test and usage example
"""
from datetime import date, datetime, timedelta
from decimal import Decimal
from typing import List

from models import (
    Transaction, Category, TransactionType, PeriodType,
    ConsumptionPattern, PatternComparison
)
from analysis.enhanced_analysis import (
    EnhancedAnalysisEngine, analyze_weekly_consumption_with_history,
    learn_user_pattern, compare_patterns
)


def create_sample_data():
    """Create sample data"""
    # Create categories
    categories = [
        Category(id=1, name="Food", transaction_type=TransactionType.EXPENSE),
        Category(id=2, name="Entertainment", transaction_type=TransactionType.EXPENSE),
        Category(id=3, name="Shopping", transaction_type=TransactionType.EXPENSE),
        Category(id=4, name="Transport", transaction_type=TransactionType.EXPENSE),
    ]
    
    # Create historical transaction data (past 3 months)
    historical_transactions = []
    base_date = date.today() - timedelta(days=90)
    
    # Simulate historical consumption patterns
    for i in range(60):  # 60 historical transactions
        transaction_date = base_date + timedelta(days=i*1.5)
        
        # Simulate different consumption patterns
        if i % 4 == 0:  # Food
            historical_transactions.append(Transaction(
                id=i+1,
                name=f"Food purchase {i+1}",
                type=TransactionType.EXPENSE,
                amount=Decimal("25.00"),
                transaction_date=transaction_date,
                category_id=1,
                ledger_id=1
            ))
        elif i % 4 == 1:  # Entertainment
            historical_transactions.append(Transaction(
                id=i+1,
                name=f"Entertainment {i+1}",
                type=TransactionType.EXPENSE,
                amount=Decimal("50.00"),
                transaction_date=transaction_date,
                category_id=2,
                ledger_id=1
            ))
        elif i % 4 == 2:  # Shopping
            historical_transactions.append(Transaction(
                id=i+1,
                name=f"Shopping {i+1}",
                type=TransactionType.EXPENSE,
                amount=Decimal("100.00"),
                transaction_date=transaction_date,
                category_id=3,
                ledger_id=1
            ))
        else:  # Transport
            historical_transactions.append(Transaction(
                id=i+1,
                name=f"Transport {i+1}",
                type=TransactionType.EXPENSE,
                amount=Decimal("15.00"),
                transaction_date=transaction_date,
                category_id=4,
                ledger_id=1
            ))
    
    # Create this week transaction data (with changes)
    current_week_transactions = [
        Transaction(
            id=101,
            name="Restaurant dinner",
            type=TransactionType.EXPENSE,
            amount=Decimal("80.00"),  # higher than usual
            transaction_date=date.today() - timedelta(days=6),
            category_id=1,
            ledger_id=1
        ),
        Transaction(
            id=102,
            name="Movie tickets",
            type=TransactionType.EXPENSE,
            amount=Decimal("30.00"),  # lower than usual
            transaction_date=date.today() - timedelta(days=5),
            category_id=2,
            ledger_id=1
        ),
        Transaction(
            id=103,
            name="Online shopping",
            type=TransactionType.EXPENSE,
            amount=Decimal("150.00"),  # higher than usual
            transaction_date=date.today() - timedelta(days=3),
            category_id=3,
            ledger_id=1
        ),
        Transaction(
            id=104,
            name="New category - Health",
            type=TransactionType.EXPENSE,
            amount=Decimal("60.00"),  # new category
            transaction_date=date.today() - timedelta(days=2),
            category_id=5,  # new categoryID
            ledger_id=1
        ),
    ]
    
    # Add new category
    categories.append(Category(id=5, name="Health", transaction_type=TransactionType.EXPENSE))
    
    return categories, historical_transactions, current_week_transactions


def test_pattern_learning():
    """Test pattern learning functionality"""
    print("=== Test pattern learning functionality ===")
    
    categories, historical_transactions, current_week_transactions = create_sample_data()
    
    # Learn historical patterns
    print("1. Learn historical consumption patterns...")
    pattern = learn_user_pattern(
        user_id=123,
        historical_transactions=historical_transactions,
        categories=categories,
        period_type=PeriodType.WEEKLY
    )
    
    print(f"Learning completed!")
    print(f"- Data points count: {pattern.data_points_count}")
    print(f"- Pattern confidence: {pattern.pattern_confidence:.1%}")
    print(f"- Category count: {len(pattern.category_patterns)}")
    
    # Show main category patterns
    print("\nMain category patterns:")
    for cat_id, cat_pattern in pattern.category_patterns.items():
        print(f"- {cat_pattern.category_name}: "
              f"Average {cat_pattern.average_amount}, "
              f"Frequency {cat_pattern.frequency:.1%}, "
              f"Trend {cat_pattern.trend}")
    
    return pattern


def test_pattern_comparison():
    """Test pattern comparison functionality"""
    print("\n=== Test pattern comparison functionality ===")
    
    categories, historical_transactions, current_week_transactions = create_sample_data()
    
    # Perform complete analysis
    print("1. Perform enhanced analysis...")
    comparison = analyze_weekly_consumption_with_history(
        user_id=123,
        current_week_transactions=current_week_transactions,
        historical_transactions=historical_transactions,
        categories=categories
    )
    
    print(f"Analysis completed!")
    print(f"- Found {len(comparison.deviations)}  changes")
    print(f"- Significant changes: {comparison.significant_changes_count}  ")
    print(f"- Comparison confidence: {comparison.comparison_confidence:.1%}")
    
    # Show main changes
    print("\nMain changes:")
    for deviation in comparison.deviations:
        print(f"- {deviation.category_name}: {deviation.deviation_type.value}")
        print(f"  Change degree: {deviation.deviation_magnitude:.1%}")
        print(f"  Importance: {deviation.significance.value}")
        print(f"  Explanation: {deviation.explanation}")
        if deviation.recommendation:
            print(f"  Recommendation: {deviation.recommendation}")
        print()
    
    return comparison


def test_enhanced_analysis_engine():
    """Test enhanced analysis engine"""
    print("\n=== Test enhanced analysis engine ===")
    
    categories, historical_transactions, current_week_transactions = create_sample_data()
    
    # Create analysis engine
    engine = EnhancedAnalysisEngine()
    
    # Perform complete analysis
    comparison = engine.analyze_with_historical_context(
        user_id=123,
        current_transactions=current_week_transactions,
        historical_transactions=historical_transactions,
        categories=categories,
        period_type=PeriodType.WEEKLY
    )
    
    # Get summary
    print("Learning summary:")
    print(engine.get_learning_summary(comparison.historical_pattern))
    
    print("\nComparison summary:")
    print(engine.get_comparison_summary(comparison))
    
    # Check if pattern needs updating
    should_update = engine.should_update_pattern(
        comparison.historical_pattern,
        len(current_week_transactions)
    )
    print(f"\nNeed to update pattern: {should_update}")
    
    return comparison


if __name__ == "__main__":
    print("Start testing pattern learning functionality...\n")
    
    try:
        # Test pattern learning
        pattern = test_pattern_learning()
        
        # Test pattern comparison
        comparison = test_pattern_comparison()
        
        # Test enhanced analysis engine
        enhanced_comparison = test_enhanced_analysis_engine()
        
        print("\n=== Test completed ===")
        print("All functionality tests passed!")
        
    except Exception as e:
        print(f"Test failed: {e}")
        import traceback
        traceback.print_exc()
