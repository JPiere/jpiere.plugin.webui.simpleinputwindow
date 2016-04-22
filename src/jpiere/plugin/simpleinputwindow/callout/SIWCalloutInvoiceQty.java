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
import java.util.Properties;

import jpiere.plugin.simpleinputwindow.base.ISimpleInputWindowCallout;
import jpiere.plugin.simpleinputwindow.form.SimpleInputWindowDataBinder;

import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MStorageReservation;
import org.compiere.model.MUOM;
import org.compiere.model.MUOMConversion;
import org.compiere.util.Env;
import org.compiere.util.Msg;

public class SIWCalloutInvoiceQty implements ISimpleInputWindowCallout {

	/**
	 *
	 *  M_Product_ID, M_Locator_ID, M_AttributeSetInstance_ID in M_InventoryLine Table
	 **/
	@Override
	public String start(SimpleInputWindowDataBinder dataBinder,int rowIndex, String ColumnName, Object newValue, Object oldValue)
	{

		if (newValue == null)
			return "";

		int WindowNo = dataBinder.getSimpleInputWindow().getGridTab().getWindowNo();
		Properties ctx = Env.getCtx();
		int tabNo = dataBinder.getSimpleInputWindow().getGridTab().getTabNo();

		int M_Product_ID = Env.getContextAsInt(ctx, WindowNo, tabNo, "M_Product_ID");

		BigDecimal QtyInvoiced = Env.ZERO;
		BigDecimal QtyEntered, PriceActual, PriceEntered;

		//	No Product
		if (M_Product_ID == 0)
		{
			QtyEntered = (BigDecimal)dataBinder.getValue(rowIndex, "QtyEntered");
			QtyInvoiced = QtyEntered;
			dataBinder.setValue(rowIndex, "QtyInvoiced", QtyInvoiced);
		}
		//	UOM Changed - convert from Entered -> Product
		else if (ColumnName.equals("C_UOM_ID"))
		{
			int C_UOM_To_ID = ((Integer)newValue).intValue();
			QtyEntered = (BigDecimal)dataBinder.getValue(rowIndex,"QtyEntered");
			BigDecimal QtyEntered1 = QtyEntered.setScale(MUOM.getPrecision(ctx, C_UOM_To_ID), BigDecimal.ROUND_HALF_UP);
			if (QtyEntered.compareTo(QtyEntered1) != 0)
			{
//				if (log.isLoggable(Level.FINE)) log.fine("Corrected QtyEntered Scale UOM=" + C_UOM_To_ID
//					+ "; QtyEntered=" + QtyEntered + "->" + QtyEntered1);
				QtyEntered = QtyEntered1;
				dataBinder.setValue(rowIndex, "QtyEntered", QtyEntered);
			}
			QtyInvoiced = MUOMConversion.convertProductFrom (ctx, M_Product_ID,	C_UOM_To_ID, QtyEntered);
			if (QtyInvoiced == null)
				QtyInvoiced = QtyEntered;
			boolean conversion = QtyEntered.compareTo(QtyInvoiced) != 0;
			PriceActual = (BigDecimal)dataBinder.getValue(rowIndex,"PriceActual");
			PriceEntered = MUOMConversion.convertProductFrom (ctx, M_Product_ID, C_UOM_To_ID, PriceActual);
			if (PriceEntered == null)
				PriceEntered = PriceActual;
//			if (log.isLoggable(Level.FINE)) log.fine("UOM=" + C_UOM_To_ID
//				+ ", QtyEntered/PriceActual=" + QtyEntered + "/" + PriceActual
//				+ " -> " + conversion
//				+ " QtyOrdered/PriceEntered=" + QtyOrdered + "/" + PriceEntered);
			Env.setContext(ctx, WindowNo, "UOMConversion", conversion ? "Y" : "N");
			dataBinder.setValue(rowIndex, "QtyInvoiced", QtyInvoiced);
			dataBinder.setValue(rowIndex, "PriceEntered", PriceEntered);
		}
		//	QtyEntered changed - calculate QtyOrdered
		else if (ColumnName.equals("QtyEntered"))
		{
			int C_UOM_To_ID = Env.getContextAsInt(ctx, WindowNo, tabNo, "C_UOM_ID");
			QtyEntered = (BigDecimal)newValue;
			BigDecimal QtyEntered1 = QtyEntered.setScale(MUOM.getPrecision(ctx, C_UOM_To_ID), BigDecimal.ROUND_HALF_UP);
			if (QtyEntered.compareTo(QtyEntered1) != 0)
			{
//				if (log.isLoggable(Level.FINE)) log.fine("Corrected QtyEntered Scale UOM=" + C_UOM_To_ID
//					+ "; QtyEntered=" + QtyEntered + "->" + QtyEntered1);
				QtyEntered = QtyEntered1;
				dataBinder.setValue(rowIndex, "QtyEntered", QtyEntered);
			}
			QtyInvoiced = MUOMConversion.convertProductFrom (ctx, M_Product_ID,	C_UOM_To_ID, QtyEntered);
			if (QtyInvoiced == null)
				QtyInvoiced = QtyEntered;
			boolean conversion = QtyEntered.compareTo(QtyInvoiced) != 0;
//			if (log.isLoggable(Level.FINE)) log.fine("UOM=" + C_UOM_To_ID
//				+ ", QtyEntered=" + QtyEntered
//				+ " -> " + conversion
//				+ " QtyOrdered=" + QtyOrdered);
			Env.setContext(ctx, WindowNo, "UOMConversion", conversion ? "Y" : "N");
			dataBinder.setValue(rowIndex, "QtyInvoiced", QtyInvoiced);
		}
		//	QtyOrdered changed - calculate QtyEntered (should not happen)
		else if (ColumnName.equals("QtyInvoiced"))
		{
			int C_UOM_To_ID = Env.getContextAsInt(ctx, WindowNo, tabNo, "C_UOM_ID");
			QtyInvoiced = (BigDecimal)newValue;
			int precision = MProduct.get(ctx, M_Product_ID).getUOMPrecision();
			BigDecimal QtyInvoiced1 = QtyInvoiced.setScale(precision, BigDecimal.ROUND_HALF_UP);
			if (QtyInvoiced.compareTo(QtyInvoiced1) != 0)
			{
//				if (log.isLoggable(Level.FINE)) log.fine("Corrected QtyOrdered Scale "
//					+ QtyOrdered + "->" + QtyOrdered1);
				QtyInvoiced = QtyInvoiced1;
				dataBinder.setValue(rowIndex, "QtyInvoiced", QtyInvoiced);
			}
			QtyEntered = MUOMConversion.convertProductTo (ctx, M_Product_ID, C_UOM_To_ID, QtyInvoiced);
			if (QtyEntered == null)
				QtyEntered = QtyInvoiced;
			boolean conversion = QtyInvoiced.compareTo(QtyEntered) != 0;
//			if (log.isLoggable(Level.FINE)) log.fine("UOM=" + C_UOM_To_ID
//				+ ", QtyOrdered=" + QtyOrdered
//				+ " -> " + conversion
//				+ " QtyEntered=" + QtyEntered);
			Env.setContext(ctx, WindowNo, "UOMConversion", conversion ? "Y" : "N");
			dataBinder.setValue(rowIndex, "QtyEntered", QtyEntered);
		}
		else
		{
			QtyInvoiced = (BigDecimal)dataBinder.getValue(rowIndex,"QtyInvoiced");
		}


		return "";
	}



}
