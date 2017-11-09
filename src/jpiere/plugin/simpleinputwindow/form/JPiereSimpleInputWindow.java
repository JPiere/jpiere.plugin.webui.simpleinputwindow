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
import java.sql.SQLException;
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
import org.adempiere.exceptions.DBException;
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
import org.adempiere.webui.component.ConfirmPanel;
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
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.GridTable;
import org.compiere.model.GridWindow;
import org.compiere.model.GridWindowVO;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MPInstance;
import org.compiere.model.MProcess;
import org.compiere.model.MRefList;
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
	private String PARENT_TABLE_NAME;
	private String LINK_COLUMN_NAME;



	/****************************************************
	 * Window Info
	 ****************************************************/
	private GridTab gridTab ;
	private GridView gridView ;

	private HashMap<Integer,SimpleInputWindowGridView> simpleInputWindowGridViewMap = new HashMap<Integer,SimpleInputWindowGridView> ();

	private Button SearchButton;

	private Button SaveButton;

	private Button IgnoreButton;

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
		Env.setContext(Env.getCtx(), form.getWindowNo(), "TABLE_NAME", TABLE_NAME);

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

		//for Context
		LINK_COLUMN_NAME = m_simpleInputWindow.getAD_Tab().getAD_Column().getColumnName();
		if(!Util.isEmpty(LINK_COLUMN_NAME))
		{
			PARENT_TABLE_NAME = LINK_COLUMN_NAME.substring(0, LINK_COLUMN_NAME.length()-("_ID").length());
			if(MTable.getTable_ID(PARENT_TABLE_NAME)==0)
				PARENT_TABLE_NAME = null;
			else
				Env.setContext(Env.getCtx(), form.getWindowNo(), "PARENT_TABLE_NAME", PARENT_TABLE_NAME);
		}
		Env.setContext(Env.getCtx(), form.getWindowNo(), "IsSOTrx", gridTab.getGridWindow().isSOTrx());

		setupFields(gridTab);//Set up display field

	}

	private void zkInit()
	{
		form.appendChild(mainLayout);
		form.setHeight("100%");

		/*Main Layout(Borderlayout)*/
		ZKUpdateUtil.setWidth(mainLayout, "100%");
		ZKUpdateUtil.setHeight(mainLayout, "100%");

		//Main Layout(Borderlayout)-North
		North north = new North();
		mainLayout.appendChild(north);

		//Search Parameter Panel
		north.appendChild(parameterPanel);
		north.setStyle("border: none");
		parameterPanel.appendChild(parameterLayout); 		//parameterLayout = Grid
		ZKUpdateUtil.setWidth(parameterLayout, "100%");
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
					;//Error

				}else{

					String DefaultValue = m_simpleInputSearches[i].getDefaultValue();
					if(DefaultValue == null || DefaultValue.isEmpty())
					{
						Env.setContext(Env.getCtx(), form.getWindowNo(), editor.getColumnName(), "");
					}else{

						String value = Env.parseContext(Env.getCtx(), form.getWindowNo(), DefaultValue, false);
						Env.setContext(Env.getCtx(), form.getWindowNo(), editor.getColumnName(), value);
						editor.setValue(Env.parseContext(Env.getCtx(), form.getWindowNo(), DefaultValue, false));
						setParentCtx(editor);

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
					ZKUpdateUtil.setHflex((HtmlBasedComponent)editor.getComponent(), "true");
					actualxpos = actualxpos + searchField.getColumnSpan();

					//Create button that call Quick Entry Window.
					if(m_simpleInputSearches[i].getJP_QuickEntryWindow_ID() > 0)
					{
						Button QuickEntryButton = new Button();
						QuickEntryButton.setId(editor.getColumnName());
						QuickEntryButton.addActionListener(this);
						QuickEntryButton.setEnabled(true);
						if (ThemeManager.isUseFontIconForImage())
							QuickEntryButton.setIconSclass("z-icon-Form");
						else
							QuickEntryButton.setImage(ThemeManager.getThemeResource("images/mForm.png"));
						row.appendChild(QuickEntryButton);
					}

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
							if(image != null && (image.endsWith("Zoom16.png")||image.endsWith("Refresh16.png")) )
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
				if (ThemeManager.isUseFontIconForImage())
					SearchButton.setIconSclass("z-icon-Find");
				else
					SearchButton.setImage(ThemeManager.getThemeResource("images/Find16.png"));
				row.appendCellChild(SearchButton);

				CreateButton = new Button();
				CreateButton.setId("CreateButton");
				CreateButton.addActionListener(this);
				CreateButton.setEnabled(true);
				if (ThemeManager.isUseFontIconForImage())
					CreateButton.setIconSclass("z-icon-New");
				else
					CreateButton.setImage(ThemeManager.getThemeResource("images/New16.png"));
				if(!gridTab.isReadOnly() && gridTab.isInsertRecord())
					row.appendCellChild(CreateButton);

				SaveButton = new Button();
				SaveButton.setId("SaveButton");
				SaveButton.addActionListener(this);
				SaveButton.setEnabled(false);
				if (ThemeManager.isUseFontIconForImage())
					SaveButton.setIconSclass("z-icon-Save");
				else
					SaveButton.setImage(ThemeManager.getThemeResource("images/Save16.png"));
				if(!gridTab.isReadOnly())
					row.appendCellChild(SaveButton);

				IgnoreButton = new Button();
				IgnoreButton.setId("IgnoreButton");
				IgnoreButton.addActionListener(this);
				IgnoreButton.setEnabled(false);
				if (ThemeManager.isUseFontIconForImage())
					IgnoreButton.setIconSclass("z-icon-Ignore");
				else
					IgnoreButton.setImage(ThemeManager.getThemeResource("images/Ignore16.png"));
				if(!gridTab.isReadOnly())
					row.appendCellChild(IgnoreButton);


				HomeButton = new Button();
				HomeButton.setId("HomeButton");
				HomeButton.addActionListener(this);
				HomeButton.setEnabled(true);
				if (ThemeManager.isUseFontIconForImage())
					HomeButton.setIconSclass("z-icon-Home");
				else
					HomeButton.setImage(ThemeManager.getThemeResource("images/Home16.png"));
				row.appendCellChild(HomeButton);

				loadToolbarButtons();
				ProcessButton = new Button();
				ProcessButton.setId("ProcessButton");
				ProcessButton.addActionListener(this);
				ProcessButton.setEnabled(false);
				if (ThemeManager.isUseFontIconForImage())
					ProcessButton.setIconSclass("z-icon-Process");
				else
					ProcessButton.setImage(ThemeManager.getThemeResource("images/Process16.png"));
				if(toolbarProcessButtons.size()> 0 )
					row.appendCellChild(ProcessButton);

				DeleteButton = new Button();
				DeleteButton.setId("DeleteButton");
				DeleteButton.addActionListener(this);
				DeleteButton.setEnabled(false);
				if (ThemeManager.isUseFontIconForImage())
					DeleteButton.setIconSclass("z-icon-Delete");
				else
					DeleteButton.setImage(ThemeManager.getThemeResource("images/Delete16.png"));
				if(!gridTab.isReadOnly() && m_simpleInputWindow.isDeleteable())
					row.appendCellChild(DeleteButton);

				CustomizeButton = new Button();
				CustomizeButton.setId("CustomizeButton");
				CustomizeButton.addActionListener(this);
				CustomizeButton.setEnabled(false);
				if (ThemeManager.isUseFontIconForImage())
					CustomizeButton.setIconSclass("z-icon-Customize");
				else
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
		ZKUpdateUtil.setWidth(displayDataPanel, "100%");
		ZKUpdateUtil.setHeight(displayDataPanel, "100%");
		ZKUpdateUtil.setHflex(displayDataPanel, "1");
		ZKUpdateUtil.setVflex(displayDataPanel, "1");
		ZKUpdateUtil.setWidth(displayDataLayout, "100%");
		ZKUpdateUtil.setHeight(displayDataLayout, "100%");
		displayDataLayout.setStyle("border: none");

			//Edit Area
			editArea.setStyle("border: none");
			displayDataLayout.appendChild(editArea);
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
		ZKUpdateUtil.setWidth(tabbox, "100%");
		ZKUpdateUtil.setHeight(tabbox, "100%");
		ZKUpdateUtil.setHflex(tabbox, "1");
		ZKUpdateUtil.setVflex(tabbox, "1");

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
				//Set default value
				GridField[] allFields =gridTab.getFields();
				for(int i = 0; i < allFields.length; i++)
				{
					Object defaultValue = allFields[i].getDefault();
					if(defaultValue!=null)
					{
						po.set_ValueNoCheck(allFields[i].getColumnName(), defaultValue);
					}
				}

				//Overwrite default value by Value of Search Fields
				for(Map.Entry<String, WEditor> entry: searchEditorMap.entrySet())
				{
					if(entry.getValue().getValue() != null && po.get_ColumnIndex(entry.getKey()) != -1)
					{
						Object value = entry.getValue().getValue() ;
						if (entry.getKey().endsWith("_ID") && value instanceof String )
						{
							value = Integer.parseInt((String)value);
						}
						po.set_ValueNoCheck(entry.getKey(), value);
					}
				}
				break;
			}
		}//for

		listPOs.add(po);

		createSimpleInputWindowGridView(0, listPOs, createTabTitle(null), null, SimpleInputWindowGridView.NEW_RECORD);

		//First Tab On Demand Rendering
		currentTabIndex = 0;
		currentSimpleInputWindowGridView = simpleInputWindowGridViewMap.get(currentTabIndex);
		tabbox.setSelectedIndex(currentTabIndex);
		tabbox.getTabpanel(currentTabIndex).appendChild(currentSimpleInputWindowGridView.getGrid());

		newModel = po ;
		newModelLineNo = 0;

		updateColumn();

		//set Buttons
		SearchButton.setEnabled(true);
		CreateButton.setEnabled(true);
		SaveButton.setEnabled(true);
		IgnoreButton.setEnabled(true);
		ProcessButton.setEnabled(true);
		CustomizeButton.setEnabled(false);
		DeleteButton.setEnabled(true);
		frozenNum.setReadWrite(false);

		Events.postEvent(Events.ON_CLICK, CreateButton,"startInitializeCallout");

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


	/**
	 * createView
	 *
	 * @return boolean
	 */
	private boolean createView () throws Exception
	{
		if(tabbox != null)
			editArea.removeChild(tabbox);

		tabbox = new Tabbox();
		tabbox.addEventListener(Events.ON_SELECT, this);

		tabbox.setParent(editArea);
		ZKUpdateUtil.setWidth(tabbox, "100%");
		ZKUpdateUtil.setHeight(tabbox, "100%");
	    ZKUpdateUtil.setHflex(tabbox, "1");
		ZKUpdateUtil.setVflex(tabbox, "1");

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
					if(createSimpleInputWindowGridView(tabIndex, listPOs, createTabTitle(tabFieldValue), tabFieldValue))
					{
						tabIndex++;
						tabFieldValue = po.get_Value(m_simpleInputWindow.getJP_TabField().getAD_Column().getColumnName());
						listPOs = new ArrayList<PO>();
						listPOs.add(po);
					}
				}
			}
		}//for

		//last tab
		createSimpleInputWindowGridView(tabIndex, listPOs, createTabTitle(tabFieldValue), tabFieldValue);

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

			int AD_Reference_ID = m_simpleInputWindow.getJP_TabField().getAD_Column().getAD_Reference_ID();
			if(AD_Reference_ID==SystemIDs.REFERENCE_DATATYPE_TABLEDIR
					|| AD_Reference_ID==SystemIDs.REFERENCE_DATATYPE_TABLE
					|| AD_Reference_ID==SystemIDs.REFERENCE_DATATYPE_SEARCH
					|| AD_Reference_ID==SystemIDs.REFERENCE_DATATYPE_LOCATOR
					|| AD_Reference_ID==SystemIDs.REFERENCE_DATATYPE_LOCATION
					|| AD_Reference_ID==SystemIDs.REFERENCE_DATATYPE_ACCOUNT
					|| AD_Reference_ID==SystemIDs.REFERENCE_DATATYPE_ASSIGNMENT)
			{
				MLookup lookup = MLookupFactory.get(Env.getCtx(), form.getWindowNo(), 0, m_simpleInputWindow.getJP_TabField().getAD_Column_ID(), DisplayType.Search);
				WSearchEditor editor = new WSearchEditor("keyColumn", true, false, true, lookup);
				editor.setValue(new Integer(tabFieldValue.toString()).intValue());
				return editor.getDisplay();

			}else if(AD_Reference_ID==SystemIDs.REFERENCE_DATATYPE_LIST){

				return MRefList.getListName(Env.getCtx(), m_simpleInputWindow.getJP_TabField().getAD_Column().getAD_Reference_Value_ID(), tabFieldValue.toString());

			}else if(AD_Reference_ID==SystemIDs.REFERENCE_DATATYPE_DATE
					|| AD_Reference_ID==SystemIDs.REFERENCE_DATATYPE_DATETIME
					|| AD_Reference_ID==SystemIDs.REFERENCE_DATATYPE_TIME ){
				return ((Timestamp)tabFieldValue).toString();
			}else{
				return tabFieldValue.toString();
			}
		}
	}

	private boolean createSimpleInputWindowGridView(int tabIndex, ArrayList<PO> listPOs, String tabTitle, Object tabFieldValue)
	{
		if(m_simpleInputWindow.getJP_TabField_ID()==0)
		{
			return createSimpleInputWindowGridView(tabIndex, listPOs, tabTitle, tabFieldValue, SimpleInputWindowGridView.SEARCH_SINGLE_TAB);
		}else{
			return createSimpleInputWindowGridView(tabIndex, listPOs, tabTitle, tabFieldValue, SimpleInputWindowGridView.SEARCH_MULTI_TAB);
		}
	}

	private boolean createSimpleInputWindowGridView(int tabIndex, ArrayList<PO> listPOs, String tabTitle, Object tabFieldValue, String edit_mode)
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

		ZKUpdateUtil.setWidth(grid, "100%");
		ZKUpdateUtil.setHeight(grid, "100%");
		ZKUpdateUtil.setHflex(grid, "1");
		ZKUpdateUtil.setVflex(grid, "1");
		grid.setMold("paging");
		grid.setPageSize(m_simpleInputWindow.getJP_PageSize());

		if(m_simpleInputWindow.getJP_TabField_ID()==0)
		{
			simpleInputWindowGridViewMap.put(tabIndex, new SimpleInputWindowGridView(SIWGridTable, listModel, renderer, grid, null, null, false, edit_mode));
		}else{

			if(Util.isEmpty(m_simpleInputWindow.getJP_TabField().getAD_Column().getColumnSQL()))
				simpleInputWindowGridViewMap.put(tabIndex, new SimpleInputWindowGridView(SIWGridTable, listModel, renderer, grid
						, m_simpleInputWindow.getJP_TabField().getAD_Column().getColumnName(), tabFieldValue, false, edit_mode));
			else
				simpleInputWindowGridViewMap.put(tabIndex, new SimpleInputWindowGridView(SIWGridTable, listModel, renderer, grid
						, m_simpleInputWindow.getJP_TabField().getAD_Column().getColumnName(), tabFieldValue, true, edit_mode));
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

		for (int i = 0; i < numColumns; i++)
		{
			// IDEMPIERE-2148: when has tab customize, ignore check properties isDisplayedGrid
			if ((isHasCustomizeData || gridFields[i].isDisplayedGrid()) && !gridFields[i].isToolbarOnlyButton())
			{
				org.zkoss.zul.Column column = new Column();
				column.setLabel(gridFields[i].getHeader());
				if(!gridFields[i].isSameLine() && !gridTab.isReadOnly() && gridTab.isInsertRecord())
					column.setImage(ThemeManager.getThemeResource("images/New10.png"));

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


	@Override
	public void actionPerformed(ActionEvent event)
	{

		if(dirtyModel.size() > 0 || newModel!=null)
		{
			if(!saveData(false))
				return;
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
			PO po = currentSimpleInputWindowGridView.getSimpleInputWindowListModel().getPO(selecttion[i]);
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

		//If Search field is Link Column, you can kick process with no data.
		if(editor.getColumnName().equals(LINK_COLUMN_NAME))
		{
			ProcessButton.setEnabled(true);

			//need to initial one time only.
			if(tabbox == null)
				prepareInitialProcess();
		}

		setCSS(editor);

		setParentCtx(editor);

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


	/**
	 * onEvent
	 *
	 * @return void
	 */
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

		//Click Grid Area
		else if (event.getTarget() instanceof Grid && Events.ON_CLICK.equals(event.getName()))
		{

			Object data = event.getData();
			org.zkoss.zul.Row row = null;
			if (data != null && data instanceof Component)
			{
				AbstractComponent cmp = (AbstractComponent) data;
				if(cmp instanceof Cell && cmp.getChildren().size() > 0 && !(cmp.getChildren().get(0) instanceof org.zkoss.zul.Label))
				{
					//control focus
					if(currentSimpleInputWindowGridView.getSimpleInputWindowGridRowRenderer().setFocus(cmp.getChildren().get(0)))
						return;
				}

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

			return;

		//SimpleInputWindowQuickEntry#ConfirmPanel
		}else if(event.getName().equals(ConfirmPanel.A_CANCEL)){

			quickEntry = null;
			quickEntryColumnName = null;

			return;

		//SimpleInputWindowQuickEntry#ConfirmPanel
		}else if(event.getName().equals(ConfirmPanel.A_OK)){

			WEditor editor = searchEditorMap.get(quickEntryColumnName);
			if(quickEntry.getRecord_ID() > 0)
			{
				editor.setValue(quickEntry.getRecord_ID());
			}else{
				FDialog.info(form.getWindowNo(), null, "JP_CannotCreateNew");
				return ;
			}

			setCSS(editor);
			setParentCtx(editor);

			//If Search field is Link Column, you can kick process with no data.
			if(quickEntryColumnName.equals(LINK_COLUMN_NAME))
			{
				ProcessButton.setEnabled(true);

				//need to initial one time only.
				if(tabbox == null)
					prepareInitialProcess();
			}

			quickEntry = null;
			quickEntryColumnName = null;

			return;

		/*When Select other tab*/
		}else if (event.getName().equals(Events.ON_SELECT)){

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

			return;

		//Press Ok Button on Customize Grid
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

			return;

		//Press Customize Button
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

				FDialog.info(form.getWindowNo(), null, Msg.getMsg(Env.getCtx(), "killsession.saveWorkMessage"));//Save Your Work!!
			}

			return;

		//Press Ignore Button
		}else if(event.getTarget().equals(IgnoreButton)){

			ignore();

		//Press Home Button
		}else if(event.getTarget().equals(HomeButton)){

			refresh();

			return;

		//Press Search Button
		}else if (event.getTarget().equals(SearchButton)){
			if(dirtyModel.size()==0 && newModel==null)
			{

				if(!createView ())
					return;

				//set Buttons
				SearchButton.setEnabled(true);
				CreateButton.setEnabled(true);
				SaveButton.setEnabled(true);
				IgnoreButton.setEnabled(true);
				ProcessButton.setEnabled(true);

				CustomizeButton.setEnabled(true);
				DeleteButton.setEnabled(true);
				frozenNum.setReadWrite(false);

			}else{

				FDialog.ask(form.getWindowNo(), null, Msg.getMsg(Env.getCtx(), "SaveChanges?"), new Callback<Boolean>() {//Do you want to save changes?

					@Override
					public void onCallback(Boolean result)
					{
						if (result)
						{
							if(!saveData(true))
							{
								FDialog.error(form.getWindowNo(), form, "SaveError");
							}
						}else{
							;//Nothing to do;
						}
			        }

				});//FDialog.

			}

			return;

		//Call from process dialog(After Process)
		}if(event.getName().equals("onComplete")){

			SimpleInputWindowProcessModelDialog dialog = (SimpleInputWindowProcessModelDialog)event.getTarget();
			HtmlBasedComponent  htmlLog = dialog.getInfoResultContent();
			ProcessInfo pInfo = dialog.getProcessInfo();
			SimpleInputWindowFDialog.info(form.getWindowNo(), htmlLog, pInfo.getSummary(), null, pInfo.getTitle());

			//Refresh Current Grid
			ArrayList<PO> poList = currentSimpleInputWindowGridView.getSimpleInputWindowGridTable().getPOs();
			for(PO po : poList)
			{
				po.load(null);
			}

			currentSimpleInputWindowGridView.getGrid().setModel(currentSimpleInputWindowGridView.getSimpleInputWindowListModel());
			if(currentSimpleInputWindowGridView.getSimpleInputWindowGridRowRenderer().getCurrentRow()!=null)
			{
				currentSimpleInputWindowGridView.getSimpleInputWindowGridRowRenderer().setCurrentRow(currentSimpleInputWindowGridView.getSimpleInputWindowGridRowRenderer().getCurrentRow());
			}

			return;

		//Press Save Button
		}else if(event.getTarget().equals(SaveButton)){

			saveData(false);

			return;

		//Press Create Button
		}else if(event.getTarget().equals(CreateButton)){

			if(event.getData()!=null && event.getData().equals("startInitializeCallout"))
			{
				currentSimpleInputWindowGridView.getSimpleInputWindowGridRowRenderer().startInitializeCallout();
				return;
			}


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

			return;

		//Select All Record
		}else if (event.getName().equals(Events.ON_CHECK)){

			toggleSelectionForAll(tabbox.getSelectedIndex(),((Checkbox)event.getTarget()).isChecked());

			return;

		//Press Delete Button
		}else if(event.getTarget().equals(DeleteButton)){

			onDelete();

			return;

		//Press Process Button
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

			return;

		//

		}else{

			//Press Quick Entry Button
			if(event.getTarget() instanceof org.zkoss.zul.Button)
			{
				for(int i = 0 ; i < m_simpleInputSearches.length; i++)
				{
					if(event.getTarget().getId().equals(m_simpleInputSearches[i].getAD_Field().getAD_Column().getColumnName()))
					{
						//Create Quick entry window
						quickEntry = new SimpleInutWindowQuickEntry (form.getWindowNo(), m_simpleInputSearches[i].getJP_QuickEntryWindow_ID(), this);
						quickEntryColumnName = m_simpleInputSearches[i].getAD_Field().getAD_Column().getColumnName();
						WEditor editor = searchEditorMap.get(quickEntryColumnName);
						quickEntry.loadRecord (editor.getValue()==null ? 0 : Integer.parseInt(editor.getValue().toString()));

						AEnv.showWindow(quickEntry);
					}
				}
			}

			return;
		}
	}//onEvent

	SimpleInutWindowQuickEntry quickEntry ;
	String quickEntryColumnName;

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
		IgnoreButton.setEnabled(false);
		ProcessButton.setEnabled(false);
		CustomizeButton.setEnabled(false);
		DeleteButton.setEnabled(false);
		frozenNum.setReadWrite(true);

	}


	public void ignore()
	{
		List<Row> rowList = currentSimpleInputWindowGridView.getGrid().getRows().getChildren();
		int rowIndex = 0;
		org.zkoss.zul.Row row = null;
		Cell lineNoCell = null;
		org.zkoss.zul.Label lineNoLabel = null;

		//Ignore New Record
		if(newModel!=null)
		{
			rowIndex =currentSimpleInputWindowGridView.getSimpleInputWindowListModel().getRowIndexFromID(newModel.get_ID());
			if(rowIndex == -1)
				rowIndex = newModelLineNo.intValue();

			currentSimpleInputWindowGridView.getSimpleInputWindowListModel().removePO(rowIndex);
			rowList.remove(rowIndex);
		}

		//Ignore Update Record
		Collection<PO> POs = dirtyModel.values();
		for(PO po : POs)
		{
			po.load(null);//TODO:エラー処理
			rowIndex =currentSimpleInputWindowGridView.getSimpleInputWindowListModel().getRowIndexFromID(po.get_ID());
			row = rowList.get(rowIndex);
			lineNoCell = (Cell)row.getChildren().get(1);
			lineNoLabel = (org.zkoss.zul.Label)lineNoCell.getChildren().get(0);
			lineNoLabel.setValue(lineNoLabel.getValue().replace("*", ""));
		}

		currentSimpleInputWindowGridView.getGrid().setModel(currentSimpleInputWindowGridView.getSimpleInputWindowListModel());
		currentSimpleInputWindowGridView.getSimpleInputWindowGridRowRenderer().stopEditing(false);

		dirtyModel.clear();
		dirtyLineNo.clear();
		newModel = null;
		newModelLineNo = null;

		updateColumn();
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

						setParentCtx(editor);
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
								setParentCtx(editor);
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
					List<Row> rowList = currentSimpleInputWindowGridView.getGrid().getRows().getChildren();
					int rowIndex = 0;
					org.zkoss.zul.Row row = null;
					Cell lineNoCell = null;
					org.zkoss.zul.Label lineNoLabel = null;


					if(newModel!=null)
					{
						newModel.saveEx(trxName);

						rowIndex =currentSimpleInputWindowGridView.getSimpleInputWindowListModel().getRowIndexFromID(newModel.get_ID());
						if(rowIndex == -1)
							rowIndex = newModelLineNo.intValue();
						row = rowList.get(rowIndex);
						lineNoCell = (Cell)row.getChildren().get(1);
						lineNoLabel = (org.zkoss.zul.Label)lineNoCell.getChildren().get(0);
						lineNoLabel.setValue(lineNoLabel.getValue().replace("+*", ""));

						newModel = null;
						newModelLineNo = null;
					}

					Collection<PO> POs = dirtyModel.values();
					for(PO po :POs)
					{
						if(checkExclusiveControl(po))
						{
							po.saveEx(trxName);

							rowIndex =currentSimpleInputWindowGridView.getSimpleInputWindowListModel().getRowIndexFromID(po.get_ID());
							row = rowList.get(rowIndex);
							lineNoCell = (Cell)row.getChildren().get(1);
							lineNoLabel = (org.zkoss.zul.Label)lineNoCell.getChildren().get(0);
							lineNoLabel.setValue(lineNoLabel.getValue().replace("*", ""));
						}
					}//for

					updateColumn();
				}
			});


			List<Row> rowList = currentSimpleInputWindowGridView.getGrid().getRows().getChildren();
			int rowIndex = 0;
			org.zkoss.zul.Row row = null;
			Cell lineNoCell = null;
			org.zkoss.zul.Label lineNoLabel = null;

			//Check not save PO because Exclusive Control.
			HashMap<Integer,PO> notSaveModel = new HashMap<Integer,PO>();
			HashMap<Integer,Integer> notSaveLineNo = new HashMap<Integer,Integer>();
			ArrayList<String> lineLabelList = new ArrayList<String>();
			Collection<PO> POs =  dirtyModel.values();
			for(PO po : POs)
			{
				rowIndex =currentSimpleInputWindowGridView.getSimpleInputWindowListModel().getRowIndexFromID(po.get_ID());
				row = rowList.get(rowIndex);
				lineNoCell = (Cell)row.getChildren().get(1);
				lineNoLabel = (org.zkoss.zul.Label)lineNoCell.getChildren().get(0);
				if(lineNoLabel.getValue().contains("*"))
				{
					notSaveModel.put((Integer)po.get_ID(), po);
					notSaveLineNo.put((Integer)po.get_ID(), rowIndex);
					lineLabelList.add(lineNoLabel.getValue());
				}
			}

			dirtyModel.clear();
			dirtyLineNo.clear();
			dirtyModel.putAll(notSaveModel);
			dirtyLineNo.putAll(notSaveLineNo);
			if(dirtyModel.size()>0)
			{
				String msg = Msg.getMsg(Env.getCtx(), "CurrentRecordModified");//Current record was changed by another user, please ReQuery
				msg = msg + System.lineSeparator() + Msg.getElement(Env.getCtx(), "LineNo") + " : ";
				for(String lineLabel :lineLabelList)
				{
					msg = msg + lineLabel + ", ";
				}
				FDialog.info(form.getWindowNo(), form, msg);

				return false;
			}

			if(isRefreshAfterSave)
			{
				if(!createView ())
				{
					throw new Exception(message.toString());
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
						return;
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
							currentSimpleInputWindowGridView.getGrid().setModel(currentSimpleInputWindowGridView.getSimpleInputWindowListModel());
							return;

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
	public void updateColumn()
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
				if(DisplayType.isNumeric(gridFields[j].getDisplayType()) && gridFields[j].getDisplayLength()!=0)
				{
					if(po.get_Value(gridFields[j].getColumnName())!=null)
					{
						totalValues[j] =totalValues[j].add(new BigDecimal(po.get_Value(gridFields[j].getColumnName()).toString()));
					}
				}
			}//for j
		}//for i

		//Update column label
		Column column = null;
		for(int i = 0; i <  gridFields.length; i++)
		{
			if(DisplayType.isNumeric(gridFields[i].getDisplayType()) && gridFields[i].getDisplayLength()!=0)
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

	private void setParentCtx(WEditor editor)
	{
		if(PARENT_TABLE_NAME != null && editor.getColumnName().equals(LINK_COLUMN_NAME))
		{
			PO po = null;
			List<IModelFactory> factoryList = Service.locator().list(IModelFactory.class).getServices();
			if (factoryList != null)
			{

				int record_id = 0;
				if(editor.getValue()!=null)
					record_id = Integer.parseInt(editor.getValue().toString());

				for(IModelFactory factory : factoryList)
				{
					po = factory.getPO(PARENT_TABLE_NAME, record_id, null);
					if (po != null)
						break;
				}

				for(int i = 0; i < po.get_ColumnCount(); i++)
				{
					if(record_id == 0 || po.get_Value(i)==null)
					{
						Env.setContext(Env.getCtx(), gridTab.getWindowNo(), po.get_ColumnName(i), "");
					}else{

						if(po.get_Value(i).toString().equals("true") || po.get_Value(i).toString().equals("false"))
						{
							Env.setContext(Env.getCtx(), form.getWindowNo(), po.get_ColumnName(i),  po.get_Value(i).toString().equals("true") ? "Y" : "N");
						}else{
							Env.setContext(Env.getCtx(), gridTab.getWindowNo(), po.get_ColumnName(i), po.get_Value(i).toString());
						}
					}
				}

				Env.setContext(Env.getCtx(), form.getWindowNo(), "IsSOTrx", gridTab.getGridWindow().isSOTrx());
				Env.setContext(Env.getCtx(), form.getWindowNo(), "PARENT_RECORD_ID", po.get_ID());

			}//if (factoryList != null)
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

	/**
	 * this method is for initial process before create grid.
	 */
	private void prepareInitialProcess()
	{
		if(tabbox != null)
			editArea.removeChild(tabbox);

		tabbox = new Tabbox();
		tabbox.setParent(editArea);
		ZKUpdateUtil.setWidth(tabbox, "100%");
		ZKUpdateUtil.setHeight(tabbox, "100%");
		ZKUpdateUtil.setVflex(tabbox, "1");
		ZKUpdateUtil.setHflex(tabbox, "1");

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
				//Set default value
				for(int i = 0; i < gridFields.length; i++)
				{
					Object defaultValue = gridFields[i].getDefault();
					if(defaultValue!=null)
					{
						po.set_ValueNoCheck(gridFields[i].getColumnName(), defaultValue);
					}
				}

				//Overwrite default value
				for(Map.Entry<String, WEditor> entry: searchEditorMap.entrySet())
				{
					if(entry.getValue().getValue() != null && po.get_ColumnIndex(entry.getKey()) != -1)
					{
						Object value = entry.getValue().getValue() ;
						if (entry.getKey().endsWith("_ID") && value instanceof String )
						{
							value = Integer.parseInt((String)value);
						}
						po.set_ValueNoCheck(entry.getKey(), value);
					}
				}
				break;
			}
		}//for

		listPOs.add(po);

		createSimpleInputWindowGridView(0, listPOs, createTabTitle(null), null, SimpleInputWindowGridView.NEW_RECORD);

		//First Tab On Demand Rendering
		currentTabIndex = 0;
		currentSimpleInputWindowGridView = simpleInputWindowGridViewMap.get(currentTabIndex);
		tabbox.setSelectedIndex(currentTabIndex);
		tabbox.getTabpanel(currentTabIndex).appendChild(currentSimpleInputWindowGridView.getGrid());

		editArea.removeChild(tabbox);

	}

	/**
	 *
	 *  If this method returns false, you can not save. because other people saved same record before you save.
	 *  I refered GridTable.hasChanged() method.
	 *
	 */
	private boolean checkExclusiveControl(PO po)
	{
		int colUpdated = po.get_ColumnIndex("Updated");
		int colProcessed = po.get_ColumnIndex("Processed");

		boolean hasUpdated = (colUpdated > 0);
		boolean hasProcessed = (colProcessed > 0);

		String columns = null;
		if (hasUpdated && hasProcessed) {
			columns = new String("Updated, Processed");
		} else if (hasUpdated) {
			columns = new String("Updated");
		} else if (hasProcessed) {
			columns = new String("Processed");
		} else {
			// no columns updated or processed to commpare
			return false;
		}

		Timestamp dbUpdated = null;
    	String dbProcessedS = null;
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	String sql = "SELECT " + columns + " FROM " + TABLE_NAME + " WHERE " + TABLE_NAME + "_ID=?";
    	try
    	{
    		pstmt = DB.prepareStatement(sql, null);
    		pstmt.setInt(1, po.get_ID());
    		rs = pstmt.executeQuery();
    		if (rs.next()) {
    			int idx = 1;
    			if (hasUpdated)
    				dbUpdated = rs.getTimestamp(idx++);
    			if (hasProcessed)
    				dbProcessedS = rs.getString(idx++);
    		}
    		else
    			if (log.isLoggable(Level.INFO)) log.info("No Value " + sql);
    	}
    	catch (SQLException e)
    	{
    		throw new DBException(e, sql);
    	}
    	finally
    	{
    		DB.close(rs, pstmt);
    		rs = null; pstmt = null;
    	}

    	if (hasUpdated)
    	{
			Timestamp memUpdated = null;
			memUpdated = (Timestamp) po.get_Value(colUpdated);
			if (memUpdated != null && ! memUpdated.equals(dbUpdated))
				return false;
    	}

    	if (hasProcessed)
    	{
			Boolean memProcessed = null;
			memProcessed = (Boolean) po.get_Value(colProcessed);

			Boolean dbProcessed = Boolean.TRUE;
			if (! dbProcessedS.equals("Y"))
				dbProcessed = Boolean.FALSE;
			if (memProcessed != null && ! memProcessed.equals(dbProcessed))
				return false;
    	}

		return true;
	}
}
