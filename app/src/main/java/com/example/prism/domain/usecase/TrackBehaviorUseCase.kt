package com.example.prism.domain.usecase

import com.example.prism.data.local.BehaviorDao
import com.example.prism.data.local.BehaviorEntity
import com.example.prism.data.repository.UserInterestRepository
import com.example.prism.domain.model.UserBehavior
import javax.inject.Inject

class TrackBehaviorUseCase @Inject constructor(
    private val behaviorDao: BehaviorDao,
    private val userInterestRepository: UserInterestRepository
) {
    suspend operator fun invoke(behavior: UserBehavior) {
        behaviorDao.insertBehavior(
            BehaviorEntity(
                contentId = behavior.contentId,
                interest = behavior.interest,
                eventType = behavior.eventType,
                durationMs = behavior.durationMs,
                timestamp = behavior.timestamp
            )
        )
        userInterestRepository.recordBehavior(behavior)
    }
}