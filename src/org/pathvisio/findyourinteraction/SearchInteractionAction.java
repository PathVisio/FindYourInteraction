package org.pathvisio.findyourinteraction;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.bridgedb.Xref;
import org.pathvisio.core.data.XrefWithSymbol;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.model.MLine;
import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.model.PathwayElement.MAnchor;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.core.view.Graphics;
import org.pathvisio.core.view.VPathwayElement;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.gui.ProgressDialog;
import org.pathvisio.gui.dialogs.DatabaseSearchDialog;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class SearchInteractionAction extends AbstractAction{

	/**
	 * Search feature for looking up accession
	 * numbers of reactions/interactions.
	 */
	private static final long serialVersionUID = -4231646730955550080L;
	private String rheaWS = "http://www.rhea-db.org/rest/1.0/ws/reaction/cmlreact?q=";
	private VPathwayElement elt;
	private IDMapperStack mapper;
	private PathwayElement queryElement = null;
	private int tries;
	
	private PvDesktop desktop;
	private PathwayElement input;
	

	public SearchInteractionAction(PvDesktop desktop) {
		// This will be the label of the pop up menu item.
		putValue (NAME, "Find Your Interaction");
		putValue (SHORT_DESCRIPTION,"Search the online Rhea database for references,"
				+ " based on the identifiers of the interactors");
		this.desktop = desktop;
		mapper = desktop.getSwingEngine().getGdbManager().getCurrentGdb();
	}
	/**
	 * This should be called before the action is triggered 
	 */
	public void setElement (VPathwayElement anElt)
	{
		elt = anElt;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
//		VPathway vp = elt.getDrawing();
//		component = (VPathwaySwing)vp.getWrapper();
		if(elt instanceof Graphics) {
			PathwayElement p = ((Graphics)elt).getPathwayElement();
			if (p instanceof MLine){
				this.input = p;
				search(p.getPathway(), p.getStartGraphRef(),
						p.getEndGraphRef());
			}
		}
	}
	/**
//	 * Search for identifiers for the selected interaction in Rhea
//	 * (http://www.rhea-db.org/home) based on identifiers of the nodes that are
//	 * connected by the interaction
//	 * 
//	 * @param pwy
//	 */
	private void search(Pathway pwy, final String startNode,
			final String endNode) {
		String startNodeId = getElementId(startNode, pwy);
		String endNodeId = getElementId(endNode, pwy);
		String query = "";
//		System.out.println(startNode+"\t"+endNode);
//		System.out.println(startNodeId+"\t"+endNodeId);
		/*
		 * Eg. query: http://www.rhea-db.org/rest/1.0/ws/reaction?q=glucose *
		 * http://www.rhea-db.org/rest/1.0/ws/reaction?q=CHEBI:17632
		 * http://www.rhea-db.org/rest/1.0/ws/reaction?q=CHEBI:17632+CHEBI:16301
		 */
		if ((startNode != null && !"".equals(startNodeId.trim()))
				&& (endNode != null && !"".equals(endNodeId.trim())) ){
			if (tries == 0) {
				query = rheaWS + startNodeId.trim() + "+" + endNodeId.trim();
				System.out.println(query+"	1");
			} else if (tries == 1) {
				query = rheaWS + startNodeId.trim();
				System.out.println(query+"	2");
			} else if (tries == 2) {
				query = rheaWS + endNodeId.trim();
				System.out.println(query+"	3");
			}
		}		
		else if ((startNode != null && endNode == null && !"".equals(startNodeId.trim()))) {
			int dialogResult = 
					JOptionPane.showConfirmDialog
					(null, "Only the start node is connected.\n Do you still want to search ?",
							"Warning",JOptionPane.YES_NO_OPTION);
			if(dialogResult == JOptionPane.YES_OPTION){
				query = rheaWS + startNodeId.trim();
				System.out.println(query+"	4");
			}
		} else if ((endNode != null && startNode == null && !"".equals(endNodeId.trim()))) {
			int dialogResult = 
					JOptionPane.showConfirmDialog
					(null, "Only the end node is connected.\n Do you still want to search ?",
							"Warning",JOptionPane.YES_NO_OPTION);
			if(dialogResult == JOptionPane.YES_OPTION){
				query = rheaWS + endNodeId.trim();
				System.out.println(query+"	5");
			}
		}
//		else if ( "".equals(endNodeId.trim()) && "".equals(startNodeId.trim( )      )   ) {
//			JOptionPane.showMessageDialog
//			(null, "Both nodes are not correctly annotated",
//					"Error",JOptionPane.ERROR_MESSAGE);
//		}
//		else if ((startNodeId != null && !"".equals(startNodeId.trim( )    )  )   ) {
//			int dialogResult = 
//					JOptionPane.showConfirmDialog
//					(null, "Only the start node is annotated.\n"
//							+ "Annotating the datanodes will improve the results.\n"
//							+ "Do you still want to search ?",
//							"Warning",JOptionPane.YES_NO_OPTION);
//			if(dialogResult == JOptionPane.YES_OPTION){
//				query = rheaWS + startNodeId.trim();
//				System.out.println(query+"	6");
//			}
//		}
//		else if ((endNodeId != null && !"".equals(endNodeId.trim()))) {
//			int dialogResult = 
//					JOptionPane.showConfirmDialog
//					(null, "Only the end node is annotated.\n"
//							+ "Annotating the datanodes will improve the results.\n"
//							+ "Do you still want to search ?",
//							"Warning",JOptionPane.YES_NO_OPTION);
//			if(dialogResult == JOptionPane.YES_OPTION){
//				query = rheaWS + endNodeId.trim();
//				System.out.println(query+"	7");
//			}
//		}		
		else{
			JOptionPane.showMessageDialog
					(null, "Search function for this pathway element has not been implemented yet",
							"Error",JOptionPane.ERROR_MESSAGE);
		}

		tries++;

		final String text = query.trim();

		final ProgressKeeper progress = new ProgressKeeper();
		ProgressDialog dialog = new ProgressDialog(desktop.getSwingEngine().getFrame(),
				"Searching Rhea for interactions. Query =" + query, progress,
				true, true);
		/// TODO
//		dialog.setLocation( new InteractionDialog(desktop.getSwingEngine(),(Graphics) elt.getPathwayElement(),component) );
		
		if ( ! query.equals("")){
		SwingWorker<List<XrefWithSymbol>, Void> sw = new SwingWorker<List<XrefWithSymbol>, Void>() {
			protected List<XrefWithSymbol> doInBackground() throws IDMapperException {
				// The result set
				List<XrefWithSymbol> result = new ArrayList<XrefWithSymbol>();
				// querying Rhea using webservice				
				try {
					DocumentBuilderFactory dbf = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();

					URL queryText = new URL(text);
					Document doc = db.parse(queryText.openStream());

					String text2parse = doc.getDocumentElement()
							.getTextContent();
					text2parse = text2parse.replaceAll(
							"^\\s+|\\s+$|\\s*(\n)\\s*|(\\s)\\s*", "$1$2")
							.replace("\t", " ");

					if (text2parse.contains("rhea")) {
						String[] parsedText = text2parse.split("\n");

						for (int i = 0; i < parsedText.length; i = i + 4) {
							/*
							 * Get id
							 */

							Xref intxref = new Xref(parsedText[i],
									DataSource.getExistingBySystemCode("Rh"));
							/*
							 * Get uri
							 */
							String interactionUri = parsedText[i + 2];

							Document doc2 = null;
							String reactionText = "";
							try {								
								DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
								doc2 =    factory.newDocumentBuilder().
										parse(new URL(interactionUri).openStream());
								reactionText = doc2.getElementsByTagName("name").item(0).getTextContent();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							result.add(new XrefWithSymbol(intxref, reactionText));

						}
					} else {
						if (tries < 3) { // Threshold up to 3 to run all the case
							search(input.getPathway(), input.getStartGraphRef(),
									input.getEndGraphRef());
						} else {
							System.out.println("No reactions");
						}
				}

				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return result;
			}

			private void applyAutoFill(XrefWithSymbol ref) {
				input.setElementID(ref.getId());
				input.setDataSource(ref.getDataSource());
//				idText.setText(ref.getId());
//				dsm.setSelectedItem(ref.getDataSource());
			}

			@Override
			public void done() {
				progress.finished();
				if (!progress.isCancelled()) {
					List<XrefWithSymbol> results = null;
					try {
						results = get();
						// Show results to user
						if (results != null && results.size() > 0) {
							String pretext = "Reactions shown for ";
							DatabaseSearchDialog resultDialog = new DatabaseSearchDialog(
									pretext + text.replace(rheaWS, ""), results);
							resultDialog.setVisible(true);
							XrefWithSymbol selected = resultDialog
									.getSelected();
							if (selected != null) {
								applyAutoFill(selected);
							}
						} else {
							String posttext = "";
							String pretext = "No reactions found for ";
							if (!((text.contains("CHEBI")|| (text.contains("\\d+"))))) {
								posttext = ".\nAnnotating the datanodes will improve the results.";
							}
							if (text.isEmpty()) {
								pretext = "Search function for this pathway element has not been implemented yet";
							}
							JOptionPane.showMessageDialog(null,
									pretext + text.replace(rheaWS, "")
									+ posttext);
						}
					} catch (InterruptedException e) {
						// Ignore, thread interrupted. Same as cancel.
					} catch (ExecutionException e) {
						JOptionPane.showMessageDialog(null,
								"Exception occurred while searching,\n"
										+ "see error log for details.",
										"Error", JOptionPane.ERROR_MESSAGE);
						Logger.log.error("Error while searching", e);
					}
				}
			}
		};
		sw.execute();
		
		dialog.setVisible(true);
		}
		
	}
	/*
	 * Select query id (identifier or label)
	 */
	private String getElementId(String nodeRef, Pathway pwy) {
		boolean matchFound = false;
//		System.out.println("1 inside "+nodeRef);
		String id = "";
		if (nodeRef != null) {
			for (PathwayElement pe : pwy.getDataObjects()) {
				if (!(pe.getGraphId() == null)) {
					if (pe.getObjectType() == ObjectType.DATANODE) {
						if (pe.getGraphId().equalsIgnoreCase(nodeRef)) {
							queryElement = pe;
							matchFound = true;
						}
					}
				}
			}
			if (!matchFound) {
				for (PathwayElement pe : pwy.getDataObjects()) {
					if (!(pe.getGraphId() == null)) {
						if (pe.getObjectType() == ObjectType.LINE) {
							for (MAnchor anchor : pe.getMAnchors()) {
								if (anchor.getGraphId().equalsIgnoreCase(
										nodeRef)) {
									queryElement = findConnectedNode(anchor,
											pwy);
									matchFound = true;
								}
							}
						}
					}
				}
			}
			Set<Xref> ids;
			if (matchFound && queryElement != null) {
				try {
					ids = mapper.mapID(queryElement.getXref(), DataSource.getExistingBySystemCode("Ce"));
					if(ids.isEmpty()){
						ids = mapper.mapID(queryElement.getXref(), DataSource.getExistingBySystemCode("S"));
					}
					if(ids.isEmpty()){
//						id = "";
						id = queryElement.getTextLabel();
						id = id.replaceAll(" ", "+");
					}else{
						String tmpid = ids.iterator().next().getId();
						if(ids.iterator().next().getDataSource() == DataSource.getExistingBySystemCode("S")){
								/*
								 * Choose ids starting with P/Q if found
								 */
								for(Xref ref:ids){
									if(ref.getId().startsWith("P")||ref.getId().startsWith("Q")){
										tmpid = ref.getId();
									}
								}
							}
						id = tmpid;
					}
					
				} catch (IDMapperException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (id.matches("\\d+")) {
					if (!(id.contains("CHEBI:"))) {
						id = "CHEBI:" + id;
					}
				}

			}
		}
//		System.out.println("1 id:"+id);
		return id;
	}
	private PathwayElement findConnectedNode(MAnchor anchor, Pathway pwy) {
		PathwayElement targetNode = null;
		String targetNodeRef = "";
		for (PathwayElement pwe : pwy.getDataObjects()) {
			if (pwe.getObjectType() == ObjectType.LINE) {
				if (anchor.getGraphId().equalsIgnoreCase(pwe.getEndGraphRef())) {
					targetNodeRef = pwe.getStartGraphRef();
				} else if (anchor.getGraphId().equalsIgnoreCase(
						pwe.getStartGraphRef())) {
					targetNodeRef = pwe.getEndGraphRef();
				}
			}
		}
		for (PathwayElement pwe1 : pwy.getDataObjects()) {
			if (pwe1.getObjectType() == ObjectType.DATANODE) {
				if (pwe1.getGraphId().equalsIgnoreCase(targetNodeRef)) {
					targetNode = pwe1;
				}
			}
		}
		return targetNode;
	}
	
}

