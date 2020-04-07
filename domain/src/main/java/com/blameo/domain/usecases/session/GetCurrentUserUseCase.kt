package com.blameo.domain.usecases.session

import com.blameo.data.models.pojos.Me
import com.blameo.data.repositories.SessionRepository
import io.reactivex.Single
import com.blameo.domain.usecases.NoInputUseCase
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(private val sessionRepository: SessionRepository):
    NoInputUseCase<Single<Me>> {
    override fun execute(): Single<Me> {
        return Single.just(sessionRepository.currentSession())
    }

}