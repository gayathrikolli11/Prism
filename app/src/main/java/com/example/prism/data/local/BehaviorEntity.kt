package com.example.prism.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.prism.domain.model.BehaviorEvent
import com.example.prism.domain.model.Interest

@Entity(tableName = "behavior_events")
data class BehaviorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contentId: String,
    val interest: Interest,
    val eventType: BehaviorEvent,
    val durationMs: Long,
    val timestamp: Long
)