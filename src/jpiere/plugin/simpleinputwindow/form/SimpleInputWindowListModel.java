/******************************************************************************
 * Copyright (C) 2008 Low Heng Sin                                            *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package jpiere.plugin.simpleinputwindow.form;

import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.util.SortComparator;
import org.compiere.model.GridField;
import org.compiere.model.PO;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelExt;
import org.zkoss.zul.ListitemComparator;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.ext.Sortable;

/**
 *
 * @author Low Heng Sin
 *
 */
public class SimpleInputWindowListModel extends AbstractListModel<Object> implements TableModelListener, Sortable<Object> {

	/**
	 *
	 */
	private static final long serialVersionUID = 698185856751242764L;
	private SimpleInputWindowGridTable tableModel;
	@SuppressWarnings("unused")
	private GridField[] gridField;
	@SuppressWarnings("unused")
	private int windowNo;

	private int pageSize = -1;
	private int pageNo = 0;

	private boolean editing = false;

	private ArrayList<Integer> selection = null;

	/**
	 *
	 * @param tableModel
	 * @param windowNo
	 */
	public SimpleInputWindowListModel(SimpleInputWindowGridTable tableModel, int windowNo) {
		this.tableModel = tableModel;
		this.windowNo = windowNo;
		gridField = tableModel.getFields();
		tableModel.addTableModelListener(this);

		selection = new ArrayList<Integer>();
	}

	/**
	 * @param rowIndex
	 * @see ListModel#getElementAt(int)
	 */
	public Object getElementAt(int rowIndex) {
		int columnCount = tableModel.getColumnCount();
		Object[] values = new Object[columnCount];
		if (pageSize > 0) {
			rowIndex = (pageNo * pageSize) + rowIndex;
		}
		if (rowIndex < tableModel.getRowCount()) {
			for (int i = 0; i < columnCount; i++) {
				values[i] = tableModel.getValueAt(rowIndex, i);
			}
		}

		return values;
	}

	public PO getPO(int rowIndex){
		int columnCount = tableModel.getColumnCount();
		Object[] values = new Object[columnCount];
		if (pageSize > 0) {
			rowIndex = (pageNo * pageSize) + rowIndex;
		}
		if (rowIndex < tableModel.getRowCount()) {
			return tableModel.getPOs().get(rowIndex);
		}

		return null;
	}

	public void setPO(PO po){
		tableModel.setPO(po);
//		WTableModelEvent tcEvent = new WTableModelEvent(this, 0, 0);//TODO:要実装
//		fireTableChange(tcEvent);
	}

	public void setValueAt (Object value, int row, GridField gridField)
	{
		tableModel.setValueAt(value, row, gridField);
//		WTableModelEvent tcEvent = new WTableModelEvent(this, 0, 0);//TODO:要実装
//		fireTableChange(tcEvent);
	}	//	setValueAt

	public Object getValueAt(int rowIndex, GridField gridField) {

		return tableModel.getValueAt(rowIndex, gridField);
	}

	public Object getValueAt(int rowIndex, String columnName) {
		return tableModel.getValueAt(rowIndex, columnName);
	}

	public void addToSelection(int rowIndex) {
		if (!selection.contains(rowIndex))
			selection.add(rowIndex);
	}

	public boolean removeFromSelection(int rowIndex) {
		return selection.remove((Integer)rowIndex);
	}

	public int[] getSelections()
	{
		int[] selected = new int[selection.size()];
		int i = 0;
		for(Integer row : selection)
		{
			selected[i++] = row.intValue();
		}
		return selected;
	}

	/**
	 * set current page no ( starting from 0 )
	 * @param pg
	 */
	public void setPage(int pg) {
		if (pageNo != pg) {
			if (pg > 0) {
				int start = pg * pageSize;
				if (start >= tableModel.getRowCount()) {
					return;
				}
			}
			pageNo = pg;
			fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
		}
	}

	/**
	 * @return current page no ( starting from 0 )
	 */
	public int getPage() {
		return pageNo;
	}

	/**
	 * Set number of rows per page
	 * @param pgSize
	 */
	public void setPageSize(int pgSize) {
		pageSize = pgSize;
	}

	/**
	 * Get number of rows per page
	 * @return pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Get total number of rows
	 * @return int
	 * @see ListModel#getSize()
	 */
	public int getSize() {
		int total = tableModel.getRowCount();
		if (pageSize <= 0)
			return total;
		else if ((total - ( pageNo * pageSize)) < 0) {
			pageNo = 0;
			return pageSize > total ? total : pageSize;
		} else {
			int end = (pageNo + 1) * pageSize;
			if (end > total)
				return total - ( pageNo * pageSize);
			else
				return pageSize;
		}
	}

	/**
	 * Request components that attached to this model to re-render a row.
	 * @param row
	 */
	public void updateComponent(int row) {
		updateComponent(row, row);
	}

	/**
	 * Request components that attached to this model to re-render a range of row.
	 * @param fromRow
	 * @param toRow
	 */
	public void updateComponent(int fromRow, int toRow) {
		//must run from the UI thread
		if (Executions.getCurrent() != null) {
			fireEvent(ListDataEvent.CONTENTS_CHANGED, fromRow, toRow);
		}
	}

	/**
	 * @param cmpr
	 * @param ascending
	 * @see ListModelExt#sort(Comparator, boolean)
	 */
	public void sort(Comparator<Object> cmpr, boolean ascending) {
		//use default zk comparator
		if (cmpr instanceof ListitemComparator) {
			ListitemComparator lic = (ListitemComparator) cmpr;
			tableModel.sort(lic.getListheader().getColumnIndex(), ascending);
		} else if (cmpr instanceof SortComparator) {
			SortComparator sc = (SortComparator)cmpr;
			tableModel.sort(sc.getColumnIndex(), ascending);
		}
		fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
	}

	protected ArrayList<WTableModelListener> m_listeners = new ArrayList<WTableModelListener>();

	/**
	 * Send the specified <code>event</code> to all listeners.
	 *
	 * @param event	The event tofire
	 */
	private void fireTableChange(WTableModelEvent event)
	{
	    for (WTableModelListener listener : m_listeners)
	    {
	       listener.tableChanged(event);
	    }

	    return;
	}

	/**
	 * Add a listener for events from the data model.
     *
     * The listener will only be added if it doesn't already exist.
	 *
	 * @param listener	A listener for changes in the table mode
	 */
	public void addTableModelListener(WTableModelListener listener)
	{
	    if (listener == null)
	    {
	    	return;
	    }

        if (!m_listeners.contains(listener))
        {
            m_listeners.add(listener);
        }

	    return;
	}

	/**
	 * @param e
	 * @see TableModelListener#tableChanged(TableModelEvent)
	 */
	public void tableChanged(TableModelEvent e) {
		if (Executions.getCurrent() != null) {
			if (e.getType() == TableModelEvent.DELETE)
			{
				if (pageSize > 0)
				{
					int pgIndex = e.getFirstRow() % pageSize;
					fireEvent(ListDataEvent.CONTENTS_CHANGED, pgIndex, getSize());
				}
				else
					fireEvent(ListDataEvent.INTERVAL_REMOVED, e.getFirstRow(), e.getLastRow());
			}
			else if (e.getType() == TableModelEvent.INSERT)
			{
				if (pageSize > 0)
				{
					int pgIndex = e.getFirstRow() % pageSize;
					fireEvent(ListDataEvent.CONTENTS_CHANGED, pgIndex, getSize());
				}
				else
					fireEvent(ListDataEvent.INTERVAL_ADDED, e.getFirstRow(), e.getLastRow());
			}
			else if (e.getLastRow() == Integer.MAX_VALUE)
			{
				fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
			}
			else
			{
				if (!editing)
					fireEvent(ListDataEvent.CONTENTS_CHANGED, e.getFirstRow(), e.getLastRow());
			}
		}
	}

	/**
	 * @param b
	 */
	public void setEditing(boolean b) {
		editing = b;
	}

	@Override
	public String getSortDirection(Comparator<Object> cmpr) {
		return "natural";
	}

}
