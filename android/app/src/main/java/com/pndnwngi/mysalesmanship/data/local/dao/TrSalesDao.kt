package com.pndnwngi.mysalesmanship.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pndnwngi.mysalesmanship.data.local.entity.TrSalesEntity

@Dao
interface TrSalesDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(trSales: TrSalesEntity): Long

    @Query("SELECT * FROM tr_sales WHERE tr_outlet_id = :trOutletId")
    fun getSalesByVisit(trOutletId: Long): List<TrSalesEntity>
}
