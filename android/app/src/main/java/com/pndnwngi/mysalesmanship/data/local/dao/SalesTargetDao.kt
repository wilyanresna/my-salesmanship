package com.pndnwngi.mysalesmanship.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pndnwngi.mysalesmanship.data.local.entity.SalesTargetEntity

@Dao
interface SalesTargetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(targets: List<SalesTargetEntity>): List<Long>

    @Query("SELECT * FROM sales_target WHERE route_id = :routeId AND week_start <= :date AND week_end >= :date")
    fun getTargetsByRouteAndDate(routeId: Long, date: String): List<SalesTargetEntity>

    @Query("SELECT * FROM sales_target")
    fun getAllTargets(): List<SalesTargetEntity>
}
