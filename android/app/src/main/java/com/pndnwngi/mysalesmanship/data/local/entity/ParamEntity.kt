package com.pndnwngi.mysalesmanship.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "param")
data class ParamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "group_name")
    val groupName: String,
    
    val key: String,
    val value: String,
    val description: String?
)
