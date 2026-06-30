package com.pndnwngi.mysalesmanship.data.remote

import com.google.gson.annotations.SerializedName

data class PullSalesmanResponse(
    val id: Long,
    val name: String,
    @SerializedName("district_id") val districtId: Long,
    @SerializedName("spv_name") val spvName: String
)

data class PullOutletResponse(
    val id: Long,
    val name: String,
    @SerializedName("owner_name") val ownerName: String?,
    val phone: String?,
    val address: String?,
    val lat: Double?,
    val lng: Double?,
    val barcode: String?,
    @SerializedName("outlet_status") val outletStatus: String,
    @SerializedName("route_id") val routeId: Long
)

data class PullProductResponse(
    val id: Long,
    val name: String,
    val sku: String,
    val price: Double,
    @SerializedName("uom_bal") val uomBal: Int,
    @SerializedName("uom_slf") val uomSlf: Int,
    @SerializedName("uom_bks") val uomBks: Int,
    @SerializedName("is_active") val isActive: Boolean
)

data class PullParamResponse(
    val id: Long,
    @SerializedName("group_name") val groupName: String,
    val key: String,
    val value: String,
    val description: String?,
    @SerializedName("is_active") val isActive: Boolean
)

data class PullStockItemResponse(
    val id: Long,
    @SerializedName("stock_rokok_id") val stockRokokId: Long,
    @SerializedName("product_id") val productId: Long,
    @SerializedName("qty_dus_init") val qtyDusInit: Int,
    @SerializedName("qty_bal_init") val qtyBalInit: Int,
    @SerializedName("qty_slf_init") val qtySlfInit: Int,
    @SerializedName("qty_bks_init") val qtyBksInit: Int
)

data class PullStockResponse(
    val id: Long,
    @SerializedName("date_used") val dateUsed: String,
    val status: String,
    val items: List<PullStockItemResponse>
)

data class PullTargetResponse(
    val id: Long,
    @SerializedName("sales_id") val salesId: Long,
    @SerializedName("route_id") val routeId: Long,
    @SerializedName("product_id") val productId: Long,
    @SerializedName("target_qty") val targetQty: Int,
    @SerializedName("week_start") val weekStart: String,
    @SerializedName("week_end") val weekEnd: String
)

data class PullResponse(
    val salesman: PullSalesmanResponse,
    val outlets: List<PullOutletResponse>,
    val products: List<PullProductResponse>,
    val params: List<PullParamResponse>,
    @SerializedName("stock_rokok") val stockRokok: PullStockResponse?,
    val targets: List<PullTargetResponse>
)
