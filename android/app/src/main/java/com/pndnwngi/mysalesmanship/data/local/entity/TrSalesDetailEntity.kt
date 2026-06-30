package com.pndnwngi.mysalesmanship.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tr_sales_detail")
data class TrSalesDetailEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "tr_sales_id")
    val trSalesId: Long,
    
    @ColumnInfo(name = "product_id")
    val productId: Long,
    
    val qty: Int,
    val price: Double,
    val total: Double
)
