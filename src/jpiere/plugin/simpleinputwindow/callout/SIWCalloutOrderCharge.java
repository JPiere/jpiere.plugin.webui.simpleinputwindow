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

import jpiere.plugin.simpleinputwindow.base.ISimpleInputWindowCallout;
import jpiere.plugin.simpleinputwindow.form.SimpleInputWindowDataBinder;

public class SIWCalloutOrderCharge implements ISimpleInputWindowCallout {

	/**
	 *
	 *  M_Product_ID, M_Locator_ID, M_AttributeSetInstance_ID in M_InventoryLine Table
	 **/
	@Override
	public String start(SimpleInputWindowDataBinder dataBinder,int rowIndex, String ColumnName, Object newValue, Object oldValue)
	{

		String msg = null;
		if (ColumnName.equals("M_Product_ID")){
			msg = new SIWCalloutOrderProduct().start(dataBinder, rowIndex, ColumnName, newValue, oldValue);

		}
		else if(ColumnName.equals("C_BPartner_Location_ID"))
		{
			msg = new SIWCalloutOrderTax().start(dataBinder, rowIndex, ColumnName, newValue, oldValue);

		}else if(ColumnName.equals("C_Charge_ID")){
				;
		}else if (ColumnName.equals("C_UOM_ID")){
			msg = new SIWCalloutOrderProduct().start(dataBinder, rowIndex, ColumnName, newValue, oldValue);
		}

		return msg;
	}



}
