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

package com.drgarbage.controlflowgraphfactory.actions;

import java.util.List;

import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.WorkbenchMessages;

import com.drgarbage.controlflowgraphfactory.editors.ControlFlowGraphEditor;
import com.drgarbage.visualgraphic.commands.PasteCommand;
import com.drgarbage.visualgraphic.model.VertexBase;

public class PasteAction extends WorkbenchPartAction
{

	/**
	 * Constructs a new CopyTemplateAction.  You must manually add this action to the palette
	 * viewer's list of selection listeners.  Otherwise, this action's enabled state won't be
	 * updated properly.
	 * 
	 * @param editor the workbench part
	 * @see org.eclipse.gef.ui.actions.EditorPartAction#EditorPartAction(IEditorPart)
	 */
	public PasteAction(IEditorPart editor) {
		super(editor);
		setId(ActionFactory.PASTE.getId());
		setText(WorkbenchMessages.Workbench_paste);
		
        ISharedImages sharedImages = editor.getSite().getWorkbenchWindow().getWorkbench().getSharedImages();
		
		setToolTipText(WorkbenchMessages.Workbench_pasteToolTip);
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
        setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
        
        setLazyEnablementCalculation(true);
	}

	/**
	 * Returns whether the selected EditPart is a TemplateEditPart.
	 * @return whether the selected EditPart is a TemplateEditPart
	 */
	protected boolean calculateEnabled() {
		Object o = Clipboard.getDefault().getContents();
		if(o == null )
			return false;
		
		if(o != null && o.toString().equals("EMPTY"))
			return false;

		return true;
	}

	/**
	 * Sets the default {@link Clipboard Clipboard's} contents to be the currently selected
	 * template.
	 */
	@SuppressWarnings("unchecked")
	public void run() {
		Object o = Clipboard.getDefault().getContents();
		if(o != null
				&& !o.toString().equals("EMPTY")){
			List<VertexBase> models = (List<VertexBase>) o;
			
			ControlFlowGraphEditor cfgEditor = (ControlFlowGraphEditor) getWorkbenchPart();
			PasteCommand cmd = new PasteCommand(cfgEditor.getModel(), models);
			execute(cmd);

			Clipboard.getDefault().setContents(new String("EMPTY"));
			refresh();
		}
	}

}
