/**
 * 
 */
package abhi.plugin.util;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Abhinandan
 *
 * 
 */
public class Utils {

	
	public static IFile retrieveFileFromActiveWorkBench(ExecutionEvent event) throws ExecutionException {
		IEditorInput editorInput = HandlerUtil.getActiveEditorInputChecked(event);
		IFile file = editorInput instanceof FileEditorInput?((FileEditorInput) editorInput).getFile():null;
		return file;
	}
	
	/* 
	 * Returns the path of the parent of the file 
	 * for opening the folder or the command window
	 */
	public static IPath retrieveParentPath(ExecutionEvent event) throws ExecutionException {
		IFile file = retrieveFileFromActiveWorkBench(event);
		IPath path = file.getParent().getLocation();
		/*if(path != null)
			System.out.println(path.toString());*/
		return path;
	}
	
	public static IPath retrieveFilePath(ExecutionEvent event) throws ExecutionException {
		IFile file = retrieveFileFromActiveWorkBench(event);
		IPath path = file.getLocation();
		/*if(path != null)
			System.out.println(path.toString());*/
		
		return path;
	}
	
	/* 
	 * Returns the path of the parent of the file 
	 * for opening the folder or the command window
	 */
	public static IPath retrievePathToResourceFromPackageExplorer(ExecutionEvent event){
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		IPath locationPath =null;
		
		if(sel!=null){
		
			IStructuredSelection selection = (IStructuredSelection) sel;
			
			Object firstElement = selection.getFirstElement();
			
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
					 locationPath = ((IJavaProject) firstElement).getResource().getLocation();
	
	
				} else if (firstElement instanceof IPackageFragmentRoot && !(firstElement instanceof JarPackageFragmentRoot)) {
					IResource resource = ((IPackageFragmentRoot) firstElement).getResource();
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
					
				}/*else {
					
	 				MessageDialog.openInformation(shell, "Hi","Please select a folder/file: " + firstElement.getClass());
	 				
				}*/
			}
			return locationPath;

	}

}
