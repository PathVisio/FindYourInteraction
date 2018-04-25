//  FindYourInteraction Plugin for PathVisio
//	Find metabolic reaction from Rhea
//	Copyright 2016-2018 BiGCaT, Maastricht University
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
   
package org.pathvisio.findyourinteraction;

import javax.swing.JPopupMenu;

import org.pathvisio.core.view.VPathwayElement;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;
import org.pathvisio.gui.PathwayElementMenuListener.PathwayElementMenuHook;

/**
 * PathVisio plugin class
 * Registering menu items and actions
 * @author anwesha, jonathan, mkutmon
 *
 */
public class FindYourInteractionPlugin implements Plugin{
	private PvDesktop desktop;
	private PathwayElementMenuHook menuHook;
	
	@Override
	public void init(final PvDesktop desktop) {
		this.desktop = desktop;
		
		menuHook = new PathwayElementMenuHook() {	
			@Override
			public void pathwayElementMenuHook(VPathwayElement e, JPopupMenu menu) {
				SearchInteractionAction showInfoAction = new SearchInteractionAction(desktop, e);
				menu.add (showInfoAction);
			}
		};
		desktop.addPathwayElementMenuHook(new PathwayElementMenuHook() {
                  /** 
                   * This method is called whenever the user right-clicks
                   * on an element in the Pathway.
                   * VPathwayElement contains the object that was clicked on.
                   */
				@Override
				public void pathwayElementMenuHook(VPathwayElement e, JPopupMenu menu) {
					SearchInteractionAction showInfoAction = new SearchInteractionAction(desktop, e);
                    menu.add (showInfoAction);
				}
          });
	}

	@Override
	public void done() {
		desktop.getSwingEngine().getApplicationPanel().getPathwayElementMenuListener().removePathwayElementMenuHook(menuHook);
	}
}
