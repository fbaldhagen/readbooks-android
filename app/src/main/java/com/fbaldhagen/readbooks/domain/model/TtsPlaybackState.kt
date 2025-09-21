package com.fbaldhagen.readbooks.domain.model

enum class TtsPlaybackState {
    IDLE,
    PLAYING,
    PAUSED,
    BUFFERING,
    FINISHED,
    ERROR
}