/**
 * 
 */
package pagecode;

import jakarta.faces.component.html.HtmlOutputText;
import jakarta.faces.component.UIColumn;
import jakarta.faces.component.html.HtmlDataTable;
import jakarta.faces.component.html.HtmlPanelGrid;
import jakarta.faces.component.html.HtmlPanelGroup;

/**
 * @author mkerr
 *
 */
public class NewOrderResults extends PageCodeBase {

	protected HtmlOutputText NewO;
	protected HtmlOutputText tes;
	protected UIColumn column1;
	protected HtmlOutputText text2;
	protected HtmlOutputText text3;
	protected HtmlOutputText text4;
	protected HtmlOutputText text5;
	protected HtmlOutputText text6;
	protected HtmlOutputText text7;
	protected HtmlDataTable table1;
	protected HtmlPanelGrid NewOrderInputGrid;
	protected HtmlPanelGroup newOrderGroup;
	protected HtmlOutputText newOrderDetails;
	protected HtmlPanelGroup customerGroup;
	protected HtmlOutputText customerId;
	protected HtmlPanelGroup customerNameGroup;
	protected HtmlOutputText customerName;
	protected HtmlOutputText outCustomerLastName;
	protected HtmlPanelGroup customerCreditGroup;
	protected HtmlOutputText customerCredit;
	protected HtmlOutputText outCustomerCredit;
	protected HtmlPanelGroup customerDiscountGroup;
	protected HtmlOutputText customerDiscount;
	protected HtmlOutputText outCustomerDiscount;
	protected HtmlPanelGroup orderIdGroup;
	protected HtmlOutputText orderId;
	protected HtmlOutputText outOrderId;
	protected HtmlPanelGroup orderDateGroup;
	protected HtmlOutputText orderEntryDate;
	protected HtmlOutputText outOrderEntryDate;
	protected HtmlPanelGroup orderItemCountGroup;
	protected HtmlOutputText outOrderItemCount;
	protected HtmlPanelGroup warehouseGroup;
	protected HtmlOutputText warehouseId;
	protected HtmlOutputText outWarehouseId;
	protected HtmlPanelGroup warehouseTaxGroup;
	protected HtmlOutputText warehouseTax;
	protected HtmlOutputText outWarehouseTax;
	protected HtmlPanelGroup districtGroup;
	protected HtmlOutputText districtId;
	protected HtmlOutputText outDistrictId;
	protected HtmlPanelGroup districtTaxGroup;
	protected HtmlOutputText districtTax;
	protected HtmlOutputText outDistrictTax;
	protected HtmlPanelGroup orderlineGroup;
	protected HtmlOutputText orderlineDetails;
	protected HtmlPanelGroup itemId1Group;
	protected HtmlOutputText itemId1Output;
	protected HtmlOutputText itemId1;
	protected HtmlPanelGroup itemName1Group;
	protected HtmlOutputText itemName10Output;
	protected HtmlOutputText itemName1;
	protected HtmlPanelGroup quantity1Group;
	protected HtmlOutputText itemQuantity1Output;
	protected HtmlOutputText itemQuantity1;
	protected HtmlPanelGroup itemSupplyWarehouseId1Group;
	protected HtmlOutputText itemSupplyWarehouseId1Output;
	protected HtmlOutputText itemSupplyWarehouseId1;
	protected HtmlPanelGroup itemStockQuantity1Group;
	protected HtmlOutputText itemStockQuantity1Output;
	protected HtmlOutputText itemStockQuantity9;
	protected HtmlPanelGroup itemPrice1Group;
	protected HtmlOutputText itemPrice1Output;
	protected HtmlOutputText itemPrice1;
	protected HtmlPanelGroup itemTotal1Group;
	protected HtmlOutputText itemTotal1Output;
	protected HtmlOutputText itemTotal1;
	protected HtmlPanelGroup itemId2Group;
	protected HtmlOutputText itemId2Output;
	protected HtmlOutputText itemId2;
	protected HtmlPanelGroup itemName2Group;
	protected HtmlOutputText itemName2Output;
	protected HtmlOutputText itemName2;
	protected HtmlPanelGroup quantity2Group;
	protected HtmlOutputText itemQuantity2Output;
	protected HtmlOutputText itemQuantity2;
	protected HtmlPanelGroup itemSupplyWarehouseId2Group;
	protected HtmlOutputText itemSupplyWarehouseId2Output;
	protected HtmlOutputText itemSupplyWarehouseId2;
	protected HtmlPanelGroup itemStockQuantity2Group;
	protected HtmlOutputText itemStockQuantity2Output;
	protected HtmlOutputText itemStockQuantity2;
	protected HtmlPanelGroup itemPrice2Group;
	protected HtmlOutputText itemPrice2Output;
	protected HtmlOutputText itemPrice2;
	protected HtmlPanelGroup itemTotal2Group;
	protected HtmlOutputText itemTotal2Output;
	protected HtmlOutputText itemTotal2;
	protected HtmlPanelGroup itemId3Group;
	protected HtmlOutputText itemId3Output;
	protected HtmlOutputText itemId3;
	protected HtmlPanelGroup itemName3Group;
	protected HtmlOutputText itemName3Output;
	protected HtmlOutputText itemName3;
	protected HtmlPanelGroup quantity3Group;
	protected HtmlOutputText itemQuantity3Output;
	protected HtmlOutputText itemQuantity3;
	protected HtmlPanelGroup itemSupplyWarehouseId3Group;
	protected HtmlOutputText itemSupplyWarehouseId3Output;
	protected HtmlOutputText itemSupplyWarehouseId3;
	protected HtmlPanelGroup itemStockQuantity3Group;
	protected HtmlOutputText itemStockQuantity3Output;
	protected HtmlOutputText itemStockQuantity3;
	protected HtmlPanelGroup itemPrice3Group;
	protected HtmlOutputText itemPrice3Output;
	protected HtmlOutputText itemPrice3;
	protected HtmlPanelGroup itemTotal3Group;
	protected HtmlOutputText itemTotal3Output;
	protected HtmlOutputText itemTotal3;
	protected HtmlPanelGroup itemId4Group;
	protected HtmlOutputText itemId4Output;
	protected HtmlOutputText itemId4;
	protected HtmlPanelGroup itemName4Group;
	protected HtmlOutputText itemName4Output;
	protected HtmlOutputText itemName4;
	protected HtmlPanelGroup quantity4Group;
	protected HtmlOutputText itemQuantity4Output;
	protected HtmlOutputText itemQuantity4;
	protected HtmlPanelGroup itemSupplyWarehouseId4Group;
	protected HtmlOutputText itemSupplyWarehouseId4Output;
	protected HtmlOutputText itemSupplyWarehouseId4;
	protected HtmlPanelGroup itemStockQuantity4Group;
	protected HtmlOutputText itemStockQuantity4Output;
	protected HtmlOutputText itemStockQuantity4;
	protected HtmlPanelGroup itemPrice4Group;
	protected HtmlOutputText itemPrice4Output;
	protected HtmlOutputText itemPrice4;
	protected HtmlPanelGroup itemTotal4Group;
	protected HtmlOutputText itemTotal4Output;
	protected HtmlOutputText itemTotal4;
	protected HtmlPanelGroup itemId5Group;
	protected HtmlOutputText itemId5Output;
	protected HtmlOutputText itemId5;
	protected HtmlPanelGroup itemName5Group;
	protected HtmlOutputText itemName5Output;
	protected HtmlOutputText itemName5;
	protected HtmlPanelGroup quantity5Group;
	protected HtmlOutputText itemQuantity5Output;
	protected HtmlOutputText itemQuantity5;
	protected HtmlPanelGroup itemSupplyWarehouseId5Group;
	protected HtmlOutputText itemSupplyWarehouseId5Output;
	protected HtmlOutputText itemSupplyWarehouseId5;
	protected HtmlPanelGroup itemStockQuantity5Group;
	protected HtmlOutputText itemStockQuantity5Output;
	protected HtmlOutputText itemStockQuantity5;
	protected HtmlPanelGroup itemPrice5Group;
	protected HtmlOutputText itemPrice5Output;
	protected HtmlOutputText itemPrice5;
	protected HtmlPanelGroup itemTotal5Group;
	protected HtmlOutputText itemTotal5Output;
	protected HtmlOutputText itemTotal5;
	protected HtmlPanelGroup itemId6Group;
	protected HtmlOutputText itemId6Output;
	protected HtmlOutputText itemId6;
	protected HtmlPanelGroup itemName6Group;
	protected HtmlOutputText itemName6Output;
	protected HtmlOutputText itemName6;
	protected HtmlPanelGroup quantity6Group;
	protected HtmlOutputText itemQuantity6Output;
	protected HtmlOutputText itemQuantity6;
	protected HtmlPanelGroup itemSupplyWarehouseId6Group;
	protected HtmlOutputText itemSupplyWarehouseId6Output;
	protected HtmlOutputText itemSupplyWarehouseId6;
	protected HtmlPanelGroup itemStockQuantity6Group;
	protected HtmlOutputText itemStockQuantity6Output;
	protected HtmlOutputText itemStockQuantity6;
	protected HtmlPanelGroup itemPrice6Group;
	protected HtmlOutputText itemPrice6Output;
	protected HtmlOutputText itemPrice6;
	protected HtmlPanelGroup itemTotal6Group;
	protected HtmlOutputText itemTotal6Output;
	protected HtmlOutputText itemTotal6;
	protected HtmlPanelGroup itemId7Group;
	protected HtmlOutputText itemId7Output;
	protected HtmlOutputText itemId7;
	protected HtmlPanelGroup itemName7Group;
	protected HtmlOutputText itemName7Output;
	protected HtmlOutputText itemName7;
	protected HtmlPanelGroup quantity7Group;
	protected HtmlOutputText itemQuantity7Output;
	protected HtmlOutputText itemQuantity7;
	protected HtmlPanelGroup itemSupplyWarehouseId7Group;
	protected HtmlOutputText itemSupplyWarehouseId7Output;
	protected HtmlOutputText itemSupplyWarehouseId7;
	protected HtmlPanelGroup itemStockQuantity7Group;
	protected HtmlOutputText itemStockQuantity7Output;
	protected HtmlOutputText itemStockQuantity7;
	protected HtmlPanelGroup itemPrice7Group;
	protected HtmlOutputText itemPrice7Output;
	protected HtmlOutputText itemPrice7;
	protected HtmlPanelGroup itemTotal7Group;
	protected HtmlOutputText itemTotal7Output;
	protected HtmlOutputText itemTotal7;
	protected HtmlPanelGroup itemId8Group;
	protected HtmlOutputText itemId8Output;
	protected HtmlOutputText itemId8;
	protected HtmlPanelGroup itemName8Group;
	protected HtmlOutputText itemName8Output;
	protected HtmlOutputText itemName8;
	protected HtmlPanelGroup quantity8Group;
	protected HtmlOutputText itemQuantity8Output;
	protected HtmlOutputText itemQuantity8;
	protected HtmlPanelGroup itemSupplyWarehouseId8Group;
	protected HtmlOutputText itemSupplyWarehouseId8Output;
	protected HtmlOutputText itemSupplyWarehouseId8;
	protected HtmlPanelGroup itemStockQuantity8Group;
	protected HtmlOutputText itemStockQuantity8Output;
	protected HtmlOutputText itemStockQuantity8;
	protected HtmlPanelGroup itemPrice8Group;
	protected HtmlOutputText itemPrice8Output;
	protected HtmlOutputText itemPrice8;
	protected HtmlPanelGroup itemTotal8Group;
	protected HtmlOutputText itemTotal8Output;
	protected HtmlOutputText itemTotal8;
	protected HtmlPanelGroup itemId9Group;
	protected HtmlOutputText itemId9Output;
	protected HtmlOutputText itemId9;
	protected HtmlPanelGroup itemName9Group;
	protected HtmlOutputText itemName9Output;
	protected HtmlOutputText itemName9;
	protected HtmlPanelGroup quantity9Group;
	protected HtmlOutputText itemQuantity9Output;
	protected HtmlOutputText itemQuantity9;
	protected HtmlPanelGroup itemSupplyWarehouseId9Group;
	protected HtmlOutputText itemSupplyWarehouseId9Output;
	protected HtmlOutputText itemSupplyWarehouseId9;
	protected HtmlPanelGroup itemStockQuantity9Group;
	protected HtmlOutputText itemStockQuantity9Output;
	protected HtmlPanelGroup itemPrice9Group;
	protected HtmlOutputText itemPrice9Output;
	protected HtmlOutputText itemPrice9;
	protected HtmlPanelGroup itemTotal9Group;
	protected HtmlOutputText itemTotal9Output;
	protected HtmlOutputText itemTotal9;
	protected HtmlPanelGroup itemId10Group;
	protected HtmlOutputText itemId10Output;
	protected HtmlOutputText itemId10;
	protected HtmlPanelGroup itemName10Group;
	protected HtmlOutputText itemName10;
	protected HtmlPanelGroup quantity10Group;
	protected HtmlOutputText itemQuantity10Output;
	protected HtmlOutputText itemQuantity10;
	protected HtmlPanelGroup itemSupplyWarehouseId10Group;
	protected HtmlOutputText itemSupplyWarehouseId10Output;
	protected HtmlOutputText itemSupplyWarehouseId10;
	protected HtmlPanelGroup itemStockQuantity10Group;
	protected HtmlOutputText itemStockQuantity10Output;
	protected HtmlOutputText itemStockQuantity10;
	protected HtmlPanelGroup itemPrice10Group;
	protected HtmlOutputText itemPrice10Output;
	protected HtmlOutputText itemPrice10;
	protected HtmlPanelGroup itemTotal10Group;
	protected HtmlOutputText itemTotal10Output;
	protected HtmlOutputText itemTotal10;
	protected HtmlPanelGroup itemId11Group;
	protected HtmlOutputText itemId11Output;
	protected HtmlOutputText itemId11;
	protected HtmlPanelGroup itemName11Group;
	protected HtmlOutputText itemName11Output;
	protected HtmlOutputText itemName11;
	protected HtmlPanelGroup quantity11Group;
	protected HtmlOutputText itemQuantity11Output;
	protected HtmlOutputText itemQuantity11;
	protected HtmlPanelGroup itemSupplyWarehouseId11Group;
	protected HtmlOutputText itemSupplyWarehouseId11Output;
	protected HtmlOutputText itemSupplyWarehouseId11;
	protected HtmlPanelGroup itemStockQuantity11Group;
	protected HtmlOutputText itemStockQuantity11Output;
	protected HtmlOutputText itemStockQuantity11;
	protected HtmlPanelGroup itemPrice11Group;
	protected HtmlOutputText itemPrice11Output;
	protected HtmlOutputText itemPrice11;
	protected HtmlPanelGroup itemTotal11Group;
	protected HtmlOutputText itemTotal11Output;
	protected HtmlOutputText itemTotal11;
	protected HtmlPanelGroup itemId12Group;
	protected HtmlOutputText itemId12Output;
	protected HtmlOutputText itemId12;
	protected HtmlPanelGroup itemName12Group;
	protected HtmlOutputText itemName12Output;
	protected HtmlOutputText itemName12;
	protected HtmlPanelGroup quantity12Group;
	protected HtmlOutputText itemQuantity12Output;
	protected HtmlOutputText itemQuantity12;
	protected HtmlPanelGroup itemSupplyWarehouseId12Group;
	protected HtmlOutputText itemSupplyWarehouseId12Output;
	protected HtmlOutputText itemSupplyWarehouseId12;
	protected HtmlPanelGroup itemStockQuantity12Group;
	protected HtmlOutputText itemStockQuantity12Output;
	protected HtmlOutputText itemStockQuantity12;
	protected HtmlPanelGroup itemPrice12Group;
	protected HtmlOutputText itemPrice12Output;
	protected HtmlOutputText itemPrice12;
	protected HtmlPanelGroup itemTotal12Group;
	protected HtmlOutputText itemTotal12Output;
	protected HtmlOutputText itemTotal12;
	protected HtmlPanelGroup itemId13Group;
	protected HtmlOutputText itemId13Output;
	protected HtmlOutputText itemId13;
	protected HtmlPanelGroup itemName13Group;
	protected HtmlOutputText itemName13Output;
	protected HtmlOutputText itemName13;
	protected HtmlPanelGroup quantity13Group;
	protected HtmlOutputText itemQuantity13Output;
	protected HtmlOutputText itemQuantity13;
	protected HtmlPanelGroup itemSupplyWarehouseId13Group;
	protected HtmlOutputText itemSupplyWarehouseId13Output;
	protected HtmlOutputText itemSupplyWarehouseId13;
	protected HtmlPanelGroup itemStockQuantity13Group;
	protected HtmlOutputText itemStockQuantity13Output;
	protected HtmlOutputText itemStockQuantity13;
	protected HtmlPanelGroup itemPrice13Group;
	protected HtmlOutputText itemPrice13Output;
	protected HtmlOutputText itemPrice13;
	protected HtmlPanelGroup itemTotal13Group;
	protected HtmlOutputText itemTotal13Output;
	protected HtmlOutputText itemTotal13;
	protected HtmlPanelGroup itemId14Group;
	protected HtmlOutputText itemId14Output;
	protected HtmlOutputText itemId14;
	protected HtmlPanelGroup itemName14Group;
	protected HtmlOutputText itemName14Output;
	protected HtmlOutputText itemName14;
	protected HtmlPanelGroup quantity14Group;
	protected HtmlOutputText itemQuantity14Output;
	protected HtmlOutputText itemQuantity14;
	protected HtmlPanelGroup itemSupplyWarehouseId14Group;
	protected HtmlOutputText itemSupplyWarehouseId14Output;
	protected HtmlOutputText itemSupplyWarehouseId14;
	protected HtmlPanelGroup itemStockQuantity14Group;
	protected HtmlOutputText itemStockQuantity14Output;
	protected HtmlOutputText itemStockQuantity14;
	protected HtmlPanelGroup itemPrice14Group;
	protected HtmlOutputText itemPrice14Output;
	protected HtmlOutputText itemPrice14;
	protected HtmlPanelGroup itemTotal14Group;
	protected HtmlOutputText itemTotal14Output;
	protected HtmlOutputText itemTotal14;
	protected HtmlPanelGroup itemId15Group;
	protected HtmlOutputText itemId15Output;
	protected HtmlOutputText itemId15;
	protected HtmlPanelGroup itemName15Group;
	protected HtmlOutputText itemName15Output;
	protected HtmlOutputText itemName15;
	protected HtmlPanelGroup quantity15Group;
	protected HtmlOutputText itemQuantity15Output;
	protected HtmlOutputText itemQuantity15;
	protected HtmlPanelGroup itemSupplyWarehouseId15Group;
	protected HtmlOutputText itemSupplyWarehouseId15Output;
	protected HtmlOutputText itemSupplyWarehouseId15;
	protected HtmlPanelGroup itemStockQuantity15Group;
	protected HtmlOutputText itemStockQuantity15Output;
	protected HtmlOutputText itemStockQuantity15;
	protected HtmlPanelGroup itemPrice15Group;
	protected HtmlOutputText itemPrice15Output;
	protected HtmlOutputText itemPrice15;
	protected HtmlPanelGroup itemTotal15Group;
	protected HtmlOutputText itemTotal15Output;
	protected HtmlOutputText itemTotal15;
	protected HtmlPanelGroup grandTotalGroup;
	protected HtmlOutputText outGrandTotalOutput;
	protected HtmlPanelGroup statusGroup;
	protected HtmlOutputText outStatusOutput;
	protected HtmlOutputText outCustomerId;
	protected HtmlPanelGroup couponGroup;
	protected HtmlOutputText outCouponOutput;
	protected HtmlOutputText getTes() {
		if (tes == null) {
			tes = (HtmlOutputText) findComponentInRoot("tes");
		}
		return tes;
	}
	protected UIColumn getColumn1() {
		if (column1 == null) {
			column1 = (UIColumn) findComponentInRoot("column1");
		}
		return column1;
	}
	protected HtmlOutputText getText2() {
		if (text2 == null) {
			text2 = (HtmlOutputText) findComponentInRoot("text2");
		}
		return text2;
	}
	protected HtmlOutputText getText3() {
		if (text3 == null) {
			text3 = (HtmlOutputText) findComponentInRoot("text3");
		}
		return text3;
	}
	protected HtmlOutputText getText4() {
		if (text4 == null) {
			text4 = (HtmlOutputText) findComponentInRoot("text4");
		}
		return text4;
	}
	protected HtmlOutputText getText5() {
		if (text5 == null) {
			text5 = (HtmlOutputText) findComponentInRoot("text5");
		}
		return text5;
	}
	protected HtmlOutputText getText6() {
		if (text6 == null) {
			text6 = (HtmlOutputText) findComponentInRoot("text6");
		}
		return text6;
	}
	protected HtmlOutputText getText7() {
		if (text7 == null) {
			text7 = (HtmlOutputText) findComponentInRoot("text7");
		}
		return text7;
	}
	protected HtmlDataTable getTable1() {
		if (table1 == null) {
			table1 = (HtmlDataTable) findComponentInRoot("table1");
		}
		return table1;
	}
	protected HtmlPanelGrid getNewOrderInputGrid() {
		if (NewOrderInputGrid == null) {
			NewOrderInputGrid = (HtmlPanelGrid) findComponentInRoot("NewOrderInputGrid");
		}
		return NewOrderInputGrid;
	}
	protected HtmlPanelGroup getNewOrderGroup() {
		if (newOrderGroup == null) {
			newOrderGroup = (HtmlPanelGroup) findComponentInRoot("newOrderGroup");
		}
		return newOrderGroup;
	}
	protected HtmlOutputText getNewOrderDetails() {
		if (newOrderDetails == null) {
			newOrderDetails = (HtmlOutputText) findComponentInRoot("newOrderDetails");
		}
		return newOrderDetails;
	}
	protected HtmlPanelGroup getCustomerGroup() {
		if (customerGroup == null) {
			customerGroup = (HtmlPanelGroup) findComponentInRoot("customerGroup");
		}
		return customerGroup;
	}
	protected HtmlOutputText getCustomerId() {
		if (customerId == null) {
			customerId = (HtmlOutputText) findComponentInRoot("customerId");
		}
		return customerId;
	}
	protected HtmlPanelGroup getCustomerNameGroup() {
		if (customerNameGroup == null) {
			customerNameGroup = (HtmlPanelGroup) findComponentInRoot("customerNameGroup");
		}
		return customerNameGroup;
	}
	protected HtmlOutputText getCustomerName() {
		if (customerName == null) {
			customerName = (HtmlOutputText) findComponentInRoot("customerName");
		}
		return customerName;
	}
	protected HtmlOutputText getOutCustomerLastName() {
		if (outCustomerLastName == null) {
			outCustomerLastName = (HtmlOutputText) findComponentInRoot("outCustomerLastName");
		}
		return outCustomerLastName;
	}
	protected HtmlPanelGroup getCustomerCreditGroup() {
		if (customerCreditGroup == null) {
			customerCreditGroup = (HtmlPanelGroup) findComponentInRoot("customerCreditGroup");
		}
		return customerCreditGroup;
	}
	protected HtmlOutputText getCustomerCredit() {
		if (customerCredit == null) {
			customerCredit = (HtmlOutputText) findComponentInRoot("customerCredit");
		}
		return customerCredit;
	}
	protected HtmlOutputText getOutCustomerCredit() {
		if (outCustomerCredit == null) {
			outCustomerCredit = (HtmlOutputText) findComponentInRoot("outCustomerCredit");
		}
		return outCustomerCredit;
	}
	protected HtmlPanelGroup getCustomerDiscountGroup() {
		if (customerDiscountGroup == null) {
			customerDiscountGroup = (HtmlPanelGroup) findComponentInRoot("customerDiscountGroup");
		}
		return customerDiscountGroup;
	}
	protected HtmlOutputText getCustomerDiscount() {
		if (customerDiscount == null) {
			customerDiscount = (HtmlOutputText) findComponentInRoot("customerDiscount");
		}
		return customerDiscount;
	}
	protected HtmlOutputText getOutCustomerDiscount() {
		if (outCustomerDiscount == null) {
			outCustomerDiscount = (HtmlOutputText) findComponentInRoot("outCustomerDiscount");
		}
		return outCustomerDiscount;
	}
	protected HtmlPanelGroup getOrderIdGroup() {
		if (orderIdGroup == null) {
			orderIdGroup = (HtmlPanelGroup) findComponentInRoot("orderIdGroup");
		}
		return orderIdGroup;
	}
	protected HtmlOutputText getOrderId() {
		if (orderId == null) {
			orderId = (HtmlOutputText) findComponentInRoot("orderId");
		}
		return orderId;
	}
	protected HtmlOutputText getOutOrderId() {
		if (outOrderId == null) {
			outOrderId = (HtmlOutputText) findComponentInRoot("outOrderId");
		}
		return outOrderId;
	}
	protected HtmlPanelGroup getOrderDateGroup() {
		if (orderDateGroup == null) {
			orderDateGroup = (HtmlPanelGroup) findComponentInRoot("orderDateGroup");
		}
		return orderDateGroup;
	}
	protected HtmlOutputText getOrderEntryDate() {
		if (orderEntryDate == null) {
			orderEntryDate = (HtmlOutputText) findComponentInRoot("orderEntryDate");
		}
		return orderEntryDate;
	}
	protected HtmlOutputText getOutOrderEntryDate() {
		if (outOrderEntryDate == null) {
			outOrderEntryDate = (HtmlOutputText) findComponentInRoot("outOrderEntryDate");
		}
		return outOrderEntryDate;
	}
	protected HtmlPanelGroup getOrderItemCountGroup() {
		if (orderItemCountGroup == null) {
			orderItemCountGroup = (HtmlPanelGroup) findComponentInRoot("orderItemCountGroup");
		}
		return orderItemCountGroup;
	}
	protected HtmlOutputText getOutOrderItemCount() {
		if (outOrderItemCount == null) {
			outOrderItemCount = (HtmlOutputText) findComponentInRoot("outOrderItemCount");
		}
		return outOrderItemCount;
	}
	protected HtmlPanelGroup getWarehouseGroup() {
		if (warehouseGroup == null) {
			warehouseGroup = (HtmlPanelGroup) findComponentInRoot("warehouseGroup");
		}
		return warehouseGroup;
	}
	protected HtmlOutputText getWarehouseId() {
		if (warehouseId == null) {
			warehouseId = (HtmlOutputText) findComponentInRoot("warehouseId");
		}
		return warehouseId;
	}
	protected HtmlOutputText getOutWarehouseId() {
		if (outWarehouseId == null) {
			outWarehouseId = (HtmlOutputText) findComponentInRoot("outWarehouseId");
		}
		return outWarehouseId;
	}
	protected HtmlPanelGroup getWarehouseTaxGroup() {
		if (warehouseTaxGroup == null) {
			warehouseTaxGroup = (HtmlPanelGroup) findComponentInRoot("warehouseTaxGroup");
		}
		return warehouseTaxGroup;
	}
	protected HtmlOutputText getWarehouseTax() {
		if (warehouseTax == null) {
			warehouseTax = (HtmlOutputText) findComponentInRoot("warehouseTax");
		}
		return warehouseTax;
	}
	protected HtmlOutputText getOutWarehouseTax() {
		if (outWarehouseTax == null) {
			outWarehouseTax = (HtmlOutputText) findComponentInRoot("outWarehouseTax");
		}
		return outWarehouseTax;
	}
	protected HtmlPanelGroup getDistrictGroup() {
		if (districtGroup == null) {
			districtGroup = (HtmlPanelGroup) findComponentInRoot("districtGroup");
		}
		return districtGroup;
	}
	protected HtmlOutputText getDistrictId() {
		if (districtId == null) {
			districtId = (HtmlOutputText) findComponentInRoot("districtId");
		}
		return districtId;
	}
	protected HtmlOutputText getOutDistrictId() {
		if (outDistrictId == null) {
			outDistrictId = (HtmlOutputText) findComponentInRoot("outDistrictId");
		}
		return outDistrictId;
	}
	protected HtmlPanelGroup getDistrictTaxGroup() {
		if (districtTaxGroup == null) {
			districtTaxGroup = (HtmlPanelGroup) findComponentInRoot("districtTaxGroup");
		}
		return districtTaxGroup;
	}
	protected HtmlOutputText getDistrictTax() {
		if (districtTax == null) {
			districtTax = (HtmlOutputText) findComponentInRoot("districtTax");
		}
		return districtTax;
	}
	protected HtmlOutputText getOutDistrictTax() {
		if (outDistrictTax == null) {
			outDistrictTax = (HtmlOutputText) findComponentInRoot("outDistrictTax");
		}
		return outDistrictTax;
	}
	protected HtmlPanelGroup getOrderlineGroup() {
		if (orderlineGroup == null) {
			orderlineGroup = (HtmlPanelGroup) findComponentInRoot("orderlineGroup");
		}
		return orderlineGroup;
	}
	protected HtmlOutputText getOrderlineDetails() {
		if (orderlineDetails == null) {
			orderlineDetails = (HtmlOutputText) findComponentInRoot("orderlineDetails");
		}
		return orderlineDetails;
	}
	protected HtmlPanelGroup getItemId1Group() {
		if (itemId1Group == null) {
			itemId1Group = (HtmlPanelGroup) findComponentInRoot("itemId1Group");
		}
		return itemId1Group;
	}
	protected HtmlOutputText getItemId1Output() {
		if (itemId1Output == null) {
			itemId1Output = (HtmlOutputText) findComponentInRoot("itemId1Output");
		}
		return itemId1Output;
	}
	protected HtmlOutputText getItemId1() {
		if (itemId1 == null) {
			itemId1 = (HtmlOutputText) findComponentInRoot("itemId1");
		}
		return itemId1;
	}
	protected HtmlPanelGroup getItemName1Group() {
		if (itemName1Group == null) {
			itemName1Group = (HtmlPanelGroup) findComponentInRoot("itemName1Group");
		}
		return itemName1Group;
	}
	protected HtmlOutputText getItemName10Output() {
		if (itemName10Output == null) {
			itemName10Output = (HtmlOutputText) findComponentInRoot("itemName10Output");
		}
		return itemName10Output;
	}
	protected HtmlOutputText getItemName1() {
		if (itemName1 == null) {
			itemName1 = (HtmlOutputText) findComponentInRoot("itemName1");
		}
		return itemName1;
	}
	protected HtmlPanelGroup getQuantity1Group() {
		if (quantity1Group == null) {
			quantity1Group = (HtmlPanelGroup) findComponentInRoot("quantity1Group");
		}
		return quantity1Group;
	}
	protected HtmlOutputText getItemQuantity1Output() {
		if (itemQuantity1Output == null) {
			itemQuantity1Output = (HtmlOutputText) findComponentInRoot("itemQuantity1Output");
		}
		return itemQuantity1Output;
	}
	protected HtmlOutputText getItemQuantity1() {
		if (itemQuantity1 == null) {
			itemQuantity1 = (HtmlOutputText) findComponentInRoot("itemQuantity1");
		}
		return itemQuantity1;
	}
	protected HtmlPanelGroup getItemSupplyWarehouseId1Group() {
		if (itemSupplyWarehouseId1Group == null) {
			itemSupplyWarehouseId1Group = (HtmlPanelGroup) findComponentInRoot("itemSupplyWarehouseId1Group");
		}
		return itemSupplyWarehouseId1Group;
	}
	protected HtmlOutputText getItemSupplyWarehouseId1Output() {
		if (itemSupplyWarehouseId1Output == null) {
			itemSupplyWarehouseId1Output = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId1Output");
		}
		return itemSupplyWarehouseId1Output;
	}
	protected HtmlOutputText getItemSupplyWarehouseId1() {
		if (itemSupplyWarehouseId1 == null) {
			itemSupplyWarehouseId1 = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId1");
		}
		return itemSupplyWarehouseId1;
	}
	protected HtmlPanelGroup getItemStockQuantity1Group() {
		if (itemStockQuantity1Group == null) {
			itemStockQuantity1Group = (HtmlPanelGroup) findComponentInRoot("itemStockQuantity1Group");
		}
		return itemStockQuantity1Group;
	}
	protected HtmlOutputText getItemStockQuantity1Output() {
		if (itemStockQuantity1Output == null) {
			itemStockQuantity1Output = (HtmlOutputText) findComponentInRoot("itemStockQuantity1Output");
		}
		return itemStockQuantity1Output;
	}
	protected HtmlOutputText getItemStockQuantity9() {
		if (itemStockQuantity9 == null) {
			itemStockQuantity9 = (HtmlOutputText) findComponentInRoot("itemStockQuantity9");
		}
		return itemStockQuantity9;
	}
	protected HtmlPanelGroup getItemPrice1Group() {
		if (itemPrice1Group == null) {
			itemPrice1Group = (HtmlPanelGroup) findComponentInRoot("itemPrice1Group");
		}
		return itemPrice1Group;
	}
	protected HtmlOutputText getItemPrice1Output() {
		if (itemPrice1Output == null) {
			itemPrice1Output = (HtmlOutputText) findComponentInRoot("itemPrice1Output");
		}
		return itemPrice1Output;
	}
	protected HtmlOutputText getItemPrice1() {
		if (itemPrice1 == null) {
			itemPrice1 = (HtmlOutputText) findComponentInRoot("itemPrice1");
		}
		return itemPrice1;
	}
	protected HtmlPanelGroup getItemTotal1Group() {
		if (itemTotal1Group == null) {
			itemTotal1Group = (HtmlPanelGroup) findComponentInRoot("itemTotal1Group");
		}
		return itemTotal1Group;
	}
	protected HtmlOutputText getItemTotal1Output() {
		if (itemTotal1Output == null) {
			itemTotal1Output = (HtmlOutputText) findComponentInRoot("itemTotal1Output");
		}
		return itemTotal1Output;
	}
	protected HtmlOutputText getItemTotal1() {
		if (itemTotal1 == null) {
			itemTotal1 = (HtmlOutputText) findComponentInRoot("itemTotal1");
		}
		return itemTotal1;
	}
	protected HtmlPanelGroup getItemId2Group() {
		if (itemId2Group == null) {
			itemId2Group = (HtmlPanelGroup) findComponentInRoot("itemId2Group");
		}
		return itemId2Group;
	}
	protected HtmlOutputText getItemId2Output() {
		if (itemId2Output == null) {
			itemId2Output = (HtmlOutputText) findComponentInRoot("itemId2Output");
		}
		return itemId2Output;
	}
	protected HtmlOutputText getItemId2() {
		if (itemId2 == null) {
			itemId2 = (HtmlOutputText) findComponentInRoot("itemId2");
		}
		return itemId2;
	}
	protected HtmlPanelGroup getItemName2Group() {
		if (itemName2Group == null) {
			itemName2Group = (HtmlPanelGroup) findComponentInRoot("itemName2Group");
		}
		return itemName2Group;
	}
	protected HtmlOutputText getItemName2Output() {
		if (itemName2Output == null) {
			itemName2Output = (HtmlOutputText) findComponentInRoot("itemName2Output");
		}
		return itemName2Output;
	}
	protected HtmlOutputText getItemName2() {
		if (itemName2 == null) {
			itemName2 = (HtmlOutputText) findComponentInRoot("itemName2");
		}
		return itemName2;
	}
	protected HtmlPanelGroup getQuantity2Group() {
		if (quantity2Group == null) {
			quantity2Group = (HtmlPanelGroup) findComponentInRoot("quantity2Group");
		}
		return quantity2Group;
	}
	protected HtmlOutputText getItemQuantity2Output() {
		if (itemQuantity2Output == null) {
			itemQuantity2Output = (HtmlOutputText) findComponentInRoot("itemQuantity2Output");
		}
		return itemQuantity2Output;
	}
	protected HtmlOutputText getItemQuantity2() {
		if (itemQuantity2 == null) {
			itemQuantity2 = (HtmlOutputText) findComponentInRoot("itemQuantity2");
		}
		return itemQuantity2;
	}
	protected HtmlPanelGroup getItemSupplyWarehouseId2Group() {
		if (itemSupplyWarehouseId2Group == null) {
			itemSupplyWarehouseId2Group = (HtmlPanelGroup) findComponentInRoot("itemSupplyWarehouseId2Group");
		}
		return itemSupplyWarehouseId2Group;
	}
	protected HtmlOutputText getItemSupplyWarehouseId2Output() {
		if (itemSupplyWarehouseId2Output == null) {
			itemSupplyWarehouseId2Output = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId2Output");
		}
		return itemSupplyWarehouseId2Output;
	}
	protected HtmlOutputText getItemSupplyWarehouseId2() {
		if (itemSupplyWarehouseId2 == null) {
			itemSupplyWarehouseId2 = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId2");
		}
		return itemSupplyWarehouseId2;
	}
	protected HtmlPanelGroup getItemStockQuantity2Group() {
		if (itemStockQuantity2Group == null) {
			itemStockQuantity2Group = (HtmlPanelGroup) findComponentInRoot("itemStockQuantity2Group");
		}
		return itemStockQuantity2Group;
	}
	protected HtmlOutputText getItemStockQuantity2Output() {
		if (itemStockQuantity2Output == null) {
			itemStockQuantity2Output = (HtmlOutputText) findComponentInRoot("itemStockQuantity2Output");
		}
		return itemStockQuantity2Output;
	}
	protected HtmlOutputText getItemStockQuantity2() {
		if (itemStockQuantity2 == null) {
			itemStockQuantity2 = (HtmlOutputText) findComponentInRoot("itemStockQuantity2");
		}
		return itemStockQuantity2;
	}
	protected HtmlPanelGroup getItemPrice2Group() {
		if (itemPrice2Group == null) {
			itemPrice2Group = (HtmlPanelGroup) findComponentInRoot("itemPrice2Group");
		}
		return itemPrice2Group;
	}
	protected HtmlOutputText getItemPrice2Output() {
		if (itemPrice2Output == null) {
			itemPrice2Output = (HtmlOutputText) findComponentInRoot("itemPrice2Output");
		}
		return itemPrice2Output;
	}
	protected HtmlOutputText getItemPrice2() {
		if (itemPrice2 == null) {
			itemPrice2 = (HtmlOutputText) findComponentInRoot("itemPrice2");
		}
		return itemPrice2;
	}
	protected HtmlPanelGroup getItemTotal2Group() {
		if (itemTotal2Group == null) {
			itemTotal2Group = (HtmlPanelGroup) findComponentInRoot("itemTotal2Group");
		}
		return itemTotal2Group;
	}
	protected HtmlOutputText getItemTotal2Output() {
		if (itemTotal2Output == null) {
			itemTotal2Output = (HtmlOutputText) findComponentInRoot("itemTotal2Output");
		}
		return itemTotal2Output;
	}
	protected HtmlOutputText getItemTotal2() {
		if (itemTotal2 == null) {
			itemTotal2 = (HtmlOutputText) findComponentInRoot("itemTotal2");
		}
		return itemTotal2;
	}
	protected HtmlPanelGroup getItemId3Group() {
		if (itemId3Group == null) {
			itemId3Group = (HtmlPanelGroup) findComponentInRoot("itemId3Group");
		}
		return itemId3Group;
	}
	protected HtmlOutputText getItemId3Output() {
		if (itemId3Output == null) {
			itemId3Output = (HtmlOutputText) findComponentInRoot("itemId3Output");
		}
		return itemId3Output;
	}
	protected HtmlOutputText getItemId3() {
		if (itemId3 == null) {
			itemId3 = (HtmlOutputText) findComponentInRoot("itemId3");
		}
		return itemId3;
	}
	protected HtmlPanelGroup getItemName3Group() {
		if (itemName3Group == null) {
			itemName3Group = (HtmlPanelGroup) findComponentInRoot("itemName3Group");
		}
		return itemName3Group;
	}
	protected HtmlOutputText getItemName3Output() {
		if (itemName3Output == null) {
			itemName3Output = (HtmlOutputText) findComponentInRoot("itemName3Output");
		}
		return itemName3Output;
	}
	protected HtmlOutputText getItemName3() {
		if (itemName3 == null) {
			itemName3 = (HtmlOutputText) findComponentInRoot("itemName3");
		}
		return itemName3;
	}
	protected HtmlPanelGroup getQuantity3Group() {
		if (quantity3Group == null) {
			quantity3Group = (HtmlPanelGroup) findComponentInRoot("quantity3Group");
		}
		return quantity3Group;
	}
	protected HtmlOutputText getItemQuantity3Output() {
		if (itemQuantity3Output == null) {
			itemQuantity3Output = (HtmlOutputText) findComponentInRoot("itemQuantity3Output");
		}
		return itemQuantity3Output;
	}
	protected HtmlOutputText getItemQuantity3() {
		if (itemQuantity3 == null) {
			itemQuantity3 = (HtmlOutputText) findComponentInRoot("itemQuantity3");
		}
		return itemQuantity3;
	}
	protected HtmlPanelGroup getItemSupplyWarehouseId3Group() {
		if (itemSupplyWarehouseId3Group == null) {
			itemSupplyWarehouseId3Group = (HtmlPanelGroup) findComponentInRoot("itemSupplyWarehouseId3Group");
		}
		return itemSupplyWarehouseId3Group;
	}
	protected HtmlOutputText getItemSupplyWarehouseId3Output() {
		if (itemSupplyWarehouseId3Output == null) {
			itemSupplyWarehouseId3Output = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId3Output");
		}
		return itemSupplyWarehouseId3Output;
	}
	protected HtmlOutputText getItemSupplyWarehouseId3() {
		if (itemSupplyWarehouseId3 == null) {
			itemSupplyWarehouseId3 = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId3");
		}
		return itemSupplyWarehouseId3;
	}
	protected HtmlPanelGroup getItemStockQuantity3Group() {
		if (itemStockQuantity3Group == null) {
			itemStockQuantity3Group = (HtmlPanelGroup) findComponentInRoot("itemStockQuantity3Group");
		}
		return itemStockQuantity3Group;
	}
	protected HtmlOutputText getItemStockQuantity3Output() {
		if (itemStockQuantity3Output == null) {
			itemStockQuantity3Output = (HtmlOutputText) findComponentInRoot("itemStockQuantity3Output");
		}
		return itemStockQuantity3Output;
	}
	protected HtmlOutputText getItemStockQuantity3() {
		if (itemStockQuantity3 == null) {
			itemStockQuantity3 = (HtmlOutputText) findComponentInRoot("itemStockQuantity3");
		}
		return itemStockQuantity3;
	}
	protected HtmlPanelGroup getItemPrice3Group() {
		if (itemPrice3Group == null) {
			itemPrice3Group = (HtmlPanelGroup) findComponentInRoot("itemPrice3Group");
		}
		return itemPrice3Group;
	}
	protected HtmlOutputText getItemPrice3Output() {
		if (itemPrice3Output == null) {
			itemPrice3Output = (HtmlOutputText) findComponentInRoot("itemPrice3Output");
		}
		return itemPrice3Output;
	}
	protected HtmlOutputText getItemPrice3() {
		if (itemPrice3 == null) {
			itemPrice3 = (HtmlOutputText) findComponentInRoot("itemPrice3");
		}
		return itemPrice3;
	}
	protected HtmlPanelGroup getItemTotal3Group() {
		if (itemTotal3Group == null) {
			itemTotal3Group = (HtmlPanelGroup) findComponentInRoot("itemTotal3Group");
		}
		return itemTotal3Group;
	}
	protected HtmlOutputText getItemTotal3Output() {
		if (itemTotal3Output == null) {
			itemTotal3Output = (HtmlOutputText) findComponentInRoot("itemTotal3Output");
		}
		return itemTotal3Output;
	}
	protected HtmlOutputText getItemTotal3() {
		if (itemTotal3 == null) {
			itemTotal3 = (HtmlOutputText) findComponentInRoot("itemTotal3");
		}
		return itemTotal3;
	}
	protected HtmlPanelGroup getItemId4Group() {
		if (itemId4Group == null) {
			itemId4Group = (HtmlPanelGroup) findComponentInRoot("itemId4Group");
		}
		return itemId4Group;
	}
	protected HtmlOutputText getItemId4Output() {
		if (itemId4Output == null) {
			itemId4Output = (HtmlOutputText) findComponentInRoot("itemId4Output");
		}
		return itemId4Output;
	}
	protected HtmlOutputText getItemId4() {
		if (itemId4 == null) {
			itemId4 = (HtmlOutputText) findComponentInRoot("itemId4");
		}
		return itemId4;
	}
	protected HtmlPanelGroup getItemName4Group() {
		if (itemName4Group == null) {
			itemName4Group = (HtmlPanelGroup) findComponentInRoot("itemName4Group");
		}
		return itemName4Group;
	}
	protected HtmlOutputText getItemName4Output() {
		if (itemName4Output == null) {
			itemName4Output = (HtmlOutputText) findComponentInRoot("itemName4Output");
		}
		return itemName4Output;
	}
	protected HtmlOutputText getItemName4() {
		if (itemName4 == null) {
			itemName4 = (HtmlOutputText) findComponentInRoot("itemName4");
		}
		return itemName4;
	}
	protected HtmlPanelGroup getQuantity4Group() {
		if (quantity4Group == null) {
			quantity4Group = (HtmlPanelGroup) findComponentInRoot("quantity4Group");
		}
		return quantity4Group;
	}
	protected HtmlOutputText getItemQuantity4Output() {
		if (itemQuantity4Output == null) {
			itemQuantity4Output = (HtmlOutputText) findComponentInRoot("itemQuantity4Output");
		}
		return itemQuantity4Output;
	}
	protected HtmlOutputText getItemQuantity4() {
		if (itemQuantity4 == null) {
			itemQuantity4 = (HtmlOutputText) findComponentInRoot("itemQuantity4");
		}
		return itemQuantity4;
	}
	protected HtmlPanelGroup getItemSupplyWarehouseId4Group() {
		if (itemSupplyWarehouseId4Group == null) {
			itemSupplyWarehouseId4Group = (HtmlPanelGroup) findComponentInRoot("itemSupplyWarehouseId4Group");
		}
		return itemSupplyWarehouseId4Group;
	}
	protected HtmlOutputText getItemSupplyWarehouseId4Output() {
		if (itemSupplyWarehouseId4Output == null) {
			itemSupplyWarehouseId4Output = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId4Output");
		}
		return itemSupplyWarehouseId4Output;
	}
	protected HtmlOutputText getItemSupplyWarehouseId4() {
		if (itemSupplyWarehouseId4 == null) {
			itemSupplyWarehouseId4 = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId4");
		}
		return itemSupplyWarehouseId4;
	}
	protected HtmlPanelGroup getItemStockQuantity4Group() {
		if (itemStockQuantity4Group == null) {
			itemStockQuantity4Group = (HtmlPanelGroup) findComponentInRoot("itemStockQuantity4Group");
		}
		return itemStockQuantity4Group;
	}
	protected HtmlOutputText getItemStockQuantity4Output() {
		if (itemStockQuantity4Output == null) {
			itemStockQuantity4Output = (HtmlOutputText) findComponentInRoot("itemStockQuantity4Output");
		}
		return itemStockQuantity4Output;
	}
	protected HtmlOutputText getItemStockQuantity4() {
		if (itemStockQuantity4 == null) {
			itemStockQuantity4 = (HtmlOutputText) findComponentInRoot("itemStockQuantity4");
		}
		return itemStockQuantity4;
	}
	protected HtmlPanelGroup getItemPrice4Group() {
		if (itemPrice4Group == null) {
			itemPrice4Group = (HtmlPanelGroup) findComponentInRoot("itemPrice4Group");
		}
		return itemPrice4Group;
	}
	protected HtmlOutputText getItemPrice4Output() {
		if (itemPrice4Output == null) {
			itemPrice4Output = (HtmlOutputText) findComponentInRoot("itemPrice4Output");
		}
		return itemPrice4Output;
	}
	protected HtmlOutputText getItemPrice4() {
		if (itemPrice4 == null) {
			itemPrice4 = (HtmlOutputText) findComponentInRoot("itemPrice4");
		}
		return itemPrice4;
	}
	protected HtmlPanelGroup getItemTotal4Group() {
		if (itemTotal4Group == null) {
			itemTotal4Group = (HtmlPanelGroup) findComponentInRoot("itemTotal4Group");
		}
		return itemTotal4Group;
	}
	protected HtmlOutputText getItemTotal4Output() {
		if (itemTotal4Output == null) {
			itemTotal4Output = (HtmlOutputText) findComponentInRoot("itemTotal4Output");
		}
		return itemTotal4Output;
	}
	protected HtmlOutputText getItemTotal4() {
		if (itemTotal4 == null) {
			itemTotal4 = (HtmlOutputText) findComponentInRoot("itemTotal4");
		}
		return itemTotal4;
	}
	protected HtmlPanelGroup getItemId5Group() {
		if (itemId5Group == null) {
			itemId5Group = (HtmlPanelGroup) findComponentInRoot("itemId5Group");
		}
		return itemId5Group;
	}
	protected HtmlOutputText getItemId5Output() {
		if (itemId5Output == null) {
			itemId5Output = (HtmlOutputText) findComponentInRoot("itemId5Output");
		}
		return itemId5Output;
	}
	protected HtmlOutputText getItemId5() {
		if (itemId5 == null) {
			itemId5 = (HtmlOutputText) findComponentInRoot("itemId5");
		}
		return itemId5;
	}
	protected HtmlPanelGroup getItemName5Group() {
		if (itemName5Group == null) {
			itemName5Group = (HtmlPanelGroup) findComponentInRoot("itemName5Group");
		}
		return itemName5Group;
	}
	protected HtmlOutputText getItemName5Output() {
		if (itemName5Output == null) {
			itemName5Output = (HtmlOutputText) findComponentInRoot("itemName5Output");
		}
		return itemName5Output;
	}
	protected HtmlOutputText getItemName5() {
		if (itemName5 == null) {
			itemName5 = (HtmlOutputText) findComponentInRoot("itemName5");
		}
		return itemName5;
	}
	protected HtmlPanelGroup getQuantity5Group() {
		if (quantity5Group == null) {
			quantity5Group = (HtmlPanelGroup) findComponentInRoot("quantity5Group");
		}
		return quantity5Group;
	}
	protected HtmlOutputText getItemQuantity5Output() {
		if (itemQuantity5Output == null) {
			itemQuantity5Output = (HtmlOutputText) findComponentInRoot("itemQuantity5Output");
		}
		return itemQuantity5Output;
	}
	protected HtmlOutputText getItemQuantity5() {
		if (itemQuantity5 == null) {
			itemQuantity5 = (HtmlOutputText) findComponentInRoot("itemQuantity5");
		}
		return itemQuantity5;
	}
	protected HtmlPanelGroup getItemSupplyWarehouseId5Group() {
		if (itemSupplyWarehouseId5Group == null) {
			itemSupplyWarehouseId5Group = (HtmlPanelGroup) findComponentInRoot("itemSupplyWarehouseId5Group");
		}
		return itemSupplyWarehouseId5Group;
	}
	protected HtmlOutputText getItemSupplyWarehouseId5Output() {
		if (itemSupplyWarehouseId5Output == null) {
			itemSupplyWarehouseId5Output = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId5Output");
		}
		return itemSupplyWarehouseId5Output;
	}
	protected HtmlOutputText getItemSupplyWarehouseId5() {
		if (itemSupplyWarehouseId5 == null) {
			itemSupplyWarehouseId5 = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId5");
		}
		return itemSupplyWarehouseId5;
	}
	protected HtmlPanelGroup getItemStockQuantity5Group() {
		if (itemStockQuantity5Group == null) {
			itemStockQuantity5Group = (HtmlPanelGroup) findComponentInRoot("itemStockQuantity5Group");
		}
		return itemStockQuantity5Group;
	}
	protected HtmlOutputText getItemStockQuantity5Output() {
		if (itemStockQuantity5Output == null) {
			itemStockQuantity5Output = (HtmlOutputText) findComponentInRoot("itemStockQuantity5Output");
		}
		return itemStockQuantity5Output;
	}
	protected HtmlOutputText getItemStockQuantity5() {
		if (itemStockQuantity5 == null) {
			itemStockQuantity5 = (HtmlOutputText) findComponentInRoot("itemStockQuantity5");
		}
		return itemStockQuantity5;
	}
	protected HtmlPanelGroup getItemPrice5Group() {
		if (itemPrice5Group == null) {
			itemPrice5Group = (HtmlPanelGroup) findComponentInRoot("itemPrice5Group");
		}
		return itemPrice5Group;
	}
	protected HtmlOutputText getItemPrice5Output() {
		if (itemPrice5Output == null) {
			itemPrice5Output = (HtmlOutputText) findComponentInRoot("itemPrice5Output");
		}
		return itemPrice5Output;
	}
	protected HtmlOutputText getItemPrice5() {
		if (itemPrice5 == null) {
			itemPrice5 = (HtmlOutputText) findComponentInRoot("itemPrice5");
		}
		return itemPrice5;
	}
	protected HtmlPanelGroup getItemTotal5Group() {
		if (itemTotal5Group == null) {
			itemTotal5Group = (HtmlPanelGroup) findComponentInRoot("itemTotal5Group");
		}
		return itemTotal5Group;
	}
	protected HtmlOutputText getItemTotal5Output() {
		if (itemTotal5Output == null) {
			itemTotal5Output = (HtmlOutputText) findComponentInRoot("itemTotal5Output");
		}
		return itemTotal5Output;
	}
	protected HtmlOutputText getItemTotal5() {
		if (itemTotal5 == null) {
			itemTotal5 = (HtmlOutputText) findComponentInRoot("itemTotal5");
		}
		return itemTotal5;
	}
	protected HtmlPanelGroup getItemId6Group() {
		if (itemId6Group == null) {
			itemId6Group = (HtmlPanelGroup) findComponentInRoot("itemId6Group");
		}
		return itemId6Group;
	}
	protected HtmlOutputText getItemId6Output() {
		if (itemId6Output == null) {
			itemId6Output = (HtmlOutputText) findComponentInRoot("itemId6Output");
		}
		return itemId6Output;
	}
	protected HtmlOutputText getItemId6() {
		if (itemId6 == null) {
			itemId6 = (HtmlOutputText) findComponentInRoot("itemId6");
		}
		return itemId6;
	}
	protected HtmlPanelGroup getItemName6Group() {
		if (itemName6Group == null) {
			itemName6Group = (HtmlPanelGroup) findComponentInRoot("itemName6Group");
		}
		return itemName6Group;
	}
	protected HtmlOutputText getItemName6Output() {
		if (itemName6Output == null) {
			itemName6Output = (HtmlOutputText) findComponentInRoot("itemName6Output");
		}
		return itemName6Output;
	}
	protected HtmlOutputText getItemName6() {
		if (itemName6 == null) {
			itemName6 = (HtmlOutputText) findComponentInRoot("itemName6");
		}
		return itemName6;
	}
	protected HtmlPanelGroup getQuantity6Group() {
		if (quantity6Group == null) {
			quantity6Group = (HtmlPanelGroup) findComponentInRoot("quantity6Group");
		}
		return quantity6Group;
	}
	protected HtmlOutputText getItemQuantity6Output() {
		if (itemQuantity6Output == null) {
			itemQuantity6Output = (HtmlOutputText) findComponentInRoot("itemQuantity6Output");
		}
		return itemQuantity6Output;
	}
	protected HtmlOutputText getItemQuantity6() {
		if (itemQuantity6 == null) {
			itemQuantity6 = (HtmlOutputText) findComponentInRoot("itemQuantity6");
		}
		return itemQuantity6;
	}
	protected HtmlPanelGroup getItemSupplyWarehouseId6Group() {
		if (itemSupplyWarehouseId6Group == null) {
			itemSupplyWarehouseId6Group = (HtmlPanelGroup) findComponentInRoot("itemSupplyWarehouseId6Group");
		}
		return itemSupplyWarehouseId6Group;
	}
	protected HtmlOutputText getItemSupplyWarehouseId6Output() {
		if (itemSupplyWarehouseId6Output == null) {
			itemSupplyWarehouseId6Output = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId6Output");
		}
		return itemSupplyWarehouseId6Output;
	}
	protected HtmlOutputText getItemSupplyWarehouseId6() {
		if (itemSupplyWarehouseId6 == null) {
			itemSupplyWarehouseId6 = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId6");
		}
		return itemSupplyWarehouseId6;
	}
	protected HtmlPanelGroup getItemStockQuantity6Group() {
		if (itemStockQuantity6Group == null) {
			itemStockQuantity6Group = (HtmlPanelGroup) findComponentInRoot("itemStockQuantity6Group");
		}
		return itemStockQuantity6Group;
	}
	protected HtmlOutputText getItemStockQuantity6Output() {
		if (itemStockQuantity6Output == null) {
			itemStockQuantity6Output = (HtmlOutputText) findComponentInRoot("itemStockQuantity6Output");
		}
		return itemStockQuantity6Output;
	}
	protected HtmlOutputText getItemStockQuantity6() {
		if (itemStockQuantity6 == null) {
			itemStockQuantity6 = (HtmlOutputText) findComponentInRoot("itemStockQuantity6");
		}
		return itemStockQuantity6;
	}
	protected HtmlPanelGroup getItemPrice6Group() {
		if (itemPrice6Group == null) {
			itemPrice6Group = (HtmlPanelGroup) findComponentInRoot("itemPrice6Group");
		}
		return itemPrice6Group;
	}
	protected HtmlOutputText getItemPrice6Output() {
		if (itemPrice6Output == null) {
			itemPrice6Output = (HtmlOutputText) findComponentInRoot("itemPrice6Output");
		}
		return itemPrice6Output;
	}
	protected HtmlOutputText getItemPrice6() {
		if (itemPrice6 == null) {
			itemPrice6 = (HtmlOutputText) findComponentInRoot("itemPrice6");
		}
		return itemPrice6;
	}
	protected HtmlPanelGroup getItemTotal6Group() {
		if (itemTotal6Group == null) {
			itemTotal6Group = (HtmlPanelGroup) findComponentInRoot("itemTotal6Group");
		}
		return itemTotal6Group;
	}
	protected HtmlOutputText getItemTotal6Output() {
		if (itemTotal6Output == null) {
			itemTotal6Output = (HtmlOutputText) findComponentInRoot("itemTotal6Output");
		}
		return itemTotal6Output;
	}
	protected HtmlOutputText getItemTotal6() {
		if (itemTotal6 == null) {
			itemTotal6 = (HtmlOutputText) findComponentInRoot("itemTotal6");
		}
		return itemTotal6;
	}
	protected HtmlPanelGroup getItemId7Group() {
		if (itemId7Group == null) {
			itemId7Group = (HtmlPanelGroup) findComponentInRoot("itemId7Group");
		}
		return itemId7Group;
	}
	protected HtmlOutputText getItemId7Output() {
		if (itemId7Output == null) {
			itemId7Output = (HtmlOutputText) findComponentInRoot("itemId7Output");
		}
		return itemId7Output;
	}
	protected HtmlOutputText getItemId7() {
		if (itemId7 == null) {
			itemId7 = (HtmlOutputText) findComponentInRoot("itemId7");
		}
		return itemId7;
	}
	protected HtmlPanelGroup getItemName7Group() {
		if (itemName7Group == null) {
			itemName7Group = (HtmlPanelGroup) findComponentInRoot("itemName7Group");
		}
		return itemName7Group;
	}
	protected HtmlOutputText getItemName7Output() {
		if (itemName7Output == null) {
			itemName7Output = (HtmlOutputText) findComponentInRoot("itemName7Output");
		}
		return itemName7Output;
	}
	protected HtmlOutputText getItemName7() {
		if (itemName7 == null) {
			itemName7 = (HtmlOutputText) findComponentInRoot("itemName7");
		}
		return itemName7;
	}
	protected HtmlPanelGroup getQuantity7Group() {
		if (quantity7Group == null) {
			quantity7Group = (HtmlPanelGroup) findComponentInRoot("quantity7Group");
		}
		return quantity7Group;
	}
	protected HtmlOutputText getItemQuantity7Output() {
		if (itemQuantity7Output == null) {
			itemQuantity7Output = (HtmlOutputText) findComponentInRoot("itemQuantity7Output");
		}
		return itemQuantity7Output;
	}
	protected HtmlOutputText getItemQuantity7() {
		if (itemQuantity7 == null) {
			itemQuantity7 = (HtmlOutputText) findComponentInRoot("itemQuantity7");
		}
		return itemQuantity7;
	}
	protected HtmlPanelGroup getItemSupplyWarehouseId7Group() {
		if (itemSupplyWarehouseId7Group == null) {
			itemSupplyWarehouseId7Group = (HtmlPanelGroup) findComponentInRoot("itemSupplyWarehouseId7Group");
		}
		return itemSupplyWarehouseId7Group;
	}
	protected HtmlOutputText getItemSupplyWarehouseId7Output() {
		if (itemSupplyWarehouseId7Output == null) {
			itemSupplyWarehouseId7Output = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId7Output");
		}
		return itemSupplyWarehouseId7Output;
	}
	protected HtmlOutputText getItemSupplyWarehouseId7() {
		if (itemSupplyWarehouseId7 == null) {
			itemSupplyWarehouseId7 = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId7");
		}
		return itemSupplyWarehouseId7;
	}
	protected HtmlPanelGroup getItemStockQuantity7Group() {
		if (itemStockQuantity7Group == null) {
			itemStockQuantity7Group = (HtmlPanelGroup) findComponentInRoot("itemStockQuantity7Group");
		}
		return itemStockQuantity7Group;
	}
	protected HtmlOutputText getItemStockQuantity7Output() {
		if (itemStockQuantity7Output == null) {
			itemStockQuantity7Output = (HtmlOutputText) findComponentInRoot("itemStockQuantity7Output");
		}
		return itemStockQuantity7Output;
	}
	protected HtmlOutputText getItemStockQuantity7() {
		if (itemStockQuantity7 == null) {
			itemStockQuantity7 = (HtmlOutputText) findComponentInRoot("itemStockQuantity7");
		}
		return itemStockQuantity7;
	}
	protected HtmlPanelGroup getItemPrice7Group() {
		if (itemPrice7Group == null) {
			itemPrice7Group = (HtmlPanelGroup) findComponentInRoot("itemPrice7Group");
		}
		return itemPrice7Group;
	}
	protected HtmlOutputText getItemPrice7Output() {
		if (itemPrice7Output == null) {
			itemPrice7Output = (HtmlOutputText) findComponentInRoot("itemPrice7Output");
		}
		return itemPrice7Output;
	}
	protected HtmlOutputText getItemPrice7() {
		if (itemPrice7 == null) {
			itemPrice7 = (HtmlOutputText) findComponentInRoot("itemPrice7");
		}
		return itemPrice7;
	}
	protected HtmlPanelGroup getItemTotal7Group() {
		if (itemTotal7Group == null) {
			itemTotal7Group = (HtmlPanelGroup) findComponentInRoot("itemTotal7Group");
		}
		return itemTotal7Group;
	}
	protected HtmlOutputText getItemTotal7Output() {
		if (itemTotal7Output == null) {
			itemTotal7Output = (HtmlOutputText) findComponentInRoot("itemTotal7Output");
		}
		return itemTotal7Output;
	}
	protected HtmlOutputText getItemTotal7() {
		if (itemTotal7 == null) {
			itemTotal7 = (HtmlOutputText) findComponentInRoot("itemTotal7");
		}
		return itemTotal7;
	}
	protected HtmlPanelGroup getItemId8Group() {
		if (itemId8Group == null) {
			itemId8Group = (HtmlPanelGroup) findComponentInRoot("itemId8Group");
		}
		return itemId8Group;
	}
	protected HtmlOutputText getItemId8Output() {
		if (itemId8Output == null) {
			itemId8Output = (HtmlOutputText) findComponentInRoot("itemId8Output");
		}
		return itemId8Output;
	}
	protected HtmlOutputText getItemId8() {
		if (itemId8 == null) {
			itemId8 = (HtmlOutputText) findComponentInRoot("itemId8");
		}
		return itemId8;
	}
	protected HtmlPanelGroup getItemName8Group() {
		if (itemName8Group == null) {
			itemName8Group = (HtmlPanelGroup) findComponentInRoot("itemName8Group");
		}
		return itemName8Group;
	}
	protected HtmlOutputText getItemName8Output() {
		if (itemName8Output == null) {
			itemName8Output = (HtmlOutputText) findComponentInRoot("itemName8Output");
		}
		return itemName8Output;
	}
	protected HtmlOutputText getItemName8() {
		if (itemName8 == null) {
			itemName8 = (HtmlOutputText) findComponentInRoot("itemName8");
		}
		return itemName8;
	}
	protected HtmlPanelGroup getQuantity8Group() {
		if (quantity8Group == null) {
			quantity8Group = (HtmlPanelGroup) findComponentInRoot("quantity8Group");
		}
		return quantity8Group;
	}
	protected HtmlOutputText getItemQuantity8Output() {
		if (itemQuantity8Output == null) {
			itemQuantity8Output = (HtmlOutputText) findComponentInRoot("itemQuantity8Output");
		}
		return itemQuantity8Output;
	}
	protected HtmlOutputText getItemQuantity8() {
		if (itemQuantity8 == null) {
			itemQuantity8 = (HtmlOutputText) findComponentInRoot("itemQuantity8");
		}
		return itemQuantity8;
	}
	protected HtmlPanelGroup getItemSupplyWarehouseId8Group() {
		if (itemSupplyWarehouseId8Group == null) {
			itemSupplyWarehouseId8Group = (HtmlPanelGroup) findComponentInRoot("itemSupplyWarehouseId8Group");
		}
		return itemSupplyWarehouseId8Group;
	}
	protected HtmlOutputText getItemSupplyWarehouseId8Output() {
		if (itemSupplyWarehouseId8Output == null) {
			itemSupplyWarehouseId8Output = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId8Output");
		}
		return itemSupplyWarehouseId8Output;
	}
	protected HtmlOutputText getItemSupplyWarehouseId8() {
		if (itemSupplyWarehouseId8 == null) {
			itemSupplyWarehouseId8 = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId8");
		}
		return itemSupplyWarehouseId8;
	}
	protected HtmlPanelGroup getItemStockQuantity8Group() {
		if (itemStockQuantity8Group == null) {
			itemStockQuantity8Group = (HtmlPanelGroup) findComponentInRoot("itemStockQuantity8Group");
		}
		return itemStockQuantity8Group;
	}
	protected HtmlOutputText getItemStockQuantity8Output() {
		if (itemStockQuantity8Output == null) {
			itemStockQuantity8Output = (HtmlOutputText) findComponentInRoot("itemStockQuantity8Output");
		}
		return itemStockQuantity8Output;
	}
	protected HtmlOutputText getItemStockQuantity8() {
		if (itemStockQuantity8 == null) {
			itemStockQuantity8 = (HtmlOutputText) findComponentInRoot("itemStockQuantity8");
		}
		return itemStockQuantity8;
	}
	protected HtmlPanelGroup getItemPrice8Group() {
		if (itemPrice8Group == null) {
			itemPrice8Group = (HtmlPanelGroup) findComponentInRoot("itemPrice8Group");
		}
		return itemPrice8Group;
	}
	protected HtmlOutputText getItemPrice8Output() {
		if (itemPrice8Output == null) {
			itemPrice8Output = (HtmlOutputText) findComponentInRoot("itemPrice8Output");
		}
		return itemPrice8Output;
	}
	protected HtmlOutputText getItemPrice8() {
		if (itemPrice8 == null) {
			itemPrice8 = (HtmlOutputText) findComponentInRoot("itemPrice8");
		}
		return itemPrice8;
	}
	protected HtmlPanelGroup getItemTotal8Group() {
		if (itemTotal8Group == null) {
			itemTotal8Group = (HtmlPanelGroup) findComponentInRoot("itemTotal8Group");
		}
		return itemTotal8Group;
	}
	protected HtmlOutputText getItemTotal8Output() {
		if (itemTotal8Output == null) {
			itemTotal8Output = (HtmlOutputText) findComponentInRoot("itemTotal8Output");
		}
		return itemTotal8Output;
	}
	protected HtmlOutputText getItemTotal8() {
		if (itemTotal8 == null) {
			itemTotal8 = (HtmlOutputText) findComponentInRoot("itemTotal8");
		}
		return itemTotal8;
	}
	protected HtmlPanelGroup getItemId9Group() {
		if (itemId9Group == null) {
			itemId9Group = (HtmlPanelGroup) findComponentInRoot("itemId9Group");
		}
		return itemId9Group;
	}
	protected HtmlOutputText getItemId9Output() {
		if (itemId9Output == null) {
			itemId9Output = (HtmlOutputText) findComponentInRoot("itemId9Output");
		}
		return itemId9Output;
	}
	protected HtmlOutputText getItemId9() {
		if (itemId9 == null) {
			itemId9 = (HtmlOutputText) findComponentInRoot("itemId9");
		}
		return itemId9;
	}
	protected HtmlPanelGroup getItemName9Group() {
		if (itemName9Group == null) {
			itemName9Group = (HtmlPanelGroup) findComponentInRoot("itemName9Group");
		}
		return itemName9Group;
	}
	protected HtmlOutputText getItemName9Output() {
		if (itemName9Output == null) {
			itemName9Output = (HtmlOutputText) findComponentInRoot("itemName9Output");
		}
		return itemName9Output;
	}
	protected HtmlOutputText getItemName9() {
		if (itemName9 == null) {
			itemName9 = (HtmlOutputText) findComponentInRoot("itemName9");
		}
		return itemName9;
	}
	protected HtmlPanelGroup getQuantity9Group() {
		if (quantity9Group == null) {
			quantity9Group = (HtmlPanelGroup) findComponentInRoot("quantity9Group");
		}
		return quantity9Group;
	}
	protected HtmlOutputText getItemQuantity9Output() {
		if (itemQuantity9Output == null) {
			itemQuantity9Output = (HtmlOutputText) findComponentInRoot("itemQuantity9Output");
		}
		return itemQuantity9Output;
	}
	protected HtmlOutputText getItemQuantity9() {
		if (itemQuantity9 == null) {
			itemQuantity9 = (HtmlOutputText) findComponentInRoot("itemQuantity9");
		}
		return itemQuantity9;
	}
	protected HtmlPanelGroup getItemSupplyWarehouseId9Group() {
		if (itemSupplyWarehouseId9Group == null) {
			itemSupplyWarehouseId9Group = (HtmlPanelGroup) findComponentInRoot("itemSupplyWarehouseId9Group");
		}
		return itemSupplyWarehouseId9Group;
	}
	protected HtmlOutputText getItemSupplyWarehouseId9Output() {
		if (itemSupplyWarehouseId9Output == null) {
			itemSupplyWarehouseId9Output = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId9Output");
		}
		return itemSupplyWarehouseId9Output;
	}
	protected HtmlOutputText getItemSupplyWarehouseId9() {
		if (itemSupplyWarehouseId9 == null) {
			itemSupplyWarehouseId9 = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId9");
		}
		return itemSupplyWarehouseId9;
	}
	protected HtmlPanelGroup getItemStockQuantity9Group() {
		if (itemStockQuantity9Group == null) {
			itemStockQuantity9Group = (HtmlPanelGroup) findComponentInRoot("itemStockQuantity9Group");
		}
		return itemStockQuantity9Group;
	}
	protected HtmlOutputText getItemStockQuantity9Output() {
		if (itemStockQuantity9Output == null) {
			itemStockQuantity9Output = (HtmlOutputText) findComponentInRoot("itemStockQuantity9Output");
		}
		return itemStockQuantity9Output;
	}
	protected HtmlPanelGroup getItemPrice9Group() {
		if (itemPrice9Group == null) {
			itemPrice9Group = (HtmlPanelGroup) findComponentInRoot("itemPrice9Group");
		}
		return itemPrice9Group;
	}
	protected HtmlOutputText getItemPrice9Output() {
		if (itemPrice9Output == null) {
			itemPrice9Output = (HtmlOutputText) findComponentInRoot("itemPrice9Output");
		}
		return itemPrice9Output;
	}
	protected HtmlOutputText getItemPrice9() {
		if (itemPrice9 == null) {
			itemPrice9 = (HtmlOutputText) findComponentInRoot("itemPrice9");
		}
		return itemPrice9;
	}
	protected HtmlPanelGroup getItemTotal9Group() {
		if (itemTotal9Group == null) {
			itemTotal9Group = (HtmlPanelGroup) findComponentInRoot("itemTotal9Group");
		}
		return itemTotal9Group;
	}
	protected HtmlOutputText getItemTotal9Output() {
		if (itemTotal9Output == null) {
			itemTotal9Output = (HtmlOutputText) findComponentInRoot("itemTotal9Output");
		}
		return itemTotal9Output;
	}
	protected HtmlOutputText getItemTotal9() {
		if (itemTotal9 == null) {
			itemTotal9 = (HtmlOutputText) findComponentInRoot("itemTotal9");
		}
		return itemTotal9;
	}
	protected HtmlPanelGroup getItemId10Group() {
		if (itemId10Group == null) {
			itemId10Group = (HtmlPanelGroup) findComponentInRoot("itemId10Group");
		}
		return itemId10Group;
	}
	protected HtmlOutputText getItemId10Output() {
		if (itemId10Output == null) {
			itemId10Output = (HtmlOutputText) findComponentInRoot("itemId10Output");
		}
		return itemId10Output;
	}
	protected HtmlOutputText getItemId10() {
		if (itemId10 == null) {
			itemId10 = (HtmlOutputText) findComponentInRoot("itemId10");
		}
		return itemId10;
	}
	protected HtmlPanelGroup getItemName10Group() {
		if (itemName10Group == null) {
			itemName10Group = (HtmlPanelGroup) findComponentInRoot("itemName10Group");
		}
		return itemName10Group;
	}
	protected HtmlOutputText getItemName10() {
		if (itemName10 == null) {
			itemName10 = (HtmlOutputText) findComponentInRoot("itemName10");
		}
		return itemName10;
	}
	protected HtmlPanelGroup getQuantity10Group() {
		if (quantity10Group == null) {
			quantity10Group = (HtmlPanelGroup) findComponentInRoot("quantity10Group");
		}
		return quantity10Group;
	}
	protected HtmlOutputText getItemQuantity10Output() {
		if (itemQuantity10Output == null) {
			itemQuantity10Output = (HtmlOutputText) findComponentInRoot("itemQuantity10Output");
		}
		return itemQuantity10Output;
	}
	protected HtmlOutputText getItemQuantity10() {
		if (itemQuantity10 == null) {
			itemQuantity10 = (HtmlOutputText) findComponentInRoot("itemQuantity10");
		}
		return itemQuantity10;
	}
	protected HtmlPanelGroup getItemSupplyWarehouseId10Group() {
		if (itemSupplyWarehouseId10Group == null) {
			itemSupplyWarehouseId10Group = (HtmlPanelGroup) findComponentInRoot("itemSupplyWarehouseId10Group");
		}
		return itemSupplyWarehouseId10Group;
	}
	protected HtmlOutputText getItemSupplyWarehouseId10Output() {
		if (itemSupplyWarehouseId10Output == null) {
			itemSupplyWarehouseId10Output = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId10Output");
		}
		return itemSupplyWarehouseId10Output;
	}
	protected HtmlOutputText getItemSupplyWarehouseId10() {
		if (itemSupplyWarehouseId10 == null) {
			itemSupplyWarehouseId10 = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId10");
		}
		return itemSupplyWarehouseId10;
	}
	protected HtmlPanelGroup getItemStockQuantity10Group() {
		if (itemStockQuantity10Group == null) {
			itemStockQuantity10Group = (HtmlPanelGroup) findComponentInRoot("itemStockQuantity10Group");
		}
		return itemStockQuantity10Group;
	}
	protected HtmlOutputText getItemStockQuantity10Output() {
		if (itemStockQuantity10Output == null) {
			itemStockQuantity10Output = (HtmlOutputText) findComponentInRoot("itemStockQuantity10Output");
		}
		return itemStockQuantity10Output;
	}
	protected HtmlOutputText getItemStockQuantity10() {
		if (itemStockQuantity10 == null) {
			itemStockQuantity10 = (HtmlOutputText) findComponentInRoot("itemStockQuantity10");
		}
		return itemStockQuantity10;
	}
	protected HtmlPanelGroup getItemPrice10Group() {
		if (itemPrice10Group == null) {
			itemPrice10Group = (HtmlPanelGroup) findComponentInRoot("itemPrice10Group");
		}
		return itemPrice10Group;
	}
	protected HtmlOutputText getItemPrice10Output() {
		if (itemPrice10Output == null) {
			itemPrice10Output = (HtmlOutputText) findComponentInRoot("itemPrice10Output");
		}
		return itemPrice10Output;
	}
	protected HtmlOutputText getItemPrice10() {
		if (itemPrice10 == null) {
			itemPrice10 = (HtmlOutputText) findComponentInRoot("itemPrice10");
		}
		return itemPrice10;
	}
	protected HtmlPanelGroup getItemTotal10Group() {
		if (itemTotal10Group == null) {
			itemTotal10Group = (HtmlPanelGroup) findComponentInRoot("itemTotal10Group");
		}
		return itemTotal10Group;
	}
	protected HtmlOutputText getItemTotal10Output() {
		if (itemTotal10Output == null) {
			itemTotal10Output = (HtmlOutputText) findComponentInRoot("itemTotal10Output");
		}
		return itemTotal10Output;
	}
	protected HtmlOutputText getItemTotal10() {
		if (itemTotal10 == null) {
			itemTotal10 = (HtmlOutputText) findComponentInRoot("itemTotal10");
		}
		return itemTotal10;
	}
	protected HtmlPanelGroup getItemId11Group() {
		if (itemId11Group == null) {
			itemId11Group = (HtmlPanelGroup) findComponentInRoot("itemId11Group");
		}
		return itemId11Group;
	}
	protected HtmlOutputText getItemId11Output() {
		if (itemId11Output == null) {
			itemId11Output = (HtmlOutputText) findComponentInRoot("itemId11Output");
		}
		return itemId11Output;
	}
	protected HtmlOutputText getItemId11() {
		if (itemId11 == null) {
			itemId11 = (HtmlOutputText) findComponentInRoot("itemId11");
		}
		return itemId11;
	}
	protected HtmlPanelGroup getItemName11Group() {
		if (itemName11Group == null) {
			itemName11Group = (HtmlPanelGroup) findComponentInRoot("itemName11Group");
		}
		return itemName11Group;
	}
	protected HtmlOutputText getItemName11Output() {
		if (itemName11Output == null) {
			itemName11Output = (HtmlOutputText) findComponentInRoot("itemName11Output");
		}
		return itemName11Output;
	}
	protected HtmlOutputText getItemName11() {
		if (itemName11 == null) {
			itemName11 = (HtmlOutputText) findComponentInRoot("itemName11");
		}
		return itemName11;
	}
	protected HtmlPanelGroup getQuantity11Group() {
		if (quantity11Group == null) {
			quantity11Group = (HtmlPanelGroup) findComponentInRoot("quantity11Group");
		}
		return quantity11Group;
	}
	protected HtmlOutputText getItemQuantity11Output() {
		if (itemQuantity11Output == null) {
			itemQuantity11Output = (HtmlOutputText) findComponentInRoot("itemQuantity11Output");
		}
		return itemQuantity11Output;
	}
	protected HtmlOutputText getItemQuantity11() {
		if (itemQuantity11 == null) {
			itemQuantity11 = (HtmlOutputText) findComponentInRoot("itemQuantity11");
		}
		return itemQuantity11;
	}
	protected HtmlPanelGroup getItemSupplyWarehouseId11Group() {
		if (itemSupplyWarehouseId11Group == null) {
			itemSupplyWarehouseId11Group = (HtmlPanelGroup) findComponentInRoot("itemSupplyWarehouseId11Group");
		}
		return itemSupplyWarehouseId11Group;
	}
	protected HtmlOutputText getItemSupplyWarehouseId11Output() {
		if (itemSupplyWarehouseId11Output == null) {
			itemSupplyWarehouseId11Output = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId11Output");
		}
		return itemSupplyWarehouseId11Output;
	}
	protected HtmlOutputText getItemSupplyWarehouseId11() {
		if (itemSupplyWarehouseId11 == null) {
			itemSupplyWarehouseId11 = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId11");
		}
		return itemSupplyWarehouseId11;
	}
	protected HtmlPanelGroup getItemStockQuantity11Group() {
		if (itemStockQuantity11Group == null) {
			itemStockQuantity11Group = (HtmlPanelGroup) findComponentInRoot("itemStockQuantity11Group");
		}
		return itemStockQuantity11Group;
	}
	protected HtmlOutputText getItemStockQuantity11Output() {
		if (itemStockQuantity11Output == null) {
			itemStockQuantity11Output = (HtmlOutputText) findComponentInRoot("itemStockQuantity11Output");
		}
		return itemStockQuantity11Output;
	}
	protected HtmlOutputText getItemStockQuantity11() {
		if (itemStockQuantity11 == null) {
			itemStockQuantity11 = (HtmlOutputText) findComponentInRoot("itemStockQuantity11");
		}
		return itemStockQuantity11;
	}
	protected HtmlPanelGroup getItemPrice11Group() {
		if (itemPrice11Group == null) {
			itemPrice11Group = (HtmlPanelGroup) findComponentInRoot("itemPrice11Group");
		}
		return itemPrice11Group;
	}
	protected HtmlOutputText getItemPrice11Output() {
		if (itemPrice11Output == null) {
			itemPrice11Output = (HtmlOutputText) findComponentInRoot("itemPrice11Output");
		}
		return itemPrice11Output;
	}
	protected HtmlOutputText getItemPrice11() {
		if (itemPrice11 == null) {
			itemPrice11 = (HtmlOutputText) findComponentInRoot("itemPrice11");
		}
		return itemPrice11;
	}
	protected HtmlPanelGroup getItemTotal11Group() {
		if (itemTotal11Group == null) {
			itemTotal11Group = (HtmlPanelGroup) findComponentInRoot("itemTotal11Group");
		}
		return itemTotal11Group;
	}
	protected HtmlOutputText getItemTotal11Output() {
		if (itemTotal11Output == null) {
			itemTotal11Output = (HtmlOutputText) findComponentInRoot("itemTotal11Output");
		}
		return itemTotal11Output;
	}
	protected HtmlOutputText getItemTotal11() {
		if (itemTotal11 == null) {
			itemTotal11 = (HtmlOutputText) findComponentInRoot("itemTotal11");
		}
		return itemTotal11;
	}
	protected HtmlPanelGroup getItemId12Group() {
		if (itemId12Group == null) {
			itemId12Group = (HtmlPanelGroup) findComponentInRoot("itemId12Group");
		}
		return itemId12Group;
	}
	protected HtmlOutputText getItemId12Output() {
		if (itemId12Output == null) {
			itemId12Output = (HtmlOutputText) findComponentInRoot("itemId12Output");
		}
		return itemId12Output;
	}
	protected HtmlOutputText getItemId12() {
		if (itemId12 == null) {
			itemId12 = (HtmlOutputText) findComponentInRoot("itemId12");
		}
		return itemId12;
	}
	protected HtmlPanelGroup getItemName12Group() {
		if (itemName12Group == null) {
			itemName12Group = (HtmlPanelGroup) findComponentInRoot("itemName12Group");
		}
		return itemName12Group;
	}
	protected HtmlOutputText getItemName12Output() {
		if (itemName12Output == null) {
			itemName12Output = (HtmlOutputText) findComponentInRoot("itemName12Output");
		}
		return itemName12Output;
	}
	protected HtmlOutputText getItemName12() {
		if (itemName12 == null) {
			itemName12 = (HtmlOutputText) findComponentInRoot("itemName12");
		}
		return itemName12;
	}
	protected HtmlPanelGroup getQuantity12Group() {
		if (quantity12Group == null) {
			quantity12Group = (HtmlPanelGroup) findComponentInRoot("quantity12Group");
		}
		return quantity12Group;
	}
	protected HtmlOutputText getItemQuantity12Output() {
		if (itemQuantity12Output == null) {
			itemQuantity12Output = (HtmlOutputText) findComponentInRoot("itemQuantity12Output");
		}
		return itemQuantity12Output;
	}
	protected HtmlOutputText getItemQuantity12() {
		if (itemQuantity12 == null) {
			itemQuantity12 = (HtmlOutputText) findComponentInRoot("itemQuantity12");
		}
		return itemQuantity12;
	}
	protected HtmlPanelGroup getItemSupplyWarehouseId12Group() {
		if (itemSupplyWarehouseId12Group == null) {
			itemSupplyWarehouseId12Group = (HtmlPanelGroup) findComponentInRoot("itemSupplyWarehouseId12Group");
		}
		return itemSupplyWarehouseId12Group;
	}
	protected HtmlOutputText getItemSupplyWarehouseId12Output() {
		if (itemSupplyWarehouseId12Output == null) {
			itemSupplyWarehouseId12Output = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId12Output");
		}
		return itemSupplyWarehouseId12Output;
	}
	protected HtmlOutputText getItemSupplyWarehouseId12() {
		if (itemSupplyWarehouseId12 == null) {
			itemSupplyWarehouseId12 = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId12");
		}
		return itemSupplyWarehouseId12;
	}
	protected HtmlPanelGroup getItemStockQuantity12Group() {
		if (itemStockQuantity12Group == null) {
			itemStockQuantity12Group = (HtmlPanelGroup) findComponentInRoot("itemStockQuantity12Group");
		}
		return itemStockQuantity12Group;
	}
	protected HtmlOutputText getItemStockQuantity12Output() {
		if (itemStockQuantity12Output == null) {
			itemStockQuantity12Output = (HtmlOutputText) findComponentInRoot("itemStockQuantity12Output");
		}
		return itemStockQuantity12Output;
	}
	protected HtmlOutputText getItemStockQuantity12() {
		if (itemStockQuantity12 == null) {
			itemStockQuantity12 = (HtmlOutputText) findComponentInRoot("itemStockQuantity12");
		}
		return itemStockQuantity12;
	}
	protected HtmlPanelGroup getItemPrice12Group() {
		if (itemPrice12Group == null) {
			itemPrice12Group = (HtmlPanelGroup) findComponentInRoot("itemPrice12Group");
		}
		return itemPrice12Group;
	}
	protected HtmlOutputText getItemPrice12Output() {
		if (itemPrice12Output == null) {
			itemPrice12Output = (HtmlOutputText) findComponentInRoot("itemPrice12Output");
		}
		return itemPrice12Output;
	}
	protected HtmlOutputText getItemPrice12() {
		if (itemPrice12 == null) {
			itemPrice12 = (HtmlOutputText) findComponentInRoot("itemPrice12");
		}
		return itemPrice12;
	}
	protected HtmlPanelGroup getItemTotal12Group() {
		if (itemTotal12Group == null) {
			itemTotal12Group = (HtmlPanelGroup) findComponentInRoot("itemTotal12Group");
		}
		return itemTotal12Group;
	}
	protected HtmlOutputText getItemTotal12Output() {
		if (itemTotal12Output == null) {
			itemTotal12Output = (HtmlOutputText) findComponentInRoot("itemTotal12Output");
		}
		return itemTotal12Output;
	}
	protected HtmlOutputText getItemTotal12() {
		if (itemTotal12 == null) {
			itemTotal12 = (HtmlOutputText) findComponentInRoot("itemTotal12");
		}
		return itemTotal12;
	}
	protected HtmlPanelGroup getItemId13Group() {
		if (itemId13Group == null) {
			itemId13Group = (HtmlPanelGroup) findComponentInRoot("itemId13Group");
		}
		return itemId13Group;
	}
	protected HtmlOutputText getItemId13Output() {
		if (itemId13Output == null) {
			itemId13Output = (HtmlOutputText) findComponentInRoot("itemId13Output");
		}
		return itemId13Output;
	}
	protected HtmlOutputText getItemId13() {
		if (itemId13 == null) {
			itemId13 = (HtmlOutputText) findComponentInRoot("itemId13");
		}
		return itemId13;
	}
	protected HtmlPanelGroup getItemName13Group() {
		if (itemName13Group == null) {
			itemName13Group = (HtmlPanelGroup) findComponentInRoot("itemName13Group");
		}
		return itemName13Group;
	}
	protected HtmlOutputText getItemName13Output() {
		if (itemName13Output == null) {
			itemName13Output = (HtmlOutputText) findComponentInRoot("itemName13Output");
		}
		return itemName13Output;
	}
	protected HtmlOutputText getItemName13() {
		if (itemName13 == null) {
			itemName13 = (HtmlOutputText) findComponentInRoot("itemName13");
		}
		return itemName13;
	}
	protected HtmlPanelGroup getQuantity13Group() {
		if (quantity13Group == null) {
			quantity13Group = (HtmlPanelGroup) findComponentInRoot("quantity13Group");
		}
		return quantity13Group;
	}
	protected HtmlOutputText getItemQuantity13Output() {
		if (itemQuantity13Output == null) {
			itemQuantity13Output = (HtmlOutputText) findComponentInRoot("itemQuantity13Output");
		}
		return itemQuantity13Output;
	}
	protected HtmlOutputText getItemQuantity13() {
		if (itemQuantity13 == null) {
			itemQuantity13 = (HtmlOutputText) findComponentInRoot("itemQuantity13");
		}
		return itemQuantity13;
	}
	protected HtmlPanelGroup getItemSupplyWarehouseId13Group() {
		if (itemSupplyWarehouseId13Group == null) {
			itemSupplyWarehouseId13Group = (HtmlPanelGroup) findComponentInRoot("itemSupplyWarehouseId13Group");
		}
		return itemSupplyWarehouseId13Group;
	}
	protected HtmlOutputText getItemSupplyWarehouseId13Output() {
		if (itemSupplyWarehouseId13Output == null) {
			itemSupplyWarehouseId13Output = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId13Output");
		}
		return itemSupplyWarehouseId13Output;
	}
	protected HtmlOutputText getItemSupplyWarehouseId13() {
		if (itemSupplyWarehouseId13 == null) {
			itemSupplyWarehouseId13 = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId13");
		}
		return itemSupplyWarehouseId13;
	}
	protected HtmlPanelGroup getItemStockQuantity13Group() {
		if (itemStockQuantity13Group == null) {
			itemStockQuantity13Group = (HtmlPanelGroup) findComponentInRoot("itemStockQuantity13Group");
		}
		return itemStockQuantity13Group;
	}
	protected HtmlOutputText getItemStockQuantity13Output() {
		if (itemStockQuantity13Output == null) {
			itemStockQuantity13Output = (HtmlOutputText) findComponentInRoot("itemStockQuantity13Output");
		}
		return itemStockQuantity13Output;
	}
	protected HtmlOutputText getItemStockQuantity13() {
		if (itemStockQuantity13 == null) {
			itemStockQuantity13 = (HtmlOutputText) findComponentInRoot("itemStockQuantity13");
		}
		return itemStockQuantity13;
	}
	protected HtmlPanelGroup getItemPrice13Group() {
		if (itemPrice13Group == null) {
			itemPrice13Group = (HtmlPanelGroup) findComponentInRoot("itemPrice13Group");
		}
		return itemPrice13Group;
	}
	protected HtmlOutputText getItemPrice13Output() {
		if (itemPrice13Output == null) {
			itemPrice13Output = (HtmlOutputText) findComponentInRoot("itemPrice13Output");
		}
		return itemPrice13Output;
	}
	protected HtmlOutputText getItemPrice13() {
		if (itemPrice13 == null) {
			itemPrice13 = (HtmlOutputText) findComponentInRoot("itemPrice13");
		}
		return itemPrice13;
	}
	protected HtmlPanelGroup getItemTotal13Group() {
		if (itemTotal13Group == null) {
			itemTotal13Group = (HtmlPanelGroup) findComponentInRoot("itemTotal13Group");
		}
		return itemTotal13Group;
	}
	protected HtmlOutputText getItemTotal13Output() {
		if (itemTotal13Output == null) {
			itemTotal13Output = (HtmlOutputText) findComponentInRoot("itemTotal13Output");
		}
		return itemTotal13Output;
	}
	protected HtmlOutputText getItemTotal13() {
		if (itemTotal13 == null) {
			itemTotal13 = (HtmlOutputText) findComponentInRoot("itemTotal13");
		}
		return itemTotal13;
	}
	protected HtmlPanelGroup getItemId14Group() {
		if (itemId14Group == null) {
			itemId14Group = (HtmlPanelGroup) findComponentInRoot("itemId14Group");
		}
		return itemId14Group;
	}
	protected HtmlOutputText getItemId14Output() {
		if (itemId14Output == null) {
			itemId14Output = (HtmlOutputText) findComponentInRoot("itemId14Output");
		}
		return itemId14Output;
	}
	protected HtmlOutputText getItemId14() {
		if (itemId14 == null) {
			itemId14 = (HtmlOutputText) findComponentInRoot("itemId14");
		}
		return itemId14;
	}
	protected HtmlPanelGroup getItemName14Group() {
		if (itemName14Group == null) {
			itemName14Group = (HtmlPanelGroup) findComponentInRoot("itemName14Group");
		}
		return itemName14Group;
	}
	protected HtmlOutputText getItemName14Output() {
		if (itemName14Output == null) {
			itemName14Output = (HtmlOutputText) findComponentInRoot("itemName14Output");
		}
		return itemName14Output;
	}
	protected HtmlOutputText getItemName14() {
		if (itemName14 == null) {
			itemName14 = (HtmlOutputText) findComponentInRoot("itemName14");
		}
		return itemName14;
	}
	protected HtmlPanelGroup getQuantity14Group() {
		if (quantity14Group == null) {
			quantity14Group = (HtmlPanelGroup) findComponentInRoot("quantity14Group");
		}
		return quantity14Group;
	}
	protected HtmlOutputText getItemQuantity14Output() {
		if (itemQuantity14Output == null) {
			itemQuantity14Output = (HtmlOutputText) findComponentInRoot("itemQuantity14Output");
		}
		return itemQuantity14Output;
	}
	protected HtmlOutputText getItemQuantity14() {
		if (itemQuantity14 == null) {
			itemQuantity14 = (HtmlOutputText) findComponentInRoot("itemQuantity14");
		}
		return itemQuantity14;
	}
	protected HtmlPanelGroup getItemSupplyWarehouseId14Group() {
		if (itemSupplyWarehouseId14Group == null) {
			itemSupplyWarehouseId14Group = (HtmlPanelGroup) findComponentInRoot("itemSupplyWarehouseId14Group");
		}
		return itemSupplyWarehouseId14Group;
	}
	protected HtmlOutputText getItemSupplyWarehouseId14Output() {
		if (itemSupplyWarehouseId14Output == null) {
			itemSupplyWarehouseId14Output = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId14Output");
		}
		return itemSupplyWarehouseId14Output;
	}
	protected HtmlOutputText getItemSupplyWarehouseId14() {
		if (itemSupplyWarehouseId14 == null) {
			itemSupplyWarehouseId14 = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId14");
		}
		return itemSupplyWarehouseId14;
	}
	protected HtmlPanelGroup getItemStockQuantity14Group() {
		if (itemStockQuantity14Group == null) {
			itemStockQuantity14Group = (HtmlPanelGroup) findComponentInRoot("itemStockQuantity14Group");
		}
		return itemStockQuantity14Group;
	}
	protected HtmlOutputText getItemStockQuantity14Output() {
		if (itemStockQuantity14Output == null) {
			itemStockQuantity14Output = (HtmlOutputText) findComponentInRoot("itemStockQuantity14Output");
		}
		return itemStockQuantity14Output;
	}
	protected HtmlOutputText getItemStockQuantity14() {
		if (itemStockQuantity14 == null) {
			itemStockQuantity14 = (HtmlOutputText) findComponentInRoot("itemStockQuantity14");
		}
		return itemStockQuantity14;
	}
	protected HtmlPanelGroup getItemPrice14Group() {
		if (itemPrice14Group == null) {
			itemPrice14Group = (HtmlPanelGroup) findComponentInRoot("itemPrice14Group");
		}
		return itemPrice14Group;
	}
	protected HtmlOutputText getItemPrice14Output() {
		if (itemPrice14Output == null) {
			itemPrice14Output = (HtmlOutputText) findComponentInRoot("itemPrice14Output");
		}
		return itemPrice14Output;
	}
	protected HtmlOutputText getItemPrice14() {
		if (itemPrice14 == null) {
			itemPrice14 = (HtmlOutputText) findComponentInRoot("itemPrice14");
		}
		return itemPrice14;
	}
	protected HtmlPanelGroup getItemTotal14Group() {
		if (itemTotal14Group == null) {
			itemTotal14Group = (HtmlPanelGroup) findComponentInRoot("itemTotal14Group");
		}
		return itemTotal14Group;
	}
	protected HtmlOutputText getItemTotal14Output() {
		if (itemTotal14Output == null) {
			itemTotal14Output = (HtmlOutputText) findComponentInRoot("itemTotal14Output");
		}
		return itemTotal14Output;
	}
	protected HtmlOutputText getItemTotal14() {
		if (itemTotal14 == null) {
			itemTotal14 = (HtmlOutputText) findComponentInRoot("itemTotal14");
		}
		return itemTotal14;
	}
	protected HtmlPanelGroup getItemId15Group() {
		if (itemId15Group == null) {
			itemId15Group = (HtmlPanelGroup) findComponentInRoot("itemId15Group");
		}
		return itemId15Group;
	}
	protected HtmlOutputText getItemId15Output() {
		if (itemId15Output == null) {
			itemId15Output = (HtmlOutputText) findComponentInRoot("itemId15Output");
		}
		return itemId15Output;
	}
	protected HtmlOutputText getItemId15() {
		if (itemId15 == null) {
			itemId15 = (HtmlOutputText) findComponentInRoot("itemId15");
		}
		return itemId15;
	}
	protected HtmlPanelGroup getItemName15Group() {
		if (itemName15Group == null) {
			itemName15Group = (HtmlPanelGroup) findComponentInRoot("itemName15Group");
		}
		return itemName15Group;
	}
	protected HtmlOutputText getItemName15Output() {
		if (itemName15Output == null) {
			itemName15Output = (HtmlOutputText) findComponentInRoot("itemName15Output");
		}
		return itemName15Output;
	}
	protected HtmlOutputText getItemName15() {
		if (itemName15 == null) {
			itemName15 = (HtmlOutputText) findComponentInRoot("itemName15");
		}
		return itemName15;
	}
	protected HtmlPanelGroup getQuantity15Group() {
		if (quantity15Group == null) {
			quantity15Group = (HtmlPanelGroup) findComponentInRoot("quantity15Group");
		}
		return quantity15Group;
	}
	protected HtmlOutputText getItemQuantity15Output() {
		if (itemQuantity15Output == null) {
			itemQuantity15Output = (HtmlOutputText) findComponentInRoot("itemQuantity15Output");
		}
		return itemQuantity15Output;
	}
	protected HtmlOutputText getItemQuantity15() {
		if (itemQuantity15 == null) {
			itemQuantity15 = (HtmlOutputText) findComponentInRoot("itemQuantity15");
		}
		return itemQuantity15;
	}
	protected HtmlPanelGroup getItemSupplyWarehouseId15Group() {
		if (itemSupplyWarehouseId15Group == null) {
			itemSupplyWarehouseId15Group = (HtmlPanelGroup) findComponentInRoot("itemSupplyWarehouseId15Group");
		}
		return itemSupplyWarehouseId15Group;
	}
	protected HtmlOutputText getItemSupplyWarehouseId15Output() {
		if (itemSupplyWarehouseId15Output == null) {
			itemSupplyWarehouseId15Output = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId15Output");
		}
		return itemSupplyWarehouseId15Output;
	}
	protected HtmlOutputText getItemSupplyWarehouseId15() {
		if (itemSupplyWarehouseId15 == null) {
			itemSupplyWarehouseId15 = (HtmlOutputText) findComponentInRoot("itemSupplyWarehouseId15");
		}
		return itemSupplyWarehouseId15;
	}
	protected HtmlPanelGroup getItemStockQuantity15Group() {
		if (itemStockQuantity15Group == null) {
			itemStockQuantity15Group = (HtmlPanelGroup) findComponentInRoot("itemStockQuantity15Group");
		}
		return itemStockQuantity15Group;
	}
	protected HtmlOutputText getItemStockQuantity15Output() {
		if (itemStockQuantity15Output == null) {
			itemStockQuantity15Output = (HtmlOutputText) findComponentInRoot("itemStockQuantity15Output");
		}
		return itemStockQuantity15Output;
	}
	protected HtmlOutputText getItemStockQuantity15() {
		if (itemStockQuantity15 == null) {
			itemStockQuantity15 = (HtmlOutputText) findComponentInRoot("itemStockQuantity15");
		}
		return itemStockQuantity15;
	}
	protected HtmlPanelGroup getItemPrice15Group() {
		if (itemPrice15Group == null) {
			itemPrice15Group = (HtmlPanelGroup) findComponentInRoot("itemPrice15Group");
		}
		return itemPrice15Group;
	}
	protected HtmlOutputText getItemPrice15Output() {
		if (itemPrice15Output == null) {
			itemPrice15Output = (HtmlOutputText) findComponentInRoot("itemPrice15Output");
		}
		return itemPrice15Output;
	}
	protected HtmlOutputText getItemPrice15() {
		if (itemPrice15 == null) {
			itemPrice15 = (HtmlOutputText) findComponentInRoot("itemPrice15");
		}
		return itemPrice15;
	}
	protected HtmlPanelGroup getItemTotal15Group() {
		if (itemTotal15Group == null) {
			itemTotal15Group = (HtmlPanelGroup) findComponentInRoot("itemTotal15Group");
		}
		return itemTotal15Group;
	}
	protected HtmlOutputText getItemTotal15Output() {
		if (itemTotal15Output == null) {
			itemTotal15Output = (HtmlOutputText) findComponentInRoot("itemTotal15Output");
		}
		return itemTotal15Output;
	}
	protected HtmlOutputText getItemTotal15() {
		if (itemTotal15 == null) {
			itemTotal15 = (HtmlOutputText) findComponentInRoot("itemTotal15");
		}
		return itemTotal15;
	}
	protected HtmlPanelGroup getGrandTotalGroup() {
		if (grandTotalGroup == null) {
			grandTotalGroup = (HtmlPanelGroup) findComponentInRoot("grandTotalGroup");
		}
		return grandTotalGroup;
	}
	protected HtmlOutputText getOutGrandTotalOutput() {
		if (outGrandTotalOutput == null) {
			outGrandTotalOutput = (HtmlOutputText) findComponentInRoot("outGrandTotalOutput");
		}
		return outGrandTotalOutput;
	}
	protected HtmlPanelGroup getStatusGroup() {
		if (statusGroup == null) {
			statusGroup = (HtmlPanelGroup) findComponentInRoot("statusGroup");
		}
		return statusGroup;
	}
	protected HtmlOutputText getOutStatusOutput() {
		if (outStatusOutput == null) {
			outStatusOutput = (HtmlOutputText) findComponentInRoot("outStatusOutput");
		}
		return outStatusOutput;
	}
	protected HtmlOutputText getOutCustomerId() {
		if (outCustomerId == null) {
			outCustomerId = (HtmlOutputText) findComponentInRoot("outCustomerId");
		}
		return outCustomerId;
	}
	protected HtmlPanelGroup getCouponGroup() {
		if (couponGroup == null) {
			couponGroup = (HtmlPanelGroup) findComponentInRoot("cGroup");
		}
		return couponGroup;
	}
	protected HtmlOutputText getOutCouponOutput() {
		if (outCouponOutput == null) {
			outCouponOutput = (HtmlOutputText) findComponentInRoot("outCouponOutput");
		}
		return outCouponOutput;
	}

}