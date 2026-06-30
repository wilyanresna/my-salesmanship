package com.pndnwngi.mysalesmanship.data.repository

import androidx.room.withTransaction
import com.pndnwngi.mysalesmanship.data.local.AppDatabase
import com.pndnwngi.mysalesmanship.data.local.entity.*
import com.pndnwngi.mysalesmanship.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PullRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val database: AppDatabase
) : PullRepository {

    override suspend fun pullData(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.pullData()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    database.withTransaction {
                        val dbWriter = database.openHelper.writableDatabase
                        dbWriter.execSQL("DELETE FROM mst_salesman")
                        dbWriter.execSQL("DELETE FROM mst_product")
                        dbWriter.execSQL("DELETE FROM mst_outlet")
                        dbWriter.execSQL("DELETE FROM param")
                        dbWriter.execSQL("DELETE FROM stock_rokok")
                        dbWriter.execSQL("DELETE FROM stock_rokok_item")
                        dbWriter.execSQL("DELETE FROM sales_target")

                        val salesmanEntity = MstSalesmanEntity(
                            salesId = body.salesman.id,
                            salesName = body.salesman.name,
                            spvId = 0L,
                            spvName = body.salesman.spvName,
                            territoryId = 0L,
                            districtId = body.salesman.districtId
                        )
                        database.mstSalesmanDao().insert(salesmanEntity)

                        val productEntities = body.products.map {
                            MstProductEntity(
                                serverId = it.id,
                                name = it.name,
                                sku = it.sku,
                                price = it.price,
                                uomBal = it.uomBal,
                                uomSlf = it.uomSlf,
                                uomBks = it.uomBks
                            )
                        }
                        database.mstProductDao().insertAll(productEntities)

                        val outletEntities = body.outlets.map {
                            MstOutletEntity(
                                serverId = it.id,
                                name = it.name,
                                ownerName = it.ownerName,
                                barcode = it.barcode,
                                lat = it.lat,
                                lng = it.lng,
                                outletStatus = it.outletStatus,
                                routeId = it.routeId
                            )
                        }
                        database.mstOutletDao().insertAll(outletEntities)

                        val paramEntities = body.params.map {
                            ParamEntity(
                                groupName = it.groupName,
                                key = it.key,
                                value = it.value,
                                description = it.description
                            )
                        }
                        database.paramDao().insertAll(paramEntities)

                        body.stockRokok?.let { stock ->
                            val stockId = database.stockRokokDao().insert(
                                StockRokokEntity(
                                    serverId = stock.id,
                                    salesId = body.salesman.id,
                                    dateUsed = stock.dateUsed,
                                    status = stock.status
                                )
                            )
                            val stockItemEntities = stock.items.map {
                                StockRokokItemEntity(
                                    stockRokokId = stockId,
                                    productId = it.productId,
                                    qtyDusInit = it.qtyDusInit,
                                    qtyBalInit = it.qtyBalInit,
                                    qtySlfInit = it.qtySlfInit,
                                    qtyBksInit = it.qtyBksInit
                                )
                            }
                            database.stockRokokItemDao().insertAll(stockItemEntities)
                        }

                        val targetEntities = body.targets.map {
                            SalesTargetEntity(
                                salesId = it.salesId,
                                routeId = it.routeId,
                                productId = it.productId,
                                targetQty = it.targetQty,
                                weekStart = it.weekStart,
                                weekEnd = it.weekEnd
                            )
                        }
                        database.salesTargetDao().insertAll(targetEntities)
                    }
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Response body is empty"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Gagal menarik data dari server"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
