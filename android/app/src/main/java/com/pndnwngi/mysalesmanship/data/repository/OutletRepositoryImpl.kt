package com.pndnwngi.mysalesmanship.data.repository

import com.pndnwngi.mysalesmanship.data.local.AppDatabase
import com.pndnwngi.mysalesmanship.data.local.entity.MstOutletEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OutletRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : OutletRepository {

    override suspend fun getOutletsByRoute(routeId: Long): List<MstOutletEntity> = withContext(Dispatchers.IO) {
        database.mstOutletDao().getOutletsByRoute(routeId)
    }

    override suspend fun getOutletByBarcode(barcode: String): MstOutletEntity? = withContext(Dispatchers.IO) {
        database.mstOutletDao().getOutletByBarcode(barcode)
    }

    override suspend fun searchOutletsByName(name: String): List<MstOutletEntity> = withContext(Dispatchers.IO) {
        database.mstOutletDao().searchOutletsByName(name)
    }

    override suspend fun getOutletById(serverId: Long): MstOutletEntity? = withContext(Dispatchers.IO) {
        database.mstOutletDao().getOutletById(serverId)
    }

    override suspend fun getAllOutlets(): List<MstOutletEntity> = withContext(Dispatchers.IO) {
        database.mstOutletDao().getAllOutlets()
    }
}
