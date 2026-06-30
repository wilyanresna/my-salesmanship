package com.pndnwngi.mysalesmanship

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pndnwngi.mysalesmanship.data.local.AppDatabase
import com.pndnwngi.mysalesmanship.data.local.entity.MstOutletEntity
import com.pndnwngi.mysalesmanship.data.local.entity.MstProductEntity
import com.pndnwngi.mysalesmanship.data.local.entity.MstSalesmanEntity
import com.pndnwngi.mysalesmanship.data.local.entity.SyncQueueEntity
import com.pndnwngi.mysalesmanship.data.local.entity.TrOutletEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DaoTest {
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun testMstSalesmanDao() = runBlocking {
        val salesmanDao = db.mstSalesmanDao()
        val salesman = MstSalesmanEntity(
            salesId = 1L,
            salesName = "Andi Wijaya",
            spvId = 2L,
            spvName = "Budi Santoso",
            territoryId = 3L,
            districtId = 4L
        )
        salesmanDao.insert(salesman)
        val fetched = salesmanDao.getSalesman()
        assertNotNull(fetched)
        assertEquals("Andi Wijaya", fetched?.salesName)
        assertEquals(2L, fetched?.spvId)
    }

    @Test
    @Throws(Exception::class)
    fun testMstProductDao() = runBlocking {
        val productDao = db.mstProductDao()
        val products = listOf(
            MstProductEntity(id = 1L, serverId = 101L, name = "Djarum Super 12", sku = "DJ-SUP-12", price = 2500.0, uomBal = 20, uomSlf = 10, uomBks = 12),
            MstProductEntity(id = 2L, serverId = 102L, name = "LA Bold 16", sku = "LA-BLD-16", price = 2800.0, uomBal = 20, uomSlf = 10, uomBks = 16)
        )
        productDao.insertAll(products)

        val all = productDao.getAllProducts()
        assertEquals(2, all.size)

        val productById = productDao.getProductById(1L)
        assertNotNull(productById)
        assertEquals("Djarum Super 12", productById?.name)

        val productByBarcode = productDao.getProductByBarcode("LA-BLD-16")
        assertNotNull(productByBarcode)
        assertEquals("LA Bold 16", productByBarcode?.name)
    }

    @Test
    @Throws(Exception::class)
    fun testMstOutletDao() = runBlocking {
        val outletDao = db.mstOutletDao()
        val outlets = listOf(
            MstOutletEntity(id = 1L, serverId = 201L, name = "Toko Mawar", ownerName = "Pak Joko", barcode = "OTL-0001", lat = -6.2614, lng = 106.8106, outletStatus = "ACTIVE", routeId = 10L),
            MstOutletEntity(id = 2L, serverId = 202L, name = "Warung Melati", ownerName = "Bu Sari", barcode = "OTL-0002", lat = -6.2650, lng = 106.8120, outletStatus = "ACTIVE", routeId = 10L),
            MstOutletEntity(id = 3L, serverId = 203L, name = "Toko Sejahtera", ownerName = "Pak Bowo", barcode = "OTL-0003", lat = -6.2590, lng = 106.8090, outletStatus = "ACTIVE", routeId = 11L)
        )
        outletDao.insertAll(outlets)

        val byRoute = outletDao.getOutletsByRoute(10L)
        assertEquals(2, byRoute.size)

        val byBarcode = outletDao.getOutletByBarcode("OTL-0003")
        assertNotNull(byBarcode)
        assertEquals("Toko Sejahtera", byBarcode?.name)

        val search = outletDao.searchOutletsByName("Mawar")
        assertEquals(1, search.size)
        assertEquals("Toko Mawar", search[0].name)
    }

    @Test
    @Throws(Exception::class)
    fun testTrOutletDao() = runBlocking {
        val trOutletDao = db.trOutletDao()
        val visit = TrOutletEntity(
            id = 1L,
            outletId = 201L,
            salesId = 1L,
            visitNo = 1,
            visitType = "NORMAL",
            status = "OPEN",
            startTime = 1719730000000L,
            endTime = null,
            lat = -6.2614,
            lng = 106.8106
        )
        trOutletDao.insert(visit)

        val openVisits = trOutletDao.getAllOpenVisits()
        assertEquals(1, openVisits.size)

        trOutletDao.updateStatusAndEndTime(1L, "CLOSED", 1719732000000L)
        val visitClosed = trOutletDao.getVisitById(1L)
        assertEquals("CLOSED", visitClosed?.status)
        assertEquals(1719732000000L, visitClosed?.endTime)

        val openVisitsEmpty = trOutletDao.getAllOpenVisits()
        assertEquals(0, openVisitsEmpty.size)
    }

    @Test
    @Throws(Exception::class)
    fun testSyncQueueDao() = runBlocking {
        val syncQueueDao = db.syncQueueDao()
        val item1 = SyncQueueEntity(id = 1L, entityType = "VISIT", entityId = 10L, batchId = "uuid-1", status = "PENDING")
        val item2 = SyncQueueEntity(id = 2L, entityType = "VISIT", entityId = 11L, batchId = "uuid-2", status = "FAILED")
        
        syncQueueDao.insert(item1)
        syncQueueDao.insert(item2)

        val pending = syncQueueDao.getPendingItems()
        assertEquals(1, pending.size)
        assertEquals("PENDING", pending[0].status)

        val failed = syncQueueDao.getFailedItems()
        assertEquals(1, failed.size)
        assertEquals("FAILED", failed[0].status)

        syncQueueDao.updateStatus(2L, "SYNCED")
        syncQueueDao.deleteSyncedItems()

        val failedEmpty = syncQueueDao.getFailedItems()
        assertEquals(0, failedEmpty.size)
    }
}
