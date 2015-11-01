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
package jpiere.plugin.simpleinputwindow.model;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.Query;
import org.compiere.util.Util;

/**
 * MSimpleInputWindow
 *
 * JPIERE-0111
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class MSimpleInputWindow extends X_JP_SimpleInputWindow {

	MSimpleInputSearch[] m_simpleInputSearches;

	public MSimpleInputWindow(Properties ctx, int JP_SimpleInputWindow_ID,
			String trxName) {
		super(ctx, JP_SimpleInputWindow_ID, trxName);
	}

	public MSimpleInputWindow(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	/**
	 * 	Get MSimpleInputWindow with Value
	 *	@param ctx context
	 *	@param Value value
	 *	@return MMatrixWindow or null
	 */
	public static MSimpleInputWindow get (Properties ctx, String JP_SimpleInputWindow_ID)
	{
		if (JP_SimpleInputWindow_ID == null || JP_SimpleInputWindow_ID.length() == 0)
			return null;
		final String whereClause = "JP_SimpleInputWindow_ID=?";
		MSimpleInputWindow retValue = new Query(ctx, I_JP_SimpleInputWindow.Table_Name, whereClause, null)
				.setParameters(new Integer(JP_SimpleInputWindow_ID).intValue())
				.firstOnly();
		return retValue;
	}	//	get


	public MSimpleInputSearch[] getSimpleInputSearches(String whereClause, String orderClause)
	{

		StringBuilder whereClauseFinal = new StringBuilder(MSimpleInputSearch.COLUMNNAME_JP_SimpleInputWindow_ID + "=? AND IsActive='Y'");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MSimpleInputSearch.COLUMNNAME_SeqNo;
		//
		List<MSimpleInputSearch> list = new Query(getCtx(), I_JP_SimpleInputSearch.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();
		//
		return list.toArray(new MSimpleInputSearch[list.size()]);
	}	//	getLines

	/**
	 * 	Get MatrixSearches of Matrix Window.
	 * 	@param requery requery
	 * 	@param orderBy optional order by column
	 * 	@return MatrixSearche
	 */
	public MSimpleInputSearch[] getSimpleInputSearches(boolean requery, String orderBy)
	{
		if (m_simpleInputSearches != null && !requery) {
			set_TrxName(m_simpleInputSearches, get_TrxName());
			return m_simpleInputSearches;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += "SeqNo";

		m_simpleInputSearches = getSimpleInputSearches(null, orderClause);
		return m_simpleInputSearches;
	}	//	getLines

	/**
	 * 	Get SimpleInputSearches of Simple Input Window.
	 */
	public MSimpleInputSearch[] getSimpleInputSearches()
	{
		return getSimpleInputSearches(false, null);
	}
}
