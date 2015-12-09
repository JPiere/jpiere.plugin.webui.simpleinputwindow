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
package jpiere.plugin.simpleinputwindow.base;

import jpiere.plugin.simpleinputwindow.form.SimpleInputWindowDataBinder;



/**
 *  Simple Input Window Callout Interface
 */
public interface ISimpleInputWindowCallout
{
	/**
	 *	Start Simple Input Window Callout.
	 *
	 *	<p>Step1:Update WEditor Value for display data.</p>
	 *	<p>Step2:Update ViewModel data for display data.</p>
	 *	<p>Step3:Update Context : GridField.setValue method can update context</p>
	 *	<p>Step4:Update tableModel for consistency</p>
	 *	<p>Step5:Put map of dirtyModel for save data.</p>
	 *
	 *
	 *  @param dataBinder 				:dataBinder.setValue(x, y, newValue)
	 *  @param x 		 				:Column number that Callout occurred
	 *  @param y 		 				:Row number that Callout occurred
	 *  @param value 	 				:New Value
	 *  @param oldValue	 				:Old Value
	 *  @return Error message or ""
	 */
	public String start (SimpleInputWindowDataBinder dataBinder, int rowIndex, String ColumnName, Object newValue, Object oldValue );



}
