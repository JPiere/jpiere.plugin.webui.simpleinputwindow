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

import org.compiere.model.MPriceList;
import org.compiere.model.MProductPricing;
import org.compiere.model.MRole;
import org.compiere.model.MUOMConversion;
import org.compiere.util.Env;

public class SIWCalloutOrderAmt implements ISimpleInputWindowCallout {

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

		int C_UOM_To_ID = Env.getContextAsInt(ctx, WindowNo, tabNo, "C_UOM_ID");
		int M_Product_ID = Env.getContextAsInt(ctx, WindowNo, tabNo, "M_Product_ID");
		int M_PriceList_ID = Env.getContextAsInt(ctx, WindowNo, tabNo, "M_PriceList_ID");
		int StdPrecision = MPriceList.getStandardPrecision(ctx, M_PriceList_ID);
		MPriceList pl = new MPriceList(ctx, M_PriceList_ID, null);
		boolean isEnforcePriceLimit = pl.isEnforcePriceLimit();
		BigDecimal QtyEntered, QtyOrdered, PriceEntered, PriceActual, PriceLimit, Discount, PriceList;
		//	get values
		QtyEntered = (BigDecimal)dataBinder.getValue(rowIndex, "QtyEntered");
		QtyOrdered = (BigDecimal)dataBinder.getValue(rowIndex, "QtyOrdered");
//		if (log.isLoggable(Level.FINE)) log.fine("QtyEntered=" + QtyEntered + ", Ordered=" + QtyOrdered + ", UOM=" + C_UOM_To_ID);
		//
		PriceEntered = (BigDecimal)dataBinder.getValue(rowIndex, "PriceEntered");
		PriceActual = (BigDecimal)dataBinder.getValue(rowIndex, "PriceActual");
		Discount = (BigDecimal)dataBinder.getValue(rowIndex, "Discount");
		PriceLimit = (BigDecimal)dataBinder.getValue(rowIndex, "PriceLimit");
		PriceList = (BigDecimal)dataBinder.getValue(rowIndex, "PriceList");
//		if (log.isLoggable(Level.FINE)){
//			log.fine("PriceList=" + PriceList + ", Limit=" + PriceLimit + ", Precision=" + StdPrecision);
//			log.fine("PriceEntered=" + PriceEntered + ", Actual=" + PriceActual + ", Discount=" + Discount);
//		}

		//		No Product
		if (M_Product_ID == 0)
		{
			// if price change sync price actual and entered
			// else ignore
			if (ColumnName.equals("PriceActual"))
			{
				PriceEntered = (BigDecimal) newValue;
				dataBinder.setValue(rowIndex, "PriceEntered", newValue);
			}
			else if (ColumnName.equals("PriceEntered"))
			{
				PriceActual = (BigDecimal) newValue;
				dataBinder.setValue(rowIndex, "PriceActual", newValue);
			}
		}
		//	Product Qty changed - recalc price
		else if ((ColumnName.equals("QtyOrdered")
			|| ColumnName.equals("QtyEntered")
			|| ColumnName.equals("C_UOM_ID")
			|| ColumnName.equals("M_Product_ID"))
			&& !"N".equals(Env.getContext(ctx, WindowNo, "DiscountSchema")))
		{
			int C_BPartner_ID = Env.getContextAsInt(ctx, WindowNo, "C_BPartner_ID");
			if (ColumnName.equals("QtyEntered"))
				QtyOrdered = MUOMConversion.convertProductFrom (ctx, M_Product_ID,
					C_UOM_To_ID, QtyEntered);
			if (QtyOrdered == null)
				QtyOrdered = QtyEntered;
			boolean IsSOTrx = Env.getContext(ctx, WindowNo, "IsSOTrx").equals("Y");
			MProductPricing pp = new MProductPricing (M_Product_ID, C_BPartner_ID, QtyOrdered, IsSOTrx);
			pp.setM_PriceList_ID(M_PriceList_ID);
			int M_PriceList_Version_ID = Env.getContextAsInt(ctx, WindowNo, "M_PriceList_Version_ID");
			pp.setM_PriceList_Version_ID(M_PriceList_Version_ID);
			Timestamp date = (Timestamp)dataBinder.getValue(rowIndex, "DateOrdered");
			pp.setPriceDate(date);
			//
			PriceEntered = MUOMConversion.convertProductFrom (ctx, M_Product_ID,
				C_UOM_To_ID, pp.getPriceStd());
			if (PriceEntered == null)
				PriceEntered = pp.getPriceStd();
			//
//			if (log.isLoggable(Level.FINE)) log.fine("QtyChanged -> PriceActual=" + pp.getPriceStd()
//				+ ", PriceEntered=" + PriceEntered + ", Discount=" + pp.getDiscount());
			PriceActual = pp.getPriceStd();
			PriceEntered = pp.getPriceStd();
			Discount = pp.getDiscount();
			PriceLimit = pp.getPriceLimit();
			PriceList = pp.getPriceList();
			dataBinder.setValue(rowIndex, "PriceList", pp.getPriceList());
			dataBinder.setValue(rowIndex, "PriceLimit", pp.getPriceLimit());
			dataBinder.setValue(rowIndex, "PriceActual", pp.getPriceStd());
			dataBinder.setValue(rowIndex, "PriceEntered", pp.getPriceStd());
			dataBinder.setValue(rowIndex, "Discount", pp.getDiscount());
			dataBinder.setValue(rowIndex, "PriceEntered", PriceEntered);
			Env.setContext(ctx, WindowNo, "DiscountSchema", pp.isDiscountSchema() ? "Y" : "N");
		}
		else if (ColumnName.equals("PriceActual"))
		{
			PriceActual = (BigDecimal)newValue;
			PriceEntered = MUOMConversion.convertProductFrom (ctx, M_Product_ID,
				C_UOM_To_ID, PriceActual);
			if (PriceEntered == null)
				PriceEntered = PriceActual;
			//
//			if (log.isLoggable(Level.FINE)) log.fine("PriceActual=" + PriceActual
//				+ " -> PriceEntered=" + PriceEntered);
			dataBinder.setValue(rowIndex, "PriceEntered", PriceEntered);
		}
		else if (ColumnName.equals("PriceEntered"))
		{
			PriceEntered = (BigDecimal)newValue;
			PriceActual = MUOMConversion.convertProductTo (ctx, M_Product_ID,
				C_UOM_To_ID, PriceEntered);
			if (PriceActual == null)
				PriceActual = PriceEntered;
			//
//			if (log.isLoggable(Level.FINE)) log.fine("PriceEntered=" + PriceEntered
//				+ " -> PriceActual=" + PriceActual);
			dataBinder.setValue(rowIndex, "PriceActual", PriceActual);
		}

		//  Discount entered - Calculate Actual/Entered
		if (ColumnName.equals("Discount"))
		{
			if ( PriceList.doubleValue() != 0 )
				PriceActual = BigDecimal.valueOf((100.0 - Discount.doubleValue()) / 100.0 * PriceList.doubleValue());
			if (PriceActual.scale() > StdPrecision)
				PriceActual = PriceActual.setScale(StdPrecision, BigDecimal.ROUND_HALF_UP);
			PriceEntered = MUOMConversion.convertProductFrom (ctx, M_Product_ID,
				C_UOM_To_ID, PriceActual);
			if (PriceEntered == null)
				PriceEntered = PriceActual;
			dataBinder.setValue(rowIndex, "PriceActual", PriceActual);
			dataBinder.setValue(rowIndex, "PriceEntered", PriceEntered);
		}
		//	calculate Discount
		else
		{
			if (PriceList.compareTo(Env.ZERO) == 0)
				Discount = Env.ZERO;
			else
				Discount = BigDecimal.valueOf((PriceList.doubleValue() - PriceActual.doubleValue()) / PriceList.doubleValue() * 100.0);
			if (Discount.scale() > 2)
				Discount = Discount.setScale(2, BigDecimal.ROUND_HALF_UP);
			dataBinder.setValue(rowIndex, "Discount", Discount);
		}
//		if (log.isLoggable(Level.FINE)) log.fine("PriceEntered=" + PriceEntered + ", Actual=" + PriceActual + ", Discount=" + Discount);

		//	Check PriceLimit
		String epl = Env.getContext(ctx, WindowNo, "EnforcePriceLimit");
		boolean enforce = Env.isSOTrx(ctx, WindowNo) && epl != null && !epl.equals("") ? epl.equals("Y") : isEnforcePriceLimit;
		if (enforce && MRole.getDefault().isOverwritePriceLimit())
			enforce = false;
		//	Check Price Limit?
		if (enforce && PriceLimit.doubleValue() != 0.0
		  && PriceActual.compareTo(PriceLimit) < 0)
		{
			PriceActual = PriceLimit;
			PriceEntered = MUOMConversion.convertProductFrom (ctx, M_Product_ID,
				C_UOM_To_ID, PriceLimit);
			if (PriceEntered == null)
				PriceEntered = PriceLimit;
//			if (log.isLoggable(Level.FINE)) log.fine("(under) PriceEntered=" + PriceEntered + ", Actual" + PriceLimit);
			dataBinder.setValue(rowIndex, "PriceActual", PriceLimit);
			dataBinder.setValue(rowIndex, "PriceEntered", PriceEntered);
//			mTab.fireDataStatusEEvent ("UnderLimitPrice", "", false);//TTODO:イベントがファイヤーされていたけど、その後の処理があるのではないか？　FDialogに書き換える必要を検討
			//	Repeat Discount calc
			if (PriceList.compareTo(Env.ZERO) != 0)
			{
				Discount = BigDecimal.valueOf((PriceList.doubleValue () - PriceActual.doubleValue ()) / PriceList.doubleValue () * 100.0);
				if (Discount.scale () > 2)
					Discount = Discount.setScale (2, BigDecimal.ROUND_HALF_UP);
				dataBinder.setValue(rowIndex, "Discount", Discount);
			}
		}

		//	Line Net Amt
		BigDecimal LineNetAmt = QtyOrdered.multiply(PriceActual);
		if (LineNetAmt.scale() > StdPrecision)
			LineNetAmt = LineNetAmt.setScale(StdPrecision, BigDecimal.ROUND_HALF_UP);
//		if (log.isLoggable(Level.INFO)) log.info("LineNetAmt=" + LineNetAmt);
		dataBinder.setValue(rowIndex, "LineNetAmt", LineNetAmt);
		//
		return "";
	}



}
