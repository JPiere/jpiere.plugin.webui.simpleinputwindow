package jpiere.plugin.simpleinputwindow.form;

import org.zkoss.zul.Grid;

public class SimpleInputWindowGridView {

	private SimpleInputWindowGridTable simpleInputWindowGridTable;
	private SimpleInputWindowListModel listModel;
	private SimpleInputWindowGridRowRenderer renderer;
	private Grid grid;

	public SimpleInputWindowGridView(SimpleInputWindowGridTable SIWGridTable ,SimpleInputWindowListModel listModel
			,SimpleInputWindowGridRowRenderer renderer, Grid grid) {
		this.simpleInputWindowGridTable = SIWGridTable;
		this.listModel=listModel;
		this.renderer = renderer;
		this.grid = grid;
	}

	public SimpleInputWindowGridTable getSimpleInputWindowGridTable()
	{
		return simpleInputWindowGridTable;
	}

	public SimpleInputWindowListModel getSimpleInputWindowListModel()
	{
		return listModel;
	}

	public SimpleInputWindowGridRowRenderer getSimpleInputWindowGridRowRenderer()
	{
		return renderer;
	}

	public Grid getGrid()
	{
		return grid;
	}

}
