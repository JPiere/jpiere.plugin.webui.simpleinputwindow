/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package jpiere.plugin.simpleinputwindow.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for JP_SimpleInputSearch
 *  @author iDempiere (generated) 
 *  @version Release 3.1 - $Id$ */
public class X_JP_SimpleInputSearch extends PO implements I_JP_SimpleInputSearch, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20151209L;

    /** Standard Constructor */
    public X_JP_SimpleInputSearch (Properties ctx, int JP_SimpleInputSearch_ID, String trxName)
    {
      super (ctx, JP_SimpleInputSearch_ID, trxName);
      /** if (JP_SimpleInputSearch_ID == 0)
        {
			setAD_Field_ID (0);
			setAD_Tab_ID (0);
// @0|AD_Tab_ID@
			setColumnSpan (0);
// 2
			setIsMandatory (false);
// N
			setJP_SimpleInputSearch_ID (0);
			setJP_SimpleInputWindow_ID (0);
			setSeqNo (0);
// @SQL=SELECT COALESCE(MAX(SeqNo),0)+10 AS DefaultValue FROM JP_SimpleInputSearch WHERE JP_SimpleInputWindow_ID=@JP_SimpleInputWindow_ID@
			setXPosition (0);
// 1
        } */
    }

    /** Load Constructor */
    public X_JP_SimpleInputSearch (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 4 - System 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer ("X_JP_SimpleInputSearch[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_Field getAD_Field() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Field)MTable.get(getCtx(), org.compiere.model.I_AD_Field.Table_Name)
			.getPO(getAD_Field_ID(), get_TrxName());	}

	/** Set Field.
		@param AD_Field_ID 
		Field on a database table
	  */
	public void setAD_Field_ID (int AD_Field_ID)
	{
		if (AD_Field_ID < 1) 
			set_Value (COLUMNNAME_AD_Field_ID, null);
		else 
			set_Value (COLUMNNAME_AD_Field_ID, Integer.valueOf(AD_Field_ID));
	}

	/** Get Field.
		@return Field on a database table
	  */
	public int getAD_Field_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Field_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_Tab getAD_Tab() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Tab)MTable.get(getCtx(), org.compiere.model.I_AD_Tab.Table_Name)
			.getPO(getAD_Tab_ID(), get_TrxName());	}

	/** Set Tab.
		@param AD_Tab_ID 
		Tab within a Window
	  */
	public void setAD_Tab_ID (int AD_Tab_ID)
	{
		if (AD_Tab_ID < 1) 
			set_Value (COLUMNNAME_AD_Tab_ID, null);
		else 
			set_Value (COLUMNNAME_AD_Tab_ID, Integer.valueOf(AD_Tab_ID));
	}

	/** Get Tab.
		@return Tab within a Window
	  */
	public int getAD_Tab_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Tab_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Column Span.
		@param ColumnSpan 
		Number of column for a box of field
	  */
	public void setColumnSpan (int ColumnSpan)
	{
		set_Value (COLUMNNAME_ColumnSpan, Integer.valueOf(ColumnSpan));
	}

	/** Get Column Span.
		@return Number of column for a box of field
	  */
	public int getColumnSpan () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_ColumnSpan);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Default Logic.
		@param DefaultValue 
		Default value hierarchy, separated by ;
	  */
	public void setDefaultValue (String DefaultValue)
	{
		set_Value (COLUMNNAME_DefaultValue, DefaultValue);
	}

	/** Get Default Logic.
		@return Default value hierarchy, separated by ;
	  */
	public String getDefaultValue () 
	{
		return (String)get_Value(COLUMNNAME_DefaultValue);
	}

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Mandatory.
		@param IsMandatory 
		Data entry is required in this column
	  */
	public void setIsMandatory (boolean IsMandatory)
	{
		set_Value (COLUMNNAME_IsMandatory, Boolean.valueOf(IsMandatory));
	}

	/** Get Mandatory.
		@return Data entry is required in this column
	  */
	public boolean isMandatory () 
	{
		Object oo = get_Value(COLUMNNAME_IsMandatory);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	public org.compiere.model.I_AD_Window getJP_QuickEntryWindow() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Window)MTable.get(getCtx(), org.compiere.model.I_AD_Window.Table_Name)
			.getPO(getJP_QuickEntryWindow_ID(), get_TrxName());	}

	/** Set Quick Entry Window.
		@param JP_QuickEntryWindow_ID Quick Entry Window	  */
	public void setJP_QuickEntryWindow_ID (int JP_QuickEntryWindow_ID)
	{
		if (JP_QuickEntryWindow_ID < 1) 
			set_Value (COLUMNNAME_JP_QuickEntryWindow_ID, null);
		else 
			set_Value (COLUMNNAME_JP_QuickEntryWindow_ID, Integer.valueOf(JP_QuickEntryWindow_ID));
	}

	/** Get Quick Entry Window.
		@return Quick Entry Window	  */
	public int getJP_QuickEntryWindow_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_QuickEntryWindow_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Simple Input Search.
		@param JP_SimpleInputSearch_ID Simple Input Search	  */
	public void setJP_SimpleInputSearch_ID (int JP_SimpleInputSearch_ID)
	{
		if (JP_SimpleInputSearch_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_SimpleInputSearch_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_SimpleInputSearch_ID, Integer.valueOf(JP_SimpleInputSearch_ID));
	}

	/** Get Simple Input Search.
		@return Simple Input Search	  */
	public int getJP_SimpleInputSearch_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_SimpleInputSearch_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_SimpleInputSearch_UU.
		@param JP_SimpleInputSearch_UU JP_SimpleInputSearch_UU	  */
	public void setJP_SimpleInputSearch_UU (String JP_SimpleInputSearch_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_SimpleInputSearch_UU, JP_SimpleInputSearch_UU);
	}

	/** Get JP_SimpleInputSearch_UU.
		@return JP_SimpleInputSearch_UU	  */
	public String getJP_SimpleInputSearch_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_SimpleInputSearch_UU);
	}

	public I_JP_SimpleInputWindow getJP_SimpleInputWindow() throws RuntimeException
    {
		return (I_JP_SimpleInputWindow)MTable.get(getCtx(), I_JP_SimpleInputWindow.Table_Name)
			.getPO(getJP_SimpleInputWindow_ID(), get_TrxName());	}

	/** Set Simple Input Window.
		@param JP_SimpleInputWindow_ID Simple Input Window	  */
	public void setJP_SimpleInputWindow_ID (int JP_SimpleInputWindow_ID)
	{
		if (JP_SimpleInputWindow_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_SimpleInputWindow_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_SimpleInputWindow_ID, Integer.valueOf(JP_SimpleInputWindow_ID));
	}

	/** Get Simple Input Window.
		@return Simple Input Window	  */
	public int getJP_SimpleInputWindow_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_SimpleInputWindow_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Sequence.
		@param SeqNo 
		Method of ordering records; lowest number comes first
	  */
	public void setSeqNo (int SeqNo)
	{
		set_Value (COLUMNNAME_SeqNo, Integer.valueOf(SeqNo));
	}

	/** Get Sequence.
		@return Method of ordering records; lowest number comes first
	  */
	public int getSeqNo () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SeqNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set X Position.
		@param XPosition 
		Absolute X (horizontal) position in 1/72 of an inch
	  */
	public void setXPosition (int XPosition)
	{
		set_Value (COLUMNNAME_XPosition, Integer.valueOf(XPosition));
	}

	/** Get X Position.
		@return Absolute X (horizontal) position in 1/72 of an inch
	  */
	public int getXPosition () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_XPosition);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}