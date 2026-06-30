package com.pndnwngi.mysalesmanship.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pndnwngi.mysalesmanship.data.local.dao.*
import com.pndnwngi.mysalesmanship.data.local.entity.*

@Database(
    entities = [
        MstSalesmanEntity::class,
        MstProductEntity::class,
        MstOutletEntity::class,
        ParamEntity::class,
        StockRokokEntity::class,
        StockRokokItemEntity::class,
        SalesTargetEntity::class,
        TrOutletEntity::class,
        TrCheckStockEntity::class,
        TrSalesEntity::class,
        TrSalesDetailEntity::class,
        SyncQueueEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mstSalesmanDao(): MstSalesmanDao
    abstract fun mstProductDao(): MstProductDao
    abstract fun mstOutletDao(): MstOutletDao
    abstract fun paramDao(): ParamDao
    abstract fun stockRokokDao(): StockRokokDao
    abstract fun stockRokokItemDao(): StockRokokItemDao
    abstract fun salesTargetDao(): SalesTargetDao
    abstract fun trOutletDao(): TrOutletDao
    abstract fun trCheckStockDao(): TrCheckStockDao
    abstract fun trSalesDao(): TrSalesDao
    abstract fun trSalesDetailDao(): TrSalesDetailDao
    abstract fun syncQueueDao(): SyncQueueDao
}
