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

package jpiere.plugin.simpleinputwindow.process;

import java.util.Collection;

import org.adempiere.model.GenericPO;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.SvrProcess;

/**
 *  Simple Input Window Sample Process2
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class SimpleInputWindowSampleProcess2 extends SvrProcess {

	@Override
	protected void prepare() {
		;
	}

	@Override
	protected String doIt() throws Exception {

		String whereClause = "JP_ReferenceTest.JP_ReferenceTest_ID=" + getRecord_ID();

		Collection<GenericPO> genericPOs = new Query(getCtx(), "JP_ReferenceTest", whereClause, get_TrxName())
									.setClient_ID()
									.list();

		String msg = null;
		for(PO po : genericPOs)
		{
			msg = new Integer(po.get_ID()).toString();
			addBufferLog(getAD_PInstance_ID(), null, null, msg, po.get_Table_ID(), po.get_ID());
		}

		return "Process = " + genericPOs.size()  ;
	}

}
