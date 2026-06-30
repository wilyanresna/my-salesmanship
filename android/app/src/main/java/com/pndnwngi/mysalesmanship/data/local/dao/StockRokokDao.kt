package com.pndnwngi.mysalesmanship.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pndnwngi.mysalesmanship.data.local.entity.StockRokokEntity

@Dao
interface StockRokokDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(stockRokok: StockRokokEntity): Long

    @Query("SELECT * FROM stock_rokok WHERE date_used = :dateUsed LIMIT 1")
    fun getStockRokokByDate(dateUsed: String): StockRokokEntity?

    @Query("SELECT * FROM stock_rokok LIMIT 1")
    fun getActiveStock(): StockRokokEntity?

    @Query("UPDATE stock_rokok SET status = :status WHERE id = :id")
    fun updateStatus(id: Long, status: String): Int
}
