package com.blameo.domain.usecases

interface UseCase<P, R> {
    fun execute(params: P?): R
}