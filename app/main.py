# main.py
from fastapi import FastAPI, Query
from fastapi.middleware.cors import CORSMiddleware
import uvicorn
from typing import List
from datetime import date, timedelta
import os

from models import Transaction, Category, PeriodType
from ai.insight import generate_historical_consumption_insight
from db import get_transactions_for_user, get_categories_for_user

app = FastAPI(title="Finance Tracker AI Analysis")
cors_urls = os.getenv("CORS_DOMAINS", "http://localhost:5173")

# Add CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=cors_urls.split(","),  # Frontend URLs
    allow_credentials=True,
    allow_methods=["*"],  # Allow all methods
    allow_headers=["*"],  # Allow all headers
)

# ---------------------------
# API route: analyze consumption with date range
# ---------------------------
@app.get("/api/v1/analyze/consumption")
def analyze_user_consumption(
    user_id: int = Query(..., description="User ID"),
    start_date: date = Query(..., description="Start date (YYYY-MM-DD)"),
    end_date: date = Query(..., description="End date (YYYY-MM-DD)")
):
    """
    Analyze user consumption and generate AI insights
    
    Queries transactions and categories from database and generates AI insights
    """
    try:
        # Query current period transactions and categories from database
        current_transactions = get_transactions_for_user(user_id, start_date, end_date)
        categories = get_categories_for_user(user_id)

        # Check if there are any current transactions
        if not current_transactions or len(current_transactions) == 0:
            # Return fallback message if no transactions
            return {
                "ai_insights": "You don't have any transactions yet. Start your first record!",
                "transaction_count": 0,
                "date_range": {
                    "start_date": start_date.isoformat(),
                    "end_date": end_date.isoformat()
                }
            }

        # Calculate historical period: 90 days before the start_date
        historical_end_date = start_date - timedelta(days=1)
        historical_start_date = historical_end_date - timedelta(days=90)
        
        # Query historical transactions for pattern learning
        historical_transactions = get_transactions_for_user(
            user_id, 
            historical_start_date, 
            historical_end_date
        )

        # Generate AI insights using historical pattern analysis
        insights_text = generate_historical_consumption_insight(
            user_id=user_id,
            current_transactions=current_transactions,
            historical_transactions=historical_transactions,
            categories=categories,
            period_type=PeriodType.WEEKLY
        )

        # Return AI insights
        return {
            "ai_insights": insights_text,
            "transaction_count": len(current_transactions),
            "historical_transaction_count": len(historical_transactions),
            "date_range": {
                "start_date": start_date.isoformat(),
                "end_date": end_date.isoformat()
            },
            "historical_date_range": {
                "start_date": historical_start_date.isoformat(),
                "end_date": historical_end_date.isoformat()
            },
            "analysis_mode": "historical"
        }
    except Exception as e:
        # Return error response
        return {
            "ai_insights": f"Error generating insights: {str(e)}",
            "transaction_count": 0,
            "date_range": {
                "start_date": start_date.isoformat(),
                "end_date": end_date.isoformat()
            },
            "error": str(e)
        }

@app.get("/health")
def health_check():
    """Health check endpoint for Docker"""
    return {"status": "healthy"}

# ---------------------------
# Start server
# ---------------------------
if __name__ == "__main__":
    print("🚀 Starting FastAPI server at http://127.0.0.1:8000 ...")
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)