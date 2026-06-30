package com.pndnwngi.mysalesmanship.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_rokok")
data class StockRokokEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "server_id")
    val serverId: Long,
    
    @ColumnInfo(name = "sales_id")
    val salesId: Long,
    
    @ColumnInfo(name = "date_used")
    val dateUsed: String,
    
    val status: String
)
