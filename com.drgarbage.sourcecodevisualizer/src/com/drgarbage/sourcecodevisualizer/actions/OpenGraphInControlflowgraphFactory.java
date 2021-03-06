/**
 * Copyright (c) 2008-2012, Dr. Garbage Community
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.drgarbage.sourcecodevisualizer.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.ICompilationUnitDocumentProvider;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.drgarbage.core.ActionUtils;
import com.drgarbage.core.CoreMessages;
import com.drgarbage.core.CorePlugin;
import com.drgarbage.core.IExternalCommunication;
import com.drgarbage.core.img.CoreImg;
import com.drgarbage.sourcecodevisualizer.SourcecodeVisualizerMessages;
import com.drgarbage.sourcecodevisualizer.SourcecodeVisualizerPlugin;
import com.drgarbage.sourcecodevisualizer.editors.JavaCodeEditor;
import com.drgarbage.utils.Messages;

/**
 * Creates a diagram and open the control flow graph editor 
 * from Controlflow Factory Plugin.
 *
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id: OpenGraphInControlflowgraphFactory.java 1523 2012-04-13 14:34:24Z Sergej Alekseev $
 */
public class OpenGraphInControlflowgraphFactory extends RetargetAction {

	/**
	 * Text constants from preferences.
	 */
	public static final String ID = "com.drgarbage.sourcecodevizualizer.actions.createbytecodegraph";
	private static final String text = SourcecodeVisualizerMessages.Sourcecodevisualizer_CreateBytecodeGraphAction_Text;
	private static final String toolTipText = SourcecodeVisualizerMessages.Sourcecodevisualizer_CreateBytecodeGraphAction_TooltipText;
	
	/**
	 * Active class file editor.
	 */
	private JavaCodeEditor editor = null;
	
	/**
	 * String of the Constructor.
	 */
	private static final String constructorString = "<init>";
	
	/**
	 * Constructor.
	 * @param editor
	 * @param classFileDocument
	 */
	public OpenGraphInControlflowgraphFactory(JavaCodeEditor editor){
		super(ID, text);
		this.editor = editor;
		setToolTipText(toolTipText);	
		setImageDescriptor(CoreImg.createBytecodeGraphIcon_16x16);
	}
	
    /*
     * (non-Javadoc) Method declared on IAction.
     */
    public void run() {
    	
    	IExternalCommunication comunicationObject =  CorePlugin.getExternalCommunication();
    	if(comunicationObject == null){
    		String msg = CoreMessages.ERROR_Opening_Graph_in_CFGF_failed
    				+'\n'
    				+ CoreMessages.ERROR_CFGF_is_not_installed;
    		Messages.error(msg);
    		return;
    	}
    		
    	IMethod method = editor.getSelectedMethod();
    	
    	if(method == null){
    		String errorMsg = CoreMessages.ERROR_Opening_Graph_in_CFGF_failed
    				+ '\n'
    				+ "The selected method object is null.";
    		IStatus s = new Status(IStatus.ERROR, SourcecodeVisualizerPlugin.PLUGIN_ID, errorMsg, null);
    		CorePlugin.log(s);
    		
    		String msg = CoreMessages.ERROR_Opening_Graph_in_CFGF_failed 
    				+ '\n'
    				+ CoreMessages.ExceptionAdditionalMessage;
    		Messages.error(msg);
    		return;   		
    	}

    	String[] classPath = null;
		String packageName = null;
		String className = null;
		String methodName = null;
		String methodSig = null;
		
        IDocumentProvider p = editor.getDocumentProvider();
		if (p instanceof ICompilationUnitDocumentProvider) {
			ICompilationUnitDocumentProvider cp= (ICompilationUnitDocumentProvider) p;
			ICompilationUnit unit = cp.getWorkingCopy(editor.getEditorInput());                   
			IJavaProject jp = unit.getJavaProject();

			try {
				/* get class path */
				classPath = JavaRuntime.computeDefaultRuntimeClassPath(jp);

				/* get package name and classes */
				IJavaElement[] elements = unit.getChildren();
				for(IJavaElement element: elements){
					if(element.getElementType() == IJavaElement.PACKAGE_DECLARATION){
						packageName = element.getElementName();
						break;
					}
				}

			} catch (JavaModelException e) {
				String errorMsg = CoreMessages.ERROR_Opening_Graph_in_CFGF_failed
						+ '\n'
						+ "Can not get packege or class name.";
				IStatus s = new Status(IStatus.ERROR, SourcecodeVisualizerPlugin.PLUGIN_ID, errorMsg, e);
				CorePlugin.log(s);

				String msg = CoreMessages.ERROR_Opening_Graph_in_CFGF_failed 
						+ '\n'
						+ CoreMessages.ExceptionAdditionalMessage;
				Messages.error(msg);
				
	    		return;
	    		
			} catch (CoreException e) {
				String errorMsg = CoreMessages.ERROR_Opening_Graph_in_CFGF_failed
	    				+ '\n'
	    				+ "Can not compute runtime class path.";
				IStatus s = new Status(IStatus.ERROR, SourcecodeVisualizerPlugin.PLUGIN_ID, errorMsg, e);
	    		CorePlugin.log(s);
	    		
	    		String msg = CoreMessages.ERROR_Opening_Graph_in_CFGF_failed 
	    				+ '\n'
	    				+ CoreMessages.ExceptionAdditionalMessage;
	    		Messages.error(msg);
				
				return;
			}

		}
    	
		/* package and class name */
		IType classType = method.getDeclaringType();
		className = classType.getElementName();
		
		/* method name */
		methodName = method.getElementName();
 
		/* method name */              
		try{
			if(method.isConstructor()){
				methodName = constructorString;
			}
			else{
				methodName = method.getElementName();
			}

			/* resolve parameter signature */
			StringBuffer buf = new StringBuffer("(");
			String[] parameterTypes = method.getParameterTypes();
			String res = null;
			for(int i = 0; i < parameterTypes.length; i++){
				res = ActionUtils.getResolvedTypeName(parameterTypes[i], method.getDeclaringType());
				buf.append(res);
			}						
			buf.append(")");
			
			res = ActionUtils.getResolvedTypeName(method.getReturnType(), method.getDeclaringType());
			buf.append(res);
			
			methodSig = buf.toString();
			
		}catch(IllegalArgumentException e){
			String errorMsg = CoreMessages.ERROR_Opening_Graph_in_CFGF_failed
    				+ '\n'
    				+ e.getMessage();
			IStatus s = new Status(IStatus.ERROR, SourcecodeVisualizerPlugin.PLUGIN_ID, errorMsg, e);
    		CorePlugin.log(s);
    		
    		String msg = CoreMessages.ERROR_Opening_Graph_in_CFGF_failed 
    				+ '\n'
    				+ CoreMessages.ExceptionAdditionalMessage;
    		Messages.error(msg);
    		
    		return;
    		
		} catch (JavaModelException e) {
			String errorMsg = CoreMessages.ERROR_Opening_Graph_in_CFGF_failed
    				+ '\n'
    				+ e.getMessage();
			IStatus s = new Status(IStatus.ERROR, SourcecodeVisualizerPlugin.PLUGIN_ID, errorMsg, e);
    		CorePlugin.log(s);
    		
    		String msg = CoreMessages.ERROR_Opening_Graph_in_CFGF_failed 
    				+ '\n'
    				+ CoreMessages.ExceptionAdditionalMessage;
    		Messages.error(msg);
    		
    		return;
		}

		StringBuffer buf = new StringBuffer();
		buf.append(className);
		buf.append(".");
		if(methodName.equals(constructorString)){
			buf.append(className);
		}
		else{
			buf.append(methodName);	
		}
		
		buf.append(".src.graph");
    		
		comunicationObject.generateGraph(buf.toString(), classPath, packageName, className, methodName, methodSig);
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.RetargetAction#runWithEvent(org.eclipse.swt.widgets.Event)
	 */
	public void runWithEvent(Event event) {
		run();	
	}
}
