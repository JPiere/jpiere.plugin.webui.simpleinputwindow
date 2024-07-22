/******************************************************************************
 * Product: JPiere                                                            *
 * Copyright (C) Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)                  *
 *                                                                            *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY.                          *
 * See the GNU General Public License for more details.                       *
 *                                                                            *
 * JPiere is maintained by OSS ERP Solutions Co., Ltd.                        *
 * (http://www.oss-erp.co.jp)                                                 *
 *****************************************************************************/
package jpiere.plugin.simpleinputwindow.callout;

import java.sql.Timestamp;
import java.util.Properties;

import org.adempiere.base.Core;
import org.compiere.model.GridTab;
import org.compiere.model.MInOutLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.util.Env;

import jpiere.plugin.simpleinputwindow.base.ISimpleInputWindowCallout;
import jpiere.plugin.simpleinputwindow.form.SimpleInputWindowDataBinder;

public class SIWCalloutInvoiceTax implements ISimpleInputWindowCallout {

	@Override
	public String start (SimpleInputWindowDataBinder dataBinder,int rowIndex, String ColumnName, Object newValue, Object oldValue)
	{
		String column = ColumnName;
		if (newValue == null)
			return "";

		int WindowNo = dataBinder.getSimpleInputWindow().getGridTab().getWindowNo();
		GridTab mTab = dataBinder.getSimpleInputWindow().getGridTab();
		Properties ctx = Env.getCtx();
		int tabNo = mTab.getTabNo();

		//	Check Product
		int M_Product_ID = 0;
		if (column.equals("M_Product_ID"))
			M_Product_ID = ((Integer)newValue).intValue();
		else
			M_Product_ID = Env.getContextAsInt(ctx, WindowNo, tabNo, "M_Product_ID");
		int C_Charge_ID = 0;
		if (column.equals("C_Charge_ID"))
			C_Charge_ID = ((Integer)newValue).intValue();
		else
			C_Charge_ID = Env.getContextAsInt(ctx, WindowNo, tabNo, "C_Charge_ID");

		if (M_Product_ID == 0 && C_Charge_ID == 0)
			return new SIWCalloutInvoiceAmt().start(dataBinder, rowIndex, ColumnName, newValue, oldValue);

		//	Check Partner Location
		int shipC_BPartner_Location_ID = 0;
		shipC_BPartner_Location_ID = Env.getContextAsInt(ctx, WindowNo, "C_BPartner_Location_ID");
		if (shipC_BPartner_Location_ID == 0)
			return new SIWCalloutInvoiceAmt().start(dataBinder, rowIndex, ColumnName, newValue, oldValue);

		//
		Timestamp billDate = Env.getContextAsDate(ctx, WindowNo, "DateInvoiced");

		int AD_Org_ID = Env.getContextAsInt(ctx, WindowNo, "AD_Org_ID");
		
		String deliveryViaRule = getLineDeliveryViaRule(ctx, WindowNo, mTab);
		int dropshipLocationId = getDropShipLocationId(ctx, WindowNo, mTab);
		int C_Tax_ID = Core.getTaxLookup().get(ctx, M_Product_ID, C_Charge_ID, billDate, billDate,
			AD_Org_ID, 0, shipC_BPartner_Location_ID, shipC_BPartner_Location_ID,dropshipLocationId,
			"Y".equals(Env.getContext(ctx, WindowNo, "IsSOTrx")), deliveryViaRule, null);
		
		if (C_Tax_ID == 0)
			;
		else
			dataBinder.setValue(rowIndex, "C_Tax_ID", Integer.valueOf(C_Tax_ID));
		//


		return "";
	}	//	tax

	private String getLineDeliveryViaRule(Properties ctx, int windowNo, GridTab mTab) {
		if (mTab.getValue("C_OrderLine_ID") != null) {
			int C_OrderLine_ID = (Integer) mTab.getValue("C_OrderLine_ID");
			if (C_OrderLine_ID > 0) {
				MOrderLine orderLine = new MOrderLine(ctx, C_OrderLine_ID, null);
				return orderLine.getParent().getDeliveryViaRule();
			}
		}
		if (mTab.getValue("M_InOutLine_ID") != null) {
			int M_InOutLine_ID = (Integer) mTab.getValue("M_InOutLine_ID");
			if (M_InOutLine_ID > 0) {
				MInOutLine ioLine = new MInOutLine(ctx, M_InOutLine_ID, null);
				return ioLine.getParent().getDeliveryViaRule();
			}
		}
		int C_Order_ID = Env.getContextAsInt(ctx, windowNo, "C_Order_ID", true);
		if (C_Order_ID > 0) {
			MOrder order = new MOrder(ctx, C_Order_ID, null);
			return order.getDeliveryViaRule();
		}
		return null;
	}

	/**
	 * Get the drop shipment location ID from the related order
	 * @param ctx
	 * @param windowNo
	 * @param mTab
	 * @return
	 */
	private int getDropShipLocationId(Properties ctx, int windowNo, GridTab mTab) {
		if (mTab.getValue("C_OrderLine_ID") != null) {
			int C_OrderLine_ID = (Integer) mTab.getValue("C_OrderLine_ID");
			if (C_OrderLine_ID > 0) {
				MOrderLine orderLine = new MOrderLine(ctx, C_OrderLine_ID, null);
				return orderLine.getParent().getDropShip_Location_ID();
			}
		}
		int C_Order_ID = Env.getContextAsInt(ctx, windowNo, "C_Order_ID", true);
		if (C_Order_ID > 0) {
			MOrder order = new MOrder(ctx, C_Order_ID, null);
			return order.getDropShip_Location_ID();
		}
		return -1;
	}


}
