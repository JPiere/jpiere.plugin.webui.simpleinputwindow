/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package jpiere.plugin.simpleinputwindow.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.base.IModelFactory;
import org.adempiere.base.Service;
import org.compiere.model.MInventory;
import org.compiere.model.MInventoryLine;
import org.compiere.model.MInventoryLineMA;
import org.compiere.model.MStorageOnHand;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.AdempiereSystemError;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 *	Update existing Inventory Count List with current Book value
 *
 *  @author Jorg Janke
 *  @version $Id: InventoryCountUpdate.java,v 1.2 2006/07/30 00:51:01 jjanke Exp $
 */
public class SimpleInputWindowInventoryCountUpdate extends SvrProcess
{
	/** Physical Inventory		*/
	private int		p_M_Inventory_ID = 0;
	/** Update to What			*/
	private boolean	p_InventoryCountSetZero = false;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("InventoryCountSet"))
				p_InventoryCountSetZero = "Z".equals(para[i].getParameter());
			else if (name.equals("M_Inventory_ID"))
				p_M_Inventory_ID = para[i].getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
//		p_M_Inventory_ID = getRecord_ID();
	}	//	prepare


	/**
	 * 	Process
	 *	@return message
	 *	@throws Exception
	 */
	protected String doIt () throws Exception
	{
		if (log.isLoggable(Level.INFO)) log.info("M_Inventory_ID=" + p_M_Inventory_ID);
		MInventory inventory = new MInventory (getCtx(), p_M_Inventory_ID, get_TrxName());
		if (inventory.get_ID() == 0)
			throw new AdempiereSystemError ("Not found: M_Inventory_ID=" + p_M_Inventory_ID);

		List<IModelFactory> factoryList = Service.locator().list(IModelFactory.class).getServices();
		if (factoryList == null)
		{
			;//
		}
		PO po = null;
		DocAction document = null;
		for(IModelFactory factory : factoryList)
		{
			po = factory.getPO(MInventory.Table_Name, p_M_Inventory_ID, null);//
			if (po != null && po instanceof DocAction)
			{
				document = (DocAction)po;

				if(!document.getDocStatus().equals(DocAction.STATUS_Drafted))
				{
					return Msg.getMsg(getCtx(), "JP_Process_Cannot_Perform");
				}

				break;
			}

		}//for


		if(document == null)
			return Msg.getMsg(getCtx(), "Error");

		//	Multiple Lines for one item
		StringBuilder sql = new StringBuilder("UPDATE M_InventoryLine SET IsActive='N' ")
			.append("WHERE M_Inventory_ID=").append(p_M_Inventory_ID)
			.append(" AND (M_Product_ID, M_Locator_ID, M_AttributeSetInstance_ID) IN ")
				.append("(SELECT M_Product_ID, M_Locator_ID, M_AttributeSetInstance_ID ")
				.append("FROM M_InventoryLine ")
				.append("WHERE M_Inventory_ID=").append(p_M_Inventory_ID)
				.append(" GROUP BY M_Product_ID, M_Locator_ID, M_AttributeSetInstance_ID ")
				.append("HAVING COUNT(*) > 1)");
		int multiple = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.INFO)) log.info("Multiple=" + multiple);

		int delMA = MInventoryLineMA.deleteInventoryMA(p_M_Inventory_ID, get_TrxName());
		if (log.isLoggable(Level.INFO)) log.info("DeletedMA=" + delMA);

		//	ASI
		sql = new StringBuilder("UPDATE M_InventoryLine l ")
			.append("SET (QtyBook,QtyCount) = ")
				.append("(SELECT QtyOnHand,QtyOnHand FROM M_StorageOnHand s ")
				.append("WHERE s.M_Product_ID=l.M_Product_ID AND s.M_Locator_ID=l.M_Locator_ID")
				.append(" AND s.M_AttributeSetInstance_ID=l.M_AttributeSetInstance_ID),")
			.append(" Updated=SysDate,")
			.append(" UpdatedBy=").append(getAD_User_ID())
			//
			.append(" WHERE M_Inventory_ID=").append(p_M_Inventory_ID)
			.append(" AND EXISTS (SELECT * FROM M_StorageOnHand s ")
				.append("WHERE s.M_Product_ID=l.M_Product_ID AND s.M_Locator_ID=l.M_Locator_ID")
				.append(" AND s.M_AttributeSetInstance_ID=l.M_AttributeSetInstance_ID)");
		int no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.INFO)) log.info("Update with ASI=" + no);

		//	No ASI
		int noMA = updateWithMA();

		//	Set Count to Zero
		if (p_InventoryCountSetZero)
		{
			sql = new StringBuilder("UPDATE M_InventoryLine l ")
				.append("SET QtyCount=0 ")
				.append("WHERE M_Inventory_ID=").append(p_M_Inventory_ID);
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.INFO)) log.info("Set Count to Zero=" + no);
		}

		if (multiple > 0){
			StringBuilder msgreturn = new StringBuilder("@M_InventoryLine_ID@ - #").append((no + noMA)).append(" --> @InventoryProductMultiple@");
			return msgreturn.toString();
		}
		StringBuilder msgreturn = new StringBuilder("@M_InventoryLine_ID@ - #").append(no);
		return msgreturn.toString();
	}	//	doIt

	/**
	 * 	Update Inventory Lines With Material Allocation
	 *	@return no updated
	 */
	private int updateWithMA()
	{
		int no = 0;
		//
		String sql = "SELECT * FROM M_InventoryLine WHERE M_Inventory_ID=? AND M_AttributeSetInstance_ID=0";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, get_TrxName());
			pstmt.setInt (1, p_M_Inventory_ID);
			rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				MInventoryLine il = new MInventoryLine (getCtx(), rs, get_TrxName());
				BigDecimal onHand = Env.ZERO;
				MStorageOnHand[] storages = MStorageOnHand.getAll(getCtx(), il.getM_Product_ID(), il.getM_Locator_ID(), get_TrxName());
				MInventoryLineMA ma = null;
				for (int i = 0; i < storages.length; i++)
				{
					MStorageOnHand storage = storages[i];
					if (storage.getQtyOnHand().signum() == 0)
						continue;
					onHand = onHand.add(storage.getQtyOnHand());
					//	No ASI
					if (storage.getM_AttributeSetInstance_ID() == 0
						&& storages.length == 1)
						continue;
					//	Save ASI
					ma = new MInventoryLineMA (il,
						storage.getM_AttributeSetInstance_ID(), storage.getQtyOnHand(),storage.getDateMaterialPolicy(),true);
					if (!ma.save())
						;
				}
				il.setQtyBook(onHand);
				il.setQtyCount(onHand);
				if (il.save())
					no++;
			}
		}
		catch (Exception e)
		{
			log.log (Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		//
		if (log.isLoggable(Level.INFO)) log.info("#" + no);
		return no;
	}	//	updateWithMA


}	//	InventoryCountUpdate
