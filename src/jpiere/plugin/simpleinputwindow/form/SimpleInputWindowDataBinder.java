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

import java.util.HashMap;
import java.util.TreeMap;

import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.panel.CustomForm;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.zkoss.zul.ListModelMap;
//import jpiere.plugin.matrixwindow.base.IMatrixWindowCallout;
//import jpiere.plugin.matrixwindow.base.IMatrixWindowCalloutFactory;

/**
 * Transfer data from editor to GridTab
 *
 * JPIERE-0098
 *
 * @author hengsin
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class SimpleInputWindowDataBinder implements ValueChangeListener {

	private final static CLogger logger = CLogger.getCLogger(SimpleInputWindowDataBinder.class);

	private GridTab gridTab;

	//View Model:Map of Data Model for Display<Identifier of Row.<Column Number,data>>
	private ListModelMap<Object, Object>  viewModel;

	//Convertion Table:Connect View Model with Table Modle<Identifier of Row.<Column Number,Identifier of Data>>
	private ListModelMap<Object, Object>  convetionTable ;

	//Map of PO Instance that corresponding to Table.<ID of PO,PO>
	private HashMap<Integer,PO> 	tableModel;

	//Map of PO Instance that have to save.<ID of PO,PO>
	private HashMap<Integer,PO> 	dirtyModel;

	//Map of All Column GridField <Column order num,,GridField>
	private HashMap<Integer,GridField> columnGridFieldMap;

	//Map of All Column WEditor <Column order num,,WEditor>
	private HashMap<Integer,WEditor>   columnEditorMap;

	private CustomForm form;

	/**
	 *
	 * @param gridTab
	 */
	public SimpleInputWindowDataBinder(GridTab gridTab)
	{
		this.gridTab = gridTab;
	}

	public ListModelMap<Object, Object>  getViewModel()
	{
		return viewModel;
	}

	public  ListModelMap<Object, Object>  getConvetionTable()
	{
		return convetionTable;
	}

	public HashMap<Integer,PO> 	getTableModel()
	{
		return tableModel;
	}

	public HashMap<Integer,PO> getDirtyModel()
	{
		return dirtyModel;
	}

	public void setColumnGridFieldMap(HashMap<Integer,GridField> columnGridFieldMap)
	{
		this.columnGridFieldMap = columnGridFieldMap;
	}

	public HashMap<Integer,GridField> getColumnGridFieldMap()
	{
		return columnGridFieldMap;
	}

	public void setColumnEditorMap(HashMap<Integer,WEditor>  columnEditorMap)
	{
		this.columnEditorMap = columnEditorMap;
	}

	public HashMap<Integer,WEditor>  getColumnEditorMap()
	{
		return columnEditorMap;
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
        	//Step1:Get Row(Y) and Column(X) info
        	WEditor editor = (WEditor) source;
        	String[] yx = editor.getComponent().getId().split("_");
            	int y =Integer.valueOf(yx[0]);
            	int x =Integer.valueOf(yx[1]);

            //Step2:Update ViewModel data for display data.Please refer to JPMatrixGridRowRenderer.editRow() method.
        	ListModelMap.Entry<Object, Object>  viewModelRow = viewModel.getElementAt(y);
        	@SuppressWarnings("unchecked")
			TreeMap<Integer,Object> viewModelRowData = (TreeMap<Integer,Object>)viewModelRow.getValue();
        	Object oldValue = viewModelRowData.get(x);
        	viewModelRowData.put(x, newValue);

          	//Step3:Update Context : GridField.setValue method can update context
        	editor.getGridField().setValue(newValue, false);

        	//Step4:Update tableModel for consistency.Get Po's ID form convetionTable
           	ListModelMap.Entry<Object, Object>  conversionTableRow = convetionTable.getElementAt(y);
        	@SuppressWarnings("unchecked")
    		TreeMap<Integer,Object> conversionTableRowData = (TreeMap<Integer,Object>)conversionTableRow.getValue();
        	Object PO_ID = conversionTableRowData.get(x);
        	PO po = tableModel.get(PO_ID);
        	po.set_ValueNoCheck(editor.getColumnName(), newValue);

        	//Sstep5:Put map of dirtyModel for save data.
        	dirtyModel.put((Integer)PO_ID, po);

        	//Callout
//    		List<IMatrixWindowCalloutFactory> factories = Service.locator().list(IMatrixWindowCalloutFactory.class).getServices();
//    		if (factories != null)
//    		{
//    			String calloutMessage = null;
//    			for(IMatrixWindowCalloutFactory factory : factories)
//    			{
//    				IMatrixWindowCallout callout = factory.getCallout(po.get_TableName(), editor.getColumnName());
//    				if(callout != null)
//    				{
//    					calloutMessage =callout.start(this, x, y, newValue, oldValue);
//    					if(calloutMessage != null && !calloutMessage.equals(""))
//    					{
//    						getColumnGridFieldMap().get(0).getGridTab().fireDataStatusEEvent("Message",calloutMessage, false);
//    						logger.saveError("Error", new Exception(calloutMessage));
//    					}
//
//    				}
//
//    			}//for
//
//    		}//if (factories != null)

        }//if (source instanceof WEditor)

    } // ValueChange

	public void setValue(int x, int y, Object newValue)
	{
    	//Step1:Update Editor Value for display data.
    	WEditor editor = columnEditorMap.get(x);
    	editor.setValue(newValue);

    	//Step2:Update ViewModel data for display data.Please refer to JPMatrixGridRowRenderer.editRow() method.
       	ListModelMap.Entry<Object, Object>  viewModelRow = viewModel.getElementAt(y);
    	@SuppressWarnings("unchecked")
		TreeMap<Integer,Object> viewModelRowData = (TreeMap<Integer,Object>)viewModelRow.getValue();
    	viewModelRowData.put(x, newValue);

    	//Step3:Update Context : GridField.setValue method can update context
    	editor.getGridField().setValue(newValue, false);

    	//Step4:Update tableModel for consistency.Get Po's ID form convetionTable
       	ListModelMap.Entry<Object, Object>  conversionTableRow = convetionTable.getElementAt(y);
    	@SuppressWarnings("unchecked")
		TreeMap<Integer,Object> conversionTableRowData = (TreeMap<Integer,Object>)conversionTableRow.getValue();
    	Object PO_ID = conversionTableRowData.get(x);
    	PO po = tableModel.get(PO_ID);
    	po.set_ValueNoCheck(editor.getColumnName(), editor.getValue());

    	//Sstep5:Put map of dirtyModel for save data.
    	dirtyModel.put((Integer)PO_ID, po);
	}


}