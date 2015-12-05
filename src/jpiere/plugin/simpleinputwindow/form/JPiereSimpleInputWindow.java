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
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.table.AbstractTableModel;

import jpiere.plugin.simpleinputwindow.model.MSimpleInputSearch;
import jpiere.plugin.simpleinputwindow.model.MSimpleInputWindow;
import jpiere.plugin.simpleinputwindow.window.SimpleInputWindowCustomizeGridViewDialog;
import jpiere.plugin.simpleinputwindow.window.SimpleInputWindowFDialog;
import jpiere.plugin.simpleinputwindow.window.SimpleInputWindowProcessModelDialog;

import org.adempiere.base.IModelFactory;
import org.adempiere.base.Service;
import org.adempiere.model.MTabCustomization;
import org.adempiere.util.Callback;
import org.adempiere.webui.AdempiereWebUI;
import org.adempiere.webui.ISupportMask;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.adwindow.GridTabRowRenderer;
import org.adempiere.webui.adwindow.GridView;
import org.adempiere.webui.adwindow.ProcessButtonPopup;
import org.adempiere.webui.adwindow.ToolbarProcessButton;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Columns;
import org.adempiere.webui.component.EditorBox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.NumberBox;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Tabbox;
import org.adempiere.webui.component.Tabpanel;
import org.adempiere.webui.editor.IZoomableEditor;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WEditorPopupMenu;
import org.adempiere.webui.editor.WNumberEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.editor.WYesNoEditor;
import org.adempiere.webui.editor.WebEditorFactory;
import org.adempiere.webui.event.ActionEvent;
import org.adempiere.webui.event.ActionListener;
import org.adempiere.webui.event.ContextMenuListener;
import org.adempiere.webui.event.DialogEvents;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.GridTable;
import org.compiere.model.GridWindow;
import org.compiere.model.GridWindowVO;
import org.compiere.model.I_AD_Field;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MPInstance;
import org.compiere.model.MProcess;
import org.compiere.model.MRole;
import org.compiere.model.MTab;
import org.compiere.model.MTable;
import org.compiere.model.MToolBarButton;
import org.compiere.model.MToolBarButtonRestrict;
import org.compiere.model.PO;
import org.compiere.model.SystemIDs;
import org.compiere.model.X_AD_ToolBarButton;
import org.compiere.process.ProcessInfo;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.util.TrxRunnable;
import org.compiere.util.Util;
import org.zkoss.zk.au.out.AuFocus;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
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
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
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
	private Tabbox tabbox;

	protected Checkbox selectAll;

	private GridField[] gridFields;
	private AbstractTableModel tableModel;
	private int numColumns = 5;

	private String whereClause ;

	private StringBuilder message = new StringBuilder();

	//Map of PO Instance that have to save.<ID of PO,PO>
	private HashMap<Integer,PO>  dirtyModel  = new HashMap<Integer,PO>();

	//Map that is ID of PO and LineNo for save.< ID of PO, LieNo>
	private HashMap<Integer,Integer>  dirtyLineNo  = new HashMap<Integer,Integer>();

	//New data Model
	private PO newModel  = null;

	//New data Model line no
	private Integer newModelLineNo  = null;

	/**********************************************************************
	 * Parameter of Application Dictionary(System Client)
	 **********************************************************************/

	//Model
	private MSimpleInputWindow 	 m_simpleInputWindow;
	private MSimpleInputSearch[] m_simpleInputSearches ;

	private MTab			m_Tab;

	//Search Field Editor Map
	private HashMap<String,WEditor> searchEditorMap = new HashMap<String,WEditor> ();


	//Table Name
	private String TABLE_NAME ;

	/****************************************************
	 * Window Info
	 ****************************************************/
	private GridTab gridTab ;
	private GridView gridView ;

	private Grid simpleInputGrid  = new Grid();			//TODO:削除対象
	private HashMap<Integer,SimpleInputWindowGridView> simpleInputWindowGridViewMap = new HashMap<Integer,SimpleInputWindowGridView> ();

	private Button SearchButton;

	private Button SaveButton;

	private Button CreateButton;

	private Button HomeButton;

	private Button ProcessButton;

	private Button CustomizeButton;

	private Button DeleteButton;

	private WNumberEditor frozenNum;

	private SimpleInputWindowGridRowRenderer renderer;

	private SimpleInputWindowListModel listModel;

	boolean isHasCustomizeData = false;

	private Map<Integer, String> columnWidthMap;

	private static final int MIN_COLUMN_WIDTH = 100;

	private static final int MAX_COLUMN_WIDTH = 300;

	private static final int MIN_COMBOBOX_WIDTH = 160;

	private static final int MIN_NUMERIC_COL_WIDTH = 120;

	private Center editArea = new Center();

	private int currentTabIndex = 0;

	private SimpleInputWindowGridView currentSimpleInputWindowGridView;

	/**
	 * Constractor
	 *
	 * @throws IOException
	 */
    public JPiereSimpleInputWindow() throws IOException
    {
    	;
    }

	public CustomForm getForm()
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

		m_simpleInputSearches = m_simpleInputWindow.getSimpleInputSearches();

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
		form.setHeight("100%");

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
			//Caluculate max colspan
			int maxColspan = 0;
			int tmpColspan = 0;
			for(int i = 0; i < m_simpleInputSearches.length; i++)
			{
				tmpColspan = m_simpleInputSearches[i].getXPosition() + m_simpleInputSearches[i].getColumnSpan();
				if(maxColspan < tmpColspan)
					maxColspan = tmpColspan;
			}

			row = parameterLayoutRows.newRow();
				Groupbox searchGB = new Groupbox();
				row.appendCellChild(searchGB, maxColspan >= 10?  maxColspan+2 : 10);
				searchGB.appendChild(new Caption(Msg.getMsg(Env.getCtx(), "SearchCriteria")+" & " + Msg.getMsg(Env.getCtx(), "ValuePreference")));
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
					}

					editor.setMandatory(m_simpleInputSearches[i].isMandatory());
					setCSS(editor);

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
				SearchButton = new Button();
				SearchButton.setId("SearchButton");
				SearchButton.addActionListener(this);
				SearchButton.setEnabled(true);
				SearchButton.setImage(ThemeManager.getThemeResource("images/Find16.png"));
				row.appendCellChild(SearchButton);

				CreateButton = new Button();
				CreateButton.setId("CreateButton");
				CreateButton.addActionListener(this);
				CreateButton.setEnabled(true);
				CreateButton.setImage(ThemeManager.getThemeResource("images/New16.png"));
				if(!gridTab.isReadOnly() && gridTab.isInsertRecord())
					row.appendCellChild(CreateButton);

				SaveButton = new Button();
				SaveButton.setId("SaveButton");
				SaveButton.addActionListener(this);
				SaveButton.setEnabled(false);
				SaveButton.setImage(ThemeManager.getThemeResource("images/Save16.png"));
				if(!gridTab.isReadOnly())
					row.appendCellChild(SaveButton);

				HomeButton = new Button();
				HomeButton.setId("HomeButton");
				HomeButton.addActionListener(this);
				HomeButton.setEnabled(true);
				HomeButton.setImage(ThemeManager.getThemeResource("images/Home16.png"));
				row.appendCellChild(HomeButton);

				loadToolbarButtons();
				ProcessButton = new Button();
				ProcessButton.setId("ProcessButton");
				ProcessButton.addActionListener(this);
				ProcessButton.setEnabled(false);
				ProcessButton.setImage(ThemeManager.getThemeResource("images/Process16.png"));
				if(toolbarProcessButtons.size()> 0 )
					row.appendCellChild(ProcessButton);

				DeleteButton = new Button();
				DeleteButton.setId("DeleteButton");
				DeleteButton.addActionListener(this);
				DeleteButton.setEnabled(false);
				DeleteButton.setImage(ThemeManager.getThemeResource("images/Delete16.png"));
				if(!gridTab.isReadOnly() && m_simpleInputWindow.isDeleteable())
					row.appendCellChild(DeleteButton);

				CustomizeButton = new Button();
				CustomizeButton.setId("CustomizeButton");
				CustomizeButton.addActionListener(this);
				CustomizeButton.setEnabled(false);
				CustomizeButton.setImage(ThemeManager.getThemeResource("images/Customize16.png"));
				row.appendCellChild(CustomizeButton);


				row.appendCellChild(new Label(Msg.getElement(Env.getCtx(), "JP_FrozenField")).rightAlign(),1);
				frozenNum= new WNumberEditor("JP_FrozenField", true, false, true, DisplayType.Integer, "");
				frozenNum.setValue(m_simpleInputWindow.getJP_FrozenField());
				frozenNum.addValueChangeListener(this);
				row.appendCellChild(frozenNum.getComponent());

		//Edit Area
		Center center = new Center();
		center.setStyle("margin-top: 12px");
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

				//Edit Area
				editArea.setStyle("border: none");
				displayDataLayout.appendChild(editArea);
//				tabbox.setParent(editArea);
//				tabbox.setWidth("100%");
//			    tabbox.setHeight("100%");
//			    tabbox.setVflex("1");
//			    tabbox.setHflex("1");
//
//				Tabs tabs = new Tabs();
//				tabbox.appendChild(tabs);
//				Tabpanels tabpanels = new Tabpanels();
//				tabbox.appendChild(tabpanels);
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

	private boolean createNew() throws Exception {

		if(tabbox != null)
			editArea.removeChild(tabbox);

		tabbox = new Tabbox();
		tabbox.setParent(editArea);
		tabbox.setWidth("100%");
	    tabbox.setHeight("100%");
	    tabbox.setVflex("1");
	    tabbox.setHflex("1");

		Tabs tabs = new Tabs();
		tabbox.appendChild(tabs);
		Tabpanels tabpanels = new Tabpanels();
		tabbox.appendChild(tabpanels);

		ArrayList<PO> listPOs = new ArrayList<PO>();
		List<IModelFactory> factoryList = Service.locator().list(IModelFactory.class).getServices();
		if (factoryList == null)
		{
			;//
		}
		PO po = null;
		for(IModelFactory factory : factoryList)
		{
			po = factory.getPO(TABLE_NAME, 0, null);//
			if (po != null)
			{
				for(int i = 0; i < gridFields.length; i++)
				{
					Object defaultValue = gridFields[i].getDefault();
					if(defaultValue!=null)
					{
						po.set_ValueNoCheck(gridFields[i].getColumnName(), defaultValue);
					}
				}

				for(Map.Entry<String, WEditor> entry: searchEditorMap.entrySet())
				{
					if(entry.getValue().getValue() != null && po.get_ColumnIndex(entry.getKey()) != -1)
						po.set_ValueNoCheck(entry.getKey(), entry.getValue().getValue());
				}
				break;
			}
		}//for

		listPOs.add(po);

		boolean isOK = createSimpleInputWindowGridView(0, listPOs, createTabTitle(null), null);

		//First Tab On Demand Rendering
		currentTabIndex = 0;
		currentSimpleInputWindowGridView = simpleInputWindowGridViewMap.get(currentTabIndex);
		tabbox.setSelectedIndex(currentTabIndex);
		tabbox.getTabpanel(currentTabIndex).appendChild(currentSimpleInputWindowGridView.getGrid());
		updateColumn();

		newModel = po ;
		newModelLineNo = 0;

		//set Buttons
		SearchButton.setEnabled(true);
		CreateButton.setEnabled(true);
		SaveButton.setEnabled(true);
		ProcessButton.setEnabled(true);
		CustomizeButton.setEnabled(false);
		DeleteButton.setEnabled(true);
		frozenNum.setReadWrite(false);

		return true;
	}

	public SimpleInputWindowListModel getListModel()
	{
		return listModel;
	}

	public HashMap<Integer,PO> getDirtyModel()
	{
		return dirtyModel;
	}

	public PO getNewModel()
	{
		return newModel;
	}

	public void setNewModel(PO po)
	{
		newModel = po;
	}

	public Integer getNewModelLineNo()
	{
		return newModelLineNo;
	}

	public void setNewModelLineNo(Integer lineNo)
	{
		newModelLineNo = lineNo;
	}

	public HashMap<Integer,Integer> getDirtyLineNo()
	{
		return dirtyLineNo;
	}

	public GridTab getGridTab()
	{
		return gridTab;
	}

	private boolean createView () throws Exception
	{
		if(tabbox != null)
			editArea.removeChild(tabbox);

		tabbox = new Tabbox();
		tabbox.addEventListener(Events.ON_SELECT, this);

		tabbox.setParent(editArea);
		tabbox.setWidth("100%");
	    tabbox.setHeight("100%");
	    tabbox.setVflex("1");
	    tabbox.setHflex("1");

		Tabs tabs = new Tabs();
		tabbox.appendChild(tabs);
		Tabpanels tabpanels = new Tabpanels();
		tabbox.appendChild(tabpanels);

		//Create String where clause
		whereClause = createWhere();
		if(!Util.isEmpty(message.toString()))
		{
			FDialog.info(form.getWindowNo(), null, message.toString());
			message = new StringBuilder();
			return false;
		}

		//Create array of PO from where clause
		ArrayList<PO> allPOs  = getPOs(whereClause);
		if(allPOs.size()==0)
		{
			message.append(System.getProperty("line.separator") + Msg.getMsg(Env.getCtx(), "not.found"));
			FDialog.info(form.getWindowNo(), null, message.toString());
			message = new StringBuilder();
			return false;
		}

		ArrayList<PO> listPOs = new ArrayList<PO>();
		Object tabFieldValue = null;
		if(m_simpleInputWindow.getJP_TabField_ID()!=0)
			tabFieldValue = allPOs.get(0).get_Value(m_simpleInputWindow.getJP_TabField().getAD_Column().getColumnName());
		int tabIndex = 0;
		for(PO po:allPOs)
		{
			if(tabFieldValue == null) //Not use tab Fields
			{
				listPOs.add(po);
			}
			else
			{
				if(tabFieldValue.equals(po.get_Value(m_simpleInputWindow.getJP_TabField().getAD_Column().getColumnName())))
				{
					listPOs.add(po);
				}else{
					boolean isOK = createSimpleInputWindowGridView(tabIndex, listPOs, createTabTitle(tabFieldValue), tabFieldValue);
					tabIndex++;
					tabFieldValue = po.get_Value(m_simpleInputWindow.getJP_TabField().getAD_Column().getColumnName());
					listPOs = new ArrayList<PO>();
					listPOs.add(po);
				}
			}
		}//for

		//last tab
		boolean isOK = createSimpleInputWindowGridView(tabIndex, listPOs, createTabTitle(tabFieldValue), tabFieldValue);

		//First Tab On Demand Rendering
		currentTabIndex = 0;
		currentSimpleInputWindowGridView = simpleInputWindowGridViewMap.get(currentTabIndex);
		tabbox.setSelectedIndex(currentTabIndex);
		tabbox.getTabpanel(currentTabIndex).appendChild(currentSimpleInputWindowGridView.getGrid());
		updateColumn();

		return true;

	}

	private String createTabTitle(Object tabFieldValue)
	{
		if(tabFieldValue == null)
		{
			return gridTab.getName();
		}else{

			if(m_simpleInputWindow.getJP_TabField().getAD_Column().getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_TABLEDIR
					|| m_simpleInputWindow.getJP_TabField().getAD_Column().getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_TABLE
					|| m_simpleInputWindow.getJP_TabField().getAD_Column().getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_SEARCH )
			{
				MLookup lookup = MLookupFactory.get(Env.getCtx(), form.getWindowNo(), 0, m_simpleInputWindow.getJP_TabField().getAD_Column_ID(), DisplayType.Search);
				WSearchEditor editor = new WSearchEditor("keyColumn", true, false, true, lookup);
				editor.setValue(new Integer(tabFieldValue.toString()).intValue());
				return editor.getDisplay();
			}else if(m_simpleInputWindow.getJP_TabField().getAD_Column().getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_INTEGER ){
				return tabFieldValue.toString();
			}else if(m_simpleInputWindow.getJP_TabField().getAD_Column().getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_STRING ){
				return tabFieldValue.toString();
			}else if( m_simpleInputWindow.getJP_TabField().getAD_Column().getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_DATE
					|| m_simpleInputWindow.getJP_TabField().getAD_Column().getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_DATETIME
					|| m_simpleInputWindow.getJP_TabField().getAD_Column().getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_TIME ){
				return ((Timestamp)tabFieldValue).toString();
			}else{
				return tabFieldValue.toString();
			}
		}
	}

	private boolean createSimpleInputWindowGridView(int tabIndex, ArrayList<PO> listPOs, String tabTitle, Object tabFieldValue)
	{
		Tab tab  = new Tab(tabTitle);
		tabbox.getTabs().appendChild(tab);
		org.zkoss.zul.Tabpanel tabpanel = new Tabpanel();
		tabbox.getTabpanels().appendChild(tabpanel);

		Grid grid  = new Grid();

		grid.setId(new Integer(tabIndex).toString());

		org.zkoss.zul.Columns columns = grid.getColumns();
		if(columns == null)
		{
			setupColumns(grid, tabIndex);

		}else{
			List<Component> cmpList = grid.getChildren();

			for(Component cmp : cmpList)
			{
				if(cmp instanceof Frozen)
				{
					cmp.detach();
					break;
				}
			}//for
		}//if(columns == null)

		Frozen frozen = new Frozen();
		frozen.setColumns(((BigDecimal)frozenNum.getValue()).intValue() + 2);//freeze selection and indicator column
		grid.appendChild(frozen);

		SimpleInputWindowGridTable SIWGridTable = createTableModel(listPOs);

		SimpleInputWindowListModel listModel = new SimpleInputWindowListModel(SIWGridTable, form.getWindowNo());
		grid.setModel(listModel);
		listModel.addTableModelListener(this);

		SimpleInputWindowGridRowRenderer renderer = new SimpleInputWindowGridRowRenderer(this,listModel);
		renderer.setGridView(gridView);
		renderer.setGridTab(gridTab);
		renderer.createRecordProcessDialog();

		grid.setRowRenderer(renderer);
		grid.addEventListener(Events.ON_CLICK, this);

		grid.setWidth("100%");
		grid.setHeight("100%");
		grid.setVflex(true);
		grid.setVisible(true);
		grid.setMold("paging");
		grid.setPageSize(m_simpleInputWindow.getJP_PageSize());

		if(m_simpleInputWindow.getJP_TabField_ID()==0)
		{
			simpleInputWindowGridViewMap.put(tabIndex, new SimpleInputWindowGridView(SIWGridTable, listModel, renderer, grid, null, null,false));
		}else{

			if(Util.isEmpty(m_simpleInputWindow.getJP_TabField().getAD_Column().getColumnSQL()))
				simpleInputWindowGridViewMap.put(tabIndex, new SimpleInputWindowGridView(SIWGridTable, listModel, renderer, grid, m_simpleInputWindow.getJP_TabField().getAD_Column().getColumnName(), tabFieldValue, false));
			else
				simpleInputWindowGridViewMap.put(tabIndex, new SimpleInputWindowGridView(SIWGridTable, listModel, renderer, grid, m_simpleInputWindow.getJP_TabField().getAD_Column().getColumnName(), tabFieldValue, true));
		}
		return true;
	}

	/*
	 * Map of PO Instance <ID of PO,PO>
	 *
	 *
	 */
	private SimpleInputWindowGridTable createTableModel(ArrayList<PO> POs)
	{
		SimpleInputWindowGridTable SIWGridTable= new SimpleInputWindowGridTable();
		SIWGridTable.init(POs, gridFields);
		return SIWGridTable;
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


	private void setupColumns(org.zkoss.zul.Grid grid, int tabNo)
	{
		if (init) return;

		Columns columns = new Columns();

		org.zkoss.zul.Column selection = new Column();
		selection.setWidth("22px");
		try{
			selection.setSort("none");
		} catch (Exception e) {}
//		selection.setStyle("border-right: none");
		Checkbox selectAll = new Checkbox();
		selection.appendChild(selectAll);
		selectAll.setId("selectAll"+"_"+tabNo);
		selectAll.addEventListener(Events.ON_CHECK, this);
		columns.appendChild(selection);

		org.zkoss.zul.Column indicator = new Column();
		indicator.setWidth("28px");
		try {
			indicator.setSort("none");
		} catch (Exception e) {}

		indicator.setLabel(Msg.getElement(Env.getCtx(), "LineNo"));
		columns.appendChild(indicator);

		grid.appendChild(columns);
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
			Object value = entry.getValue().getValue();
			if(entry.getValue() instanceof WStringEditor)
			{
				String stringValue = (String)entry.getValue().getValue();
				if(Util.isEmpty(stringValue))
					value = null;
			}

			if(value != null)
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

		MRole role = MRole.get(Env.getCtx(), Env.getAD_Role_ID(Env.getCtx()));
		String orgAccessSQL = role.getOrgWhere(false);
		if( orgAccessSQL != null)
			whereClause.append(" AND ").append(gridTab.getTableName()).append(".").append(orgAccessSQL);

		return whereClause.toString();
	}

	private ArrayList<PO> getPOs (String whereClause)
	{
		//
		ArrayList<PO> list = new ArrayList<PO>();

		StringBuilder sql = null;
		sql = new StringBuilder("SELECT " + TABLE_NAME+".*");

		//Virtual Column
		for(int i = 0; i < gridFields.length; i++)
		{
			if(gridFields[i].isVirtualColumn())
				sql.append("," + gridFields[i].getColumnSQL(true));
		}

		sql.append(" FROM " + TABLE_NAME );

		if(m_simpleInputWindow.getJP_JoinClause() != null)
		{
			sql.append(" "+ m_simpleInputWindow.getJP_JoinClause());
		}

		sql.append(whereClause);

		if(m_simpleInputWindow.getOrderByClause() != null)
		{
			sql.append(" ORDER BY "+ m_simpleInputWindow.getOrderByClause());
		}else{
			sql.append(" ORDER BY ").append(TABLE_NAME).append(".").append(TABLE_NAME).append("_ID");
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

		return list;
	}	//	getPOs

	private ArrayList<Object>  getTabFields(String whereClause,boolean reload)
	{
		ArrayList<Object> list = new ArrayList<Object>();

		StringBuilder sql = null;
		sql = new StringBuilder("SELECT DISTINCT ");

		I_AD_Field tabField = m_simpleInputWindow.getJP_TabField();
		if(Util.isEmpty(tabField.getAD_Column().getColumnSQL()))
		{
			sql.append(TABLE_NAME + "." + tabField.getAD_Column().getColumnName());
		}else{//Virtual Column
			sql.append(tabField.getAD_Column().getColumnSQL() + " AS " + tabField.getAD_Column().getColumnName());
		}

		sql.append(" FROM " + TABLE_NAME );

		if(m_simpleInputWindow.getJP_JoinClause() != null)
		{
			sql.append(" "+ m_simpleInputWindow.getJP_JoinClause());
		}

		sql.append(whereClause);

//		if(m_simpleInputWindow.getOrderByClause() != null)
//		{
//			sql.append(" ORDER BY "+ m_simpleInputWindow.getOrderByClause());
//		}else{
//			sql.append(" ORDER BY ").append(TABLE_NAME).append(".").append(TABLE_NAME).append("_ID");
//		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), null);
			rs = pstmt.executeQuery();

			while (rs.next())
			{

				list.add(rs.getObject(1));
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

		return list;
	}


	@Override
	public void actionPerformed(ActionEvent event)
	{

		if(dirtyModel.size() > 0 )
		{
			saveData(false);
		}

		ToolbarProcessButton button = (ToolbarProcessButton)event.getSource();

		ProcessInfo pInfo = prepareProcess(button.getProcess_ID());
		DB.createT_SelectionNew(pInfo.getAD_PInstance_ID() , getSaveKeys(pInfo.getAD_PInstance_ID()), null);


		SimpleInputWindowProcessModelDialog dialog = new SimpleInputWindowProcessModelDialog(form.getWindowNo(),pInfo, false, this);

		//Mask
		Object window = SessionManager.getAppDesktop().findWindow(form.getWindowNo());
		final ISupportMask parent = LayoutUtils.showWindowWithMask(dialog, (Component)window, LayoutUtils.OVERLAP_PARENT);
		dialog.addEventListener(DialogEvents.ON_WINDOW_CLOSE, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				parent.hideMask();
			}
		});

		if (dialog.isValid())
		{
			//dialog.setWidth("500px");
			dialog.setBorder("normal");
			form.getParent().appendChild(dialog);
			//showBusyMask(dialog);
			LayoutUtils.openOverlappedWindow(form.getParent(), dialog, "middle_center");
			dialog.focus();
		}
		else
		{
			//onRefresh(true, false);
		}

	}

	 protected ProcessInfo prepareProcess (int processId){
		 final MProcess m_process = MProcess.get(Env.getCtx(), processId);
		 final ProcessInfo m_pi = new ProcessInfo(m_process.getName(), processId);
		 m_pi.setAD_User_ID(Env.getAD_User_ID(Env.getCtx()));
		 m_pi.setAD_Client_ID(Env.getAD_Client_ID(Env.getCtx()));

		 MPInstance instance = new MPInstance(Env.getCtx(), processId, 0);
		 instance.saveEx();
		 final int pInstanceID = instance.getAD_PInstance_ID();
		 // Execute Process
		 m_pi.setAD_PInstance_ID(pInstanceID);
		 m_pi.setAD_InfoWindow_ID(0);

		 return m_pi;
	 }

	 /**
	 * Save selected id, viewID of all process to map viewIDMap to save into T_Selection
	 */
	public Collection<KeyNamePair> getSaveKeys (int infoCulumnId)
	{

		int[]  selecttion = currentSimpleInputWindowGridView.getSimpleInputWindowListModel().getSelections();
		Collection<KeyNamePair> m_viewIDMap = new ArrayList <KeyNamePair>();

		for(int i = 0; i < selecttion.length; i++)
		{
			PO po = currentSimpleInputWindowGridView.getSimpleInputWindowListModel().getPO(i);
			m_viewIDMap.add(new KeyNamePair(po.get_ID(),""));
		}

		 return m_viewIDMap;

	}


	@Override
	public void tableChanged(WTableModelEvent event) {
		return;
	}

	@Override
	public void valueChange(ValueChangeEvent e) {

		WEditor editor = searchEditorMap.get(e.getPropertyName());

		if(editor == null)
		{
			editor = frozenNum;
			Integer integer = (Integer)e.getNewValue();
			if(integer==null)
			{
				editor.setValue(m_simpleInputWindow.getJP_FrozenField());
			}else if(gridFields.length <= integer.intValue()){
				editor.setValue(m_simpleInputWindow.getJP_FrozenField());
			}else{
				editor.setValue(e.getNewValue());
			}

			return;
		}

		editor.setValue(e.getNewValue());

		if(editor instanceof WYesNoEditor)
		{
			Env.setContext(Env.getCtx(), form.getWindowNo(), editor.getColumnName(), e.getNewValue().equals("true") ? "Y" : "N");
		}else{
			Env.setContext(Env.getCtx(), form.getWindowNo(), editor.getColumnName(), e.getNewValue()==null ? null : e.getNewValue().toString());
		}

		setCSS(editor);

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
	public void onEvent(final Event event) throws Exception {

		if(message != null && !Util.isEmpty(message.toString()))
		{
			FDialog.info(form.getWindowNo(), null, message.toString());
			message = new StringBuilder();
			return;
		}


		if (event == null)
		{
			return;
		}
		else if (event.getTarget() instanceof Grid && Events.ON_CLICK.equals(event.getName()))
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
				currentSimpleInputWindowGridView.getSimpleInputWindowGridRowRenderer().setCurrentRow(currentSimpleInputWindowGridView.getSimpleInputWindowGridRowRenderer().getCurrentRow());
				if(!currentSimpleInputWindowGridView.getSimpleInputWindowGridRowRenderer().isEditing())
					currentSimpleInputWindowGridView.getSimpleInputWindowGridRowRenderer().editCurrentRow();
			}
			event.stopPropagation();

		}else if (event.getName().equals(Events.ON_SELECT)){//Select other tab

			//Stop to edit cell for do not influence other cell of other tab, If you press other tab When you are editing a cell
			if(currentSimpleInputWindowGridView.getSimpleInputWindowGridRowRenderer().getCurrentRow()!=null)
			{
				currentSimpleInputWindowGridView.getSimpleInputWindowGridRowRenderer().setCurrentRow(currentSimpleInputWindowGridView.getSimpleInputWindowGridRowRenderer().getCurrentRow());
			}

			if(dirtyModel.size()==0 && newModel==null)
			{
				currentTabIndex = tabbox.getSelectedIndex();
				currentSimpleInputWindowGridView = simpleInputWindowGridViewMap.get(currentTabIndex);
				if(tabbox.getTabpanel(currentTabIndex).getChildren().size()==0)//On Demand Rendering
				{
					tabbox.getTabpanel(currentTabIndex).appendChild(currentSimpleInputWindowGridView.getGrid());
					updateColumn();
				}

			}else{

				FDialog.ask(form.getWindowNo(), null, Msg.getMsg(Env.getCtx(), "SaveChanges?"), new Callback<Boolean>() {//Do you want to save changes?

					@Override
					public void onCallback(Boolean result)
					{
						if (result)
						{
							saveData(false);
							currentTabIndex = tabbox.getSelectedIndex();
							currentSimpleInputWindowGridView = simpleInputWindowGridViewMap.get(currentTabIndex);
							if(tabbox.getTabpanel(currentTabIndex).getChildren().size()==0)//On Demand Rendering
								tabbox.getTabpanel(currentTabIndex).appendChild(currentSimpleInputWindowGridView.getGrid());

						}else{
							tabbox.setSelectedIndex(currentTabIndex);
						}
			        }

				});//FDialog.

			}

		}else if (event.getName().equals("onCustomizeGrid")){

			setupFields(gridTab);
			org.zkoss.zul.Columns columns = simpleInputWindowGridViewMap.get(tabbox.getSelectedIndex()).getGrid().getColumns();
			if(columns != null)
			{
				columns.detach();
				setupColumns(simpleInputWindowGridViewMap.get(tabbox.getSelectedIndex()).getGrid(), tabbox.getSelectedIndex());
			}

			editArea.removeChild(tabbox);

			tabbox = new Tabbox();
			tabbox.setParent(editArea);
			tabbox.setWidth("100%");
		    tabbox.setHeight("100%");
		    tabbox.setVflex("1");
		    tabbox.setHflex("1");

			Tabs tabs = new Tabs();
			tabbox.appendChild(tabs);
			Tabpanels tabpanels = new Tabpanels();
			tabbox.appendChild(tabpanels);

			Tab tab  = new Tab(gridTab.getName());
			tabbox.getTabs().appendChild(tab);
			org.zkoss.zul.Tabpanel tabpanel = new Tabpanel();
			tabbox.getTabpanels().appendChild(tabpanel);
			Grid grid  = new Grid();

			grid.setId(new Integer(0).toString());

			columns = grid.getColumns();
			if(columns == null)
			{
				setupColumns(grid, 0);

			}else{
				List<Component> cmpList = grid.getChildren();

				for(Component cmp : cmpList)
				{
					if(cmp instanceof Frozen)
					{
						cmp.detach();
						break;
					}
				}//for
			}//if

			tabpanel.appendChild(grid);


		}else if(event.getTarget().equals(CustomizeButton)){

			if(dirtyModel.size()==0 && newModel==null)
			{
				org.zkoss.zul.Columns columns = simpleInputWindowGridViewMap.get(tabbox.getSelectedIndex()).getGrid().getColumns();
				List<Component> columnList = columns.getChildren();
				Map<Integer, String> columnsWidth = new HashMap<Integer, String>();
				ArrayList<Integer> gridFieldIds = new ArrayList<Integer>();
				for (int i = 0; i < gridFields.length; i++)
				{
					// 2 is offset of num of column in grid view and actual data fields.
					// in grid view, add two function column, indicator column and selection (checkbox) column
					// @see GridView#setupColumns
					Column column = (Column) columnList.get(i+2);
					String width = column.getWidth();
					columnsWidth.put(gridFields[i].getAD_Field_ID(), width);
					gridFieldIds.add(gridFields[i].getAD_Field_ID());

				}

				SimpleInputWindowCustomizeGridViewDialog.showCustomize(0, m_simpleInputWindow.getAD_Tab_ID(), columnsWidth,gridFieldIds, this);

			}else{

				FDialog.info(form.getWindowNo(), null, "データを保存してから実行して下さい");//FDialog.
			}

		}else if(event.getTarget().equals(HomeButton)){

			refresh();

		/*JPiereMatrixWindowQuickEntry#ConfirmPanel*/
		}else if (event.getTarget().equals(SearchButton) || event.getName().equals("onComplete")){//onCompolete from process dialog

			if(dirtyModel.size()==0 && newModel==null)
			{

				if(!createView ())
					return;

				//set Buttons
				SearchButton.setEnabled(true);
				CreateButton.setEnabled(true);
				SaveButton.setEnabled(true);
				ProcessButton.setEnabled(true);

				CustomizeButton.setEnabled(true);
				DeleteButton.setEnabled(true);
				frozenNum.setReadWrite(false);

				if(event.getName().equals("onComplete"))
				{
					SimpleInputWindowProcessModelDialog dialog = (SimpleInputWindowProcessModelDialog)event.getTarget();
					HtmlBasedComponent  htmlLog = dialog.getInfoResultContent();
					ProcessInfo pInfo = dialog.getProcessInfo();
					SimpleInputWindowFDialog.info(form.getWindowNo(), htmlLog, pInfo.getSummary(), null, pInfo.getTitle());
				}

			}else{

				FDialog.ask(form.getWindowNo(), null, Msg.getMsg(Env.getCtx(), "SaveChanges?"), new Callback<Boolean>() {//Do you want to save changes?

					@Override
					public void onCallback(Boolean result)
					{
						if (result)
						{
							saveData(true);
						}else{
							;//Nothing to do;
						}
			        }

				});//FDialog.

			}

		}else if(event.getTarget().equals(SaveButton)){

				saveData(false);

		}else if(event.getTarget().equals(CreateButton)){

			frozenNum.setReadWrite(false);

			if(dirtyModel.size()==0 && newModel==null)
			{
				if(!createNew())
				{
					;
				}

			}else{

				FDialog.ask(form.getWindowNo(), null, Msg.getMsg(Env.getCtx(), "SaveChanges?"), new Callback<Boolean>() {//Do you want to save changes?

					@Override
					public void onCallback(Boolean result)
					{
						if (result)
						{
							if(saveData(false))
							{
								try {
									onEvent(event);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}else{
							;//Nothing to do;
						}
			        }

				});//FDialog.
			}

		}else if (event.getName().equals(Events.ON_CHECK)){

			toggleSelectionForAll(tabbox.getSelectedIndex(),((Checkbox)event.getTarget()).isChecked());

		}else if(event.getTarget().equals(DeleteButton)){

			onDelete();

		}else if(event.getTarget().equals(ProcessButton)){

			ProcessButtonPopup popup = new ProcessButtonPopup();
			popup.setWidgetAttribute(AdempiereWebUI.WIDGET_INSTANCE_NAME, "processButtonPopup");

			List<org.zkoss.zul.Button> buttonList = new ArrayList<org.zkoss.zul.Button>();
			for(ToolbarProcessButton processButton : toolbarProcessButtons) {
				if (processButton.getButton().isVisible()) {
					buttonList.add(processButton.getButton());
				}
			}

			popup.render(buttonList);

			LayoutUtils.openPopupWindow(ProcessButton, popup, "after_start");
		}
	}//onEvent

	private void toggleSelectionForAll(int tabIndex, boolean isChecked) {
		org.zkoss.zul.Rows rows = currentSimpleInputWindowGridView.getGrid().getRows();
		List<Component> childs = rows.getChildren();
		for(Component comp : childs) {
			org.zkoss.zul.Row row = (org.zkoss.zul.Row) comp;
			Component firstChild = row.getFirstChild();
			if (firstChild instanceof Cell) {
				firstChild = firstChild.getFirstChild();
			}
			if (firstChild instanceof Checkbox) {
				Checkbox checkbox = (Checkbox) firstChild;
				checkbox.setChecked(isChecked);
				int rowIndex = (Integer) checkbox.getAttribute(GridTabRowRenderer.GRID_ROW_INDEX_ATTR);
				if (isChecked)
				{
					currentSimpleInputWindowGridView.getSimpleInputWindowListModel().addToSelection(rowIndex);
					currentSimpleInputWindowGridView.selectAll.setChecked(true);
				}else{
					currentSimpleInputWindowGridView.getSimpleInputWindowListModel().removeFromSelection(rowIndex);
					currentSimpleInputWindowGridView.selectAll.setChecked(false);
				}
			}
		}
	}

	public void setInitialStatus()
	{
		frozenNum.setValue(m_simpleInputWindow.getJP_FrozenField());
		frozenNum.setReadWrite(true);
		editArea.removeChild(tabbox);

		//set Buttons
		SearchButton.setEnabled(true);
		CreateButton.setEnabled(true);
		SaveButton.setEnabled(false);
		ProcessButton.setEnabled(false);
		CustomizeButton.setEnabled(false);
		DeleteButton.setEnabled(false);
		frozenNum.setReadWrite(true);

	}

	public void refresh()
	{
		if(dirtyModel.size()==0 && newModel==null)
		{
			String columnName = null;
			WEditor editor = null;
			String DefaultValue = null;

			for(Map.Entry<String, WEditor> entry: searchEditorMap.entrySet())
			{
				columnName = entry.getKey();
				editor = entry.getValue();
				for(int i = 0; i < m_simpleInputSearches.length; i++)
				{
					if(columnName.equals(m_simpleInputSearches[i].getAD_Field().getAD_Column().getColumnName()))
					{
						DefaultValue = m_simpleInputSearches[i].getDefaultValue();
						if(!Util.isEmpty(DefaultValue))
						{
							String value = Env.parseContext(Env.getCtx(), form.getWindowNo(), DefaultValue, false);
							Env.setContext(Env.getCtx(), form.getWindowNo(), editor.getColumnName(), value);
							editor.setValue(Env.parseContext(Env.getCtx(), form.getWindowNo(), DefaultValue, false));

							if(editor instanceof WTableDirEditor)
							{
								((WTableDirEditor) editor).actionRefresh();
								((WTableDirEditor) editor).getLookup().setSelectedItem("");
							}


						}else{
							editor.setValue(null);
							Env.setContext(Env.getCtx(), form.getWindowNo(), editor.getColumnName(), "");
						}

						setCSS(editor);
					}

				}//for i

			}//for

			setInitialStatus();

		}else{

			FDialog.ask(form.getWindowNo(), null, Msg.getMsg(Env.getCtx(), "SaveChanges?"), new Callback<Boolean>() {//Do you want to save changes?

				@Override
				public void onCallback(Boolean result)
				{
					if (result)
					{
						if(!saveData(false))
							return ;

					}else{
						dirtyModel.clear();
						dirtyLineNo.clear();
						newModel = null;
						newModelLineNo = null;
					}

					String columnName = null;
					WEditor editor = null;
					String DefaultValue = null;

					for(Map.Entry<String, WEditor> entry: searchEditorMap.entrySet())
					{
						columnName = entry.getKey();
						editor = entry.getValue();
						for(int i = 0; i < m_simpleInputSearches.length; i++)
						{
							if(columnName.equals(m_simpleInputSearches[i].getAD_Field().getAD_Column().getColumnName()))
							{
								DefaultValue = m_simpleInputSearches[i].getDefaultValue();
								if(!Util.isEmpty(DefaultValue))
								{
									String value = Env.parseContext(Env.getCtx(), form.getWindowNo(), DefaultValue, false);
									Env.setContext(Env.getCtx(), form.getWindowNo(), editor.getColumnName(), value);
									editor.setValue(Env.parseContext(Env.getCtx(), form.getWindowNo(), DefaultValue, false));

									if(editor instanceof WTableDirEditor)
									{
										((WTableDirEditor) editor).actionRefresh();
										((WTableDirEditor) editor).getLookup().setSelectedItem("");
									}


								}else{
									editor.setValue(null);
									Env.setContext(Env.getCtx(), form.getWindowNo(), editor.getColumnName(), "");
								}

								setCSS(editor);
							}

						}//for i

					}//for

					setInitialStatus();

		        }

			});//FDialog.
		}//else
	}

	public boolean saveData(boolean isRefreshAfterSave)
	{
		try
		{
			Trx.run(new TrxRunnable()
			{
				public void run(String trxName)
				{

					if(newModel!=null)
						newModel.saveEx(trxName);

					Collection<PO> POs = dirtyModel.values();
					for(PO po :POs)
					{
						po.saveEx(trxName);
					}

					updateColumn();
				}
			});

			if(isRefreshAfterSave)
			{
				if(!createView ())
				{
					throw new Exception(message.toString());
				}
			}else{

				List<Row> rowList = currentSimpleInputWindowGridView.getGrid().getRows().getChildren();

				//Delete "+*"
				if(newModel!=null)
				{
					int rowIndex =currentSimpleInputWindowGridView.getSimpleInputWindowListModel().getRowIndexFromID(newModel.get_ID());
					if(rowIndex == -1)
						rowIndex = newModelLineNo.intValue();
					org.zkoss.zul.Row row = rowList.get(rowIndex);
					Cell lineNoCell = (Cell)row.getChildren().get(1);
					org.zkoss.zul.Label lineNoLabel = (org.zkoss.zul.Label)lineNoCell.getChildren().get(0);
					lineNoLabel.setValue(lineNoLabel.getValue().replace("+*", ""));
				}

				//Delete "*"
				Collection<PO> POs =  dirtyModel.values();
				int rowIndex = 0;
				org.zkoss.zul.Row row = null;
				Cell lineNoCell = null;
				org.zkoss.zul.Label lineNoLabel = null;
				for(PO po : POs)
				{
					rowIndex =currentSimpleInputWindowGridView.getSimpleInputWindowListModel().getRowIndexFromID(po.get_ID());
					row = rowList.get(rowIndex);
					lineNoCell = (Cell)row.getChildren().get(1);
					lineNoLabel = (org.zkoss.zul.Label)lineNoCell.getChildren().get(0);
					lineNoLabel.setValue(lineNoLabel.getValue().replace("*", ""));
				}

			}

			newModel = null;
			newModelLineNo = null;
			dirtyModel.clear();
			dirtyLineNo.clear();

			return true;
		}
		catch (Exception e)
		{
			FDialog.error(form.getWindowNo(), form, "SaveError", e.getLocalizedMessage());

			return false;

		}finally{
			;
		}
	}   //  saveData

	private void onDelete()
	{
		 if (gridTab.isReadOnly())
	        return;

		 final int[] indices = currentSimpleInputWindowGridView.getSimpleInputWindowListModel().getSelections();
		 if (indices.length > 0 )
		 {
			onDelete(indices);
			currentSimpleInputWindowGridView.getSimpleInputWindowGridRowRenderer().resetCursor();
			return;
		 }

		 if(currentSimpleInputWindowGridView.getSimpleInputWindowGridRowRenderer().getCurrentRowIndex() < 0)
		 {
			 FDialog.error(form.getWindowNo(), "DeleteError");
			 return;
		 }

		 final PO po= currentSimpleInputWindowGridView.getSimpleInputWindowGridTable().getPO(currentSimpleInputWindowGridView.getSimpleInputWindowGridRowRenderer().getCurrentRowIndex());
		 if(po == null)
		 {
			 FDialog.error(form.getWindowNo(), "DeleteError");
			 return;
		 }

		 String popupMsg = Msg.getMsg(Env.getCtx(), "DeleteRecord?");
		 String lineNo = Msg.getElement(Env.getCtx(), "LineNo")+"　:　";
		 popupMsg = popupMsg + System.lineSeparator() +  lineNo + (currentSimpleInputWindowGridView.getSimpleInputWindowGridRowRenderer().getCurrentRowIndex()+1);

		 FDialog.ask(form.getWindowNo(), null, popupMsg, new Callback<Boolean>() {

			@Override
			public void onCallback(Boolean result)
			{
				if (result)
				{
					try
					{
						int poID = po.get_ID();
						po.deleteEx(false);

						dirtyModel.remove(poID);
						dirtyLineNo.remove(poID);
						if(newModelLineNo != null && newModelLineNo.intValue() == currentSimpleInputWindowGridView.getSimpleInputWindowGridRowRenderer().getCurrentRowIndex())
						{
							newModel = null;
							newModelLineNo = null;
						}

					} catch (Exception e) {
						FDialog.error(form.getWindowNo(), "DeleteError");
					}

					currentSimpleInputWindowGridView.getSimpleInputWindowListModel().removePO(currentSimpleInputWindowGridView.getSimpleInputWindowGridRowRenderer().getCurrentRowIndex());

					List<Row> rowList = currentSimpleInputWindowGridView.getGrid().getRows().getChildren();
					rowList.remove(currentSimpleInputWindowGridView.getSimpleInputWindowGridRowRenderer().getCurrentRowIndex());
					currentSimpleInputWindowGridView.getGrid().setModel(currentSimpleInputWindowGridView.getSimpleInputWindowListModel());
					currentSimpleInputWindowGridView.getSimpleInputWindowGridRowRenderer().resetCursor();
					updateColumn();

				}//if (result)

	        }
		});//FDialog.

		 return;

	}

	private void onDelete(final int[] indices)
	{

		String stringMsg = Msg.getMsg(Env.getCtx(), "DeleteRecord?") + System.lineSeparator()
				+ Msg.getElement(Env.getCtx(), "LineNo") + " : " +System.lineSeparator() ;
		StringBuilder stringBuilderMsg = new StringBuilder(stringMsg);
		Arrays.sort(indices);//Sort Ascending
		for(int i = 0; i < indices.length; i++)
		{
			stringBuilderMsg.append((indices[i]+1)+", ");
			if(i%10 == 9)
				stringBuilderMsg.append(System.lineSeparator());
		}

		 FDialog.ask(form.getWindowNo(), null, stringBuilderMsg.toString(), new Callback<Boolean>() {

				@Override
				public void onCallback(Boolean result)
				{
					if (result)
					{
						Trx trx = null;
						String m_trxName = null;

						try
						{
							StringBuilder l_trxname = new StringBuilder("SimpleInput_").append(gridTab.getTableName());
							if (l_trxname.length() > 23)
								l_trxname.setLength(23);
							m_trxName = Trx.createTrxName(l_trxname.toString());
							trx = Trx.get(m_trxName, true);

							PO po = null;
							ArrayList<Integer> deleteID = new ArrayList<Integer>();
							for(int i = indices.length-1; i >= 0; i--)//delete descending
							{
								if(newModelLineNo != null && newModelLineNo.intValue() == indices[i])
								{
									newModel = null;
									newModelLineNo = null;
								}

								po= currentSimpleInputWindowGridView.getSimpleInputWindowGridTable().getPO(indices[i]);
								deleteID.add(po.get_ID());
								if(po.get_ID()!=0)
								{
									po.set_TrxName(m_trxName);
									po.deleteEx(false);
								}
							}

							trx.commit();

							for(Integer id : deleteID)
							{
								dirtyModel.remove(id);
								dirtyLineNo.remove(id);
							}

						} catch (Exception e) {

							trx.rollback();
							FDialog.error(form.getWindowNo(), "DeleteError");

						}finally{
							trx = null;
							m_trxName = null;
						}

						try
						{
							for(int i = indices.length-1; i >= 0; i--)//delete descending
							{
								currentSimpleInputWindowGridView.getSimpleInputWindowListModel().removeFromSelection(indices[i]);
								currentSimpleInputWindowGridView.getSimpleInputWindowListModel().removePO(indices[i]);

								List<Row> rowList = currentSimpleInputWindowGridView.getGrid().getRows().getChildren();
								rowList.remove(indices[i]);
							}
							currentSimpleInputWindowGridView.selectAll.setChecked(false);
							currentSimpleInputWindowGridView.getGrid().setModel(currentSimpleInputWindowGridView.getSimpleInputWindowListModel());
							updateColumn();

						} catch (Exception e) {
							FDialog.error(form.getWindowNo(), "Error");
						}
					}//if (result)

		        }//onCallback(Boolean result)

			});//FDialog.

			 return;

	}//onDelete(int[] indices)


	String sum = Msg.getMsg(Env.getCtx(), "Sum");
	private void updateColumn()
	{

		if(!m_simpleInputWindow.isSummarized())
			return;

		List<Component>columnList =  currentSimpleInputWindowGridView.getGrid().getColumns().getChildren();
		PO po= null;
		BigDecimal[] totalValues = new BigDecimal[columnList.size()];
		for(int i = 0 ; i < totalValues.length; i++)
			totalValues[i] = new BigDecimal(0);

		//Calculate
		for(int i = 0; i < currentSimpleInputWindowGridView.getSimpleInputWindowListModel().getSize(); i++)
		{
			po= currentSimpleInputWindowGridView.getSimpleInputWindowListModel().getPO(i);
			for(int j = 0; j < gridFields.length; j++)
			{
				if(DisplayType.isNumeric(gridFields[j].getDisplayType()))
				{
					totalValues[j] =totalValues[j].add((BigDecimal)po.get_Value(gridFields[j].getColumnName()));
				}
			}
		}

		//Update column label
		Column column = null;
		for(int i = 0; i <  gridFields.length; i++)
		{
			if(DisplayType.isNumeric(gridFields[i].getDisplayType()))
			{
				column = (Column)columnList.get(i+2);//2 is fix column(Checkbox + Line Number)
				column.setLabel(gridFields[i].getHeader()
						+ " (" + sum + ":" + DisplayType.getNumberFormat(gridFields[i].getDisplayType()).format(totalValues[i]) + ")");
			}
		}
	}

	/**
	 * setCSS
	 *
	 * Please call after set value and isMandatory to WEditor
	 *
	 */
	private void setCSS(WEditor editor)
	{
		//Set CSS of Label
		if(editor instanceof WSearchEditor
				|| editor instanceof WTableDirEditor)
		{
			if(editor.isMandatory() && editor.getValue()==null)
				editor.getLabel().setStyle("cursor: pointer; text-decoration: underline;color: #333; color:red;");
			else
				editor.getLabel().setStyle("cursor: pointer; text-decoration: underline;color: #333;");
		}else if (editor instanceof WStringEditor){

			String stringValue = (String)editor.getValue();
			if(editor.isMandatory() && Util.isEmpty(stringValue))
				editor.getLabel().setStyle("color:red;");
			else
				editor.getLabel().setStyle("color:#333;");
		}
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

	public HashMap<String,WEditor> getSearchEditorMap()
	{
		return searchEditorMap;
	}


	public SimpleInputWindowGridView getSimpleInputWindowGridView(int tabIndex)
	{
		return simpleInputWindowGridViewMap.get(tabIndex);
	}

}
