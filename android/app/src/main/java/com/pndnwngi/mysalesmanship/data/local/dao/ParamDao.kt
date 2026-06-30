package com.pndnwngi.mysalesmanship.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pndnwngi.mysalesmanship.data.local.entity.ParamEntity

@Dao
interface ParamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(params: List<ParamEntity>): List<Long>

    @Query("SELECT * FROM param WHERE group_name = :groupName")
    fun getParamsByGroup(groupName: String): List<ParamEntity>
}
