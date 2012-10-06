package abhi.plugin.cmdwin;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

@SuppressWarnings("restriction")
public class OpenCmdWindowCommand extends AbstractHandler {
	private String commandId = null;
	private static final String OPEN_COMMAND_WINDOW = "abhi.plugin.cmdwin.commands.opencmdwin";
	private static final String OPEN_FOLDER_WINDOW = "abhi.plugin.cmdwin.commands.openfolder";
	
	
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if(Platform.getOS().equals(Platform.OS_WIN32)){
		commandId = event.getCommand().getId();
		Shell shell = HandlerUtil.getActiveShell(event);
		//MessageDialog.openInformation(shell, "COMMAND ID",commandId );
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		IStructuredSelection selection = (IStructuredSelection) sel;
		IPath locationPath =null;
		Object firstElement = selection.getFirstElement();
		
		try {
			if(firstElement instanceof JarPackageFragmentRoot){
				
				locationPath= ((JarPackageFragmentRoot)firstElement).getPath();
				locationPath = locationPath.removeLastSegments(1);
				
			}else if (firstElement instanceof IFolder || firstElement instanceof IProject || firstElement instanceof IWorkspaceRoot ) {
				locationPath = ((IResource) firstElement).getLocation();

			}else if(firstElement instanceof IFile){
				
				IContainer fileParent = ((IFile) firstElement).getParent();
				if(fileParent!=null)
					locationPath = fileParent.getLocation();
				
			}
			else if (firstElement instanceof IJavaProject) {
				 locationPath = ((IJavaProject) firstElement).getResource()
						.getLocation();


			} else if (firstElement instanceof IPackageFragmentRoot && !(firstElement instanceof JarPackageFragmentRoot)) {
				IResource resource = ((IPackageFragmentRoot) firstElement)
						.getResource();
				if(resource!= null){
					locationPath = resource.getLocation();
				}

			} else if (firstElement instanceof IPackageFragment ) {
				IResource resource = ((IPackageFragment) firstElement).getResource();
				if(resource!= null){
					locationPath = resource.getLocation();
				}

			}else if (firstElement instanceof ICompilationUnit) {
				locationPath = ((ICompilationUnit) firstElement).getResource().getParent().getLocation();
			} 
			
			if(locationPath != null)
				new Thread(new OpenCmdWindowThread(locationPath.toString())).start();
			else {
 				MessageDialog.openInformation(shell, "Hi","Please select a folder/file: " + firstElement.getClass());
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
		return null;
	}

	class OpenCmdWindowThread implements Runnable {
		String absPath = null;
		String[] cmdWindow = null;
		
		OpenCmdWindowThread(String absPath) {
			this.absPath = absPath;
			if(OPEN_COMMAND_WINDOW.equals(commandId))
				this.cmdWindow = new String[] { "cmd.exe", "/C","\"start; cd " + absPath + "\"" };
			else if(OPEN_FOLDER_WINDOW.equals(commandId))
				this.cmdWindow = new String[]{"explorer.exe",absPath.replace('/', '\\')};
		}

		@Override
		public void run() {
			try {
				Runtime.getRuntime().exec(cmdWindow);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		};
	}
	
}
