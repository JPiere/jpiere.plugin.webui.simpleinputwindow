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
package jpiere.plugin.simpleinputwindow.window;

import java.text.SimpleDateFormat;

import jpiere.plugin.simpleinputwindow.component.SimpleInputWindowDocumentLink;
import jpiere.plugin.simpleinputwindow.form.JPiereSimpleInputWindow;

import org.adempiere.webui.apps.ProcessModalDialog;
import org.adempiere.webui.theme.ThemeManager;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoLog;
import org.compiere.process.ProcessInfoUtil;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zhtml.Table;
import org.zkoss.zhtml.Td;
import org.zkoss.zhtml.Text;
import org.zkoss.zhtml.Tr;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vlayout;


/**
 * JPiereMatrixWindowProcessModelDialog
 *
 * JPIERE-0111
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class SimpleInputWindowProcessModelDialog extends ProcessModalDialog {

	private JPiereSimpleInputWindow simpleInputWindow;

	private HtmlBasedComponent resultPanelLayout;
	private HtmlBasedComponent messageResultContent;
	private HtmlBasedComponent infoResultContent;

	private Table logMessageTable;

	private int[]		    m_ids = null;

	private boolean isParameterPage = true;

	//Simple Input Window use this constractor only.
	public SimpleInputWindowProcessModelDialog(int WindowNo, ProcessInfo pi,
			boolean autoStart,JPiereSimpleInputWindow simpleInputWindow) {
		super(WindowNo, pi, autoStart);
		this.simpleInputWindow = simpleInputWindow;
	}

	public SimpleInputWindowProcessModelDialog(EventListener<Event> listener,
			int WindowNo, ProcessInfo pi, boolean autoStart) {
		super(listener, WindowNo, pi, autoStart);
	}

	public SimpleInputWindowProcessModelDialog(int WindowNo,
			int AD_Process_ID, int tableId, int recordId, boolean autoStart,JPiereSimpleInputWindow simpleInputWindow) {
		super(WindowNo, AD_Process_ID, tableId, recordId, autoStart );

		this.simpleInputWindow = simpleInputWindow;
	}

	public SimpleInputWindowProcessModelDialog(EventListener<Event> listener,
			int WindowNo, int AD_Process_ID, int tableId, int recordId,
			boolean autoStart) {
		super(listener, WindowNo, AD_Process_ID, tableId, recordId, autoStart);
	}

	@Override
	public void onEvent(Event event)
	{
		super.onEvent(event);

		if(event.getName().equals("onComplete"))
		{
			try{
				simpleInputWindow.onEvent(event);
			}catch(Exception e){
				;
			}
		}
	}

	public HtmlBasedComponent getInfoResultContent()
	{
		return infoResultContent;
	}

	@Override
	public void updateUI() {
		swithToFinishScreen();
	}

	protected void swithToFinishScreen() {
		ProcessInfo pi = getProcessInfo();
		ProcessInfoUtil.setLogFromDB(pi);

		layoutResultPanel (topParameterLayout);

		StringBuilder buildMsg = new StringBuilder(getInitialMessage());
		buildMsg.append("<p><font color=\"").append(pi.isError() ? "#FF0000" : "#0000FF").append("\">** ")
		.append(pi.getSummary())
		.append("</font></p>");

		((Html)messageResultContent).setContent (buildMsg.toString());

		// Add Log info with zoom on record id
		infoResultContent.getChildren().removeAll(infoResultContent.getChildren());
		appendRecordLogInfo(pi.getLogs(), infoResultContent);

		bOK.setLabel(Msg.getMsg(Env.getCtx(), "Parameter"));
		bOK.setImage(ThemeManager.getThemeResource("images/Reset16.png"));

		bCancel.setLabel(Msg.getMsg(Env.getCtx(), "Close"));
		bCancel.setImage(ThemeManager.getThemeResource("images/Cancel16.png"));

		isParameterPage = false;

		m_ids = pi.getIDs();

		//move message div to center to give more space to display potentially very long log info
//		replaceComponent (resultPanelLayout, topParameterLayout);
//		invalidate();
//		Clients.response(new AuEcho(this, "onAfterProcess", null));
	}

	private void layoutResultPanel (HtmlBasedComponent topParameterLayout){
		if (resultPanelLayout == null){
			resultPanelLayout = new Vlayout();
			resultPanelLayout.setSclass("result-parameter-layout");
			resultPanelLayout.setVflex("true");
			// reference for update late
			messageResultContent = setHeadMessage(resultPanelLayout, null);

			infoResultContent = new Div();
			resultPanelLayout.appendChild(infoResultContent);
		}
	}

	protected void replaceComponent(HtmlBasedComponent newComponent, HtmlBasedComponent oldComponent) {
//		oldComponent.getParent().insertBefore(newComponent, oldComponent);
//		oldComponent.detach();
	}

	private void appendRecordLogInfo(ProcessInfoLog[] m_logs, HtmlBasedComponent infoResultContent) {
		if (m_logs == null)
			return;

		SimpleDateFormat dateFormat = DisplayType
				.getDateFormat(DisplayType.Date);

		logMessageTable = new Table();
		logMessageTable.setId("logrecords");
		logMessageTable.setDynamicProperty("border", "1");
		logMessageTable.setDynamicProperty("cellpadding", "0");
		logMessageTable.setDynamicProperty("cellspacing", "0");
		logMessageTable.setDynamicProperty("width", "100%");

		infoResultContent.appendChild(logMessageTable);

		boolean datePresents = false;
		boolean numberPresents = false;
		boolean msgPresents = false;

		for (ProcessInfoLog log : m_logs) {
			if (log.getP_Date() != null)
				datePresents = true;
			if (log.getP_Number() != null)
				numberPresents = true;
			if (log.getP_Msg() != null)
				msgPresents = true;
		}

		for (int i = 0; i < m_logs.length; i++) {

			Tr tr = new Tr();
			logMessageTable.appendChild(tr);

			ProcessInfoLog log = m_logs[i];

			if (datePresents) {
				Td td = new Td();
				if (log.getP_Date() != null) {
					Label label = new Label(dateFormat.format(log.getP_Date()));
					td.appendChild(label);
					// label.setStyle("padding-right:100px");
				}
				tr.appendChild(td);

			}

			if (numberPresents) {

				Td td = new Td();
				if (log.getP_Number() != null) {
					Label labelPno = new Label("" + log.getP_Number());
					td.appendChild(labelPno);
				}
				tr.appendChild(td);
			}

			if (msgPresents) {
				Td td = new Td();
				if (log.getP_Msg() != null) {
					if (log.getAD_Table_ID() > 0 && log.getRecord_ID() > 0) {
						SimpleInputWindowDocumentLink recordLink = new SimpleInputWindowDocumentLink(log.getP_Msg(), log.getAD_Table_ID(), log.getRecord_ID());
						td.appendChild(recordLink);
					} else {
						Text t = new Text();
						t.setEncode(false);
						t.setValue(log.getP_Msg());
						td.appendChild(t);
					}
				}
				tr.appendChild(td);
			}
		}
		//messageDiv.appendChild(logMessageTable);
	}

}
