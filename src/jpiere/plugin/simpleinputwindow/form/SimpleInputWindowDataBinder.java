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

import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.PO;
import org.compiere.util.CLogger;

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

	private GridTab gridTab;

	private SimpleInputWindowGridRowRenderer rendere;

	private SimpleInputWindowListModel listModel;

	//Map of PO Instance that have to save.<ID of PO,PO>
	private HashMap<Integer,PO> 	dirtyModel;

	/**
	 *
	 * @param gridTab
	 */
	public SimpleInputWindowDataBinder(GridTab gridTab, SimpleInputWindowGridRowRenderer rendere
										,SimpleInputWindowListModel listModel,HashMap<Integer,PO> dirtyModel)
	{
		this.gridTab = gridTab;
		this.rendere = rendere;
		this.listModel = listModel;
		this.dirtyModel = dirtyModel;
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
            listModel.setValueAt (newValue, rendere.getCurrentRowIndex(), gridField);

            //Step3:Update Context : GridField.setValue method can update context
            gridField.setValue(newValue, false);

            //Step4:dirty model
            PO po = listModel.getPO(rendere.getCurrentRowIndex());
            dirtyModel.put((Integer)po.get_ID(), po);
        }

    } // ValueChange

}