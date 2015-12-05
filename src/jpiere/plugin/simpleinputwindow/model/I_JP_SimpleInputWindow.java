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
package jpiere.plugin.simpleinputwindow.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for JP_SimpleInputWindow
 *  @author iDempiere (generated) 
 *  @version Release 3.1
 */
@SuppressWarnings("all")
public interface I_JP_SimpleInputWindow 
{

    /** TableName=JP_SimpleInputWindow */
    public static final String Table_Name = "JP_SimpleInputWindow";

    /** AD_Table_ID=1000039 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 4 - System 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(4);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within client
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
	  */
	public int getAD_Org_ID();

    /** Column name AD_Tab_ID */
    public static final String COLUMNNAME_AD_Tab_ID = "AD_Tab_ID";

	/** Set Tab.
	  * Tab within a Window
	  */
	public void setAD_Tab_ID (int AD_Tab_ID);

	/** Get Tab.
	  * Tab within a Window
	  */
	public int getAD_Tab_ID();

	public org.compiere.model.I_AD_Tab getAD_Tab() throws RuntimeException;

    /** Column name AD_Window_ID */
    public static final String COLUMNNAME_AD_Window_ID = "AD_Window_ID";

	/** Set Window.
	  * Data entry or display window
	  */
	public void setAD_Window_ID (int AD_Window_ID);

	/** Get Window.
	  * Data entry or display window
	  */
	public int getAD_Window_ID();

	public org.compiere.model.I_AD_Window getAD_Window() throws RuntimeException;

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name Description */
    public static final String COLUMNNAME_Description = "Description";

	/** Set Description.
	  * Optional short description of the record
	  */
	public void setDescription (String Description);

	/** Get Description.
	  * Optional short description of the record
	  */
	public String getDescription();

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name IsDeleteable */
    public static final String COLUMNNAME_IsDeleteable = "IsDeleteable";

	/** Set Records deletable.
	  * Indicates if records can be deleted from the database
	  */
	public void setIsDeleteable (boolean IsDeleteable);

	/** Get Records deletable.
	  * Indicates if records can be deleted from the database
	  */
	public boolean isDeleteable();

    /** Column name IsSummarized */
    public static final String COLUMNNAME_IsSummarized = "IsSummarized";

	/** Set Calculate Sum (Σ).
	  * Calculate the Sum of numeric content or length
	  */
	public void setIsSummarized (boolean IsSummarized);

	/** Get Calculate Sum (Σ).
	  * Calculate the Sum of numeric content or length
	  */
	public boolean isSummarized();

    /** Column name JP_FrozenField */
    public static final String COLUMNNAME_JP_FrozenField = "JP_FrozenField";

	/** Set Frozen Field	  */
	public void setJP_FrozenField (int JP_FrozenField);

	/** Get Frozen Field	  */
	public int getJP_FrozenField();

    /** Column name JP_JoinClause */
    public static final String COLUMNNAME_JP_JoinClause = "JP_JoinClause";

	/** Set SQL JOIN	  */
	public void setJP_JoinClause (String JP_JoinClause);

	/** Get SQL JOIN	  */
	public String getJP_JoinClause();

    /** Column name JP_PageSize */
    public static final String COLUMNNAME_JP_PageSize = "JP_PageSize";

	/** Set Page Size	  */
	public void setJP_PageSize (int JP_PageSize);

	/** Get Page Size	  */
	public int getJP_PageSize();

    /** Column name JP_SimpleInputWindow_ID */
    public static final String COLUMNNAME_JP_SimpleInputWindow_ID = "JP_SimpleInputWindow_ID";

	/** Set Simple Input Window	  */
	public void setJP_SimpleInputWindow_ID (int JP_SimpleInputWindow_ID);

	/** Get Simple Input Window	  */
	public int getJP_SimpleInputWindow_ID();

    /** Column name JP_SimpleInputWindow_UU */
    public static final String COLUMNNAME_JP_SimpleInputWindow_UU = "JP_SimpleInputWindow_UU";

	/** Set JP_SimpleInputWindow_UU	  */
	public void setJP_SimpleInputWindow_UU (String JP_SimpleInputWindow_UU);

	/** Get JP_SimpleInputWindow_UU	  */
	public String getJP_SimpleInputWindow_UU();

    /** Column name JP_TabField_ID */
    public static final String COLUMNNAME_JP_TabField_ID = "JP_TabField_ID";

	/** Set Tab Field	  */
	public void setJP_TabField_ID (int JP_TabField_ID);

	/** Get Tab Field	  */
	public int getJP_TabField_ID();

	public org.compiere.model.I_AD_Field getJP_TabField() throws RuntimeException;

    /** Column name Name */
    public static final String COLUMNNAME_Name = "Name";

	/** Set Name.
	  * Alphanumeric identifier of the entity
	  */
	public void setName (String Name);

	/** Get Name.
	  * Alphanumeric identifier of the entity
	  */
	public String getName();

    /** Column name OrderByClause */
    public static final String COLUMNNAME_OrderByClause = "OrderByClause";

	/** Set Sql ORDER BY.
	  * Fully qualified ORDER BY clause
	  */
	public void setOrderByClause (String OrderByClause);

	/** Get Sql ORDER BY.
	  * Fully qualified ORDER BY clause
	  */
	public String getOrderByClause();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();

    /** Column name Value */
    public static final String COLUMNNAME_Value = "Value";

	/** Set Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value);

	/** Get Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public String getValue();

    /** Column name WhereClause */
    public static final String COLUMNNAME_WhereClause = "WhereClause";

	/** Set Sql WHERE.
	  * Fully qualified SQL WHERE clause
	  */
	public void setWhereClause (String WhereClause);

	/** Get Sql WHERE.
	  * Fully qualified SQL WHERE clause
	  */
	public String getWhereClause();
}
