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
package jpiere.plugin.simpleinputwindow.factory;

import jpiere.plugin.simpleinputwindow.base.ISimpleInputWindowCallout;
import jpiere.plugin.simpleinputwindow.base.ISimpleInputWindowCalloutFactory;
import jpiere.plugin.simpleinputwindow.callout.SIWCalloutOrderController;
import jpiere.plugin.simpleinputwindow.callout.SimpleInputWindowCalloutInventory;
import jpiere.plugin.simpleinputwindow.callout.SIWCalloutInvoiceController;
import jpiere.plugin.simpleinputwindow.callout.SimpleInputWindowSampleCallout;

/**
 * Dafault Simple Input Window Callout Factory
 *
 * JPIERE-0111
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class DefaultSimpleInputWindowCalloutFactory implements ISimpleInputWindowCalloutFactory {

	@Override
	public ISimpleInputWindowCallout getCallout(String tableName, String columnName) {

		if(tableName.equals("JP_ReferenceTest") && (columnName.equals("Rate") || columnName.equals("QtyEntered") || columnName.equals("M_Product_ID")))
		{
			return new SimpleInputWindowSampleCallout();
		}else if(tableName.equals("M_InventoryLine") &&
				(columnName.equals("M_Product_ID") || columnName.equals("M_Locator_ID") || columnName.equals("M_AttributeSetInstance_ID"))){
			return new SimpleInputWindowCalloutInventory();
		}else if(tableName.equals("C_OrderLine")){
			return new SIWCalloutOrderController();
		}else if(tableName.equals("C_InvoiceLine")){
			return new SIWCalloutInvoiceController();
		}
		return null;

	}

}
