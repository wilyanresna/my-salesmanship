package com.pndnwngi.mysalesmanship.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tr_outlet")
data class TrOutletEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "outlet_id")
    val outletId: Long,
    
    @ColumnInfo(name = "sales_id")
    val salesId: Long,
    
    @ColumnInfo(name = "visit_no")
    val visitNo: Int,
    
    @ColumnInfo(name = "visit_type")
    val visitType: String,
    
    val status: String,
    
    @ColumnInfo(name = "start_time")
    val startTime: Long,
    
    @ColumnInfo(name = "end_time")
    val endTime: Long?,
    
    val lat: Double?,
    val lng: Double?
)
