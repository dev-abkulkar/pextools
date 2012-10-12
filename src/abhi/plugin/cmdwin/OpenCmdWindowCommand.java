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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;

import abhi.plugin.util.Utils;

@SuppressWarnings("restriction")
public class OpenCmdWindowCommand extends AbstractHandler {
	private String commandId = null;
	private static final String OPEN_COMMAND_WINDOW = "abhi.plugin.cmdwin.commands.opencmdwin";
	private static final String OPEN_FOLDER_WINDOW = "abhi.plugin.cmdwin.commands.openfolder";
	private Shell shell = null; 
	private ExecutionEvent event = null;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		this.event = event;
		this.commandId = event.getCommand().getId();
		this.shell = HandlerUtil.getActiveShell(event);
		
		try{
			IPath locationPath = locatePathToResource();
			
			if(locationPath != null)
				new Thread(new OpenCmdWindowThread(locationPath.toString())).start();
			else
				MessageDialog.openInformation(shell, "Hi","Please select a folder/file ");	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private IPath locatePathToResource() throws ExecutionException{
		IPath path = Utils.retrievePathToResourceFromPackageExplorer(event);
		if(path == null){
			path = Utils.retrieveParentPath(event); 
		}
		return path;
	}

	class OpenCmdWindowThread implements Runnable {
		String absPath = null;
		String[] cmdWindow = null;
		
		OpenCmdWindowThread(String absPath) {
			
			this.absPath = absPath;
			
			if(OPEN_COMMAND_WINDOW.equals(commandId))
				this.cmdWindow = getStringForOpenCommandWindow();
			else if(OPEN_FOLDER_WINDOW.equals(commandId))
				this.cmdWindow = getStringForOpenFolderWindow();
		}

		@Override
		public void run() {
			if(cmdWindow == null){
				MessageDialog.openError(shell, "Sorry!", "This feature is unavailable on your Operating System");
			}
			try {
				Runtime.getRuntime().exec(cmdWindow);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		public String[] getStringForOpenCommandWindow(){
			if(Platform.getOS().equals(Platform.OS_WIN32)){
				
				return new String[] { "cmd.exe", "/C","\"start; cd /D " + absPath + "\"" };
			//>cmd.exe /C "start; cd /D E:/wsapce/Test"
			}else if(Platform.getOS().equals(Platform.OS_LINUX)){
				
				return new String[] {"gnome-terminal","--working-directory="+absPath};
			}
			
			return null;
		}
		
		public String[] getStringForOpenFolderWindow(){
			
			if(Platform.getOS().equals(Platform.OS_WIN32)){
				
				return new String[]{"explorer.exe",absPath.replace('/', '\\')};
			
			}else if(Platform.getOS().equals(Platform.OS_LINUX)){
				
				return new String[] {"nautilus",absPath};
			}
			
			return null;
			
		}
	}
	
}
