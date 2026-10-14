package com.example.assignment2.di

import com.example.assignment2.data.repository.AuthRepository
import com.example.assignment2.data.repository.AuthRepositoryImpl
import com.example.assignment2.data.repository.EntityRepository
import com.example.assignment2.data.repository.EntityRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// the viewmodels ask for the interfaces (AuthRepository, EntityRepository).
// @Binds tells Hilt which class to give them when that happens.
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindEntityRepository(impl: EntityRepositoryImpl): EntityRepository
}
