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
import java.util.Properties;

/**
 * MSimpleInputSearch
 *
 * JPIERE-0111
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class MSimpleInputSearch extends X_JP_SimpleInputSearch {

	public MSimpleInputSearch(Properties ctx, int JP_SimpleInputSearch_ID,
			String trxName) {
		super(ctx, JP_SimpleInputSearch_ID, trxName);
	}

	public MSimpleInputSearch(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

}
