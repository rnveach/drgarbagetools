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

package com.drgarbage.bytecodevisualizer.preferences;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerMessages;
import com.drgarbage.core.CoreConstants;
import com.drgarbage.core.jface.LinkFactory;
import com.drgarbage.core.preferences.AbstractPreferencePage;

/**
 * Main Page of the Bytecode Visualizer Preferences. 
 *
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class BytecodeVisualizerPreferencePage extends AbstractPreferencePage {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(final Composite parent) {
		/* set links to the default preferences */
		
		String msg = MessageFormat.format(
				BytecodeVisualizerMessages.BytecodeVisualizerPreferencePage_link_File_Associations, 
				new Object[] {
						CoreConstants.PREFPAGEID_FILE_ASSOCIATIONS
				});
		return LinkFactory.createPreferencePageLink(parent, SWT.NONE, msg);
	}

}
