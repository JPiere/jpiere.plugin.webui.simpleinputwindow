package jpiere.plugin.simpleinputwindow.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import javax.swing.table.AbstractTableModel;

import org.compiere.model.DataStatusEvent;
import org.compiere.model.DataStatusListener;
import org.compiere.model.GridField;
import org.compiere.model.PO;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.MSort;

public class SimpleInputWindowGridTable extends AbstractTableModel {

	/** Keep track of last sorted column index and sort direction */
	private int 				m_lastSortColumnIndex = -1;
	private boolean 			m_lastSortedAscending = true;

	private Properties          m_ctx;
	private int					m_AD_Table_ID;
	private int				    m_WindowNo;

	private volatile ArrayList<MSort>		m_sort = new ArrayList<MSort>(100);

	/**	Rowcount                    */
	private int				    m_rowCount = 0;
	/**	Has Data changed?           */
	private boolean			    m_changed = false;
	/** Index of changed row via SetValueAt */
	private int				    m_rowChanged = -1;
	/** Insert mode active          */
	private boolean			    m_inserting = false;

	private ArrayList<PO> list_POs;

	private GridField[] gridFields;

	public SimpleInputWindowGridTable() {
	}

	@Override
	public int getRowCount() {
		return list_POs.size();
	}

	@Override
	public int getColumnCount() {
		return gridFields.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		GridField gf = gridFields[columnIndex];
//		Object obj = POs.get(rowIndex).get_Value(gf.getColumnName());
		return list_POs.get(rowIndex).get_Value(gf.getColumnName());
	}

	public Object getValueAt (int rowIndex, GridField gridField)
	{
		return list_POs.get(rowIndex).get_Value(gridField.getColumnName());
	}

	public Object getValueAt (int rowIndex, String columnName)
	{
		return list_POs.get(rowIndex).get_Value(columnName);
	}

	public ArrayList<PO>  getPOs(){
		return list_POs;
	}

	public PO getPO(int rowIndex){

		return list_POs.get(rowIndex);
	}

	public void removePO(int rowIndex){
		list_POs.remove(rowIndex);
	}

	public void setPO(PO po)
	{
		list_POs.add(po);
	}

	public GridField[] getFields()
	{
		return gridFields;
	}

	public void init(ArrayList<PO> POs, GridField[] gridFields)
	{
		this.list_POs=POs;
		this.gridFields=gridFields;
	}

	/**
	 * 	Set Value in data and update GridField.
	 *  (called directly or from JTable.editingStopped())
	 *
	 *  @param  value value to assign to cell
	 *  @param  row row index of cell
	 *  @param  col column index of cell
	 */
	public void setValueAt (Object value, int rowIndex, GridField gridField)
	{
		list_POs.get(rowIndex).set_ValueNoCheck(gridField.getColumnName(), value);
	}	//	setValueAt

	/**
	 *	Get Column at index
	 *  @param index index
	 *  @return GridField
	 */
	protected GridField getField (int index)
	{
		if (index < 0 || index >= gridFields.length)
			return null;
		return gridFields[index];
	}	//	getColumn

	/**
	 * Returns a column given its name.
	 *
	 * @param columnName string containing name of column to be located
	 * @return the column index with <code>columnName</code>, or -1 if not found
	 */
	public int findColumn (String columnName)
	{
		for (int i = 0; i < gridFields.length; i++)
		{
			GridField field = gridFields[i];
			if (columnName.equals(field.getColumnName()))
				return i;
		}
		return -1;
	}   //  findColumn

	/**
	 *	Sort Entries by Column.
	 *  actually the rows are not sorted, just the access pointer ArrayList
	 *  with the same size as m_buffer with MSort entities
	 *  @param col col
	 *  @param ascending ascending
	 */
	public void sort (int col, boolean ascending)
	{
//		if (log.isLoggable(Level.INFO)) log.info("#" + col + " " + ascending);

		if (col < 0) {
			return;
		}
		if (getRowCount() == 0)
			return;

		boolean isSameSortEntries = (col == m_lastSortColumnIndex && ascending == m_lastSortedAscending);
		if (!isSameSortEntries)
		{
			m_lastSortColumnIndex = col;
			m_lastSortedAscending = ascending;
		}

		//cache changed row
		Object[] changedRow = m_rowChanged >= 0 ? getDataAtRow(m_rowChanged) : null;

		GridField field = getField (col);
		//	RowIDs are not sorted
		if (field.getDisplayType() == DisplayType.RowID)
			return;
		boolean isLookup = DisplayType.isLookup(field.getDisplayType());
		boolean isASI = DisplayType.PAttribute == field.getDisplayType();

		//	fill MSort entities with data entity
		for (int i = 0; i < m_sort.size(); i++)
		{
			MSort sort = (MSort)m_sort.get(i);
			Object[] rowData = getDataAtRow(i);
			if (rowData[col] == null)
				sort.data = null;
			else if (isLookup || isASI)
				sort.data = field.getLookup().getDisplay(rowData[col]);	//	lookup
			else
				sort.data = rowData[col];								//	data
		}
//		if (log.isLoggable(Level.INFO)) log.info(field.toString() + " #" + m_sort.size());

		//	sort it
		MSort sort = new MSort(0, null);
		sort.setSortAsc(ascending);
		Collections.sort(m_sort, sort);
//		if (m_virtual)
//		{
//			Object[] newRow = m_virtualBuffer.get(NEW_ROW_ID);
//			m_virtualBuffer.clear();
//			if (newRow != null && newRow.length > 0)
//				m_virtualBuffer.put(NEW_ROW_ID, newRow);
//
			if (changedRow != null && changedRow.length > 0)
			{
//				if (changedRow[m_indexKeyColumn] != null && (Integer)changedRow[m_indexKeyColumn] > 0)
//				{
//					m_virtualBuffer.put((Integer)changedRow[m_indexKeyColumn], changedRow);
//					for(int i = 0; i < m_sort.size(); i++)
//					{
//						if (m_sort.get(i).index == (Integer)changedRow[m_indexKeyColumn])
//						{
//							m_rowChanged = i;
//							break;
//						}
//					}
//				}
			}

			//release sort memory
			for (int i = 0; i < m_sort.size(); i++)
			{
				m_sort.get(i).data = null;
			}
//		}

		if (!isSameSortEntries)
		{
			//	update UI
			fireTableDataChanged();
			//  Info detected by MTab.dataStatusChanged and current row set to 0
			fireDataStatusIEvent("Sorted", "#" + m_sort.size());
		}
	}	//	sort

	public void waitLoadingForRow(int row) {
		//	need to wait for data read into buffer
//		int loops = 0;
		//wait for [timeout] seconds
//		int timeout = MSysConfig.getIntValue(MSysConfig.GRIDTABLE_LOAD_TIMEOUT_IN_SECONDS, 30, Env.getAD_Client_ID(Env.getCtx()));
//		while (row >= m_sort.size() && m_loaderFuture != null && !m_loaderFuture.isDone() && loops < timeout)
//		{
//			if (log.isLoggable(Level.FINE)) log.fine("Waiting for loader row=" + row + ", size=" + m_sort.size());
//			try
//			{
//				m_loaderFuture.get(1000, TimeUnit.MILLISECONDS);
//			}
//			catch (Exception ie)
//			{}
//			loops++;
//		}
//		if (m_sort.size() == 0) {
//			// check if there is a DB error saved to show
//			Exception savedEx = CLogger.retrieveException();
//			if (savedEx != null)
//				throw new IllegalStateException(savedEx);
//		}
//		if (row >= m_sort.size()) {
//			log.warning("Reached " + timeout + " seconds timeout loading row " + (row+1) + " for SQL=" + m_SQL);
//			throw new IllegalStateException("Timeout loading row " + (row+1));
//		}
	}

	private Object[] getDataAtRow(int row)
	{
		return getDataAtRow(row, true);
	}

	private Object[] getDataAtRow(int row, boolean fetchIfNotFound)
	{
//		waitLoadingForRow(row);
//		MSort sort = (MSort)m_sort.get(row);
//		Object[] rowData = null;
//		if (m_virtual)
//		{
//			if (sort.index != NEW_ROW_ID && !(m_virtualBuffer.containsKey(sort.index)) && fetchIfNotFound)
//			{
//				fillBuffer(row, DEFAULT_FETCH_SIZE);
//			}
//			rowData = (Object[])m_virtualBuffer.get(sort.index);
//		}
//		else
//		{
//			rowData = (Object[])m_buffer.get(sort.index);
//		}
		return null;
	}

	/**
	 *  Create and fire Data Status Info Event
	 *  @param AD_Message message
	 *  @param info additional info
	 */
	protected void fireDataStatusIEvent (String AD_Message, String info)
	{
		DataStatusEvent e = createDSE();
		e.setInfo(AD_Message, info, false,false);
		fireDataStatusChanged (e);
	}   //  fireDataStatusEvent

	private void fireDataStatusChanged (DataStatusEvent e)
	{
		DataStatusListener[] listeners = listenerList.getListeners(DataStatusListener.class);
        for (int i = 0; i < listeners.length; i++)
        	listeners[i].dataStatusChanged(e);
	}	//	fireDataStatusChanged

	/**
	 *  Create Data Status Event
	 *  @return data status event
	 */
	private DataStatusEvent createDSE()
	{
		boolean changed = m_changed;
		if (m_rowChanged != -1)
			changed = true;
		DataStatusEvent dse = new DataStatusEvent(this, m_rowCount, changed,
			Env.isAutoCommit(m_ctx, m_WindowNo), m_inserting);
		dse.AD_Table_ID = m_AD_Table_ID;
		dse.Record_ID = null;
		return dse;
	}   //  createDSE
}
