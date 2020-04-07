package com.blameo.domain.usecases.channels

import com.blameo.data.models.results.GetChannelResult
import com.blameo.data.repositories.ChannelRemoteRepository
import com.blameo.domain.usecases.NoInputUseCase
import io.reactivex.Single
import javax.inject.Inject

class GetChannelRemoteUseCase @Inject constructor(private val channelRemoteRepository: ChannelRemoteRepository) :

    NoInputUseCase<Single<GetChannelResult>> {
    override fun execute(): Single<GetChannelResult> {
        return channelRemoteRepository.getChannels()
    }
}




