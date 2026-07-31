package com.mabrouk.recall.core.model

/**
 * Origin of a note's content. Capture paths for OCR/VOICE/PDF land later;
 * the enum is reserved so persistence and UI can branch early.
 */
enum class SourceType {
    TEXT,
    OCR,
    VOICE,
    PDF,
}
