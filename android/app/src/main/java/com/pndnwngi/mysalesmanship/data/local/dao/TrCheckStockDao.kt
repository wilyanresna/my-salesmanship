package com.pndnwngi.mysalesmanship.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pndnwngi.mysalesmanship.data.local.entity.TrCheckStockEntity

@Dao
interface TrCheckStockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(trCheckStock: TrCheckStockEntity): Long

    @Query("SELECT * FROM tr_check_stock WHERE tr_outlet_id = :trOutletId")
    fun getCheckStockByVisit(trOutletId: Long): List<TrCheckStockEntity>
}
