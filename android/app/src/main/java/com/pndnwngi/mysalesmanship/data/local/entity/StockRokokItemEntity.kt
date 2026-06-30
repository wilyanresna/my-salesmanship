package com.pndnwngi.mysalesmanship.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_rokok_item")
data class StockRokokItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "stock_rokok_id")
    val stockRokokId: Long,
    
    @ColumnInfo(name = "product_id")
    val productId: Long,
    
    @ColumnInfo(name = "qty_dus_init")
    val qtyDusInit: Int,
    
    @ColumnInfo(name = "qty_bal_init")
    val qtyBalInit: Int,
    
    @ColumnInfo(name = "qty_slf_init")
    val qtySlfInit: Int,
    
    @ColumnInfo(name = "qty_bks_init")
    val qtyBksInit: Int
)
