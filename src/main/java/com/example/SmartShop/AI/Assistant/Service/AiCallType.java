package com.example.SmartShop.AI.Assistant.Service;

/**
 * Routes OpenRouter calls to the correct system prompt and sampling settings.
 * STRUCTURED must stay machine-parseable (JSON). ASSISTANT is natural language only.
 * SUGGEST is comma-separated catalog titles only (no prose).
 */
public enum AiCallType {
    STRUCTURED,
    ASSISTANT,
    SUGGEST
}
