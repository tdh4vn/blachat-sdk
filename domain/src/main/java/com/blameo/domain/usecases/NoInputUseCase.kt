package com.blameo.domain.usecases

interface NoInputUseCase<R> {
    fun execute(): R
}