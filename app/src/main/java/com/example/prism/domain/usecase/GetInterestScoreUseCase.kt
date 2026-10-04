package com.example.prism.domain.usecase

import com.example.prism.data.repository.UserInterestRepository
import com.example.prism.domain.model.Interest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInterestScoresUseCase @Inject constructor(
    private val userInterestRepository: UserInterestRepository
) {
    operator fun invoke(): Flow<Map<Interest, Float>> = userInterestRepository.interestScores
}