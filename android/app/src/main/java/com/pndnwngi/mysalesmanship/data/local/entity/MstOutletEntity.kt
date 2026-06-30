package com.pndnwngi.mysalesmanship.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mst_outlet")
data class MstOutletEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "server_id")
    val serverId: Long,
    
    val name: String,
    
    @ColumnInfo(name = "owner_name")
    val ownerName: String?,
    
    val barcode: String?,
    val lat: Double?,
    val lng: Double?,
    
    @ColumnInfo(name = "outlet_status")
    val outletStatus: String,
    
    @ColumnInfo(name = "route_id")
    val routeId: Long
)
