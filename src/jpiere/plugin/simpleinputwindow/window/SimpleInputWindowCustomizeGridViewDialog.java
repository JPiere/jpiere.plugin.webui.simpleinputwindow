package jpiere.plugin.simpleinputwindow.window;

import java.util.ArrayList;
import java.util.Map;

import jpiere.plugin.simpleinputwindow.form.JPiereSimpleInputWindow;
import jpiere.plugin.simpleinputwindow.panel.SimpleInputWindowCustomizeGridViewPanel;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Window;
import org.compiere.util.Env;
import org.compiere.util.Msg;

public class SimpleInputWindowCustomizeGridViewDialog extends Window {

	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = -4093048147438176240L;

	private SimpleInputWindowCustomizeGridViewPanel customizePanel;

	/**
	 *	Standard Constructor
	 * 	@param WindowNo window no
	 *  @param AD_Tab_ID tab
	 * 	@param AD_User_ID user
	 * @param columnsWidth
	 */
	public SimpleInputWindowCustomizeGridViewDialog(int windowNo, int AD_Tab_ID, int AD_User_ID, Map<Integer, String> columnsWidth,ArrayList<Integer> gridFieldIds)
	{
		setClosable(true);
		setTitle(Msg.getMsg(Env.getCtx(), "Customize"));
		initComponent(windowNo,AD_Tab_ID, AD_User_ID, columnsWidth,gridFieldIds);
	}

	private void initComponent(int windowNo, int AD_Tab_ID, int AD_User_ID, Map<Integer, String> columnsWidth,ArrayList<Integer> gridFieldIds) {
		customizePanel = new SimpleInputWindowCustomizeGridViewPanel(windowNo, AD_Tab_ID, AD_User_ID, columnsWidth,gridFieldIds);
		this.setStyle("position : absolute;");
		this.setWidth("600px");
		this.setHeight("500px");
		this.setBorder("normal");
		this.setSclass("popup-dialog");
		appendChild(customizePanel);
		customizePanel.createUI();
		customizePanel.query();
	}

	/**
	 * @return whether change have been successfully save to db
	 */
	public boolean isSaved() {
		return customizePanel.isSaved();
	}

	public void setGridPanel(JPiereSimpleInputWindow gridPanel){
		customizePanel.setGridPanel(gridPanel);
	}

	/**
	 * Show User customize (modal)
	 * @param WindowNo window no
	 * @param AD_Tab_ID
	 * @param columnsWidth
	 * @param gridFieldIds list fieldId current display in gridview
	 * @param gridPanel
	 */
	public static boolean showCustomize (int WindowNo, int AD_Tab_ID, Map<Integer, String> columnsWidth,ArrayList<Integer> gridFieldIds, JPiereSimpleInputWindow gridPanel)
	{
		SimpleInputWindowCustomizeGridViewDialog customizeWindow = new SimpleInputWindowCustomizeGridViewDialog(WindowNo, AD_Tab_ID, Env.getAD_User_ID(Env.getCtx()), columnsWidth,gridFieldIds);
		customizeWindow.setGridPanel(gridPanel);
		AEnv.showWindow(customizeWindow);
		return customizeWindow.isSaved();
	}   //  showProduct

}
