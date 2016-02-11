package org.pathvisio.findyourinteraction;

import javax.swing.JPopupMenu;

import org.pathvisio.core.view.VPathwayElement;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;
import org.pathvisio.gui.PathwayElementMenuListener.PathwayElementMenuHook;

public class FindYourInteractionPlugin implements Plugin{
	private PvDesktop desktop;
	
	@Override
	public void init(final PvDesktop desktop) {
		// TODO Auto-generated method stub
		this.desktop = desktop;
		desktop.addPathwayElementMenuHook(new PathwayElementMenuHook(){
                  /**
                   * This method is called whenever the user right-clicks
                   * on an element in the Pathway.
                   * VPathwayElement contains the object that was clicked on.
                   */
				@Override
				public void pathwayElementMenuHook(VPathwayElement e,
						JPopupMenu menu) {
					// TODO Auto-generated method stub
					// We instantiate an Action
					SearchInteractionAction showInfoAction = new SearchInteractionAction(desktop);
                    
                    // pass the clicked element to the action object
                    showInfoAction.setElement(e);
//                    desktop.getSwingEngine().getEngine().getActivePathway().get
                    // Insert action into the menu.
                    // NB: this is optional, we can choose e.g.
                    // to insert only when the clicked element is a certain type.
                    menu.add (showInfoAction);
				}
          });
	}

	@Override
	public void done() {
		// TODO Auto-generated method stub
//		desktop.dispose();;
	}

}
