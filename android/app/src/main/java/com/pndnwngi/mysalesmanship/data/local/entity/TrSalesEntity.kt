package com.pndnwngi.mysalesmanship.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tr_sales")
data class TrSalesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "tr_outlet_id")
    val trOutletId: Long,
    
    @ColumnInfo(name = "sales_order")
    val salesOrder: String
)
