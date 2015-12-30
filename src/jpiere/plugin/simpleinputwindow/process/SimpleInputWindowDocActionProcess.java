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

import java.util.List;
import java.util.logging.Level;

import org.adempiere.base.IModelFactory;
import org.adempiere.base.Service;
import org.compiere.model.MOrder;
import org.compiere.model.MRefList;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 *  Simple Input Window Sample Process
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class SimpleInputWindowDocActionProcess extends SvrProcess {

	String tableName = null;
	int recordID= 0;
	String docAction = DocAction.ACTION_Complete;

	@Override
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.endsWith("_ID"))
				recordID = para[i].getParameterAsInt();
			else if (name.equals("DocAction"))
				docAction = para[i].getParameterAsString();
			else if (name.equals(MTable.COLUMNNAME_TableName))
				tableName = para[i].getParameterAsString();
			else
				log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
		}
	}

	@Override
	protected String doIt() throws Exception {

		List<IModelFactory> factoryList = Service.locator().list(IModelFactory.class).getServices();
		if (factoryList == null)
		{
			;//
		}
		PO po = null;
		DocAction document = null;
		for(IModelFactory factory : factoryList)
		{
			po = factory.getPO(tableName, recordID, null);//
			if (po != null && po instanceof DocAction)
			{
				document = (DocAction)po;

				if(document.getDocStatus().equals(DocAction.STATUS_Closed)
						|| document.getDocStatus().equals(DocAction.STATUS_Voided)
						|| document.getDocStatus().equals(DocAction.STATUS_Reversed))
				{
					return Msg.getMsg(Env.getCtx(), "JP_Cannot_Process_DocAction");//You can not process the DocAction.
				}

				if(document.getDocStatus().equals(DocAction.STATUS_Completed))
				{
					if(docAction.equals(DocAction.ACTION_Close) || docAction.equals(DocAction.ACTION_Reverse_Accrual)
							|| docAction.equals(DocAction.ACTION_Reverse_Correct) || docAction.equals(DocAction.ACTION_Void))
					{
						;
					}else{
						return Msg.getMsg(Env.getCtx(), "JP_Cannot_Process_DocAction");//You can not process the DocAction.
					}
				}

				if(tableName.equals(MOrder.Table_Name))
				{
					((MOrder)document).setDocAction(docAction);
				}

				document.processIt(docAction);
				document.saveEx();
				addBufferLog(getAD_PInstance_ID(), null, null, document.getDocumentInfo(), po.get_Table_ID(), po.get_ID());
				break;
			}

		}//for

		if(document == null)
			return Msg.getMsg(getCtx(), "Error");

		String msg = Msg.getMsg(getCtx(), "Display Document Info") + " : " + document.getDocumentInfo()
				+ System.lineSeparator()
				+ Msg.getElement(getCtx(), "DocStatus") + " : " + MRefList.getListName(getCtx(), 131, document.getDocStatus())
				+ System.lineSeparator();


		return msg;
	}

}
