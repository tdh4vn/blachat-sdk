package com.blameo.domain.usecases.session

import com.blameo.data.repositories.SessionRepository
import io.reactivex.Single
import com.blameo.domain.usecases.NoInputUseCase
import javax.inject.Inject

class CheckLoginUseCase @Inject constructor(private val sessionRepository: SessionRepository)
    : NoInputUseCase<Single<Boolean>> {
    override fun execute(): Single<Boolean> {
        return Single.just(sessionRepository.checkLogin())
    }

}