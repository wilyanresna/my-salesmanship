package com.pndnwngi.mysalesmanship.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mst_salesman")
data class MstSalesmanEntity(
    @PrimaryKey
    @ColumnInfo(name = "sales_id")
    val salesId: Long,
    
    @ColumnInfo(name = "sales_name")
    val salesName: String,
    
    @ColumnInfo(name = "spv_id")
    val spvId: Long,
    
    @ColumnInfo(name = "spv_name")
    val spvName: String,
    
    @ColumnInfo(name = "territory_id")
    val territoryId: Long,
    
    @ColumnInfo(name = "district_id")
    val districtId: Long
)
