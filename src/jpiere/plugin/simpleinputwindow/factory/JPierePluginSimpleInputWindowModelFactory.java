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

import java.sql.ResultSet;

import jpiere.plugin.simpleinputwindow.model.MSimpleInputSearch;
import jpiere.plugin.simpleinputwindow.model.MSimpleInputWindow;

import org.adempiere.base.IModelFactory;
import org.compiere.model.PO;
import org.compiere.util.Env;

/**
 *  JPiere Plugins Simple Input Window Model Factory
 *
 *  JPIERE-0111
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPierePluginSimpleInputWindowModelFactory implements IModelFactory {

	@Override
	public Class<?> getClass(String tableName) {
		
		if(tableName.startsWith("JP_SimpleInput"))
		{
			if(tableName.equals(MSimpleInputWindow.Table_Name)){
				return MSimpleInputWindow.class;
			}else if(tableName.equals(MSimpleInputSearch.Table_Name)){
				return MSimpleInputSearch.class;
			}
		}
		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) 
	{
		if(tableName.startsWith("JP_SimpleInput"))
		{
			if(tableName.equals(MSimpleInputWindow.Table_Name)){
				return  new MSimpleInputWindow(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MSimpleInputSearch.Table_Name)){
				return  new MSimpleInputSearch(Env.getCtx(), Record_ID, trxName);
			}
		}
		return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) 
	{
		if(tableName.startsWith("JP_SimpleInput"))
		{
			if(tableName.equals(MSimpleInputWindow.Table_Name)){
				return  new MSimpleInputWindow(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MSimpleInputSearch.Table_Name)){
				return  new MSimpleInputSearch(Env.getCtx(), rs, trxName);
			}
		}
		return null;
	}

}
