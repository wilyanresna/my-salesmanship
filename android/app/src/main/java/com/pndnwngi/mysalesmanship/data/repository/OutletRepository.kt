package com.pndnwngi.mysalesmanship.data.repository

import com.pndnwngi.mysalesmanship.data.local.entity.MstOutletEntity

interface OutletRepository {
    suspend fun getOutletsByRoute(routeId: Long): List<MstOutletEntity>
    suspend fun getOutletByBarcode(barcode: String): MstOutletEntity?
    suspend fun searchOutletsByName(name: String): List<MstOutletEntity>
    suspend fun getOutletById(serverId: Long): MstOutletEntity?
    suspend fun getAllOutlets(): List<MstOutletEntity>
}
