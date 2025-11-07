"""
AI module for consumption analysis
Provides AI-powered insights and analysis
"""

from .prompts import (
    build_simple_analysis_prompt, get_ai_instructions
)
from .client import client
from .insight import generate_ai_insights, generate_consumption_insight

__all__ = [
    # Prompt functions
    "build_simple_analysis_prompt",
    "get_ai_instructions",

    # AI functions
    "generate_ai_insights",
    "generate_consumption_insight",

    # Client
    "client"
]