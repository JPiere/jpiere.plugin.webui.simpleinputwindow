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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.adempiere.util.GridRowCtx;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.adwindow.ADWindow;
import org.adempiere.webui.adwindow.AbstractADWindowContent;
import org.adempiere.webui.adwindow.GridView;
import org.adempiere.webui.adwindow.IADTabpanel;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.Datebox;
import org.adempiere.webui.component.NumberBox;
import org.adempiere.webui.component.Searchbox;
import org.adempiere.webui.editor.WButtonEditor;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WEditorPopupMenu;
import org.adempiere.webui.editor.WebEditorFactory;
import org.adempiere.webui.event.ActionEvent;
import org.adempiere.webui.event.ActionListener;
import org.adempiere.webui.event.ContextMenuListener;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.HelpController;
import org.adempiere.webui.session.SessionManager;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.PO;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.zkoss.zk.au.out.AuScript;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.RendererCtrl;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.RowRendererExt;
import org.zkoss.zul.impl.XulElement;


/**
 * JPiere Simple Input Window GridRowRenderer
 *
 * JPIERE-0111
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class SimpleInputWindowGridRowRenderer implements RowRenderer<Object[]> ,RowRendererExt, RendererCtrl,EventListener<Event>{

	public static final String GRID_ROW_INDEX_ATTR = "grid.row.index";
	private static final String CELL_DIV_STYLE = "height: 100%; cursor: pointer; ";
	private static final String CELL_DIV_STYLE_ALIGN_CENTER = CELL_DIV_STYLE + "text-align:center; ";
	private static final String CELL_DIV_STYLE_ALIGN_RIGHT = CELL_DIV_STYLE + "text-align:right; ";

	private static final int MAX_TEXT_LENGTH = 60;
	private GridTab gridTab ;
	private int windowNo;
	private SimpleInputWindowDataBinder dataBinder;
	private Map<GridField, WEditor> editors = new LinkedHashMap<GridField, WEditor>();
	private Map<GridField, WEditor> readOnlyEditors = new LinkedHashMap<GridField, WEditor>();

	private RowListener rowListener;

	private Grid grid = null;
	private GridView gridView = null;
	private Row currentRow;
	private Object[] currentValues;
	private PO currentPO;
	private boolean editing = false;
	private int currentRowIndex = -1;
	private int currentColumnIndex = -1;
	private AbstractADWindowContent m_windowPanel;
	private ActionListener buttonListener;
	/**
	 * Flag detect this view has customized column or not
	 * value is set at {@link #render(Row, Object[], int)}
	 */
//	private boolean isGridViewCustomized = false;
	/** DefaultFocusField		*/
	private WEditor	defaultFocusField = null;

	private CustomForm form ;

	private JPiereSimpleInputWindow simpleInputWindow;

	private SimpleInputWindowListModel listModel;

	/**
	 *
	 * @param gridTab
	 * @param form
	 */
	public SimpleInputWindowGridRowRenderer(GridTab gridTab ,CustomForm form
								,SimpleInputWindowListModel listModel,HashMap<Integer,PO> dirtyModel)
	{
		this.windowNo = form.getWindowNo();
		this.form = form;
		this.listModel = listModel;
		this.dataBinder = new SimpleInputWindowDataBinder(gridTab, this, listModel, dirtyModel);
	}

	//TODO
	private WEditor getEditorCell(GridField gridField) {
		WEditor editor = editors.get(gridField);

		if (editor != null)  {
			prepareFieldEditor(gridField, editor);
			editor.addValueChangeListener(dataBinder);
			gridField.removePropertyChangeListener(editor);
			gridField.addPropertyChangeListener(editor);
//			editor.setValue(gridField.getValue());
		}
		return editor;
	}

	private void prepareFieldEditor(GridField gridField, WEditor editor){
		if (editor instanceof WButtonEditor)
        {
			if (buttonListener != null)
			{
				((WButtonEditor)editor).addActionListener(buttonListener);
			}
			else
			{
				Object window = SessionManager.getAppDesktop().findWindow(windowNo);
            	if (window != null && window instanceof ADWindow)
            	{
            		AbstractADWindowContent windowPanel = ((ADWindow)window).getADWindowContent();
            		((WButtonEditor)editor).addActionListener(windowPanel);
            	}
			}
        }

        //streach component to fill grid cell
		if (editor.getComponent() instanceof HtmlBasedComponent) {
        	editor.fillHorizontal();
		}
	}

	public int getColumnIndex(GridField field) {
		GridField[] fields = gridView.getFields();
		for(int i = 0; i < fields.length; i++) {
			if (fields[i] == field)
				return i;
		}
		return 0;
	}


	private Component createReadonlyCheckbox(Object value) {
		Checkbox checkBox = new Checkbox();
		if (value != null && "true".equalsIgnoreCase(value.toString()))
			checkBox.setChecked(true);
		else
			checkBox.setChecked(false);
		checkBox.setDisabled(true);
		return checkBox;
	}

	/**
	 * call {@link #getDisplayText(Object, GridField, int, boolean)} with isForceGetValue = false
	 * @param value
	 * @param gridField
	 * @param rowIndex
	 * @return
	 */
	private String getDisplayText(Object value, GridField gridField, int rowIndex){
		return getDisplayText(value, gridField, rowIndex, false);
	}

	/**
	 * Get display text of a field. when field have isDisplay = false always return empty string, except isForceGetValue = true
	 * @param value
	 * @param gridField
	 * @param rowIndex
	 * @param isForceGetValue
	 * @return
	 */
	private String getDisplayText(Object value, GridField gridField, int rowIndex, boolean isForceGetValue)
	{
		if (value == null)
			return "";

		if (rowIndex >= 0) {
			GridRowCtx gridRowCtx = new GridRowCtx(Env.getCtx(), gridTab, rowIndex);
			if (!isForceGetValue && !gridField.isDisplayed(gridRowCtx, true)) {
				return "";
			}
		}

		if (gridField.isEncryptedField())
		{
			return "********";
		}
		else if (readOnlyEditors.get(gridField) != null)
		{
			WEditor editor = readOnlyEditors.get(gridField);
			return editor.getDisplayTextForGridView(value);
		}
    	else
    		return value.toString();
	}


	/**
	 * get component to display value of a field.
	 * when display is boolean or button, return correspond component
	 * other return a label with text get from {@link #getDisplayText(Object, GridField, int, boolean)}
	 * @param rowIndex
	 * @param value
	 * @param gridField
	 * @param isForceGetValue
	 * @return
	 */
	private Component getDisplayComponent(int rowIndex, Object value, GridField gridField) {
		Component component;
		if (gridField.getDisplayType() == DisplayType.YesNo) {
			component = createReadonlyCheckbox(value);
		} else if (gridField.getDisplayType() == DisplayType.Button) {
			GridRowCtx gridRowCtx = new GridRowCtx(Env.getCtx(), gridTab, rowIndex);
			WButtonEditor editor = new WButtonEditor(gridField, rowIndex);
			editor.setValue(gridTab.getValue(rowIndex, gridField.getColumnName()));
			editor.setReadWrite(gridField.isEditable(gridRowCtx, true,true));
			editor.getComponent().setAttribute(GRID_ROW_INDEX_ATTR, rowIndex);
			editor.addActionListener(buttonListener);
			component = editor.getComponent();
		} else {
			String text = getDisplayText(value, gridField, rowIndex);
			WEditor editor = getEditorCell(gridField);
			if (editor.getDisplayComponent() == null){
				Label label = new Label();
				setLabelText(text, label);
				component = label;
			}else{
				component = editor.getDisplayComponent();
				if (component instanceof Html){
					((Html)component).setContent(text);
				}else{
					throw new UnsupportedOperationException("neet a componet has setvalue function");
				}
			}
		}
		return component;
	}

	/**
	 * @param text
	 * @param label
	 */
	private void setLabelText(String text, Label label) {
		String display = text;
		if (text != null && text.length() > MAX_TEXT_LENGTH)
			display = text.substring(0, MAX_TEXT_LENGTH - 3) + "...";
		// since 5.0.8, the org.zkoss.zhtml.Text is encoded by default
//		if (display != null)
//			display = XMLs.encodeText(display);
		label.setValue(display);
		if (text != null && text.length() > MAX_TEXT_LENGTH)
			label.setTooltiptext(text);
	}

	/**
	 *
	 * @return active editor list
	 */
	public List<WEditor> getEditors() {
		List<WEditor> editorList = new ArrayList<WEditor>();
		if (!editors.isEmpty())
			editorList.addAll(editors.values());

		return editorList;
	}


	/**
	 * Detach all editor and optionally set the current value of the editor as cell label.
	 * @param updateCellLabel
	 */
	public void stopEditing(boolean updateCellLabel) {
		if (!editing) {
			return;
		} else {
			editing = false;
		}
		Row row = null;
		for (Entry<GridField, WEditor> entry : editors.entrySet()) {
			if (entry.getValue().getComponent().getParent() != null) {
				Component child = entry.getValue().getComponent();
				Cell div = null;
				while (div == null && child != null) {
					Component parent = child.getParent();
					if (parent instanceof Cell && parent.getParent() instanceof Row)
						div = (Cell)parent;
					else
						child = parent;
				}
				Component component = div!=null ? (Component) div.getAttribute("display.component") : null;
				if (updateCellLabel) {
					if (component instanceof Label) {
						Label label = (Label)component;
						label.getChildren().clear();
						String text = getDisplayText(entry.getValue().getValue(), entry.getValue().getGridField(), -1);
						setLabelText(text, label);
					} else if (component instanceof Checkbox) {
						Checkbox checkBox = (Checkbox)component;
						Object value = entry.getValue().getValue();
						if (value != null && "true".equalsIgnoreCase(value.toString()))
							checkBox.setChecked(true);
						else
							checkBox.setChecked(false);
					} else if (component instanceof Html){
						((Html)component).setContent(getDisplayText(entry.getValue().getValue(), entry.getValue().getGridField(), -1));
					}
				}
				if (row == null)
					row = ((Row)div.getParent());

				entry.getValue().getComponent().detach();
				entry.getKey().removePropertyChangeListener(entry.getValue());
				entry.getValue().removeValuechangeListener(dataBinder);

				if (component.getParent() == null || component.getParent() != div)
					div.appendChild(component);
				else if (!component.isVisible()) {
					component.setVisible(true);
				}
			}
		}

		SimpleInputWindowListModel model = (SimpleInputWindowListModel) grid.getModel();
		model.setEditing(false);
	}

	/**
	 * @param row
	 * @param data
	 * @see RowRenderer#render(Row, Object)
	 */
	@Override
	public void render(Row row, Object[] data, int index) throws Exception{
		//don't render if not visible
		int columnCount = 0;
		GridField[] gridPanelFields = null;//Grid
		GridField[] gridTabFields = null;

		if (simpleInputWindow != null) {
			gridPanelFields = simpleInputWindow.getFields();
			columnCount = gridPanelFields.length;
			gridTabFields = gridTab.getFields();
		}else{

		}

		if (grid == null)
			grid = (Grid) row.getParent().getParent();

		if (rowListener == null)
			rowListener = new RowListener((Grid)row.getParent().getParent());

//		if (!isGridViewCustomized) {
//			for(int i = 0; i < gridTabFields.length; i++) {
//				if (gridPanelFields[i].getAD_Field_ID() != gridTabFields[i].getAD_Field_ID()) {
//					isGridViewCustomized = true;
//					break;
//				}
//			}
//		}

		currentValues = data;

//		if (!isGridViewCustomized) {
//			currentValues = data;
//		} else {
//			List<Object> dataList = new ArrayList<Object>();
//			for(GridField gridField : gridPanelFields) {
//				for(int i = 0; i < gridTabFields.length; i++) {
//					if (gridField.getAD_Field_ID() == gridTabFields[i].getAD_Field_ID()) {
//						dataList.add(data[i]);
//						break;
//					}
//				}
//			}
//			currentValues = dataList.toArray(new Object[0]);
//		}


		Grid grid = (Grid) row.getParent().getParent();
		org.zkoss.zul.Columns columns = grid.getColumns();

		int rowIndex = index;
//		if (paging != null && paging.getPageSize() > 0) {
//			rowIndex = (paging.getActivePage() * paging.getPageSize()) + rowIndex;
//		}

		Cell cell = new Cell();
		cell.setTooltiptext(Msg.getMsg(Env.getCtx(), "Select"));
		Checkbox selection = new Checkbox();
		selection.setAttribute(GRID_ROW_INDEX_ATTR, rowIndex);
		selection.setChecked(gridTab.isSelected(rowIndex));
		cell.setStyle("border: none;");
		selection.addEventListener(Events.ON_CHECK, this);

		if (!selection.isChecked()) {
			if (simpleInputWindow.selectAll.isChecked()) {
				simpleInputWindow.selectAll.setChecked(false);
			}
		}

		cell.appendChild(selection);
		row.appendChild(cell);

		cell = new Cell();
		cell.addEventListener(Events.ON_CLICK, this);
		cell.setStyle("border: none;");
		cell.setTooltiptext(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "EditRecord")));

		row.appendChild(cell);

		Boolean isActive = null;
		int colIndex = -1;
		for (int i = 0; i < columnCount; i++) {
			if (editors.get(gridPanelFields[i]) == null) {
				WEditor editor = WebEditorFactory.getEditor(gridPanelFields[i], true);
				editors.put(gridPanelFields[i], editor);
				if (editor instanceof WButtonEditor) {
					((WButtonEditor)editor).addActionListener(buttonListener);
				}

				//readonly for display text
				WEditor readOnlyEditor = WebEditorFactory.getEditor(gridPanelFields[i], true);
				readOnlyEditor.setReadWrite(false);
				readOnlyEditors.put(gridPanelFields[i], readOnlyEditor);

				editor.getComponent().setWidgetOverride("fieldHeader", HelpController.escapeJavascriptContent(gridPanelFields[i].getHeader()));
    			editor.getComponent().setWidgetOverride("fieldDescription", HelpController.escapeJavascriptContent(gridPanelFields[i].getDescription()));
    			editor.getComponent().setWidgetOverride("fieldHelp", HelpController.escapeJavascriptContent(gridPanelFields[i].getHelp()));
    			editor.getComponent().setWidgetListener("onFocus", "zWatch.fire('onFieldTooltip', this, null, this.fieldHeader(), this.fieldDescription(), this.fieldHelp());");

    			//	Default Focus
    			if (defaultFocusField == null && gridPanelFields[i].isDefaultFocus())
    				defaultFocusField = editor;
			}

			if ("IsActive".equals(gridPanelFields[i].getColumnName())) {
				isActive = Boolean.FALSE;
				if (currentValues[i] != null) {
					if ("true".equalsIgnoreCase(currentValues[i].toString())) {
						isActive = Boolean.TRUE;
					}
				}
			}

			// IDEMPIERE-2148: when has tab customize, ignore check properties isDisplayedGrid
//			if ((!isGridViewCustomized && gridPanelFields[i].isDisplayedGrid()) || gridPanelFields[i].isToolbarOnlyButton()) {
//				continue;
//			}

			colIndex ++;

			Cell div = new Cell();
			String divStyle = CELL_DIV_STYLE;
			org.zkoss.zul.Column column = (org.zkoss.zul.Column) columns.getChildren().get(colIndex);
			if (column.isVisible()) {
				Component component = getDisplayComponent(rowIndex, currentValues[i], gridPanelFields[i]);
				div.appendChild(component);
				div.setAttribute("display.component", component);
				div.setId(String.valueOf(row.getIndex())+"_"+String.valueOf(i));//Set RowIndex(Y-axis) and Column(X-axis) in ID of Cell(div)

				if (DisplayType.YesNo == gridPanelFields[i].getDisplayType() || DisplayType.Image == gridPanelFields[i].getDisplayType()) {
					divStyle = CELL_DIV_STYLE_ALIGN_CENTER;
				}
				else if (DisplayType.isNumeric(gridPanelFields[i].getDisplayType())) {
					divStyle = CELL_DIV_STYLE_ALIGN_RIGHT;
				}

				GridRowCtx ctx = new GridRowCtx(Env.getCtx(), gridTab, rowIndex);
				if (!gridPanelFields[i].isDisplayed(ctx, true)){
					// IDEMPIERE-2253
					div.removeChild(component);
				}
			}
			div.setStyle(divStyle);
			div.setWidth("100%");
			div.setAttribute("columnName", gridPanelFields[i].getColumnName());
			div.addEventListener(Events.ON_CLICK, rowListener);
//			div.addEventListener(Events.ON_DOUBLE_CLICK, rowListener);
			row.appendChild(div);
		}

		if (rowIndex == gridTab.getCurrentRow()) {
			setCurrentRow(row);
		}

		row.setStyle("cursor:pointer");
		row.addEventListener(Events.ON_CLICK, rowListener);
//		row.addEventListener(Events.ON_OK, rowListener);
		row.setTooltiptext("Row " + (rowIndex+1));

		if (isActive == null) {
			Object isActiveValue = gridTab.getValue(rowIndex, "IsActive");
			if (isActiveValue != null) {
				if ("true".equalsIgnoreCase(isActiveValue.toString())) {
					isActive = Boolean.TRUE;
				} else {
					isActive = Boolean.FALSE;
				}
			}
		}
		if (isActive != null && !isActive.booleanValue()) {
			LayoutUtils.addSclass("grid-inactive-row", row);
		}


	}


	/**
	 * @param row
	 */
	public void setCurrentRow(Row row) {
		if (currentRow != null && currentRow.getParent() != null && currentRow != row) {
			Cell cell = (Cell) currentRow.getChildren().get(1);
			if (cell != null) {
				cell.setSclass("row-indicator");
			}
		}

		currentRowIndex = rowListener.getRowIndex();
		currentColumnIndex =rowListener.getColumnIndex();
		currentPO =listModel.getPO(currentRowIndex);
		List<Row> rowList = grid.getRows().getChildren();
		currentRow = rowList.get(currentRowIndex);

		if (editing) {
			stopEditing(true);
			editCurrentRow();
		}

		String script = "jq('#"+currentRow.getUuid()+"').addClass('highlight').siblings().removeClass('highlight')";

		Boolean isActive = null;
		Object isActiveValue = gridTab.getValue(currentRowIndex, "IsActive");
		if (isActiveValue != null) {
			if ("true".equalsIgnoreCase(isActiveValue.toString())) {
				isActive = Boolean.TRUE;
			} else {
				isActive = Boolean.FALSE;
			}
		}
		if (isActive != null && !isActive.booleanValue()) {
			script = "jq('#"+row.getUuid()+"').addClass('grid-inactive-row').siblings().removeClass('highlight')";
		}

		Clients.response(new AuScript(script));
	}

	/**
	 * @return Row
	 */
	public Row getCurrentRow() {
		return currentRow;
	}

	/**
	 * @return current row index ( absolute )
	 */
	public int getCurrentRowIndex() {
		return currentRowIndex;
	}

	public int getCurrentColumnIndex() {
		return currentColumnIndex;
	}

	NumberBox numberbox ;
	Datebox datebox ;
	Searchbox searchbox ;
	Combobox combobox;


	/**
	 * Enter edit mode
	 */
	public void editCurrentRow() {

		Cell cell = (Cell) currentRow.getChildren().get(1);
		if (cell != null) {
			cell.setSclass("row-indicator-selected");
		}

		Env.setContext(Env.getCtx(), form.getWindowNo(), 0, "IsActive", currentPO.get_ValueAsBoolean("IsActive"));

		if (currentRow != null && currentRow.getParent() != null && currentRow.isVisible()
				&& grid != null && grid.isVisible() && grid.getParent() != null && grid.getParent().isVisible()) {
				GridField[] simpleInputFields = simpleInputWindow.getFields();

				int columnCount = simpleInputFields.length;
				org.zkoss.zul.Columns columns = grid.getColumns();
				//skip selection and indicator column
				int colIndex = 1;

				currentValues =(Object[])listModel.getElementAt(currentRowIndex);

				//TODO:Set Context コンテキストを設定するとすべて読取専用になってしまうので要調査
//				Object obj = null;
//				PO po =listModel.getPO(currentRowIndex);
//				for(int p = 0; p < po.get_ColumnCount(); p++)
//				{
//					obj = po.get_Value(p);
//					if(obj==null)
//						Env.setContext(Env.getCtx(), form.getWindowNo(), 0, po.get_ColumnName(p), null);
//					else
//						Env.setContext(Env.getCtx(), form.getWindowNo(), 0, po.get_ColumnName(p), po.get_Value(p).toString());
//				}

				for (int i = 0; i < columnCount; i++) {
					if (simpleInputFields[i].isToolbarOnlyButton()) {
						continue;
					}
					colIndex ++;

					if (editors.get(simpleInputFields[i]) == null)
						editors.put(simpleInputFields[i], WebEditorFactory.getEditor(simpleInputFields[i], true));

					org.zkoss.zul.Column column = (org.zkoss.zul.Column) columns.getChildren().get(colIndex);
					if (column.isVisible()) {
						Cell div = (Cell) currentRow.getChildren().get(colIndex);
						WEditor editor = getEditorCell(simpleInputFields[i]);
						editor.setValue(currentValues[i]);
						editor.getComponent().addEventListener(Events.ON_OK, this);//OnEvent()

						if (div.getChildren().isEmpty() || !(div.getChildren().get(0) instanceof Button))
							div.getChildren().clear();
						else if (!div.getChildren().isEmpty()) {
							div.getChildren().get(0).setVisible(false);
						}
						div.appendChild(editor.getComponent());
						WEditorPopupMenu popupMenu = editor.getPopupMenu();

			            if (popupMenu != null)
			            {
			            	popupMenu.addMenuListener((ContextMenuListener)editor);
			            	div.appendChild(popupMenu);
			            	popupMenu.addContextElement((XulElement) editor.getComponent());
			            }


			            Properties ctx = simpleInputFields[i].getVO().ctx;
			            //check context
						if (!simpleInputFields[i].isDisplayed(ctx, true)){
							// IDEMPIERE-2253
							div.removeChild(editor.getComponent());
						}

						//control focus
						if(i==currentColumnIndex)
						{

							Component cmp =div.getChildren().get(0);

							if(div.getChildren().get(0) instanceof NumberBox)
							{
								numberbox = (NumberBox)div.getChildren().get(0);
			    	        	numberbox.focus();
			    	        	numberbox.getDecimalbox().select();
							}else if(div.getChildren().get(0) instanceof Datebox){

								datebox = (Datebox)div.getChildren().get(0);
								datebox.focus();
								datebox.select();

							}else if(div.getChildren().get(0) instanceof Combobox){

								combobox = (Combobox)div.getChildren().get(0);
								combobox.focus();
								combobox.select();

							}else if(div.getChildren().get(0) instanceof Searchbox){

								searchbox = (Searchbox)div.getChildren().get(0);
								searchbox.focus();
								searchbox.getTextbox().select();

							}else{
								div.focus();
							}
						}

						editor.setReadWrite(simpleInputFields[i].isEditableGrid(true));

					}//if (column.isVisible())
				}//for
				editing = true;

				listModel.setEditing(true);

			}
	}

	/**
	 * @see RowRendererExt#getControls()
	 */
	@Override
	public int getControls() {
		return DETACH_ON_RENDER;
	}

	/**
	 * @see RowRendererExt#newCell(Row)
	 */
	@Override
	public Component newCell(Row row) {
		return null;
	}

	/**
	 * @see RowRendererExt#newRow(Grid)
	 */
	@Override
	public Row newRow(Grid grid) {
		return null;
	}

	/**
	 * @see RendererCtrl#doCatch(Throwable)
	 */
	@Override
	public void doCatch(Throwable ex) throws Throwable {

	}

	/**
	 * @see RendererCtrl#doFinally()
	 */
	@Override
	public void doFinally() {

	}

	/**
	 * @see RendererCtrl#doTry()
	 */
	@Override
	public void doTry() {

	}

	/**
	 * set focus to first active editor
	 */
	public void focusToFirstEditor() {
//		if (currentRow != null && currentRow.getParent() != null) {
//			WEditor toFocus = null;
//			WEditor firstEditor = null;
//			if (defaultFocusField != null
//					&& defaultFocusField.isVisible() && defaultFocusField.isReadWrite() && defaultFocusField.getComponent().getParent() != null
//					&& !(defaultFocusField instanceof WImageEditor)) {
//				toFocus = defaultFocusField;
//			}
//			else
//			{
//				for (WEditor editor : getEditors()) {
//					if (editor.isVisible() && editor.getComponent().getParent() != null) {
//						if (editor.isReadWrite()) {
//							toFocus = editor;
//							break;
//						}
//						if (firstEditor == null)
//							firstEditor = editor;
//					}
//				}
//			}
//			if (toFocus != null) {
//				focusToEditor(toFocus);
//			} else if (firstEditor != null) {
//				focusToEditor(firstEditor);
//			}
//		}
	}

	protected void focusToEditor(WEditor toFocus) {
//		Component c = toFocus.getComponent();
//		if (c instanceof EditorBox) {
//			c = ((EditorBox)c).getTextbox();
//		} else if (c instanceof NumberBox) {
//			c = ((NumberBox)c).getDecimalbox();
//		} else if (c instanceof Urlbox) {
//			c = ((Urlbox) c).getTextbox();
//		}
//		((HtmlBasedComponent)c).focus();
	}

	/**
	 * set focus to next readwrite editor from ref
	 * @param ref
	 */
	public void focusToNextEditor(WEditor ref) {
		boolean found = false;
		for (WEditor editor : getEditors()) {
			if (editor == ref) {
				found = true;
				continue;
			}
			if (found) {
				if (editor.isVisible() && editor.isReadWrite()) {
					focusToEditor(editor);
					break;
				}
			}
		}
	}

	/**
	 *
	 * @param gridPanel
	 */
	public void setGridPanel(GridView gridPanel) {
//		this.gridPanel = gridPanel;
	}

	/**
	 * RowListener
	 *
	 * In case you want to set a row event
	 *
	 */
	static class RowListener implements EventListener<Event> {

		private Grid _grid;

		private int rowIndex = 0;
		private int columnIndex = 0;

		public RowListener(Grid grid) {
			_grid = grid;
		}

		public int getRowIndex()
		{
			return rowIndex;
		}

		public int getColumnIndex()
		{
			return columnIndex;
		}

		public void onEvent(Event event) throws Exception {

			if(event.getTarget() instanceof Cell)//Get Row Index
			{
				String[] yx = ((Cell)event.getTarget()).getId().split("_");
				rowIndex =Integer.valueOf(yx[0]).intValue();
	            columnIndex =Integer.valueOf(yx[1]).intValue();
			}

			if (Events.ON_CLICK.equals(event.getName())) {
				if (Executions.getCurrent().getAttribute("gridView.onSelectRow") != null)
					return;
				Event evt = new Event(Events.ON_CLICK, _grid, event.getTarget());
				Events.sendEvent(_grid, evt);
				evt.stopPropagation();
			}
			else if (Events.ON_DOUBLE_CLICK.equals(event.getName())) {
				Event evt = new Event(Events.ON_DOUBLE_CLICK, _grid, _grid);
				Events.sendEvent(_grid, evt);
			}
			else if (Events.ON_OK.equals(event.getName())) {
				Event evt = new Event(Events.ON_OK, _grid, _grid);
				Events.sendEvent(_grid, evt);
			}
		}
	}




	/**
	 * @return boolean
	 */
	public boolean isEditing() {
		return editing;
	}



	/**
	 *
	 * setADWindowPanel Method
	 *
	 * Need to Create Process Dialog
	 *
	 */
	public void setADWindowPanel(AbstractADWindowContent windowPanel,IADTabpanel adTabpanel) {
		if (this.m_windowPanel == windowPanel)
			return;

		this.m_windowPanel = windowPanel;

		buttonListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				WButtonEditor editor = (WButtonEditor) event.getSource();
				Integer rowIndex = (Integer) editor.getComponent().getAttribute(GRID_ROW_INDEX_ATTR);
				if (rowIndex != null) {
					int newRowIndex = gridTab.navigate(rowIndex);
					if (newRowIndex == rowIndex) {
						m_windowPanel.actionPerformed(event);
					}
				} else {
					m_windowPanel.actionPerformed(event);
				}
			}
		};
	}



	private int minRowIndex = 0;
	private int maxRowIndex = 0;

	/**
	 * Enter Key Event(onOK)
	 */
	@Override
	public void onEvent(Event event) throws Exception {

		if (event.getTarget() instanceof Cell) {
			Cell cell = (Cell) event.getTarget();
			if (cell.getSclass() != null && cell.getSclass().indexOf("row-indicator-selected") >= 0)
//				Events.sendEvent(gridPanel, new Event(DetailPane.ON_EDIT_EVENT, gridPanel));
				;
			else
				Events.sendEvent(event.getTarget().getParent(), event);
		} else if (event.getTarget() instanceof Checkbox) {
			Executions.getCurrent().setAttribute("gridView.onSelectRow", Boolean.TRUE);
			Checkbox checkBox = (Checkbox) event.getTarget();
			try {
				simpleInputWindow.onEvent(new Event("onSelectRow",null,checkBox));
			} catch (Exception e) {
				;
			}
//			Events.sendEvent(gridPanel, new Event("onSelectRow", gridPanel, checkBox));
		}else if(event.getName().equals(Events.ON_OK)){

			List<Row> rowList = grid.getRows().getChildren();
			minRowIndex = grid.getActivePage() * grid.getPageSize();
			maxRowIndex = minRowIndex + grid.getPageSize();

			boolean isLastPage =  maxRowIndex >= rowList.size() ? true : false;
			maxRowIndex = maxRowIndex > rowList.size() ? rowList.size() : maxRowIndex;

			if(event.getTarget().getParent() instanceof Searchbox)
			{

				Searchbox searchBox =(Searchbox)event.getTarget().getParent();
				if(searchBox.getText().equals(""))
				{
					;//If you push Enter key at Blank Search field, iDempiere dispay Info Window. So, stay same row.
				}else{
					currentRowIndex++;
				}

			}else{
				currentRowIndex++;
			}

			if(maxRowIndex <= currentRowIndex)
			{
				if(isLastPage)
				{
					//TODO:新規レコード追加?
					currentRowIndex = minRowIndex;
				}else{
					currentRowIndex = minRowIndex;
				}
			}
			currentRow = rowList.get(currentRowIndex);
			currentPO =listModel.getPO(currentRowIndex);

			stopEditing(true);
			editCurrentRow();

			event.stopPropagation();

			String script = "jq('#"+currentRow.getUuid()+"').addClass('highlight').siblings().removeClass('highlight')";

			Boolean isActive = null;
			Object isActiveValue = gridTab.getValue(currentRowIndex, "IsActive");
			if (isActiveValue != null) {
				if ("true".equalsIgnoreCase(isActiveValue.toString())) {
					isActive = Boolean.TRUE;
				} else {
					isActive = Boolean.FALSE;
				}
			}
			if (isActive != null && !isActive.booleanValue()) {
				script = "jq('#"+currentRow.getUuid()+"').addClass('grid-inactive-row').siblings().removeClass('highlight')";
			}

			Clients.response(new AuScript(script));

			return;
		}
	}//onEvent

	public void setGridView(GridView gridView)
	{
		this.gridView = gridView;
	}

	public void setSimpleInputWindow(JPiereSimpleInputWindow simpleInputWindow)
	{
		this.simpleInputWindow = simpleInputWindow;
	}

	public void setGridTab(GridTab gridTab)
	{
		this.gridTab = gridTab;
	}

}
