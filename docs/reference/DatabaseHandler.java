package sm.co.id.salesmanship.database;

import static sm.co.id.salesmanship.BuildConfig.FOLDER_NAME;
import static sm.co.id.salesmanship.BuildConfig.LOG_FILE;
import static sm.co.id.salesmanship.BuildConfig.VERSION_NAME;
import static sm.co.id.salesmanship.database.DatabaseExportImport.exportDb;
import static sm.co.id.salesmanship.database.TableDefintion.DB_TRACKING;
import static sm.co.id.salesmanship.database.TableDefintion.MST_ADJUSTMENT_TYPE;
import static sm.co.id.salesmanship.database.TableDefintion.MST_BRAND_PROG;
import static sm.co.id.salesmanship.database.TableDefintion.MST_CONTRACT;
import static sm.co.id.salesmanship.database.TableDefintion.MST_MAPPING_NON_MDTRY;
import static sm.co.id.salesmanship.database.TableDefintion.MST_NON_VISIBLE;
import static sm.co.id.salesmanship.database.TableDefintion.MST_NOTATION;
import static sm.co.id.salesmanship.database.TableDefintion.MST_OI_PKS;
import static sm.co.id.salesmanship.database.TableDefintion.MST_OI_PKS_DTL;
import static sm.co.id.salesmanship.database.TableDefintion.MST_OUTLET_DEDICATED;
import static sm.co.id.salesmanship.database.TableDefintion.MST_OUTLET_SHOPBLIND_DTL;
import static sm.co.id.salesmanship.database.TableDefintion.MST_OUTLET_OWNER;
import static sm.co.id.salesmanship.database.TableDefintion.MST_PARAMETER_VERSION;
import static sm.co.id.salesmanship.database.TableDefintion.MST_PARAM_GLOBAL;
import static sm.co.id.salesmanship.database.TableDefintion.MST_PRODUCT_DISPLAY;
import static sm.co.id.salesmanship.database.TableDefintion.MST_SALES_TYPE;
import static sm.co.id.salesmanship.database.TableDefintion.MST_STATUS_OUTLET;
import static sm.co.id.salesmanship.database.TableDefintion.MST_TTO_ITEM_SUBCATEGORY;
import static sm.co.id.salesmanship.database.TableDefintion.MST_TTO_PRODUCT;
import static sm.co.id.salesmanship.database.TableDefintion.MST_TTO_RECEIVER;
import static sm.co.id.salesmanship.database.TableDefintion.MST_UNIT_DISPLAY;
import static sm.co.id.salesmanship.database.TableDefintion.MST_VILLAGE;
import static sm.co.id.salesmanship.database.TableDefintion.MST_VISIBILITY_DOMINAN;
import static sm.co.id.salesmanship.database.TableDefintion.TR_ADDT_UNIT;
import static sm.co.id.salesmanship.database.TableDefintion.TR_COMPENSATION_HIST;
import static sm.co.id.salesmanship.database.TableDefintion.TR_DOWNLOAD;
import static sm.co.id.salesmanship.database.TableDefintion.TR_NONPP_PROG_EXEC_HIST;
import static sm.co.id.salesmanship.database.TableDefintion.TR_NOTES_ROUTE;
import static sm.co.id.salesmanship.database.TableDefintion.TR_PROG_PHOTO;
import static sm.co.id.salesmanship.database.TableDefintion.TR_SVY_VOLUME;
import static sm.co.id.salesmanship.database.TableDefintion.TR_TTO_CONTRACT;
import static sm.co.id.salesmanship.database.TableDefintion.TR_TTO_HDR;
import static sm.co.id.salesmanship.database.TableDefintion.TR_TTO_PRODUCT_DISPLAY;
import static sm.co.id.salesmanship.database.TableDefintion.TR_TTO_RECEIVER;
import static sm.co.id.salesmanship.database.TableDefintion.TR_TTO_SIGNATURE;
import static sm.co.id.salesmanship.database.TableDefintion.TR_TTO_UNIT_DISPLAY;
import static sm.co.id.salesmanship.database.TableDefintion.TR_UNIT_SUPPORT_HIST;
import static sm.co.id.salesmanship.util.Constant.AA;
import static sm.co.id.salesmanship.util.Constant.ALAT_PEMBAYARAN_GROUP_PARAM;
import static sm.co.id.salesmanship.util.Constant.ALAT_PEMBAYARAN_LINKAJA;
import static sm.co.id.salesmanship.util.Constant.BILL_STATUS_PAID;
import static sm.co.id.salesmanship.util.Constant.BILL_STATUS_UNPAID;
import static sm.co.id.salesmanship.util.Constant.BPPU;
import static sm.co.id.salesmanship.util.Constant.GROUP_PARAM.NON_PP_ITEM_MAPPING;
import static sm.co.id.salesmanship.util.Constant.GROUP_PARAM_REAL_PAYMENT_METHOD;
import static sm.co.id.salesmanship.util.Constant.MST_COST_ITEM_PEMBULATAN;
import static sm.co.id.salesmanship.util.Constant.PARENT_PATH;
import static sm.co.id.salesmanship.util.Constant.PATH_FILE_SFA;
import static sm.co.id.salesmanship.util.Constant.REALISASI_BIAYA_STATUS_CLAIM_NEED_REVISION;
import static sm.co.id.salesmanship.util.Constant.REALISASI_BIAYA_STATUS_CLAIM_UN_UPLOADED;
import static sm.co.id.salesmanship.util.Constant.REALISASI_BIAYA_STATUS_CLAIM_WAITING_TO_VALIDATE;
import static sm.co.id.salesmanship.util.Constant.REALISASI_BIAYA_STATUS_GROUP_PARAM;
import static sm.co.id.salesmanship.util.Constant.TYPE_KUNJUNGAN;
import static sm.co.id.salesmanship.util.Constant.TYPE_KUNJUNGAN_KEMBALI;
import static sm.co.id.salesmanship.util.Constant.TipeBppr.DALAM_KOTA;
import static sm.co.id.salesmanship.util.Constant.TipeBppr.LUAR_KOTA;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.GsonBuilder;

import net.sqlcipher.Cursor;
import net.sqlcipher.DatabaseErrorHandler;

import android.database.SQLException;

import net.sqlcipher.database.SQLiteDatabase;

import android.database.sqlite.SQLiteException;

import net.sqlcipher.database.SQLiteDatabaseHook;
import net.sqlcipher.database.SQLiteOpenHelper;
import net.sqlcipher.database.SQLiteStatement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import id.co.sm.stocksharing.datasource.MainDataSource;
import id.co.sm.stocksharing.datasource.SendDataSource;
import id.co.sm.stocksharing.model.ShareJournal;
import id.co.sm.stocksharing.utils.ShareUtils;
import sm.co.id.salesmanship.R;
import sm.co.id.salesmanship.database.TableDefintion.LOG_ACTIVIY;
import sm.co.id.salesmanship.database.TableDefintion.LOG_PRINT;
import sm.co.id.salesmanship.database.TableDefintion.MST_HIS_CALLSHEET;
import sm.co.id.salesmanship.database.TableDefintion.MST_HIS_MAINTENANCE;
import sm.co.id.salesmanship.database.TableDefintion.MST_ITEM_GROUP;
import sm.co.id.salesmanship.database.TableDefintion.MST_ITEM_SUBCATEGORY;
import sm.co.id.salesmanship.database.TableDefintion.MST_OUTLET;
import sm.co.id.salesmanship.database.TableDefintion.MST_OUTLET_INVEST;
import sm.co.id.salesmanship.database.TableDefintion.MST_OUTLET_TYPE;
import sm.co.id.salesmanship.database.TableDefintion.MST_PP_PROGRAM;
import sm.co.id.salesmanship.database.TableDefintion.MST_PRODUCT;
import sm.co.id.salesmanship.database.TableDefintion.MST_REASON_BARCODE;
import sm.co.id.salesmanship.database.TableDefintion.MST_SALESMAN;
import sm.co.id.salesmanship.database.TableDefintion.MST_TARGET;
import sm.co.id.salesmanship.database.TableDefintion.MST_TTO_MAX_AMP;
import sm.co.id.salesmanship.database.TableDefintion.TEMP_TR_NOTATION;
import sm.co.id.salesmanship.database.TableDefintion.TR_ADJUST_TKRGLG;
import sm.co.id.salesmanship.database.TableDefintion.TR_BPPM;
import sm.co.id.salesmanship.database.TableDefintion.TR_BPPM_ACV;
import sm.co.id.salesmanship.database.TableDefintion.TR_BPPU;
import sm.co.id.salesmanship.database.TableDefintion.TR_DURATION_NOTES;
import sm.co.id.salesmanship.database.TableDefintion.TR_INBOX;
import sm.co.id.salesmanship.database.TableDefintion.TR_MAINTENANCE;
import sm.co.id.salesmanship.database.TableDefintion.TR_NOTATION;
import sm.co.id.salesmanship.database.TableDefintion.TR_OUTLET_OWNER;
import sm.co.id.salesmanship.database.TableDefintion.TR_OUTLET_PKP;
import sm.co.id.salesmanship.database.TableDefintion.TR_PHOTO_MAINTENANCE;
import sm.co.id.salesmanship.database.TableDefintion.TR_POSM;
import sm.co.id.salesmanship.database.TableDefintion.TR_PU_ACV;
import sm.co.id.salesmanship.database.TableDefintion.TR_RETUR_RECAP;
import sm.co.id.salesmanship.database.TableDefintion.TR_SALES;
import sm.co.id.salesmanship.database.TableDefintion.TR_SALES_TEMP;
import sm.co.id.salesmanship.database.TableDefintion.TR_TTO_DTL;
import sm.co.id.salesmanship.database.TableDefintion.TR_VISDOM_NONVIS;
import sm.co.id.salesmanship.database.TableDefintion.TWEEK;
import sm.co.id.salesmanship.model.DateModel;
import sm.co.id.salesmanship.model.DbTracking;
import sm.co.id.salesmanship.model.LogActivity;
import sm.co.id.salesmanship.model.MstAdjustmentType;
import sm.co.id.salesmanship.model.MstBarcodeReason;
import sm.co.id.salesmanship.model.MstBrandProg;
import sm.co.id.salesmanship.model.MstNonVisible;
import sm.co.id.salesmanship.model.MstNotation;
import sm.co.id.salesmanship.model.MstOutletShopblindDtl;
import sm.co.id.salesmanship.model.MstOutletType;
import sm.co.id.salesmanship.model.MstParameterVersion;
import sm.co.id.salesmanship.model.MstSalesType;
import sm.co.id.salesmanship.model.MstStatusOutlet;
import sm.co.id.salesmanship.model.MstVisibilityDominan;
import sm.co.id.salesmanship.model.OutletListResult;
import sm.co.id.salesmanship.model.OutletModel;
import sm.co.id.salesmanship.model.PredefineData;
import sm.co.id.salesmanship.model.TWeek;
import sm.co.id.salesmanship.model.TrInboxModel;
import sm.co.id.salesmanship.model.TrNotesRoute;
import sm.co.id.salesmanship.model.TrOutletShopblindDtl;
import sm.co.id.salesmanship.model.adapter.BppmAlocation;
import sm.co.id.salesmanship.model.adapter.BppmDtl;
import sm.co.id.salesmanship.model.adapter.BppuAlocation;
import sm.co.id.salesmanship.model.adapter.DashboardExProgram;
import sm.co.id.salesmanship.model.adapter.DetailDashboard;
import sm.co.id.salesmanship.model.adapter.EksekusiProgramNonPp;
import sm.co.id.salesmanship.model.adapter.EksekusiProgramPp;
import sm.co.id.salesmanship.model.adapter.Kompensasi;
import sm.co.id.salesmanship.model.adapter.Lov;
import sm.co.id.salesmanship.model.adapter.PpProgram;
import sm.co.id.salesmanship.model.adapter.TradeMerchandiser;
import sm.co.id.salesmanship.model.adapter.UnitSupport;
import sm.co.id.salesmanship.model.claudiaConsumer.MstCounty;
import sm.co.id.salesmanship.model.claudiaConsumer.MstProductAll;
import sm.co.id.salesmanship.model.claudiaConsumer.MstSubDistrict;
import sm.co.id.salesmanship.model.download.DownloadBrandDedicatedResult;
import sm.co.id.salesmanship.model.download.DownloadMstRealBiayaResult;
import sm.co.id.salesmanship.model.download.DownloadTrCostRealizationResult;
import sm.co.id.salesmanship.model.download.MstAttachmentMapping;
import sm.co.id.salesmanship.model.download.MstProgramMap;
import sm.co.id.salesmanship.model.download.MstTpbp;
import sm.co.id.salesmanship.model.download.StgTrBppm;
import sm.co.id.salesmanship.model.maintenance.ImageModel;
import sm.co.id.salesmanship.model.maintenance.Maintenance;
import sm.co.id.salesmanship.model.tandaterima.MstBank;
import sm.co.id.salesmanship.model.tandaterima.MstOiPks;
import sm.co.id.salesmanship.model.tandaterima.TrPpAllocatedTrf;
import sm.co.id.salesmanship.model.tandaterima.TrTtoHdr;
import sm.co.id.salesmanship.model.transaction.MstAcctTopUpTkrGlg;
import sm.co.id.salesmanship.model.transaction.MstActualUnitSupport;
import sm.co.id.salesmanship.model.transaction.MstBrandGroup;
import sm.co.id.salesmanship.model.transaction.MstCostItem;
import sm.co.id.salesmanship.model.transaction.MstCostItemMap;
import sm.co.id.salesmanship.model.transaction.MstHisMaintenance;
import sm.co.id.salesmanship.model.transaction.MstItemGroup;
import sm.co.id.salesmanship.model.transaction.MstItemSubCategory;
import sm.co.id.salesmanship.model.transaction.MstItemSubcategoryAlias;
import sm.co.id.salesmanship.model.transaction.MstManufacture;
import sm.co.id.salesmanship.model.transaction.MstOutletInvest;
import sm.co.id.salesmanship.model.transaction.MstParam;
import sm.co.id.salesmanship.model.transaction.MstParamGlobal;
import sm.co.id.salesmanship.model.transaction.MstPpProgram;
import sm.co.id.salesmanship.model.transaction.MstPpProgramDtl;
import sm.co.id.salesmanship.model.transaction.MstProduct;
import sm.co.id.salesmanship.model.transaction.MstProductActCompNonOutlet;
import sm.co.id.salesmanship.model.transaction.MstProductPrice;
import sm.co.id.salesmanship.model.transaction.MstProductSurvey;
import sm.co.id.salesmanship.model.transaction.MstSalesmanRoute;
import sm.co.id.salesmanship.model.transaction.MstVehicle;
import sm.co.id.salesmanship.model.transaction.MstVillage;
import sm.co.id.salesmanship.model.transaction.StartEndRoute;
import sm.co.id.salesmanship.model.transaction.StgMstHisCallsheet;
import sm.co.id.salesmanship.model.transaction.StgMstOutletDedicated;
import sm.co.id.salesmanship.model.transaction.StgMstOutletDl;
import sm.co.id.salesmanship.model.transaction.StgMstProduct;
import sm.co.id.salesmanship.model.transaction.StgMstSalesman;
import sm.co.id.salesmanship.model.transaction.StgMstSalesmanMultiCov;
import sm.co.id.salesmanship.model.transaction.StgMstTarget;
import sm.co.id.salesmanship.model.transaction.StgTrAdjustTkrglgModel;
import sm.co.id.salesmanship.model.transaction.StgTrBppr;
import sm.co.id.salesmanship.model.transaction.StgTrDownloadModel;
import sm.co.id.salesmanship.model.transaction.StgTrDurationNotesModel;
import sm.co.id.salesmanship.model.transaction.StgTrNotationModel;
import sm.co.id.salesmanship.model.transaction.StgTrPosmModel;
import sm.co.id.salesmanship.model.transaction.StgTrSalesModel;
import sm.co.id.salesmanship.model.transaction.StgTrStockRokok;
import sm.co.id.salesmanship.model.transaction.StgTrVisdomNonvisModel;
import sm.co.id.salesmanship.model.transaction.TrAddtUnit;
import sm.co.id.salesmanship.model.transaction.TrBppm;
import sm.co.id.salesmanship.model.transaction.TrBppmAcv;
import sm.co.id.salesmanship.model.transaction.TrBppmDtl;
import sm.co.id.salesmanship.model.transaction.TrBppr;
import sm.co.id.salesmanship.model.transaction.TrBpprBadStock;
import sm.co.id.salesmanship.model.transaction.TrBpprDeliverOrder;
import sm.co.id.salesmanship.model.transaction.TrBppu;
import sm.co.id.salesmanship.model.transaction.TrBppuAlocation;
import sm.co.id.salesmanship.model.transaction.TrCompensation;
import sm.co.id.salesmanship.model.transaction.TrCostRealization;
import sm.co.id.salesmanship.model.transaction.TrMaintenance;
import sm.co.id.salesmanship.model.transaction.TrNonppProgExec;
import sm.co.id.salesmanship.model.transaction.TrPhotoMaintenance;
import sm.co.id.salesmanship.model.transaction.TrPpProgExec;
import sm.co.id.salesmanship.model.transaction.TrProgPhoto;
import sm.co.id.salesmanship.model.transaction.TrPuAcv;
import sm.co.id.salesmanship.model.transaction.TrPuAllocation;
import sm.co.id.salesmanship.model.transaction.TrPuDtl;
import sm.co.id.salesmanship.model.transaction.TrPuHdr;
import sm.co.id.salesmanship.model.transaction.TrSalesPayment;
import sm.co.id.salesmanship.model.transaction.TrSalesPrint;
import sm.co.id.salesmanship.model.transaction.TrStockRokok;
import sm.co.id.salesmanship.model.transaction.TrSvyUpline;
import sm.co.id.salesmanship.model.transaction.TrSvyVolume;
import sm.co.id.salesmanship.model.transaction.TrToppingUp;
import sm.co.id.salesmanship.model.transaction.VaPaymentUi;
import sm.co.id.salesmanship.model.ui.paymentmethod.UiPaymentMethod;
import sm.co.id.salesmanship.model.ui.print.AlokasiPuPrint;
import sm.co.id.salesmanship.model.ui.realisasibiaya.UIPrintRealisasiBiaya;
import sm.co.id.salesmanship.model.upload.StgMstOutletUpModel;
import sm.co.id.salesmanship.model.upload.StgTrToppingUpModel;
import sm.co.id.salesmanship.model.upload.WmsRekapRetur;
import sm.co.id.salesmanship.model.upload.WmsRekapTg;
import sm.co.id.salesmanship.model.upload.WmsStockPosm;
import sm.co.id.salesmanship.model.upload.WmsStockRokok;
import sm.co.id.salesmanship.model.upload.WmsUploadBppr;
import sm.co.id.salesmanship.util.Constant;
import sm.co.id.salesmanship.util.PhotoUtils;
import sm.co.id.salesmanship.util.Strings;
import sm.co.id.salesmanship.util.Utils;
import sm.co.id.salesmanship.view.infobidangpromosi.BidangInformasi;
import sm.co.id.salesmanship.view.infobidangpromosi.BidangInformasiGroup;

/**
 * Created by andreasnu on 8/12/2016.
 */

public class DatabaseHandler extends SQLiteOpenHelper implements DatabaseErrorHandler {

    public static final int SQLITE_STATUS_MEMORY_USED = 0;
    //Database Info
    public static final String DATABASE_NAME = "apps";
    private static final String TAG = "DatabaseHandler";
    private SQLiteDatabase mDb;

    /**
     * Changelog DATABASE_VERSION
     * versi 1.2.3 = 9
     * versi 1.2.4 = 10
     * versi 1.2.5 = 11
     * versi 1.2.6 = 12
     * versi 1.5.0 = 13
     * versi 1.6.0 = 14
     * versi 1.7.0 = 15
     * versi 1.8.0 = 16
     * versi 1.8.1 = 17
     * versi 1.10.0 = 18
     * versi 1.11.0 = 19 -- WMS
     * versi 1.11.1 = 20 -- Bug fix WMS
     * versi 1.13.0 = 22 -- CR WMS (BPPR BPPM beda week)
     * versi 1.13.1 = 23 -- CR WMS (Bug Fix)
     * versi 1.14.0 = 24 -- CR TTO
     * versi 1.15.0 = 25 -- CR TTO AFTER PILOTING
     * versi 1.16.0 = 26 -- CR TTO AFTER PILOTING CYCLE 2
     * versi 1.17.0 = 27 -- CR TTO AFTER PILOTING CYCLE 3
     * versi 1.18.0 = 28 -- CR TTO AFTER PILOTING CYCLE 4
     * versi 1.19.0 = 29 -- CR TTO AFTER ROLLOUT
     * versi 1.20.0 = 30 -- CR Survey Volume 2023 - 2023-04-14, BUG FIX REPORT IS CANCEL, DLL (TTO)
     * versi 1.23.1 = 31 -- CIMERA CI00001016
     * versi 1.21.0 = 32 -- CR NIRWANA
     * versi 1.25.0 = 33 -- CR NIRWANA AFTER PILOTING 1
     * versi 1.26.0 = 34 -- CR KPI Retail
     * versi 1.27.0 = 35 -- CR NIRWANA PIL 5
     * versi 1.28.0 = 36 -- CR LUAR CYCLE dan AE BPPR Stockiest
     * versi 1.29.0 = 37 -- AE - TANDA TERIMA OUTLET MANDATORY KTP
     * versi 2.3.0 = 38 -- CR NIRWANA PILOTING 8, CR KPI Retail Phase 2, CR Cashless Payment, CR TTO 2024
     * versi 2.4.0 = 39 -- CIMERA CI00002524, CR Cashless Payment Phase 2
     * versi 2.4.1 = 40 -- hotfix cashless payment
     * versi 2.6.1 = 41 -- hotfix skemdis (jeremiag - 6 Agustus 2024)
     * versi 2.7.0 = 42 -- AE DESCRIPTION MAINTENANCE UNIT
     * versi 2.11.0 = 44 -- AE ACTIVITY COMPETITOR Q3
     * versi 2.12.0 = 45 -- AE ACTIVITY COMPETITOR Q4
     * versi 2.13.0 = 46 -- CR KPI Retail V1
     * versi 2.13.0 = 46 -- CR BPPM DAN BPPR MULTICOV
     * versi 2.14.0 = 47 -- AE VALIDASI OVERBUDGET POTONGAN PENJUALAN Q1
     * versi 2.16.0 = 48 -- AE STATUS OUTLET AMBASSADOR
     * versi 3.22.0 = 49 -- REALISASI BIAYA
     */
    private static final int DATABASE_VERSION = 49;

    @SuppressLint("StaticFieldLeak")
    private static DatabaseHandler sInstance;
    private String dateString;
    private Context context;

    private String CREATE_INDEX_MST_OUTLET = "CREATE INDEX MST_OUTLET_IDX_1 ON MST_OUTLET(OUTLET_STATUS, DISTRICT_ID, ROUTE)";

    //region Create Table
    // LOG_ACTIVIY
    private String CREATE_LOG_ACTIVITY = "CREATE TABLE IF NOT EXISTS " + LOG_ACTIVIY.TABLE + " (" +
            LOG_ACTIVIY.ID + " INTEGER PRIMARY KEY ASC," +
            LOG_ACTIVIY.USERNAME + " VARCHAR(30) NOT NULL," +
            LOG_ACTIVIY.ACTIVITY + " VARCHAR(100) NOT NULL," +
            LOG_ACTIVIY.DATE_TIME + " DATETIME," +
            LOG_ACTIVIY.WEEK + " NUMERIC(5,0))";

    // LOG_PRINT
    private String CREATE_LOG_PRINT = "CREATE TABLE IF NOT EXISTS " + LOG_PRINT.TABLE + " (" +
            LOG_PRINT.OUTLET_ID + " VARCHAR(30) PRIMARY KEY ASC NOT NULL," +
            LOG_PRINT.VERSI + " NUMERIC(5,0))";

    // MST_ADJUSTMENT_TYPE
    private String CREATE_MST_ADJUSTMENT_TYPE = "CREATE TABLE IF NOT EXISTS " + MST_ADJUSTMENT_TYPE.TABLE + "( " +
            MST_ADJUSTMENT_TYPE.ID + " INTEGER PRIMARY KEY ASC, " +
            MST_ADJUSTMENT_TYPE.ADJUSTMENT_ID + " VARCHAR(20) NOT NULL, " +
            MST_ADJUSTMENT_TYPE.NAME + " VARCHAR(100) NOT NULL, " +
            MST_ADJUSTMENT_TYPE.DESCRIPTION + " VARCHAR(100), " +
            MST_ADJUSTMENT_TYPE.FORM_TYPE + " VARCHAR(100) NOT NULL, " +
            "CONSTRAINT " + MST_ADJUSTMENT_TYPE.TABLE + "_UNIQUE UNIQUE (" +
            MST_ADJUSTMENT_TYPE.ADJUSTMENT_ID + " " +
            "))";

    // MST_BRAND_DEDICATED
    private String CREATE_MST_BRAND_DEDICATED = "CREATE TABLE \"MST_BRAND_DEDICATED\" (\n" +
            "\t\"ID\"\tINTEGER PRIMARY KEY,\n" +
            "\t\"OU_CODE\"\tVARCHAR,\n" +
            "\t\"TERRITORY_CODE\"\tVARCHAR,\n" +
            "\t\"DISTRICT_CODE\"\tVARCHAR,\n" +
            "\t\"ROUTE_CODE\"\tVARCHAR,\n" +
            "\t\"BRAND_DEDICATED\"\tVARCHAR,\n" +
            "\t\"BRAND_SAFETYNET\"\tVARCHAR,\n" +
            "\t\"BRAND_TACTICAL\"\tVARCHAR\n" +
            ");";

    // MST_BRAND_PROG
    private String CREATE_MST_BRAND_PROG = "CREATE TABLE IF NOT EXISTS " + MST_BRAND_PROG.TABLE + "(" +
            MST_BRAND_PROG.ID + " INTEGER PRIMARY KEY ASC, " +
            MST_BRAND_PROG.BRAND_ID + " VARCHAR(20) NOT NULL, " +
            MST_BRAND_PROG.BRAND_NAME + " VARCHAR(50) NOT NULL, " +
            MST_BRAND_PROG.FLAG_DEV + " VARCHAR(20) NOT NULL, " +
            MST_BRAND_PROG.VISIBILITY_ID + " VARCHAR(50) NOT NULL," +
            MST_BRAND_PROG.POSM + " INTEGER NOT NULL," +
            MST_BRAND_PROG.SORT_ORDER + " INTEGER NOT NULL," +
            "CONSTRAINT " + MST_BRAND_PROG.TABLE + "_UNIQUE UNIQUE (" +
            MST_BRAND_PROG.BRAND_ID + " " +
            "))";

    // MST_COST_ITEM
    private String CREATE_MST_COST_ITEM = "CREATE TABLE MST_COST_ITEM (\n" +
            "\tID NUMBER NOT NULL,\n" +
            "\tDFMS_ID NUMBER,\n" +
            "\tITEM_NAME VARCHAR,\n" +
            "\tIS_PPH VARCHAR,\n" +
            "\tIS_TRANS VARCHAR, \n" +
            "\tIS_NEED_DESC VARCHAR, \n" +
            "\tDESC_RULES VARCHAR \n" +
            ");";

    // MST_COST_ITEM_MAP
    private String CREATE_MST_COST_ITEM_MAP = "CREATE TABLE MST_COST_ITEM_MAP (\n" +
            "\tID NUMBER NOT NULL,\n" +
            "\tDFMS_ITEM_ID NUMBER,\n" +
            "\tREMARKS_NAME VARCHAR\n" +
            ");";

    // MST_HIS_CALLSHEET
    private String CREATE_MST_HIS_CALLSHEET = "CREATE TABLE IF NOT EXISTS " + MST_HIS_CALLSHEET.TABLE + " (" +
            MST_HIS_CALLSHEET.ID + " INTEGER PRIMARY KEY ASC," +
            MST_HIS_CALLSHEET.AREA + " VARCHAR(10) NOT NULL," +
            MST_HIS_CALLSHEET.DISTRICT_ID + " VARCHAR(30) NOT NULL," +
            MST_HIS_CALLSHEET.ROUTE + " VARCHAR(2) NOT NULL," +
            MST_HIS_CALLSHEET.OUTLET_ID + " VARCHAR(70) NOT NULL," +
            MST_HIS_CALLSHEET.OUTLET_NAME + " VARCHAR(100) NOT NULL," +
            MST_HIS_CALLSHEET.PRODUCT_ID + " VARCHAR(10) NOT NULL," +
            MST_HIS_CALLSHEET.PRODUCT_CODE + " VARCHAR(150) NOT NULL," +
            MST_HIS_CALLSHEET.WEEK + " INTEGER NOT NULL," +
            MST_HIS_CALLSHEET.STOCK + " NUMERIC(10,0) NOT NULL," +
            MST_HIS_CALLSHEET.BUY + " NUMERIC(10,0) NOT NULL," +
            MST_HIS_CALLSHEET.DISTRIBUTION_NOTATION + " VARCHAR(18)," +
            MST_HIS_CALLSHEET.FLAG_4TL + " VARCHAR(10))";

    // MST_MAP_POSM
    private String CREATE_MST_MAP_POSM = "CREATE TABLE IF NOT EXISTS `MST_MAP_POSM` (" +
            "`ID` INTEGER PRIMARY KEY ASC, " +
            "`OU_CODE` VARCHAR, " +
            "`TERRITORY_CODE` VARCHAR, " +
            "`DISTRICT_CODE` VARCHAR, " +
            "`ROUTE` INTEGER, " +
            "`TOTAL_BRAND` INTEGER, " +
            "`BRAND_ID` VARCHAR)";

    // MST_NON_VISIBLE
    private String CREATE_MST_NON_VISIBLE = "CREATE TABLE IF NOT EXISTS " + MST_NON_VISIBLE.TABLE + "( " +
            MST_NON_VISIBLE.ID + " INTEGER PRIMARY KEY ASC, " +
            MST_NON_VISIBLE.NON_VISIBLE_ID + " VARCHAR(20) NOT NULL, " +
            MST_NON_VISIBLE.NAME + " VARCHAR(100) NOT NULL, " +
            MST_NON_VISIBLE.DESCRIPTION + " VARCHAR(100), " +
            MST_NON_VISIBLE.SORT_ORDER + " INTEGER NOT NULL, " +
            "CONSTRAINT " + MST_NON_VISIBLE.TABLE + "_UNIQUE UNIQUE (" +
            MST_NON_VISIBLE.NON_VISIBLE_ID + " " +
            "))";

    // MST_NOTATION
    private String CREATE_MST_NOTATION = "CREATE TABLE IF NOT EXISTS " + MST_NOTATION.TABLE + "( " +
            MST_NOTATION.ID + " INTEGER PRIMARY KEY ASC, " +
            MST_NOTATION.NOTATION_ID + " VARCHAR(20) NOT NULL, " +
            MST_NOTATION.NAME + " VARCHAR(100) NOT NULL, " +
            MST_NOTATION.DESCRIPTION + " VARCHAR(100), " +
            MST_NOTATION.SORT_ORDER + " INTEGER NOT NULL, " +
            "CONSTRAINT " + MST_NOTATION.TABLE + "_UNIQUE UNIQUE (" +
            MST_NOTATION.NOTATION_ID + " " +
            "))";

    // MST_OUTLET
    private String CREATE_MST_OUTLET = "CREATE TABLE IF NOT EXISTS " + MST_OUTLET.TABLE + " (" +
            MST_OUTLET.ID + " INTEGER PRIMARY KEY ASC," +
            MST_OUTLET.OUTLET_ID + " VARCHAR(70) NOT NULL," +
            MST_OUTLET.AREA + " VARCHAR(10) NOT NULL," +
            MST_OUTLET.TERITORY + " VARCHAR(5) NOT NULL," +
            MST_OUTLET.DISTRICT_ID + " VARCHAR(30) NOT NULL," +
            MST_OUTLET.DISTRICT_CODE + " VARCHAR(5) NOT NULL," +
            MST_OUTLET.ROUTE + " VARCHAR(2) NOT NULL," +
            MST_OUTLET.OUTLET_NAME + " VARCHAR(100) NOT NULL," +
            MST_OUTLET.BARCODE_ID + " VARCHAR(30)," +
            MST_OUTLET.OUTLET_STATUS + " VARCHAR(30) NOT NULL," +
            MST_OUTLET.NEW_OUTLET + " VARCHAR(1)," +
            MST_OUTLET.TIPE_OUTLET + " VARCHAR(10) ," +
            MST_OUTLET.DEDICATED_ROUTE + " VARCHAR(10)," +
            MST_OUTLET.OWNER_NAME + " VARCHAR(100)," +
            MST_OUTLET.LONGITUDE + " VARCHAR(20)," +
            MST_OUTLET.LATITUDE + " VARCHAR(20)," +
            MST_OUTLET.KELURAHAN + " VARCHAR(50)," +
            MST_OUTLET.ADDRESS + " VARCHAR(250)," +
            MST_OUTLET.FLAG_UPDATE + " VARCHAR(1)," +
            MST_OUTLET.COMPANY_ID + " VARCHAR(10)," +
            MST_OUTLET.COMPANY_NAME + " VARCHAR(50)," +
            MST_OUTLET.FLAG_STOCKIST + " VARCHAR(1)," +
            MST_OUTLET.COVERAGE_TYPE + " VARCHAR," +
            MST_OUTLET.FLAG_BARCODE + " VARCHAR(1)," +
            MST_OUTLET.BARCODE_REASON + " VARCHAR(100)," +
            MST_OUTLET.ORACLE_ID + " NUMBER," +
            MST_OUTLET.PARTY_NO + " VARCHAR(50)," +
            MST_OUTLET.SALESFLAG_ID + " VARCHAR," +
            MST_OUTLET.SALESMAN_ID_ORIGINAL + " VARCHAR," +
            MST_OUTLET.TRANSDATE + " NUMBER," +
            MST_OUTLET.SALESMAN_ID_REPLACEMENT + " VARCHAR," +
            MST_OUTLET.VOLUME_TOTAL_GG_OUTLET + " NUMBER," +
            MST_OUTLET.FILE_NAME + " VARCHAR," +
            MST_OUTLET.PHONE_NO + " VARCHAR," +
            MST_OUTLET.FLAG_GPS + " VARCHAR," +
            MST_OUTLET.OTL_COVERAGE + " VARCHAR," +
            MST_OUTLET.COUNTY_ID + " NUMBER," +
            MST_OUTLET.SUBDISTRICT_ID + " NUMBER," +

            MST_OUTLET.VILLAGE_ID + " NUMBER," +
            MST_OUTLET.FLAG_GK + " VARCHAR2(1)," +
            MST_OUTLET.OUTLET_CLASS + " VARCHAR2(100)," +
            MST_OUTLET.CALL_CYCLE + " VARCHAR2(10)," +
            MST_OUTLET.CATEGORY_OUTLET_ID + " NUMBER," +
            MST_OUTLET.JENIS_OUTLET_ID + " NUMBER," +
            MST_OUTLET.OTL_AMBASSADOR_STATUS_ID + " VARCHAR," +
            MST_OUTLET.SHOPBLIND_TYPE_ID + " VARCHAR)";

    // MST_OUTLET_TYPE
    private String CREATE_MST_OUTLET_TYPE = "CREATE TABLE IF NOT EXISTS " + MST_OUTLET_TYPE.TABLE + "( " +
            MST_OUTLET_TYPE.ID + " INTEGER PRIMARY KEY ASC, " +
            MST_OUTLET_TYPE.OUTLET_TYPE_ID + " VARCHAR(20) NOT NULL, " +
            MST_OUTLET_TYPE.OUTLET_TYPE_NAME + " VARCHAR(100) NOT NULL, " +
            MST_OUTLET_TYPE.DESCRIPTION + " VARCHAR(100), " +
            MST_OUTLET_TYPE.SORT_ORDER + " INTEGER NOT NULL, " +
            "CONSTRAINT " + MST_OUTLET_TYPE.TABLE + "_UNIQUE UNIQUE (" +
            MST_OUTLET_TYPE.OUTLET_TYPE_ID + " " +
            "))";

    // MST_PARAMETER_VERSION
    private String CREATE_MST_PARAMETER_VERSION = "CREATE TABLE IF NOT EXISTS " + MST_PARAMETER_VERSION.TABLE + "( " +
            MST_PARAMETER_VERSION.VERSION + " INTEGER NOT NULL" +
            ")";

    // MST_PARAM
    private String CREATE_MST_PARAM = "CREATE TABLE `MST_PARAM` (\n" +
            "\t`ID`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "\t`DFMS_PARAM_ID`\tVARCHAR,\n" +
            "\t`DFMS_PARAM_CODE`\tVARCHAR,\n" +
            "\t`GROUP_PARAM`\tVARCHAR,\n" +
            "\t`PARAM_NAME`\tVARCHAR,\n" +
            "\t`REMARK`\tVARCHAR,\n" +
            "\t`WEEK`\tINTEGER,\n" +
            "\t`DATE_CREATED`\tINTEGER\n" +
            ");";

    // MST_PARAM_GLOBAL
    private String CREATE_MST_PARAM_GLOBAL = "CREATE TABLE MST_PARAM_GLOBAL (\n" +
            "\tID NUMBER,\n" +
            "\tPARAM_ID NUMBER,\n" +
            "\tPARAM_NAME VARCHAR,\n" +
            "\tWEEK NUMBER,\n" +
            "\tGROUP_PARAM VARCHAR,\n" +
            "\tSORT_ORDER NUMBER,\n" +
            "\tREMARKS VARCHAR,\n" +
            "\tAPPS VARCHAR,\n" +
            "\tDATE_CREATED NUMBER\n" +
            ");";

    /**
     * CI00000321
     *
     * @author michells - 2022-11-15
     * change primary key to autoincrement and add column pp_program_id
     */

    // MST_PP_PROGRAM
    private String CREATE_MST_PP_PROGRAM = "CREATE TABLE IF NOT EXISTS `MST_PP_PROGRAM` (" +
            "`ID` INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "`PP_PROGRAM_ID` INTEGER, " +
            "`P2_NUMBER` INTEGER, " +
            "`PROGRAM_NAME` VARCHAR, " +
            "`BRAND_ID` VARCHAR, " +
            "`CHANNEL_ID` INTEGER, " +
            "`DATE_CREATED` INTEGER, " +
            "`FLAG_COA` VARCHAR, " +
            "`BRAND_CODE` VARCHAR, " +
            "`FLAG_PILAR` VARCHAR, " +
            "`IS_OPEN` VARCHAR, " +
            "`NONPB` VARCHAR, " +
            "`IS_CONTRACT` VARCHAR, " +
            "`PROGRAM_ID` NUMBER, " +
            "`PROGRAM_RANK` NUMBER, " +
            "`BENEFIT_RECIPIENT_ID` NUMBER, " +
            "`END_PERIODE` LONG," +
            "`IS_GENERATE_TTO` VARCHAR, " +
            "`IS_RECIPIENT_SIGN` VARCHAR)";

    // MST_PRODUCT
    private String CREATE_MST_PRODUCT = "CREATE TABLE IF NOT EXISTS " + MST_PRODUCT.TABLE + " (" +
            MST_PRODUCT.ID + " VARCHAR(20) PRIMARY KEY," +
            MST_PRODUCT.PRODUCT_ID + " VARCHAR(10) NOT NULL," +
            MST_PRODUCT.PRODUCT_CODE + " VARCHAR(150) NOT NULL," +
            MST_PRODUCT.PRODUCT_NAME + " VARCHAR(150) NOT NULL," +
            MST_PRODUCT.PRODUCT_SEQN + " INTEGER NOT NULL," +
            MST_PRODUCT.PRICE + " NUMERIC(12,0)," +
            MST_PRODUCT.AREA + " VARCHAR(10)," +
            MST_PRODUCT.FLAG_RIS + " VARCHAR(10), " +
            // WMS HH HELPER 25-01-2022
            MST_PRODUCT.IS_DUS + " INTEGER, " +
            MST_PRODUCT.UOM_BAL + " INTEGER, " +
            MST_PRODUCT.UOM_SLF + " INTEGER, " +
            MST_PRODUCT.UOM_BKS + " INTEGER " + ")";

    // MST_PRODUCT_SURVEY
    private String CREATE_MST_PRODUCT_SURVEY = "CREATE TABLE IF NOT EXISTS MST_PRODUCT_SURVEY (" +
            "ID NUMBER PRIMARY KEY," +
            "PRODUCT_ID NUMBER," +
            "PRODUCT_CODE VARCHAR," +
            "PRODUCT_NAME VARCHAR," +
            "PRODUCT_SEQN NUMBER," +
            "SUBCHANNEL_NAME VARCHAR," +
            "DATE_CREATED NUMBER," +
            "OU_CODE VARCHAR)";

    // MST_REASON_BARCODE
    private String CREATE_MST_REASON_BARCODE = "CREATE TABLE IF NOT EXISTS " + MST_REASON_BARCODE.TABLE + "( " +
            MST_REASON_BARCODE.ID + " INTEGER PRIMARY KEY ASC, " +
            MST_REASON_BARCODE.REASON_ID + " VARCHAR(20) NOT NULL, " +
            MST_REASON_BARCODE.REASON_NAME + " VARCHAR(100) NOT NULL, " +
            MST_REASON_BARCODE.DESCRIPTION + " VARCHAR(100), " +
            MST_REASON_BARCODE.SORT_ORDER + " INTEGER NOT NULL, " +
            "CONSTRAINT " + MST_REASON_BARCODE.TABLE + "_UNIQUE UNIQUE (" +
            MST_REASON_BARCODE.REASON_ID + " " +
            "))";

    // MST_SALES_TYPE
    private String CREATE_MST_SALES_TYPE = "CREATE TABLE IF NOT EXISTS " + MST_SALES_TYPE.TABLE + "( " +
            MST_SALES_TYPE.ID + " INTEGER PRIMARY KEY ASC, " +
            MST_SALES_TYPE.SALES_TYPE_ID + " VARCHAR(20) NOT NULL, " +
            MST_SALES_TYPE.NAME + " VARCHAR(100) NOT NULL, " +
            MST_SALES_TYPE.DESCRIPTION + " VARCHAR(100), " +
            MST_SALES_TYPE.SORT_ORDER + " INTEGER, " +
            "CONSTRAINT " + MST_SALES_TYPE.TABLE + "_UNIQUE UNIQUE (" +
            MST_SALES_TYPE.SALES_TYPE_ID + " " +
            "))";

    // MST_SALESMAN
    private String CREATE_MST_SALESMAN = "CREATE TABLE IF NOT EXISTS " + MST_SALESMAN.TABLE + " (" +
            MST_SALESMAN.ID + " INTEGER PRIMARY KEY ASC," +
            MST_SALESMAN.USERNAME + " VARCHAR(30) NOT NULL," +
            MST_SALESMAN.SALESMAN_NAME + " VARCHAR(100) NOT NULL," +
            MST_SALESMAN.REGION + " VARCHAR(10) NOT NULL," +
            MST_SALESMAN.AREA + " VARCHAR(5) NOT NULL," +
            MST_SALESMAN.TERITORY + " VARCHAR(5) NOT NULL," +
            MST_SALESMAN.DISTRICT_ID + " VARCHAR(30) NOT NULL," +
            MST_SALESMAN.DISTRICT_CODE + " VARCHAR(5) NOT NULL," +
            MST_SALESMAN.ROUTE + " VARCHAR(3) NOT NULL," +
            MST_SALESMAN.NIK + " VARCHAR(6) NOT NULL," +
            MST_SALESMAN.AO + " VARCHAR(10) NOT NULL," +
            MST_SALESMAN.EMAIL + " VARCHAR(50) NOT NULL," +
            MST_SALESMAN.SPV_ID + " VARCHAR(30) NOT NULL," +
            MST_SALESMAN.SPV_NAME + " VARCHAR(50) NOT NULL," +
            MST_SALESMAN.WEEK + " NUMERIC(5,0) NOT NULL," +
            MST_SALESMAN.FLAG_GK + " VARCHAR(50) NOT NULL," +
            MST_SALESMAN.IS_STOCKIST + " VARCHAR(50))";

    // MST_SALESMAN_ROUTE
    private String CREATE_MST_SALESMAN_ROUTE = "CREATE TABLE MST_SALESMAN_ROUTE (\n" +
            "\tID NUMBER NOT NULL,\n" +
            "\tSALESMAN_ID NUMBER,\n" +
            "\tAO_ID NUMBER,\n" +
            "\tAO_CODE VARCHAR,\n" +
            "\tOU_ID NUMBER,\n" +
            "\tOU_CODE VARCHAR,\n" +
            "\tTERRITORY_ID NUMBER,\n" +
            "\tTERRITORY_CODE VARCHAR,\n" +
            "\tDISTRICT_ID NUMBER,\n" +
            "\tDISTRICT_CODE VARCHAR,\n" +
            "\tROUTE_ID NUMBER,\n" +
            "\tROUTE_CODE VARCHAR,\n" +
            "\tCOVERAGE VARCHAR,\n" +
            "\tSUBCOVERAGE VARCHAR,\n" +
            "\tCOST_CENTER NUMBER,\n" +
            "\tPOSITION_NAME VARCHAR\n" +
            ");";

    // MST_STATUS_OUTLET
    private String CREATE_MST_STATUS_OUTLET = "CREATE TABLE IF NOT EXISTS " + MST_STATUS_OUTLET.TABLE + "( " +
            MST_STATUS_OUTLET.ID + " INTEGER PRIMARY KEY ASC, " +
            MST_STATUS_OUTLET.STATUS_OUTLET_ID + " VARCHAR(20) NOT NULL, " +
            MST_STATUS_OUTLET.NAME + " VARCHAR(100) NOT NULL, " +
            MST_STATUS_OUTLET.DESCRIPTION + " VARCHAR(100), " +
            MST_STATUS_OUTLET.SORT_ORDER + " INTEGER NOT NULL, " +
            "CONSTRAINT " + MST_STATUS_OUTLET.TABLE + "_UNIQUE UNIQUE (" +
            MST_STATUS_OUTLET.STATUS_OUTLET_ID + " " +
            "))";

    // MST_TARGET
    private String CREATE_MST_TARGET = "CREATE TABLE IF NOT EXISTS " + MST_TARGET.TABLE + " (" +
            MST_TARGET.ID + " INTEGER PRIMARY KEY ASC," +
            MST_TARGET.WEEK + " INTEGER NOT NULL," +
            MST_TARGET.AREA + " VARCHAR(10) NOT NULL," +
            MST_TARGET.DISTRICT_ID + " VARCHAR(30) NOT NULL," +
            MST_TARGET.DISTRICT_CODE + " VARCHAR(5) NOT NULL," +
            MST_TARGET.ROUTE + " VARCHAR(2) NOT NULL," +
            MST_TARGET.PARAMETER_ID + " VARCHAR(20) NOT NULL," +
            MST_TARGET.PARAMETER_NAME + " VARCHAR(50) NOT NULL," +
            MST_TARGET.TARGET + " NUMERIC(10,2) NOT NULL," +
            MST_TARGET.ACHIEVEMENT + " NUMERIC(10,2)," +
            MST_TARGET.PERCENT + " NUMERIC(5,2)," +
            MST_TARGET.PRODUCT_ID + " VARCHAR(10) NOT NULL," +
            MST_TARGET.PRODUCT_CODE + " VARCHAR(150) NOT NULL)";

    // MST_VEHICLE
    private String CREATE_MST_VEHICLE = "CREATE TABLE MST_VEHICLE (\n" +
            "\tID NUMBER NOT NULL,\n" +
            "\tVEHICLE_DESC VARCHAR,\n" +
            "\tPOLICE_NO VARCHAR,\n" +
            "\tAO_CODE VARCHAR,\n" +
            "\tOU_CODE VARCHAR,\n" +
            "\tWEEK NUMBER\n" +
            ");";

    // MST_VISIBILITY_DOMINAN
    private String CREATE_MST_VISIBILITY_DOMINAN = "CREATE TABLE IF NOT EXISTS " + MST_VISIBILITY_DOMINAN.TABLE + "( " +
            MST_VISIBILITY_DOMINAN.ID + " INTEGER PRIMARY KEY ASC, " +
            MST_VISIBILITY_DOMINAN.VISIBILITY_ID + " VARCHAR(20) NOT NULL, " +
            MST_VISIBILITY_DOMINAN.NAME + " VARCHAR(100) NOT NULL, " +
            MST_VISIBILITY_DOMINAN.DESCRIPTION + " VARCHAR(100), " +
            MST_VISIBILITY_DOMINAN.SORT_ORDER + " INTEGER NOT NULL, " +
            "CONSTRAINT " + MST_VISIBILITY_DOMINAN.TABLE + "_UNIQUE UNIQUE (" +
            MST_VISIBILITY_DOMINAN.VISIBILITY_ID + " " +
            "))";

    /*
     #CR OUTLET DEDICATED
    14 SEPTEMBER 2020
    Developed by antonyh
     */
    //MST_OUTLET_DEDICATED
    private String CREATE_MST_OUTLET_DEDICATED = "CREATE TABLE IF NOT EXISTS " + MST_OUTLET_DEDICATED.TABLE + " (" +
            MST_OUTLET_DEDICATED.ID + " INTEGER PRIMARY KEY ASC," +
            MST_OUTLET_DEDICATED.OUTLET_ID + " VARCHAR(100)," +
            MST_OUTLET_DEDICATED.FOCUS_NAME + " VARCHAR(200)," +
            MST_OUTLET_DEDICATED.BRAND_CODE + " VARCHAR(30)," +
            MST_OUTLET_DEDICATED.SEQUENCE + " INTEGER)";

    /* CR HH Retail - 25 Mei 2021
       1. Maintenance Unit
       2. LinkAja!
       Developed by yufrim
    */
    //MST_OUTLET_INVEST
    private String CREATE_MST_OUTLET_INVEST = "CREATE TABLE IF NOT EXISTS " + MST_OUTLET_INVEST.TABLE + " (" +
            MST_OUTLET_INVEST.ID + " NUMBER PRIMARY KEY ASC," +
            MST_OUTLET_INVEST.PLAN_ID + " NUMBER," +
            MST_OUTLET_INVEST.OUTLET_ID + " VARCHAR(70)," +
            MST_OUTLET_INVEST.BRAND_CODE + " VARCHAR(3)," +
            MST_OUTLET_INVEST.ITEM_GROUP_ID + " VARCHAR(20)," +
            MST_OUTLET_INVEST.ITEM_GROUP_NAME + " VARCHAR(200)," +
            MST_OUTLET_INVEST.QTY + " NUMBER," +
            MST_OUTLET_INVEST.WEEK + " NUMBER," +
            MST_OUTLET_INVEST.DATE_CREATED + " NUMBER," +
            MST_OUTLET_INVEST.PROGRAM_TYPE_NAME + " VARCHAR(50))";

    //MST_ITEM_SUBCATEGORY
    private String CREATE_MST_ITEM_SUBCATEGORY = "CREATE TABLE IF NOT EXISTS " + MST_ITEM_SUBCATEGORY.TABLE + " (" +
            MST_ITEM_SUBCATEGORY.ID + " NUMBER PRIMARY KEY ASC," +
            MST_ITEM_SUBCATEGORY.ITEM_CATEGORY_ID + " NUMBER," +
            MST_ITEM_SUBCATEGORY.ITEM_SUBCATEGORY_ID + " NUMBER," +
            MST_ITEM_SUBCATEGORY.ITEM_SUBCATEGORY_NAME + " VARCHAR(150)," +
            MST_ITEM_SUBCATEGORY.WEEK + " NUMBER," +
            MST_ITEM_SUBCATEGORY.DATE_CREATED + " NUMBER)";

    //MST_ITEM
    private String CREATE_MST_ITEM_GROUP = "CREATE TABLE IF NOT EXISTS " + MST_ITEM_GROUP.TABLE + " (" +
            MST_ITEM_GROUP.ID + " NUMBER PRIMARY KEY ASC," +
            MST_ITEM_GROUP.ITEM_GROUP_ID + " NUMBER," +
            MST_ITEM_GROUP.ITEM_GROUP_NAME + " VARCHAR(250)," +
            MST_ITEM_GROUP.ITEM_SUBCATEGORY_ID + " NUMBER," +
            MST_ITEM_GROUP.DATE_CREATED + " NUMBER)";

    //MST_VILLAGE
    private String CREATE_MST_VILLAGE = "CREATE TABLE IF NOT EXISTS " + MST_VILLAGE.TABLE + " (" +
            MST_VILLAGE.ID + " NUMBER PRIMARY KEY ASC," +
            MST_VILLAGE.COUNTY_ID + " NUMBER," +
            MST_VILLAGE.COUNTY_NAME + " VARCHAR(100)," +
            MST_VILLAGE.SUBDISTRICT_ID + " NUMBER," +
            MST_VILLAGE.SUBDISTRICT_NAME + " VARCHAR(100)," +
            MST_VILLAGE.VILLAGE_ID + " NUMBER," +
            MST_VILLAGE.VILLAGE_NAME + " VARCHAR(100)," +
            MST_VILLAGE.DATE_CREATED + " NUMBER," +
            MST_VILLAGE.SIGN_MODIFIED + " NUMBER," +
            MST_VILLAGE.DISTRICT_ID + " VARCHAR(30)," +
            MST_VILLAGE.WEEK + " NUMBER," +
            MST_VILLAGE.STATUS + " VARCHAR2(1)," +
            MST_VILLAGE.USER_CREATED + " VARCHAR2(30)," +
            MST_VILLAGE.USER_MODIFIED + " VARCHAR2(30)," +
            MST_VILLAGE.DATE_MODIFIED + " DATE," +
            MST_VILLAGE.PROVINCE_ID + " NUMBER," +
            MST_VILLAGE.PROVINCE_NAME + " VARCHAR(100))";

    /**
     * modify by hafizhr
     * 19 Jun 2024
     * AE Q2 2024 Maintenance Unit - Add Kolom Description
     */
    //TR_MAINTENANCE
    private String CREATE_TR_MAINTENANCE = "CREATE TABLE IF NOT EXISTS " + TR_MAINTENANCE.TABLE + " (" +
            TR_MAINTENANCE.ID + " NUMBER PRIMARY KEY ASC," +
            TR_MAINTENANCE.OUTLET_ID + " VARCHAR(30)," +
            TR_MAINTENANCE.PLAN_ID + " NUMBER," +
            TR_MAINTENANCE.ITEM_GROUP_ID + " NUMBER," +
            TR_MAINTENANCE.ITEM_GROUP_NAME + " VARCHAR(150)," +
            TR_MAINTENANCE.BRAND_CODE + " VARCHAR(150)," +
            TR_MAINTENANCE.QTY_CONTRACT + " NUMBER," +
            TR_MAINTENANCE.QTY_ACTUAL + " NUMBER," +
            TR_MAINTENANCE.PLANOGRAM + " NUMBER," +
            TR_MAINTENANCE.CLEAN + " NUMBER," +
            TR_MAINTENANCE.POSITION + " NUMBER," +
            TR_MAINTENANCE.GOOD_VISUAL + " NUMBER," +
            TR_MAINTENANCE.BAD_VISUAL + " NUMBER," +
            TR_MAINTENANCE.FLAG_PHOTO + " NUMBER," +
            TR_MAINTENANCE.WEEK + " NUMBER," +
            TR_MAINTENANCE.SUBCATEGORY_ID + " NUMBER," +
            TR_MAINTENANCE.CATEGORY_ID + " NUMBER," +
            TR_MAINTENANCE.USER_CREATED + " VARCHAR(30)," +
            TR_MAINTENANCE.DATE_CREATED + " NUMBER," +
            TR_MAINTENANCE.ITEM_ID + " NUMBER," +
            TR_MAINTENANCE.ITEM_NAME + " VARCHAR2(100)," +
            TR_MAINTENANCE.ITEM_DESCRIPTION + " VARCHAR2(1000)," +
            TR_MAINTENANCE.ITEM_SIZE + " VARCHAR2(100)," +
            TR_MAINTENANCE.VIS_GOOD + " NUMBER," +
            TR_MAINTENANCE.VIS_BAD + " NUMBER, " +
            TR_MAINTENANCE.DESCRIPTION + " VARCHAR2(250) )";

    //TR_PHOTO_MAINTENANCE
    private String CREATE_TR_PHOTO_MAINTENANCE = "CREATE TABLE IF NOT EXISTS " + TR_PHOTO_MAINTENANCE.TABLE + " (" +
            TR_PHOTO_MAINTENANCE.ID + " NUMBER PRIMARY KEY ASC," +
            TR_PHOTO_MAINTENANCE.OUTLET_ID + " VARCHAR(70)," +
            TR_PHOTO_MAINTENANCE.ITEM_GROUP_ID + " NUMBER," +
            TR_PHOTO_MAINTENANCE.WEEK + " NUMBER," +
            TR_PHOTO_MAINTENANCE.FILE_NAME + " TEXT," +
            TR_PHOTO_MAINTENANCE.DATE_CREATED + " NUMBER," +
            TR_PHOTO_MAINTENANCE.SORT_ORDER + " NUMBER," +
            TR_PHOTO_MAINTENANCE.TR_MAINTENANCE_ID + " NUMBER," +
            TR_PHOTO_MAINTENANCE.TR_ADDT_UNIT_ID + " NUMBER," +
            TR_PHOTO_MAINTENANCE.SUBCATEGORY_ID + " NUMBER," +
            TR_PHOTO_MAINTENANCE.ITEM_ID + " NUMBER," +
            TR_PHOTO_MAINTENANCE.STATUS + " VARCHAR(1))";

    //MST_HIS_MAINTENANCE
    private String CREATE_MST_HIS_MAINTENANCE = "CREATE TABLE IF NOT EXISTS " + MST_HIS_MAINTENANCE.TABLE + " (" +
            MST_HIS_MAINTENANCE.ID + " NUMBER PRIMARY KEY ASC," +
            MST_HIS_MAINTENANCE.OUTLET_ID + " VARCHAR(30)," +
            MST_HIS_MAINTENANCE.ITEM_GROUP_ID + " NUMBER," +
            MST_HIS_MAINTENANCE.ITEM_GROUP_NAME + " VARCHAR(150)," +
            MST_HIS_MAINTENANCE.BRAND_CODE + " VARCHAR(150)," +
            MST_HIS_MAINTENANCE.QTY_CONTRACT + " NUMBER," +
            MST_HIS_MAINTENANCE.QTY_ACTUAL + " NUMBER," +
            MST_HIS_MAINTENANCE.PLANOGRAM + " NUMBER," +
            MST_HIS_MAINTENANCE.CLEAN + " NUMBER," +
            MST_HIS_MAINTENANCE.POSITION + " NUMBER," +
            MST_HIS_MAINTENANCE.GOOD_VISUAL + " NUMBER," +
            MST_HIS_MAINTENANCE.BAD_VISUAL + " NUMBER," +
            MST_HIS_MAINTENANCE.SUB_CATEGORY_ID + " NUMBER," +
            MST_HIS_MAINTENANCE.CATEGORY_ID + " NUMBER," +
            MST_HIS_MAINTENANCE.ITEM_NAME + " VARCHAR(100))";

    // START_END_ROUTE
    private String CREATE_START_END_ROUTE = "CREATE TABLE IF NOT EXISTS START_END_ROUTE ( " +
            "'ID' INTEGER PRIMARY KEY ASC, " +
            "'OU_CODE' VARCHAR(10), " +
            "'TERRITORY_CODE' VARCHAR(10), " +
            "'DISTRICT_CODE' VARCHAR(10), " +
            "'ROUTE' INTEGER, " +
            "'TIME_START' INTEGER, " +
            "'TIME_END' INTEGER, " +
            "'LONGITUDE_START' VARCHAR(200), " +
            "'LATITUDE_START' VARCHAR(200), " +
            "'LONGITUDE_END' VARCHAR(200), " +
            "'LATITUDE_END' VARCHAR(200), " +
            "'KM_AWAL' INTEGER, " +
            "'KM_AKHIR' INTEGER, " +
            "'WEEK' INTEGER, " +
            "'USER_CREATED' VARCHAR(50), " +
            "'DATE_CREATED' INTEGER, " +
            "'GMT' VARCHAR(20)" +
            ")";

    // TEMP_TR_NOTATION
    private String CREATE_TEMP_NOTATION = "CREATE TABLE IF NOT EXISTS " + TEMP_TR_NOTATION.TABLE + " (" +
            TEMP_TR_NOTATION.ID + " INTEGER PRIMARY KEY ASC," +
            TEMP_TR_NOTATION.DISTRICT_ID + " VARCHAR(30) NOT NULL," +
            TEMP_TR_NOTATION.ROUTE + " VARCHAR(2) NOT NULL," +
            TEMP_TR_NOTATION.OUTLET_ID + " VARCHAR(70) NOT NULL," +
            TEMP_TR_NOTATION.OUTLET_NAME + " VARCHAR(100) NOT NULL," +
            TEMP_TR_NOTATION.PRODUCT_ID + " VARCHAR(10) NOT NULL," +
            TEMP_TR_NOTATION.PRODUCT_CODE + " VARCHAR(150) NOT NULL," +
            TEMP_TR_NOTATION.PRODUCT_SEQN + " INTEGER NOT NULL," +
            TEMP_TR_NOTATION.STOCK + " NUMERIC(6,0) NOT NULL," +
            TEMP_TR_NOTATION.BUY_SA + " NUMERIC(6,0)," +
            TEMP_TR_NOTATION.FACEUP + " NUMERIC(6,0)," +
            TEMP_TR_NOTATION.WHOLESALE_SCHEDULE + " NUMERIC(6,0)," +
            TEMP_TR_NOTATION.OOS_DURATION + " NUMERIC(6,0)," +
            TEMP_TR_NOTATION.DISTRIBUTION_NOTATION + " VARCHAR(10))";
    /***
     * --CR WMS by khalida
     * Tabel ini punya historis / backup tabel,
     * Pastikan penambahan kolom di tabel ini mengikuti tabel replicate nya
     *
     * modified by dimass02
     * 7 Feb 2024
     * AE BPPR Stockiest
     * */
    // TR_ADJUST_TKRGLG
    private String CREATE_TR_ADJUST_TKRGLG = "CREATE TABLE IF NOT EXISTS " + TR_ADJUST_TKRGLG.TABLE + " (" +
            TR_ADJUST_TKRGLG.ID + " INTEGER PRIMARY KEY ASC," +
            TR_ADJUST_TKRGLG.DISTRICT_ID + " VARCHAR(30) NOT NULL," +
            TR_ADJUST_TKRGLG.ROUTE + " VARCHAR(20) NOT NULL," +
            TR_ADJUST_TKRGLG.ADJUSTMENT_TYPE + " VARCHAR(100) NOT NULL," +
            TR_ADJUST_TKRGLG.PRODUCT_ID + " VARCHAR(10) NOT NULL," +
            TR_ADJUST_TKRGLG.PRODUCT_CODE + " VARCHAR(150) NOT NULL," +
            TR_ADJUST_TKRGLG.QTY + " NUMERIC(6,0) NOT NULL," +
            TR_ADJUST_TKRGLG.PRICE + " NUMERIC(12,0) NOT NULL," +
            TR_ADJUST_TKRGLG.TOTAL_PRICE + " NUMERIC(12,0) NOT NULL," +
            TR_ADJUST_TKRGLG.BANDEROLE + " NUMERIC(12,0) NOT NULL," +
            TR_ADJUST_TKRGLG.BPPR_NO + " VARCHAR(30)," +
            TR_ADJUST_TKRGLG.TR_BPPR_STOCKIEST_ID + " NUMBER )";
    /***
     * --CR WMS by khalida
     * Tabel ini punya historis / backup tabel,
     * Pastikan penambahan kolom di tabel ini mengikuti tabel replicate nya
     * */
    // TR_BPPM
    private String CREATE_TR_BPPM = "CREATE TABLE `TR_BPPM` (" +
            "`ID` INTEGER PRIMARY KEY, " +
            "`BPPM_NO` VARCHAR, " +
            "`BPPM_DATE` LONG, " +
            "`VERSION` VARCHAR, " +
            "`DATE_CREATED` LONG, " +
            "`IS_PRINT` VARCHAR, " + // CR Print Summary 18-11-2021
            "`COUNT_PRINT` INTEGER, " + // CR Print Summary 25-11-2021
            "`BPPM_STATUS_ID` INTEGER, " + // WMS HH HELPER 25-01-2022
            "`LAST_UPLOAD` INTEGER," +
            "`USER_CREATED` VARCHAR" + ", " +
            "`IS_REVISE` VARCHAR )";
    /***
     * --CR WMS by khalida
     * Tabel ini punya historis / backup tabel,
     * Pastikan penambahan kolom di tabel ini mengikuti tabel replicate nya
     * */
    // TR_BPPM_ALOCATION
    private String CREATE_TR_BPPM_ALOCATION = "CREATE TABLE `TR_BPPM_ALOCATION` (" +
            "`ID` INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "`TR_BPPM_DTL_ID` INTEGER, " +
            "`PP_PROGRAM_ID` INTEGER, " +
            "`PROGRAM_TYPE_ID` INTEGER, " +
            "`QTY` INTEGER, " +
            "`STATUS` VARCHAR, " +
            "`DATE_CREATED` INTEGER, " +
            "`USER_CREATED` VARCHAR)";
    /***
     * --CR WMS by khalida
     * Tabel ini punya historis / backup tabel,
     * Pastikan penambahan kolom di tabel ini mengikuti tabel replicate nya
     *
     * @modified by lukkis 9 JAN 2023
     * AR TTO DES 2022
     * Add column ITEM_MAIN_CATEGORY
     *
     * @modified by muhammadf16 26 FEB 2024
     * CR TTO 2024
     * Add column ITEM_WEIGHT
     * */
    // TR_BPPM_DTL
    private String CREATE_TR_BPPM_DTL = "CREATE TABLE `TR_BPPM_DTL` (" +
            "`ID` INTEGER PRIMARY KEY, " +
            "`TR_BPPM_ID` INTEGER, " +
            "`ITEM_ID` INTEGER, " +
            "`ITEM_CODE` VARCHAR, " +
            "`ITEM_NAME` VARCHAR, " +
            "`ITEM_DESCRIPTION` VARCHAR, " +
            "`ITEM_SUBCATEGORY` VARCHAR, " +
            "`ITEM_SIZE` VARCHAR, " +
            "`ITEM_CATEGORY` VARCHAR, " +
            "`ITEM_BRAND` VARCHAR, " +
            "`STOCK_AWAL_GOOD` INTEGER, " +
            "`STATUS` VARCHAR, " +
            "`DATE_CREATED` INTEGER, " +
            "`STOCK_FINAL_GOOD` INTEGER, " + // WMS HH HELPER 25-01-2022
            "`RETURN_QTY_BARU` INTEGER, " +
            "`RETURN_QTY_BEKAS` INTEGER, " +
            "`RETURN_QTY_RUSAK` INTEGER, " +
            "`USER_CREATED` VARCHAR, " +
            "`ITEM_MAIN_CATEGORY` VARCHAR, " +
            "`ITEM_WEIGHT` VARCHAR "
            + ")";
    /***
     * --CR WMS by khalida
     * Tabel ini punya historis / backup tabel,
     * Pastikan penambahan kolom di tabel ini mengikuti tabel replicate nya
     * */
    // TR_BPPR
    private String CREATE_TR_BPPR = "CREATE TABLE `TR_BPPR` (" +
            "`ID` NUMBER PRIMARY KEY, " +
            "`BPPR_NO` VARCHAR, " +
            "`BPPR_TYPE` VARCHAR, " +
            "`BPPR_DATE` NUMBER, " +
            "`IS_ACTIVE` VARCHAR, " +
            "`DATE_CREATED` NUMBER, " +
            "`LAST_NP` VARCHAR, " +
            "`BPPR_VERSION` INTEGER, " + // WMS HH HELPER 25-01-2022
            "`BPPR_STATUS_ID` INTEGER, " +
            "`LAST_UPLOAD` INTEGER, " +
            "`BPPR_CLOSED` VARCHAR" + ")"; // WMS HH HELPER 03-02-2022
    //region HIST //
    private String CREATE_TR_BPPR_HIST = "CREATE TABLE `TR_BPPR_HIST` (" +
            "`ID` NUMBER, " +
            "`BPPR_NO` VARCHAR, " +
            "`BPPR_TYPE` VARCHAR, " +
            "`BPPR_DATE` NUMBER, " +
            "`IS_ACTIVE` VARCHAR, " +
            "`DATE_CREATED` NUMBER, " +
            "`LAST_NP` VARCHAR, " +
            "`BPPR_VERSION` INTEGER, " + // WMS HH HELPER 25-01-2022
            "`BPPR_STATUS_ID` INTEGER, " +
            "`LAST_UPLOAD` INTEGER, " +
            "`BPPR_CLOSED` VARCHAR" + ")"; // WMS HH HELPER 03-02-2022
    private String CREATE_TR_BPPR_BAD_STOCK_HIST = "CREATE TABLE IF NOT EXISTS `TR_BPPR_BAD_STOCK_HIST` ("
            + "`ID` INTEGER ,"
            + "`BAD_STOCK_TYPE_ID` NUMBER,"
            + "`TRANSACTION_ID` NUMBER,"
            + "`BPPR_NO` VARCHAR2(30),"
            + "`IS_CHECKED` VARCHAR2(1),"
            + "`PRODUCT_ID` NUMBER,"
            + "`PRODUCT_CODE` VARCHAR2(30),"
            + "`PRICE_BAND` NUMBER,"
            + "`QTY_PACK` NUMBER,"
            + "`PRICE_PACK` NUMBER,"
            + "`TOTAL_PRICE` NUMBER,"
            + "`STATUS` VARCHAR2(20),"
            + "`DATE_CREATED` LONG,"
            + "`USER_CREATED` VARCHAR2(20),"
            + "`WEEK` NUMBER"
            + ")";
    private String CREATE_TR_BPPR_DELIVER_ORDER_HIST = "CREATE TABLE IF NOT EXISTS `TR_BPPR_DELIVER_ORDER_HIST` ("
            + "`ID` INTEGER, "
            + "`TR_BPPR_ID` INTEGER, "
            + "`BPPR_NO` VARCHAR2(30), "
            + "`VHC_NOPOL` VARCHAR2(30), "
            + "`DRIVER` VARCHAR2(100), "
            + "`SJ_NO` VARCHAR2(20), "
            + "`SJ_DATE` NUMBER, "
            + "`DATE_CREATED` INTEGER, "
            + "`USER_CREATED` VARCHAR2(50), "
            + "`FLAG_ADD` VARCHAR2(50), "
            + "`FLAG_UPLOAD` VARCHAR2(1), "
            + "`DATE_MODIFIED` INTEGER, "
            + "`USER_MODIFIED` VARCHAR2(50),"
            + "`STATUS` VARCHAR2(1)," // 03 FEB 2022 ADD STATUS AND FLAG_VALIDATE
            + "`FLAG_VALIDATE` VARCHAR2(1), "
            + "`SJ_NO_NAME` VARCHAR " // 21 APR 2022
            + ")";
    private String CREATE_TR_STOCK_ROKOK_HIST = "CREATE TABLE `TR_STOCK_ROKOK_HIST` (" +
            "`ID` NUMBER , " +
            "`TR_BPPR_ID` NUMBER, " +
            "`BPPR_NO` VARCHAR, " +
            "`PRODUCT_ID` INTEGER, " +
            "`PRODUCT_CODE` VARCHAR, " +
            "`PRODUCT_NAME` VARCHAR, " +
            "`PRODUCT_SEQ` NUMBER, " +
            "`TOT_STOCK_AWAL` NUMBER, " +
            "`TOT_STOCK_GOOD` NUMBER, " +
            "`TOT_STOCK_BAD` NUMBER, " +
            "`TOT_STOCK_USED` NUMBER, " +
            "`STATUS` VARCHAR, " +
            "`DATE_CREATED` INTEGER, " +
            "`STOCK_INIT_DUS` INTEGER, " + // WMS HH HELPER 25-01-2022
            "`STOCK_INIT_BAL` INTEGER, " +
            "`STOCK_INIT_SLF` INTEGER, " +
            "`STOCK_INIT_BKS` INTEGER, " +
            "`STOCK_FINAL_GOOD_DUS` INTEGER, " +
            "`STOCK_FINAL_GOOD_BAL` INTEGER, " +
            "`STOCK_FINAL_GOOD_SLF` INTEGER, " +
            "`STOCK_FINAL_GOOD_BKS` INTEGER, " +
            "`TOT_STOCK_USED_ORI` NUMBER " + ")";
    private String CREATE_TR_ADJUST_TKRGLG_HIST = "CREATE TABLE IF NOT EXISTS " + TR_ADJUST_TKRGLG.TABLE_HIST + " (" +
            TR_ADJUST_TKRGLG.ID + " INTEGER," +
            TR_ADJUST_TKRGLG.DISTRICT_ID + " VARCHAR(30) NOT NULL," +
            TR_ADJUST_TKRGLG.ROUTE + " VARCHAR(20) NOT NULL," +
            TR_ADJUST_TKRGLG.ADJUSTMENT_TYPE + " VARCHAR(100) NOT NULL," +
            TR_ADJUST_TKRGLG.PRODUCT_ID + " VARCHAR(10) NOT NULL," +
            TR_ADJUST_TKRGLG.PRODUCT_CODE + " VARCHAR(150) NOT NULL," +
            TR_ADJUST_TKRGLG.QTY + " NUMERIC(6,0) NOT NULL," +
            TR_ADJUST_TKRGLG.PRICE + " NUMERIC(12,0) NOT NULL," +
            TR_ADJUST_TKRGLG.TOTAL_PRICE + " NUMERIC(12,0) NOT NULL," +
            TR_ADJUST_TKRGLG.BANDEROLE + " NUMERIC(12,0) NOT NULL," +
            TR_ADJUST_TKRGLG.BPPR_NO + " VARCHAR(30))";

    private String CREATE_TR_TOPPING_UP_HIST = "CREATE TABLE `TR_TOPPING_UP_HIST` (" +
            "`ID` NUMBER, " +
            "`BPPR_NO` VARCHAR, " +
            "`PRODUCT_CODE` VARCHAR, " +
            "`TOPING_UP_QTY` NUMBER, " +
            "`PRICE` NUMBER, " +
            "`TOT_PRICE` NUMBER, " +
            "`DATE_CREATED` INTEGER, " +
            "`DISTRICT_ID` VARCHAR, " +
            "`ROUTE` NUMBER, `NOTA_CODE` VARCHAR, `NOTA_DATE` INTEGER)";

    private String CREATE_TR_BPPM_HIST = "CREATE TABLE `TR_BPPM_HIST` (" +
            "`ID` INTEGER, " +
            "`BPPM_NO` VARCHAR, " +
            "`BPPM_DATE` LONG, " +
            "`VERSION` VARCHAR, " +
            "`DATE_CREATED` LONG, " +
            "`IS_PRINT` VARCHAR, " + // CR Print Summary 18-11-2021
            "`COUNT_PRINT` INTEGER, " + // CR Print Summary 25-11-2021
            "`BPPM_STATUS_ID` INTEGER, " + // WMS HH HELPER 25-01-2022
            "`LAST_UPLOAD` INTEGER, `USER_CREATED` VARCHAR, " + "" +
            "`IS_REVISE` VARCHAR )";

    // TR_BPPM_ALOCATION
    private String CREATE_TR_BPPM_ALOCATION_HIST = "CREATE TABLE `TR_BPPM_ALOCATION_HIST` (" +
            "`ID` INTEGER , " +
            "`TR_BPPM_DTL_ID` INTEGER, " +
            "`PP_PROGRAM_ID` INTEGER, " +
            "`PROGRAM_TYPE_ID` INTEGER, " +
            "`QTY` INTEGER, " +
            "`STATUS` VARCHAR, " +
            "`DATE_CREATED` INTEGER, " +
            "`USER_CREATED` VARCHAR)";

    /**
     * @modified by lukkis 9 JAN 2023
     * AR TTO DES 2022
     * Add column ITEM_MAIN_CATEGORY
     */
    // TR_BPPM_DTL
    private String CREATE_TR_BPPM_DTL_HIST = "CREATE TABLE `TR_BPPM_DTL_HIST` (" +
            "`ID` INTEGER PRIMARY KEY, " +
            "`TR_BPPM_ID` INTEGER, " +
            "`ITEM_ID` INTEGER, " +
            "`ITEM_CODE` VARCHAR, " +
            "`ITEM_NAME` VARCHAR, " +
            "`ITEM_DESCRIPTION` VARCHAR, " +
            "`ITEM_SUBCATEGORY` VARCHAR, " +
            "`ITEM_SIZE` VARCHAR, " +
            "`ITEM_CATEGORY` VARCHAR, " +
            "`ITEM_BRAND` VARCHAR, " +
            "`STOCK_AWAL_GOOD` INTEGER, " +
            "`STATUS` VARCHAR, " +
            "`DATE_CREATED` INTEGER, " +
            "`STOCK_FINAL_GOOD` INTEGER, " + // WMS HH HELPER 25-01-2022
            "`RETURN_QTY_BARU` INTEGER, " +
            "`RETURN_QTY_BEKAS` INTEGER, " +
            "`RETURN_QTY_RUSAK` INTEGER, " +
            "`USER_CREATED` VARCHAR, " +
            "`ITEM_MAIN_CATEGORY` VARCHAR, " +
            "`ITEM_WEIGHT` VARCHAR" +
            ")";
    private String CREATE_TR_PU_HDR_HIST = "CREATE TABLE IF NOT EXISTS 'TR_PU_HDR_HIST' ("
            + "`ID` INTEGER,"
            + "`PU_NO` VARCHAR(20),"
            + "`BPPM_NO` VARCHAR(20),"
            + "`PU_STATUS_ID` NUMBER,"
            + "`ITEM_ID` NUMBER,"
            + "`FINAL_QTY` NUMBER,"
            + "`NIK_SALESMAN` VARCHAR(30),"
            + "`STATUS` VARCHAR(1),"
            + "`DATE_CREATED` LONG,"
            + "`USER_CREATED` VARCHAR(50),"
            + "`DATE_MODIFIED` LONG,"
            + "`USER_MODIFIED` VARCHAR(50),"
            + "`RETURN_QTY_BEKAS` NUMBER,"
            + "`RETURN_QTY_RUSAK` NUMBER"
            + ")";
    /**
     * @modified by lukkis 9 JAN 2023
     * AR TTO DES 2022
     * Add column ITEM_MAIN_CATEGORY
     */
    private String CREATE_TR_PU_DTL_HIST = "CREATE TABLE IF NOT EXISTS `TR_PU_DTL_HIST` ("
            + "`ID` INTEGER ,"
            + "`TR_PU_HDR_ID` NUMBER,"
            + "`PLAN_ID` NUMBER,"
            + "`OUTLET_ID` VARCHAR(30),"
            + "`ITEM_GROUP_ID` NUMBER,"
            + "`ITEM_GROUP_NAME` VARCHAR(200),"
            + "`ITEM_ID` NUMBER,"
            + "`ITEM_CODE` VARCHAR(20),"
            + "`ITEM_NAME` VARCHAR(100),"
            + "`ITEM_SUBCATEGORY` VARCHAR(250),"
            + "`ITEM_CATEGORY` VARCHAR(250),"
            + "`ITEM_DESCRIPTION` VARCHAR(1000),"
            + "`QTY_WITHDRAW` NUMBER,"
            + "`QTY_OUTLET` NUMBER,"
            + "`BRAND_CODE` VARCHAR(3),"
            + "`WEEK` NUMBER,"
            + "`ITEM_STATUS` VARCHAR(1),"
            + "`VISIT_COMPLETED` VARCHAR(1),"
            + "`STATUS` VARCHAR(1),"
            + "`DATE_CREATED` LONG,"
            + "`USER_CREATED` VARCHAR(50),"
            + "`DATE_MODIFIED` LONG,"
            + "`USER_MODIFIED` VARCHAR(50),"
            + "`MST_OUTLET_ID` NUMBER,"
            + "`FLAG_DOWNLOADED` VARCHAR DEFAULT 'N',"
            + "`ITEM_SIZE` VARCHAR(100), "
            + "`ITEM_MAIN_CATEGORY` VARCHAR(50)"
            + ")";
    private String CREATE_TR_PU_ALLOCATION_HIST = "CREATE TABLE IF NOT EXISTS `TR_PU_ALLOCATION_HIST` ("
            + "`ID` INTEGER,"
            + "`ITEM_ID` NUMBER,"
            + "`PP_PROGRAM_ID` NUMBER,"
            + "`PROGRAM_TYPE_ID` NUMBER,"
            + "`QTY` NUMBER,"
            + "`STATUS` VARCHAR(1),"
            + "`USER_CREATED` VARCHAR(50),"
            + "`DATE_CREATED` LONG,"
            + "`USER_MODIFIED` VARCHAR(50),"
            + "`DATE_MODIFIED` LONG"
            + ")";
    //endregion
    // TR_BPPU
    private String CREATE_TR_BPPU = "CREATE TABLE `TR_BPPU` (" +
            "`ID` INTEGER PRIMARY KEY, " +
            "`BPPU_NO` VARCHAR, " +
            "`BPPU_DATE` INTEGER, " +
            "`STATUS` VARCHAR, " +
            "`DATE_CREATED` INTEGER, " +
            "`IS_PRINT` VARCHAR, " + // CR Print Summary 18-11-2021
            "`COUNT_PRINT` INTEGER, " + // CR Print Summary 25-11-2021
            "`IS_REVISE` VARCHAR)";

    // TR_BPPU_ALOCATION
    private String CREATE_TR_BPPU_ALOCATION = "CREATE TABLE `TR_BPPU_ALOCATION` (" +
            "`ID` INTEGER PRIMARY KEY, " +
            "`TR_BPPU_ID` INTEGER, " +
            "`PP_PROGRAM_ID` INTEGER, " +
            "`AMOUNT` INTEGER, " +
            "`STATUS` VARCHAR, " +
            "`DATE_CREATED` INTEGER)";

    // TR_COMPENSATION
    private String CREATE_TR_COMPENSATION = "CREATE TABLE `TR_COMPENSATION` (" +
            "`ID` INTEGER PRIMARY KEY, " +
            "`OU_CODE` VARCHAR, " +
            "`TERRITORY_CODE` VARCHAR, " +
            "`DISTRICT_CODE` VARCHAR, " +
            "`ROUTE` NUMBER, " +
            "`OUTLET_ID` VARCHAR, " +
            "`OUTLET_NAME` VARCHAR, " +
            "`TR_PROGRAM_EXECUTION_ID` INTEGER, " +
            "`TR_BPPM_ALOCATION_ID` INTEGER, " +
            "`GIFT_COMPS_TYPE_ID` INTEGER, " +
            "`GIFT_PRODUCT_ID` INTEGER, " +
            "`GIFT_QTY` INTEGER, " +
            "`REASON_COMPS_TYPE_ID` INTEGER, " +
            "`REASON_PRODUCT_ID` INTEGER, " +
            "`REASON_QTY` INTEGER, " +
            "`STATUS` VARCHAR, " +
            "`DATE_CREATED` INTEGER, " +
            "`TR_BPPM_DTL_ID` INTEGER, " +
            "`BPPU_NO` VARCHAR, " +
            "`BPPM_NO` VARCHAR, " +
            "`DESCRIPTION` VARCHAR, " +
            "`WEEK` NUMBER, " +
            "`TR_TTO_HDR_ID` NUMBER," +
            "`ITEM_WEIGHT` VARCHAR)";

    // TR_COMPENSATION
    private String CREATE_TR_COMPENSATION_TEMP = "CREATE TABLE `TR_COMPENSATION_TEMP` (" +
            "`ID` INTEGER, " +
            "`OU_CODE` VARCHAR, " +
            "`TERRITORY_CODE` VARCHAR, " +
            "`DISTRICT_CODE` VARCHAR, " +
            "`ROUTE` NUMBER, " +
            "`OUTLET_ID` VARCHAR, " +
            "`OUTLET_NAME` VARCHAR, " +
            "`TR_PROGRAM_EXECUTION_ID` INTEGER, " +
            "`TR_BPPM_ALOCATION_ID` INTEGER, " +
            "`GIFT_COMPS_TYPE_ID` INTEGER, " +
            "`GIFT_PRODUCT_ID` INTEGER, " +
            "`GIFT_QTY` INTEGER, " +
            "`REASON_COMPS_TYPE_ID` INTEGER, " +
            "`REASON_PRODUCT_ID` INTEGER, " +
            "`REASON_QTY` INTEGER, " +
            "`STATUS` VARCHAR, " +
            "`DATE_CREATED` INTEGER, " +
            "`TR_BPPM_DTL_ID` INTEGER, " +
            "`BPPU_NO` VARCHAR, " +
            "`BPPM_NO` VARCHAR, " +
            "`DESCRIPTION` VARCHAR, " +
            "`WEEK` NUMBER, " +
            "`TR_TTO_HDR_ID` NUMBER," +
            "`ITEM_WEIGHT` VARCHAR)";

    // TR_COST_REALIZATION
    private String CREATE_TR_COST_REALIZATION = "CREATE TABLE TR_COST_REALIZATION (\n" +
            "\tID NUMBER PRIMARY KEY ASC,\n" +
            "\tSALESMAN_ID NUMBER,\n" +
            "\tNIK VARCHAR,\n" +
            "\tAO_ID NUMBER,\n" +
            "\tAO_CODE VARCHAR,\n" +
            "\tOU_ID NUMBER,\n" +
            "\tOU_CODE VARCHAR,\n" +
            "\tTERRITORY_ID NUMBER,\n" +
            "\tTERRITORY_CODE VARCHAR,\n" +
            "\tDISTRICT_ID NUMBER,\n" +
            "\tDISTRICT_CODE VARCHAR,\n" +
            "\tROUTE_ID NUMBER,\n" +
            "\tROUTE_CODE VARCHAR,\n" +
            "\tCOST_CENTER VARCHAR,\n" +
            "\tCOVERAGE VARCHAR,\n" +
            "\tSUBCOVERAGE VARCHAR,\n" +
            "\tPOLICE_NO VARCHAR,\n" +
            "\tCOST_REAL_NO VARCHAR,\n" +
            "\tTRANS_DATE NUMBER,\n" +
            "\tDATE_CLAIM NUMBER,\n" +
            "\tWEEK NUMBER,\n" +
            "\tCOST_REAL_DETAIL_ID NUMBER,\n" +
            "\tITEM_ID NUMBER,\n" +
            "\tAMOUNT NUMBER,\n" +
            "\tFUEL_TYPE_ID NUMBER,\n" +
            "\tFUEL_LITER NUMBER,\n" +
            "\tKILOMETER NUMBER,\n" +
            "\tSHEET NUMBER,\n" +
            "\tDESCRIPTION VARCHAR,\n" +
            "\tROUNDING NUMBER,\n" +
            "\tTAX_VALUE NUMBER,\n" +
            "\tIS_COOP_SPBU VARCHAR,\n" +
            "\tIS_NOT_RECEIPT VARCHAR,\n" +
            "\tSTATUS_CLAIM NUMBER,\n" +
            "\tIS_REVISE VARCHAR,\n" +
            "\tSTATUS VARCHAR,\n" +
            "\tVERSION NUMBER,\n" +
            "\tDATE_CREATED NUMBER,\n" +
            "\tUSER_CREATED VARCHAR,\n" +
            "\tDATE_MODIFIED NUMBER,\n" +
            "\tUSER_MODIFIED VARCHAR,\n" +
            "\tRECEIVER_NAME VARCHAR,\n" + // CR 10593 29 April 2021
            "\tPAYMENT_METHOD_ID NUMBER\n" +
            ");";

    // TR_DOWNLOAD
    private String CREATE_TR_DOWNLOAD = "CREATE TABLE IF NOT EXISTS " + TR_DOWNLOAD.TABLE + " (" +
            TR_DOWNLOAD.ID + " INTEGER PRIMARY KEY ASC," +
            TR_DOWNLOAD.DISTRICT_ID + " VARCHAR(20) NOT NULL," +
            TR_DOWNLOAD.DISTRICT_CODE + " VARCHAR(10) NOT NULL," +
            TR_DOWNLOAD.ROUTE + " VARCHAR(1) NOT NULL," +
            TR_DOWNLOAD.FLAG_SELECTED + " VARCHAR(1) NOT NULL," +
            TR_DOWNLOAD.FLAG_PRODUCT + " VARCHAR(1) NOT NULL," +
            TR_DOWNLOAD.FLAG_OUTLET + " VARCHAR(1) NOT NULL," +
            TR_DOWNLOAD.FLAG_TARGET + " VARCHAR(1) NOT NULL," +
            TR_DOWNLOAD.FLAG_HISCALLSHEET + " VARCHAR(1) NOT NULL," +
            TR_DOWNLOAD.FLAG_UPLOADED + " VARCHAR(1) NOT NULL" +
            ")";

    // TR_DURATION_NOTES
    private String CREATE_TR_DURATION_NOTES = "CREATE TABLE IF NOT EXISTS " + TR_DURATION_NOTES.TABLE + " (" +
            TR_DURATION_NOTES.ID + " INTEGER PRIMARY KEY ASC," +
            TR_DURATION_NOTES.DISTRICT_ID + " VARCHAR(30) NOT NULL," +
            TR_DURATION_NOTES.DISTRICT_CODE + " VARCHAR(5) NOT NULL," +
            TR_DURATION_NOTES.ROUTE + " VARCHAR(2) NOT NULL," +
            TR_DURATION_NOTES.OUTLET_ID + " VARCHAR(70) NOT NULL UNIQUE," +
            TR_DURATION_NOTES.OUTLET_NAME + " VARCHAR(100) NOT NULL ," +
            TR_DURATION_NOTES.CALL_START + " DATETIME," +
            TR_DURATION_NOTES.CALL_END + " DATETIME," +
            TR_DURATION_NOTES.DURATION + " NUMERIC(10,0)," +
            TR_DURATION_NOTES.NOTES + " VARCHAR(100)," +
            TR_DURATION_NOTES.ACTUAL_LONG + " VARCHAR(20)," +
            TR_DURATION_NOTES.ACTUAL_LAT + " VARCHAR(20)," +
            TR_DURATION_NOTES.DISTANCE + " NUMBER)";

    // TR_INBOX
    private String CREATE_TR_INBOX = "CREATE TABLE IF NOT EXISTS " + TR_INBOX.TABLE + " (" +
            TR_INBOX.ID + " INTEGER PRIMARY KEY ASC," +
            TR_INBOX.MESSAGE_TYPE + " VARCHAR(10) NOT NULL," +
            TR_INBOX.STATUS + " VARCHAR(10) NOT NULL," +
            TR_INBOX.MESSAGE + " VARCHAR(100) NOT NULL," +
            TR_INBOX.DOWNLOAD_DATE + " DATETIME NOT NULL," +
            TR_INBOX.MODUL_TYPE + " VARCHAR(100))";

    // TR_NONPP_PROG_EXEC
    private String CREATE_TR_NONPP_PROG_EXEC = "CREATE TABLE `TR_NONPP_PROG_EXEC` (" +
            "`ID` INTEGER PRIMARY KEY, " +
            "`OU_CODE` VARCHAR, " +
            "`TERRITORY_CODE` VARCHAR, " +
            "`DISTRICT_CODE` VARCHAR, " +
            "`ROUTE` INTEGER, " +
            "`OUTLET_ID` VARCHAR, " +
            "`OUTLET_NAME` VARCHAR, " +
            "`TR_BPPM_ALOCATION_ID` INTEGER, " +
            "`QTY` INTEGER, " +
            "`STATUS` VARCHAR, " +
            "`DATE_CREATED` INTEGER, " +
            "`TR_BPPM_DTL_ID` INTEGER, " +
            "`TR_PU_ALLOCATION_ID` INTEGER, " +
            "`FLAG_PU` VARCHAR(1) DEFAULT 'N', " +
            "`WEEK` NUMBER, " +
            "`FLAG_KUNJUNGAN_KEMBALI` VARCHAR(1)) ";

    // TR_NONPP_PROG_EXEC
    private String CREATE_TR_NONPP_PROG_EXEC_TEMP = "CREATE TABLE `TR_NONPP_PROG_EXEC_TEMP` (" +
            "`ID` INTEGER , " +
            "`OU_CODE` VARCHAR, " +
            "`TERRITORY_CODE` VARCHAR, " +
            "`DISTRICT_CODE` VARCHAR, " +
            "`ROUTE` INTEGER, " +
            "`OUTLET_ID` VARCHAR, " +
            "`OUTLET_NAME` VARCHAR, " +
            "`TR_BPPM_ALOCATION_ID` INTEGER, " +
            "`QTY` INTEGER, " +
            "`STATUS` VARCHAR, " +
            "`DATE_CREATED` INTEGER, " +
            "`TR_BPPM_DTL_ID` INTEGER, `TR_PU_ALLOCATION_ID` INTEGER, `FLAG_PU` VARCHAR(1) DEFAULT 'N', `WEEK` NUMBER, `FLAG_KUNJUNGAN_KEMBALI` VARCHAR(1))";

    // TR_NOTATION
    private String CREATE_TR_NOTATION = "CREATE TABLE IF NOT EXISTS " + TR_NOTATION.TABLE + " (" +
            TR_NOTATION.ID + " INTEGER PRIMARY KEY ASC," +
            TR_NOTATION.DISTRICT_ID + " VARCHAR(30) NOT NULL," +
            TR_NOTATION.ROUTE + " VARCHAR(2) NOT NULL," +
            TR_NOTATION.OUTLET_ID + " VARCHAR(70) NOT NULL," +
            TR_NOTATION.OUTLET_NAME + " VARCHAR(100) NOT NULL," +
            TR_NOTATION.PRODUCT_ID + " VARCHAR(10) NOT NULL," +
            TR_NOTATION.PRODUCT_CODE + " VARCHAR(150) NOT NULL," +
            TR_NOTATION.PRODUCT_SEQN + " INTEGER NOT NULL," +
            TR_NOTATION.STOCK + " NUMERIC(6,0) NOT NULL," +
            TR_NOTATION.BUY_SA + " NUMERIC(6,0)," +
            TR_NOTATION.FACEUP + " NUMERIC(6,0)," +
            TR_NOTATION.WHOLESALE_SCHEDULE + " NUMERIC(6,0)," +
            TR_NOTATION.OOS_DURATION + " NUMERIC(6,0)," +
            TR_NOTATION.DISTRIBUTION_NOTATION + " VARCHAR(10))";

    // TR_NOTES_ROUTE
    private String CREATE_TR_NOTES_ROUTE = "CREATE TABLE IF NOT EXISTS " + TR_NOTES_ROUTE.TABLE + " (" +
            TR_NOTES_ROUTE.ID + " INTEGER PRIMARY KEY ASC," +
            TR_NOTES_ROUTE.DISTRICT_ID + " VARCHAR(30) NOT NULL," +
            TR_NOTES_ROUTE.ROUTE + " VARCHAR(2) NOT NULL," +
            TR_NOTES_ROUTE.NOTES + " VARCHAR(250))";

    // TR_POSM
    private String CREATE_TR_POSM = "CREATE TABLE IF NOT EXISTS " + TR_POSM.TABLE + " (" +
            TR_POSM.ID + " INTEGER PRIMARY KEY ASC," +
            TR_POSM.DISTRICT_ID + " VARCHAR(30) NOT NULL," +
            TR_POSM.ROUTE + " VARCHAR(2) NOT NULL," +
            TR_POSM.OUTLET_ID + " VARCHAR(70) NOT NULL," +
            TR_POSM.OUTLET_NAME + " VARCHAR(100) NOT NULL," +
            TR_POSM.PRODUCT_ID + " VARCHAR(10) NOT NULL," +
            TR_POSM.PRODUCT_CODE + " VARCHAR(150) NOT NULL," +
            TR_POSM.STICKER_PB + " NUMERIC(3,0)," +
            TR_POSM.STICKER_TPS + " NUMERIC(3,0)," +
            TR_POSM.SHOPBLIND_S_PB + " NUMERIC(3,0)," +
            TR_POSM.SHOPBLIND_M_PB + " NUMERIC(3,0)," +
            TR_POSM.SHOPBLIND_B_PB + " NUMERIC(3,0)," +
            TR_POSM.SHOPBLIND_S_TPS + " NUMERIC(3,0)," +
            TR_POSM.SHOPBLIND_M_TPS + " NUMERIC(3,0)," +
            TR_POSM.SHOPBLIND_B_TPS + " NUMERIC(3,0))";

    // TR_PP_PROG_EXEC
    private String CREATE_TR_PP_PROG_EXEC = "CREATE TABLE `TR_PP_PROG_EXEC` (" +
            "`ID` INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "`OU_CODE` VARCHAR, " +
            "`TERRITORY_CODE` VARCHAR, " +
            "`DISTRICT_CODE` VARCHAR, " +
            "`ROUTE` INTEGER, " +
            "`OUTLET_ID` VARCHAR, " +
            "`OUTLET_NAME` VARCHAR, " +
            "`PP_PROGRAM_ID` INTEGER, " +
            "`STATUS` VARCHAR, " +
            "`DATE_CREATED` INTEGER, " +
            "`WEEK` NUMBER, " +
            "`FLAG_KUNJUNGAN_KEMBALI` VARCHAR(1))";

    // TR_PP_PROG_EXEC
    private String CREATE_TR_PP_PROG_EXEC_TEMP = "CREATE TABLE `TR_PP_PROG_EXEC_TEMP` (" +
            "`ID` INTEGER , " +
            "`OU_CODE` VARCHAR, " +
            "`TERRITORY_CODE` VARCHAR, " +
            "`DISTRICT_CODE` VARCHAR, " +
            "`ROUTE` INTEGER, " +
            "`OUTLET_ID` VARCHAR, " +
            "`OUTLET_NAME` VARCHAR, " +
            "`PP_PROGRAM_ID` INTEGER, " +
            "`STATUS` VARCHAR, " +
            "`DATE_CREATED` INTEGER, " +
            "`WEEK` NUMBER, " +
            "`FLAG_KUNJUNGAN_KEMBALI` VARCHAR(1))";

    // TR_RETUR_RECAP
    private String CREATE_TR_RETUR_RECAP = "CREATE TABLE IF NOT EXISTS " + TR_RETUR_RECAP.TABLE + " (" +
            TR_RETUR_RECAP.ID + " INTEGER PRIMARY KEY ASC," +
            TR_RETUR_RECAP.PRODUCT_ID + " VARCHAR(10) NOT NULL," +
            TR_RETUR_RECAP.PRODUCT_CODE + " VARCHAR(150) NOT NULL," +
            TR_RETUR_RECAP.PRODUCT_BS_CODE + " VARCHAR(30)," +
            TR_RETUR_RECAP.QTY + " NUMERIC(6,0) NOT NULL," +
            TR_RETUR_RECAP.BANDEROLE + " NUMERIC(12,0) NOT NULL)";

    // TR_SALES
    /**
     * modified by dimass02
     * 7 Feb 2024
     * AE BPPR Stockiest
     */
    private String CREATE_TR_SALES = "CREATE TABLE IF NOT EXISTS " + TR_SALES.TABLE + " (" +
            TR_SALES.ID + " INTEGER PRIMARY KEY ASC," +
            TR_SALES.SALES_TYPE + " VARCHAR(15) NOT NULL," +
            TR_SALES.FLAG_SALES_ADD + " INTEGER NOT NULL," +
            TR_SALES.FLAG_SALES_ADD_STATUS + " INTEGER," +
            TR_SALES.SALESMAN_ID + " VARCHAR(30) NOT NULL," +
            TR_SALES.SALESMAN_NAME + " VARCHAR(70) NOT NULL," +
            TR_SALES.DISTRICT_ID + " VARCHAR(30) NOT NULL," +
            TR_SALES.ROUTE + " VARCHAR(2) NOT NULL," +
            TR_SALES.OUTLET_ID + " VARCHAR(70) NOT NULL," +
            TR_SALES.OUTLET_NAME + " VARCHAR(100) NOT NULL," +
            TR_SALES.BARCODE_ID + " VARCHAR(30) ," +
            TR_SALES.PRODUCT_ID + " VARCHAR(10) NOT NULL," +
            TR_SALES.PRODUCT_SEQN + " INTEGER NOT NULL," +
            TR_SALES.PRODUCT_CODE + " VARCHAR(150) NOT NULL," +
            TR_SALES.PRODUCT_NAME + " VARCHAR(150) NOT NULL," +
            TR_SALES.QTY + " NUMERIC(6,0) NOT NULL," +
            TR_SALES.IDEAL_BUY + " NUMERIC(6,0)," +
            TR_SALES.BANDEROLE + " NUMERIC(12,0)," +
            TR_SALES.P2_NUMBER + " VARCHAR(20)," +
            TR_SALES.PRICE + " NUMERIC(12,0)," +
            TR_SALES.TOTAL_PRICE + " NUMERIC(12,0)," +
            TR_SALES.PRINT_VERSION + " INTEGER," +
            TR_SALES.PRINT_DATETIME + " DATETIME(12,0)," +
            TR_SALES.PP_PROGRAM_ID + " NUMERIC(12,0)," +
            TR_SALES.BPPR_NO + " VARCHAR(30)," +
            TR_SALES.NOTE_CODE + " VARCHAR(15)," +
            TR_SALES.NOTA_VERSION + " INTEGER," +
            TR_SALES.COUNT_PRINT + " INTEGER," +
            TR_SALES.IS_RELEASED + " VARCHAR(15) DEFAULT 'N'," +
            TR_SALES.STATUS + " VARCHAR(1)," +
            TR_SALES.TR_BPPR_STOCKIEST_ID + " NUMBER,  " +
            TR_SALES.BUDGET_CATEGORY_ID + " INTEGER, " +
            TR_SALES.BUDGET_REQ_REF + " NUMBER)";

    private String CREATE_TR_SALES_PRINT = "CREATE TABLE TR_SALES_PRINT (\n" +
            "    ID                     NUMBER PRIMARY KEY NOT NULL,\n" +
            "    SALES_TYPE             VARCHAR(50),\n" +
            "    FLAG_SALES_ADD         NUMBER,\n" +
            "    FLAG_SALES_ADD_STATUS  NUMBER,\n" +
            "    SALESMAN_ID            VARCHAR(30),\n" +
            "    SALESMAN_NAME          VARCHAR(70),\n" +
            "    DISTRICT_ID            VARCHAR(50),\n" +
            "    ROUTE                  NUMBER,\n" +
            "    OUTLET_ID              VARCHAR(70),\n" +
            "    OUTLET_NAME            VARCHAR(100),\n" +
            "    PRODUCT_ID             NUMBER,\n" +
            "    PRODUCT_SEQN           NUMBER,\n" +
            "    PRODUCT_CODE           VARCHAR(10),\n" +
            "    PRODUCT_NAME           VARCHAR(100),\n" +
            "    QTY                    NUMBER,\n" +
            "    IDEAL_BUY              NUMBER,\n" +
            "    BANDEROLE              NUMBER,\n" +
            "    P2_NUMBER              VARCHAR(20),\n" +
            "    PRICE                  NUMBER,\n" +
            "    TOTAL_PRICE            NUMBER,\n" +
            "    NOTE_CODE              VARCHAR(50),\n" +
            "    DATE_CREATED           NUMBER,\n" +
            "    STATUS                 VARCHAR(1),\n" +
            "    PRINT_VERSION          NUMBER\n" +
            ");";

    // TR_SALES_TEMP
    private String CREATE_TR_SALES_TEMP = "CREATE TABLE IF NOT EXISTS " + TR_SALES_TEMP.TABLE + " (" +
            TR_SALES_TEMP.ID + " INTEGER PRIMARY KEY ASC," +
            TR_SALES_TEMP.SALES_TYPE + " VARCHAR(15) NOT NULL," +
            TR_SALES_TEMP.FLAG_SALES_ADD + " INTEGER NOT NULL," +
            TR_SALES_TEMP.FLAG_SALES_ADD_STATUS + " INTEGER," +
            TR_SALES_TEMP.SALESMAN_ID + " VARCHAR(30) NOT NULL," +
            TR_SALES_TEMP.SALESMAN_NAME + " VARCHAR(70) NOT NULL," +
            TR_SALES_TEMP.DISTRICT_ID + " VARCHAR(30) NOT NULL," +
            TR_SALES_TEMP.ROUTE + " VARCHAR(2) NOT NULL," +
            TR_SALES_TEMP.OUTLET_ID + " VARCHAR(70) NOT NULL," +
            TR_SALES_TEMP.OUTLET_NAME + " VARCHAR(100) NOT NULL," +
            TR_SALES_TEMP.BARCODE_ID + " VARCHAR(30) ," +
            TR_SALES_TEMP.PRODUCT_ID + " VARCHAR(10) NOT NULL," +
            TR_SALES_TEMP.PRODUCT_SEQN + " INTEGER NOT NULL," +
            TR_SALES_TEMP.PRODUCT_CODE + " VARCHAR(150) NOT NULL," +
            TR_SALES_TEMP.PRODUCT_NAME + " VARCHAR(150) NOT NULL," +
            TR_SALES_TEMP.QTY + " NUMERIC(6,0) NOT NULL," +
            TR_SALES_TEMP.IDEAL_BUY + " NUMERIC(6,0)," +
            TR_SALES_TEMP.BANDEROLE + " NUMERIC(12,0)," +
            TR_SALES_TEMP.P2_NUMBER + " VARCHAR(20)," +
            TR_SALES_TEMP.PRICE + " NUMERIC(12,0)," +
            TR_SALES_TEMP.TOTAL_PRICE + " NUMERIC(12,0)," +
            TR_SALES_TEMP.PRINT_VERSION + " INTEGER," +
            TR_SALES_TEMP.PRINT_DATETIME + " DATETIME(12,0)," +
            TR_SALES_TEMP.PP_PROGRAM_ID + " NUMERIC(12,0)," +
            TR_SALES_TEMP.BPPR_NO + " VARCHAR(30)," +
            TR_SALES_TEMP.NOTE_CODE + " VARCHAR(15), " +
            TR_SALES.NOTA_VERSION + " INTEGER," +
            TR_SALES.COUNT_PRINT + " INTEGER," +
            TR_SALES.IS_RELEASED + " VARCHAR(15) DEFAULT 'N'," +
            TR_SALES.STATUS + " VARCHAR(1)," +
            TR_SALES.TR_BPPR_STOCKIEST_ID + " NUMBER,  " +
            TR_SALES.BUDGET_CATEGORY_ID + " INTEGER )";

    private String CREATE_TR_SALES_PAYMENT = "CREATE TABLE TR_SALES_PAYMENT (" +
            "ID NUMBER PRIMARY KEY, " +
            "OUTLET_ID VARCHAR, " +
            "DISTRICT_ID VARCHAR, " +
            "ROUTE VARCHAR, " +
            "FLAG_SALES_ADD NUMBER, " +
            "PAYMENT_TYPE VARCHAR, " +
            "PAYMENT NUMBER, " +
            "TRX_ID VARCHAR, " +
            "ADDED_CASH NUMBER, " +
            "USER_CREATED VARCHAR, " +
            "DATE_CREATED NUMBER, " +
            "NOTE_CODE VARCHAR, " +
            "FLAG_RPU VARCHAR, " +
            "VIRTUAL_ACCOUNT_NO VARCHAR, " +
            "PAYMENT_STATUS_ID NUMBER," +
            "PAYMENT_LIMIT DATE, " +
            "BANK_PARTNER_ID NUMBER" +
            ")";

    // TR_STOCK_ROKOK
    private String CREATE_TR_STOCK_ROKOK = "CREATE TABLE `TR_STOCK_ROKOK` (" +
            "`ID` NUMBER PRIMARY KEY, " +
            "`TR_BPPR_ID` NUMBER, " +
            "`BPPR_NO` VARCHAR, " +
            "`PRODUCT_ID` INTEGER, " +
            "`PRODUCT_CODE` VARCHAR, " +
            "`PRODUCT_NAME` VARCHAR, " +
            "`PRODUCT_SEQ` NUMBER, " +
            "`TOT_STOCK_AWAL` NUMBER, " +
            "`TOT_STOCK_GOOD` NUMBER, " +
            "`TOT_STOCK_BAD` NUMBER, " +
            "`TOT_STOCK_USED` NUMBER, " +
            "`STATUS` VARCHAR, " +
            "`DATE_CREATED` INTEGER, " +
            "`STOCK_INIT_DUS` INTEGER, " + // WMS HH HELPER 25-01-2022
            "`STOCK_INIT_BAL` INTEGER, " +
            "`STOCK_INIT_SLF` INTEGER, " +
            "`STOCK_INIT_BKS` INTEGER, " +
            "`STOCK_FINAL_GOOD_DUS` INTEGER, " +
            "`STOCK_FINAL_GOOD_BAL` INTEGER, " +
            "`STOCK_FINAL_GOOD_SLF` INTEGER, " +
            "`STOCK_FINAL_GOOD_BKS` INTEGER, " +
            "`TOT_STOCK_USED_ORI` NUMBER " + ")";

    // TR_SVY_VOLUME
    private String CREATE_TR_SVY_VOLUME = "CREATE TABLE `TR_SVY_VOLUME` (" +
            "`ID` NUMBER PRIMARY KEY, " +
            "`OUTLET_ID` VARCHAR, " +
            "`PRODUCT_ID` NUMBER, " +
            "`OPR_DAY` NUMBER, " +
            "`VOL_PER_DAY` NUMBER, " +
            "`VOL_PER_WEEK` NUMBER, " +
            "`UOM_ID` NUMBER, " +
            "`DATE_CREATED` INTEGER, " +
            "`WEEK` NUMBER, " +
            "`OPR_DAY_MON` VARCHAR2(1), " +
            "`OPR_DAY_TUE` VARCHAR2(1), " +
            "`OPR_DAY_WED` VARCHAR2(1), " +
            "`OPR_DAY_THU` VARCHAR2(1), " +
            "`OPR_DAY_FRI` VARCHAR2(1), " +
            "`OPR_DAY_SAT` VARCHAR2(1), " +
            "`OPR_DAY_SUN` VARCHAR2(1))";

    // TR_PROG_PHOTO
    private String CREATE_TR_PROG_PHOTO = "CREATE TABLE `TR_PROG_PHOTO` (" +
            "`ID` NUMBER PRIMARY KEY, " +
            "`TR_PP_PROG_ID` NUMBER, " +
            "`TR_NONPP_PROG_ID` NUMBER, " +
            "`OUTLET_ID` VARCHAR(70), " +
            "`FILE_NAME` VARCHAR(500), " +
            "`DESCRIPTION` VARCHAR(30), " +
            "`PP_NUMBER` NUMBER, " +
            "`BRAND_CODE` VARCHAR(10), " +
            "`PROGRAM_NAME` VARCHAR(50)," +
            "`WEEK` NUMBER)";

//    // TR_NONPROG_PHOTO
//    private String CREATE_TR_NONPROG_PHOTO = "CREATE TABLE `TR_NONPROG_PHOTO` (" +
//            "`ID` NUMBER PRIMARY KEY, " +
//            "`MST_OUTLET_ID` NUMBER, " +
//            "`OUTLET_ID` VARCHAR(70), " +
//            "`FILE_NAME` VARCHAR(500), " +
//            "`DESCRIPTION` VARCHAR(30))";

    // TR_TOPPING_UP
    /***
     * --CR WMS by khalida
     * Tabel ini punya historis / backup tabel,
     * Pastikan penambahan kolom di tabel ini mengikuti tabel replicate nya
     * */
    private String CREATE_TR_TOPPING_UP = "CREATE TABLE `TR_TOPPING_UP` (" +
            "`ID` NUMBER PRIMARY KEY, " +
            "`BPPR_NO` VARCHAR, " +
            "`PRODUCT_CODE` VARCHAR, " +
            "`TOPING_UP_QTY` NUMBER, " +
            "`PRICE` NUMBER, " +
            "`TOT_PRICE` NUMBER, " +
            "`DATE_CREATED` INTEGER, " +
            "`DISTRICT_ID` VARCHAR, " +
            "`ROUTE` NUMBER, `NOTA_CODE` VARCHAR, `NOTA_DATE` INTEGER)";

    // TR_UNIT_SUPPORT
    private String CREATE_TR_UNIT_SUPPORT = "CREATE TABLE `TR_UNIT_SUPPORT` (" +
            "`ID` INTEGER PRIMARY KEY, " +
            "`OU_CODE` VARCHAR, " +
            "`TERRITORY_CODE` VARCHAR, " +
            "`DISTRICT_CODE` VARCHAR, " +
            "`ROUTE` INTEGER, " +
            "`OUTLET_ID` VARCHAR, " +
            "`OUTLET_NAME` VARCHAR, " +
            "`TR_PROGRAM_EXECUTION_ID` INTEGER, " +
            "`TR_BPPM_DTL_ID` INTEGER, " +
            "`TR_BPPM_ALOCATION_ID` INTEGER, " +
            "`QTY` INTEGER, " +
            "`STATUS` VARCHAR, " +
            "`DATE_CREATED` INTEGER, " +
            "`DESCRIPTION` VARCHAR,"
            + "`TR_PU_ALLOCATION_ID` NUMBER,"
            + "`FLAG_PU` VARCHAR(1) DEFAULT 'N'," +
            " `WEEK` NUMBER, " +
            "`TR_TTO_HDR_ID` NUMBER)";

    // TR_UNIT_SUPPORT
    private String CREATE_TR_UNIT_SUPPORT_TEMP = "CREATE TABLE `TR_UNIT_SUPPORT_TEMP` (" +
            "`ID` INTEGER, " +
            "`OU_CODE` VARCHAR, " +
            "`TERRITORY_CODE` VARCHAR, " +
            "`DISTRICT_CODE` VARCHAR, " +
            "`ROUTE` INTEGER, " +
            "`OUTLET_ID` VARCHAR, " +
            "`OUTLET_NAME` VARCHAR, " +
            "`TR_PROGRAM_EXECUTION_ID` INTEGER, " +
            "`TR_BPPM_DTL_ID` INTEGER, " +
            "`TR_BPPM_ALOCATION_ID` INTEGER, " +
            "`QTY` INTEGER, " +
            "`STATUS` VARCHAR, " +
            "`DATE_CREATED` INTEGER, " +
            "`DESCRIPTION` VARCHAR,"
            + "`TR_PU_ALLOCATION_ID` NUMBER,"
            + "`FLAG_PU` VARCHAR(1)," +
            " `WEEK` NUMBER, " +
            "`TR_TTO_HDR_ID` NUMBER)";

    // TR_VISDOM_NONVIS
    private String CREATE_TR_VISDOM_NONVIS = "CREATE TABLE IF NOT EXISTS " + TR_VISDOM_NONVIS.TABLE + " (" +
            TR_VISDOM_NONVIS.ID + " INTEGER PRIMARY KEY ASC," +
            TR_VISDOM_NONVIS.DISTRICT_ID + " VARCHAR(30) NOT NULL," +
            TR_VISDOM_NONVIS.ROUTE + " VARCHAR(2) NOT NULL," +
            TR_VISDOM_NONVIS.OUTLET_ID + " VARCHAR(70) NOT NULL," +
            TR_VISDOM_NONVIS.OUTLET_NAME + " VARCHAR(100) NOT NULL," +
            TR_VISDOM_NONVIS.VISIBILITY_DOMINAN_ID + " VARCHAR(20)," +
            TR_VISDOM_NONVIS.VISIBILITY_DOMINAN_NAME + " VARCHAR(100)," +
            TR_VISDOM_NONVIS.PROGRAM_PACK_ID + " VARCHAR(20)," +
            TR_VISDOM_NONVIS.PROGRAM_PACK_NAME + " VARCHAR(100)," +
            TR_VISDOM_NONVIS.BRAND_ID + " VARCHAR(20)," +
            TR_VISDOM_NONVIS.BRAND_NAME + " VARCHAR(100)," +
            TR_VISDOM_NONVIS.NON_VISIBLE + " VARCHAR(20)," +
            TR_VISDOM_NONVIS.NON_VISIBLE_NAME + " VARCHAR(100)," +
            TR_VISDOM_NONVIS.VISIBILITY_DOMINAN_AFTER_ID + " VARCHAR(10))";

    // TWEEK
    private String CREATE_TWEEK = "CREATE TABLE IF NOT EXISTS " + TWEEK.TABLE + " (" +
            TWEEK.TRANSDATE + " DATETIME UNIQUE," +
            TWEEK.WEEK_NO + " NUMERIC(5,0))";

    /**
     * @author gerwinh
     * WMS HH HELPER 2022
     * 25 Jan 2022
     * MST_PRODUCT_PRICE
     * TR_VEHICLE_INFO // Update 3 Feb 2022 - to TR_BPPR_DELIVER_ORDER
     */
    private String CREATE_MST_PRODUCT_PRICE = "CREATE TABLE IF NOT EXISTS `MST_PRODUCT_PRICE` ("
            + "`ID` INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "`PRODUCT_ID` INTEGER, "
            + "`PRODUCT_CODE` VARCHAR2(150), "
            + "`PRODUCT_NAME` VARCHAR2(150), "
            + "`PRICE_BAND` INTEGER, "
            + "`PRICE_PACK` INTEGER, "
            + "`TYPE_PRICE_ID` INTEGER, "
            + "`WEEK` INTEGER, "
            + "`MST_PRODUCT_ID` VARCHAR2(20)" // WMS HH HELPER 03-02-2022
            + ")";
    /***
     * --CR WMS by khalida
     * Tabel ini punya historis / backup tabel,
     * Pastikan penambahan kolom di tabel ini mengikuti tabel replicate nya
     * */
    private String CREATE_TR_BPPR_DELIVER_ORDER = "CREATE TABLE IF NOT EXISTS `TR_BPPR_DELIVER_ORDER` ("
            + "`ID` INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "`TR_BPPR_ID` INTEGER, "
            + "`BPPR_NO` VARCHAR2(30), "
            + "`VHC_NOPOL` VARCHAR2(30), "
            + "`DRIVER` VARCHAR2(100), "
            + "`SJ_NO` VARCHAR2(20), "
            + "`SJ_DATE` NUMBER, "
            + "`DATE_CREATED` INTEGER, "
            + "`USER_CREATED` VARCHAR2(50), "
            + "`FLAG_ADD` VARCHAR2(50), "
            + "`FLAG_UPLOAD` VARCHAR2(1), "
            + "`DATE_MODIFIED` INTEGER, "
            + "`USER_MODIFIED` VARCHAR2(50),"
            + "`STATUS` VARCHAR2(1)," // 03 FEB 2022 ADD STATUS AND FLAG_VALIDATE
            + "`FLAG_VALIDATE` VARCHAR2(1), "
            + "`SJ_NO_NAME` VARCHAR " // 21 APR 2022
            + ")";
    /***
     * --CR WMS by khalida
     * Tabel ini punya historis / backup tabel,
     * Pastikan penambahan kolom di tabel ini mengikuti tabel replicate nya
     * */
    /**
     * @author gerwinh
     * WMS HH HELPER 2022
     * 03 Feb 2022
     * Add table TR_BPPR_BAD_STOCK
     */
    private String CREATE_TR_BPPR_BAD_STOCK = "CREATE TABLE IF NOT EXISTS `TR_BPPR_BAD_STOCK` ("
            + "`ID` INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "`BAD_STOCK_TYPE_ID` NUMBER,"
            + "`TRANSACTION_ID` NUMBER,"
            + "`BPPR_NO` VARCHAR2(30),"
            + "`IS_CHECKED` VARCHAR2(1),"
            + "`PRODUCT_ID` NUMBER,"
            + "`PRODUCT_CODE` VARCHAR2(30),"
            + "`PRICE_BAND` NUMBER,"
            + "`QTY_PACK` NUMBER,"
            + "`PRICE_PACK` NUMBER,"
            + "`TOTAL_PRICE` NUMBER,"
            + "`STATUS` VARCHAR2(20),"
            + "`DATE_CREATED` LONG,"
            + "`USER_CREATED` VARCHAR2(20),"
            + "`WEEK` NUMBER"
            + ")";

    /**
     * @author khalida
     * WMS HH HELPER 2022
     * 15 FEB 2022
     * Add table MST_ACTUAL_UNIT_SUPPORT
     * @modified by lukkis 9 JAN 2023
     * AR TTO DES 2022
     * Add column ITEM_MAIN_CATEGORY
     **/
    private String CREATE_MST_ACTUAL_UNIT_SUPPORT = "CREATE TABLE IF NOT EXISTS 'MST_ACTUAL_UNIT_SUPPORT' ("
            + "`ID` INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "`PLAN_ID` NUMBER,"
            + "`OUTLET_ID` VARCHAR(30),"
            + "`ITEM_GROUP_ID` NUMBER,"
            + "`ITEM_GROUP_NAME` VARCHAR(200),"
            + "`ITEM_ID` NUMBER,"
            + "`ITEM_CODE` VARCHAR(20),"
            + "`ITEM_NAME` VARCHAR(100),"
            + "`ITEM_DESCRIPTION` VARCHAR(1000),"
            + "`ITEM_SUBCATEGORY` VARCHAR(250),"
            + "`ITEM_CATEGORY` VARCHAR(250),"
            + "`QTY` NUMBER,"
            + "`BRAND_CODE` VARCHAR(3),"
            + "`WEEK` NUMBER,"
            + "`ITEM_STATUS` VARCHAR(1),"
            + "`DATE_CREATED` LONG,"
            + "`USER_CREATED` VARCHAR(50),"
            + "`ITEM_SIZE` VARCHAR(100),"
            + "`ITEM_MAIN_CATEGORY` VARCHAR(50)"
            + ")";
    /***
     * --CR WMS by khalida
     * Tabel ini punya historis / backup tabel,
     * Pastikan penambahan kolom di tabel ini mengikuti tabel replicate nya
     * */
    private String CREATE_TR_PU_HDR = "CREATE TABLE IF NOT EXISTS 'TR_PU_HDR' ("
            + "`ID` INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "`PU_NO` VARCHAR(20),"
            + "`BPPM_NO` VARCHAR(20),"
            + "`PU_STATUS_ID` NUMBER,"
            + "`ITEM_ID` NUMBER,"
            + "`FINAL_QTY` NUMBER,"
            + "`NIK_SALESMAN` VARCHAR(30),"
            + "`STATUS` VARCHAR(1),"
            + "`DATE_CREATED` LONG,"
            + "`USER_CREATED` VARCHAR(50),"
            + "`DATE_MODIFIED` LONG,"
            + "`USER_MODIFIED` VARCHAR(50),"
            + "`RETURN_QTY_BEKAS` NUMBER,"
            + "`RETURN_QTY_RUSAK` NUMBER"
            + ")";
    /***
     * --CR WMS by khalida
     * Tabel ini punya historis / backup tabel,
     * Pastikan penambahan kolom di tabel ini mengikuti tabel replicate nya
     *
     * @modified by lukkis 9 JAN 2023
     * AR TTO DES 2022
     * Add column ITEM_MAIN_CATEGORY
     * */
    private String CREATE_TR_PU_DTL = "CREATE TABLE IF NOT EXISTS `TR_PU_DTL` ("
            + "`ID` INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "`TR_PU_HDR_ID` NUMBER,"
            + "`PLAN_ID` NUMBER,"
            + "`OUTLET_ID` VARCHAR(30),"
            + "`ITEM_GROUP_ID` NUMBER,"
            + "`ITEM_GROUP_NAME` VARCHAR(200),"
            + "`ITEM_ID` NUMBER,"
            + "`ITEM_CODE` VARCHAR(20),"
            + "`ITEM_NAME` VARCHAR(100),"
            + "`ITEM_SUBCATEGORY` VARCHAR(250),"
            + "`ITEM_CATEGORY` VARCHAR(250),"
            + "`ITEM_DESCRIPTION` VARCHAR(1000),"
            + "`QTY_WITHDRAW` NUMBER,"
            + "`QTY_OUTLET` NUMBER,"
            + "`BRAND_CODE` VARCHAR(3),"
            + "`WEEK` NUMBER,"
            + "`ITEM_STATUS` VARCHAR(1),"
            + "`VISIT_COMPLETED` VARCHAR(1),"
            + "`STATUS` VARCHAR(1),"
            + "`DATE_CREATED` LONG,"
            + "`USER_CREATED` VARCHAR(50),"
            + "`DATE_MODIFIED` LONG,"
            + "`USER_MODIFIED` VARCHAR(50),"
            + "`MST_OUTLET_ID` NUMBER,"
            + "`FLAG_DOWNLOADED` VARCHAR DEFAULT 'N',"
            + "`ITEM_SIZE` VARCHAR(100),"
            + "`ITEM_MAIN_CATEGORY` VARCHAR(50)"
            + ")";
    /***
     * --CR WMS by khalida
     * Tabel ini punya historis / backup tabel,
     * Pastikan penambahan kolom di tabel ini mengikuti tabel replicate nya
     * */
    private String CREATE_TR_PU_ALLOCATION = "CREATE TABLE IF NOT EXISTS `TR_PU_ALLOCATION` ("
            + "`ID` INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "`ITEM_ID` NUMBER,"
            + "`PP_PROGRAM_ID` NUMBER,"
            + "`PROGRAM_TYPE_ID` NUMBER,"
            + "`QTY` NUMBER,"
            + "`STATUS` VARCHAR(1),"
            + "`USER_CREATED` VARCHAR(50),"
            + "`DATE_CREATED` LONG,"
            + "`USER_MODIFIED` VARCHAR(50),"
            + "`DATE_MODIFIED` LONG"
            + ")";
    private String CREATE_MST_ACCT_TOPUP_TKRGLG = "CREATE TABLE IF NOT EXISTS `MST_ACCT_TOPUP_TKRGLG` ("
            + "`ID` INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "`OU_CODE` VARCHAR(10),"
            + "`OU_NAME` VARCHAR(100),"
            + "`CUSTOMER_NAME` VARCHAR(100),"
            + "`CUSTOMER_NUMBER` NUMBER,"
            + "`PARTY_NAME` VARCHAR(100),"
            + "`PARTY_NUMBER` VARCHAR(50),"
            + "`TYPE` NUMBER,"
            + "`WEEK` NUMBER,"
            + "`DATE_CREATED` LONG"
            + ")";
    // TR_BPPM_TEMP
    private String CREATE_TR_BPPM_TEMP = "CREATE TABLE `TR_BPPM_TEMP` (" +
            "`ID` INTEGER PRIMARY KEY, " +
            "`BPPM_NO` VARCHAR, " +
            "`BPPM_DATE` LONG, " +
            "`VERSION` VARCHAR, " +
            "`DATE_CREATED` LONG, " +
            "`IS_PRINT` VARCHAR, " + // CR Print Summary 18-11-2021
            "`COUNT_PRINT` INTEGER, " + // CR Print Summary 25-11-2021
            "`BPPM_STATUS_ID` INTEGER, " + // WMS HH HELPER 25-01-2022
            "`LAST_UPLOAD` INTEGER " + ")";

    //DB_TRACKING
    private String CREATE_DB_TRACKING = "CREATE TABLE IF NOT EXISTS `DB_TRACKING` ("
            + "`ID` INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "`VERSION_DB` NUMBER,"
            + "`VERSION_DB_OLD` NUMBER,"
            + "`VERSION_APP` VARCHAR(10),"
            + "`NEED_UPLOAD` VARCHAR(1)"
            + ")";


    private String CREATE_HISTORY_UPLOAD = "CREATE TABLE IF NOT EXISTS `HISTORY_UPLOAD_MOBIDOC` ("
            + "`ID` INTEGER,"
            + "`DATE_UPLOAD` VARCHAR(30),"
            + "`USERNAME` VARCHAR(30)"
            + ")";

    /**
     * JOSUAH
     * 18 Oct 2022
     * CR WMS (BPPM BPPR beda week)
     */
    /***
     * --CR WMS by khalida
     * Tabel ini punya historis / backup tabel,
     * Pastikan penambahan kolom di tabel ini mengikuti tabel replicate nya
     * */
    // TR_BPPM_ACV
    private String CREATE_TR_BPPM_ACV = "CREATE TABLE IF NOT EXISTS " + TR_BPPM_ACV.TABLE + " (" +
            TR_BPPM_ACV.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TR_BPPM_ACV.BPPM_NO + " VARCHAR, " +
            TR_BPPM_ACV.TR_BPPM_ID + " INTEGER, " +
            TR_BPPM_ACV.TR_BPPM_DTL_ID + " INTEGER, " +
            TR_BPPM_ACV.OUTLET_ID + " VARCHAR, " +
            TR_BPPM_ACV.STOCK_EKSEKUSI + " INTEGER, " +
            TR_BPPM_ACV.WEEK + " INTEGER, " +
            TR_BPPM_ACV.TR_BPPM_ALOCATION_ID + " INTEGER, " +
            TR_BPPM_ACV.USER_CREATED + " VARCHAR, " +
            TR_BPPM_ACV.DATE_CREATED + " LONG, " +
            TR_BPPM_ACV.TERRITORY_CODE + " VARCHAR, " +
            TR_BPPM_ACV.DISTRICT_CODE + " VARCHAR, " +
            TR_BPPM_ACV.ROUTE + " INTEGER, " +
            TR_BPPM_ACV.TR_COMPENSATION_ID + " INTEGER DEFAULT 0" +
            ")";

    // TR_BPPM_ACV_HIST
    private String CREATE_TR_BPPM_ACV_HIST = "CREATE TABLE IF NOT EXISTS " + TR_BPPM_ACV.HIST_TABLE + " (" +
            TR_BPPM_ACV.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TR_BPPM_ACV.BPPM_NO + " VARCHAR, " +
            TR_BPPM_ACV.TR_BPPM_ID + " INTEGER, " +
            TR_BPPM_ACV.TR_BPPM_DTL_ID + " INTEGER, " +
            TR_BPPM_ACV.OUTLET_ID + " VARCHAR, " +
            TR_BPPM_ACV.STOCK_EKSEKUSI + " INTEGER, " +
            TR_BPPM_ACV.WEEK + " INTEGER, " +
            TR_BPPM_ACV.TR_BPPM_ALOCATION_ID + " INTEGER, " +
            TR_BPPM_ACV.USER_CREATED + " VARCHAR, " +
            TR_BPPM_ACV.DATE_CREATED + " LONG, " +
            TR_BPPM_ACV.TERRITORY_CODE + " VARCHAR, " +
            TR_BPPM_ACV.DISTRICT_CODE + " VARCHAR, " +
            TR_BPPM_ACV.ROUTE + " INTEGER, " +
            TR_BPPM_ACV.TR_COMPENSATION_ID + " INTEGER DEFAULT 0" +
            ")";
    /***
     * --CR WMS by khalida
     * Tabel ini punya historis / backup tabel,
     * Pastikan penambahan kolom di tabel ini mengikuti tabel replicate nya
     * */
    // TR_PU_ACV
    private String CREATE_TR_PU_ACV = "CREATE TABLE IF NOT EXISTS " + TR_PU_ACV.TABLE + " (" +
            TR_PU_ACV.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TR_PU_ACV.PU_NO + " VARCHAR, " +
            TR_PU_ACV.BPPM_NO + " VARCHAR, " +
            TR_PU_ACV.OUTLET_ID + " VARCHAR, " +
            TR_PU_ACV.TR_PU_ALOCATION_ID + " INTEGER, " +
            TR_PU_ACV.ITEM_ID + " INTEGER, " +
            TR_PU_ACV.STOCK_EKSEKUSI + " INTEGER, " +
            TR_PU_ACV.WEEK + " INTEGER, " +
            TR_PU_ACV.USER_CREATED + " VARCHAR, " +
            TR_PU_ACV.DATE_CREATED + " LONG, " +
            TR_PU_ACV.TERRITORY_CODE + " VARCHAR, " +
            TR_PU_ACV.DISTRICT_CODE + " VARCHAR, " +
            TR_PU_ACV.ROUTE + " INTEGER " +
            ")";

    // TR_PU_ACV_HIST
    private String CREATE_TR_PU_ACV_HIST = "CREATE TABLE IF NOT EXISTS " + TR_PU_ACV.HIST_TABLE + " (" +
            TR_PU_ACV.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TR_PU_ACV.PU_NO + " VARCHAR, " +
            TR_PU_ACV.BPPM_NO + " VARCHAR, " +
            TR_PU_ACV.OUTLET_ID + " VARCHAR, " +
            TR_PU_ACV.TR_PU_ALOCATION_ID + " INTEGER, " +
            TR_PU_ACV.ITEM_ID + " INTEGER, " +
            TR_PU_ACV.STOCK_EKSEKUSI + " INTEGER, " +
            TR_PU_ACV.WEEK + " INTEGER, " +
            TR_PU_ACV.USER_CREATED + " VARCHAR, " +
            TR_PU_ACV.DATE_CREATED + " LONG, " +
            TR_PU_ACV.TERRITORY_CODE + " VARCHAR, " +
            TR_PU_ACV.DISTRICT_CODE + " VARCHAR, " +
            TR_PU_ACV.ROUTE + " INTEGER " +
            ")";

    /**
     * @author hafizhr
     * 10 AUG 2022
     * TANDA TERIMA OUTLET
     * <p>
     * Edited by lukkis
     * 10 OKT 2022
     * AR TTO SEPT
     * @modified lukkis 07-FEB-2023
     * CR TTO AFTER PILOTING
     * - Add phone number
     * @modified muhammadf16 26-FEB-2024
     * CR TTO 2024
     * - Add ID_CARD_PATH_FILE
     */

    // MST_OUTLET_OWNER
    private String CREATE_MST_OUTLET_OWNER = "CREATE TABLE IF NOT EXISTS " + MST_OUTLET_OWNER.TABLE + " (" +
            MST_OUTLET_OWNER.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MST_OUTLET_OWNER.OUTLET_ID + " VARCHAR2(20), " +
            MST_OUTLET_OWNER.OWNER_NAME + " VARCHAR2(100), " +
            MST_OUTLET_OWNER.IDENTITY_NUMBER + " VARCHAR2(50), " +
            MST_OUTLET_OWNER.NPWP_NUMBER + " VARCHAR2(50), " +
            MST_OUTLET_OWNER.OWNER_ADDRESS + " VARCHAR2(1000), " +
            MST_OUTLET_OWNER.COMPANY_NAME + " VARCHAR2(200), " +
            MST_OUTLET_OWNER.COMPANY_NPWP + " VARCHAR2(50), " +
            MST_OUTLET_OWNER.COMPANY_ADDRESS + " VARCHAR2(1000), " +
            MST_OUTLET_OWNER.WEEK + " NUMBER, " +
            MST_OUTLET_OWNER.IS_PKP + " VARCHAR2(1), " +
            MST_OUTLET_OWNER.COMPANY_EMAIL + " VARCHAR2(100)," +
            MST_OUTLET_OWNER.LEGAL_ENTITY_ID + " NUMBER, " +
            MST_OUTLET_OWNER.FLAG_PHOTO_KTP + " VARCHAR2(1), " +
            MST_OUTLET_OWNER.PHONE_NO + " VARCHAR2(20), " +
            MST_OUTLET_OWNER.OUTLET_NAME_NON_BDN_HKM + " VARCHAR(100), " +
            MST_OUTLET_OWNER.ID_CARD_PATH_FILE + " VARCHAR2(200) )";

    // MST_OI_PKS
    private String CREATE_MST_OI_PKS = "CREATE TABLE IF NOT EXISTS " + MST_OI_PKS.TABLE + " (" +
            MST_OI_PKS.ID + " NUMBER PRIMARY KEY, " +
            MST_OI_PKS.PLAN_ID + " NUMBER, " +
            MST_OI_PKS.OUTLET_ID + " VARCHAR2(20), " +
            MST_OI_PKS.PP_NO + " NUMBER, " +
            MST_OI_PKS.PROGRAM_NAME + " VARCHAR2(100), " +
            MST_OI_PKS.PROGRAM_TYPE_ID + " NUMBER, " +
            MST_OI_PKS.BRAND_ID + " NUMBER, " +
            MST_OI_PKS.BRAND_CODE + " VARCHAR2(10), " +
            MST_OI_PKS.PAYMENT_TYPE_ID + " NUMBER, " +
            MST_OI_PKS.CONTRACT_ID + " NUMBER, " +
            MST_OI_PKS.CONTRACT_AMOUNT + " NUMBER, " +
            MST_OI_PKS.CONTRACT_PERIODE_START + " DATE, " +
            MST_OI_PKS.CONTRACT_PERIODE_END + " DATE, " +
            MST_OI_PKS.WEEK + " NUMBER, " +
            MST_OI_PKS.PROGRAM_RANK + " NUMBER )";

    // MST_OI_PKS_DTL
    private String CREATE_MST_OI_PKS_DTL = "CREATE TABLE IF NOT EXISTS " + MST_OI_PKS_DTL.TABLE + " (" +
            MST_OI_PKS_DTL.ID + " NUMBER PRIMARY KEY, " +
            MST_OI_PKS_DTL.MST_OI_PKS_ID + " NUMBER, " +
            MST_OI_PKS_DTL.STEP + " NUMBER, " +
            MST_OI_PKS_DTL.FEE + " NUMBER, " +
            MST_OI_PKS_DTL.WEEK + " NUMBER, " +
            MST_OI_PKS_DTL.PP_NO + " NUMBER, " +
            MST_OI_PKS_DTL.PP_ID + " NUMBER )";

    /**
     * @modified lukkis 07-FEB-2023
     * CR TTO AFTER PILOTING
     * - Add TERM_AND_COND_PDP
     */
    // TR_TTO_HDR
    private String CREATE_TR_TTO_HDR = "CREATE TABLE IF NOT EXISTS " + TR_TTO_HDR.TABLE + " (" +
            TR_TTO_HDR.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TR_TTO_HDR.TR_PP_PROG_EXEC_ID + " NUMBER, " +
            TR_TTO_HDR.OUTLET_ID + " VARCHAR2(20), " +
            TR_TTO_HDR.OU_CODE + " VARCHAR, " +
            TR_TTO_HDR.TERITORY_CODE + " VARCHAR, " +
            TR_TTO_HDR.DISTRICT_CODE + " VARCHAR, " +
            TR_TTO_HDR.ROUTE + " INTEGER, " +
            TR_TTO_HDR.TR_TTO_RECEIVER_ID + " NUMBER, " +
            TR_TTO_HDR.TTO_NUMBER + " VARCHAR2, " +
            TR_TTO_HDR.TTO_TYPE_ID + " NUMBER, " +
            TR_TTO_HDR.TERM_AND_COND + " VARCHAR2(1), " +
            TR_TTO_HDR.DPP + " NUMBER, " +
            TR_TTO_HDR.PPN + " NUMBER, " +
            TR_TTO_HDR.PPH + " NUMBER, " +
            TR_TTO_HDR.NETTO + " NUMBER, " +
            TR_TTO_HDR.STATUS + " VARCHAR2(1), " +
            TR_TTO_HDR.TRANS_DATE + " NUMBER, " +
            TR_TTO_HDR.VERSION + " NUMBER, " +
            TR_TTO_HDR.DESCRIPTION + " VARCHAR2(1000), " +
            TR_TTO_HDR.IS_NEW_PROGRAM + " VARCHAR2(1), " +
            TR_TTO_HDR.LEGAL_ENTITY_ID + " NUMBER, " +
            TR_TTO_HDR.FLAG_NPWP + " VARCHAR2(1), " +
            TR_TTO_HDR.IS_GROSS_UP + " VARCHAR2(1), " +
            TR_TTO_HDR.IS_PKP + " VARCHAR2(1), " +
            TR_TTO_HDR.PPH_PERCENT_ID + " NUMBER, " +
            TR_TTO_HDR.PATH_FILE + " VARCHAR2(500), " +
            TR_TTO_HDR.TRANS_DATE_PDF + " NUMBER, " +
            TR_TTO_HDR.BENEFIT_RECIPIENT_ID + " NUMBER, " +
            TR_TTO_HDR.DATE_CREATED + " NUMBER, " +
            TR_TTO_HDR.USER_CREATED + " VARCHAR2(20), " +
            TR_TTO_HDR.IS_SPLIT + " VARCHAR(1), " +
            TR_TTO_HDR.FLAG_KUNJUNGAN_KEMBALI + " VARCHAR(1), " +
            TR_TTO_HDR.TERM_AND_COND_PDP + " VARCHAR(1)," +
            TR_TTO_HDR.PAYMENT_METHOD + " NUMBER," +
            TR_TTO_HDR.TR_TTO_RECEIVER_BANK_ID + " NUMBER," +
            TR_TTO_HDR.IS_RECIPIENT_SIGN + " VARCHAR(1))";

    /**
     * @modified by lukkis 16 JAN 2023
     * CR4 JAN 2023 TTO
     * Add column MST_TTO_RECEIVER_ID
     */
    // TR_TTO_RECEIVER
    private String CREATE_TR_TTO_RECEIVER = "CREATE TABLE IF NOT EXISTS " + TR_TTO_RECEIVER.TABLE + " (" +
            TR_TTO_RECEIVER.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TR_TTO_RECEIVER.NAME + " VARCHAR2(500), " +
            TR_TTO_RECEIVER.IDENTITY_NUMBER + " VARCHAR2(25), " +
            TR_TTO_RECEIVER.ID_CARD_PATH_FILE + " VARCHAR2(500), " +
            TR_TTO_RECEIVER.NPWP_NUMBER + " VARCHAR2(25), " +
            TR_TTO_RECEIVER.ADDRESS + " VARCHAR2(1000), " +
            TR_TTO_RECEIVER.RELATION_ID + " NUMBER, " +
            TR_TTO_RECEIVER.PHONE_NO + " VARCHAR2(25), " +
            TR_TTO_RECEIVER.USER_CREATED + " VARCHAR2(30), " +
            TR_TTO_RECEIVER.DATE_CREATED + " DATE, " +
            TR_TTO_RECEIVER.OUTLET_ID + " VARCHAR2(25), " +
            TR_TTO_RECEIVER.STATUS + " VARCHAR2(1), " +
            TR_TTO_RECEIVER.FLAG_KUNJUNGAN_KEMBALI + " VARCHAR2(1), " +
            TR_TTO_RECEIVER.MST_TTO_RECEIVER_ID + " NUMBER) ";

    // TR_TTO_CONTRACT
    private String CREATE_TR_TTO_CONTRACT = "CREATE TABLE IF NOT EXISTS " + TR_TTO_CONTRACT.TABLE + " (" +
            TR_TTO_CONTRACT.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TR_TTO_CONTRACT.TR_TTO_HDR_ID + " NUMBER, " +
            TR_TTO_CONTRACT.PERIODE_START + " NUMBER, " +
            TR_TTO_CONTRACT.PERIODE_END + " NUMBER, " +
            TR_TTO_CONTRACT.PAYMENT_TYPE_ID + " NUMBER, " +
            TR_TTO_CONTRACT.CONTRACT_INPUT + " NUMBER, " +
            TR_TTO_CONTRACT.CONTRACT_AMOUNT + " NUMBER, " +
            TR_TTO_CONTRACT.DATE_CREATED + " NUMBER, " +
            TR_TTO_CONTRACT.USER_CREATED + " VARCHAR2(20), " +
            TR_TTO_CONTRACT.NETT_AMOUNT + " NUMBER, " +
            TR_TTO_CONTRACT.STATUS + " VARCHAR2(1), " +
            TR_TTO_CONTRACT.PERIOD_MONTH + " NUMBER)";

    // TR_TTO_PRODUCT_DISPLAY
    private String CREATE_TR_TTO_PRODUCT_DISPLAY = "CREATE TABLE IF NOT EXISTS " + TR_TTO_PRODUCT_DISPLAY.TABLE + " (" +
            TR_TTO_PRODUCT_DISPLAY.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TR_TTO_PRODUCT_DISPLAY.PRODUCT_ID + " NUMBER, " +
            TR_TTO_PRODUCT_DISPLAY.PRODUCT_CODE + " VARCHAR2(500), " +
            TR_TTO_PRODUCT_DISPLAY.DATE_CREATED + " NUMBER, " +
            TR_TTO_PRODUCT_DISPLAY.USER_CREATED + " VARCHAR2(20), " +
            TR_TTO_PRODUCT_DISPLAY.TR_TTO_CONTRACT_ID + " NUMBER, " +
            TR_TTO_PRODUCT_DISPLAY.STATUS + " VARCHAR2(1))";

    // TR_TTO_UNIT_DISPLAY
    private String CREATE_TR_TTO_UNIT_DISPLAY = "CREATE TABLE IF NOT EXISTS " + TR_TTO_UNIT_DISPLAY.TABLE + " (" +
            TR_TTO_UNIT_DISPLAY.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TR_TTO_UNIT_DISPLAY.UNIT_DISPLAY_ID + " NUMBER, " +
            TR_TTO_UNIT_DISPLAY.UNIT_DISPLAY_SUBCATEGORY + " VARCHAR2(500), " +
            TR_TTO_UNIT_DISPLAY.DATE_CREATED + " NUMBER, " +
            TR_TTO_UNIT_DISPLAY.USER_CREATED + " VARCHAR2(20), " +
            TR_TTO_UNIT_DISPLAY.TR_TTO_CONTRACT_ID + " NUMBER, " +
            TR_TTO_UNIT_DISPLAY.QTY + " NUMBER, " +
            TR_TTO_UNIT_DISPLAY.STATUS + " VARCHAR2(1)) ";

    // TR_COMPENSATION_HIST
    private String CREATE_TR_COMPENSATION_HIST = "CREATE TABLE IF NOT EXISTS " + TR_COMPENSATION_HIST.TABLE + " (" +
            TR_COMPENSATION_HIST.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TR_COMPENSATION_HIST.TR_COMPENSATION_ID + " NUMBER, " +
            TR_COMPENSATION_HIST.TR_PROG_EXEC_ID + " NUMBER, " +
            TR_COMPENSATION_HIST.TR_BPPM_DTL_ID + " NUMBER, " +
            TR_COMPENSATION_HIST.TR_BPPM_ALOCATION_ID + " NUMBER, " +
            TR_COMPENSATION_HIST.OU_CODE + " VARCHAR2, " +
            TR_COMPENSATION_HIST.GIFT_COMPS_TYPE_ID + " NUMBER, " +
            TR_COMPENSATION_HIST.GIFT_PRODUCT_ID + " NUMBER, " +
            TR_COMPENSATION_HIST.GIFT_PRODUCT_QTY + " NUMBER, " +
            TR_COMPENSATION_HIST.REASON_COMPS_TYPE_ID + " NUMBER, " +
            TR_COMPENSATION_HIST.REASON_PRODUCT_ID + " NUMBER, " +
            TR_COMPENSATION_HIST.REASON_PRODUCT_QTY + " NUMBER, " +
            TR_COMPENSATION_HIST.STATUS + " VARCHAR2, " +
            TR_COMPENSATION_HIST.DATE_CREATED + " NUMBER, " +
            TR_COMPENSATION_HIST.TERITORY_CODE + " VARCHAR2, " +
            TR_COMPENSATION_HIST.DISTRICT_CODE + " VARCHAR2, " +
            TR_COMPENSATION_HIST.ROUTE + " NUMBER, " +
            TR_COMPENSATION_HIST.OUTLET_ID + " VARCHAR2, " +
            TR_COMPENSATION_HIST.OUTLET_NAME + " VARCHAR2, " +
            TR_COMPENSATION_HIST.BPPU_NO + " VARCHAR, " +
            TR_COMPENSATION_HIST.BPPM_NO + " VARCHAR, " +
            TR_COMPENSATION_HIST.DESCRIPTION + " VARCHAR, " +
            TR_COMPENSATION_HIST.TTO_VERSION + " NUMBER, " +
            TR_COMPENSATION_HIST.ITEM_WEIGHT + " VARCHAR )";

    //TR_NONPP_PROG_EXEC_HIST
    private String CREATE_TR_NONPP_PROG_EXEC_HIST = "CREATE TABLE IF NOT EXISTS " + TR_NONPP_PROG_EXEC_HIST.TABLE + " (" +
            TR_NONPP_PROG_EXEC_HIST.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TR_NONPP_PROG_EXEC_HIST.OU_CODE + " VARCHAR NOT NULL, " +
            TR_NONPP_PROG_EXEC_HIST.TERITORY_CODE + " VARCHAR, " +
            TR_NONPP_PROG_EXEC_HIST.DISTRICT_CODE + " VARCHAR, " +
            TR_NONPP_PROG_EXEC_HIST.ROUTE + " NUMBER, " +
            TR_NONPP_PROG_EXEC_HIST.OUTLET_ID + " VARCHAR, " +
            TR_NONPP_PROG_EXEC_HIST.OUTLET_NAME + " VARCHAR, " +
            TR_NONPP_PROG_EXEC_HIST.TR_BPPM_ALOCATION_ID + " NUMBER, " +
            TR_NONPP_PROG_EXEC_HIST.QTY + " NUMBER, " +
            TR_NONPP_PROG_EXEC_HIST.STATUS + " VARCHAR, " +
            TR_NONPP_PROG_EXEC_HIST.DATE_CREATED + " NUMBER, " +
            TR_NONPP_PROG_EXEC_HIST.TR_BPPM_DTL_ID + " NUMBER, " +
            TR_NONPP_PROG_EXEC_HIST.TR_NONPP_PROG_EXEC_ID + " NUMBER, " +
            TR_NONPP_PROG_EXEC_HIST.TTO_VERSION + " NUMBER, " +
            /*Add by lukkis 9-NOV-2022 After Merging WMS*/
            TR_NONPP_PROG_EXEC_HIST.TR_PU_ALLOCATION_ID + " NUMBER, " +
            TR_NONPP_PROG_EXEC_HIST.FLAG_PU + " VARCHAR2(1) DEFAULT 'N') ";

    //TR_UNIT_SUPPORT_HIST
    private String CREATE_TR_UNIT_SUPPORT_HIST = "CREATE TABLE IF NOT EXISTS " + TR_UNIT_SUPPORT_HIST.TABLE + " (" +
            TR_UNIT_SUPPORT_HIST.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TR_UNIT_SUPPORT_HIST.OU_CODE + " VARCHAR NOT NULL, " +
            TR_UNIT_SUPPORT_HIST.TERITORY_CODE + " VARCHAR, " +
            TR_UNIT_SUPPORT_HIST.DISTRICT_CODE + " VARCHAR, " +
            TR_UNIT_SUPPORT_HIST.ROUTE + " NUMBER, " +
            TR_UNIT_SUPPORT_HIST.OUTLET_ID + " VARCHAR, " +
            TR_UNIT_SUPPORT_HIST.OUTLET_NAME + " VARCHAR, " +
            TR_UNIT_SUPPORT_HIST.TR_PROG_EXEC_ID + " NUMBER, " +
            TR_UNIT_SUPPORT_HIST.TR_BPPM_ALOCATION_ID + " NUMBER, " +
            TR_UNIT_SUPPORT_HIST.QTY + " NUMBER, " +
            TR_UNIT_SUPPORT_HIST.STATUS + " VARCHAR, " +
            TR_UNIT_SUPPORT_HIST.DATE_CREATED + " NUMBER, " +
            TR_UNIT_SUPPORT_HIST.TR_BPPM_DTL_ID + " NUMBER, " +
            TR_UNIT_SUPPORT_HIST.DESCRIPTION + " VARCHAR, " +
            TR_UNIT_SUPPORT_HIST.TR_UNIT_SUPPORT_ID + " NUMBER, " +
            TR_UNIT_SUPPORT_HIST.TTO_VERSION + " NUMBER, " +
            /*Add by lukkis 9-NOV-2022 After Merging WMS*/
            TR_UNIT_SUPPORT_HIST.TR_PU_ALLOCATION_ID + " NUMBER, " +
            TR_UNIT_SUPPORT_HIST.FLAG_PU + " VARCHAR2(1) DEFAULT 'N') ";

    //TR_TTO_SIGNATURE
    private String CREATE_TR_TTO_SIGNATURE = "CREATE TABLE IF NOT EXISTS " + TR_TTO_SIGNATURE.TABLE + " (" +
            TR_TTO_SIGNATURE.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TR_TTO_SIGNATURE.TR_TTO_HDR_ID + " NUMBER, " +
            TR_TTO_SIGNATURE.PATH_FILE + " VARCHAR2(500), " +
            TR_TTO_SIGNATURE.SIGN_BY + " NUMBER, " +
            TR_TTO_SIGNATURE.SIGN_DATE + " NUMBER, " +
            TR_TTO_SIGNATURE.DATE_CREATED + " NUMBER, " +
            TR_TTO_SIGNATURE.USER_CREATED + " VARCHAR2(20), " +
            TR_TTO_SIGNATURE.DESCRIPTION + " VARCHAR2(500), " +
            TR_TTO_SIGNATURE.STATUS + " VARCHAR2(1), " +
            TR_TTO_SIGNATURE.LATITUDE_SIGN + " VARCHAR2(200), " +
            TR_TTO_SIGNATURE.LONGITUDE_SIGN + " VARCHAR2(200)) ";
    //CR - Tanda Terima
    // MST_CONTRACT
    private String CREATE_MST_CONTRACT = "CREATE TABLE IF NOT EXISTS " + MST_CONTRACT.TABLE + " (" +
            MST_CONTRACT.ID + " INTEGER PRIMARY KEY, " +
            MST_CONTRACT.OUTLET_ID + " VARCHAR2(20), " +
            MST_CONTRACT.PP_ID + " NUMBER, " +
            MST_CONTRACT.PP_NO + " NUMBER, " +
            MST_CONTRACT.PROGRAM_NAME + " VARCHAR2(200), " +
            MST_CONTRACT.PROGRAM_TYPE_ID + " NUMBER, " +
            MST_CONTRACT.BRAND_ID + " NUMBER, " +
            MST_CONTRACT.BRAND_CODE + " VARCHAR2(10), " +
            MST_CONTRACT.CONTRACT_NO + " VARCHAR2(40), " +
            MST_CONTRACT.PERIODE_START + " NUMBER, " +
            MST_CONTRACT.PERIODE_END + " NUMBER, " +
            MST_CONTRACT.PAYMENT_TYPE_ID + " NUMBER, " +
            MST_CONTRACT.CONTRACT_AMOUNT + " NUMBER, " +
            MST_CONTRACT.REMAIN_PAYMENT + " NUMBER, " +
            MST_CONTRACT.DPP + " NUMBER, " +
            MST_CONTRACT.PPN + " NUMBER, " +
            MST_CONTRACT.PPH + " NUMBER, " +
            MST_CONTRACT.NETT_AMOUNT + " NUMBER, " +
            MST_CONTRACT.LEGAL_ENTITY_ID + " NUMBER, " +
            MST_CONTRACT.FLAG_NPWP + " VARCHAR2(1), " +
            MST_CONTRACT.IS_GROSS_UP + " VARCHAR2(1), " +
            MST_CONTRACT.IS_PKP + " VARCHAR2(1), " +
            MST_CONTRACT.PPH_PERCENT_ID + " NUMBER, " +
            MST_CONTRACT.PROGRAM_RANK + " NUMBER, " +
            MST_CONTRACT.TTO_TYPE_ID + " NUMBER, " +
            MST_CONTRACT.WEEK + " NUMBER, " +
            MST_CONTRACT.CONTRACT_INPUT + " NUMBER, " +
            MST_CONTRACT.BENEFIT_RECIPIENT_ID + " NUMBER, " +
            MST_CONTRACT.PERIOD_MONTH + " NUMBER ) ";

    /**
     * @modifed by lukkis 16 JAN 2023
     * CR4 JAN 2023 TTO
     * Add column MST_TTO_RECEIVER_ID
     * @modifed by muhammadf16 26 FEB 2024
     * CR TTO 2024
     * Add column ID_CARD_PATH_FILE
     */
    // MST_TTO_RECEIVER
    private String CREATE_MST_TTO_RECEIVER = "CREATE TABLE IF NOT EXISTS " + MST_TTO_RECEIVER.TABLE + " (" +
            MST_TTO_RECEIVER.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MST_TTO_RECEIVER.OUTLET_ID + " NUMBER, " +
            MST_TTO_RECEIVER.NAME + " VARCHAR2(100), " +
            MST_TTO_RECEIVER.IDENTITY_NUMBER + " VARCHAR2(50), " +
            MST_TTO_RECEIVER.NPWP_NUMBER + " VARCHAR2(50), " +
            MST_TTO_RECEIVER.ADDRESS + " VARCHAR2(1000), " +
            MST_TTO_RECEIVER.FLAG_PHOTO_KTP + " VARCHAR2(1), " +
            MST_TTO_RECEIVER.RELATION_ID + " VARCHAR2(100), " +
            MST_TTO_RECEIVER.PHONE_NO + " VARCHAR2(20), " +
            MST_TTO_RECEIVER.MST_TTO_RECEIVER_ID + " NUMBER, " +
            MST_TTO_RECEIVER.ID_CARD_PATH_FILE + " VARCHAR2(200))";

    /*
     * Edited by lukkis
     * 10 OKT 2022
     * AR TTO SEPT
     * */
    // MST_TTO_MAX_AMP
    private String CREATE_MST_TTO_MAX_AMP = "CREATE TABLE IF NOT EXISTS " + MST_TTO_MAX_AMP.TABLE + " (" +
            MST_TTO_MAX_AMP.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MST_TTO_MAX_AMP.LEGAL_ENTITY_ID + " NUMBER, " +
            MST_TTO_MAX_AMP.FLAG_NPWP + " NUMBER, " +
            MST_TTO_MAX_AMP.IS_GROSS_UP + " VARCHAR2(1), " +
            MST_TTO_MAX_AMP.IS_PKP + " VARCHAR2(1), " +
            MST_TTO_MAX_AMP.MAX_AMP + " NUMBER, " +
            MST_TTO_MAX_AMP.USER_CREATED + " VARCHAR2(30), " +
            MST_TTO_MAX_AMP.DATE_CREATED + " DATE, " +
            MST_TTO_MAX_AMP.USER_MODIFIED + " VARCHAR2(30), " +
            MST_TTO_MAX_AMP.DATE_MODIFIED + " DATE)";

    // MST_PRODUCT_DISPLAY
    private String CREATE_MST_PRODUCT_DISPLAY = "CREATE TABLE IF NOT EXISTS " + MST_PRODUCT_DISPLAY.TABLE + " (" +
            MST_PRODUCT_DISPLAY.ID + " INTEGER PRIMARY KEY, " +
            MST_PRODUCT_DISPLAY.PRODUCT_ID + " NUMBER, " +
            MST_PRODUCT_DISPLAY.PRODUCT_CODE + " VARCHAR2(50), " +
            MST_PRODUCT_DISPLAY.MST_CONTRACT_ID + " NUMBER)";

    // MST_UNIT_DISPLAY
    private String CREATE_MST_UNIT_DISPLAY = "CREATE TABLE IF NOT EXISTS " + MST_UNIT_DISPLAY.TABLE + " (" +
            MST_UNIT_DISPLAY.ID + " INTEGER PRIMARY KEY, " +
            MST_UNIT_DISPLAY.UNIT_DISPLAY_ID + " NUMBER, " +
            MST_UNIT_DISPLAY.UNIT_DISPLAY_SUBCATEGORY + " VARCHAR2(500), " +
            MST_UNIT_DISPLAY.MST_CONTRACT_ID + " NUMBER, " +
            MST_UNIT_DISPLAY.QTY + " NUMBER)";

    // MST_TTO_PRODUCT
    private String CREATE_MST_TTO_PRODUCT = "CREATE TABLE IF NOT EXISTS MST_TTO_PRODUCT (\n" +
            "            ID                        INTEGER PRIMARY KEY\n" +
            "            , PRODUCT_ID              NUMBER\n" +
            "            , PRODUCT_CODE\t          VARCHAR2(150)\n" +
            "            , WEEK\t                  NUMBER\n" +
            "        )";

    // MST_TTO_ITEM_SUBCATEGORY
    private String CREATE_MST_TTO_ITEM_SUBCATEGORY = "CREATE TABLE IF NOT EXISTS MST_TTO_ITEM_SUBCATEGORY (\n" +
            "            ID                        INTEGER PRIMARY KEY\n" +
            "            , ITEM_SUBCATEGORY_ID     NUMBER\n" +
            "            , ITEM_SUBCATEGORY_NAME   VARCHAR2(150)\n" +
            "            , WEEK\t                  NUMBER\n" +
            "            , CATEGORY_ID\t           NUMBER\n" +
            "            , CATEGORY_NAME\t         VARCHAR2(50)\n" +
            "            , USER_CREATED\t          VARCHAR2(30)\n" +
            "            , DATE_CREATED\t          DATE\n" +
            "        )";
    /**
     * Add by Novalm 12-okt-2022
     * AR TTO September 2022
     * - TR_OUTLET_OWNER
     * - TR_OUTLET_PKP
     *
     * @modified lukkis 07-FEB-2023
     * CR TTO AFTER PILOTING
     * - Add phone number
     */
    //TR_OUTLET_OWNER
    private String CREATE_TR_OUTLET_OWNER = "CREATE TABLE IF NOT EXISTS " + TR_OUTLET_OWNER.TABLE + " (" +
            TR_OUTLET_OWNER.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TR_OUTLET_OWNER.OUTLET_ID + " VARCHAR2(20), " +
            TR_OUTLET_OWNER.NAME + " VARCHAR2(100), " +
            TR_OUTLET_OWNER.IDENTITY_NUMBER + " VARCHAR2(50), " +
            TR_OUTLET_OWNER.ID_CARD_PATH_FILE + " VARCHAR2(500), " +
            TR_OUTLET_OWNER.NPWP_NUMBER + " VARCHAR2(50), " +
            TR_OUTLET_OWNER.ADDRESS + " VARCHAR2(1000), " +
            TR_OUTLET_OWNER.USER_CREATED + " VARCHAR2(30), " +
            TR_OUTLET_OWNER.DATE_CREATED + " DATE," +
            TR_OUTLET_OWNER.PHONE_NO + " VARCHAR2(20)," +
            TR_OUTLET_OWNER.IS_UPLOADED + " VARCHAR2(1))";

    private String CREATE_TR_OUTLET_PKP = "CREATE TABLE IF NOT EXISTS " + TR_OUTLET_PKP.TABLE + " (" +
            TR_OUTLET_PKP.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TR_OUTLET_PKP.OUTLET_ID + " VARCHAR2(20), " +
            TR_OUTLET_PKP.IS_PKP + " VARCHAR2(1), " +
            TR_OUTLET_PKP.COMPANY_NAME + " VARCHAR2(100), " +
            TR_OUTLET_PKP.COMPANY_NPWP + " VARCHAR2(50), " +
            TR_OUTLET_PKP.COMPANY_ADDRESS + " VARCHAR2(1000), " +
            TR_OUTLET_PKP.COMPANY_EMAIL + " VARCHAR2(100), " +
            TR_OUTLET_PKP.LEGAL_ENTITY_ID + " NUMBER, " +
            TR_OUTLET_PKP.USER_CREATED + " VARCHAR2(30), " +
            TR_OUTLET_PKP.DATE_CREATED + " DATE, " +
            TR_OUTLET_PKP.OUTLET_NAME_NON_BDN_HKM + " VARCHAR(100)," +
            TR_OUTLET_PKP.IS_UPLOADED + " VARCHAR2(1))";
    //endregion

    /*
     * Add by lukkis 21-OKT-2022
     * AR TTO SEPT 2022
     * */
    private String CREATE_TR_TTO_DTL = "CREATE TABLE IF NOT EXISTS " + TR_TTO_DTL.TABLE + " (" +
            TR_TTO_DTL.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TR_TTO_DTL.TR_TTO_HDR_ID + " NUMBER, " +
            TR_TTO_DTL.TR_BPPM_DTL_ID + " NUMBER, " +
            TR_TTO_DTL.GIFT_COMPS_TYPE_ID + " NUMBER, " +
            TR_TTO_DTL.GIFT_PRODUCT_ID + " NUMBER, " +
            TR_TTO_DTL.TR_PU_ALOCATION_ID + " NUMBER, " +
            TR_TTO_DTL.ITEM_ID + " NUMBER, " +
            TR_TTO_DTL.QTY + " NUMBER, " +
            TR_TTO_DTL.STATUS + " VARCHAR2(1), " +
            TR_TTO_DTL.DATE_CREATED + " DATE, " +
            TR_TTO_DTL.USER_CREATED + " VARCHAR2(30))";

    /**
     * Add by hafizhr
     * 03-MAR-2023
     * CR AFTER PILOTING CYCLE 2
     */
    // TR_PP_PROG_EXEC_LOG
    private String CREATE_TR_PP_PROG_EXEC_LOG = "CREATE TABLE `TR_PP_PROG_EXEC_LOG` (" +
            "`ID` INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "`TR_PP_PROG_EXEC_ID` INTEGER, " +
            "`OU_CODE` VARCHAR, " +
            "`TERRITORY_CODE` VARCHAR, " +
            "`DISTRICT_CODE` VARCHAR, " +
            "`ROUTE` INTEGER, " +
            "`OUTLET_ID` VARCHAR, " +
            "`OUTLET_NAME` VARCHAR, " +
            "`PP_PROGRAM_ID` INTEGER, " +
            "`STATUS` VARCHAR, " +
            "`DATE_CREATED` INTEGER, " +
            "`WEEK` NUMBER, " +
            "`FLAG_KUNJUNGAN_KEMBALI` VARCHAR(1), " +
            "`TR_TTO_LOG_ID` NUMBER )";

    // TR_PROG_PHOTO_LOG
    private String CREATE_TR_PROG_PHOTO_LOG = "CREATE TABLE IF NOT EXISTS TR_PROG_PHOTO_LOG ( " +
            "`ID` INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "`TR_PROG_PHOTO_ID` NUMBER, " +
            "`TR_PP_PROG_ID` NUMBER, " +
            "`TR_NONPP_PROG_ID` NUMBER, " +
            "`OUTLET_ID` VARCHAR(70), " +
            "`FILE_NAME` VARCHAR(500), " +
            "`DESCRIPTION` VARCHAR(30), " +
            "`PP_NUMBER` NUMBER, " +
            "`BRAND_CODE` VARCHAR(10), " +
            "`PROGRAM_NAME` VARCHAR(50), " +
            "`WEEK` NUMBER, " +
            "`TR_TTO_LOG_ID` NUMBER )";

    // TR_COMPENSATION_LOG
    private String CREATE_TR_COMPENSATION_LOG = "CREATE TABLE `TR_COMPENSATION_LOG` (" +
            "`ID` INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "`TR_COMPENSATION_ID` INTEGER, " +
            "`OU_CODE` VARCHAR, " +
            "`TERRITORY_CODE` VARCHAR, " +
            "`DISTRICT_CODE` VARCHAR, " +
            "`ROUTE` NUMBER, " +
            "`OUTLET_ID` VARCHAR, " +
            "`OUTLET_NAME` VARCHAR, " +
            "`TR_PROGRAM_EXECUTION_ID` INTEGER, " +
            "`TR_BPPM_ALOCATION_ID` INTEGER, " +
            "`GIFT_COMPS_TYPE_ID` INTEGER, " +
            "`GIFT_PRODUCT_ID` INTEGER, " +
            "`GIFT_QTY` INTEGER, " +
            "`REASON_COMPS_TYPE_ID` INTEGER, " +
            "`REASON_PRODUCT_ID` INTEGER, " +
            "`REASON_QTY` INTEGER, " +
            "`STATUS` VARCHAR, " +
            "`DATE_CREATED` INTEGER, " +
            "`TR_BPPM_DTL_ID` INTEGER, " +
            "`BPPU_NO` VARCHAR, " +
            "`BPPM_NO` VARCHAR, " +
            "`DESCRIPTION` VARCHAR, " +
            "`WEEK` NUMBER, " +
            "`TR_TTO_LOG_ID` NUMBER, " +
            "`TR_TTO_HDR_ID` NUMBER, " +
            "`ITEM_WEIGHT` VARCHAR)";

    // TR_UNIT_SUPPORT_LOG
    private String CREATE_TR_UNIT_SUPPORT_LOG = "CREATE TABLE `TR_UNIT_SUPPORT_LOG` (" +
            "`ID` INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "`TR_UNIT_SUPPORT_ID` INTEGER, " +
            "`OU_CODE` VARCHAR, " +
            "`TERRITORY_CODE` VARCHAR, " +
            "`DISTRICT_CODE` VARCHAR, " +
            "`ROUTE` INTEGER, " +
            "`OUTLET_ID` VARCHAR, " +
            "`OUTLET_NAME` VARCHAR, " +
            "`TR_PROGRAM_EXECUTION_ID` INTEGER, " +
            "`TR_BPPM_DTL_ID` INTEGER, " +
            "`TR_BPPM_ALOCATION_ID` INTEGER, " +
            "`QTY` INTEGER, " +
            "`STATUS` VARCHAR, " +
            "`DATE_CREATED` INTEGER, " +
            "`DESCRIPTION` VARCHAR,"
            + "`TR_PU_ALLOCATION_ID` NUMBER,"
            + "`FLAG_PU` VARCHAR(1) DEFAULT 'N'," +
            " `WEEK` NUMBER," +
            " `TR_TTO_LOG_ID` NUMBER, " +
            " `TR_TTO_HDR_ID` NUMBER )";

    // TR_TTO_HDR_LOG
    private String CREATE_TR_TTO_HDR_LOG = "CREATE TABLE IF NOT EXISTS TR_TTO_HDR_LOG (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "TR_TTO_HDR_ID NUMBER, " +
            "TR_PP_PROG_EXEC_ID NUMBER, " +
            "OUTLET_ID VARCHAR2(20), " +
            "OU_CODE VARCHAR, " +
            "TERITORY_CODE VARCHAR, " +
            "DISTRICT_CODE VARCHAR, " +
            "ROUTE INTEGER, " +
            "TR_TTO_RECEIVER_ID NUMBER, " +
            "TTO_NUMBER VARCHAR2, " +
            "TTO_TYPE_ID NUMBER, " +
            "TERM_AND_COND VARCHAR2(1), " +
            "DPP NUMBER, " +
            "PPN NUMBER, " +
            "PPH NUMBER, " +
            "NETTO NUMBER, " +
            "STATUS VARCHAR2(1), " +
            "TRANS_DATE NUMBER, " +
            "VERSION NUMBER, " +
            "DESCRIPTION VARCHAR2(1000), " +
            "IS_NEW_PROGRAM VARCHAR2(1), " +
            "LEGAL_ENTITY_ID NUMBER, " +
            "FLAG_NPWP VARCHAR2(1), " +
            "IS_GROSS_UP VARCHAR2(1), " +
            "IS_PKP VARCHAR2(1), " +
            "PPH_PERCENT_ID NUMBER, " +
            "PATH_FILE VARCHAR2(500), " +
            "TRANS_DATE_PDF NUMBER, " +
            "BENEFIT_RECIPIENT_ID NUMBER, " +
            "DATE_CREATED NUMBER, " +
            "USER_CREATED VARCHAR2(20), " +
            "IS_SPLIT VARCHAR(1), " +
            "FLAG_KUNJUNGAN_KEMBALI VARCHAR(1), " +
            "TERM_AND_COND_PDP VARCHAR(1)," +
            "TR_TTO_LOG_ID NUMBER," +
            "PAYMENT_METHOD NUMBER," +
            "TR_TTO_RECEIVER_BANK_ID NUMBER," +
            "IS_RECIPIENT_SIGN VARCHAR(1))";

    // TR_TTO_CONTRACT_LOG
    private String CREATE_TR_TTO_CONTRACT_LOG = "CREATE TABLE IF NOT EXISTS TR_TTO_CONTRACT_LOG (" +
            "ID  INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "TR_TTO_CONTRACT_ID NUMBER, " +
            "TR_TTO_HDR_ID  NUMBER, " +
            "PERIODE_START  NUMBER, " +
            "PERIODE_END  NUMBER, " +
            "PAYMENT_TYPE_ID  NUMBER, " +
            "CONTRACT_INPUT  NUMBER, " +
            "CONTRACT_AMOUNT  NUMBER, " +
            "DATE_CREATED  NUMBER, " +
            "USER_CREATED  VARCHAR2(20), " +
            "NETT_AMOUNT  NUMBER, " +
            "TR_TTO_LOG_ID NUMBER, " +
            "STATUS VARCHAR2(1), " +
            "PERIOD_MONTH NUMBER )";

    // TR_TTO_PRODUCT_DISPLAY_LOG
    private String CREATE_TR_TTO_PRODUCT_DISPLAY_LOG = "CREATE TABLE IF NOT EXISTS TR_TTO_PRODUCT_DISPLAY_LOG (" +
            "ID  INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "TR_TTO_PRODUCT_DISPLAY_ID NUMBER, " +
            "PRODUCT_ID  NUMBER, " +
            "PRODUCT_CODE  VARCHAR2(500), " +
            "DATE_CREATED  NUMBER, " +
            "USER_CREATED  VARCHAR2(20), " +
            "TR_TTO_CONTRACT_ID  NUMBER, " +
            "TR_TTO_LOG_ID NUMBER, " +
            "STATUS VARCHAR2(1) )";

    // TR_TTO_UNIT_DISPLAY_LOG
    private String CREATE_TR_TTO_UNIT_DISPLAY_LOG = "CREATE TABLE IF NOT EXISTS TR_TTO_UNIT_DISPLAY_LOG (" +
            "ID  INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "TR_TTO_UNIT_DISPLAY_ID  NUMBER, " +
            "UNIT_DISPLAY_ID  NUMBER, " +
            "UNIT_DISPLAY_SUBCATEGORY  VARCHAR2(500), " +
            "DATE_CREATED  NUMBER, " +
            "USER_CREATED  VARCHAR2(20), " +
            "TR_TTO_CONTRACT_ID  NUMBER, " +
            "TR_TTO_LOG_ID NUMBER, " +
            "QTY NUMBER, " +
            "STATUS VARCHAR2(1))";

    // TR_TTO_DTL_LOG
    private String CREATE_TR_TTO_DTL_LOG = "CREATE TABLE IF NOT EXISTS TR_TTO_DTL_LOG (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "TR_TTO_DTL_ID NUMBER, " +
            "TR_TTO_HDR_ID NUMBER, " +
            "TR_BPPM_DTL_ID NUMBER, " +
            "GIFT_COMPS_TYPE_ID NUMBER, " +
            "GIFT_PRODUCT_ID NUMBER, " +
            "TR_PU_ALOCATION_ID NUMBER, " +
            "ITEM_ID NUMBER, " +
            "QTY NUMBER, " +
            "STATUS VARCHAR2(1), " +
            "DATE_CREATED DATE, " +
            "USER_CREATED VARCHAR2(30), " +
            "TR_TTO_LOG_ID NUMBER )";

    //TR_TTO_SIGNATURE_LOG
    private String CREATE_TR_TTO_SIGNATURE_LOG = "CREATE TABLE IF NOT EXISTS TR_TTO_SIGNATURE_LOG (" +
            "ID  INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "TR_TTO_SIGNATURE_ID  NUMBER, " +
            "TR_TTO_HDR_ID  NUMBER, " +
            "PATH_FILE  VARCHAR2(500), " +
            "SIGN_BY  NUMBER, " +
            "SIGN_DATE  NUMBER, " +
            "DATE_CREATED  NUMBER, " +
            "USER_CREATED  VARCHAR2(20), " +
            "DESCRIPTION  VARCHAR2(500), " +
            "TR_TTO_LOG_ID NUMBER, " +
            "STATUS VARCHAR2(1), " +
            "LATITUDE_SIGN VARCHAR2(200), " +
            "LONGITUDE_SIGN VARCHAR2(200))";

    // TR_TTO_RECEIVER_LOG
    private String CREATE_TR_TTO_RECEIVER_LOG = "CREATE TABLE IF NOT EXISTS TR_TTO_RECEIVER_LOG (" +
            "ID  INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "TR_TTO_RECEIVER_ID  NUMBER, " +
            "NAME  VARCHAR2(500), " +
            "IDENTITY_NUMBER  VARCHAR2(25), " +
            "ID_CARD_PATH_FILE  VARCHAR2(500), " +
            "NPWP_NUMBER  VARCHAR2(25), " +
            "ADDRESS  VARCHAR2(1000), " +
            "RELATION_ID  NUMBER, " +
            "PHONE_NO  VARCHAR2(25), " +
            "USER_CREATED  VARCHAR2(30), " +
            "DATE_CREATED  DATE, " +
            "OUTLET_ID  VARCHAR2(25), " +
            "STATUS  VARCHAR2(1), " +
            "FLAG_KUNJUNGAN_KEMBALI VARCHAR(1), " +
            "MST_TTO_RECEIVER_ID  NUMBER, " +
            "TR_TTO_LOG_ID NUMBER )";

    //TR_OUTLET_OWNER_LOG
    private String CREATE_TR_OUTLET_OWNER_LOG = "CREATE TABLE IF NOT EXISTS TR_OUTLET_OWNER_LOG (" +
            "ID  INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "TR_OUTLET_OWNER_ID  NUMBER, " +
            "OUTLET_ID  VARCHAR2(20), " +
            "NAME  VARCHAR2(100), " +
            "IDENTITY_NUMBER  VARCHAR2(50), " +
            "ID_CARD_PATH_FILE  VARCHAR2(500), " +
            "NPWP_NUMBER  VARCHAR2(50), " +
            "ADDRESS  VARCHAR2(1000), " +
            "USER_CREATED  VARCHAR2(30), " +
            "DATE_CREATED  DATE," +
            "PHONE_NO  VARCHAR2(20), " +
            "TR_TTO_LOG_ID NUMBER )";

    //TR_OUTLET_PKP_LOG
    private String CREATE_TR_OUTLET_PKP_LOG = "CREATE TABLE IF NOT EXISTS TR_OUTLET_PKP_LOG (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "TR_OUTLET_PKP_ID  NUMBER, " +
            "OUTLET_ID VARCHAR2(20), " +
            "IS_PKP VARCHAR2(1), " +
            "COMPANY_NAME VARCHAR2(100), " +
            "COMPANY_NPWP VARCHAR2(50), " +
            "COMPANY_ADDRESS VARCHAR2(1000), " +
            "COMPANY_EMAIL VARCHAR2(100), " +
            "LEGAL_ENTITY_ID NUMBER, " +
            "USER_CREATED VARCHAR2(30), " +
            "DATE_CREATED DATE, " +
            "TR_TTO_LOG_ID NUMBER, " +
            "OUTLET_NAME_NON_BDN_HKM VARCHAR2(100) )";

    //TR_TTO_LOG
    private String CREATE_TR_TTO_LOG = "CREATE TABLE IF NOT EXISTS TR_TTO_LOG (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "TR_TTO_HDR_ID NUMBER, " +
            "CS_TTO_HDR_ID NUMBER, " +
            "TTO_NUMBER VARCHAR2, " +
            "LOG_DATE NUMBER, " +
            "LOG_VERSION NUMBER, " +
            "RECEIVER_SIGN VARCHAR2(1), " +
            "RECEIVER_SIGN_DATE NUMBER, " +
            "USER_CREATED VARCHAR2(30), " +
            "DATE_CREATED NUMBER )";

    //TR_ADDT_UNIT
    private String CREATE_TR_ADDT_UNIT = "CREATE TABLE IF NOT EXISTS " + TR_ADDT_UNIT.TABLE + " (" +
            TR_ADDT_UNIT.ID + " NUMBER PRIMARY KEY ASC," +
            TR_ADDT_UNIT.WEEK + " NUMBER," +
            TR_ADDT_UNIT.OUTLET_ID + " VARCHAR(30)," +
            TR_ADDT_UNIT.SUBCATEGORY_ID + " NUMBER," +
            TR_ADDT_UNIT.SUBCATEGORY_NAME + " NUMBER," +
            TR_ADDT_UNIT.BRAND_MARKETING_ID + " NUMBER," +
            TR_ADDT_UNIT.BRAND_CODE + " VARCHAR(5)," +
            TR_ADDT_UNIT.QTY + " NUMBER," +
            TR_ADDT_UNIT.PLANOGRAM + " NUMBER," +
            TR_ADDT_UNIT.CLEAN + " NUMBER," +
            TR_ADDT_UNIT.POSITION + " NUMBER," +
            TR_ADDT_UNIT.UNIT_GOOD + " NUMBER," +
            TR_ADDT_UNIT.UNIT_BAD + " NUMBER," +
            TR_ADDT_UNIT.VIS_GOOD + " NUMBER," +
            TR_ADDT_UNIT.VIS_BAD + " NUMBER," +
            TR_ADDT_UNIT.STATUS + " VARCHAR(1)," +
            TR_ADDT_UNIT.USER_CREATED + " VARCHAR(30)," +
            TR_ADDT_UNIT.DATE_CREATED + " NUMBER," +
            TR_ADDT_UNIT.USER_MODIFIED + " VARCHAR2(30), " +
            TR_ADDT_UNIT.DATE_MODIFIED + " DATE, " +
            TR_ADDT_UNIT.DESCRIPTION + " VARCHAR2(250) )";

    //TTO_HDR_REKAP
    private String CREATE_TTO_HDR_REKAP = "CREATE TABLE IF NOT EXISTS TTO_HDR_REKAP (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "TR_TTO_HDR_ID NUMBER, " +
            "PP_PROGRAM_ID INTEGER, " +
            "PP_NUMBER INTEGER, " +
            "PROGRAM_NAME VARCHAR, " +
            "BRAND_CODE VARCHAR, " +
            "OUTLET_ID VARCHAR2(70), " +
            "OUTLET_NAME VARCHAR(100), " +
            "IS_CONTRACT VARCHAR(1), " +
            "TTO_NUMBER VARCHAR2, " +
            "TTO_TYPE_ID NUMBER, " +
            "TRANS_DATE NUMBER, " +
            "PAYMENT_METHOD NUMBER)";

    //TTO_DTL_REKAP
    private String CREATE_TTO_DTL_REKAP = "CREATE TABLE IF NOT EXISTS TTO_DTL_REKAP (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "TTO_HDR_REKAP_ID NUMBER, " +
            "TR_PP_PROG_EXEC_ID NUMBER, " +
            "RECEIVER_NAME VARCHAR2(100), " +
            "RELATION_ID NUMBER, " +
            "IDENTITY_NUMBER VARCHAR2(50), " +
            "NPWP_NUMBER VARCHAR2(50), " +
            "RECEIVER_ADDRESS VARCHAR2(1000), " +
            "ID_CARD_PATH_FILE VARCHAR2(500), " +
            "FLAG_PHOTO_KTP VARCHAR2(1), " +
            "PHONE_NO VARCHAR2(20), " +
            "LEGAL_ENTITY_ID NUMBER, " +
            "IS_GROSS_UP VARCHAR2(1), " +
            "DPP NUMBER, " +
            "PPN NUMBER, " +
            "PPH NUMBER, " +
            "NETTO NUMBER, " +
            "PPH_PERCENT_ID VARCHAR2, " +
            "IS_UPLOADED VARCHAR2(1), " +
            "BANK_NAME VARCHAR2(100), " +
            "ACCOUNT_NAME VARCHAR2(100), " +
            "BANK_ACCOUNT_NUMBER VARCHAR2(50))";

    //TTO_KLAUSUL_REKAP
    private String CREATE_TTO_KLAUSUL_REKAP = "CREATE TABLE IF NOT EXISTS TTO_KLAUSUL_REKAP (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "TTO_HDR_REKAP_ID NUMBER, " +
            "PERIODE_START NUMBER, " +
            "PERIODE_END NUMBER, " +
            "PAYMENT_TYPE_ID NUMBER, " +
            "CONTRACT_INPUT NUMBER, " +
            "CONTRACT_AMOUNT NUMBER, " +
            "PERIOD_MONTH NUMBER )";

    //TTO_DISPLAY_REKAP
    private String CREATE_TTO_DISPLAY_REKAP = "CREATE TABLE IF NOT EXISTS TTO_DISPLAY_REKAP (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "TTO_HDR_REKAP_ID NUMBER, " +
            "DISPLAY_TYPE_ID NUMBER, " +
            "ITEM_NAME VARCHAR2(500), " +
            "ITEM_QTY NUMBER )";

    //TTO_BARANG_REKAP
    private String CREATE_TTO_BARANG_REKAP = "CREATE TABLE IF NOT EXISTS TTO_BARANG_REKAP (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "TTO_HDR_REKAP_ID NUMBER, " +
            "ITEM_ID NUMBER, " +
            "ITEM_CODE VARCHAR(20), " +
            "ITEM_NAME VARCHAR(100), " +
            "ITEM_QTY NUMBER," +
            "GIFT_COMPS_TYPE_ID NUMBER," +
            "ITEM_WEIGHT VARCHAR(20))";

    //MST_OUTLET_OWNER_LOG
    private String CREATE_MST_OUTLET_OWNER_LOG = "CREATE TABLE IF NOT EXISTS MST_OUTLET_OWNER_LOG (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "MST_OUTLET_OWNER_ID NUMBER, " +
            "OUTLET_ID VARCHAR2(20), " +
            "OWNER_NAME VARCHAR2(100), " +
            "IDENTITY_NUMBER VARCHAR2(50), " +
            "NPWP_NUMBER VARCHAR2(50), " +
            "OWNER_ADDRESS VARCHAR2(1000), " +
            "COMPANY_NAME VARCHAR2(200), " +
            "COMPANY_NPWP VARCHAR2(50), " +
            "COMPANY_ADDRESS VARCHAR2(1000), " +
            "WEEK NUMBER, " +
            "IS_PKP VARCHAR2(1), " +
            "COMPANY_EMAIL VARCHAR2(100), " +
            "LEGAL_ENTITY_ID NUMBER, " +
            "FLAG_PHOTO_KTP VARCHAR2(1), " +
            "PHONE_NO VARCHAR2(20), " +
            "OUTLET_NAME_NON_BDN_HKM VARCHAR2(100), " +
            "TR_TTO_LOG_ID NUMBER, " +
            "ID_CARD_PATH_FILE VARCHAR2(1000))";

    /**
     * created by michells
     * CR Survey Volume 2023 - 2023-04-14
     * create new table : MST_OUTLET_UPLINE, MST_OUTLET_ALIAS, MST_ECOMMERCE, TR_UPLINE
     */
    private String CREATE_TABLE_MST_OUTLET_UPLINE = "CREATE TABLE IF NOT EXISTS MST_OUTLET_UPLINE (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "OUTLET_ID VARCHAR2(25), " +
            "OUTLET_CODE VARCHAR2(20), " +
            "OUTLET_NAME VARCHAR2(150), " +
            "AO_GROUP_CODE VARCHAR2(50), " +
            "AO_GROUP_NAME VARCHAR2(50), " +
            "ADDRESS VARCHAR2(200), " +
            "COUNTY_NAME VARCHAR2(100), " +
            "VILLAGE_NAME VARCHAR2(100), " +
            "SUBDISTRICT_NAME VARCHAR2(100), " +
            "SUBCHANNEL_NAME VARCHAR2(20)," +
            "USER_ID  VARCHAR2(50)," +
            "DATE_CREATED  DATE," +
            "USER_CREATED  VARCHAR2(30))";

    private String CREATE_TABLE_MST_OUTLET_ALIAS = "CREATE TABLE IF NOT EXISTS MST_OUTLET_ALIAS (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "OUTLET_ID VARCHAR2(25), " +
            "ALIAS VARCHAR2(50))";

    private String CREATE_TABLE_MST_ECOMMERCE = "CREATE TABLE IF NOT EXISTS MST_ECOMMERCE (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "ECOMMERCE_ID NUMBER, " +
            "NAME VARCHAR2(30))";

    private String CREATE_TABLE_TR_UPLINE = "CREATE TABLE IF NOT EXISTS TR_UPLINE (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "OUTLET_ID VARCHAR2(30), " +
            "UPLINE_TYPE NUMBER, " +
            "UPLINE_OUTLET_ID VARCHAR2(30)," +
            "USER_CREATED VARCHAR2(30)," +
            "DATE_CREATED NUMBER" +
            ")";
    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_MST_DATA_VERSION = "CREATE TABLE IF NOT EXISTS MST_DATA_VERSION (\n" +
            "    ID            INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TABLE_NAME    VARCHAR2(50),\n" +
            "    VERSION       NUMBER,\n" +
            "    STATUS        VARCHAR2(1),\n" +
            "    USER_CREATED  VARCHAR2(30),\n" +
            "    DATE_CREATED  DATE,\n" +
            "    USER_MODIFIED VARCHAR2(30),\n" +
            "    DATE_MODIFIED DATE\n" +
            ");";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_MST_IMPACT_PARAMETER = "CREATE TABLE IF NOT EXISTS MST_IMPACT_PARAMETER (\n" +
            "    ID              INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    IMPACT_PARAMETER_ID NUMBER,\n" +
            "    PARAMETER_POINT VARCHAR2(100),\n" +
            "    UOM_ID          NUMBER,\n" +
            "    STATUS          VARCHAR2(1),\n" +
            "    USER_CREATED    VARCHAR2(30),\n" +
            "    DATE_CREATED    DATE,\n" +
            "    USER_MODIFIED   VARCHAR2(30),\n" +
            "    DATE_MODIFIED   DATE\n" +
            ");";
    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_MST_PROGRAM_IMPACT = "CREATE TABLE IF NOT EXISTS MST_PROGRAM_IMPACT (\n" +
            "    ID                      INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    MST_IMPACT_PARAMETER_ID NUMBER NOT NULL,\n" +
            "    MST_PROGRAM_MAP_ID      NUMBER NOT NULL,\n" +
            "    STATUS                  VARCHAR2(1),\n" +
            "    USER_CREATED            VARCHAR2(30),\n" +
            "    DATE_CREATED            DATE,\n" +
            "    USER_MODIFIED           VARCHAR2(30),\n" +
            "    DATE_MODIFIED           DATE\n" +
            ");";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_CS_TOTAL_SPACE_SHARE = "CREATE TABLE IF NOT EXISTS CS_TOTAL_SPACE_SHARE (\n" +
            "    ID             INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    OUTLET_ID      VARCHAR2(15),\n" +
            "    VALUE          NUMBER,\n" +
            "    USER_CREATED   VARCHAR2(30),\n" +
            "    DATE_CREATED   DATE,\n" +
            "    USER_MODIFIED  VARCHAR2(30),\n" +
            "    DATE_MODIFIED  DATE\n" +
            ");\n";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_MST_TPBP = "CREATE TABLE IF NOT EXISTS MST_TPBP (\n" +
            "    ID             INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TP_HEADER_ID   NUMBER,\n" +
            "    OU_CODE        VARCHAR2(10),\n" +
            "    TERRITORY_CODE VARCHAR2(10),\n" +
            "    DISTRICT_CODE  VARCHAR2(10),\n" +
            "    ROUTE          NUMBER,\n" +
            "    TP_CATEGORY_ID NUMBER,\n" +
            "    TP_NAME        VARCHAR2(200),\n" +
            "    TP_ADDRESS     VARCHAR2(500),\n" +
            "    LANDMARK       VARCHAR2(200),\n" +
            "    OUTLET_ID      VARCHAR2(15),\n" +
            "    OUTLET_NAME    VARCHAR2(100),\n" +
            "    USER_ID        VARCHAR2(50),\n" +
            "    USER_CREATED   VARCHAR2(100),\n" +
            "    DATE_CREATED   DATE\n" +
            ");";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_MST_PROGRAM_MAP = "CREATE TABLE IF NOT EXISTS MST_PROGRAM_MAP (\n" +
            "    ID                 INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    MST_PROGRAM_MAP_ID NUMBER,\n" +
            "    PILAR_ID           NUMBER,\n" +
            "    SUBPILAR_ID        NUMBER,\n" +
            "    SUBPILAR_NAME      VARCHAR2, \n" +
            "    PROGRAM_ID         NUMBER,\n" +
            "    PROGRAM_NAME       VARCHAR2(50),\n" +
            "    CHANNEL_MKT_ID     NUMBER,\n" +
            "    CHANNEL_MKT_NAME   VARCHAR2(50),\n" +
            "    STATUS             VARCHAR2(1),\n" +
            "    USER_CREATED       VARCHAR2(30),\n" +
            "    DATE_CREATED       DATE,\n" +
            "    USER_MODIFIED      VARCHAR2(30),\n" +
            "    DATE_MODIFIED      DATE\n" +
            ");\n";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_MST_PROGRAM_UNIT = "CREATE TABLE IF NOT EXISTS MST_PROGRAM_UNIT (\n" +
            "    ID                      INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    MST_ITEM_SUBCATEGORY_ALIAS_ID NUMBER NOT NULL,\n" +
            "    MST_PROGRAM_MAP_ID      NUMBER NOT NULL,\n" +
            "    STATUS                  VARCHAR2(1),\n" +
            "    USER_CREATED            VARCHAR2(30),\n" +
            "    DATE_CREATED            DATE,\n" +
            "    USER_MODIFIED           VARCHAR2(30),\n" +
            "    DATE_MODIFIED           DATE\n" +
            ");\n";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_MST_OUTDOOR = "CREATE TABLE IF NOT EXISTS MST_OUTDOOR (\n" +
            "    ID                INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    OUTDOOR_ID        NUMBER,\n" +
            "    STATUS_VISIT_ID   NUMBER,\n" +
            "    OU_CODE           VARCHAR2(10),\n" +
            "    TERRITORY_CODE    VARCHAR2(5),\n" +
            "    DISTRICT_CODE     VARCHAR2(5),\n" +
            "    ROUTE             VARCHAR2(3),\n" +
            "    TYPE_OUTDOOR_ID   NUMBER,\n" +
            "    MANUF_SUBGROUP_ID NUMBER,\n" +
            "    BRAND_ID          NUMBER,\n" +
            "    MONTH             NUMBER,\n" +
            "    YEAR              NUMBER,\n" +
            "    LATITUDE          VARCHAR2(20),\n" +
            "    LONGITUDE         VARCHAR2(20),\n" +
            "    ADDRESS           VARCHAR2(1000),\n" +
            "    COUNTY_NAME       VARCHAR2(100),\n" +
            "    SUBDISTRICT_NAME  VARCHAR2(100),\n" +
            "    VILLAGE_NAME      VARCHAR2(100),\n" +
            "    USER_ID           VARCHAR2(50),\n" +
            "    PATH_FILE         VARCHAR2(500),\n" +
            "    WEEK              NUMBER,\n" +
            "    STATUS            VARCHAR2(1),\n" +
            "    OUTDOOR_NO        VARCHAR2(9),\n" +
            "    USER_CREATED      VARCHAR2(30),\n" +
            "    DATE_CREATED      DATE\n" +
            ");";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_MST_STREET_VISIBILITY = "CREATE TABLE IF NOT EXISTS MST_STREET_VISIBILITY (\n" +
            "    ID                   INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    STREET_VISIBILITY_ID NUMBER,\n" +
            "    STREET_VISIBILITY_TYPE_ID NUMBER,\n" +
            "    STATUS_VISIT_ID      NUMBER,\n" +
            "    OU_CODE              VARCHAR2(10),\n" +
            "    TERRITORY_CODE       VARCHAR2(5),\n" +
            "    DISTRICT_CODE        VARCHAR2(5),\n" +
            "    ROUTE                VARCHAR2(3),\n" +
            "    MANUF_SUBGROUP_ID    NUMBER,\n" +
            "    PRODUCT_ID           NUMBER,\n" +
            "    BRAND_GROUP_ID       NUMBER,\n" +
            "    MONTH                NUMBER,\n" +
            "    YEAR                 NUMBER,\n" +
            "    QTY                  NUMBER,\n" +
            "    TP_NAME              VARCHAR2(200),\n" +
            "    LANDMARK_NAME        VARCHAR2(200),\n" +
            "    LATITUDE             VARCHAR2(20),\n" +
            "    LONGITUDE            VARCHAR2(20),\n" +
            "    ADDRESS              VARCHAR2(1000),\n" +
            "    USER_ID              VARCHAR2(50),\n" +
            "    PATH_FILE            VARCHAR2(500),\n" +
            "    STATUS               VARCHAR2(1),\n" +
            "    WEEK                 NUMBER,\n" +
            "    USER_CREATED         VARCHAR2(30),\n" +
            "    DATE_CREATED         DATE\n" +
            ");";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_MST_PRODUCT_ACT_COMP = "CREATE TABLE IF NOT EXISTS MST_PRODUCT_ACT_COMP (\n" +
            "    ID                      INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    PRODUCT_ID              NUMBER,\n" +
            "    PRODUCT_CODE            VARCHAR2(150),\n" +
            "    BRAND_GROUP_ID          NUMBER,\n" +
            "    BRAND_GROUP_NAME        VARCHAR2(50),\n" +
            "    MANUFACTURE_SUBGROUP_ID NUMBER,\n" +
            "    PRODUCT_SEQN            NUMBER,\n" +
            "    USER_CREATED            VARCHAR2(30),\n" +
            "    DATE_CREATED            DATE\n" +
            ");";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_MST_MANUF_SUBGROUP = "CREATE TABLE IF NOT EXISTS MST_MANUF_SUBGROUP (\n" +
            "    ID                  INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    MANUF_SUBGROUP_ID   NUMBER,\n" +
            "    MANUF_SUBGROUP_NAME VARCHAR2(50),\n" +
            "    STATUS              VARCHAR2(1),\n" +
            "    USER_ID             VARCHAR2(50),\n" +
            "    USER_CREATED        VARCHAR2(20),\n" +
            "    DATE_CREATED        DATE,\n" +
            "    USER_MODIFIED       VARCHAR2(20),\n" +
            "    DATE_MODIFIED       DATE\n" +
            ");\n";

    /*
     * Add by bayus05 10-NOV-2023
     * CR NIRWANA
     * */
    private String CREATE_MST_BRAND_GROUP = "CREATE TABLE IF NOT EXISTS MST_BRAND_GROUP (\n" +
            "    ID                  INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    BRAND_GROUP_ID      NUMBER,\n" +
            "    BRAND_GROUP_NAME    VARCHAR2(50),\n" +
            "    STATUS              VARCHAR2(1),\n" +
            "    USER_CREATED        VARCHAR2(20),\n" +
            "    DATE_CREATED        DATE,\n" +
            "    USER_MODIFIED       VARCHAR2(20),\n" +
            "    DATE_MODIFIED       DATE\n" +
            ");\n";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_NEW_OUTDOOR = "CREATE TABLE IF NOT EXISTS TR_NEW_OUTDOOR (\n" +
            "    ID                INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    MANUF_SUBGROUP_ID NUMBER,\n" +
            "    TYPE_OUTDOOR_ID   NUMBER,\n" +
            "    OU_CODE           VARCHAR2(10),\n" +
            "    TERRITORY_CODE    VARCHAR2(5),\n" +
            "    DISTRICT_CODE     VARCHAR2(5),\n" +
            "    ROUTE             VARCHAR2(3),\n" +
            "    ADDRESS           VARCHAR2(1000),\n" +
            "    PATH_FILE         VARCHAR2(500),\n" +
            "    LATITUDE          VARCHAR2(20),\n" +
            "    LONGITUDE         VARCHAR2(20),\n" +
            "    WEEK              NUMBER,\n" +
            "    STATUS            VARCHAR2(1),\n" +
            "    USER_CREATED      VARCHAR2(30),\n" +
            "    DATE_CREATED      DATE,\n" +
            "    BRAND_VISUAL_ID   NUMBER,\n" +
            "    VISUAL_INFORMATION VARCHAR2(200)\n" +
            ");";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_CHECK_OUTDOOR = "CREATE TABLE IF NOT EXISTS TR_CHECK_OUTDOOR (\n" +
            "    ID                    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    OU_CODE               VARCHAR2(10),\n" +
            "    TERRITORY_CODE        VARCHAR2(5),\n" +
            "    DISTRICT_CODE         VARCHAR2(5),\n" +
            "    ROUTE                 VARCHAR2(3),\n" +
            "    OUTDOOR_ID            NUMBER,\n" +
            "    STATUS_VISIT_ID       NUMBER,\n" +
            "    TYPE_OUTDOOR_ID       NUMBER,\n" +
            "    MANUF_SUBGROUP_ID     NUMBER,\n" +
            "    BRAND_ID              NUMBER,\n" +
            "    MONTH                 NUMBER,\n" +
            "    YEAR                  NUMBER,\n" +
            "    LATITUDE              VARCHAR2(20),\n" +
            "    LONGITUDE             VARCHAR2(20),\n" +
            "    PATH_FILE             VARCHAR2(500),\n" +
            "    NEW_MANUF_SUBGROUP_ID NUMBER,\n" +
            "    NEW_BRAND_ID          NUMBER,\n" +
            "    VISUAL_INFORMATION    VARCHAR2(1000),\n" +
            "    WEEK                  NUMBER,\n" +
            "    FLAG_UPLOAD           VARCHAR2(1),\n" +
            "    STATUS                VARCHAR2(1),\n" +
            "    USER_CREATED          VARCHAR2(30),\n" +
            "    DATE_CREATED          DATE,\n" +
            "    USER_MODIFIED         VARCHAR2(30),\n" +
            "    DATE_MODIFIED         DATE,\n" +
            "    OUTDOOR_THEME_ID         NUMBER\n" +
            ");";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_CHECK_STREET_VISIBILITY = "CREATE TABLE IF NOT EXISTS TR_CHECK_STREET_VISIBILITY (\n" +
            "    ID                   INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    STREET_VISIBILITY_ID NUMBER,\n" +
            "    STREET_VISIBILITY_TYPE_ID NUMBER,\n" +
            "    STATUS_VISIT_ID      NUMBER,\n" +
            "    OU_CODE              VARCHAR2(10),\n" +
            "    TERRITORY_CODE       VARCHAR2(5),\n" +
            "    DISTRICT_CODE        VARCHAR2(5),\n" +
            "    ROUTE                VARCHAR2(3),\n" +
            "    MANUF_SUBGROUP_ID    NUMBER,\n" +
            "    PRODUCT_ID           NUMBER,\n" +
            "    BRAND_GROUP_ID       NUMBER,\n" +
            "    MONTH                NUMBER,\n" +
            "    YEAR                 NUMBER,\n" +
            "    QTY                  NUMBER,\n" +
            "    TP_NAME              VARCHAR2(200),\n" +
            "    LANDMARK_NAME        VARCHAR2(200),\n" +
            "    LATITUDE             VARCHAR2(20),\n" +
            "    LONGITUDE            VARCHAR2(20),\n" +
            "    ADDRESS              VARCHAR2(1000),\n" +
            "    PATH_FILE            VARCHAR2(500),\n" +
            "    WEEK                 NUMBER,\n" +
            "    STATUS               VARCHAR2(1),\n" +
            "    FLAG_UPLOAD          VARCHAR2(1), \n" +
            "    DESCRIPTION          VARCHAR2(200),\n" +
            "    USER_CREATED         VARCHAR2(30),\n" +
            "    DATE_CREATED         DATE,\n" +
            "    USER_MODIFIED        VARCHAR2(30),\n" +
            "    DATE_MODIFIED        DATE\n" +
            ");";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_COMP_EVENT = "CREATE TABLE IF NOT EXISTS TR_COMP_EVENT (\n" +
            "    ID                INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    OU_CODE           VARCHAR2(10),\n" +
            "    TERRITORY_CODE    VARCHAR2(5),\n" +
            "    DISTRICT_CODE     VARCHAR2(5),\n" +
            "    ROUTE             VARCHAR2(3),\n" +
            "    MANUF_SUBGROUP_ID NUMBER,\n" +
            "    PRODUCT_ID        NUMBER,\n" +
            "    BRAND_GROUP_ID    NUMBER,\n" +
            "    EVENT_SCALE_ID    NUMBER,\n" +
            "    EVENT_NAME        VARCHAR2, \n" +
            "    START_DATE        DATE,\n" +
            "    END_DATE          DATE,\n" +
            "    ARTIST_LEVEL_ID   NUMBER,\n" +
            "    PROVINCE_ID       NUMBER,\n" +
            "    COUNTY_ID         NUMBER,\n" +
            "    SUBDISTRICT_ID    NUMBER,\n" +
            "    VILLAGE_ID        NUMBER,\n" +
            "    TP_HEADER_ID      NUMBER,\n" +
            "    TP_CATEGORY_ID    NUMBER,\n" +
            "    TP_NAME           VARCHAR2(200),\n" +
            "    TP_ADDRESS        VARCHAR2(500),\n" +
            "    LANDMARK          VARCHAR2(200),\n" +
            "    FLAG_TP_UNREGISTERED  VARCHAR2(1),\n" +
            "    LATITUDE          VARCHAR2(20),\n" +
            "    LONGITUDE         VARCHAR2(20),\n" +
            "    DESCRIPTION       VARCHAR2(200),\n" +
            "    WEEK              NUMBER,\n" +
            "    USER_CREATED      VARCHAR2(30),\n" +
            "    DATE_CREATED      DATE,\n" +
            "    USER_MODIFIED     VARCHAR2(30),\n" +
            "    DATE_MODIFIED     DATE,\n" +
            "    STATUS            VARCHAR2(1)\n" +
            ");";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_ACT_PROD_LAUNCHING = "CREATE TABLE IF NOT EXISTS TR_ACT_PROD_LAUNCHING (\n" +
            "    ID                  INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    OU_CODE             VARCHAR2(10),\n" +
            "    TERRITORY_CODE      VARCHAR2(5),\n" +
            "    DISTRICT_CODE       VARCHAR2(5),\n" +
            "    ROUTE               NUMBER,\n" +
            "    OUTLET_ID           VARCHAR2(15),\n" +
            "    MANUF_SUBGROUP_ID   NUMBER,\n" +
            "    MANUF_SUBGROUP_NAME VARCHAR2(50),\n" +
            "    PRODUCT_ID          NUMBER,\n" +
            "    PRODUCT_CODE        VARCHAR2(150),\n" +
            "    ENTRY_DATE          DATE,\n" +
            "    BUY_PRICE           NUMBER,\n" +
            "    SOURCE_BUY_ID       NUMBER,\n" +
            "    SELL_PRICE          NUMBER,\n" +
            "    WEEK                NUMBER,\n" +
            "    STATUS              VARCHAR2(1),\n" +
            "    USER_CREATED        VARCHAR2(30),\n" +
            "    DATE_CREATED        DATE,\n" +
            "    USER_MODIFIED       VARCHAR2(30),\n" +
            "    DATE_MODIFIED       DATE\n" +
            ");";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_NEW_STREET_VISIBILITY = "CREATE TABLE TR_NEW_STREET_VISIBILITY (\n" +
            "    ID                      INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    OU_CODE                 VARCHAR2(10),\n" +
            "    TERRITORY_CODE          VARCHAR2(5),\n" +
            "    DISTRICT_CODE           VARCHAR2(5),\n" +
            "    ROUTE                   VARCHAR2(3),\n" +
            "    MANUFACTURE_SUBGROUP_ID NUMBER,\n" +
            "    PRODUCT_ID              NUMBER,\n" +
            "    BRAND_GROUP_ID          NUMBER,\n" +
            "    STREET_VISIBILITY_TYPE_ID  NUMBER,\n" +
            "    QTY                     NUMBER,\n" +
            "    ADDRESS                 VARCHAR2(500),\n" +
            "    TP_HEADER_ID            NUMBER,\n" +
            "    TP_NAME                 VARCHAR2(200),\n" +
            "    TP_ADDRESS              VARCHAR2(500),\n" +
            "    LANDMARK                VARCHAR2(200),\n" +
            "    PATH_FILE               VARCHAR2(500),\n" +
            "    LATITUDE                VARCHAR2(20),\n" +
            "    LONGITUDE               VARCHAR2(20),\n" +
            "    DESCRIPTION             VARCHAR2(200),\n" +
            "    WEEK                    NUMBER,\n" +
            "    FLAG_TP_UNREGISTERED    VARCHAR2(1),\n" +
            "    STATUS                  VARCHAR2(1),\n" +
            "    USER_CREATED            VARCHAR2(30),\n" +
            "    DATE_CREATED            DATE,\n" +
            "    USER_MODIFIED           VARCHAR2(30),\n" +
            "    DATE_MODIFIED           DATE\n" +
            ");\n";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_COMP_EVENT_DTL = "CREATE TABLE IF NOT EXISTS TR_COMP_EVENT_DTL (\n" +
            "    ID               INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TR_COMP_EVENT_ID NUMBER NOT NULL,\n" +
            "    PUBLICATION_ID   NUMBER,\n" +
            "    STATUS           VARCHAR2(1),\n" +
            "    WEEK             NUMBER,\n" +
            "    USER_CREATED     VARCHAR2(30),\n" +
            "    DATE_CREATED     DATE,\n" +
            "    USER_MODIFIED    VARCHAR2(30),\n" +
            "    DATE_MODIFIED    DATE\n" +
            ");";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_COMP_EVENT_PHOTOS = "CREATE TABLE IF NOT EXISTS TR_COMP_EVENT_PHOTOS (\n" +
            "    ID               INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TR_COMP_EVENT_ID NUMBER NOT NULL,\n" +
            "    PATH_FILE        VARCHAR2(500),\n" +
            "    STATUS           VARCHAR2(1),\n" +
            "    USER_CREATED     VARCHAR2(30),\n" +
            "    DATE_CREATED     DATE,\n" +
            "    USER_MODIFIED    VARCHAR2(30),\n" +
            "    DATE_MODIFIED    DATE\n" +
            ");";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_TOTAL_SPACE_SHARE = "CREATE TABLE IF NOT EXISTS TR_TOTAL_SPACE_SHARE (\n" +
            "    ID             INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    OU_CODE        VARCHAR2(10),\n" +
            "    TERRITORY_CODE VARCHAR2(5),\n" +
            "    DISTRICT_CODE  VARCHAR2(5),\n" +
            "    ROUTE          NUMBER,\n" +
            "    OUTLET_ID      VARCHAR2(30),\n" +
            "    OUTLET_NAME    VARCHAR2(100),\n" +
            "    VALUE          NUMBER,\n" +
            "    WEEK           NUMBER,\n" +
            "    USER_CREATED   VARCHAR2(30),\n" +
            "    DATE_CREATED   DATE,\n" +
            "    USER_MODIFIED  VARCHAR2(30),\n" +
            "    DATE_MODIFIED  DATE\n" +
            ");";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_COMP_PROG_FEE = "CREATE TABLE IF NOT EXISTS TR_COMP_PROG_FEE (\n" +
            "    ID              INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TR_COMP_PROG_ID NUMBER NOT NULL,\n" +
            "    FEE             NUMBER,\n" +
            "    UNIT_ID         NUMBER,\n" +
            "    STATUS          VARCHAR2(1),\n" +
            "    WEEK            NUMBER,\n" +
            "    USER_CREATED    VARCHAR2(30),\n" +
            "    DATE_CREATED    DATE,\n" +
            "    USER_MODIFIED   VARCHAR2(30),\n" +
            "    DATE_MODIFIED   DATE\n" +
            ");";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_COMP_PROG_FEE_UNIT = "CREATE TABLE IF NOT EXISTS TR_COMP_PROG_FEE_UNIT (\n" +
            "    ID                    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TR_COMP_PROG_ID       NUMBER NOT NULL,\n" +
            "    ITEM_SUBCATEGORY_ID   NUMBER,\n" +
            "    ITEM_SUBCATEGORY_NAME VARCHAR2(100),\n" +
            "    UNIT_ID               NUMBER,\n" +
            "    QTY                   NUMBER,\n" +
            "    STATUS                VARCHAR2(1),\n" +
            "    WEEK                  NUMBER,\n" +
            "    USER_CREATED          VARCHAR2(30),\n" +
            "    DATE_CREATED          DATE,\n" +
            "    USER_MODIFIED         VARCHAR2(30),\n" +
            "    DATE_MODIFIED         DATE\n" +
            ");";

    /*
     * Add by bayus05 31-JUL-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_COMP_PROG_CIG = "CREATE TABLE IF NOT EXISTS TR_COMP_PROG_CIG (\n" +
            "    ID                    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TR_COMP_PROG_ID       NUMBER NOT NULL,\n" +
            "    MANUF_SUBGROUP_ID     NUMBER,\n" +
            "    MANUF_SUBGROUP_NAME   VARCHAR2(50),\n" +
            "    PRODUCT_ID            NUMBER,\n" +
            "    PRODUCT_CODE          VARCHAR2(150),\n" +
            "    QTY                   NUMBER,\n" +
            "    UNIT_ID               NUMBER,\n" +
            "    STATUS                VARCHAR2(1),\n" +
            "    WEEK                  NUMBER,\n" +
            "    USER_CREATED          VARCHAR2(30),\n" +
            "    DATE_CREATED          DATE,\n" +
            "    USER_MODIFIED         VARCHAR2(30),\n" +
            "    DATE_MODIFIED         DATE\n" +
            ");";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_COMP_PROG_IMPACT = "CREATE TABLE IF NOT EXISTS TR_COMP_PROG_IMPACT (\n" +
            "    ID              INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TR_COMP_PROG_ID NUMBER NOT NULL,\n" +
            "    IMPACT_ID       NUMBER,\n" +
            "    VALUE           NUMBER,\n" +
            "    IS_MAPPING      VARCHAR2(1),\n" +
            "    WEEK            NUMBER,\n" +
            "    STATUS          VARCHAR2(1),\n" +
            "    USER_CREATED    VARCHAR2(30),\n" +
            "    DATE_CREATED    DATE,\n" +
            "    USER_MODIFIED   VARCHAR2(30),\n" +
            "    DATE_MODIFIED   DATE\n" +
            ");";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_COMP_PROG = "CREATE TABLE IF NOT EXISTS TR_COMP_PROG (\n" +
            "    ID                  INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_COMP_PROG_ID     NUMBER,\n" +
            "    PILAR_ID            NUMBER,\n" +
            "    OUTLET_ID           VARCHAR2(15),\n" +
            "    OU_CODE             VARCHAR2(10),\n" +
            "    TERRITORY_CODE      VARCHAR2(5),\n" +
            "    DISTRICT_CODE       VARCHAR2(5),\n" +
            "    ROUTE               VARCHAR2(3),\n" +
            "    PRODUCT_ID          NUMBER,\n" +
            "    PRODUCT_CODE        VARCHAR2(150),\n" +
            "    MANUF_SUBGROUP_ID   NUMBER,\n" +
            "    MANUF_SUBGROUP_NAME VARCHAR2(50),\n" +
            "    CHANNEL_MKT_ID      NUMBER,\n" +
            "    CHANNEL_MKT_NAME    VARCHAR2(50),\n" +
            "    SUBPILAR_ID         NUMBER,\n" +
            "    SUBPILAR_NAME       VARCHAR2(50),\n" +
            "    PROGRAM_MAP_ID      NUMBER,\n" +
            "    PROGRAM_NAME        VARCHAR2(50),\n" +
            "    TEAM_EXECUTE_ID     NUMBER,\n" +
            "    START_DATE          DATE,\n" +
            "    END_DATE            DATE,\n" +
            "    FLAG_PERIOD_UNKNOWN     VARCHAR2(1),\n" +
            "    FLAG_ONE_SHOT_PERIOD  VARCHAR2(1),\n" +
            "    FLAG_PERIOD_EXPIRED  VARCHAR2(1),\n" +
            "    MECHANISM           VARCHAR2(500),\n" +
            "    FEE_OUTLET          NUMBER,\n" +
            "    DURATION            NUMBER, \n" +
            "    STATUS              VARCHAR2(1),\n" +
            "    WEEK                NUMBER,\n" +
            "    USER_CREATED        VARCHAR2(30),\n" +
            "    DATE_CREATED        DATE,\n" +
            "    USER_MODIFIED       VARCHAR2(30),\n" +
            "    DATE_MODIFIED       DATE,\n" +
            "    FLAG_UPLOAD         VARCHAR2(1),\n" +
            "    FLAG_NOT_COMPENSATION  VARCHAR2(1)\n" +
            ");";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_COMP_PROG_UPLINE = "CREATE TABLE IF NOT EXISTS TR_COMP_PROG_UPLINE (\n" +
            "    ID              INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TR_COMP_PROG_ID NUMBER NOT NULL,\n" +
            "    OUTLET_ID       VARCHAR2(15),\n" +
            "    OUTLET_NAME     VARCHAR2(150),\n" +
            "    STATUS          VARCHAR2(1),\n" +
            "    WEEK            NUMBER,\n" +
            "    USER_CREATED    VARCHAR2(30),\n" +
            "    DATE_CREATED    DATE,\n" +
            "    USER_MODIFIED   VARCHAR2(30),\n" +
            "    DATE_MODIFIED   DATE\n" +
            ");\n";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_COMP_PROG_VAO = "CREATE TABLE IF NOT EXISTS TR_COMP_PROG_VAO (\n" +
            "    ID                 INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TR_COMP_PROG_ID    NUMBER NOT NULL,\n" +
            "    QTY_PACK           NUMBER,\n" +
            "    STATUS             VARCHAR2(1),\n" +
            "    WEEK               NUMBER,\n" +
            "    USER_CREATED       VARCHAR2(30),\n" +
            "    DATE_CREATED       DATE,\n" +
            "    USER_MODIFIED      VARCHAR2(30),\n" +
            "    DATE_MODIFIED      DATE,\n" +
            "    VAO_ID             NUMBER,\n" +
            "    OTHER_VAO          VARCHAR2(50)\n" +
            ");\n";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_COMP_PROG_UNIT = "CREATE TABLE IF NOT EXISTS TR_COMP_PROG_UNIT (\n" +
            "    ID                      INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TR_COMP_PROG_ID         NUMBER NOT NULL,\n" +
            "    ALIAS_SUBCATEGORY_ID     NUMBER,\n" +
            "    ALIAS_SUBCATEGORY_NAME   VARCHAR2(100),\n" +
            "    FLAG_UNIT_UNREGISTERED  VARCHAR2(1),\n" +
            "    PATH_FILE               VARCHAR2(500),\n" +
            "    FLAG_PHOTO              VARCHAR2(1),\n" +
            "    QTY                     NUMBER,\n" +
            "    STATUS                  VARCHAR2(1),\n" +
            "    WEEK                    NUMBER,\n" +
            "    USER_CREATED            VARCHAR2(30),\n" +
            "    DATE_CREATED            DATE,\n" +
            "    USER_MODIFIED           VARCHAR2(30),\n" +
            "    DATE_MODIFIED           DATE\n" +
            ");\n";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_COMP_PROG_TIME = "CREATE TABLE IF NOT EXISTS TR_COMP_PROG_TIME (\n" +
            "    ID              INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TR_COMP_PROG_ID NUMBER NOT NULL,\n" +
            "    TIME_ID         NUMBER,\n" +
            "    STATUS          VARCHAR2(1),\n" +
            "    WEEK            NUMBER,\n" +
            "    USER_CREATED    VARCHAR2(30),\n" +
            "    DATE_CREATED    DATE,\n" +
            "    USER_MODIFIED   VARCHAR2(30),\n" +
            "    DATE_MODIFIED   DATE\n" +
            ");";
    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_MST_CONSUMER_MATRIX = "CREATE TABLE IF NOT EXISTS MST_CONSUMER_MATRIX (\n" +
            "    ID             INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    ACTIVITY_ID    NUMBER,\n" +
            "    LOCATION_ID    NUMBER,\n" +
            "    TP_CATEGORY_ID NUMBER,\n" +
            "    STATUS         VARCHAR2(1),\n" +
            "    USER_CREATED   VARCHAR2(100),\n" +
            "    DATE_CREATED   DATE\n" +
            ");";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_MST_PRODUCT_ALL = "CREATE TABLE IF NOT EXISTS MST_PRODUCT_ALL (\n" +
            "    ID           INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    PRODUCT_ID   NUMBER,\n" +
            "    PRODUCT_CODE VARCHAR2(100),\n" +
            "    PRODUCT_NAME VARCHAR2(100),\n" +
            "    MANUF_SUBGROUP_ID NUMBER,\n" +
            "    BRAND_GROUP_NAME VARCHAR2(100),\n" +
            "    STATUS       VARCHAR2(1),\n" +
            "    USER_CREATED VARCHAR2(30),\n" +
            "    DATE_CREATED DATE\n" +
            ");";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_MST_COUNTY = "CREATE TABLE IF NOT EXISTS MST_COUNTY (\n" +
            "    ID           INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    COUNTY_ID    NUMBER,\n" +
            "    COUNTY_NAME  VARCHAR2(100),\n" +
            "    STATUS       VARCHAR2(20),\n" +
            "    USER_CREATED VARCHAR2(20),\n" +
            "    DATE_CREATED DATE\n" +
            ");";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_MST_SUBDISTRICT = "CREATE TABLE IF NOT EXISTS MST_SUBDISTRICT (\n" +
            "    ID               INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    COUNTY_ID        NUMBER,\n" +
            "    SUBDISTRICT_ID   NUMBER,\n" +
            "    SUBDISTRICT_NAME VARCHAR2(100),\n" +
            "    STATUS           VARCHAR2(20),\n" +
            "    USER_CREATED     VARCHAR2(20),\n" +
            "    DATE_CREATED     DATE\n" +
            ");";

    /*
     * Add by bayus05 24-MAR-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_CONSUMER_CONTACT = "CREATE TABLE IF NOT EXISTS TR_CONSUMER_CONTACT (\n" +
            "    ID                    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    OU_CODE               VARCHAR2(10),\n" +
            "    TERRITORY_CODE        VARCHAR2(10),\n" +
            "    DISTRICT_CODE         VARCHAR2(10),\n" +
            "    ROUTE                 VARCHAR2(3),\n" +
            "    ACTIVITY_ID           NUMBER,\n" +
            "    LOCATION_TYPE_ID      NUMBER,\n" +
            "    TP_CATEGORY_ID        NUMBER,\n" +
            "    MST_TPBP_ID           NUMBER,\n" +
            "    OUTLET_ID             VARCHAR2(1000),\n" +
            "    OUTLET_COUNTY_ID      NUMBER,\n" +
            "    OUTLET_SUBDISTRICT_ID NUMBER,\n" +
            "    OTHERS_TP_NAME        VARCHAR2(100),\n" +
            "    OTHERS_TP_ADDRESS     VARCHAR2(500),\n" +
            "    OTHERS_LANDMARK       VARCHAR2(200),\n" +
            "    PHONE_NO              VARCHAR2(50),\n" +
            "    CONSUMER_NAME         VARCHAR2(100),\n" +
            "    KTP_NO                VARCHAR2(50),\n" +
            "    ADDRESS               VARCHAR2(500),\n" +
            "    COUNTY_ID             NUMBER,\n" +
            "    COUNTY_NAME           VARCHAR2(500), \n" +
            "    SUBDISTRICT_ID        NUMBER,\n" +
            "    SUBDISTRICT_NAME      VARCHAR2(500), \n" +
            "    VILLAGE_ID            NUMBER,\n" +
            "    VILLAGE_NAME          VARCHAR2(500), \n" +
            "    AGE                   NUMBER,\n" +
            "    OCCUPATION_ID         NUMBER,\n" +
            "    SKU_PRIMARY_ID        NUMBER,\n" +
            "    QTY_PRIMARY           NUMBER,\n" +
            "    SKU_SECONDARY_ID      NUMBER,\n" +
            "    QTY_SECONDARY         NUMBER,\n" +
            "    SES_PERCENT           NUMBER,\n" +
            "    LATITUDE              VARCHAR2(20),\n" +
            "    LONGITUDE             VARCHAR2(20),\n" +
            "    WEEK                  NUMBER,\n" +
            "    STATUS                VARCHAR2(1),\n" +
            "    USER_CREATED          VARCHAR2(30),\n" +
            "    DATE_CREATED          DATE,\n" +
            "    USER_MODIFIED         VARCHAR2(30),\n" +
            "    DATE_MODIFIED         DATE\n" +
            ");";

    /*
     * Add by bayus05 09-JUN-2023
     * CR NIRWANA
     * */

    private String CREATE_CS_COMP_PROG = "CREATE TABLE IF NOT EXISTS CS_COMP_PROG (\n" +
            "    ID                  INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_COMP_PROG_ID     NUMBER,\n" +
            "    PILAR_ID            NUMBER,\n" +
            "    OUTLET_ID           VARCHAR2(15),\n" +
            "    PRODUCT_ID          NUMBER,\n" +
            "    PRODUCT_CODE        VARCHAR2(150),\n" +
            "    MANUF_SUBGROUP_ID   NUMBER,\n" +
            "    MANUF_SUBGROUP_NAME VARCHAR2(50),\n" +
            "    CHANNEL_MKT_ID      NUMBER,\n" +
            "    CHANNEL_MKT_NAME    VARCHAR2(50),\n" +
            "    SUBPILAR_ID         NUMBER,\n" +
            "    SUBPILAR_NAME       VARCHAR2(50),\n" +
            "    PROGRAM_MAP_ID      NUMBER,\n" +
            "    PROGRAM_NAME        VARCHAR2(50),\n" +
            "    TEAM_EXECUTE_ID     NUMBER,\n" +
            "    START_DATE          DATE,\n" +
            "    END_DATE            DATE,\n" +
            "    FLAG_PERIOD_UNKNOWN     VARCHAR2(1),\n" +
            "    FLAG_ONE_SHOT_PERIOD  VARCHAR2(1),\n" +
            "    FLAG_PERIOD_EXPIRED  VARCHAR2(1),\n" +
            "    MECHANISM           VARCHAR2(500),\n" +
            "    FEE_OUTLET          NUMBER,\n" +
            "    DURATION            NUMBER, \n" +
            "    STATUS              VARCHAR2(1),\n" +
            "    WEEK                NUMBER,\n" +
            "    USER_CREATED        VARCHAR2(30),\n" +
            "    DATE_CREATED        DATE,\n" +
            "    USER_MODIFIED       VARCHAR2(30),\n" +
            "    DATE_MODIFIED       DATE,\n" +
            "    FLAG_UPLOAD         VARCHAR2(1),\n" +
            "    FLAG_NOT_COMPENSATION         VARCHAR2(1)\n" +
            ");";

    /*
     * Add by bayus05 09-JUN-2023
     * CR NIRWANA
     * */
    private String CREATE_CS_COMP_PROG_IMPACT = "CREATE TABLE IF NOT EXISTS CS_COMP_PROG_IMPACT (\n" +
            "    ID              INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_COMP_PROG_ID NUMBER NOT NULL,\n" +
            "    IMPACT_ID       NUMBER,\n" +
            "    VALUE           NUMBER,\n" +
            "    IS_MAPPING      VARCHAR2(1),\n" +
            "    WEEK            NUMBER,\n" +
            "    STATUS          VARCHAR2(1),\n" +
            "    USER_CREATED    VARCHAR2(30),\n" +
            "    DATE_CREATED    DATE,\n" +
            "    USER_MODIFIED   VARCHAR2(30),\n" +
            "    DATE_MODIFIED   DATE\n" +
            ");";

    /*
     * Add by bayus05 09-JUN-2023
     * CR NIRWANA
     * */
    private String CREATE_CS_COMP_PROG_UPLINE = "CREATE TABLE IF NOT EXISTS CS_COMP_PROG_UPLINE (\n" +
            "    ID              INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_COMP_PROG_ID NUMBER NOT NULL,\n" +
            "    OUTLET_ID       VARCHAR2(15),\n" +
            "    OUTLET_NAME     VARCHAR2(150),\n" +
            "    STATUS          VARCHAR2(1),\n" +
            "    WEEK            NUMBER,\n" +
            "    USER_CREATED    VARCHAR2(30),\n" +
            "    DATE_CREATED    DATE,\n" +
            "    USER_MODIFIED   VARCHAR2(30),\n" +
            "    DATE_MODIFIED   DATE\n" +
            ");\n";


    /*
     * Add by bayus05 09-JUN-2023
     * CR NIRWANA
     * */
    private String CREATE_CS_COMP_PROG_FEE = "CREATE TABLE IF NOT EXISTS CS_COMP_PROG_FEE (\n" +
            "    ID              INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_COMP_PROG_ID NUMBER NOT NULL,\n" +
            "    FEE             NUMBER,\n" +
            "    UNIT_ID         NUMBER,\n" +
            "    STATUS          VARCHAR2(1),\n" +
            "    WEEK            NUMBER,\n" +
            "    USER_CREATED    VARCHAR2(30),\n" +
            "    DATE_CREATED    DATE,\n" +
            "    USER_MODIFIED   VARCHAR2(30),\n" +
            "    DATE_MODIFIED   DATE\n" +
            ");";

    /*
     * Add by bayus05 09-JUN-2023
     * CR NIRWANA
     * */
    private String CREATE_CS_COMP_PROG_FEE_UNIT = "CREATE TABLE IF NOT EXISTS CS_COMP_PROG_FEE_UNIT (\n" +
            "    ID                    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_COMP_PROG_ID       NUMBER NOT NULL,\n" +
            "    ITEM_SUBCATEGORY_ID   NUMBER,\n" +
            "    ITEM_SUBCATEGORY_NAME VARCHAR2(100),\n" +
            "    UNIT_ID               NUMBER,\n" +
            "    QTY                   NUMBER,\n" +
            "    STATUS                VARCHAR2(1),\n" +
            "    WEEK                  NUMBER,\n" +
            "    USER_CREATED          VARCHAR2(30),\n" +
            "    DATE_CREATED          DATE,\n" +
            "    USER_MODIFIED         VARCHAR2(30),\n" +
            "    DATE_MODIFIED         DATE\n" +
            ");";

    /*
     * Add by bayus05 31-JUL-2023
     * CR NIRWANA
     * */
    private String CREATE_CS_COMP_PROG_CIG = "CREATE TABLE IF NOT EXISTS CS_COMP_PROG_CIG (\n" +
            "    ID                    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_COMP_PROG_ID       NUMBER NOT NULL,\n" +
            "    MANUF_SUBGROUP_ID     NUMBER,\n" +
            "    MANUF_SUBGROUP_NAME   VARCHAR2(50),\n" +
            "    PRODUCT_ID            NUMBER,\n" +
            "    PRODUCT_CODE          VARCHAR2(150),\n" +
            "    QTY                   NUMBER,\n" +
            "    UNIT_ID               NUMBER,\n" +
            "    STATUS                VARCHAR2(1),\n" +
            "    WEEK                  NUMBER,\n" +
            "    USER_CREATED          VARCHAR2(30),\n" +
            "    DATE_CREATED          DATE,\n" +
            "    USER_MODIFIED         VARCHAR2(30),\n" +
            "    DATE_MODIFIED         DATE\n" +
            ");";


    /*
     * Add by bayus05 09-JUN-2023
     * CR NIRWANA
     * */
    private String CREATE_CS_COMP_PROG_VAO = "CREATE TABLE IF NOT EXISTS CS_COMP_PROG_VAO (\n" +
            "    ID                 INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_COMP_PROG_ID    NUMBER NOT NULL,\n" +
            "    QTY_PACK           NUMBER,\n" +
            "    STATUS             VARCHAR2(1),\n" +
            "    WEEK               NUMBER,\n" +
            "    USER_CREATED       VARCHAR2(30),\n" +
            "    DATE_CREATED       DATE,\n" +
            "    USER_MODIFIED      VARCHAR2(30),\n" +
            "    DATE_MODIFIED      DATE,\n" +
            "    VAO_ID             NUMBER,\n" +
            "    OTHER_VAO          VARCHAR2(50)\n" +
            ");\n";

    /*
     * Add by bayus05 09-JUN-2023
     * CR NIRWANA
     * */
    private String CREATE_CS_COMP_PROG_UNIT = "CREATE TABLE IF NOT EXISTS CS_COMP_PROG_UNIT (\n" +
            "    ID                      INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_COMP_PROG_ID         NUMBER NOT NULL,\n" +
            "    ALIAS_SUBCATEGORY_ID     NUMBER,\n" +
            "    ALIAS_SUBCATEGORY_NAME   VARCHAR2(100),\n" +
            "    FLAG_UNIT_UNREGISTERED  VARCHAR2(1),\n" +
            "    PATH_FILE               VARCHAR2(500),\n" +
            "    QTY                     NUMBER,\n" +
            "    STATUS                  VARCHAR2(1),\n" +
            "    WEEK                    NUMBER,\n" +
            "    USER_CREATED            VARCHAR2(30),\n" +
            "    DATE_CREATED            DATE,\n" +
            "    USER_MODIFIED           VARCHAR2(30),\n" +
            "    DATE_MODIFIED           DATE,\n" +
            "    FLAG_PHOTO              VARCHAR2(1)\n" +
            ");\n";

    /*
     * Add by bayus05 09-JUN-2023
     * CR NIRWANA
     * */
    private String CREATE_CS_COMP_PROG_TIME = "CREATE TABLE IF NOT EXISTS CS_COMP_PROG_TIME (\n" +
            "    ID              INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_COMP_PROG_ID NUMBER NOT NULL,\n" +
            "    TIME_ID         NUMBER,\n" +
            "    STATUS          VARCHAR2(1),\n" +
            "    WEEK            NUMBER,\n" +
            "    USER_CREATED    VARCHAR2(30),\n" +
            "    DATE_CREATED    DATE,\n" +
            "    USER_MODIFIED   VARCHAR2(30),\n" +
            "    DATE_MODIFIED   DATE\n" +
            ");";

    /*
     * Add by bayus05 09-JUN-2023
     * CR NIRWANA
     * */
    private String CREATE_MST_ITEM_SUBCATEGORY_ALIAS = "CREATE TABLE IF NOT EXISTS MST_ITEM_SUBCATEGORY_ALIAS (\n" +
            "    ID              INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    ALIAS_SUBCATEGORY_ID NUMBER NOT NULL,\n" +
            "    ALIAS_SUBCATEGORY_NAME VARCHAR2(50) NOT NULL,\n" +
            "    STATUS          VARCHAR2(1),\n" +
            "    USER_CREATED    VARCHAR2(30),\n" +
            "    DATE_CREATED    DATE,\n" +
            "    USER_MODIFIED   VARCHAR2(30),\n" +
            "    DATE_MODIFIED   DATE\n" +
            ");";

    /*
     * Add by bayus05 09-JUN-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_COMP_PROG_IMPACT_TEMP = "CREATE TABLE IF NOT EXISTS TR_COMP_PROG_IMPACT_TEMP (\n" +
            "    ID              INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TR_COMP_PROG_ID NUMBER NOT NULL,\n" +
            "    IMPACT_ID       NUMBER,\n" +
            "    VALUE           NUMBER,\n" +
            "    IS_MAPPING      VARCHAR2(1),\n" +
            "    WEEK            NUMBER,\n" +
            "    STATUS          VARCHAR2(1),\n" +
            "    USER_CREATED    VARCHAR2(30),\n" +
            "    DATE_CREATED    DATE,\n" +
            "    USER_MODIFIED   VARCHAR2(30),\n" +
            "    DATE_MODIFIED   DATE\n" +
            ");";

    /*
     * Add by bayus05 09-JUN-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_COMP_PROG_TEMP = "CREATE TABLE IF NOT EXISTS TR_COMP_PROG_TEMP (\n" +
            "    ID                  INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TR_COMP_PROG_ID     NUMBER,\n" +
            "    CS_COMP_PROG_ID     NUMBER,\n" +
            "    PILAR_ID            NUMBER,\n" +
            "    OUTLET_ID           VARCHAR2(15),\n" +
            "    OU_CODE             VARCHAR2(10),\n" +
            "    TERRITORY_CODE      VARCHAR2(5),\n" +
            "    DISTRICT_CODE       VARCHAR2(5),\n" +
            "    ROUTE               VARCHAR2(3),\n" +
            "    PRODUCT_ID          NUMBER,\n" +
            "    PRODUCT_CODE        VARCHAR2(150),\n" +
            "    MANUF_SUBGROUP_ID   NUMBER,\n" +
            "    MANUF_SUBGROUP_NAME VARCHAR2(50),\n" +
            "    CHANNEL_MKT_ID      NUMBER,\n" +
            "    CHANNEL_MKT_NAME    VARCHAR2(50),\n" +
            "    SUBPILAR_ID         NUMBER,\n" +
            "    SUBPILAR_NAME       VARCHAR2(50),\n" +
            "    PROGRAM_MAP_ID      NUMBER,\n" +
            "    PROGRAM_NAME        VARCHAR2(50),\n" +
            "    TEAM_EXECUTE_ID     NUMBER,\n" +
            "    START_DATE          DATE,\n" +
            "    END_DATE            DATE,\n" +
            "    FLAG_PERIOD_UNKNOWN     VARCHAR2(1),\n" +
            "    FLAG_ONE_SHOT_PERIOD  VARCHAR2(1),\n" +
            "    FLAG_PERIOD_EXPIRED  VARCHAR2(1),\n" +
            "    FLAGGING_TABLE        VARCHAR2(20),\n" +
            "    MECHANISM           VARCHAR2(500),\n" +
            "    FEE_OUTLET          NUMBER,\n" +
            "    DURATION            NUMBER, \n" +
            "    STATUS              VARCHAR2(1),\n" +
            "    WEEK                NUMBER,\n" +
            "    USER_CREATED        VARCHAR2(30),\n" +
            "    DATE_CREATED        DATE,\n" +
            "    USER_MODIFIED       VARCHAR2(30),\n" +
            "    DATE_MODIFIED       DATE,\n" +
            "    FLAG_UPLOAD         VARCHAR2(1),\n" +
            "    FLAG_NOT_COMPENSATION VARCHAR2(1)\n" +
            ");";


    /*
     * Add by bayus05 09-JUN-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_COMP_PROG_UPLINE_TEMP = "CREATE TABLE IF NOT EXISTS TR_COMP_PROG_UPLINE_TEMP (\n" +
            "    ID              INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TR_COMP_PROG_ID NUMBER NOT NULL,\n" +
            "    OUTLET_ID       VARCHAR2(15),\n" +
            "    OUTLET_NAME     VARCHAR2(150),\n" +
            "    STATUS          VARCHAR2(1),\n" +
            "    WEEK            NUMBER,\n" +
            "    USER_CREATED    VARCHAR2(30),\n" +
            "    DATE_CREATED    DATE,\n" +
            "    USER_MODIFIED   VARCHAR2(30),\n" +
            "    DATE_MODIFIED   DATE\n" +
            ");\n";


    /*
     * Add by bayus05 09-JUN-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_COMP_PROG_FEE_TEMP = "CREATE TABLE IF NOT EXISTS TR_COMP_PROG_FEE_TEMP (\n" +
            "    ID              INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TR_COMP_PROG_ID NUMBER NOT NULL,\n" +
            "    FEE             NUMBER,\n" +
            "    UNIT_ID         NUMBER,\n" +
            "    STATUS          VARCHAR2(1),\n" +
            "    WEEK            NUMBER,\n" +
            "    USER_CREATED    VARCHAR2(30),\n" +
            "    DATE_CREATED    DATE,\n" +
            "    USER_MODIFIED   VARCHAR2(30),\n" +
            "    DATE_MODIFIED   DATE\n" +
            ");";

    /*
     * Add by bayus05 09-JUN-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_COMP_PROG_FEE_UNIT_TEMP = "CREATE TABLE IF NOT EXISTS TR_COMP_PROG_FEE_UNIT_TEMP (\n" +
            "    ID                    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TR_COMP_PROG_ID       NUMBER NOT NULL,\n" +
            "    ITEM_SUBCATEGORY_ID   NUMBER,\n" +
            "    ITEM_SUBCATEGORY_NAME VARCHAR2(100),\n" +
            "    UNIT_ID               NUMBER,\n" +
            "    QTY                   NUMBER,\n" +
            "    STATUS                VARCHAR2(1),\n" +
            "    WEEK                  NUMBER,\n" +
            "    USER_CREATED          VARCHAR2(30),\n" +
            "    DATE_CREATED          DATE,\n" +
            "    USER_MODIFIED         VARCHAR2(30),\n" +
            "    DATE_MODIFIED         DATE\n" +
            ");";


    /*
     * Add by bayus05 31-JUL-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_COMP_PROG_CIG_TEMP = "CREATE TABLE IF NOT EXISTS TR_COMP_PROG_CIG_TEMP (\n" +
            "    ID                    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TR_COMP_PROG_ID       NUMBER NOT NULL,\n" +
            "    MANUF_SUBGROUP_ID     NUMBER,\n" +
            "    MANUF_SUBGROUP_NAME   VARCHAR2(50),\n" +
            "    PRODUCT_ID            NUMBER,\n" +
            "    PRODUCT_CODE          VARCHAR2(150),\n" +
            "    QTY                   NUMBER,\n" +
            "    UNIT_ID               NUMBER,\n" +
            "    STATUS                VARCHAR2(1),\n" +
            "    WEEK                  NUMBER,\n" +
            "    USER_CREATED          VARCHAR2(30),\n" +
            "    DATE_CREATED          DATE,\n" +
            "    USER_MODIFIED         VARCHAR2(30),\n" +
            "    DATE_MODIFIED         DATE\n" +
            ");";

    /*
     * Add by bayus05 09-JUN-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_COMP_PROG_VAO_TEMP = "CREATE TABLE IF NOT EXISTS TR_COMP_PROG_VAO_TEMP (\n" +
            "    ID                 INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TR_COMP_PROG_ID    NUMBER NOT NULL,\n" +
            "    QTY_PACK           NUMBER,\n" +
            "    STATUS             VARCHAR2(1),\n" +
            "    WEEK               NUMBER,\n" +
            "    USER_CREATED       VARCHAR2(30),\n" +
            "    DATE_CREATED       DATE,\n" +
            "    USER_MODIFIED      VARCHAR2(30),\n" +
            "    DATE_MODIFIED      DATE,\n" +
            "    VAO_ID             NUMBER,\n" +
            "    OTHER_VAO          VARCHAR2(50)\n" +
            ");\n";

    /*
     * Add by bayus05 09-JUN-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_COMP_PROG_UNIT_TEMP = "CREATE TABLE IF NOT EXISTS TR_COMP_PROG_UNIT_TEMP (\n" +
            "    ID                      INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TR_COMP_PROG_ID         NUMBER NOT NULL,\n" +
            "    ALIAS_SUBCATEGORY_ID     NUMBER,\n" +
            "    ALIAS_SUBCATEGORY_NAME   VARCHAR2(100),\n" +
            "    FLAG_UNIT_UNREGISTERED  VARCHAR2(1),\n" +
            "    PATH_FILE               VARCHAR2(500),\n" +
            "    FLAG_PHOTO              VARCHAR2(1),\n" +
            "    QTY                     NUMBER,\n" +
            "    STATUS                  VARCHAR2(1),\n" +
            "    WEEK                    NUMBER,\n" +
            "    USER_CREATED            VARCHAR2(30),\n" +
            "    DATE_CREATED            DATE,\n" +
            "    USER_MODIFIED           VARCHAR2(30),\n" +
            "    DATE_MODIFIED           DATE\n" +
            ");\n";

    /*
     * Add by bayus05 09-JUN-2023
     * CR NIRWANA
     * */
    private String CREATE_TR_COMP_PROG_TIME_TEMP = "CREATE TABLE IF NOT EXISTS TR_COMP_PROG_TIME_TEMP (\n" +
            "    ID              INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TR_COMP_PROG_ID NUMBER NOT NULL,\n" +
            "    TIME_ID         NUMBER,\n" +
            "    STATUS          VARCHAR2(1),\n" +
            "    WEEK            NUMBER,\n" +
            "    USER_CREATED    VARCHAR2(30),\n" +
            "    DATE_CREATED    DATE,\n" +
            "    USER_MODIFIED   VARCHAR2(30),\n" +
            "    DATE_MODIFIED   DATE\n" +
            ");";

    /*
     * Add by bayus05 07-JUL-2023
     * CR NIRWANA
     * */
    private String CREATE_MST_ATTACHMENT_MAPPING = "CREATE TABLE IF NOT EXISTS MST_ATTACHMENT_MAPPING (\n" +
            "    ID              INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    MST_ATTACHMENT_MAPPING_ID NUMBER NOT NULL,\n" +
            "    KEY_ATTACHMENT  VARCHAR2(100),\n" +
            "    HOST_SERVER     VARCHAR2(100),\n" +
            "    HOST_ROOT       VARCHAR2(100),\n" +
            "    KEY_TYPE        VARCHAR2(24),\n" +
            "    REMARKS         VARCHAR2(512),\n" +
            "    STATUS          VARCHAR2(1),\n" +
            "    USER_CREATED    VARCHAR2(30),\n" +
            "    DATE_CREATED    DATE,\n" +
            "    USER_MODIFIED   VARCHAR2(30),\n" +
            "    DATE_MODIFIED   DATE\n" +
            ");";

    /**
     * @author noviantyn
     * 27 November 2023
     * CR KPI Retail
     * <p>
     * modified by noviantyn
     * 10 Mei 2024
     * CR KPI Retail Phase 2
     * [SIT1] - Phase 2 - Rute yang sudah dilakukan delegasi tidak ditampilkan pada dashboard salesman aslinya - #52091
     * Penambahan IS_KUNJUNGAN
     * <p>
     * modified by noviantyn
     * 14 Mei 2024
     * CR KPI Retail Phase 2
     * Perubahan IS_KUNJUNGAN menjadi FM_VISIT_TYPE
     */
    private String CREATE_TR_FORCE_MAJEURE = "CREATE TABLE IF NOT EXISTS TR_FORCE_MAJEURE (\n" +
            "    ID                         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    DISTRICT_ID                VARCHAR2(30),\n" +
            "    ROUTE                      NUMBER,\n" +
            "    WEEK                       NUMBER,\n" +
            "    SALES_FLAG_REASON          VARCHAR2(500),\n" +
            "    FILE_NAME                  VARCHAR2(4000),\n" +
            "    FM_REASON_ID               NUMBER,\n" +
            "    FM_VISIT_TYPE              NUMBER" +
            ");\n";

    private String CREATE_TR_PHOTO_POSM = "CREATE TABLE IF NOT EXISTS TR_PHOTO_POSM (\n" +
            "    ID                         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    DISTRICT_ID                VARCHAR2(30),\n" +
            "    ROUTE                      NUMBER,\n" +
            "    OUTLET_ID                  VARCHAR2(70),\n" +
            "    WEEK                       NUMBER,\n" +
            "    FILE_NAME                  VARCHAR2(4000)\n" +
            ");\n";

    private String CREATE_TR_PHOTO_VISDOM = "CREATE TABLE IF NOT EXISTS TR_PHOTO_VISDOM (\n" +
            "    ID                         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    DISTRICT_ID                VARCHAR2(30),\n" +
            "    ROUTE                      NUMBER,\n" +
            "    OUTLET_ID                  VARCHAR2(70),\n" +
            "    WEEK                       NUMBER,\n" +
            "    VISDOM_TYPE                NUMBER,\n" +
            "    FILE_NAME                  VARCHAR2(4000)\n" +
            ");\n";

    private String CREATE_MST_OUTLET_UP_INFO = "CREATE TABLE IF NOT EXISTS MST_OUTLET_UP_INFO (\n" +
            "    ID                         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    DISTRICT_ID                VARCHAR2(30),\n" +
            "    ROUTE                      NUMBER,\n" +
            "    OUTLET_ID                  VARCHAR2(70),\n" +
            "    MP_TERTUA                  VARCHAR2(10),\n" +
            "    MP_TERTUA_DATE             NUMBER,\n" +
            "    FRESHNESS_ID               NUMBER,\n" +
            "    OUTLET_STATUS_REASON       VARCHAR2(500),\n" +
            "    FILE_NAME                  VARCHAR2(4000),\n" +
            "    JENIS_OUTLET_ID            NUMBER\n" +
            ");\n";

    private String CREATE_MST_MAPPING_NON_MDTRY = "CREATE TABLE IF NOT EXISTS MST_MAPPING_NON_MDTRY (\n" +
            "    ID                         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    PROGRAM_ID                 NUMBER\n" +
            ");\n";

    private String CREATE_CS_ACT_COMP_CHECK_OUTDOOR = "CREATE TABLE IF NOT EXISTS CS_ACT_COMP_CHECK_OUTDOOR (\n" +
            "ID                     INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "OUTDOOR_ID             INTEGER,\n" +
            "STATUS_VISIT_ID        INTEGER,\n" +
            "TYPE_OUTDOOR_ID        INTEGER,\n" +
            "MANUF_SUBGROUP_ID      INTEGER,\n" +
            "BRAND_ID               INTEGER,\n" +
            "MONTH                  INTEGER,\n" +
            "YEAR                   INTEGER,\n" +
            "LATITUDE               VARCHAR,\n" +
            "LONGITUDE              VARCHAR,\n" +
            "PATH_FILE              VARCHAR,\n" +
            "NEW_MANUF_SUBGROUP_ID  INTEGER,\n" +
            "NEW_BRAND_ID           INTEGER,\n" +
            "WEEK                   INTEGER,\n" +
            "STATUS                 VARCHAR,\n" +
            "USER_CREATED           VARCHAR,\n" +
            "DATE_CREATED           LONG,\n" +
            "CS_ACT_COMP_HEADER_ID  INTEGER,\n" +
            "VISUAL_INFORMATION     VARCHAR,\n" +
            "DATA_SOURCES           INTEGER,\n" +
            "OUTDOOR_THEME_ID       INTEGER,\n" +
            "TRANS_DATE             LONG\n" +
            ");";
    /**
     * @author dimass02
     * 1 Feb 2024
     * AR Bppr Stockiest
     */
    // TR_BPPR_STOCKIEST
    private String CREATE_TR_BPPR_STOCKIEST = "CREATE TABLE `TR_BPPR_STOCKIEST` (" +
            "`ID` NUMBER PRIMARY KEY, " +
            "`BPPR_NO` VARCHAR, " +
            "`BPPR_TYPE` VARCHAR, " +
            "`BPPR_DATE` NUMBER, " +
            "`IS_ACTIVE` VARCHAR, " +
            "`DATE_CREATED` NUMBER, " +
            "`LAST_NP` VARCHAR, " +
            "`BPPR_VERSION` INTEGER, " +
            "`BPPR_STATUS_ID` INTEGER, " +
            "`LAST_UPLOAD` INTEGER, " +
            "`BPPR_CLOSED` VARCHAR" + ")";

    private String CREATE_TR_TTO_RECEIVER_BANK = "CREATE TABLE IF NOT EXISTS TR_TTO_RECEIVER_BANK (\n" +
            "   ID                          INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "   TR_TTO_RECEIVER_ID           NUMBER,\n" +
            "   MST_TTO_RECEIVER_ID         NUMBER,\n" +
            "   BANK_ID                     NUMBER,\n" +
            "   BANK_NAME                   VARCHAR2(100),\n" +
            "   ACCOUNT_NAME                VARCHAR2,\n" +
            "   BANK_ACCOUNT_NUMBER         VARCHAR2(50),\n" +
            "   NAME_IDENTICAL_TO_KTP       VARCHAR2(1),\n" +
            "   MST_OUTLET_BANK_ID          NUMBER,\n" +
            "   STATUS                      VARCHAR2,\n" +
            "   USER_CREATED                VARCHAR2,\n" +
            "   DATE_CREATED                NUMBER,\n" +
            "   USER_MODIFIED               VARCHAR2(50),\n" +
            "   DATE_MODIFIED               NUMBER,\n" +
            "   MST_TTO_RECEIVER_BANK_ID    NUMBER\n" +
            ");\n";


    /*
     * Add by bayus05 06-FEB-2024
     * CR LUAR CYCLE
     * */
    private String CREATE_MST_CALLCYCLE = "CREATE TABLE IF NOT EXISTS MST_CALLCYCLE (\n" +
            "    ID                  INTEGER,\n" +
            "    WEEK                NUMBER,\n" +
            "    CALL_CYCLE          VARCHAR2(10),\n" +
            "    DATE_CREATED        DATE\n" +
            ");\n";

    private String CREATE_TR_VISIT_TYPE = "CREATE TABLE TR_VISIT_TYPE (\n" +
            "    ID             INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TERRITORY_CODE VARCHAR2(5),\n" +
            "    DISTRICT_CODE  VARCHAR2(5),\n" +
            "    ROUTE_CODE     VARCHAR2(2),\n" +
            "    OUTLET_ID      VARCHAR2(70),\n" +
            "    VISIT_TYPE_ID  NUMBER,\n" +
            "    STATUS         VARCHAR2(1),\n" +
            "    WEEK           NUMBER,\n" +
            "    DATE_CREATED   DATE,\n" +
            "    USER_CREATED   VARCHAR2(30)\n" +
            ");\n";

    private String CREATE_TR_STOCK_ROKOK_STOCKIEST = "CREATE TABLE `TR_STOCK_ROKOK_STOCKIEST` (" +
            "`ID` NUMBER PRIMARY KEY, " +
            "`TR_BPPR_ID` NUMBER, " +
            "`BPPR_NO` VARCHAR, " +
            "`PRODUCT_ID` INTEGER, " +
            "`PRODUCT_CODE` VARCHAR, " +
            "`PRODUCT_NAME` VARCHAR, " +
            "`PRODUCT_SEQ` NUMBER, " +
            "`TOT_STOCK_AWAL` NUMBER, " +
            "`TOT_STOCK_GOOD` NUMBER, " +
            "`TOT_STOCK_BAD` NUMBER, " +
            "`TOT_STOCK_USED` NUMBER, " +
            "`STATUS` VARCHAR, " +
            "`DATE_CREATED` INTEGER, " +
            "`STOCK_INIT_DUS` INTEGER, " +
            "`STOCK_INIT_BAL` INTEGER, " +
            "`STOCK_INIT_SLF` INTEGER, " +
            "`STOCK_INIT_BKS` INTEGER, " +
            "`STOCK_FINAL_GOOD_DUS` INTEGER, " +
            "`STOCK_FINAL_GOOD_BAL` INTEGER, " +
            "`STOCK_FINAL_GOOD_SLF` INTEGER, " +
            "`STOCK_FINAL_GOOD_BKS` INTEGER, " +
            "`TOT_STOCK_USED_ORI` NUMBER " + ")";

    private String CREATE_MST_PP_PROGRAM_DTL = "CREATE TABLE IF NOT EXISTS `MST_PP_PROGRAM_DTL` (\n" +
            "    ID                  INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    PP_NUMBER           INTEGER,\n" +
            "    BUDGET_CATEGORY_ID  INTEGER,\n" +
            "    ITEM_NAME           VARCHAR,\n" +
            "    PP_PRICE_NETT       INTEGER,\n" +
            "    BUDGET_REQ_REF      NUMBER, \n" +
            "    IS_OVERBUDGET       VARCHAR" +
            ");";
    private String CREATE_MST_VIRTUAL_ACCOUNT = "CREATE TABLE `MST_VIRTUAL_ACCOUNT` (" +
            "`ID` NUMBER PRIMARY KEY, " +
            "`OU_CODE` VARCHAR2(10), " +
            "`TERRITORY_CODE` VARCHAR2(5), " +
            "`DISTRICT_CODE` VARCHAR2(5), " +
            "`ROUTE` VARCHAR2(2), " +
            "`BANK_PARTNER_ID` NUMBER, " +
            "`CORPORATE_CODE` VARCHAR2(20), " +
            "`CORPORATE_NAME` VARCHAR2(200), " +
            "`CUSTOMER_NO` VARCHAR2(20), " +
            "`VIRTUAL_ACCOUNT_NO` VARCHAR(20), " +
            "`DATE_CREATED` DATE, " +
            "`USER_CREATED` VARCHAR2(20) " + ")";


    private String CREATE_MST_PAYMENT_MAP = "CREATE TABLE `MST_PAYMENT_MAP` (" +
            "`ID` NUMBER PRIMARY KEY, " +
            "`MST_PAYMENT_MAP_ID` NUMBER, " +
            "`SOURCE` VARCHAR2(20), " +
            "`PAYMENT_TERM_ID` VARCHAR2(50), " +
            "`PAYMENT_TYPE_ID` VARCHAR2(50), " +
            "`BANK_PARTNER_ID` NUMBER, " +
            "`IS_TOP` VARCHAR2(1), " +
            "`TEMPLATE` VARCHAR2(50), " +
            "`PATHFILE_ICON` VARCHAR2(500), " +
            "`MAX_PAYMENT` NUMBER, " +
            "`NOMINAL_MIN` NUMBER, " +
            "`NOMINAL_MAX` NUMBER, " +
            "`SORT_ORDER` NUMBER, " +
            "`DATE_CREATED` DATE, " +
            "`USER_CREATED` VARCHAR2(20) " + ")";

    private String CREATE_TR_SALES_PAYMENT_LOG = "CREATE TABLE `TR_SALES_PAYMENT_LOG` (" +
            "`ID` NUMBER PRIMARY KEY, " +
            "`UPLOAD_ID` VARCHAR, " +
            "`DISTRICT_ID` VARCHAR, " +
            "`ROUTE` VARCHAR, " +
            "`OUTLET_ID` VARCHAR, " +
            "`OUTLET_NAME` VARCHAR, " +
            "`PAYMENT_TYPE` VARCHAR, " +
            "`PAYMENT` NUMBER, " +
            "`FLAG_SALES_ADD` NUMBER, " +
            "`NOTA_CODE` VARCHAR, " +
            "`TRX_ID` VARCHAR, " +
            "`VIRTUAL_ACCOUNT_NO` VARCHAR, " +
            "`PAYMENT_STATUS_ID` NUMBER, " +
            "`PAYMENT_LIMIT` DATE, " +
            "`BANK_PARTNER_ID` NUMBER, " +
            "`DATE_CREATED` DATE, " +
            "`USER_CREATED` VARCHAR, " +
            "`DATE_DELETED` DATE, " +
            "`USER_DELETED` VARCHAR, " +
            "`DATE_UPLOAD` DATE)";

    /**
     * @author noviantyn
     * 31 Januari 2024
     * CR KPI Retail Phase 2
     * <p>
     * Modified By MUTIAA
     * 11 November 2024
     * CR KPI Retail V1 [Initial Phase 2]
     * Added Confidence Level
     */
    private String CREATE_KPI_MST_PARAMETER = "CREATE TABLE IF NOT EXISTS KPI_MST_PARAMETER (\n" +
            "    ID                         INTEGER PRIMARY KEY,\n" +
            "    PARAMETER_NAME             VARCHAR2(100),\n" +
            "    OUTPUT_NAME                VARCHAR2(100),\n" +
            "    IS_ALL_PRODUCT             VARCHAR2(2),\n" +
            "    IS_TARGET_QUARTAL          VARCHAR2(2),\n" +
            "    IS_TOTAL_GG                VARCHAR2(2),\n" +
            "    TARGET_TYPE_ID             NUMBER,\n" +
            "    ACHIEVEMENT_TYPE_ID        NUMBER,\n" +
            "    LIMIT_ACHIEVEMENT          NUMBER,\n" +
            "    STATUS                     VARCHAR2(1),\n" +
            "    CONFIDENCE_LEVEL           VARCHAR2(1)\n" +
            ");\n";


    private String CREATE_KPI_MST_JENISOTL_PARAM = "CREATE TABLE IF NOT EXISTS KPI_MST_JENISOTL_PARAM (\n" +
            "    ID                         INTEGER PRIMARY KEY,\n" +
            "    PARAMETER_ID               NUMBER,\n" +
            "    JENIS_OUTLET_ID            NUMBER,\n" +
            "    WEEK_EFFECTIVE             NUMBER,\n" +
            "    STATUS                     VARCHAR2(1)\n" +
            ");\n";

    private String CREATE_TR_ACHIEVEMENT_HDR = "CREATE TABLE IF NOT EXISTS TR_ACHIEVEMENT_HDR (\n" +
            "    ID                         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    WEEK                       NUMBER,\n" +
            "    AREA                       VARCHAR(10),\n" +
            "    DISTRICT_ID                VARCHAR2(30),\n" +
            "    DISTRICT_CODE              VARCHAR2(5),\n" +
            "    ROUTE                      VARCHAR2(2),\n" +
            "    PARAMETER_ID               VARCHAR2(20),\n" +
            "    PARAMETER_NAME             VARCHAR2(50),\n" +
            "    DFMS_DISTRICT_ID           NUMBER,\n" +
            "    BOBOT_PARAMETER            NUMBER,\n" +
            "    LIMIT_ACHIEVEMENT          NUMBER,\n" +
            "    ACHIEVEMENT_PARAMETER      NUMBER,\n" +
            "    ACHIEVEMENT_TOTAL          NUMBER\n" +
            ");\n";

    /**
     * Modified By MUTIAA
     * 11 November 2024
     * CR KPI Retail V1 [Initial Phase 2]
     * Added Confidence Level
     */
    private String CREATE_TR_ACHIEVEMENT_DTL = "CREATE TABLE IF NOT EXISTS TR_ACHIEVEMENT_DTL (\n" +
            "    ID                         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    WEEK                       NUMBER,\n" +
            "    AREA                       VARCHAR2(10),\n" +
            "    DISTRICT_ID                VARCHAR2(30),\n" +
            "    ROUTE                      VARCHAR2(2),\n" +
            "    PARAMETER_ID               VARCHAR2(20),\n" +
            "    PRODUCT_ID                 VARCHAR2(10),\n" +
            "    PRODUCT_CODE               VARCHAR2(150),\n" +
            "    TARGET                     NUMBER,\n" +
            "    BOBOT_PRODUCT              NUMBER,\n" +
            "    TARGET_AKHIR               NUMBER,\n" +
            "    ACTUAL                     NUMBER,\n" +
            "    ACTUAL_AKHIR               NUMBER,\n" +
            "    ACHIEVEMENT_PRODUCT        NUMBER,\n" +
            "    CONFIDENCE_LEVEL           NUMBER\n" +
            ");\n";

    private String CREATE_TR_ACHIEVEMENT_DTL_OTL = "CREATE TABLE IF NOT EXISTS TR_ACHIEVEMENT_DTL_OTL (\n" +
            "    ID                         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    WEEK                       NUMBER,\n" +
            "    AREA                       VARCHAR2(10),\n" +
            "    DISTRICT_ID                VARCHAR2(30),\n" +
            "    ROUTE                      VARCHAR2(2),\n" +
            "    PARAMETER_ID               VARCHAR2(20),\n" +
            "    OUTLET_ID                  VARCHAR2(30),\n" +
            "    JENIS_OUTLET_ID            NUMBER,\n" +
            "    UNIT_ID                    VARCHAR2(10),\n" +
            "    UNIT_CODE                  VARCHAR2(1000),\n" +
            "    UNIT_TYPE                  VARCHAR2(30),\n" +
            "    ACHIEVEMENT                NUMBER,\n" +
            "    TARGET                     NUMBER,\n" +
            "    ACTUAL                     NUMBER,\n" +
            "    CALL_CYCLE                 VARCHAR2(10),\n" +
            "    IS_CALC                    VARCHAR2(2)\n" +
            ");\n";

    private String CREATE_TR_RECAP_PRODUCT_SALES = "CREATE TABLE IF NOT EXISTS TR_RECAP_PRODUCT_SALES (\n" +
            "    ID             INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    WEEK                       NUMBER,\n" +
            "    AREA                       VARCHAR2(30),\n" +
            "    DISTRICT_ID                VARCHAR2(30),\n" +
            "    DISTRICT_CODE  VARCHAR2(5),\n" +
            "    ROUTE                      VARCHAR2(2),\n" +
            "    PRODUCT_CODE               VARCHAR2(30),\n" +
            "    TOTAL_SALES_QTY            NUMBER\n" +
            ");\n";

    private String CREATE_TR_PP_ALLOCATED_TRF = "CREATE TABLE IF NOT EXISTS TR_PP_ALLOCATED_TRF (\n" +
            "    ID              INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    PP_PROGRAM_ID   NUMBER,\n" +
            "    DATE_CREATED    NUMBER,\n" +
            "    STATUS          VARCHAR2(1)\n" +
            ");\n";

    private String CREATE_LAST_CALL_AMP = "CREATE TABLE IF NOT EXISTS LAST_CALL_AMP (\n" +
            "    ID               INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    OUTLET_ID        VARCHAR2(70),\n" +
            "    OUTLET_CODE      VARCHAR2(20),\n" +
            "    WEEK             NUMBER,\n" +
            "    TRANS_DATE       NUMBER,\n" +
            "    PP_NO            NUMBER,\n" +
            "    PROGRAM_NAME     VARCHAR2(50),\n" +
            "    EXEC_TYPE_ID     NUMBER,\n" +
            "    EXEC_TYPE_NAME   VARCHAR2(120),\n" +
            "    GIFT_TYPE_NAME   VARCHAR2(120),\n" +
            "    ITEM_CODE        VARCHAR2(20),\n" +
            "    ITEM_NAME        VARCHAR2(120),\n" +
            "    ITEM_QTY         VARCHAR2(20),\n" +
            "    ITEM_UOM         VARCHAR2(20),\n" +
            "    ITEM_WEIGHT      VARCHAR2(20),\n" +
            "    ITEM_DESC        VARCHAR2(1000),\n" +
            "    TTO_NUMBER       NUMBER,\n" +
            "    TTO_STATUS       VARCHAR2(20),\n" +
            "    PIC_NAME         VARCHAR2(100),\n" +
            "    RECEIVER_NAME    VARCHAR2(100),\n" +
            "    USER_CREATED     VARCHAR2(50),\n" +
            "    DATE_CREATED     NUMBER\n" +
            ");\n";

    private String CREATE_MST_TTO_RECEIVER_BANK = "CREATE TABLE IF NOT EXISTS MST_TTO_RECEIVER_BANK (\n" +
            "    ID                         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    OUTLET_ID                  VARCHAR2(70),\n" +
            "    OUTLET_CODE                VARCHAR2(20),\n" +
            "    BANK_ID                    NUMBER,\n" +
            "    BANK_NAME                  VARCHAR2(100),\n" +
            "    ACCOUNT_NAME               VARCHAR2(100),\n" +
            "    BANK_ACCOUNT_NUMBER        VARCHAR2(50),\n" +
            "    STATUS                     VARCHAR2(1),\n" +
            "    USER_CREATED               VARCHAR2(50),\n" +
            "    DATE_CREATED               NUMBER,\n" +
            "    MST_TTO_RECEIVER_ID        NUMBER,\n" +
            "    MST_TTO_RECEIVER_BANK_ID   NUMBER,\n" +
            "    MST_TTO_RECEIVER_MOB_ID    NUMBER\n" +
            ");\n";

    private String CREATE_MST_OUTLET_BANK = "CREATE TABLE IF NOT EXISTS MST_OUTLET_BANK (\n" +
            "    ID                INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    OUTLET_ID         VARCHAR2(70),\n" +
            "    OUTLET_CODE       VARCHAR2(20),\n" +
            "    BANK_ID           NUMBER,\n" +
            "    BANK_NAME         VARCHAR2(100),\n" +
            "    ACCOUNT_NAME      VARCHAR2(100),\n" +
            "    BANK_ACCOUNT_NUMBER VARCHAR2(50),\n" +
            "    ACCOUNT_STATUS    NUMBER,\n" +
            "    USER_CREATED      VARCHAR2(50),\n" +
            "    DATE_CREATED      NUMBER,\n" +
            "    MST_BANK_ORA_ID   NUMBER\n" +
            ");\n";

    private String CREATE_MST_BANK = "CREATE TABLE IF NOT EXISTS MST_BANK (\n" +
            "    ID                INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    MST_BANK_ORA_ID   NUMBER,\n" +
            "    BANK_NAME         VARCHAR2(50),\n" +
            "    USER_CREATED      VARCHAR2(50),\n" +
            "    DATE_CREATED      NUMBER\n" +
            ");\n";

    private String CREATE_MST_TTO_REV_FIELD_MAP = "CREATE TABLE IF NOT EXISTS MST_TTO_REV_FIELD_MAP (\n" +
            "    ID                 INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    DFMS_PARAM_ID      NUMBER,\n" +
            "    FIELD_NAME         VARCHAR2(20),\n" +
            "    GROUP_FIELD_NAME   VARCHAR2(20),\n" +
            "    IS_ENABLED         VARCHAR2(1),\n" +
            "    TYPE               NUMBER,\n" +
            "    USER_CREATED       VARCHAR2(30),\n" +
            "    DATE_CREATED       NUMBER\n" +
            ");\n";


    private String CREATE_TR_OUTLET_BANK = "CREATE TABLE IF NOT EXISTS TR_OUTLET_BANK (\n" +
            "    ID                    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    OUTLET_ID             VARCHAR2(70),\n" +
            "    OUTLET_CODE           VARCHAR2(20),\n" +
            "    BANK_ID               NUMBER,\n" +
            "    BANK_NAME             VARCHAR2(100),\n" +
            "    ACCOUNT_NAME          VARCHAR2(100),\n" +
            "    BANK_ACCOUNT_NUMBER   VARCHAR2(50),\n" +
            "    ACCOUNT_STATUS        NUMBER,\n" +
            "    NAME_IDENTICAL_TO_KTP VARCHAR2(1),\n" +
            "    MST_OUTLET_BANK_ID    NUMBER,\n" +
            "    IS_UPLOADED           VARCHAR2(1),\n" +
            "    STATUS                VARCHAR2(1),\n" +
            "    USER_CREATED          VARCHAR2(50),\n" +
            "    DATE_CREATED          NUMBER,\n" +
            "    USER_MODIFIED         VARCHAR2(50),\n" +
            "    DATE_MODIFIED         NUMBER\n" +
            ");\n";

    private String CREATE_CS_SPV_OUTLET_REV = "CREATE TABLE IF NOT EXISTS CS_SPV_OUTLET_REV (\n" +
            "    ID                 INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_SPV_OUTLET_ID   NUMBER,\n" +
            "    OUTLET_ID          VARCHAR2(70),\n" +
            "    OU_ID              NUMBER,\n" +
            "    WEEK               NUMBER,\n" +
            "    CALL_DATE          NUMBER,\n" +
            "    USER_CREATED       VARCHAR2(30),\n" +
            "    DATE_CREATED       NUMBER,\n" +
            "    STATUS             VARCHAR2(1),\n" +
            "    TERRITORY_ID       NUMBER,\n" +
            "    DISTRICT_ID        NUMBER,\n" +
            "    ROUTE_ID           NUMBER,\n" +
            "    SALESMAN_ID        NUMBER\n" +
            ");\n";

    private String CREATE_CS_SPV_PROG_PP_REV = "CREATE TABLE IF NOT EXISTS CS_SPV_PROG_PP_REV (\n" +
            "    ID                  INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_SPV_OUTLET_ID    NUMBER,\n" +
            "    CS_SPV_PROG_PP_ID   NUMBER,\n" +
            "    PP_PROGRAM_ID       NUMBER,\n" +
            "    PROGRAM_NAME        VARCHAR2(50),\n" +
            "    PP_NO               NUMBER,\n" +
            "    BRAND_CODE          VARCHAR2(10),\n" +
            "    PP_PIC_ID           NUMBER,\n" +
            "    STATUS              VARCHAR2(1),\n" +
            "    CAN_CANCEL          VARCHAR2(1) DEFAULT 'Y',\n" +
            "    USER_CREATED        VARCHAR2(30),\n" +
            "    DATE_CREATED        NUMBER\n" +
            ");\n";

    private String CREATE_CS_SPV_EXEC_PP_REV = "CREATE TABLE IF NOT EXISTS CS_SPV_EXEC_PP_REV (\n" +
            "    ID                  INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_SPV_PROG_PP_ID   NUMBER,\n" +
            "    CS_SPV_EXEC_PP_ID   NUMBER,\n" +
            "    EXEC_TYPE_ID        NUMBER,\n" +
            "    EXEC_TYPE_NAME      VARCHAR2(50),\n" +
            "    STATUS              VARCHAR2(1),\n" +
            "    USER_CREATED        VARCHAR2(30),\n" +
            "    DATE_CREATED        NUMBER\n" +
            ");\n";

    private String CREATE_CS_SPV_EXEC_GIFT_REV = "CREATE TABLE IF NOT EXISTS CS_SPV_EXEC_GIFT_REV (\n" +
            "    ID                     INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_SPV_EXEC_PP_ID      NUMBER,\n" +
            "    CS_SPV_EXEC_GIFT_ID   NUMBER,\n" +
            "    GIFT_TYPE_ID           NUMBER,\n" +
            "    GIFT_TYPE_NAME         VARCHAR2(50),\n" +
            "    STATUS                 VARCHAR2(1),\n" +
            "    USER_CREATED           VARCHAR2(30),\n" +
            "    DATE_CREATED           NUMBER\n" +
            ");\n";

    private String CREATE_CS_SPV_EXEC_GIFT_ITEM_REV = "CREATE TABLE IF NOT EXISTS CS_SPV_EXEC_GIFT_ITEM_REV (\n" +
            "    ID                         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_SPV_EXEC_GIFT_ID        NUMBER,\n" +
            "    CS_SPV_EXEC_GIFT_ITEM_ID   NUMBER,\n" +
            "    GIFT_ITEM_PRODUCT_ID       NUMBER,\n" +
            "    GIFT_QTY_AMOUNT            NUMBER,\n" +
            "    GIFT_UNIT                  VARCHAR2(20),\n" +
            "    REASON_TYPE_ID             NUMBER,\n" +
            "    REASON_PRODUCT_ID          NUMBER,\n" +
            "    REASON_PRODUCT_QTY         NUMBER,\n" +
            "    STATUS                     VARCHAR2(1),\n" +
            "    USER_CREATED               VARCHAR2(30),\n" +
            "    DATE_CREATED               NUMBER,\n" +
            "    DESCRIPTION                VARCHAR2(225),\n" +
            "    FLAG_PU                    VARCHAR2(1),\n" +
            "    CS_TTO_HDR_ID              NUMBER,\n" +
            "    ITEM_WEIGHT                VARCHAR2(50),\n" +
            "    GIFT_ITEM_PRODUCT_NAME     VARCHAR2(150),\n" +
            "    REASON_TYPE_NAME           VARCHAR2(150),\n" +
            "    REASON_PRODUCT_UOM       VARCHAR2(1),\n" +
            "    REASON_PRODUCT_NAME        VARCHAR2(150)\n" +
            ");\n";

    private String CREATE_CS_TTO_HDR_REV = "CREATE TABLE IF NOT EXISTS CS_TTO_HDR_REV (\n" +
            "    ID                            INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_TTO_RECEIVER_ID            NUMBER,\n" +
            "    CS_PP_PROG_ID                 NUMBER,\n" +
            "    CS_SPV_OUTLET_ID              NUMBER,\n" +
            "    TTO_NUMBER                    NUMBER,\n" +
            "    TTO_TYPE_ID                   NUMBER,\n" +
            "    TERM_AND_COND                 VARCHAR2(1),\n" +
            "    DPP                           NUMBER,\n" +
            "    PPN                           NUMBER,\n" +
            "    PPH                           NUMBER,\n" +
            "    NETTO                         NUMBER,\n" +
            "    TRANS_DATE                    NUMBER,\n" +
            "    VERSION                       NUMBER,\n" +
            "    IS_NEW_PROGRAM                VARCHAR2(1),\n" +
            "    PATH_FILE                     VARCHAR2(1000),\n" +
            "    USER_CREATED                  VARCHAR2(50),\n" +
            "    DATE_CREATED                  NUMBER,\n" +
            "    PRINT_DATE                    NUMBER,\n" +
            "    LEGAL_ENTITY_ID               NUMBER,\n" +
            "    FLAG_NPWP                     VARCHAR2(1),\n" +
            "    IS_GROSS_UP                   VARCHAR2(1),\n" +
            "    IS_PKP                        VARCHAR2(1),\n" +
            "    PPH_PERCENT_ID                NUMBER,\n" +
            "    BENEFIT_RECIPIENT_ID          NUMBER,\n" +
            "    PRINT_VERSION                 NUMBER,\n" +
            "    IS_SPLIT                      VARCHAR2(1),\n" +
            "    PAYMENT_METHOD                NUMBER,\n" +
            "    PAYMENT_METHOD_ORIGIN         NUMBER,\n" +
            "    CS_TTO_RECEIVER_BANK_ID       NUMBER,\n" +
            "    CS_TTO_HDR_ID                 NUMBER,\n" +
            "    CS_TTO_RECEIVER_REV_ID        NUMBER,\n" +
            "    CS_TTO_RECEIVER_BANK_REV_ID   NUMBER,\n" +
            "    REV_STATUS                    VARCHAR2(1),\n" +
            "    UPLOAD_STATUS                 VARCHAR2(1),\n" +
            "    IS_RECIPIENT_SIGN             VARCHAR2(1),\n" +
            "    STATUS_VALIDATE               NUMBER,\n" +
            "    TERM_AND_COND_PDP             VARCHAR2(1),\n" +
            "    MOBILE_REVISION_DATE          DATE\n" +
            ");\n";

    private String CREATE_CS_TTO_RECEIVER_REV = "CREATE TABLE IF NOT EXISTS CS_TTO_RECEIVER_REV (\n" +
            "    ID                    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    NAME                  VARCHAR2(500),\n" +
            "    IDENTITY_NUMBER       VARCHAR2(25),\n" +
            "    ID_CARD_PATH_FILE     VARCHAR2(1000),\n" +
            "    NPWP_NUMBER           VARCHAR2(25),\n" +
            "    ADDRESS               VARCHAR2(1000),\n" +
            "    RELATION_ID           NUMBER,\n" +
            "    PHONE_NO              VARCHAR2(50),\n" +
            "    USER_CREATED          VARCHAR2(50),\n" +
            "    DATE_CREATED          NUMBER,\n" +
            "    CS_SPV_OUTLET_ID      NUMBER,\n" +
            "    STATUS                VARCHAR2(1),\n" +
            "    MST_TTO_RECEIVER_ID   NUMBER,\n" +
            "    CS_TTO_RECEIVER_ID    NUMBER,\n" +
            "    MST_TTO_RECEIVER_MOB_ID    NUMBER,\n" +
            "    MST_OUTLET_OWNER_MOB_ID    NUMBER\n" +
            ");\n";

    private String CREATE_CS_TTO_SIGNATURE_REV = "CREATE TABLE IF NOT EXISTS CS_TTO_SIGNATURE_REV (\n" +
            "    ID                         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_TTO_HDR_ID              NUMBER,\n" +
            "    CS_TTO_SIGNATURE_ID        NUMBER,\n" +
            "    PATH_FILE                  VARCHAR2(1000),\n" +
            "    SIGN_BY                    NUMBER,\n" +
            "    SIGN_DATE                  NUMBER,\n" +
            "    USER_CREATED               VARCHAR2(50),\n" +
            "    DATE_CREATED               NUMBER,\n" +
            "    LATITUDE_SIGN              VARCHAR2(200),\n" +
            "    LONGITUDE_SIGN             VARCHAR2(200),\n" +
            "    STATUS                     VARCHAR2(1)\n" +
            ");\n";

    private String CREATE_CS_TTO_CONTRACT_REV = "CREATE TABLE IF NOT EXISTS CS_TTO_CONTRACT_REV (\n" +
            "    ID                   INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_TTO_HDR_ID        NUMBER,\n" +
            "    CS_TTO_CONTRACT_ID   NUMBER,\n" +
            "    CONTRACT_NO          VARCHAR2(40),\n" +
            "    PERIODE_START        NUMBER,\n" +
            "    PERIODE_END          NUMBER,\n" +
            "    PERIOD_MONTH         NUMBER,\n" +
            "    PAYMENT_TYPE_ID      NUMBER,\n" +
            "    CONTRACT_INPUT       NUMBER,\n" +
            "    CONTRACT_AMOUNT      NUMBER,\n" +
            "    NETT_VALUE           NUMBER,\n" +
            "    USER_CREATED         VARCHAR2(50),\n" +
            "    DATE_CREATED         NUMBER,\n" +
            "    STATUS               VARCHAR2(1)\n" +
            ");\n";

    private String CREATE_CS_TTO_EXEC_GIFT_ITEM_REV = "CREATE TABLE IF NOT EXISTS CS_TTO_EXEC_GIFT_ITEM_REV (\n" +
            "    ID                         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_TTO_HDR_ID              NUMBER,\n" +
            "    GIFT_TYPE_ID               NUMBER,\n" +
            "    GIFT_ITEM_PRODUCT_ID       NUMBER,\n" +
            "    GIFT_QTY_AMOUNT            NUMBER,\n" +
            "    STATUS                     VARCHAR2(1),\n" +
            "    USER_CREATED               VARCHAR2(30),\n" +
            "    DATE_CREATED               NUMBER,\n" +
            "    CS_TTO_EXEC_GIFT_ITEM_ID   NUMBER\n" +
            ");\n";

    private String CREATE_CS_TTO_NOTIF_REV = "CREATE TABLE IF NOT EXISTS CS_TTO_NOTIF_REV (\n" +
            "    ID                INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_TTO_HDR_ID     NUMBER,\n" +
            "    TTO_NUMBER        NUMBER,\n" +
            "    REFF_ID           NUMBER,\n" +
            "    STATUS_VALIDATE   NUMBER,\n" +
            "    NOTE              VARCHAR2(1000),\n" +
            "    USER_CREATED      VARCHAR2(20),\n" +
            "    DATE_CREATED      NUMBER,\n" +
            "    REASON_REV        NUMBER\n" +
            ");\n";

    private String CREATE_CS_TTO_RECEIVER_BANK_REV = "CREATE TABLE IF NOT EXISTS CS_TTO_RECEIVER_BANK_REV (\n" +
            "    ID                         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_TTO_RECEIVER_ID         NUMBER,\n" +
            "    BANK_ID                    NUMBER,\n" +
            "    BANK_NAME                  VARCHAR2(100),\n" +
            "    ACCOUNT_NAME               VARCHAR2(100),\n" +
            "    BANK_ACCOUNT_NUMBER        VARCHAR2(50),\n" +
            "    USER_CREATED               VARCHAR2(50),\n" +
            "    DATE_CREATED               NUMBER,\n" +
            "    MST_TTO_RECEIVER_ID        NUMBER,\n" +
            "    MST_TTO_RECEIVER_BANK_ID   NUMBER,\n" +
            "    CS_TTO_RECEIVER_BANK_ID    NUMBER,\n" +
            "    IS_PKS_BANK                VARCHAR2(1),\n" +
            "    MST_OUTLET_BANK_ID         NUMBER,\n" +
            "    TR_OUTLET_BANK_ID    		NUMBER,\n" +
            "    CS_TTO_RECEIVER_REV_ID     NUMBER,\n" +
            "    NAME_IDENTICAL_TO_KTP      VARCHAR2(1),\n" +
            "    STATUS                     VARCHAR2(1),\n" +
            "    MST_TTO_RECEIVER_MOB_ID    NUMBER,\n" +
            "    MST_TTO_RECEIVER_BANK_MOB_ID   NUMBER,\n" +
            "    MST_OUTLET_OWNER_MOB_ID   NUMBER\n" +
            ");\n";

    private String CREATE_CS_TTO_UNIT_DISPLAY_REV = "CREATE TABLE IF NOT EXISTS CS_TTO_UNIT_DISPLAY_REV (\n" +
            "    ID                         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_TTO_UNIT_DISPLAY_ID     NUMBER,\n" +
            "    CS_TTO_CONTRACT_ID         NUMBER,\n" +
            "    UNIT_DISPLAY_ID            NUMBER,\n" +
            "    UNIT_DISPLAY_SUBCATEGORY   VARCHAR2(500),\n" +
            "    USER_CREATED               VARCHAR2(50),\n" +
            "    DATE_CREATED               NUMBER,\n" +
            "    STATUS                     VARCHAR2(1),\n" +
            "    QTY                        NUMBER\n" +
            ");\n";

    private String CREATE_CS_TTO_PRODUCT_DISPLAY_REV = "CREATE TABLE IF NOT EXISTS CS_TTO_PRODUCT_DISPLAY_REV (\n" +
            "    ID                          INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_TTO_PRODUCT_DISPLAY_ID   NUMBER,\n" +
            "    CS_TTO_CONTRACT_ID          NUMBER,\n" +
            "    PRODUCT_ID                  NUMBER,\n" +
            "    PRODUCT_CODE                VARCHAR2(50),\n" +
            "    USER_CREATED                VARCHAR2(50),\n" +
            "    DATE_CREATED                NUMBER,\n" +
            "    STATUS                      VARCHAR2(1)\n" +
            ");\n";

    private String CREATE_TR_DELIVER_ORDER_DTL_LOG = "CREATE TABLE IF NOT EXISTS TR_DELIVER_ORDER_DTL_LOG (\n" +
            "    ID                      INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TR_DELIVER_ORDER_ID     NUMBER,\n" +
            "    TR_PP_PROG_EXEC_ID      NUMBER,\n" +
            "    TR_NONPP_PROG_EXEC_ID   NUMBER,\n" +
            "    OUTLET_ID               VARCHAR2(70),\n" +
            "    STATUS                  VARCHAR2(1),\n" +
            "    TR_TT_HDR_ID            NUMBER,\n" +
            "    LOG_ID                  NUMBER\n" +
            ");\n";

    private String CREATE_TR_DELIVER_ORDER_LOG = "CREATE TABLE IF NOT EXISTS TR_DELIVER_ORDER_LOG (\n" +
            "    ID                    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    NO_DELIVER_ORDER      VARCHAR2(20),\n" +
            "    POLICE_NO             VARCHAR2(20),\n" +
            "    PIC                   VARCHAR2(100),\n" +
            "    STATUS                VARCHAR2(1),\n" +
            "    PRINT_VERSION         NUMBER,\n" +
            "    PRINT_DATE            NUMBER,\n" +
            "    DATE_CREATED          NUMBER,\n" +
            "    TR_TT_HDR_ID          NUMBER,\n" +
            "    LOG_COUNT             NUMBER,\n" +
            "    TR_TTO_LOG_ID         NUMBER,\n" +
            "    TR_DELIVER_ORDER_ID   NUMBER\n" +
            ");\n";

    private String CREATE_TR_TTO_RECEIVER_BANK_LOG = "CREATE TABLE IF NOT EXISTS TR_TTO_RECEIVER_BANK_LOG (\n" +
            "    ID                         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    TR_TTO_RECEIVER_ID          NUMBER,\n" +
            "    MST_TTO_RECEIVER_ID        NUMBER,\n" +
            "    BANK_ID                    NUMBER,\n" +
            "    BANK_NAME                  VARCHAR2(100),\n" +
            "    ACCOUNT_NAME               VARCHAR2(100),\n" +
            "    BANK_ACCOUNT_NUMBER        VARCHAR2(50),\n" +
            "    TR_TTO_LOG_ID              NUMBER,\n" +
            "    TR_TTO_RECEIVER_BANK_ID    NUMBER,\n" +
            "    NAME_IDENTICAL_TO_KTP      VARCHAR2(1),\n" +
            "    MST_OUTLET_BANK_ID         NUMBER,\n" +
            "    STATUS                     VARCHAR2,\n" +
            "    USER_CREATED               VARCHAR2(50),\n" +
            "    DATE_CREATED               NUMBER,\n" +
            "    USER_MODIFIED              VARCHAR2(50),\n" +
            "    DATE_MODIFIED              NUMBER,\n" +
            "    MST_TTO_RECEIVER_BANK_ID   NUMBER,\n" +
            "    TR_TTO_RECEIVER_BANK_LOG_ID   NUMBER\n" +
            ");\n";

    private String CREATE_MST_TTO_RECEIVER_BANK_LOG = "CREATE TABLE IF NOT EXISTS MST_TTO_RECEIVER_BANK_LOG (\n" +
            "    ID                         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    OUTLET_ID                  NUMBER,\n" +
            "    OUTLET_CODE                VARCHAR2(20),\n" +
            "    MST_TTO_RECEIVER_ID        NUMBER,\n" +
            "    BANK_ID                    NUMBER,\n" +
            "    BANK_NAME                  VARCHAR2(100),\n" +
            "    ACCOUNT_NAME               VARCHAR2(100),\n" +
            "    BANK_ACCOUNT_NUMBER        VARCHAR2(50),\n" +
            "    TR_TTO_LOG_ID              NUMBER,\n" +
            "    USER_CREATED               VARCHAR2(50),\n" +
            "    DATE_CREATED               NUMBER,\n" +
            "    MST_TTO_RECEIVER_BANK_ID   NUMBER\n" +
            ");\n";

    private String CREATE_MST_TTO_RECEIVER_LOG = "CREATE TABLE IF NOT EXISTS MST_TTO_RECEIVER_LOG (\n" +
            "    ID                    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    OUTLET_ID             VARCHAR2(70),\n" +
            "    NAME                  VARCHAR2(100),\n" +
            "    IDENTITY_NUMBER       VARCHAR2(50),\n" +
            "    NPWP_NUMBER           VARCHAR2(50),\n" +
            "    ADDRESS               VARCHAR2(1000),\n" +
            "    FLAG_PHOTO_KTP        VARCHAR2(1),\n" +
            "    USER_CREATED          VARCHAR2(20),\n" +
            "    DATE_CREATED          NUMBER,\n" +
            "    RELATION_ID           NUMBER,\n" +
            "    PHONE_NO              VARCHAR2(20),\n" +
            "    MST_TTO_RECEIVER_ID   NUMBER,\n" +
            "    ID_CARD_PATH_FILE     VARCHAR2(500),\n" +
            "    TR_TTO_LOG_ID         NUMBER\n" +
            ");\n";

    private String CREATE_MST_OUTLET_BANK_LOG = "CREATE TABLE IF NOT EXISTS MST_OUTLET_BANK_LOG (\n" +
            "    ID                    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    OUTLET_ID             NUMBER,\n" +
            "    OUTLET_CODE           VARCHAR2(20),\n" +
            "    BANK_ID               NUMBER,\n" +
            "    BANK_NAME             VARCHAR2(100),\n" +
            "    ACCOUNT_NAME          VARCHAR2(100),\n" +
            "    BANK_ACCOUNT_NUMBER   VARCHAR2(50),\n" +
            "    ACCOUNT_STATUS        NUMBER,\n" +
            "    MST_OUTLET_BANK_ID    NUMBER,\n" +
            "    MST_BANK_ORA_ID      NUMBER,\n" +
            "    USER_CREATED          VARCHAR2(50),\n" +
            "    DATE_CREATED          NUMBER,\n" +
            "    TR_TTO_LOG_ID         NUMBER\n" +
            ");\n";

    private String CREATE_TR_OUTLET_BANK_LOG = "CREATE TABLE IF NOT EXISTS TR_OUTLET_BANK_LOG (\n" +
            "    ID                    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    OUTLET_ID             VARCHAR2(70),\n" +
            "    OUTLET_CODE           VARCHAR2(20),\n" +
            "    BANK_ID               NUMBER,\n" +
            "    BANK_NAME             VARCHAR2(100),\n" +
            "    ACCOUNT_NAME          VARCHAR2(100),\n" +
            "    BANK_ACCOUNT_NUMBER   VARCHAR2(50),\n" +
            "    ACCOUNT_STATUS        NUMBER,\n" +
            "    NAME_IDENTICAL_TO_KTP VARCHAR2(1),\n" +
            "    MST_OUTLET_BANK_ID    NUMBER,\n" +
            "    STATUS                VARCHAR2(1),\n" +
            "    USER_CREATED          VARCHAR2(50),\n" +
            "    DATE_CREATED          NUMBER,\n" +
            "    USER_MODIFIED         VARCHAR2(50),\n" +
            "    DATE_MODIFIED         NUMBER,\n" +
            "    TR_TTO_LOG_ID         NUMBER,\n" +
            "    TR_OUTLET_BANK_ID     NUMBER\n" +
            ");\n";

    private String CREATE_CS_SPV_OUTLET_REV_LOG = "CREATE TABLE IF NOT EXISTS CS_SPV_OUTLET_REV_LOG (\n" +
            "    ID                 INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_SPV_OUTLET_ID   NUMBER,\n" +
            "    OUTLET_ID          NUMBER,\n" +
            "    OU_ID              NUMBER,\n" +
            "    WEEK               NUMBER,\n" +
            "    CALL_DATE          NUMBER,\n" +
            "    CS_SPV_OUTLET_REV_ID  NUMBER,\n" +
            "    USER_CREATED       VARCHAR2(30),\n" +
            "    DATE_CREATED       NUMBER,\n" +
            "    STATUS             VARCHAR2(1),\n" +
            "    TERRITORY_ID       NUMBER,\n" +
            "    DISTRICT_ID        NUMBER,\n" +
            "    ROUTE_ID           NUMBER,\n" +
            "    SALESMAN_ID        NUMBER,\n" +
            "    TR_TTO_LOG_ID      NUMBER\n" +
            ");\n";

    private String CREATE_CS_SPV_PROG_PP_REV_LOG = "CREATE TABLE IF NOT EXISTS CS_SPV_PROG_PP_REV_LOG (\n" +
            "    ID                  INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_SPV_OUTLET_ID    NUMBER,\n" +
            "    CS_SPV_PROG_PP_ID   NUMBER,\n" +
            "    PP_PROGRAM_ID       NUMBER,\n" +
            "    CS_SPV_PROG_PP_REV_ID  NUMBER,\n" +
            "    PROGRAM_NAME        VARCHAR2(50),\n" +
            "    PP_NO               NUMBER,\n" +
            "    BRAND_CODE          VARCHAR2(10),\n" +
            "    PP_PIC_ID           NUMBER,\n" +
            "    STATUS              VARCHAR2(1),\n" +
            "    CAN_CANCEL          VARCHAR2(1) DEFAULT 'Y',\n" +
            "    USER_CREATED        VARCHAR2(30),\n" +
            "    DATE_CREATED        DATE,\n" +
            "    TR_TTO_LOG_ID       NUMBER\n" +
            ");\n";

    private String CREATE_CS_SPV_EXEC_PP_REV_LOG = "CREATE TABLE IF NOT EXISTS CS_SPV_EXEC_PP_REV_LOG (\n" +
            "    ID                  INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_SPV_PROG_PP_ID   NUMBER,\n" +
            "    CS_SPV_EXEC_PP_ID   NUMBER,\n" +
            "    EXEC_TYPE_ID        NUMBER,\n" +
            "    CS_SPV_EXEC_PP_REV_ID   NUMBER,\n" +
            "    EXEC_TYPE_NAME      VARCHAR2(50),\n" +
            "    STATUS              VARCHAR2(1),\n" +
            "    USER_CREATED        VARCHAR2(30),\n" +
            "    DATE_CREATED        NUMBER,\n" +
            "    TR_TTO_LOG_ID       NUMBER\n" +
            ");\n";

    private String CREATE_CS_SPV_EXEC_GIFT_REV_LOG = "CREATE TABLE IF NOT EXISTS CS_SPV_EXEC_GIFT_REV_LOG (\n" +
            "    ID                     INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_SPV_EXEC_PP_ID      NUMBER,\n" +
            "    CS_SPV_EXEC_GIFT_ID   NUMBER,\n" +
            "    CS_SPV_EXEC_GIFT_REV_ID NUMBER,\n" +
            "    GIFT_TYPE_ID           NUMBER,\n" +
            "    GIFT_TYPE_NAME         VARCHAR2(50),\n" +
            "    STATUS                 VARCHAR2(1),\n" +
            "    USER_CREATED           VARCHAR2(30),\n" +
            "    DATE_CREATED           NUMBER,\n" +
            "    USER_MODIFIED          VARCHAR2(30),\n" +
            "    DATE_MODIFIED          NUMBER,\n" +
            "    TR_TTO_LOG_ID          NUMBER\n" +
            ");\n";

    private String CREATE_CS_SPV_EXEC_GIFT_ITEM_REV_LOG = "CREATE TABLE IF NOT EXISTS CS_SPV_EXEC_GIFT_ITEM_REV_LOG (\n" +
            "    ID                       INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_SPV_EXEC_GIFT_ID      NUMBER,\n" +
            "    GIFT_ITEM_PRODUCT_ID     NUMBER,\n" +
            "    GIFT_QTY_AMOUNT          NUMBER,\n" +
            "    GIFT_UNIT                VARCHAR2(20),\n" +
            "    REASON_TYPE_ID           NUMBER,\n" +
            "    REASON_PRODUCT_ID        NUMBER,\n" +
            "    REASON_PRODUCT_QTY       NUMBER,\n" +
            "    CS_SPV_EXEC_GIFT_ITEM_ID       NUMBER,\n" +
            "    CS_SPV_EXEC_GIFT_ITEM_REV_ID   NUMBER,\n" +
            "    REASON_PRODUCT_UOM       VARCHAR2(1),\n" +
            "    STATUS                   VARCHAR2(1),\n" +
            "    USER_CREATED             VARCHAR2(30),\n" +
            "    DATE_CREATED             NUMBER,\n" +
            "    DESCRIPTION              VARCHAR2(225),\n" +
            "    FLAG_PU                  VARCHAR2(1),\n" +
            "    CS_TTO_HDR_ID            NUMBER,\n" +
            "    ITEM_WEIGHT              VARCHAR2(50),\n" +
            "    GIFT_ITEM_PRODUCT_NAME   VARCHAR2(150),\n" +
            "    REASON_TYPE_NAME         VARCHAR2(150),\n" +
            "    REASON_PRODUCT_NAME      VARCHAR2(150),\n" +
            "    TR_TTO_LOG_ID            NUMBER\n" +
            ");\n";

    private String CREATE_CS_TTO_RECEIVER_REV_LOG = "CREATE TABLE IF NOT EXISTS CS_TTO_RECEIVER_REV_LOG (\n" +
            "    ID                       INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    NAME                     VARCHAR2(500),\n" +
            "    IDENTITY_NUMBER          VARCHAR2(25),\n" +
            "    ID_CARD_PATH_FILE        VARCHAR2(1000),\n" +
            "    NPWP_NUMBER              VARCHAR2(25),\n" +
            "    ADDRESS                  VARCHAR2(1000),\n" +
            "    RELATION_ID              NUMBER,\n" +
            "    PHONE_NO                 VARCHAR2(50),\n" +
            "    USER_CREATED             VARCHAR2(50),\n" +
            "    DATE_CREATED             NUMBER,\n" +
            "    CS_SPV_OUTLET_ID         NUMBER,\n" +
            "    STATUS                   VARCHAR2(1),\n" +
            "    MST_TTO_RECEIVER_ID      NUMBER,\n" +
            "    CS_TTO_RECEIVER_ID       NUMBER,\n" +
            "    CS_TTO_RECEIVER_REV_ID   NUMBER,\n" +
            "    TR_TTO_LOG_ID            NUMBER\n" +
            ");\n";

    private String CREATE_CS_TTO_HDR_REV_LOG = "CREATE TABLE IF NOT EXISTS CS_TTO_HDR_REV_LOG (\n" +
            "    ID                        INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_TTO_RECEIVER_ID        NUMBER,\n" +
            "    CS_PP_PROG_ID             NUMBER,\n" +
            "    CS_SPV_OUTLET_ID          NUMBER,\n" +
            "    TTO_NUMBER                NUMBER,\n" +
            "    TTO_TYPE_ID               NUMBER,\n" +
            "    TERM_AND_COND             VARCHAR2(1),\n" +
            "    DPP                       NUMBER,\n" +
            "    PPN                       NUMBER,\n" +
            "    PPH                       NUMBER,\n" +
            "    NETTO                     NUMBER,\n" +
            "    TRANS_DATE                NUMBER,\n" +
            "    VERSION                   NUMBER,\n" +
            "    IS_NEW_PROGRAM            VARCHAR2(1),\n" +
            "    PATH_FILE                 VARCHAR2(1000),\n" +
            "    USER_CREATED              VARCHAR2(50),\n" +
            "    DATE_CREATED              NUMBER,\n" +
            "    PRINT_DATE                NUMBER,\n" +
            "    LEGAL_ENTITY_ID           NUMBER,\n" +
            "    FLAG_NPWP                 VARCHAR2(1),\n" +
            "    IS_GROSS_UP               VARCHAR2(1),\n" +
            "    IS_PKP                    VARCHAR2(1),\n" +
            "    PPH_PERCENT_ID            NUMBER,\n" +
            "    BENEFIT_RECIPIENT_ID      NUMBER,\n" +
            "    PRINT_VERSION             NUMBER,\n" +
            "    IS_SPLIT                  VARCHAR2(1),\n" +
            "    PAYMENT_METHOD            NUMBER,\n" +
            "    CS_TTO_RECEIVER_BANK_ID   NUMBER,\n" +
            "    CS_TTO_HDR_ID             NUMBER,\n" +
            "    CS_TTO_HDR_REV_ID         NUMBER,\n" +
            "    TR_TTO_LOG_ID             NUMBER,\n" +
            "    CS_TTO_RECEIVER_REV_ID        NUMBER,\n" +
            "    CS_TTO_RECEIVER_BANK_REV_ID   NUMBER,\n" +
            "    REV_STATUS                VARCHAR2(1),\n" +
            "    UPLOAD_STATUS             VARCHAR2(1),\n" +
            "    TERM_AND_COND_PDP         VARCHAR2(1),\n" +
            "    IS_RECIPIENT_SIGN         VARCHAR2(1),\n" +
            "    STATUS_VALIDATE           NUMBER,\n" +
            "    MOBILE_REVISION_DATE      DATE\n" +
            ");\n";

    private String CREATE_CS_TTO_EXEC_GIFT_ITEM_REV_LOG = "CREATE TABLE IF NOT EXISTS CS_TTO_EXEC_GIFT_ITEM_REV_LOG (\n" +
            "    ID                             INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_TTO_HDR_ID                  NUMBER,\n" +
            "    GIFT_TYPE_ID                   NUMBER,\n" +
            "    GIFT_ITEM_PRODUCT_ID           NUMBER,\n" +
            "    GIFT_QTY_AMOUNT                NUMBER,\n" +
            "    STATUS                         VARCHAR2(1),\n" +
            "    USER_CREATED                   VARCHAR2(30),\n" +
            "    DATE_CREATED                   NUMBER,\n" +
            "    CS_TTO_EXEC_GIFT_ITEM_ID       NUMBER,\n" +
            "    CS_TTO_EXEC_GIFT_ITEM_REV_ID   NUMBER,\n" +
            "    TR_TTO_LOG_ID                  NUMBER\n" +
            ");\n";

    private String CREATE_CS_TTO_RECEIVER_BANK_REV_LOG = "CREATE TABLE IF NOT EXISTS CS_TTO_RECEIVER_BANK_REV_LOG (\n" +
            "    ID                            INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_TTO_RECEIVER_ID            NUMBER,\n" +
            "    BANK_ID                       NUMBER,\n" +
            "    BANK_NAME                     VARCHAR2(100),\n" +
            "    ACCOUNT_NAME                  VARCHAR2(100),\n" +
            "    BANK_ACCOUNT_NUMBER           VARCHAR2(50),\n" +
            "    USER_CREATED                  VARCHAR2(50),\n" +
            "    DATE_CREATED                  NUMBER,\n" +
            "    MST_TTO_RECEIVER_ID           NUMBER,\n" +
            "    MST_TTO_RECEIVER_BANK_ID      NUMBER,\n" +
            "    CS_TTO_RECEIVER_BANK_ID       NUMBER,\n" +
            "    IS_PKS_BANK                   VARCHAR2(1),\n" +
            "    STATUS                        VARCHAR2(1),\n" +
            "    CS_TTO_RECEIVER_BANK_REV_ID   NUMBER,\n" +
            "    MST_OUTLET_BANK_ID            NUMBER,\n" +
            "    TR_OUTLET_BANK_ID             NUMBER,\n" +
            "    CS_TTO_RECEIVER_REV_ID        NUMBER,\n" +
            "    NAME_IDENTICAL_TO_KTP         VARCHAR2(1),\n" +
            "    TR_TTO_LOG_ID                 NUMBER\n" +
            ");\n";

    private String CREATE_CS_TTO_CONTRACT_REV_LOG = "CREATE TABLE IF NOT EXISTS CS_TTO_CONTRACT_REV_LOG (\n" +
            "    ID                       INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_TTO_HDR_ID            NUMBER,\n" +
            "    CS_TTO_CONTRACT_ID       NUMBER,\n" +
            "    CONTRACT_NO              VARCHAR2(40),\n" +
            "    PERIODE_START            NUMBER,\n" +
            "    PERIODE_END              NUMBER,\n" +
            "    PERIOD_MONTH             NUMBER,\n" +
            "    PAYMENT_TYPE_ID          NUMBER,\n" +
            "    CONTRACT_INPUT           NUMBER,\n" +
            "    CONTRACT_AMOUNT          NUMBER,\n" +
            "    NETT_VALUE               NUMBER,\n" +
            "    USER_CREATED             VARCHAR2(50),\n" +
            "    DATE_CREATED             NUMBER,\n" +
            "    CS_TTO_CONTRACT_REV_ID   NUMBER,\n" +
            "    TR_TTO_LOG_ID            NUMBER,\n" +
            "    STATUS                   VARCHAR2(1)\n" +
            ");\n";

    private String CREATE_CS_TTO_SIGNATURE_REV_LOG = "CREATE TABLE IF NOT EXISTS CS_TTO_SIGNATURE_REV_LOG (\n" +
            "    ID                        INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_TTO_HDR_ID             NUMBER,\n" +
            "    CS_TTO_SIGNATURE_ID       NUMBER,\n" +
            "    PATH_FILE                 VARCHAR2(1000),\n" +
            "    SIGN_BY                   NUMBER,\n" +
            "    SIGN_DATE                 NUMBER,\n" +
            "    USER_CREATED              VARCHAR2(50),\n" +
            "    DATE_CREATED              NUMBER,\n" +
            "    LATITUDE_SIGN             VARCHAR2(200),\n" +
            "    LONGITUDE_SIGN            VARCHAR2(200),\n" +
            "    CS_TTO_SIGNATURE_REV_ID   NUMBER,\n" +
            "    TR_TTO_LOG_ID             NUMBER,\n" +
            "    STATUS                    VARCHAR2(1)\n" +
            ");\n";

    private String CREATE_CS_TTO_UNIT_DISPLAY_REV_LOG = "CREATE TABLE IF NOT EXISTS CS_TTO_UNIT_DISPLAY_REV_LOG (\n" +
            "    ID                           INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_TTO_UNIT_DISPLAY_ID       NUMBER,\n" +
            "    CS_TTO_CONTRACT_ID           NUMBER,\n" +
            "    CS_TTO_CONTRACT_REV_ID       NUMBER,\n" +
            "    UNIT_DISPLAY_ID              NUMBER,\n" +
            "    UNIT_DISPLAY_SUBCATEGORY     VARCHAR2(500),\n" +
            "    USER_CREATED                 VARCHAR2(50),\n" +
            "    DATE_CREATED                 NUMBER,\n" +
            "    STATUS                       VARCHAR2(1),\n" +
            "    QTY                          NUMBER,\n" +
            "    CS_TTO_UNIT_DISPLAY_REV_ID   NUMBER,\n" +
            "    TR_TTO_LOG_ID                NUMBER\n" +
            ");\n";

    private String CREATE_CS_TTO_PRODUCT_DISPLAY_REV_LOG = "CREATE TABLE IF NOT EXISTS CS_TTO_PRODUCT_DISPLAY_REV_LOG (\n" +
            "    ID                              INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CS_TTO_PRODUCT_DISPLAY_ID       NUMBER,\n" +
            "    CS_TTO_CONTRACT_ID              NUMBER,\n" +
            "    CS_TTO_CONTRACT_REV_ID          NUMBER,\n" +
            "    PRODUCT_ID                      NUMBER,\n" +
            "    PRODUCT_CODE                    VARCHAR2(50),\n" +
            "    USER_CREATED                    VARCHAR2(50),\n" +
            "    DATE_CREATED                    NUMBER,\n" +
            "    STATUS                          VARCHAR2(1),\n" +
            "    CS_TTO_PRODUCT_DISPLAY_REV_ID   NUMBER,\n" +
            "    TR_TTO_LOG_ID                   NUMBER\n" +
            ");\n";

    private String CREATE_MST_CONTRACT_REV = "CREATE TABLE IF NOT EXISTS MST_CONTRACT_REV (\n" +
            "    ID                     INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    MST_CONTRACT_REV_ID    NUMBER,\n" +
            "    OUTLET_ID              NUMBER,\n" +
            "    OUTLET_CODE            VARCHAR2(20),\n" +
            "    PP_ID                  NUMBER,\n" +
            "    PP_NO                  NUMBER,\n" +
            "    PROGRAM_TYPE_ID        NUMBER,\n" +
            "    PROGRAM_NAME           VARCHAR2(200),\n" +
            "    BRAND_ID               NUMBER,\n" +
            "    BRAND_CODE             VARCHAR2(10),\n" +
            "    CONTRACT_NO            VARCHAR2(40),\n" +
            "    PERIODE_START          NUMBER,\n" +
            "    PERIODE_END            NUMBER,\n" +
            "    PAYMENT_TYPE_ID        NUMBER,\n" +
            "    CONTRACT_AMOUNT        NUMBER,\n" +
            "    REMAIN_PAYMENT         NUMBER,\n" +
            "    DPP                    NUMBER,\n" +
            "    PPN                    NUMBER,\n" +
            "    PPH                    NUMBER,\n" +
            "    NETT_AMOUNT            NUMBER,\n" +
            "    LEGAL_ENTITY_ID        NUMBER,\n" +
            "    FLAG_NPWP              VARCHAR2(1),\n" +
            "    IS_GROSS_UP            VARCHAR2(1),\n" +
            "    IS_PKP                 VARCHAR2(1),\n" +
            "    PPH_PERCENT_ID         NUMBER,\n" +
            "    PROGRAM_RANK           NUMBER,\n" +
            "    WEEK                   NUMBER,\n" +
            "    USER_CREATED           VARCHAR2(30),\n" +
            "    DATE_CREATED           NUMBER,\n" +
            "    TTO_TYPE_ID            NUMBER,\n" +
            "    BENEFIT_RECIPIENT_ID   NUMBER,\n" +
            "    CONTRACT_INPUT         NUMBER,\n" +
            "    PERIOD_MONTH           NUMBER,\n" +
            "    AMP_CONTRACT_ID        NUMBER\n" +
            ");\n";

    private String CREATE_MST_CONTRACT_STEP_REV = "CREATE TABLE IF NOT EXISTS MST_CONTRACT_STEP_REV (\n" +
            "    ID                    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    STEP                  NUMBER,\n" +
            "    FEE                   NUMBER,\n" +
            "    USER_CREATED          VARCHAR2(30),\n" +
            "    DATE_CREATED          NUMBER,\n" +
            "    PP_NO                 NUMBER,\n" +
            "    PP_ID                 NUMBER,\n" +
            "    MST_CONTRACT_REV_ID   NUMBER\n" +
            ");\n";

    private String CREATE_MST_PRODUCT_DISPLAY_REV = "CREATE TABLE IF NOT EXISTS MST_PRODUCT_DISPLAY_REV (\n" +
            "    ID                    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    PRODUCT_ID            NUMBER,\n" +
            "    PRODUCT_CODE          VARCHAR2(50),\n" +
            "    USER_CREATED          VARCHAR2(50),\n" +
            "    DATE_CREATED          NUMBER,\n" +
            "    MST_CONTRACT_REV_ID   NUMBER\n" +
            ");\n";

    private String CREATE_MST_UNIT_DISPLAY_REV = "CREATE TABLE IF NOT EXISTS MST_UNIT_DISPLAY_REV (\n" +
            "    ID                         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    UNIT_DISPLAY_ID            NUMBER,\n" +
            "    UNIT_DISPLAY_SUBCATEGORY   VARCHAR2(500),\n" +
            "    USER_CREATED               VARCHAR2(50),\n" +
            "    DATE_CREATED               NUMBER,\n" +
            "    QTY                        NUMBER,\n" +
            "    MST_CONTRACT_REV_ID        NUMBER\n" +
            ");\n";

    private String CREATE_TR_PAYMENT_PHOTO = "CREATE TABLE IF NOT EXISTS TR_PAYMENT_PHOTO (" +
            "ID	INTEGER PRIMARY KEY AUTOINCREMENT,  " +
            "PAYMENT_TABLE_ID	NUMBER, " +
            "PAYMENT_SOURCE_ID	NUMBER, " +
            "MANUAL_FLAG	VARCHAR2(1),    " +
            "FILEPHOTO	VARCHAR2(500),  " +
            "DESCRIPTION	VARCHAR2(200),  " +
            "STATUS	VARCHAR2(1),    " +
            "USER_CREATED	VARCHAR2(20),   " +
            "DATE_CREATED	DATE    " + ");";
    private String CREATE_TR_SALES_PHOTO = "CREATE TABLE IF NOT EXISTS TR_SALES_PHOTO (\n" +
            "   \tID                INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "   \tFLAG_SALES_ADD    INTEGER,\n" +
            "   \tOUTLET_ID         VARCHAR,\n" +
            "   \tNOTE_CODE         INTEGER,\n" +
            "   \tFILE_NAME         VARCHAR,\n" +
            "   \tDESCRIPTION       VARCHAR,\n" +
            "   \tREMARK            VARCHAR,\n" +
            "   \tUSER_CREATED      VARCHAR,\n" +
            "   \tDATE_CREATED      LONG\n" +
            ");";
    private String INSERT_MST_PAYMENT_MAP_TUNAI =
            "INSERT OR REPLACE INTO MST_PAYMENT_MAP (\n" +
                    "\tMST_PAYMENT_MAP_ID, SOURCE, PAYMENT_TERM_ID, PAYMENT_TYPE_ID,\n" +
                    "\tTEMPLATE, PATHFILE_ICON, MAX_PAYMENT,\n" +
                    "\tSORT_ORDER, DATE_CREATED, USER_CREATED\n" +
                    ")\n" +
                    "SELECT\n" +
                    "\t1, 'SFA', 'PAY001', 'PAYT01',\n" +
                    "\t'TUNAI', '/assets/payment_method_cash.png', 1,\n" +
                    "\t1, strftime('%s', 'now') * 1000, 'SYSTEM'\n" +
                    "WHERE (SELECT COUNT(*) FROM MST_PAYMENT_MAP) = 0;";

    private String CREATE_TR_COMP_PROG_CHECKLIST = "CREATE TABLE IF NOT EXISTS TR_COMP_PROG_CHECKLIST (\n" +
            "   \tID                INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "   \tTERRITORY_CODE    VARCHAR,\n" +
            "   \tDISTRICT_CODE     VARCHAR,\n" +
            "   \tROUTE             VARCHAR,\n" +
            "   \tOUTLET_ID         VARCHAR,\n" +
            "   \tIS_SURVEY         VARCHAR,\n" +
            "   \tUSER_CREATED       VARCHAR,\n" +
            "   \tDATE_CREATED       LONG,\n" +
            "   \tUSER_MODIFIED      VARCHAR,\n" +
            "   \tDATE_MODIFIED      LONG\n" +
            ");";


    private String CREATE_TR_JOURNAL = "CREATE TABLE IF NOT EXISTS TR_JOURNAL (\n" +
            "            ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "            DOC_NO          VARCHAR2(30),\n" +
            "            TRANSDATE       NUMBER,\n" +
            "            SOURCE          VARCHAR2(5),\n" +
            "            ORIGIN          VARCHAR2(10),\n" +
            "            TYPE            VARCHAR2(50),\n" +
            "            TODORO          VARCHAR2(20),\n" +
            "            OUTLET_CODE     VARCHAR2(30),\n" +
            "            ITEM_CODE       VARCHAR2(100),\n" +
            "            QTY             NUMBER,\n" +
            "            PRICE           NUMBER,\n" +
            "            WEEK            NUMBER,\n" +
            "            FLAG_UPLOAD     VARCHAR2(1),\n" +
            "            STATUS          VARCHAR2(1)\n" +
            "        );";

    private String CREATE_TR_PRIMARY_APP = "CREATE TABLE IF NOT EXISTS TR_PRIMARY_APP (\n" +
            "            ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "            USER_ID             VARCHAR2(30),\n" +
            "            APP                 VARCHAR2(10),\n" +
            "            SOURCE            VARCHAR2(10),\n" +
            "            DOC_NO              VARCHAR2(20),\n" +
            "            FLAG_UPLOAD         VARCHAR2(1)\n" +
            "        );";

    private String CREATE_MST_SALESMAN_MULTICOV = "CREATE TABLE IF NOT EXISTS MST_SALESMAN_MULTICOV (\n" +
            "            ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "            USER_ID             VARCHAR2(30),\n" +
            "            FLAG_STOCKIST       VARCHAR2(1),\n" +
            "            WEEK                NUMBER\n" +
            "        );";

    /**
     * @author noviantyn
     * 17 Januari 2025
     * CR KPI Retail V1
     * [CR V1 BPPM ONLINE - COD] MOBILE - BPPM - BPPM - Close BPPM - #106459
     * Create table TR_JOURNAL_HIST to backup the main table
     */
    private String CREATE_TR_JOURNAL_HIST = "CREATE TABLE IF NOT EXISTS TR_JOURNAL_HIST (\n" +
            "            ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "            DOC_NO          VARCHAR2(30),\n" +
            "            TRANSDATE       NUMBER,\n" +
            "            SOURCE          VARCHAR2(5),\n" +
            "            ORIGIN          VARCHAR2(10),\n" +
            "            TYPE            VARCHAR2(50),\n" +
            "            TODORO          VARCHAR2(20),\n" +
            "            OUTLET_CODE     VARCHAR2(30),\n" +
            "            ITEM_CODE       VARCHAR2(100),\n" +
            "            QTY             NUMBER,\n" +
            "            PRICE           NUMBER,\n" +
            "            WEEK            NUMBER,\n" +
            "            FLAG_UPLOAD     VARCHAR2(1),\n" +
            "            STATUS          VARCHAR2(1)\n" +
            "        );";

    /**
     * @author josuah
     * 4 June 2025
     * AE Informasi Bidang Promosi
     */
    private String CREATE_MST_OUTLET_SHOPBLIND_DTL = "CREATE TABLE IF NOT EXISTS MST_OUTLET_SHOPBLIND_DTL (" +
            "    ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    TERRITORY_CODE VARCHAR," +
            "    DISTRICT_CODE VARCHAR," +
            "    ROUTE NUMBER," +
            "    OUTLET_CODE VARCHAR," +
            "    OUTLET_SHOPBLIND_DTL_ID VARCHAR," +
            "    SHOPBLIND_LENGTH NUMBER," +
            "    STATUS VARCHAR," +
            "    WEEK INTEGER," +
            "    DATE_CREATED INTEGER," +
            "    USER_CREATED VARCHAR," +
            "    DATE_MODIFIED INTEGER," +
            "    USER_MODIFIED VARCHAR" +
            "        );";

    public DatabaseHandler(Context context) {
        super(context, context.getExternalFilesDir(null) + File.separator + FOLDER_NAME + File.separator + DATABASE_NAME, null, DATABASE_VERSION, new SQLiteDatabaseHook() {
            @Override
            public void preKey(SQLiteDatabase sqLiteDatabase) {

            }

            @Override
            public void postKey(SQLiteDatabase sqLiteDatabase) {
                SharedPreferences prefs = context.getSharedPreferences("net.sqlcipher.database.SQLCipherV3Helper", context.MODE_PRIVATE);

                boolean isMigrate = prefs.getBoolean(sqLiteDatabase.getPath(), false);

                if (!isMigrate) {
                    sqLiteDatabase.rawExecSQL("PRAGMA cipher_migrate;");
                    prefs.edit().putBoolean(sqLiteDatabase.getPath(), true).commit();
                }
            }
        });
        this.context = context;

//        Function untuk memindahkan db ketika ada db di lokasi yang lama (ketika update APK)
        checkingFilesDb(context);
    }

    public DatabaseHandler(Context context, Integer version) {
        super(context, context.getExternalFilesDir(null) + File.separator + FOLDER_NAME + File.separator + DATABASE_NAME, null, version);
    }

    /**
     * @author ivany
     * pindah lokasi Database ke android/data
     * 17 Januari 2024
     */
    public void checkingFilesDb(Context context) {
        String oldLocation = Environment.getExternalStorageDirectory() + File.separator + FOLDER_NAME;
        String newLocation = context.getExternalFilesDir(null) + File.separator;

        File oldLoc = new File(oldLocation);

        //Jika di env luar ada db nya, maka pindahkan ke env android data
        if (oldLoc.exists()) {
            if (oldLoc.isDirectory()) {
                String[] files = oldLoc.list();
                if (files != null && files.length > 0) {
                    moveDbToAndroidData(oldLocation, newLocation);
                }
            }
        }
    }

    public static void moveDbToAndroidData(String oldLocation, String newLocation) {
        try {
            File oldLoc = new File(oldLocation);
            File newLoc = new File(newLocation, oldLoc.getName());

            if (oldLoc.isDirectory()) {
                String[] files = oldLoc.list();
                for (String file : files) {
                    String oldLocation1 = (new File(oldLoc, file).getPath());
                    String newLocation1 = newLoc.getPath();
                    moveDbToAndroidData(oldLocation1, newLocation1);
                }
            } else {
                if (copyFileDbToNewLocation(oldLoc, newLoc)) {
                    deleteOldLoc(oldLoc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //delete folder and file
    public static void deleteOldLoc(File fileOrDirectory) {
        fileOrDirectory.delete();

        File dir = new File(fileOrDirectory.getAbsolutePath().replaceAll(fileOrDirectory.getName(), ""));
        if (dir.isDirectory()) {
            dir.delete();
        }
    }

    public static boolean copyFileDbToNewLocation(File sourceFile, File destFile) throws IOException {
        //no need for return value so mkdirs is ignored
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();
        //no need for return value so mkdirs is ignored
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    public static synchronized DatabaseHandler getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHandler(context.getApplicationContext());
        } else {
            sInstance.createWritableDb();
        }
        return sInstance;
    }

    public void createWritableDb() {
        if (mDb == null) {
            mDb = getWritableDatabase(AA);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // execute create table
        db.execSQL(CREATE_LOG_ACTIVITY);
        db.execSQL(CREATE_LOG_PRINT);
        db.execSQL(CREATE_MST_ADJUSTMENT_TYPE);
        db.execSQL(CREATE_MST_BRAND_DEDICATED);
        db.execSQL(CREATE_MST_BRAND_PROG);
        db.execSQL(CREATE_MST_COST_ITEM);
        db.execSQL(CREATE_MST_COST_ITEM_MAP);
        db.execSQL(CREATE_MST_HIS_CALLSHEET);
        db.execSQL(CREATE_MST_MAP_POSM);
        db.execSQL(CREATE_MST_NON_VISIBLE);
        db.execSQL(CREATE_MST_NOTATION);
        db.execSQL(CREATE_MST_OUTLET);
        db.execSQL(CREATE_MST_OUTLET_TYPE);
        db.execSQL(CREATE_MST_PARAM);
        db.execSQL(CREATE_MST_PARAM_GLOBAL);
        db.execSQL(CREATE_MST_PARAMETER_VERSION);
        db.execSQL(CREATE_MST_PP_PROGRAM);
        db.execSQL(CREATE_MST_PRODUCT);
        db.execSQL(CREATE_MST_PRODUCT_SURVEY);
        db.execSQL(CREATE_MST_REASON_BARCODE);
        db.execSQL(CREATE_MST_SALES_TYPE);
        db.execSQL(CREATE_MST_SALESMAN);
        db.execSQL(CREATE_MST_SALESMAN_ROUTE);
        db.execSQL(CREATE_MST_STATUS_OUTLET);
        db.execSQL(CREATE_MST_TARGET);
        db.execSQL(CREATE_MST_VEHICLE);
        db.execSQL(CREATE_MST_VISIBILITY_DOMINAN);
        db.execSQL(CREATE_MST_OUTLET_DEDICATED); //#CR OUTLET DEDICATED
        db.execSQL(CREATE_MST_VILLAGE); //#CR ADD NEW OUTLET

        /* CR HH Retail - 25 Mei 2021
           1. Maintenance Unit
           Developed by yufrim
        */
        db.execSQL(CREATE_MST_OUTLET_INVEST);
        db.execSQL(CREATE_MST_ITEM_SUBCATEGORY);
        db.execSQL(CREATE_MST_ITEM_GROUP);
        db.execSQL(CREATE_TR_MAINTENANCE);
        db.execSQL(CREATE_TR_PHOTO_MAINTENANCE);
        db.execSQL(CREATE_MST_HIS_MAINTENANCE);

        db.execSQL(CREATE_START_END_ROUTE);
        db.execSQL(CREATE_TEMP_NOTATION);
        db.execSQL(CREATE_TR_ADJUST_TKRGLG);
        db.execSQL(CREATE_TR_BPPM);
        db.execSQL(CREATE_TR_BPPM_ALOCATION);
        db.execSQL(CREATE_TR_BPPM_DTL);
        db.execSQL(CREATE_TR_BPPR);
        db.execSQL(CREATE_TR_BPPU);
        db.execSQL(CREATE_TR_BPPU_ALOCATION);
        db.execSQL(CREATE_TR_COMPENSATION);
        db.execSQL(CREATE_TR_COST_REALIZATION);
        db.execSQL(CREATE_TR_DOWNLOAD);
        db.execSQL(CREATE_TR_DURATION_NOTES);
        db.execSQL(CREATE_TR_INBOX);
        db.execSQL(CREATE_TR_NONPP_PROG_EXEC);
        db.execSQL(CREATE_TR_PP_PROG_EXEC);
        db.execSQL(CREATE_TR_NOTATION);
        db.execSQL(CREATE_TR_NOTES_ROUTE);
        db.execSQL(CREATE_TR_POSM);
        db.execSQL(CREATE_TR_RETUR_RECAP);
        db.execSQL(CREATE_TR_SALES);
        db.execSQL(CREATE_TR_SALES_PAYMENT);
        db.execSQL(CREATE_TR_SALES_PRINT);
        db.execSQL(CREATE_TR_SALES_TEMP);
        db.execSQL(CREATE_TR_STOCK_ROKOK);
        db.execSQL(CREATE_TR_SVY_VOLUME);
        db.execSQL(CREATE_TR_TOPPING_UP);
        db.execSQL(CREATE_TR_UNIT_SUPPORT);
        db.execSQL(CREATE_TR_VISDOM_NONVIS);
        db.execSQL(CREATE_TWEEK);
        db.execSQL(CREATE_TR_PROG_PHOTO); //#CR ADD NEW OUTLET

        // create index for MST_OUTLET
        db.execSQL(CREATE_INDEX_MST_OUTLET);

        /**
         * @author gerwinh
         * WMS HH HELPER 2022
         * 25 Jan 2022
         * TR_PRODUCT_PRICE
         * TR_VEHICLE_INFO // Update 3 Feb 2022 - Rename to TR_BPPR_DELIVER_ORDER
         */
        db.execSQL(CREATE_MST_PRODUCT_PRICE);
        db.execSQL(CREATE_TR_BPPR_DELIVER_ORDER);
        /**
         * @author gerwinh
         * WMS HH HELPER 2022
         * 03 Feb 2022
         * TR_BPPR_BAD_STOCK
         */
        db.execSQL(CREATE_TR_BPPR_BAD_STOCK);

        /**
         * @author jeremiag
         *
         */
        db.execSQL(CREATE_HISTORY_UPLOAD);

        /**
         * @author khalida
         * WMS HH HELPER 2022
         * **/
        db.execSQL(CREATE_MST_ACTUAL_UNIT_SUPPORT);
        db.execSQL(CREATE_TR_PU_HDR);
        db.execSQL(CREATE_TR_PU_DTL);
        db.execSQL(CREATE_TR_PU_ALLOCATION);
        db.execSQL(CREATE_MST_ACCT_TOPUP_TKRGLG);
        db.execSQL(CREATE_DB_TRACKING);
        insertDbTracking(db, DATABASE_VERSION, DATABASE_VERSION, VERSION_NAME, true);

        /***
         * @author khalida
         * WMS HH HELPER 2022 AFTER PILOTING
         * */
        db.execSQL(CREATE_TR_BPPR_HIST);
        db.execSQL(CREATE_TR_BPPR_BAD_STOCK_HIST);
        db.execSQL(CREATE_TR_BPPR_DELIVER_ORDER_HIST);
        db.execSQL(CREATE_TR_STOCK_ROKOK_HIST);
        db.execSQL(CREATE_TR_ADJUST_TKRGLG_HIST);
        db.execSQL(CREATE_TR_TOPPING_UP_HIST);
        db.execSQL(CREATE_TR_BPPM_HIST);
        db.execSQL(CREATE_TR_BPPM_DTL_HIST);
        db.execSQL(CREATE_TR_BPPM_ALOCATION_HIST);
        db.execSQL(CREATE_TR_PU_HDR_HIST);
        db.execSQL(CREATE_TR_PU_DTL_HIST);
        db.execSQL(CREATE_TR_PU_ALLOCATION_HIST);

        /**
         * @author JOSUAH
         * 18 Oct 2022
         * WMS CR (BPPM BPPR beda week)
         */
        db.execSQL(CREATE_TR_BPPM_ACV);
        db.execSQL(CREATE_TR_BPPM_ACV_HIST);
        db.execSQL(CREATE_TR_PU_ACV);
        db.execSQL(CREATE_TR_PU_ACV_HIST);

        db.execSQL(CREATE_TR_PP_PROG_EXEC_TEMP);
        db.execSQL(CREATE_TR_NONPP_PROG_EXEC_TEMP);
        db.execSQL(CREATE_TR_UNIT_SUPPORT_TEMP);
        db.execSQL(CREATE_TR_COMPENSATION_TEMP);

        /**
         * @author hafizhr
         * 10 AUG 2022
         * TANDA TERIMA OUTLET
         */
        db.execSQL(CREATE_MST_OUTLET_OWNER);
        db.execSQL(CREATE_MST_OI_PKS);
        db.execSQL(CREATE_MST_OI_PKS_DTL);
        db.execSQL(CREATE_TR_TTO_HDR);
        db.execSQL(CREATE_TR_TTO_RECEIVER);
        db.execSQL(CREATE_TR_TTO_CONTRACT);
        db.execSQL(CREATE_TR_TTO_PRODUCT_DISPLAY);
        db.execSQL(CREATE_TR_TTO_UNIT_DISPLAY);
        db.execSQL(CREATE_TR_COMPENSATION_HIST);
        db.execSQL(CREATE_TR_NONPP_PROG_EXEC_HIST);
        db.execSQL(CREATE_TR_UNIT_SUPPORT_HIST);
        db.execSQL(CREATE_TR_TTO_SIGNATURE);
        //CR TANDA TERIMA
        db.execSQL(CREATE_MST_CONTRACT);
        db.execSQL(CREATE_MST_TTO_RECEIVER);
        db.execSQL(CREATE_MST_PRODUCT_DISPLAY);
        db.execSQL(CREATE_MST_UNIT_DISPLAY);
        db.execSQL(CREATE_MST_TTO_PRODUCT);
        db.execSQL(CREATE_MST_TTO_ITEM_SUBCATEGORY);


        // AR TTO SEPT 2022 edited by lukkis 10 OKT 2022
        db.execSQL(CREATE_MST_TTO_MAX_AMP);

        /**
         * Add by Novalm 12-okt-2022
         * AR TTO September 2022
         * - TR_OUTLET_OWNER
         * - TR_OUTLET_PKP
         */
        db.execSQL(CREATE_TR_OUTLET_OWNER);
        db.execSQL(CREATE_TR_OUTLET_PKP);

        /**
         * Add by bayus05 24-Mar-2023
         * - MST_PRODUCT_ACT_COMP
         * - MST_DATA_VERSION
         * - MST_IMPACT_PARAMETER
         * - MST_PROGRAM_IMPACT
         * - CS_TOTAL_SPACE_SHARE
         * - MST_TPBP
         * - MST_PROGRAM_MAP
         * - MST_PROGRAM_UNIT
         * - MST_OUTDOOR
         * - MST_STREET_VISIBILITY
         * - MST_MANUF_SUBGROUP
         * - MST_BRAND_GROUP
         * - TR_NEW_OUTDOOR
         * - TR_CHECK_OUTDOOR
         * - TR_CHECK_STREET_VISIBILITY
         * - TR_COMP_EVENT
         * - TR_ACT_PROD_LAUNCHING
         * - TR_NEW_STREET_VISIBILITY
         * - TR_COMP_EVENT_DTL
         * - TR_COMP_EVENT_PHOTOS
         * - TR_COMP_PROG_FEE
         * - TR_COMP_PROG_FEE_UNIT
         * - TR_COMP_PROG_CIG
         * - TR_COMP_PROG_IMPACT
         * - TR_COMP_PROG
         * - TR_COMP_PROG_UPLINE
         * - TR_COMP_PROG_VAO
         * - CS_COMP_PROG_UNIT
         * - MST_CONSUMER_MATRIX
         * - MST_PRODUCT_ALL
         * - MST_COUNTY
         * - MST_SUBDISTRICT
         * - TR_CONSUMER_CONTACT
         * - CS_COMP_PROG_IMPACT
         * - CS_COMP_PROG
         * - CS_COMP_PROG_UPLINE
         * - CS_COMP_PROG_FEE
         * - CS_COMP_PROG_FEE_UNIT
         * - CS_COMP_PROG_CIG
         * - CS_COMP_PROG_VAO
         * - CS_COMP_PROG_UNIT
         * - CS_COMP_PROG_TIME
         * - TR_COMP_PROG_IMPACT_TEMP
         * - TR_COMP_PROG_TEMP
         * - TR_COMP_PROG_UPLINE_TEMP
         * - TR_COMP_PROG_FEE_TEMP
         * - TR_COMP_PROG_FEE_UNIT_TEMP
         * - TR_COMP_PROG_CIG_TEMP
         * - TR_COMP_PROG_VAO_TEMP
         * - TR_COMP_PROG_UNIT_TEMP
         * - TR_COMP_PROG_TIME_TEMP
         */
        db.execSQL(CREATE_MST_DATA_VERSION);
        db.execSQL(CREATE_MST_IMPACT_PARAMETER);
        db.execSQL(CREATE_MST_PROGRAM_IMPACT);
        db.execSQL(CREATE_CS_TOTAL_SPACE_SHARE);
        db.execSQL(CREATE_MST_TPBP);
        db.execSQL(CREATE_MST_PROGRAM_MAP);
        db.execSQL(CREATE_MST_PROGRAM_UNIT);
        db.execSQL(CREATE_MST_OUTDOOR);
        db.execSQL(CREATE_MST_STREET_VISIBILITY);
        db.execSQL(CREATE_MST_PRODUCT_ACT_COMP);
        db.execSQL(CREATE_MST_MANUF_SUBGROUP);
        db.execSQL(CREATE_MST_BRAND_GROUP);
        db.execSQL(CREATE_TR_NEW_OUTDOOR);
        db.execSQL(CREATE_TR_CHECK_OUTDOOR);
        db.execSQL(CREATE_TR_CHECK_STREET_VISIBILITY);
        db.execSQL(CREATE_TR_COMP_EVENT);
        db.execSQL(CREATE_TR_ACT_PROD_LAUNCHING);
        db.execSQL(CREATE_TR_NEW_STREET_VISIBILITY);
        db.execSQL(CREATE_TR_COMP_EVENT_DTL);
        db.execSQL(CREATE_TR_COMP_EVENT_PHOTOS);
        db.execSQL(CREATE_TR_COMP_PROG_FEE);
        db.execSQL(CREATE_TR_COMP_PROG_FEE_UNIT);
        db.execSQL(CREATE_TR_COMP_PROG_CIG);
        db.execSQL(CREATE_TR_COMP_PROG_IMPACT);
        db.execSQL(CREATE_TR_COMP_PROG);
        db.execSQL(CREATE_TR_COMP_PROG_UPLINE);
        db.execSQL(CREATE_TR_COMP_PROG_VAO);
        db.execSQL(CREATE_TR_COMP_PROG_UNIT);
        db.execSQL(CREATE_TR_COMP_PROG_TIME);
        db.execSQL(CREATE_TR_TOTAL_SPACE_SHARE);
        db.execSQL(CREATE_MST_CONSUMER_MATRIX);
        db.execSQL(CREATE_MST_PRODUCT_ALL);
        db.execSQL(CREATE_MST_COUNTY);
        db.execSQL(CREATE_MST_SUBDISTRICT);
        db.execSQL(CREATE_TR_CONSUMER_CONTACT);
        db.execSQL(CREATE_CS_COMP_PROG_IMPACT);
        db.execSQL(CREATE_CS_COMP_PROG);
        db.execSQL(CREATE_CS_COMP_PROG_UPLINE);
        db.execSQL(CREATE_CS_COMP_PROG_FEE);
        db.execSQL(CREATE_CS_COMP_PROG_FEE_UNIT);
        db.execSQL(CREATE_CS_COMP_PROG_CIG);
        db.execSQL(CREATE_CS_COMP_PROG_VAO);
        db.execSQL(CREATE_CS_COMP_PROG_UNIT);
        db.execSQL(CREATE_CS_COMP_PROG_TIME);
        db.execSQL(CREATE_MST_ITEM_SUBCATEGORY_ALIAS);
        db.execSQL(CREATE_TR_COMP_PROG_IMPACT_TEMP);
        db.execSQL(CREATE_TR_COMP_PROG_TEMP);
        db.execSQL(CREATE_TR_COMP_PROG_UPLINE_TEMP);
        db.execSQL(CREATE_TR_COMP_PROG_FEE_TEMP);
        db.execSQL(CREATE_TR_COMP_PROG_FEE_UNIT_TEMP);
        db.execSQL(CREATE_TR_COMP_PROG_CIG_TEMP);
        db.execSQL(CREATE_TR_COMP_PROG_VAO_TEMP);
        db.execSQL(CREATE_TR_COMP_PROG_UNIT_TEMP);
        db.execSQL(CREATE_TR_COMP_PROG_TIME_TEMP);
        db.execSQL(CREATE_MST_ATTACHMENT_MAPPING);


        /*
         * Add by lukkis 21-OKT-2022
         * AR TTO SEPT 2022
         * */
        db.execSQL(CREATE_TR_TTO_DTL);

        /**
         * Add by hafizhr
         * 03-MAR-2023
         * CR AFTER PILOTING CYCLE 2
         * */
        db.execSQL(CREATE_TR_PP_PROG_EXEC_LOG);
        db.execSQL(CREATE_TR_PROG_PHOTO_LOG);
        db.execSQL(CREATE_TR_COMPENSATION_LOG);
        db.execSQL(CREATE_TR_UNIT_SUPPORT_LOG);
        db.execSQL(CREATE_TR_TTO_HDR_LOG);
        db.execSQL(CREATE_TR_TTO_CONTRACT_LOG);
        db.execSQL(CREATE_TR_TTO_PRODUCT_DISPLAY_LOG);
        db.execSQL(CREATE_TR_TTO_UNIT_DISPLAY_LOG);
        db.execSQL(CREATE_TR_TTO_DTL_LOG);
        db.execSQL(CREATE_TR_TTO_SIGNATURE_LOG);
        db.execSQL(CREATE_TR_TTO_RECEIVER_LOG);
        db.execSQL(CREATE_TR_OUTLET_OWNER_LOG);
        db.execSQL(CREATE_TR_OUTLET_PKP_LOG);
        db.execSQL(CREATE_TR_TTO_LOG);

        /**
         * @author asas
         * 14-JUL-2023
         * NIRWANA - MAINTENANCE UNIT
         */
        db.execSQL(CREATE_TR_ADDT_UNIT);

        /**
         * @modified by novalm 28/03/23
         * CR TTO Cycle 4
         */
        db.execSQL(CREATE_TTO_HDR_REKAP);
        db.execSQL(CREATE_TTO_DTL_REKAP);
        db.execSQL(CREATE_TTO_KLAUSUL_REKAP);
        db.execSQL(CREATE_TTO_DISPLAY_REKAP);
        db.execSQL(CREATE_TTO_BARANG_REKAP);
        db.execSQL(CREATE_MST_OUTLET_OWNER_LOG);

        /**
         * Created by michells
         * CR Survey Volume 2023 - 2023-04-14
         */
        db.execSQL(CREATE_TABLE_MST_ECOMMERCE);
        db.execSQL(CREATE_TABLE_MST_OUTLET_ALIAS);
        db.execSQL(CREATE_TABLE_MST_OUTLET_UPLINE);
        db.execSQL(CREATE_TABLE_TR_UPLINE);

        /**
         * @author noviantyn
         * 28 November 2023
         * CR KPI Retail
         */
        db.execSQL(CREATE_TR_FORCE_MAJEURE);
        db.execSQL(CREATE_TR_PHOTO_POSM);
        db.execSQL(CREATE_TR_PHOTO_VISDOM);
        db.execSQL(CREATE_MST_OUTLET_UP_INFO);

        /**
         * @author jeremiag
         * 9 Februari 2024
         * CR NIRWANA PIL 5
         */
        db.execSQL(CREATE_CS_ACT_COMP_CHECK_OUTDOOR);

        /**
         * Add by bayus05 06-Feb-2024
         * CR LUAR CYCLE
         */
        db.execSQL(CREATE_MST_CALLCYCLE);
        db.execSQL(CREATE_TR_VISIT_TYPE);

        /**
         * @author dimass02
         * 1 Feb 2024
         * AE BPPR Stockiest
         * */
        db.execSQL(CREATE_TR_BPPR_STOCKIEST);
        db.execSQL(CREATE_TR_STOCK_ROKOK_STOCKIEST);

        /**
         * @author rizkik01
         * 1 Februari 2024
         * AE - TANDA TERIMA OUTLET MANDATORY KTP
         */
        db.execSQL(CREATE_MST_MAPPING_NON_MDTRY);

        /**
         * @author josuah
         * 5 Mar 2024
         * CR Cashless Payment
         * */
        db.execSQL(CREATE_MST_VIRTUAL_ACCOUNT);
        db.execSQL(CREATE_MST_PAYMENT_MAP);
        db.execSQL(CREATE_TR_SALES_PAYMENT_LOG);

        /**
         * @author jeremiag
         * 4 Maret 2024
         */
        db.execSQL(CREATE_MST_PP_PROGRAM_DTL);

        /**
         * @author noviantyn
         * 31 Januari 2024
         * CR KPI Retail Phase 2
         */
        db.execSQL(CREATE_KPI_MST_PARAMETER);
        db.execSQL(CREATE_KPI_MST_JENISOTL_PARAM);
        db.execSQL(CREATE_TR_ACHIEVEMENT_HDR);
        db.execSQL(CREATE_TR_ACHIEVEMENT_DTL);
        db.execSQL(CREATE_TR_ACHIEVEMENT_DTL_OTL);
        db.execSQL(CREATE_TR_RECAP_PRODUCT_SALES);


        /**
         * CR TTO 2024
         * @author yp03
         * 16 Feb 2024
         */
        db.execSQL(CREATE_TR_TTO_RECEIVER_BANK);
        db.execSQL(CREATE_TR_PP_ALLOCATED_TRF);
        db.execSQL(CREATE_LAST_CALL_AMP);
        db.execSQL(CREATE_MST_TTO_RECEIVER_BANK);
        db.execSQL(CREATE_MST_OUTLET_BANK);
        db.execSQL(CREATE_MST_BANK);
        db.execSQL(CREATE_MST_TTO_REV_FIELD_MAP);
        db.execSQL(CREATE_TR_OUTLET_BANK);
        db.execSQL(CREATE_CS_SPV_OUTLET_REV);
        db.execSQL(CREATE_CS_SPV_PROG_PP_REV);
        db.execSQL(CREATE_CS_SPV_EXEC_PP_REV);
        db.execSQL(CREATE_CS_SPV_EXEC_GIFT_REV);
        db.execSQL(CREATE_CS_SPV_EXEC_GIFT_ITEM_REV);
        db.execSQL(CREATE_CS_TTO_HDR_REV);
        db.execSQL(CREATE_CS_TTO_RECEIVER_REV);
        db.execSQL(CREATE_CS_TTO_SIGNATURE_REV);
        db.execSQL(CREATE_CS_TTO_CONTRACT_REV);
        db.execSQL(CREATE_CS_TTO_EXEC_GIFT_ITEM_REV);
        db.execSQL(CREATE_CS_TTO_NOTIF_REV);
        db.execSQL(CREATE_CS_TTO_RECEIVER_BANK_REV);
        db.execSQL(CREATE_CS_TTO_UNIT_DISPLAY_REV);
        db.execSQL(CREATE_CS_TTO_PRODUCT_DISPLAY_REV);
        db.execSQL(CREATE_TR_DELIVER_ORDER_DTL_LOG);
        db.execSQL(CREATE_TR_DELIVER_ORDER_LOG);
        db.execSQL(CREATE_TR_TTO_RECEIVER_BANK_LOG);
        db.execSQL(CREATE_MST_TTO_RECEIVER_BANK_LOG);
        db.execSQL(CREATE_MST_TTO_RECEIVER_LOG);
        db.execSQL(CREATE_MST_OUTLET_BANK_LOG);
        db.execSQL(CREATE_TR_OUTLET_BANK_LOG);
        db.execSQL(CREATE_CS_SPV_OUTLET_REV_LOG);
        db.execSQL(CREATE_CS_SPV_PROG_PP_REV_LOG);
        db.execSQL(CREATE_CS_SPV_EXEC_PP_REV_LOG);
        db.execSQL(CREATE_CS_SPV_EXEC_GIFT_REV_LOG);
        db.execSQL(CREATE_CS_SPV_EXEC_GIFT_ITEM_REV_LOG);
        db.execSQL(CREATE_CS_TTO_RECEIVER_REV_LOG);
        db.execSQL(CREATE_CS_TTO_HDR_REV_LOG);
        db.execSQL(CREATE_CS_TTO_EXEC_GIFT_ITEM_REV_LOG);
        db.execSQL(CREATE_CS_TTO_RECEIVER_BANK_REV_LOG);
        db.execSQL(CREATE_CS_TTO_CONTRACT_REV_LOG);
        db.execSQL(CREATE_CS_TTO_SIGNATURE_REV_LOG);
        db.execSQL(CREATE_CS_TTO_UNIT_DISPLAY_REV_LOG);
        db.execSQL(CREATE_CS_TTO_PRODUCT_DISPLAY_REV_LOG);
        db.execSQL(CREATE_MST_CONTRACT_REV);
        db.execSQL(CREATE_MST_CONTRACT_STEP_REV);
        db.execSQL(CREATE_MST_PRODUCT_DISPLAY_REV);
        db.execSQL(CREATE_MST_UNIT_DISPLAY_REV);
        db.execSQL(CREATE_TR_SALES_PHOTO);

        /**
         * CR CASHLESS PAYMENT
         * JOSUAH
         * 13 JUNE 2024
         */
        db.execSQL(CREATE_TR_PAYMENT_PHOTO);

        /**
         * @JEREMIAG
         * 22 NOVEMBER 2024
         * AE VALIDASI ACOM
         */
        db.execSQL(CREATE_TR_COMP_PROG_CHECKLIST);

        /**
         * @author dandyr01
         * 21-Nov-2024
         * CR BPPM BPPR MULTICOV
         */
        db.execSQL(CREATE_TR_JOURNAL);
        db.execSQL(CREATE_TR_PRIMARY_APP);
        db.execSQL(CREATE_MST_SALESMAN_MULTICOV);

        /**
         * @author noviantyn
         * 17 Januari 2025
         * CR KPI Retail V1
         * [CR V1 BPPM ONLINE - COD] MOBILE - BPPM - BPPM - Close BPPM - #106459
         */
        db.execSQL(CREATE_TR_JOURNAL_HIST);

        /**
         * @author josuah
         * 4 June 2025
         * AE - Informasi Bidang Promosi
         */
        db.execSQL(CREATE_MST_OUTLET_SHOPBLIND_DTL);

        // insert HARDCODED PARAM
        getPredefine(new GsonBuilder().create().fromJson(loadJSONFromAsset(), PredefineData.class), db);
    }
}