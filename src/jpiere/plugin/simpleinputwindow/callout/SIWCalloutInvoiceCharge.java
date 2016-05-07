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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.util.DB;
import org.compiere.util.Env;

import jpiere.plugin.simpleinputwindow.base.ISimpleInputWindowCallout;
import jpiere.plugin.simpleinputwindow.form.SimpleInputWindowDataBinder;

public class SIWCalloutInvoiceCharge implements ISimpleInputWindowCallout {

	/**
	 *
	 *  M_Product_ID, M_Locator_ID, M_AttributeSetInstance_ID in M_InventoryLine Table
	 **/
	@Override
	public String start(SimpleInputWindowDataBinder dataBinder,int rowIndex, String ColumnName, Object newValue, Object oldValue)
	{
		Integer C_Charge_ID = (Integer)newValue;
		if (C_Charge_ID == null || C_Charge_ID.intValue() == 0)
			return "";

		int WindowNo = dataBinder.getSimpleInputWindow().getGridTab().getWindowNo();
		Properties ctx = Env.getCtx();
		int tabNo = dataBinder.getSimpleInputWindow().getGridTab().getTabNo();
	
		//	No Product defined
		if (dataBinder.getValue(rowIndex, "M_Product_ID") != null)
		{
			dataBinder.setValue(rowIndex, "C_Charge_ID", null);
			return "ChargeExclusively";
		}
		
		dataBinder.setValue(rowIndex, "M_AttributeSetInstance_ID", null);
		dataBinder.setValue(rowIndex, "S_ResourceAssignment_ID", null);
		dataBinder.setValue(rowIndex, "C_UOM_ID", new Integer(100));//	EA

		Env.setContext(ctx, WindowNo, "DiscountSchema", "N");
		String sql = "SELECT ChargeAmt FROM C_Charge WHERE C_Charge_ID=?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, C_Charge_ID.intValue());
			rs = pstmt.executeQuery();
			if (rs.next())
			{
				dataBinder.setValue(rowIndex, "PriceEntered", rs.getBigDecimal (1));
				dataBinder.setValue(rowIndex, "PriceActual", rs.getBigDecimal (1));
				dataBinder.setValue(rowIndex, "PriceLimit", Env.ZERO);
				dataBinder.setValue(rowIndex, "PriceList", Env.ZERO);
			}
		}
		catch (SQLException e)
		{
//			log.log(Level.SEVERE, sql + e);
			return e.getLocalizedMessage();
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		
		
		return new SIWCalloutInvoiceTax().start(dataBinder, rowIndex, ColumnName, newValue, oldValue);
		
	}



}
