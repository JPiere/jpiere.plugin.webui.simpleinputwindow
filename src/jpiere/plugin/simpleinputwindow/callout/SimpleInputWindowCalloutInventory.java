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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jpiere.plugin.simpleinputwindow.base.ISimpleInputWindowCallout;
import jpiere.plugin.simpleinputwindow.form.SimpleInputWindowDataBinder;

import org.compiere.model.MDocType;
import org.compiere.model.MInventoryLine;
import org.compiere.model.MLocator;
import org.compiere.model.MWarehouse;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class SimpleInputWindowCalloutInventory implements ISimpleInputWindowCallout {

	/**
	 *
	 *  M_Product_ID, M_Locator_ID, M_AttributeSetInstance_ID in M_InventoryLine Table
	 **/
	@Override
	public String start(SimpleInputWindowDataBinder dataBinder,int rowIndex, String ColumnName, Object newValue, Object oldValue)
	{

		if(ColumnName.equals("M_Locator_ID"))
			fillLocator(dataBinder,rowIndex, ColumnName, newValue, oldValue);

		int doctypeid = Env.getContextAsInt(Env.getCtx(), dataBinder.getSimpleInputWindow().getGridTab().getWindowNo(), "C_DocType_ID");
		String docSubTypeInv = null;
		if (doctypeid > 0) {
			MDocType dt = MDocType.get(Env.getCtx(), doctypeid);
			docSubTypeInv = dt.getDocSubTypeInv();
		}

		Integer InventoryLine = (Integer)dataBinder.getValue(rowIndex, "M_InventoryLine_ID");
		BigDecimal bd = null;

		if (InventoryLine != null && InventoryLine.intValue() != 0) {
			MInventoryLine _ILine = new MInventoryLine(Env.getCtx(), InventoryLine, null);
			Integer M_Product_ID = (Integer)dataBinder.getValue(rowIndex,"M_Product_ID");
			Integer M_Locator_ID = (Integer)dataBinder.getValue(rowIndex,"M_Locator_ID");
			Integer M_AttributeSetInstance_ID = 0;
			// if product or locator has changed recalculate Book Qty
			if ((M_Product_ID != null && M_Product_ID != _ILine.getM_Product_ID()) ||
					(M_Locator_ID !=null && M_Locator_ID != _ILine.getM_Locator_ID())) {

				// Check ASI - if product has been changed remove old ASI
				if (M_Product_ID == _ILine.getM_Product_ID()) {
					M_AttributeSetInstance_ID = (Integer)dataBinder.getValue(rowIndex,"M_AttributeSetInstance_ID");
					if( M_AttributeSetInstance_ID == null )
						M_AttributeSetInstance_ID = 0;
				} else {
					dataBinder.setValue(rowIndex, "M_AttributeSetInstance_ID", null);
				}
				if (MDocType.DOCSUBTYPEINV_PhysicalInventory.equals(docSubTypeInv)) {
					try {
						bd = setQtyBook(M_AttributeSetInstance_ID, M_Product_ID, M_Locator_ID);
						dataBinder.setValue(rowIndex, "QtyBook", bd);
						dataBinder.setValue(rowIndex, "QtyCount", bd);
					} catch (Exception e) {
						return e.getLocalizedMessage();
					}
				}
			}
			return "";
		}

		//New Line - Get Book Value
		int M_Product_ID = 0;
		Integer Product = (Integer)dataBinder.getValue(rowIndex,"M_Product_ID");
		if (Product != null)
			M_Product_ID = Product.intValue();
		if (M_Product_ID == 0)
			return "";
		int M_Locator_ID = 0;
		Integer Locator = (Integer)dataBinder.getValue(rowIndex,"M_Locator_ID");
		if (Locator != null)
			M_Locator_ID = Locator.intValue();
		if (M_Locator_ID == 0)
				return "";


		//	Set Attribute
		int M_AttributeSetInstance_ID = 0;
		Integer ASI = (Integer)dataBinder.getValue(rowIndex,"M_AttributeSetInstance_ID");
		if (ASI != null)
			M_AttributeSetInstance_ID = ASI.intValue();
		//	Product Selection
		if (MInventoryLine.COLUMNNAME_M_Product_ID.equals(ColumnName))
		{
			if (Env.getContextAsInt(Env.getCtx(), dataBinder.getSimpleInputWindow().getGridTab().getWindowNo(), Env.TAB_INFO, "M_Product_ID") == M_Product_ID)
			{
				M_AttributeSetInstance_ID = Env.getContextAsInt(Env.getCtx(), dataBinder.getSimpleInputWindow().getGridTab().getWindowNo(), Env.TAB_INFO, "M_AttributeSetInstance_ID");
			}
			else
			{
				M_AttributeSetInstance_ID = 0;
			}
			if (M_AttributeSetInstance_ID != 0)
				dataBinder.setValue(rowIndex, MInventoryLine.COLUMNNAME_M_AttributeSetInstance_ID, M_AttributeSetInstance_ID);
			else
				dataBinder.setValue(rowIndex, MInventoryLine.COLUMNNAME_M_AttributeSetInstance_ID, null);
		}

		// Set QtyBook from first storage location
		// kviiksaar: Call's now the extracted function
		if (MDocType.DOCSUBTYPEINV_PhysicalInventory.equals(docSubTypeInv)) {
			try {
				bd = setQtyBook(M_AttributeSetInstance_ID, M_Product_ID, M_Locator_ID);
				dataBinder.setValue(rowIndex, "QtyBook", bd);
				dataBinder.setValue(rowIndex, "QtyCount", bd);
			} catch (Exception e) {
				return e.getLocalizedMessage();
			}
		}

		return "";
	}

	public String fillLocator(SimpleInputWindowDataBinder dataBinder,int rowIndex, String ColumnName, Object newValue, Object oldValue)
	{
		Integer locatorID = (Integer) newValue;
		if (locatorID == null || locatorID.intValue() == 0) {
			int warehouseID = Env.getContextAsInt(Env.getCtx(), dataBinder.getSimpleInputWindow().getGridTab().getWindowNo(), "M_Warehouse_ID", true);
			if (warehouseID > 0) {
				MWarehouse wh = MWarehouse.get(Env.getCtx(), warehouseID);
				MLocator defaultLocator = wh.getDefaultLocator();
				if (defaultLocator != null) {
					dataBinder.setValue(rowIndex, ColumnName, defaultLocator.getM_Locator_ID());
				}
			}
		}

		return "";
	}

	private BigDecimal setQtyBook (int M_AttributeSetInstance_ID, int M_Product_ID, int M_Locator_ID) throws Exception {
		// Set QtyBook from first storage location
		BigDecimal bd = null;
		String sql = "SELECT QtyOnHand FROM M_StorageOnHand "
			+ "WHERE M_Product_ID=?"	//	1
			+ " AND M_Locator_ID=?"		//	2
			+ " AND M_AttributeSetInstance_ID=?";
		if (M_AttributeSetInstance_ID == 0)
			sql = "SELECT SUM(QtyOnHand) FROM M_StorageOnHand "
			+ "WHERE M_Product_ID=?"	//	1
			+ " AND M_Locator_ID=?";	//	2
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, M_Product_ID);
			pstmt.setInt(2, M_Locator_ID);
			if (M_AttributeSetInstance_ID != 0)
				pstmt.setInt(3, M_AttributeSetInstance_ID);
			rs = pstmt.executeQuery();
			if (rs.next())
			{
				bd = rs.getBigDecimal(1);
				if (bd != null)
					return bd;
			} else {
				// gwu: 1719401: clear Booked Quantity to zero first in case the query returns no rows,
				// for example when the locator has never stored a particular product.
				return Env.ZERO;
			}
		}
		catch (SQLException e)
		{
//			log.log(Level.SEVERE, sql, e);
			throw new Exception(e.getLocalizedMessage());
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return Env.ZERO;
	}

}
