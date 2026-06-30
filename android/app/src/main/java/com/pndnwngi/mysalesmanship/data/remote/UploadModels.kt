package com.pndnwngi.mysalesmanship.data.remote

import com.google.gson.annotations.SerializedName

data class UploadCheckStockRequest(
    @SerializedName("product_id") val productId: Long,
    @SerializedName("stock_qty") val stockQty: Int
)

data class UploadSalesDetailRequest(
    @SerializedName("product_id") val productId: Long,
    val qty: Int,
    val price: Double,
    val total: Double
)

data class UploadSalesRequest(
    @SerializedName("sales_order") val salesOrder: String,
    val details: List<UploadSalesDetailRequest>
)

data class UploadVisitRequest(
    @SerializedName("outlet_id") val outletId: Long,
    @SerializedName("visit_no") val visitNo: Int,
    @SerializedName("visit_type") val visitType: String,
    val status: String,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String?,
    val lat: Double?,
    val lng: Double?,
    @SerializedName("check_stocks") val checkStocks: List<UploadCheckStockRequest>,
    val sales: List<UploadSalesRequest>
)

data class UploadPayload(
    @SerializedName("batch_id") val batchId: String,
    val visit: UploadVisitRequest
)
