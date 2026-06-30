package com.pndnwngi.mysalesmanship.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pndnwngi.mysalesmanship.data.local.entity.MstOutletEntity

@Dao
interface MstOutletDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(outlets: List<MstOutletEntity>): List<Long>

    @Query("SELECT * FROM mst_outlet WHERE route_id = :routeId")
    fun getOutletsByRoute(routeId: Long): List<MstOutletEntity>

    @Query("SELECT * FROM mst_outlet WHERE barcode = :barcode")
    fun getOutletByBarcode(barcode: String): MstOutletEntity?

    @Query("SELECT * FROM mst_outlet WHERE name LIKE '%' || :name || '%'")
    fun searchOutletsByName(name: String): List<MstOutletEntity>

    @Query("SELECT * FROM mst_outlet WHERE server_id = :serverId")
    fun getOutletById(serverId: Long): MstOutletEntity?

    @Query("SELECT * FROM mst_outlet")
    fun getAllOutlets(): List<MstOutletEntity>
}
