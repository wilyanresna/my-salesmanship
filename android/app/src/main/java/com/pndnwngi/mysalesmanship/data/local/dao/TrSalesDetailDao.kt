package com.pndnwngi.mysalesmanship.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pndnwngi.mysalesmanship.data.local.entity.TrSalesDetailEntity

@Dao
interface TrSalesDetailDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAll(details: List<TrSalesDetailEntity>): List<Long>

    @Query("SELECT * FROM tr_sales_detail WHERE tr_sales_id = :trSalesId")
    fun getDetailsBySalesId(trSalesId: Long): List<TrSalesDetailEntity>

    @Query("SELECT SUM(qty) FROM tr_sales_detail WHERE product_id = :productId")
    fun getTotalSoldByProduct(productId: Long): Int?
}
