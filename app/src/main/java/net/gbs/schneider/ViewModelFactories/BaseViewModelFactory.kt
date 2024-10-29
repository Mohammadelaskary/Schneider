package net.gbs.schneider.ViewModelFactories

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.gbs.schneider.Ui.Audit.AuditOrdersList.AuditOrdersListViewModel
import net.gbs.schneider.Ui.Audit.StartAudit.StartAuditViewModel

import net.gbs.schneider.Receiving.PutAway.StartPutAway.StartPutAwayPOVendorViewModel
import net.gbs.schneider.Return.Receive.InvoiceList.PutAwayInvoiceListReturnViewModel
import net.gbs.schneider.Ui.Return.PutAway.StartPutAwayReturn.StartPutAwayReturnViewModel
import net.gbs.schneider.Ui.Return.Receive.InvoiceList.InvoiceListReturnViewModel
import net.gbs.schneider.Ui.Return.Receive.StartReturn.StartReturnViewModel
import net.gbs.schneider.SignIn.SignInViewModel
import net.gbs.schneider.Ui.GetBinInfo.BinInfoViewModel
import net.gbs.schneider.Ui.GetItemInfo.ItemInfoViewModel
import net.gbs.schneider.Ui.GetSerialInfo.SerialInfoViewModel
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateIssue.IntermediateIssueWorkOrdersList.IntermediateIssueWorkOrdersListViewModel
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateIssue.IntermediateStartIssue.IntermediateStartIssueViewModel
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediatePutAwayFragments.IntermediateDetailedPutAway.IntermediateDetailedPutAwayViewModel
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediatePutAwayFragments.IntermediateFastPutAway.IntermediateFastPutAwayViewModel
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediatePutAwayFragments.IntermediatePutAwayWorkOrdersList.PutAwayWorkOrderListViewModel
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediateReceivingFragments.IntermediateReceivingStartReceiving.IntermediateStartReceivingViewModel
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediateReceivingFragments.IntermediateReceivingWorkOrdersList.ReceivingWorkOrderListViewModel
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReturn.IntermediatePutAwayReturn.IntermediatePutAwayReturnWorkOrdersList.IntermediatePutAwayReturnWorkOrdersListViewModel
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReturn.IntermediatePutAwayReturn.IntermediateReturnStartPutAway.IntermediateReturnStartPutAwayViewModel
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReturn.IntermediateReceiveReturn.IntermediateReceiveReturnWorkOrdersList.IntermediateReceiveReturnWorkOrdersListViewModel
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReturn.IntermediateReceiveReturn.IntermediateReturnStartReceiving.IntermediateReturnStartReceivingViewModel
import net.gbs.schneider.Ui.Issue.StartIssuing.StartIssuingViewModel
import net.gbs.schneider.WorkOrderDetailsList.WorkOrderDetailsListViewModel
import net.gbs.schneider.Ui.Issue.WorkOrderList.WorkOrderListViewModel
import net.gbs.schneider.Ui.POPlant.Receiving.ChangeSerials.ChangeQty.StartChangingSerialsViewModel.StartChangingQtyViewModel
import net.gbs.schneider.Ui.POPlant.Receiving.ChangeSerials.PoPlantInvoicesList.ChangeSerialsPOInvoiceListViewModel
import net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.POInvoiceListViewModel
import net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.StartChangingSerialsViewModel.StartChangingSerialsViewModel
import net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.StartReceivingViewModel
import net.gbs.schneider.Ui.POPlant.Receiving.PutAway.FastPutAway.FastPutAwayViewModel
import net.gbs.schneider.Ui.POPlant.Receiving.PutAway.InvoiceListPutAway.InvoiceListPutAwayViewModel
import net.gbs.schneider.Ui.POPlant.Receiving.PutAway.StartPutAway.StartPutAwayViewModel
import net.gbs.schneider.Ui.POVendor.Receiving.PoVendorList.POVendorListPutAwayViewModel
import net.gbs.schneider.Ui.POVendor.Receiving.PoVendorList.POVendorListViewModel
import net.gbs.schneider.Ui.POVendor.Receiving.PoVendorStartReceiving.POVendorStartReceivingViewModel
import java.lang.IllegalArgumentException

class BaseViewModelFactory(val application: Application,val activity: Activity): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(POInvoiceListViewModel::class.java))
            return POInvoiceListViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(SignInViewModel::class.java))
            return SignInViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(StartReceivingViewModel::class.java))
            return StartReceivingViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(WorkOrderDetailsListViewModel::class.java))
            return WorkOrderDetailsListViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(WorkOrderListViewModel::class.java))
            return WorkOrderListViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(StartIssuingViewModel::class.java))
            return StartIssuingViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(InvoiceListPutAwayViewModel::class.java))
            return InvoiceListPutAwayViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(StartPutAwayViewModel::class.java))
            return  StartPutAwayViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(InvoiceListReturnViewModel::class.java))
            return InvoiceListReturnViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(StartReturnViewModel::class.java))
            return StartReturnViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(AuditOrdersListViewModel::class.java))
            return AuditOrdersListViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(StartAuditViewModel::class.java))
            return StartAuditViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(POVendorListViewModel::class.java))
            return POVendorListViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(POVendorStartReceivingViewModel::class.java))
            return POVendorStartReceivingViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(StartPutAwayReturnViewModel::class.java))
            return StartPutAwayReturnViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(POVendorListPutAwayViewModel::class.java))
            return POVendorListPutAwayViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(StartPutAwayPOVendorViewModel::class.java))
            return StartPutAwayPOVendorViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(BinInfoViewModel::class.java))
            return BinInfoViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(ItemInfoViewModel::class.java))
            return ItemInfoViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(PutAwayInvoiceListReturnViewModel::class.java))
            return PutAwayInvoiceListReturnViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(ChangeSerialsPOInvoiceListViewModel::class.java))
            return ChangeSerialsPOInvoiceListViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(StartChangingSerialsViewModel::class.java))
            return StartChangingSerialsViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(StartChangingQtyViewModel::class.java))
            return StartChangingQtyViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(FastPutAwayViewModel::class.java))
            return FastPutAwayViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(SerialInfoViewModel::class.java))
            return SerialInfoViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(PutAwayWorkOrderListViewModel::class.java))
            return PutAwayWorkOrderListViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(IntermediateStartReceivingViewModel::class.java))
            return IntermediateStartReceivingViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(IntermediateIssueWorkOrdersListViewModel::class.java))
            return IntermediateIssueWorkOrdersListViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(IntermediateStartIssueViewModel::class.java))
            return IntermediateStartIssueViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(IntermediateDetailedPutAwayViewModel::class.java))
            return IntermediateDetailedPutAwayViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(IntermediateFastPutAwayViewModel::class.java))
            return IntermediateFastPutAwayViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(PutAwayWorkOrderListViewModel::class.java))
            return PutAwayWorkOrderListViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(IntermediateStartReceivingViewModel::class.java))
            return IntermediateStartReceivingViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(IntermediatePutAwayReturnWorkOrdersListViewModel::class.java))
            return IntermediatePutAwayReturnWorkOrdersListViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(ReceivingWorkOrderListViewModel::class.java))
            return ReceivingWorkOrderListViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(IntermediateReceiveReturnWorkOrdersListViewModel::class.java))
            return IntermediateReceiveReturnWorkOrdersListViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(IntermediateReturnStartReceivingViewModel::class.java))
            return IntermediateReturnStartReceivingViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(IntermediateReturnStartPutAwayViewModel::class.java))
            return IntermediateReturnStartPutAwayViewModel(application,activity) as T
        throw IllegalArgumentException("View model not found")
    }
}