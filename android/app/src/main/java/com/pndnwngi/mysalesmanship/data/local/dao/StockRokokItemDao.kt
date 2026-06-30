package com.pndnwngi.mysalesmanship.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pndnwngi.mysalesmanship.data.local.entity.StockRokokItemEntity

@Dao
interface StockRokokItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<StockRokokItemEntity>): List<Long>

    @Query("SELECT * FROM stock_rokok_item WHERE stock_rokok_id = :stockRokokId")
    fun getItemsByStockRokokId(stockRokokId: Long): List<StockRokokItemEntity>

    @Query("SELECT * FROM stock_rokok_item WHERE stock_rokok_id = :stockRokokId AND product_id = :productId LIMIT 1")
    fun getItemByStockAndProduct(stockRokokId: Long, productId: Long): StockRokokItemEntity?

    @Query("""
        UPDATE stock_rokok_item 
        SET qty_dus_init = :qtyDus, qty_bal_init = :qtyBal, qty_slf_init = :qtySlf, qty_bks_init = :qtyBks 
        WHERE id = :id
    """)
    fun updateQty(id: Long, qtyDus: Int, qtyBal: Int, qtySlf: Int, qtyBks: Int): Int
}
