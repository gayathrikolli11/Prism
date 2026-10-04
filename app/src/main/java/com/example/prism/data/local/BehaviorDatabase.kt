package com.example.prism.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.prism.domain.model.BehaviorEvent
import com.example.prism.domain.model.Interest

@Database(
    entities = [BehaviorEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(BehaviorConverters::class)
abstract class BehaviorDatabase : RoomDatabase() {
    abstract fun behaviorDao(): BehaviorDao

    companion object {
        const val DATABASE_NAME = "prism_behavior.db"
    }
}

class BehaviorConverters {
    @TypeConverter
    fun fromInterest(interest: Interest): String = interest.name

    @TypeConverter
    fun toInterest(value: String): Interest =
        runCatching { Interest.valueOf(value) }.getOrDefault(Interest.NONE)

    @TypeConverter
    fun fromBehaviorEvent(event: BehaviorEvent): String = event.name

    @TypeConverter
    fun toBehaviorEvent(value: String): BehaviorEvent =
        runCatching { BehaviorEvent.valueOf(value) }.getOrDefault(BehaviorEvent.CLICK)
}