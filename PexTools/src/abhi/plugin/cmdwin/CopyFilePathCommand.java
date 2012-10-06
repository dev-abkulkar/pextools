package abhi.plugin.cmdwin;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
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

@SuppressWarnings("restriction")
public class CopyFilePathCommand extends AbstractHandler {

	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if(Platform.getOS().equals(Platform.OS_WIN32)){
		Shell shell = HandlerUtil.getActiveShell(event);
		// MessageDialog.openInformation(shell, "COMMAND ID",commandId );
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		IStructuredSelection selection = (IStructuredSelection) sel;
		IPath locationPath = null;
		
		
		
		Object firstElement = selection.getFirstElement();
		if(firstElement instanceof JarPackageFragmentRoot){
			locationPath= ((JarPackageFragmentRoot)firstElement).getPath();
		}
		if (firstElement instanceof IResource) {
			locationPath = ((IResource) firstElement).getLocation();
			
		} else if (firstElement instanceof IAdaptable) {
			IResource resource = (IResource) ((IAdaptable)firstElement).getAdapter(IResource.class);
			if(resource!=null){
				locationPath = resource.getLocation();
					
			}
		} else {
			MessageDialog.openInformation(shell, "Hi",
					"You have opened an : \n" + firstElement.getClass());

		}
		if(locationPath!=null)
			ClipBoardUtil.copy(locationPath.toString());
		}
		return null;
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
