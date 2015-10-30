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

/** Generated Model for JP_SimpleInputField
 *  @author iDempiere (generated) 
 *  @version Release 2.1 - $Id$ */
public class X_JP_SimpleInputField extends PO implements I_JP_SimpleInputField, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20151030L;

    /** Standard Constructor */
    public X_JP_SimpleInputField (Properties ctx, int JP_SimpleInputField_ID, String trxName)
    {
      super (ctx, JP_SimpleInputField_ID, trxName);
      /** if (JP_SimpleInputField_ID == 0)
        {
			setAD_Field_ID (0);
			setIsSummarized (false);
// N
			setJP_SimpleInputField_ID (0);
			setJP_SimpleInputWindow_ID (0);
			setSeqNo (0);
// @SQL=SELECT COALESCE(MAX(SeqNo),0)+10 AS DefaultValue FROM JP_SimpleInputField WHERE JP_SimpleInputWindow_ID=@JP_SimpleInputWindow_ID@
        } */
    }

    /** Load Constructor */
    public X_JP_SimpleInputField (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_SimpleInputField[")
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
			set_ValueNoCheck (COLUMNNAME_AD_Field_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_AD_Field_ID, Integer.valueOf(AD_Field_ID));
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

	/** Set Length.
		@param FieldLength 
		Length of the column in the database
	  */
	public void setFieldLength (int FieldLength)
	{
		set_Value (COLUMNNAME_FieldLength, Integer.valueOf(FieldLength));
	}

	/** Get Length.
		@return Length of the column in the database
	  */
	public int getFieldLength () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_FieldLength);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Calculate Sum (Σ).
		@param IsSummarized 
		Calculate the Sum of numeric content or length
	  */
	public void setIsSummarized (boolean IsSummarized)
	{
		set_Value (COLUMNNAME_IsSummarized, Boolean.valueOf(IsSummarized));
	}

	/** Get Calculate Sum (Σ).
		@return Calculate the Sum of numeric content or length
	  */
	public boolean isSummarized () 
	{
		Object oo = get_Value(COLUMNNAME_IsSummarized);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Simple Input Field.
		@param JP_SimpleInputField_ID Simple Input Field	  */
	public void setJP_SimpleInputField_ID (int JP_SimpleInputField_ID)
	{
		if (JP_SimpleInputField_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_SimpleInputField_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_SimpleInputField_ID, Integer.valueOf(JP_SimpleInputField_ID));
	}

	/** Get Simple Input Field.
		@return Simple Input Field	  */
	public int getJP_SimpleInputField_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_SimpleInputField_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_SimpleInputField_UU.
		@param JP_SimpleInputField_UU JP_SimpleInputField_UU	  */
	public void setJP_SimpleInputField_UU (String JP_SimpleInputField_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_SimpleInputField_UU, JP_SimpleInputField_UU);
	}

	/** Get JP_SimpleInputField_UU.
		@return JP_SimpleInputField_UU	  */
	public String getJP_SimpleInputField_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_SimpleInputField_UU);
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
}