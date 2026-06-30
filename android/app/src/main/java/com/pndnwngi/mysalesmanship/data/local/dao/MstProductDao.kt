package com.pndnwngi.mysalesmanship.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pndnwngi.mysalesmanship.data.local.entity.MstProductEntity

@Dao
interface MstProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(products: List<MstProductEntity>): List<Long>

    @Query("SELECT * FROM mst_product")
    fun getAllProducts(): List<MstProductEntity>

    @Query("SELECT * FROM mst_product WHERE id = :id")
    fun getProductById(id: Long): MstProductEntity?

    @Query("SELECT * FROM mst_product WHERE server_id = :serverId")
    fun getProductByServerId(serverId: Long): MstProductEntity?

    @Query("SELECT * FROM mst_product WHERE sku = :barcode")
    fun getProductByBarcode(barcode: String): MstProductEntity?
}
