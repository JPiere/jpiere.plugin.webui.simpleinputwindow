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

import jpiere.plugin.simpleinputwindow.base.ISimpleInputWindowCallout;
import jpiere.plugin.simpleinputwindow.form.SimpleInputWindowDataBinder;

import org.compiere.model.Tax;
import org.compiere.util.Env;

public class SIWCalloutInvoiceTax implements ISimpleInputWindowCallout {

	@Override
	public String start (SimpleInputWindowDataBinder dataBinder,int rowIndex, String ColumnName, Object newValue, Object oldValue)
	{
		String column = ColumnName;
		if (newValue == null)
			return "";

		int WindowNo = dataBinder.getSimpleInputWindow().getGridTab().getWindowNo();
		Properties ctx = Env.getCtx();
		int tabNo = dataBinder.getSimpleInputWindow().getGridTab().getTabNo();

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

		//
		int C_Tax_ID = Tax.get (ctx, M_Product_ID, C_Charge_ID, billDate, billDate,
			AD_Org_ID, 0, shipC_BPartner_Location_ID, shipC_BPartner_Location_ID,
			"Y".equals(Env.getContext(ctx, WindowNo, "IsSOTrx")), null);

		if (C_Tax_ID == 0)
			;
		else
			dataBinder.setValue(rowIndex, "C_Tax_ID", new Integer(C_Tax_ID));
		//


		return "";
	}	//	tax





}
