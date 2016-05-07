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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Properties;

import jpiere.plugin.simpleinputwindow.base.ISimpleInputWindowCallout;
import jpiere.plugin.simpleinputwindow.form.SimpleInputWindowDataBinder;

import org.compiere.model.MOrderLine;
import org.compiere.model.MPriceList;
import org.compiere.model.MProduct;
import org.compiere.model.MProductPricing;
import org.compiere.model.MStorageReservation;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

public class SIWCalloutInvoiceProduct implements ISimpleInputWindowCallout {

	/**
	 *
	 *  M_Product_ID, M_Locator_ID, M_AttributeSetInstance_ID in M_InventoryLine Table
	 **/
	@Override
	public String start(SimpleInputWindowDataBinder dataBinder,int rowIndex, String ColumnName, Object newValue, Object oldValue)
	{

		Integer M_Product_ID = (Integer)newValue;
		if (M_Product_ID == null || M_Product_ID.intValue() == 0)
			return "";

		int WindowNo = dataBinder.getSimpleInputWindow().getGridTab().getWindowNo();
		Properties ctx = Env.getCtx();
		int tabNo = dataBinder.getSimpleInputWindow().getGridTab().getTabNo();

		dataBinder.setValue(rowIndex, "C_Charge_ID", null);
		//	Set Attribute
		if (Env.getContextAsInt(ctx, WindowNo, Env.TAB_INFO, "M_Product_ID") == M_Product_ID.intValue()
			&& Env.getContextAsInt(ctx, WindowNo, Env.TAB_INFO, "M_AttributeSetInstance_ID") != 0)
			dataBinder.setValue(rowIndex,"M_AttributeSetInstance_ID", Env.getContextAsInt(Env.getCtx(), WindowNo, Env.TAB_INFO, "M_AttributeSetInstance_ID"));
		else
			dataBinder.setValue(rowIndex,"M_AttributeSetInstance_ID", null);


		int C_BPartner_ID = Env.getContextAsInt(ctx, WindowNo, "C_BPartner_ID");
		BigDecimal Qty = (BigDecimal)dataBinder.getValue(rowIndex, "QtyInvoiced");
		boolean IsSOTrx = Env.getContext(ctx, WindowNo, "IsSOTrx").equals("Y");
		MProductPricing pp = new MProductPricing (M_Product_ID.intValue(), C_BPartner_ID, Qty, IsSOTrx);
		//
		int M_PriceList_ID = Env.getContextAsInt(ctx, WindowNo, "M_PriceList_ID");
		pp.setM_PriceList_ID(M_PriceList_ID);
		Timestamp invoiceDate = (Timestamp)Env.getContextAsDate(ctx, WindowNo,"DateInvoiced");
		/** PLV is only accurate if PL selected in header */
		int M_PriceList_Version_ID = Env.getContextAsInt(ctx, WindowNo, "M_PriceList_Version_ID");
		if ( M_PriceList_Version_ID == 0 && M_PriceList_ID > 0)
		{
			String sql = "SELECT plv.M_PriceList_Version_ID "
				+ "FROM M_PriceList_Version plv "
				+ "WHERE plv.M_PriceList_ID=? "						//	1
				+ " AND plv.ValidFrom <= ? "
				+ "ORDER BY plv.ValidFrom DESC";
			//	Use newest price list - may not be future

			M_PriceList_Version_ID = DB.getSQLValueEx(null, sql, M_PriceList_ID, invoiceDate);
			if ( M_PriceList_Version_ID > 0 )
				Env.setContext(ctx, WindowNo, "M_PriceList_Version_ID", M_PriceList_Version_ID );
		}
		pp.setM_PriceList_Version_ID(M_PriceList_Version_ID);
		pp.setPriceDate(invoiceDate);

		dataBinder.setValue(rowIndex, "PriceList", pp.getPriceList());
		dataBinder.setValue(rowIndex, "PriceLimit", pp.getPriceLimit());
		dataBinder.setValue(rowIndex, "PriceActual", pp.getPriceStd());
		dataBinder.setValue(rowIndex, "PriceEntered", pp.getPriceStd());
		dataBinder.setValue(rowIndex, "C_UOM_ID", new Integer(pp.getC_UOM_ID()));
		dataBinder.setValue(rowIndex, "QtyInvoiced", dataBinder.getValue(rowIndex, "QtyEntered"));
		Env.setContext(ctx, WindowNo, "EnforcePriceLimit", pp.isEnforcePriceLimit() ? "Y" : "N");
		Env.setContext(ctx, WindowNo, "DiscountSchema", pp.isDiscountSchema() ? "Y" : "N");


		int StdPrecision = MPriceList.getStandardPrecision(ctx, M_PriceList_ID);
		//	Line Net Amt
		BigDecimal LineNetAmt = ((BigDecimal)dataBinder.getValue(rowIndex,"QtyEntered")).multiply(pp.getPriceStd());
		if (LineNetAmt.scale() > StdPrecision)
			LineNetAmt = LineNetAmt.setScale(StdPrecision, BigDecimal.ROUND_HALF_UP);

		dataBinder.setValue(rowIndex, "LineNetAmt", LineNetAmt);

		return "";
	}
}
