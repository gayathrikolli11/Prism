package com.example.prism.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.prism.domain.model.Interest

@Dao
interface BehaviorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBehavior(entity: BehaviorEntity)

    @Query("""
        SELECT interest, eventType, COUNT(*) as count, AVG(durationMs) as avgDuration
        FROM behavior_events
        WHERE timestamp > :since
        GROUP BY interest, eventType
    """)
    suspend fun getBehaviorStats(since: Long): List<BehaviorStats>

    @Query("SELECT COUNT(*) FROM behavior_events WHERE timestamp > :since")
    suspend fun getTotalEventCount(since: Long): Int

    @Query("DELETE FROM behavior_events WHERE timestamp < :before")
    suspend fun pruneOldEvents(before: Long)
}

data class BehaviorStats(
    val interest: Interest,
    val eventType: String,
    val count: Int,
    val avgDuration: Double
)