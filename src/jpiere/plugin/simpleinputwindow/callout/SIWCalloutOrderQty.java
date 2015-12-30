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

public class SIWCalloutOrderQty implements ISimpleInputWindowCallout {

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

		BigDecimal QtyOrdered = Env.ZERO;
		BigDecimal QtyEntered, PriceActual, PriceEntered;

		//	No Product
		if (M_Product_ID == 0)
		{
			QtyEntered = (BigDecimal)dataBinder.getValue(rowIndex, "QtyEntered");
			QtyOrdered = QtyEntered;
			dataBinder.setValue(rowIndex, "QtyOrdered", QtyOrdered);
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
			QtyOrdered = MUOMConversion.convertProductFrom (ctx, M_Product_ID,
				C_UOM_To_ID, QtyEntered);
			if (QtyOrdered == null)
				QtyOrdered = QtyEntered;
			boolean conversion = QtyEntered.compareTo(QtyOrdered) != 0;
			PriceActual = (BigDecimal)dataBinder.getValue(rowIndex,"PriceActual");
			PriceEntered = MUOMConversion.convertProductFrom (ctx, M_Product_ID,
				C_UOM_To_ID, PriceActual);
			if (PriceEntered == null)
				PriceEntered = PriceActual;
//			if (log.isLoggable(Level.FINE)) log.fine("UOM=" + C_UOM_To_ID
//				+ ", QtyEntered/PriceActual=" + QtyEntered + "/" + PriceActual
//				+ " -> " + conversion
//				+ " QtyOrdered/PriceEntered=" + QtyOrdered + "/" + PriceEntered);
			Env.setContext(ctx, WindowNo, "UOMConversion", conversion ? "Y" : "N");
			dataBinder.setValue(rowIndex, "QtyOrdered", QtyOrdered);
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
			QtyOrdered = MUOMConversion.convertProductFrom (ctx, M_Product_ID,
				C_UOM_To_ID, QtyEntered);
			if (QtyOrdered == null)
				QtyOrdered = QtyEntered;
			boolean conversion = QtyEntered.compareTo(QtyOrdered) != 0;
//			if (log.isLoggable(Level.FINE)) log.fine("UOM=" + C_UOM_To_ID
//				+ ", QtyEntered=" + QtyEntered
//				+ " -> " + conversion
//				+ " QtyOrdered=" + QtyOrdered);
			Env.setContext(ctx, WindowNo, "UOMConversion", conversion ? "Y" : "N");
			dataBinder.setValue(rowIndex, "QtyOrdered", QtyOrdered);
		}
		//	QtyOrdered changed - calculate QtyEntered (should not happen)
		else if (ColumnName.equals("QtyOrdered"))
		{
			int C_UOM_To_ID = Env.getContextAsInt(ctx, WindowNo, tabNo, "C_UOM_ID");
			QtyOrdered = (BigDecimal)newValue;
			int precision = MProduct.get(ctx, M_Product_ID).getUOMPrecision();
			BigDecimal QtyOrdered1 = QtyOrdered.setScale(precision, BigDecimal.ROUND_HALF_UP);
			if (QtyOrdered.compareTo(QtyOrdered1) != 0)
			{
//				if (log.isLoggable(Level.FINE)) log.fine("Corrected QtyOrdered Scale "
//					+ QtyOrdered + "->" + QtyOrdered1);
				QtyOrdered = QtyOrdered1;
				dataBinder.setValue(rowIndex, "QtyOrdered", QtyOrdered);
			}
			QtyEntered = MUOMConversion.convertProductTo (ctx, M_Product_ID,
				C_UOM_To_ID, QtyOrdered);
			if (QtyEntered == null)
				QtyEntered = QtyOrdered;
			boolean conversion = QtyOrdered.compareTo(QtyEntered) != 0;
//			if (log.isLoggable(Level.FINE)) log.fine("UOM=" + C_UOM_To_ID
//				+ ", QtyOrdered=" + QtyOrdered
//				+ " -> " + conversion
//				+ " QtyEntered=" + QtyEntered);
			Env.setContext(ctx, WindowNo, "UOMConversion", conversion ? "Y" : "N");
			dataBinder.setValue(rowIndex, "QtyEntered", QtyEntered);
		}
		else
		{
		//	QtyEntered = (BigDecimal)mTab.getValue("QtyEntered");
			QtyOrdered = (BigDecimal)dataBinder.getValue(rowIndex,"QtyOrdered");
		}

		//	Storage
		if (M_Product_ID != 0
			&& Env.isSOTrx(ctx, WindowNo)
			&& QtyOrdered.signum() > 0)		//	no negative (returns)
		{
			MProduct product = MProduct.get (ctx, M_Product_ID);
			if (product.isStocked() && Env.getContext(ctx, WindowNo, "IsDropShip").equals("N"))
			{
				int M_Warehouse_ID = Env.getContextAsInt(ctx, WindowNo, "M_Warehouse_ID");
				int M_AttributeSetInstance_ID = Env.getContextAsInt(ctx, WindowNo, tabNo, "M_AttributeSetInstance_ID");
				BigDecimal available = MStorageReservation.getQtyAvailable
					(M_Warehouse_ID, M_Product_ID, M_AttributeSetInstance_ID, null);
				if (available == null)
					available = Env.ZERO;
				if (available.signum() == 0)
					;
//					mTab.fireDataStatusEEvent ("NoQtyAvailable", "0", false); //TODO
				else if (available.compareTo(QtyOrdered) < 0)
					;
//					mTab.fireDataStatusEEvent ("InsufficientQtyAvailable", available.toString(), false);//TODO
				else
				{
					Integer C_OrderLine_ID = (Integer)dataBinder.getValue(rowIndex,"C_OrderLine_ID");
					if (C_OrderLine_ID == null)
						C_OrderLine_ID = new Integer(0);
					BigDecimal notReserved = MOrderLine.getNotReserved(ctx,
						M_Warehouse_ID, M_Product_ID, M_AttributeSetInstance_ID,
						C_OrderLine_ID.intValue());
					if (notReserved == null)
						notReserved = Env.ZERO;
					BigDecimal total = available.subtract(notReserved);
					if (total.compareTo(QtyOrdered) < 0)
					{
						StringBuilder msgpts = new StringBuilder("@QtyAvailable@=").append(available)
								.append("  -  @QtyNotReserved@=").append(notReserved).append("  =  ").append(total);
						String info = Msg.parseTranslation(ctx, msgpts.toString());
//						mTab.fireDataStatusEEvent ("InsufficientQtyAvailable",	//TODO
//							info, false);
					}
				}
			}
		}
		//
		return "";
	}



}
