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
package jpiere.plugin.simpleinputwindow.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jpiere.plugin.simpleinputwindow.base.ISimpleInputWindowCallout;
import jpiere.plugin.simpleinputwindow.base.ISimpleInputWindowCalloutFactory;

import org.adempiere.base.Service;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.GridField;
import org.compiere.model.MLookup;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.zkoss.zul.Cell;

/**
 * Transfer data from editor to GridTab
 *
 * JPIERE-0111
 *
 * @author hengsin
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class SimpleInputWindowDataBinder implements ValueChangeListener {

	private final static CLogger logger = CLogger.getCLogger(SimpleInputWindowDataBinder.class);

	private SimpleInputWindowGridRowRenderer rendere;

	private SimpleInputWindowListModel listModel;

	//Map of PO Instance that have to save.<ID of PO,PO>
	private HashMap<Integer,PO> 	dirtyModel;

	//Map that is ID of PO and LineNo for save.< ID of PO, LieNo>
	private HashMap<Integer,Integer>  dirtyLineNo ;

	private JPiereSimpleInputWindow simpleInputWindow;

	/**
	 *
	 * @param gridTab
	 */
	public SimpleInputWindowDataBinder(JPiereSimpleInputWindow simpleInputWindow, SimpleInputWindowGridRowRenderer rendere, SimpleInputWindowListModel listModel)
	{
		this.simpleInputWindow = simpleInputWindow;
		this.rendere = rendere;
		this.listModel = listModel;
		this.dirtyModel = simpleInputWindow.getDirtyModel();
		this.dirtyLineNo = simpleInputWindow.getDirtyLineNo();
	}


	/**
	 * @param e
	 */
	public void valueChange(ValueChangeEvent e)
    {

		Object newValue = e.getNewValue();

        Object source = e.getSource();
        if (source instanceof WEditor)
        {
        	//Step1:
        	WEditor editor = (WEditor) source;
        	GridField gridField = editor.getGridField();

            //Step2:Update ViewModel data for display data.Please refer to JPMatrixGridRowRenderer.editRow() method.
        	Object oldValue = listModel.getValueAt(rendere.getCurrentRowIndex(), gridField);
            listModel.setValueAt (newValue, rendere.getCurrentRowIndex(), gridField);

            //Step3:Update Context : GridField.setValue method can update context
            gridField.setValue(newValue, false);

            //Step4:dirty model
            PO po = listModel.getPO(rendere.getCurrentRowIndex());
            if(!dirtyModel.containsKey((Integer)po.get_ID()) && po.get_ID()!=0)
            {
            	Cell  lineNoCell =  (Cell)editor.getComponent().getParent().getParent().getChildren().get(1);
            	org.zkoss.zul.Label lineNoLabel = (org.zkoss.zul.Label)lineNoCell.getChildren().get(0);
            	lineNoLabel.setValue("*"+lineNoLabel.getValue());
            }

            if(po.get_ID()==0)
            {
            	;
            }else{
	            dirtyModel.put((Integer)po.get_ID(), po);
	            dirtyLineNo.put((Integer)po.get_ID(),rendere.getCurrentRowIndex());
            }

            //Callout
			List<ISimpleInputWindowCalloutFactory> factories = Service.locator().list(ISimpleInputWindowCalloutFactory.class).getServices();
			if (factories != null)
			{
				String calloutMessage = null;
				for(ISimpleInputWindowCalloutFactory factory : factories)
				{
					ISimpleInputWindowCallout callout = factory.getCallout(po.get_TableName(), editor.getColumnName());
					if(callout != null)
					{
						calloutMessage =callout.start(this, rendere.getCurrentRowIndex(), gridField.getColumnName(), newValue, oldValue);
						if(calloutMessage != null && !calloutMessage.equals(""))
						{
							FDialog.error(simpleInputWindow.getForm().getWindowNo(), calloutMessage);
							logger.saveError("Error", new Exception(calloutMessage));
						}

						ArrayList<GridField> dependants = simpleInputWindow.getGridTab().getDependantFields(editor.getColumnName());
						if(dependants.size() > 0)
						{
				    		for (GridField dependentField : dependants)
				    		{
				    			//  if the field has a lookup
				    			if (dependentField != null && dependentField.getLookup() instanceof MLookup)
				    			{
				    				MLookup mLookup = (MLookup)dependentField.getLookup();
				    				//  if the lookup is dynamic (i.e. contains this columnName as variable)
				    				if (mLookup.getValidation().indexOf("@"+gridField.getColumnName()+"@") != -1)
				    				{
				    					mLookup.refresh();
				    				}
				    			}
				    		}   //  for all dependent fields
						}

						break;
					}

				}//for

			}//if (factories != null)
			//Callout finish

           return;

        }

    } // ValueChange


	public void setValue(int rowIndex, String columnName, Object newValue)
	{
    	//Step1:Update Editor Value for display data.
		List<WEditor>  editors = rendere.getEditors();
		boolean haveEditor = false;
		for(WEditor editor : editors)
		{
			if(editor.getColumnName().equals(columnName))
			{
				editor.setValue(newValue);

		        //Step2:Update ViewModel data for display data.Please refer to JPMatrixGridRowRenderer.editRow() method.
		    	Object oldValue = listModel.getValueAt(rendere.getCurrentRowIndex(), editor.getGridField());
		        listModel.setValueAt (newValue, rendere.getCurrentRowIndex(), editor.getGridField());

	            //Step3:Update Context : GridField.setValue method can update context
		        editor.getGridField().setValue(newValue, false);

	            //Step4:dirty model
	            PO po = listModel.getPO(rendere.getCurrentRowIndex());
	            if(!dirtyModel.containsKey((Integer)po.get_ID()) && po.get_ID()!=0)
	            {
	            	Cell  lineNoCell =  (Cell)editor.getComponent().getParent().getParent().getChildren().get(1);
	            	org.zkoss.zul.Label lineNoLabel = (org.zkoss.zul.Label)lineNoCell.getChildren().get(0);
	            	lineNoLabel.setValue("*"+lineNoLabel.getValue());
	            }

	            haveEditor = true;
		        break;
			}
		}//for

		if(!haveEditor)
		{
			PO po = listModel.getPO(rendere.getCurrentRowIndex());
			po.set_ValueNoCheck(columnName, newValue);
		}

	}

	public Object getValue(int rowIndex, String columnName)
	{
		return listModel.getValueAt(rowIndex, columnName);
	}

	public JPiereSimpleInputWindow getSimpleInputWindow()
	{
		return simpleInputWindow;
	}

}