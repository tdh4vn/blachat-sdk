package com.blameo.domain.usecases.channels

import android.util.Log
import com.blameo.data.models.results.GetMessageByIDResult
import com.blameo.data.repositories.MessageRemoteRepository
import com.blameo.domain.usecases.UseCase
import io.reactivex.Single
import javax.inject.Inject

class GetMessageByIdUseCase @Inject constructor(private val messageRemoteRepository: MessageRemoteRepository) :

    UseCase<String, Single<GetMessageByIDResult>> {
    override fun execute(params: String?): Single<GetMessageByIDResult> {
        Log.e("UC", "id: $params")
        return messageRemoteRepository.getMessageById(params!!)
    }
}




