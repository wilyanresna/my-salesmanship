package com.pndnwngi.mysalesmanship.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pndnwngi.mysalesmanship.data.local.entity.TrOutletEntity

@Dao
interface TrOutletDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(trOutlet: TrOutletEntity): Long

    @Query("UPDATE tr_outlet SET status = :status, end_time = :endTime WHERE id = :id")
    fun updateStatusAndEndTime(id: Long, status: String, endTime: Long): Int

    @Query("UPDATE tr_outlet SET status = :status WHERE id = :id")
    fun updateStatus(id: Long, status: String): Int

    @Query("""
        SELECT * FROM tr_outlet 
        WHERE outlet_id = :outletId 
          AND start_time >= :startOfDay 
          AND start_time <= :endOfDay
    """)
    fun getVisitsByOutletAndDate(outletId: Long, startOfDay: Long, endOfDay: Long): List<TrOutletEntity>

    @Query("SELECT * FROM tr_outlet WHERE status = 'OPEN'")
    fun getAllOpenVisits(): List<TrOutletEntity>

    @Query("SELECT * FROM tr_outlet WHERE id = :id")
    fun getVisitById(id: Long): TrOutletEntity?
}
