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
package jpiere.plugin.simpleinputwindow.factory;

import java.util.logging.Level;

import jpiere.plugin.simpleinputwindow.form.AbstractSimpleInputWindowForm;

import org.adempiere.webui.factory.IFormFactory;
import org.adempiere.webui.panel.ADForm;
import org.compiere.util.CLogger;


/**
 *  JPiere Plugin Simple Input Window Form Factory
 *
 *  JPIERE-0111
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPierePluginSimpleInputWindowFormFactory implements IFormFactory{

	private static final CLogger log = CLogger.getCLogger(JPierePluginSimpleInputWindowFormFactory.class);

	@Override
	public ADForm newFormInstance(String formName) {
		Object form = null;
	     if (formName.startsWith("JP_SimpleInputWindow_ID="))
	     {

	    	String JP_SimpleInputWindow_ID = formName.substring("JP_SimpleInputWindow_ID=".length());

	       	ClassLoader loader = this.getClass().getClassLoader();
	       	Class<?> clazz = null;

			try
    		{
    			//	Create instance w/o parameters
        		clazz = loader.loadClass("jpiere.plugin.simpleinputwindow.form.JPiereSimpleInputWindow");
    		}
    		catch (Exception e)
    		{
    			if (log.isLoggable(Level.INFO))
    				log.log(Level.INFO, e.getLocalizedMessage(), e);
    		}


			if (clazz != null) {
				try
	    		{
	    			form = clazz.newInstance();
	    		}
	    		catch (Exception e)
	    		{
	    			if (log.isLoggable(Level.WARNING))
	    				log.log(Level.WARNING, e.getLocalizedMessage(), e);
	    		}
			}

		      if (form != null) {
					if (form instanceof AbstractSimpleInputWindowForm )
					{
						AbstractSimpleInputWindowForm  controller = (AbstractSimpleInputWindowForm) form;
						controller.createSimpleInputWindow(JP_SimpleInputWindow_ID);
						ADForm adForm = controller.getForm();
						adForm.setICustomForm(controller);
						return adForm;
					}
		     }
	     }
	     return null;
	}


}
