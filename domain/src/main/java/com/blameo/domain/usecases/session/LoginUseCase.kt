package com.blameo.domain.usecases.session

import com.blameo.data.models.pojos.Me
import com.blameo.data.repositories.SessionRepository
import io.reactivex.Single
import com.blameo.domain.usecases.UseCase
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val sessionRepository: SessionRepository):
    UseCase<String, Single<Me>> {
    override fun execute(params: String?): Single<Me> {
        return sessionRepository.login(params!!)
    }

}