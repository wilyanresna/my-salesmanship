package com.pndnwngi.mysalesmanship.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mst_product")
data class MstProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "server_id")
    val serverId: Long,
    
    val name: String,
    val sku: String,
    val price: Double,
    
    @ColumnInfo(name = "uom_bal")
    val uomBal: Int,
    
    @ColumnInfo(name = "uom_slf")
    val uomSlf: Int,
    
    @ColumnInfo(name = "uom_bks")
    val uomBks: Int
)
