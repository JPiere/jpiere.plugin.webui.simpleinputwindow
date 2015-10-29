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

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.table.AbstractTableModel;

import jpiere.plugin.simpleinputwindow.model.MSimpleInputField;
import jpiere.plugin.simpleinputwindow.model.MSimpleInputSearch;
import jpiere.plugin.simpleinputwindow.model.MSimpleInputWindow;
import jpiere.plugin.simpleinputwindow.window.SimpleInputWindowCustomizeGridViewDialog;

import org.adempiere.base.IModelFactory;
import org.adempiere.base.Service;
import org.adempiere.model.MTabCustomization;
import org.adempiere.webui.adwindow.GridTabRowRenderer;
import org.adempiere.webui.adwindow.GridView;
import org.adempiere.webui.adwindow.ToolbarProcessButton;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Columns;
import org.adempiere.webui.component.EditorBox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.NumberBox;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.editor.IZoomableEditor;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WEditorPopupMenu;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.editor.WYesNoEditor;
import org.adempiere.webui.editor.WebEditorFactory;
import org.adempiere.webui.event.ActionEvent;
import org.adempiere.webui.event.ActionListener;
import org.adempiere.webui.event.ContextMenuListener;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.GridTable;
import org.compiere.model.GridWindow;
import org.compiere.model.GridWindowVO;
import org.compiere.model.MColumn;
import org.compiere.model.MField;
import org.compiere.model.MLookup;
import org.compiere.model.MRole;
import org.compiere.model.MTab;
import org.compiere.model.MTable;
import org.compiere.model.MToolBarButton;
import org.compiere.model.MToolBarButtonRestrict;
import org.compiere.model.PO;
import org.compiere.model.X_AD_ToolBarButton;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.util.TrxRunnable;
import org.zkoss.zk.au.out.AuFocus;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Center;
import org.zkoss.zul.Column;
import org.zkoss.zul.Frozen;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.North;
import org.zkoss.zul.Space;
import org.zkoss.zul.impl.XulElement;

/**
 * JPiereMatrixWindow
 *
 * JPIERE-0111
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class JPiereSimpleInputWindow extends AbstractSimpleInputWindowForm implements EventListener<Event>, ValueChangeListener,WTableModelListener,ActionListener{

	/**	Logger			*/
	private  static CLogger log = CLogger.getCLogger(JPiereSimpleInputWindow.class);

	private CustomForm form = new CustomForm();


	/**********************************************************************
	 * UI Component
	 **********************************************************************/

	private Borderlayout mainLayout = new Borderlayout();

	private Panel parameterPanel = new Panel();						//Set field of search condition
	private Grid parameterLayout = GridFactory.newGridLayout();

	//Display Data
	private Panel displayDataPanel = new Panel();
	private Borderlayout displayDataLayout = new Borderlayout();

	protected Checkbox selectAll;

	private GridField[] gridFields;
	private AbstractTableModel tableModel;
	private int numColumns = 5;

	private String whereClause ;

	private StringBuilder message = new StringBuilder();

	private PO[] m_POs;

	//Map of PO Instance that have to save.<ID of PO,PO>
	private HashMap<Integer,PO>  dirtyModel  = new HashMap<Integer,PO>();

	/**********************************************************************
	 * Parameter of Application Dictionary(System Client)
	 **********************************************************************/

	//Model
	private MSimpleInputWindow 	 m_simpleInputWindow;
	private MSimpleInputField[]  m_simpleInputFields ;
	private MSimpleInputSearch[] m_simpleInputSearches ;

	private MTab			m_Tab;
	private MField[]		m_contentFields;
	private MColumn[]		m_contentColumns;

	//Search Field Editor Map
	private HashMap<String,WEditor> searchEditorMap = new HashMap<String,WEditor> ();

	//AD_Window_ID
	private int AD_WINDOW_ID = 0;

	//Table Name
	private String TABLE_NAME ;

	/****************************************************
	 * Window Info
	 ****************************************************/
	private GridTab gridTab ;
	private GridView gridView ;

	private Grid simpleInputGrid  = new Grid();			//main component

	private Button SearchButton;

	private Button SaveButton;

	private Button CreateButton;

	private Button ProcessButton;

	private Button CustomizeButton;

	private Button DeleteButton;

	private SimpleInputWindowGridRowRenderer renderer;

	private SimpleInputWindowListModel listModel;

	boolean isHasCustomizeData = false;

	private Map<Integer, String> columnWidthMap;

	private static final int MIN_COLUMN_WIDTH = 100;

	private static final int MAX_COLUMN_WIDTH = 300;

	private static final int MIN_COMBOBOX_WIDTH = 160;

	private static final int MIN_NUMERIC_COL_WIDTH = 120;

	/**
	 * Constractor
	 *
	 * @throws IOException
	 */
    public JPiereSimpleInputWindow() throws IOException
    {
    	;
    }

	public ADForm getForm()
	{
		return form;
	}

	@Override
	public void createSimpleInputWindow(String JP_SimpleInputWindow_ID)
	{
    	try
		{
    		prepare(JP_SimpleInputWindow_ID);
    		zkInit();
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "", e);
		}

	}

	private void prepare(String JP_SimpleInputWindow_ID) throws Exception
	{

		//Create Models that is used by Simple Input Window
		m_simpleInputWindow = MSimpleInputWindow.get(Env.getCtx(), JP_SimpleInputWindow_ID);
		if(m_simpleInputWindow == null)
		{
			;//Error
		}

		m_simpleInputFields = m_simpleInputWindow.getSimpleInputFields();
		m_simpleInputSearches = m_simpleInputWindow.getSimpleInputSearches();

		AD_WINDOW_ID = m_simpleInputWindow.getAD_Window_ID();
		m_Tab = new MTab(Env.getCtx(), m_simpleInputWindow.getAD_Tab_ID(), null);
		TABLE_NAME = MTable.get(Env.getCtx(), m_Tab.getAD_Table_ID()).getTableName();

		//Create Window because of use Window info.
		GridWindowVO gridWindowVO =AEnv.getMWindowVO(form.getWindowNo(), m_simpleInputWindow.getAD_Window_ID(), 0);
		GridWindow gridWindow = new GridWindow(gridWindowVO);
		for(int i = 0; i < gridWindow.getTabCount(); i++)
		{
			GridTab gtab =gridWindow.getTab(i);
			if(gtab.getAD_Tab_ID()==m_simpleInputWindow.getAD_Tab_ID())
			{
				gridTab = gtab;
				break;
			}
		}

		gridTab.initTab(false);
		setupFields(gridTab);//Set up display field

	}

	private void zkInit()
	{
		form.appendChild(mainLayout);

		/*Main Layout(Borderlayout)*/
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");

		//Main Layout(Borderlayout)-North
		North north = new North();
		mainLayout.appendChild(north);

		//Search Parameter Panel
		north.appendChild(parameterPanel);
		north.setStyle("border: none");
		parameterPanel.appendChild(parameterLayout); 		//parameterLayout = Grid
		parameterLayout.setWidth("100%");
		Rows parameterLayoutRows = parameterLayout.newRows();
		Row row = null;

		if(m_simpleInputSearches.length > 0)
		{
			row = parameterLayoutRows.newRow();
				Groupbox searchGB = new Groupbox();
				row.appendCellChild(searchGB,8);
				searchGB.appendChild(new Caption(Msg.getMsg(Env.getCtx(), "SearchCriteria")));
				Grid searchGrid  = new Grid();
				searchGrid.setStyle("background-color: #E9F0FF");
				searchGrid.setStyle("border: none");
				searchGB.appendChild(searchGrid);
				Rows rows = searchGrid.newRows();

			int actualxpos = 0;
			//Create Search Fields
			for(int i = 0; i < m_simpleInputSearches.length; i++)
			{
				MSimpleInputSearch searchField = m_simpleInputSearches[i];

				if(i == 0 || actualxpos > searchField.getXPosition())
				{
					actualxpos = 0;
					row = rows.newRow();
					row.setStyle("background-color: #ffffff");
				}

				WEditor editor = null;
				for(int j = 0; j < gridFields.length; j++)
				{
					if(m_simpleInputSearches[i].getAD_Field_ID() == gridFields[j].getAD_Field_ID())
					{
						editor = WebEditorFactory.getEditor(gridFields[j], false);
						break;
					}
				}

				if(editor == null)
				{
					GridField[] gFields = GridField.createFields(Env.getCtx(), form.getWindowNo(), 0, searchField.getAD_Tab_ID());
					for(int k = 0; k < gFields.length; k++)
					{
						if(m_simpleInputSearches[i].getAD_Field_ID() == gFields[k].getAD_Field_ID())
						{
							editor = WebEditorFactory.getEditor(gFields[k], false);
							break;
						}
					}
					;
				}

				if(editor == null)
				{
					;//TODO Error

				}else{
					String DefaultValue = m_simpleInputSearches[i].getDefaultValue();
					if(DefaultValue == null || DefaultValue.isEmpty())
					{
						;
					}else{

						String value = Env.parseContext(Env.getCtx(), form.getWindowNo(), DefaultValue, false);
						Env.setContext(Env.getCtx(), form.getWindowNo(), editor.getColumnName(), value);
						editor.setValue(Env.parseContext(Env.getCtx(), form.getWindowNo(), DefaultValue, false));

						if(editor instanceof WTableDirEditor)
						{
							((WTableDirEditor) editor).actionRefresh();
							((WTableDirEditor) editor).getLookup().setSelectedItem("");
						}

					}

					if(!editor.isReadWrite())
					{
						editor.setReadWrite(true);
						if(editor instanceof WTableDirEditor)
							((WTableDirEditor) editor).actionRefresh();

					}

					//Set zoom
					if(editor instanceof WSearchEditor
							|| editor instanceof WTableDirEditor)
					{
						editor.getLabel().addEventListener(Events.ON_CLICK, new ZoomListener((IZoomableEditor) editor));
						if(m_simpleInputSearches[i].isMandatory() && editor.getValue()==null)
							editor.getLabel().setStyle("cursor: pointer; text-decoration: underline;color: #333; color:red;");
						else
							editor.getLabel().setStyle("cursor: pointer; text-decoration: underline;color: #333;");
					}

					editor.setMandatory(m_simpleInputSearches[i].isMandatory());

					//positioning
					row.appendCellChild(editor.getLabel().rightAlign(),1);
					actualxpos = actualxpos + 1;
					row.appendCellChild(editor.getComponent(),searchField.getColumnSpan());
					actualxpos = actualxpos + searchField.getColumnSpan();

					//Popup Menu
					WEditorPopupMenu  popupMenu = editor.getPopupMenu();
					List<Component> listcomp = popupMenu.getChildren();
					Menuitem menuItem = null;
					String image = null;
					for(Component comp : listcomp)
					{
						if(comp instanceof Menuitem)
						{
							menuItem = (Menuitem)comp;
							image = menuItem.getImage();
							if(image.endsWith("Zoom16.png")||image.endsWith("Refresh16.png")
									|| image.endsWith("New16.png") || image.endsWith("InfoBPartner16.png"))
							{
								menuItem.setVisible(true);
							}else{
								menuItem.setVisible(false);
							}
						}
					}//for

		            if (popupMenu != null)
		            {
		            	popupMenu.addMenuListener((ContextMenuListener)editor);
		            	row.appendChild(popupMenu);

		            	popupMenu.addContextElement((XulElement) editor.getComponent());
		            }

					editor.addValueChangeListener(this);
					searchEditorMap.put(editor.getColumnName(), editor);

				}//if(editor == null)

			}//for i : Create Search Fields

			//Dynamic Validation
			for(Map.Entry<String, WEditor> entry: searchEditorMap.entrySet())
			{
				WEditor otherEditor = entry.getValue();
				GridField gridField = otherEditor.getGridField();

				if(otherEditor instanceof WTableDirEditor || otherEditor instanceof WSearchEditor )
				{

					if(gridField.getVFormat() != null && gridField.getVFormat().indexOf('@') != -1)
					{
						String validated = Env.parseContext(Env.getCtx(), form.getWindowNo(), gridField.getVFormat(), false);
						((MLookup)gridField.getLookup()).getLookupInfo().ValidationCode=validated;

					}else if(gridField.getLookup().getValidation().indexOf('@') != -1){

						gridField.setVFormat(gridField.getLookup().getValidation());
						String validated = Env.parseContext(Env.getCtx(), form.getWindowNo(), gridField.getVFormat(), false);
						((MLookup)gridField.getLookup()).getLookupInfo().ValidationCode=validated;

					}

					if(otherEditor instanceof WTableDirEditor)
						((WTableDirEditor)otherEditor).getLookup().refresh();

				}//if

			}//for Dynamic Validation

		}//if(m_simpleInputSearches.length > 0)

		//Create Button
		row = parameterLayoutRows.newRow();
				SearchButton = new Button(Msg.getMsg(Env.getCtx(), "search"));
				SearchButton.setId("SearchButton");
				SearchButton.addActionListener(this);
				SearchButton.setEnabled(true);
				SearchButton.setImage(ThemeManager.getThemeResource("images/Find16.png"));
				row.appendCellChild(SearchButton);



				SaveButton = new Button(Msg.getMsg(Env.getCtx(), "save"));
				SaveButton.setId("SaveButton");
				SaveButton.addActionListener(this);
				SaveButton.setEnabled(false);
				SaveButton.setImage(ThemeManager.getThemeResource("images/Save16.png"));

				row.appendCellChild(SaveButton);

				CreateButton = new Button(Msg.getMsg(Env.getCtx(), "NewRecord"));
				CreateButton.setId("CreateButton");
				CreateButton.addActionListener(this);
				CreateButton.setEnabled(false);
				CreateButton.setImage(ThemeManager.getThemeResource("images/New16.png"));
//				if(m_simpleInputWindow.getJP_QuickEntryWindow_ID() > 0)
//				{
//					row.appendCellChild(CreateButton);
//				}

				loadToolbarButtons();
				ProcessButton = new Button(Msg.getMsg(Env.getCtx(), "Process"));
				ProcessButton.setId("ProcessButton");
				ProcessButton.addActionListener(this);
				ProcessButton.setEnabled(false);
				ProcessButton.setImage(ThemeManager.getThemeResource("images/Process16.png"));
				if(toolbarProcessButtons.size()> 0 )
					row.appendCellChild(ProcessButton);

				CustomizeButton = new Button(Msg.getMsg(Env.getCtx(), "Customize"));
				CustomizeButton.setId("CustomizeButton");
				CustomizeButton.addActionListener(this);
				CustomizeButton.setEnabled(false);
				CustomizeButton.setImage(ThemeManager.getThemeResource("images/Customize16.png"));

				row.appendCellChild(CustomizeButton);

				DeleteButton = new Button(Msg.getMsg(Env.getCtx(), "Delete"));
				DeleteButton.setId("DeleteButton");
				DeleteButton.addActionListener(this);
				DeleteButton.setEnabled(false);
				DeleteButton.setImage(ThemeManager.getThemeResource("images/Delete16.png"));

				row.appendCellChild(DeleteButton);

		//for space under Button
		row = parameterLayoutRows.newRow();
				row.appendCellChild(new Space(),1);

		//Edit Area
		Center center = new Center();
		mainLayout.appendChild(center);
		center.appendChild(displayDataPanel);
		displayDataPanel.appendChild(displayDataLayout);//Borderlayout
		displayDataPanel.setWidth("100%");
		displayDataPanel.setHeight("100%");
		displayDataPanel.setHflex("1");
		displayDataPanel.setVflex("1");
		displayDataLayout.setWidth("100%");
		displayDataLayout.setHeight("100%");
		displayDataLayout.setStyle("border: none");

				//Contents
				center = new Center();
				displayDataLayout.appendChild(center);
				center.appendChild(simpleInputGrid);
				center.setStyle("border: none");
				simpleInputGrid.setWidth("100%");
				simpleInputGrid.setHeight("100%");
				simpleInputGrid.setVflex(true);
				simpleInputGrid.setVisible(false);
				simpleInputGrid.setMold("paging");
				simpleInputGrid.setPageSize(m_simpleInputWindow.getJP_PageSize());


	}

	static class ZoomListener implements EventListener<Event>
	{

		private IZoomableEditor searchEditor;

		ZoomListener(IZoomableEditor editor) {
			searchEditor = editor;
		}

		public void onEvent(Event event) throws Exception {
			if (Events.ON_CLICK.equals(event.getName())) {
				searchEditor.actionZoom();
			}

		}

	}

	private ArrayList<ToolbarProcessButton> toolbarProcessButtons = new ArrayList<ToolbarProcessButton>();

	private void loadToolbarButtons() {
		//get extra toolbar process buttons
        MToolBarButton[] mToolbarButtons = MToolBarButton.getProcessButtonOfTab(gridTab.getAD_Tab_ID(), null);
        for(MToolBarButton mToolbarButton : mToolbarButtons) {
        	Boolean access = MRole.getDefault().getProcessAccess(mToolbarButton.getAD_Process_ID());
        	if (access != null && access.booleanValue()) {
        		ToolbarProcessButton toolbarProcessButton = new ToolbarProcessButton(mToolbarButton, null, this, form.getWindowNo());
        		toolbarProcessButtons.add(toolbarProcessButton);
        	}
        }

        if (toolbarProcessButtons.size() > 0) {
        	int ids[] = MToolBarButtonRestrict.getProcessButtonOfTab(Env.getCtx(), Env.getAD_Role_ID(Env.getCtx()), gridTab.getAD_Tab_ID(), null);
        	if (ids != null && ids.length > 0) {
        		for(int id : ids) {
        			X_AD_ToolBarButton tbt = new X_AD_ToolBarButton(Env.getCtx(), id, null);
        			for(ToolbarProcessButton btn : toolbarProcessButtons) {
        				if (tbt.getComponentName().equals(btn.getColumnName())) {
        					toolbarProcessButtons.remove(btn);
        					break;
        				}
        			}
        		}
        	}
        }
	}

	//TODO
	private boolean createView () throws Exception {

		simpleInputGrid.setVisible(true);

		//Create String where clause
		whereClause = createWhere();
		if(message.length() > 0)
			return false;

		//Create array of PO from where clause
		m_POs = getPOs(whereClause,true);
		if(m_POs.length==0)
		{
			message.append(System.getProperty("line.separator") + Msg.getMsg(Env.getCtx(), "not.found"));
			return false;
		}

		org.zkoss.zul.Columns columns = simpleInputGrid.getColumns();
		if(columns == null)
		{
			setupColumns();

			Frozen frozen = new Frozen();
			frozen.setColumns(m_simpleInputWindow.getJP_FrozenField()+2);//freeze selection and indicator column
			simpleInputGrid.appendChild(frozen);
		}

		simpleInputWindowGridTable = createTableModel(m_POs);

		listModel = new SimpleInputWindowListModel(simpleInputWindowGridTable, form.getWindowNo());
		simpleInputGrid.setModel(listModel);

		renderer = new SimpleInputWindowGridRowRenderer(gridTab, form, listModel, dirtyModel);
		renderer.setGridView(gridView);
		renderer.setSimpleInputWindow(this);
		renderer.setGridTab(gridTab);

		simpleInputGrid.setRowRenderer(renderer);
		simpleInputGrid.addEventListener(Events.ON_CLICK, this);

		return true;
	}

	SimpleInputWindowGridTable simpleInputWindowGridTable ;

	/*
	 * Map of PO Instance <ID of PO,PO>
	 *
	 *
	 */
	private SimpleInputWindowGridTable createTableModel(PO[] POs)
	{

		simpleInputWindowGridTable = new SimpleInputWindowGridTable();
		simpleInputWindowGridTable.init(POs,gridFields);

		return simpleInputWindowGridTable;
	}

	private boolean init;

	private void setupFields(GridTab gridTab) {
		this.gridTab = gridTab;
//		gridTab.addStateChangeListener(this);

		tableModel = gridTab.getTableModel();
		columnWidthMap = new HashMap<Integer, String>();
		GridField[] tmpFields = ((GridTable)tableModel).getFields();
		MTabCustomization tabCustomization = MTabCustomization.get(Env.getCtx(), Env.getAD_User_ID(Env.getCtx()), gridTab.getAD_Tab_ID(), null);
		isHasCustomizeData = tabCustomization != null && tabCustomization.getAD_Tab_Customization_ID() > 0
				&& tabCustomization.getCustom() != null && tabCustomization.getCustom().trim().length() > 0;
		if (isHasCustomizeData) {
			String custom = tabCustomization.getCustom().trim();
			String[] customComponent = custom.split(";");
			String[] fieldIds = customComponent[0].split("[,]");
			List<GridField> fieldList = new ArrayList<GridField>();
			for(String fieldIdStr : fieldIds) {
				fieldIdStr = fieldIdStr.trim();
				if (fieldIdStr.length() == 0) continue;
				int AD_Field_ID = Integer.parseInt(fieldIdStr);
				for(GridField gridField : tmpFields) {
					if (gridField.getAD_Field_ID() == AD_Field_ID) {
						// IDEMPIERE-2204 add field in tabCustomization list to display list event this field have showInGrid = false
						if((gridField.isDisplayedGrid() || gridField.isDisplayed()) && !gridField.isToolbarOnlyButton())
							fieldList.add(gridField);

						break;
					}
				}
			}
			gridFields = fieldList.toArray(new GridField[0]);
			if (customComponent.length == 2) {
				String[] widths = customComponent[1].split("[,]");
				for(int i = 0; i< gridFields.length && i<widths.length; i++) {
					columnWidthMap.put(gridFields[i].getAD_Field_ID(), widths[i]);
				}
			}
		} else {
			ArrayList<GridField> gridFieldList = new ArrayList<GridField>();

			for(GridField field:tmpFields){
				if(field.isDisplayedGrid() && !field.isToolbarOnlyButton()) {
					gridFieldList.add(field);
				}
			}

			Collections.sort(gridFieldList, new Comparator<GridField>() {
				@Override
				public int compare(GridField o1, GridField o2) {
					return o1.getSeqNoGrid()-o2.getSeqNoGrid();
				}
			});

			gridFields = new GridField[gridFieldList.size()];
			gridFieldList.toArray(gridFields);
		}
		numColumns = gridFields.length;
	}

	/**
	 * list field display in grid mode, in case user customize grid
	 * this list container only customize list.
	 */
	public GridField[] getFields() {
		return gridFields;
	}

	private void setupColumns()
	{
		if (init) return;

		Columns columns = new Columns();

		org.zkoss.zul.Column selection = new Column();
		selection.setWidth("22px");
		try{
			selection.setSort("none");
		} catch (Exception e) {}
		selection.setStyle("border-right: none");
		selectAll = new Checkbox();
		selection.appendChild(selectAll);
		selectAll.setId("selectAll");
		selectAll.addEventListener(Events.ON_CHECK, this);
		columns.appendChild(selection);

		org.zkoss.zul.Column indicator = new Column();
		indicator.setWidth("22px");
		try {
			indicator.setSort("none");
		} catch (Exception e) {}
		indicator.setStyle("border-left: none");
		columns.appendChild(indicator);
		simpleInputGrid.appendChild(columns);
		columns.setSizable(true);
		columns.setMenupopup("none");
		columns.setColumnsgroup(false);

		Map<Integer, String> colnames = new HashMap<Integer, String>();
		int index = 0;

		for (int i = 0; i < numColumns; i++)
		{
			// IDEMPIERE-2148: when has tab customize, ignore check properties isDisplayedGrid
			if ((isHasCustomizeData || gridFields[i].isDisplayedGrid()) && !gridFields[i].isToolbarOnlyButton())
			{
				colnames.put(index, gridFields[i].getHeader());
				index++;
				org.zkoss.zul.Column column = new Column();
				//TODO:ソート処理の実装
//				int colindex =tableModel.findColumn(gridFields[i].getColumnName());
//				column.setSortAscending(new SortComparator(i, true, Env.getLanguage(Env.getCtx())));
//				column.setSortDescending(new SortComparator(i, false, Env.getLanguage(Env.getCtx())));
				column.setLabel(gridFields[i].getHeader());
				if (columnWidthMap != null && columnWidthMap.get(gridFields[i].getAD_Field_ID()) != null && !columnWidthMap.get(gridFields[i].getAD_Field_ID()).equals("")) {
					column.setWidth(columnWidthMap.get(gridFields[i].getAD_Field_ID()));
				} else {
					if (gridFields[i].getDisplayType()==DisplayType.YesNo) {
						if (i > 0) {
							column.setHflex("min");
						} else {
							int estimatedWidth=60;
							int headerWidth = (gridFields[i].getHeader().length()+2) * 8;
							if (headerWidth > estimatedWidth)
								estimatedWidth = headerWidth;
							column.setWidth(estimatedWidth+"px");
						}
					} else if (DisplayType.isNumeric(gridFields[i].getDisplayType()) && "Line".equals(gridFields[i].getColumnName())) {
						//special treatment for line
						if (i > 0)
							column.setHflex("min");
						else
							column.setWidth("60px");
					} else {
						int estimatedWidth = 0;
						if (DisplayType.isNumeric(gridFields[i].getDisplayType()))
							estimatedWidth = MIN_NUMERIC_COL_WIDTH;
						else if (DisplayType.isLookup(gridFields[i].getDisplayType()))
							estimatedWidth = MIN_COMBOBOX_WIDTH;
						else if (DisplayType.isText(gridFields[i].getDisplayType()))
							estimatedWidth = gridFields[i].getDisplayLength() * 8;
						else
							estimatedWidth = MIN_COLUMN_WIDTH;

						int headerWidth = (gridFields[i].getHeader().length()+2) * 8;
						if (headerWidth > estimatedWidth)
							estimatedWidth = headerWidth;

						//hflex=min for first column not working well
						if (i > 0)
						{
							if (DisplayType.isLookup(gridFields[i].getDisplayType()))
							{
								if (headerWidth > MIN_COMBOBOX_WIDTH)
									column.setHflex("min");
							}
							else if (DisplayType.isNumeric(gridFields[i].getDisplayType()))
							{
								if (headerWidth > MIN_NUMERIC_COL_WIDTH)
									column.setHflex("min");
							}
							else if (!DisplayType.isText(gridFields[i].getDisplayType()))
							{
								if (headerWidth > MIN_COLUMN_WIDTH)
									column.setHflex("min");
							}
						}

						//set estimated width if not using hflex=min
						if (!"min".equals(column.getHflex())) {
							if (estimatedWidth > MAX_COLUMN_WIDTH)
								estimatedWidth = MAX_COLUMN_WIDTH;
							else if ( estimatedWidth < MIN_COLUMN_WIDTH)
								estimatedWidth = MIN_COLUMN_WIDTH;
							column.setWidth(Integer.toString(estimatedWidth) + "px");
						}
					}
				}
				columns.appendChild(column);
			}
		}
	}


	private String createWhere()
	{
		StringBuilder whereClause = new StringBuilder(" WHERE "+ TABLE_NAME+".AD_Client_ID = "+ Env.getAD_Client_ID(Env.getCtx()));

		for(Map.Entry<String, WEditor> entry: searchEditorMap.entrySet())
		{
			if(entry.getValue().getValue()!=null)
			{

				String tableName = null;
				GridField gField = ((WEditor)entry.getValue()).getGridField();
				GridTab gTab = gField.getGridTab();
				if(gTab != null)
				{
					tableName = gTab.getTableName();
				}else{
					int AD_Tab_ID = gField.getAD_Tab_ID();
					MTab tab = new MTab(Env.getCtx(),AD_Tab_ID,null);
					tableName = tab.getAD_Table().getTableName();
				}

				if(entry.getValue() instanceof WYesNoEditor)
				{
					if(entry.getValue().getValue().equals(true))
						whereClause.append(" AND "+ tableName+"."+ entry.getKey() + " = " + "'Y'");
					else
						whereClause.append(" AND "+ tableName+"."+ entry.getKey() + " = " + "'N'");

				}else if(entry.getValue().getGridField().getDisplayType()==DisplayType.List){

					whereClause.append(" AND "+ tableName+"."+ entry.getKey() + " = " + "'" + entry.getValue().getValue() + "'");

				}else if(DisplayType.isText(entry.getValue().getGridField().getDisplayType())){
					String string = (String)entry.getValue().getValue();
					if(!string.isEmpty())
					{
						whereClause.append(" AND "+ tableName+"."+ entry.getKey() + " LIKE " + "'" + string + "'");
					}

				}else if(DisplayType.isDate(entry.getValue().getGridField().getDisplayType())){

					Timestamp timestamp = (Timestamp)entry.getValue().getValue();
					whereClause.append(" AND "+ tableName+"."+ entry.getKey() + "=" +"TO_DATE('"+ timestamp.toString() +"','YYYY-MM-DD HH24:MI:SS')");

//					if(entry.getValue().getGridField().getDisplayType()==DisplayType.Date)
//					{
//						whereClause.append(" AND "+ tableName+"."+ entry.getKey() + "=" +"TO_DATE('"+ timestamp.toString() +"','YYYY-MM-DD HH24:MI:SS')");
//
//					}else if(entry.getValue().getGridField().getDisplayType()==DisplayType.DateTime){
//
//						whereClause.append(" AND "+ tableName+"."+ entry.getKey() + "=" +"TO_DATE('"+ timestamp.toString() +"','YYYY-MM-DD HH24:MI:SS')");
//
//					}else if(entry.getValue().getGridField().getDisplayType()==DisplayType.Time){
//
//						;
//					}

				}else{

					whereClause.append(" AND "+ tableName+"."+ entry.getKey() + " = " + entry.getValue().getValue());

				}

			}else{

				if(entry.getValue().isMandatory())
				{
					message.append(System.getProperty("line.separator") + Msg.getMsg(Env.getCtx(), "FillMandatory") + entry.getValue().getLabel().getValue() );
				}
			}
		}//for

		if(m_simpleInputWindow.getWhereClause() != null)
		{
			whereClause.append(" AND " + m_simpleInputWindow.getWhereClause() );
		}

		return whereClause.toString();
	}

	private PO[] getPOs (String whereClause,boolean reload)
	{
		if (reload || m_POs == null || m_POs.length == 0)
			;
		else
			return m_POs;
		//
		ArrayList<PO> list = new ArrayList<PO>();

		StringBuilder sql = new StringBuilder("SELECT " + TABLE_NAME+".* FROM " + TABLE_NAME );
		if(m_simpleInputWindow.getJP_JoinClause() != null)
		{
			sql.append(" "+ m_simpleInputWindow.getJP_JoinClause());
		}

		sql.append(whereClause);

		if(m_simpleInputWindow.getOrderByClause() != null)
		{
			sql.append(" ORDER BY "+ m_simpleInputWindow.getOrderByClause());
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), null);
			rs = pstmt.executeQuery();

			List<IModelFactory> factoryList = Service.locator().list(IModelFactory.class).getServices();
			if (factoryList == null)
			{
				;//
			}
			PO po = null;
			while (rs.next())
			{
				for(IModelFactory factory : factoryList) {
					po = factory.getPO(TABLE_NAME, rs, null);//
					if (po != null)
					{
						list.add(po);
						break;
					}
				}//for
			}//while

		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		m_POs = new PO[list.size()];
		list.toArray(m_POs);
		return m_POs;
	}	//	getPOs


	@Override
	public void actionPerformed(ActionEvent event) {

	}

	@Override
	public void tableChanged(WTableModelEvent event) {

	}

	@Override
	public void valueChange(ValueChangeEvent e) {

		WEditor editor = searchEditorMap.get(e.getPropertyName());

		editor.setValue(e.getNewValue());

		if(editor instanceof WYesNoEditor)
		{
			Env.setContext(Env.getCtx(), form.getWindowNo(), editor.getColumnName(), e.getNewValue().equals("true") ? "Y" : "N");
		}else{
			Env.setContext(Env.getCtx(), form.getWindowNo(), editor.getColumnName(), e.getNewValue()==null ? null : e.getNewValue().toString());
		}

		SearchButton.setEnabled(true);
		SaveButton.setEnabled(false);
		CreateButton.setEnabled(false);
		ProcessButton.setEnabled(false);

//		quickEntry = null;

		simpleInputGrid.setVisible(false);
		CustomizeButton.setEnabled(false);
		DeleteButton.setEnabled(false);

		if(e.getNewValue()==null && editor.isMandatory())
		{
			editor.getLabel().setStyle("cursor: pointer; text-decoration: underline;color: #333; color:red;");
		}else{
			editor.getLabel().setStyle("cursor: pointer; text-decoration: underline;color: #333; ");
		}


		//Dynamic Validation
		for(Map.Entry<String, WEditor> entry: searchEditorMap.entrySet())
		{
			WEditor otherEditor = entry.getValue();
			GridField gridField = otherEditor.getGridField();

			if(otherEditor.getColumnName().equals(editor.getColumnName()))
			{
				;
			}else if(otherEditor instanceof WTableDirEditor || otherEditor instanceof WSearchEditor ){

				if(gridField.getVFormat() != null && gridField.getVFormat().indexOf('@') != -1)
				{
					String validated = Env.parseContext(Env.getCtx(), form.getWindowNo(), gridField.getVFormat(), false);
					((MLookup)gridField.getLookup()).getLookupInfo().ValidationCode=validated;

				}else if(gridField.getLookup().getValidation().indexOf('@') != -1){

					gridField.setVFormat(gridField.getLookup().getValidation());
					String validated = Env.parseContext(Env.getCtx(), form.getWindowNo(), gridField.getVFormat(), false);
					((MLookup)gridField.getLookup()).getLookupInfo().ValidationCode=validated;

				}

				if(otherEditor instanceof WTableDirEditor)
					((WTableDirEditor)otherEditor).getLookup().refresh();

			}//if

		}//for
	}

	@Override
	public void onEvent(Event event) throws Exception {

		if (event == null)
		{
			return;
		}
		else if (event.getTarget() == simpleInputGrid && Events.ON_CLICK.equals(event.getName()))
		{

			Object data = event.getData();
			org.zkoss.zul.Row row = null;
			if (data != null && data instanceof Component)
			{
				AbstractComponent cmp = (AbstractComponent) data;
				if (cmp.getParent() instanceof org.zkoss.zul.Row)
				{
					row = (org.zkoss.zul.Row) cmp.getParent();
				}
			}

			if (row != null)
			{
				renderer.setCurrentRow(renderer.getCurrentRow());
				renderer.editCurrentRow();
			}
			event.stopPropagation();

		}else if (event.getName().equals("onSelectRow")){

			Checkbox checkbox = (Checkbox) event.getData();
			int rowIndex = (Integer) checkbox.getAttribute(GridTabRowRenderer.GRID_ROW_INDEX_ATTR);
			if (checkbox.isChecked())
			{
				gridTab.addToSelection(rowIndex);
				if (!selectAll.isChecked() && isAllSelected())
				{
					selectAll.setChecked(true);
				}
			}
			else
			{
				gridTab.removeFromSelection(rowIndex);
				if (selectAll.isChecked())
					selectAll.setChecked(false);
			}
		}

		else if (event.getName().equals("onCustomizeGrid")){

			setupFields(gridTab);

			org.zkoss.zul.Columns columns = simpleInputGrid.getColumns();
			if(columns != null)
			{
				columns.detach();
				setupColumns();

			}

			if(!createView ())
			{
				SearchButton.setEnabled(true);
				SaveButton.setEnabled(false);
				CreateButton.setEnabled(true);
				ProcessButton.setEnabled(true);
				simpleInputGrid.setVisible(false);
				CustomizeButton.setEnabled(false);
				DeleteButton.setEnabled(false);
				return;
			}


		}else if(event.getTarget().equals(CustomizeButton)){

			org.zkoss.zul.Columns columns = simpleInputGrid.getColumns();
			List<Component> columnList = columns.getChildren();
			Map<Integer, String> columnsWidth = new HashMap<Integer, String>();
			ArrayList<Integer> gridFieldIds = new ArrayList<Integer>();
			for (int i = 0; i < gridFields.length; i++) {
				// 2 is offset of num of column in grid view and actual data fields.
				// in grid view, add two function column, indicator column and selection (checkbox) column
				// @see GridView#setupColumns
				Column column = (Column) columnList.get(i+2);
				String width = column.getWidth();
				columnsWidth.put(gridFields[i].getAD_Field_ID(), width);
				gridFieldIds.add(gridFields[i].getAD_Field_ID());

			}

			SimpleInputWindowCustomizeGridViewDialog.showCustomize(0, m_simpleInputWindow.getAD_Tab_ID(), columnsWidth,gridFieldIds, this);


		/*JPiereMatrixWindowQuickEntry#ConfirmPanel*/
		}else if (event.getTarget().equals(SearchButton) || event.getName().equals("onComplete")){//onCompolete from process dialog

			if(!createView ())
			{
				SearchButton.setEnabled(true);
				SaveButton.setEnabled(false);
				CreateButton.setEnabled(true);
				ProcessButton.setEnabled(true);
				simpleInputGrid.setVisible(false);
				CustomizeButton.setEnabled(false);
				if(event.getTarget().equals(SearchButton))
					throw new Exception(Msg.getMsg(Env.getCtx(), "NotFound"));
				else
					return;
			}


			SearchButton.setEnabled(false);
			SaveButton.setEnabled(true);
			CreateButton.setEnabled(true);
			ProcessButton.setEnabled(true);
			CustomizeButton.setEnabled(true);
			DeleteButton.setEnabled(true);

		}else if(event.getTarget().equals(SaveButton)){

			boolean isOK = saveData();

			if(isOK)
			{
				dirtyModel.clear();

				if(!createView ())
				{
					simpleInputGrid.setVisible(false);
					throw new Exception(message.toString());
				}

			}else{
				;//Nothing to do
			}

		}else if (event.getTarget().equals(selectAll)){

			toggleSelectionForAll(selectAll.isChecked());

		}else if(event.getTarget().equals(DeleteButton)){

			boolean isOK = saveData();

			if(isOK)
			{
				dirtyModel.clear();

				if(!createView ())
				{
					simpleInputGrid.setVisible(false);
					throw new Exception(message.toString());
				}

			}else{
				;//Nothing to do
			}

		}
	}//onEvent

	private void toggleSelectionForAll(boolean b) {
		org.zkoss.zul.Rows rows = simpleInputGrid.getRows();
		List<Component> childs = rows.getChildren();
		for(Component comp : childs) {
			org.zkoss.zul.Row row = (org.zkoss.zul.Row) comp;
			Component firstChild = row.getFirstChild();
			if (firstChild instanceof Cell) {
				firstChild = firstChild.getFirstChild();
			}
			if (firstChild instanceof Checkbox) {
				Checkbox checkbox = (Checkbox) firstChild;
				checkbox.setChecked(b);
				int rowIndex = (Integer) checkbox.getAttribute(GridTabRowRenderer.GRID_ROW_INDEX_ATTR);
				if (b)
					gridTab.addToSelection(rowIndex);
				else
					gridTab.removeFromSelection(rowIndex);
			}
		}
	}

	private boolean isAllSelected() {
		org.zkoss.zul.Rows rows = simpleInputGrid.getRows();
		List<Component> childs = rows.getChildren();
		boolean all = false;
		for(Component comp : childs) {
			org.zkoss.zul.Row row = (org.zkoss.zul.Row) comp;
			Component firstChild = row.getFirstChild();
			if (firstChild instanceof Cell) {
				firstChild = firstChild.getFirstChild();
			}
			if (firstChild instanceof Checkbox) {
				Checkbox checkbox = (Checkbox) firstChild;
				if (!checkbox.isChecked())
					return false;
				else
					all = true;
			}
		}
		return all;
	}

	private boolean saveData()
	{
		try
		{

			Trx.run(new TrxRunnable()
			{
				public void run(String trxName)
				{

					Collection<PO> POs = dirtyModel.values();
					for(PO po :POs)
					{
						po.saveEx(trxName);
					}

					updateColumn();

				}
			});

			return true;

		}
		catch (Exception e)
		{
			FDialog.error(form.getWindowNo(), form, "Error", e.getLocalizedMessage());
			return false;
		}finally{
			;
		}
	}   //  saveData

	String sum = Msg.getMsg(Env.getCtx(), "Sum");
	private void updateColumn()
	{
//		org.zkoss.zul.Columns columns = matrixGrid.getColumns();
//		List<Component>columnList =  columns.getChildren();
//
//		BigDecimal[] totalValues = new BigDecimal[columnList.size()];
//		for(int i = 0 ; i < totalValues.length; i++)
//			totalValues[i] = new BigDecimal(0);
//
//
//		TreeMap<Integer,Object> columnDataMap = null;
//		int columnDisplayType = 0;
//		Object valuObj = null;
//		for(Object rowKey :rowKeys)//get row
//		{
//			columnDataMap = viewModel.get(rowKey);
//			for(int i = 0; i < totalValues.length; i++)//get columns
//			{
//				if(i==0)//Fix Column
//				{
//					;//Nothing to do;
//				}else{
//
//					columnDisplayType = columnGridFieldMap.get(i).getDisplayType();
//
//					if(columnDisplayType == DisplayType.Number || columnDisplayType == DisplayType.Quantity
//							|| columnDisplayType == DisplayType.Amount || columnDisplayType == DisplayType.CostPrice)
//					{
//						valuObj = columnDataMap.get(i);
//						if(valuObj!=null)
//							totalValues[i] = totalValues[i].add((BigDecimal)valuObj);
//					}else if(columnDisplayType == DisplayType.Integer){
//						valuObj = columnDataMap.get(i);
//						if(valuObj!=null)
//							totalValues[i] = totalValues[i].add(new BigDecimal(valuObj.toString()));
//					}
//					valuObj=null;
//				}//if
//			}//for
//		}//for
	}


	/**
	 * @param columnName
	 */
	public void setFocusToField(String columnName) {
		for (WEditor editor : renderer.getEditors()) {
			if (columnName.equals(editor.getColumnName())) {
				Component c = editor.getComponent();
				if (c instanceof EditorBox) {
					c = ((EditorBox)c).getTextbox();
				} else if (c instanceof NumberBox) {
					c = ((NumberBox)c).getDecimalbox();
				}
				Clients.response(new AuFocus(c));
				break;
			}
		}
	}



}
