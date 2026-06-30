package com.pndnwngi.mysalesmanship.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales_target")
data class SalesTargetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "sales_id")
    val salesId: Long,
    
    @ColumnInfo(name = "route_id")
    val routeId: Long,
    
    @ColumnInfo(name = "product_id")
    val productId: Long,
    
    @ColumnInfo(name = "target_qty")
    val targetQty: Int,
    
    @ColumnInfo(name = "week_start")
    val weekStart: String,
    
    @ColumnInfo(name = "week_end")
    val weekEnd: String
)
