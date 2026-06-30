package com.pndnwngi.mysalesmanship.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pndnwngi.mysalesmanship.data.local.entity.MstSalesmanEntity

@Dao
interface MstSalesmanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(salesman: MstSalesmanEntity): Long

    @Query("SELECT * FROM mst_salesman LIMIT 1")
    fun getSalesman(): MstSalesmanEntity?
}
