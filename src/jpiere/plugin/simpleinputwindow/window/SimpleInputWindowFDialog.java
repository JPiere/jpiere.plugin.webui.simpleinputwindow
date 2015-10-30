package jpiere.plugin.simpleinputwindow.window;

import java.util.Properties;
import java.util.logging.Level;

import jpiere.plugin.simpleinputwindow.component.SimpleInputWindowMessagebox;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Messagebox;
import org.adempiere.webui.window.FDialog;
import org.compiere.util.CLogMgt;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trace;
import org.zkoss.zk.ui.HtmlBasedComponent;

public class SimpleInputWindowFDialog extends FDialog {

	/**	Logger			*/
    private static final CLogger logger = CLogger.getCLogger(SimpleInputWindowFDialog.class);

	public static void info(int windowNo, HtmlBasedComponent htmlLog, String adMessage, String message, String title)
    {
		Properties ctx = Env.getCtx();

		StringBuffer out = new StringBuffer();

		if (logger.isLoggable(Level.INFO)) logger.info(adMessage + " - " + message);

		if (CLogMgt.isLevelFinest())
		{
		    Trace.printStack();
		}

		out = constructMessage(adMessage, message);

		String newTitle;

		if (title == null)
			newTitle = AEnv.getDialogHeader(ctx, windowNo);
		else
			newTitle = title;

		String s = out.toString().replace("\n", "<br>");

		SimpleInputWindowMessagebox msg = new SimpleInputWindowMessagebox();

		msg.show(s, newTitle,  Messagebox.OK, Messagebox.INFORMATION, null, false, htmlLog);

		return;
    }

    /**
     * Construct a message from the AD_Message and the additional message
     *
     * @param adMessage	AD_Message string
     * @param message	additional message
     * @return The translated AD_Message appended with the additional message
     */

    private static StringBuffer constructMessage(String adMessage, String message)
	{
		StringBuffer out = new StringBuffer();

		if (adMessage != null && !adMessage.equals(""))
		{
			out.append(Msg.getMsg(Env.getCtx(), adMessage));
		}

		if (message != null && message.length() > 0)
		{
			out.append("<br>").append(message);
		}

		return out;
	}

}
