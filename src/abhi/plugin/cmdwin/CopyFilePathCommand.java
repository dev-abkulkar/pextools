package abhi.plugin.cmdwin;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import abhi.plugin.util.Utils;

@SuppressWarnings("restriction")
public class CopyFilePathCommand extends AbstractHandler {

	private ExecutionEvent event = null;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		this.event =  event;
		Shell shell = HandlerUtil.getActiveShell(event);
		IPath locationPath = locatePathToResource();	
		if(locationPath!=null)
			ClipBoardUtil.copy(locationPath.toString());
		else
			MessageDialog.openInformation(shell, "Hi","Please select a folder/file ");
		
		return null;
	}
	
	private IPath locatePathToResource() throws ExecutionException{
		IPath path = null;
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		if (sel != null) {
			IStructuredSelection selection = (IStructuredSelection) sel;

			Object firstElement = selection.getFirstElement();
			if (firstElement instanceof JarPackageFragmentRoot) {
				path = ((JarPackageFragmentRoot) firstElement).getPath();
			}
			if (firstElement instanceof IResource) {
				path = ((IResource) firstElement).getLocation();
			} else if (firstElement instanceof IAdaptable) {
				IResource resource = (IResource) ((IAdaptable) firstElement).getAdapter(IResource.class);
				if (resource != null) {
					path = resource.getLocation();
				}
			}
		}
		
		if (path == null) {
			path = Utils.retrieveFilePath(event);
		}
		
		return path;
	}
	
	static class ClipBoardUtil {
		public static void copy(String text) {
			Clipboard clipboard = new Clipboard(Display.getCurrent());
			TextTransfer textTransfer = TextTransfer.getInstance();

			Transfer[] transfers = new Transfer[] { textTransfer };
			Object[] data = new Object[] { text };
			clipboard.setContents(data, transfers);
		}
	}

}
