package com.pndnwngi.mysalesmanship.di

import com.pndnwngi.mysalesmanship.data.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPullRepository(
        pullRepositoryImpl: PullRepositoryImpl
    ): PullRepository

    @Binds
    @Singleton
    abstract fun bindOutletRepository(
        outletRepositoryImpl: OutletRepositoryImpl
    ): OutletRepository

    @Binds
    @Singleton
    abstract fun bindVisitRepository(
        visitRepositoryImpl: VisitRepositoryImpl
    ): VisitRepository

    @Binds
    @Singleton
    abstract fun bindCheckStockRepository(
        checkStockRepositoryImpl: CheckStockRepositoryImpl
    ): CheckStockRepository

    @Binds
    @Singleton
    abstract fun bindSalesRepository(
        salesRepositoryImpl: SalesRepositoryImpl
    ): SalesRepository

    @Binds
    @Singleton
    abstract fun bindSyncRepository(
        syncRepositoryImpl: SyncRepositoryImpl
    ): SyncRepository

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository

    @Binds
    @Singleton
    abstract fun bindTargetRepository(
        targetRepositoryImpl: TargetRepositoryImpl
    ): TargetRepository
}
