// copyright (c) 1998,1999 Róbert Viðar Bjarnason & Sean Brunwin
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; see the file COPYING.  If not, write to
// the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.

package org.bang;

// we import some swing classes
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.undo.*;

// and  basic java classes
import java.beans.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Toolkit;
import java.io.*;
import java.io.IOException;
import java.net.*;
import java.net.Socket;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Arrays;

// Java3D classes
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;


import org.bang.WhoPanel;
import org.bang.view.*;
import org.bang.net.*;
import org.bang.net.vnet.*;
import org.bang.jini.*;

// uncomment if you the JMF1.1 API
// import org.bang.media.VideoChat;

// uncomment if you have a JavaSpeech API Impl
// import org.bang.media.BangVoice;

import org.bang.util.*;
import org.bang.jini.BangSpace;

/**
 *   This is the main bang client class and the main Swing thread.
 */
public class Bang extends JSplitPane implements  KeyListener
{
 public static final String buildNr = "0.076";
 private static final boolean CONNECT_TO_MOO = true;

 // the GUI layout
 public JSplitPane talkerPanel;

 public GridBagLayout gridbag;
 protected JPanel telnetPanel;
 public JPanel infoPanel;
 public JLabel roomLocation = new JLabel();

 public static JProgressBar progressBar = null;
 public static JLabel progressLabel = null;

 protected JScrollPane tscrollPane;
 protected JTextArea topTextArea;
 public static TextField textInput;

 protected JScrollPane meScrollPanel;
 protected JPanel mePanel;
 public JPanel miscPanel;
 public JPanel genderPanel;
 public JPanel avatarPanel;
 public final String[] avatarsName = {"Spider1", "Spider2", "Dog", "Emu", "Woman", "Bear", "theFlyer", "Glyph" };
 public final String[] avatarUrls = { "http://streamer.rit.edu/~jeffs/vrmLab/Warehouse/creatures/spider.wrl",
			     	"http://streamer.rit.edu/~jeffs/vrmLab/Warehouse/creatures/spider2.wrl",
        			"http://ariadne.iz.net/~jeffs/vrmLab/Warehouse/creatures/dog.wrl",
        			"http://ariadne.iz.net/~jeffs/vrmLab/Warehouse/creatures/emu.wrl",
        			"http://ariadne.iz.net/~jeffs/vrmLab/Warehouse/creatures/girl.wrl",
        			"http://ariadne.iz.net/~jeffs/vrmLab/Warehouse/creatures/Bear0.wrl",
        			"http://ariadne.iz.net/~jeffs/vrmLab/Warehouse/creatures/Flyer0.wrl",
        			"http://ariadne.iz.net/~jeffs/vrmLab/Warehouse/creatures/Glyph0.wrl" };

 Hashtable avHash = new Hashtable();
 public Hashtable viewHash = new Hashtable();

 String theAvName;
 String theViewPoint;

 protected JPopupMenu objPopup;
 protected JPopupMenu invPopup;

 protected JPanel workPanel;
        public JTabbedPane tabbedPane;
                public JScrollPane wscrollPane;
                        public WhoPanel whoList;
                public JScrollPane oscrollPane;
                        public WhoPanel objList;
                public JScrollPane iscrollPane;
                        public WhoPanel invList;

 public String app3dURLname;
 public String theApp3dName;
 boolean doMU;

 static public Noteditor noteditor;

 public String theCommand;

 public static int totalPanels = 8;
 public static int currentProgressValue;

 private TransformGroup[] objVpWorldTrans;
 private String[] objVpWorldDesc;

 public JComboBox viewpoints;

 // are we connected ?
 private boolean connected = false; 

 // and the thread we will live in
 private Thread t;

 // public instance of *this* applet
 public Bang thisBang;

 // is this a new user ?
 private String createnew;

 // the master MOO connection
 public MooConnection theMooConnection;
  
 // the Java3D viewer
 static public Viewer viewer;
 
 // The width and height of the frame
 public static int WIDTH = 640;
 public static int HEIGHT = 600;
 public static int INITIAL_WIDTH = 370;
 public static int INITIAL_HEIGHT = 200;
 private static JPanel progressPanel;  
 private static Dimension screenSize;
 
 private Point popupSelection;
 public String fromEditorText;  

 private JTextField buildTextInput;
 private JTextField markTextInput;
 private JButton writeMarkButton; 
 private JButton insertButton;

 private JTextField goTextInput1;
 private JTextField goTextInput2;
 private JTextField goTextInput3;

 public JLabel theSectorString;	
 public String theRTPurl;
 public String theRTPport;

// uncoment if you have a JavaSpeech API Impl   
// public  BangVoice theBangVoice;

 private Runnable bangVoiceRunnable;
 static  public BangSpace space;


 /** 
 *   This is the main bang init method. Sets up the Swing GUI and start 
 *   the Java3D viewer and MOO connection thread.
 */
 public Bang(String proxyHost, String proxyPort) {
	// sets up the master prefs for the JSplitPane
	super(VERTICAL_SPLIT);
	setOneTouchExpandable(true);

	// builds the initial Splash Screen/Frame
	JFrame frame = new JFrame("bang "+buildNr);

	progressPanel = new JPanel() {
	    public Insets getInsets() {
		return new Insets(40,30,20,30);
	    		}
			};
	progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
	frame.getContentPane().add(progressPanel, BorderLayout.CENTER);

	Dimension d = new Dimension(400, 20);
	progressLabel = new JLabel("bang initializing ...");
	progressLabel.setAlignmentX(CENTER_ALIGNMENT);
	progressLabel.setMaximumSize(d);
	progressLabel.setPreferredSize(d);
	progressPanel.add(progressLabel);
	progressPanel.add(Box.createRigidArea(new Dimension(1,20)));
	progressBar = new JProgressBar(0, totalPanels);
	progressBar.setStringPainted(true);
	progressLabel.setLabelFor(progressBar);
	progressBar.setAlignmentX(CENTER_ALIGNMENT);
	progressBar.getAccessibleContext().setAccessibleName("bang loading progress");
	progressPanel.add(progressBar);
	
	// show the frame
	frame.setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
	screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	frame.setLocation(screenSize.width/2 - INITIAL_WIDTH/2,
			screenSize.height/2 - INITIAL_HEIGHT/2);
	frame.show();
	frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	
	// init the public reference to this object
	thisBang = this;
	
	// trigger the Jini Lookup for bangSpace
	goBangSpace();	

        currentProgressValue = 0;
	
	// init the login .wrl
	String path = System.getProperties().getProperty("user.dir") + '/' ;
	path = path.replace(java.io.File.separatorChar, '/');

	// build the 3D viewer with a login .wrl as param
	viewer = new Viewer("file:" + path + "content/robot.wrl", this);
		
	progressBar.setValue(++currentProgressValue); 	

	// set up the chat/tab panel
	talkerPanel = new JSplitPane(HORIZONTAL_SPLIT);
	talkerPanel.setOneTouchExpandable(true);
	
	// here we set up the telnet panel
	telnetPanel = new JPanel();
	infoPanel = new JPanel();
	infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	roomLocation = new JLabel("bang " + buildNr);
	infoPanel.add(roomLocation);
	
	gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	c.fill = GridBagConstraints.BOTH;
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.weighty = 1.0;
	telnetPanel.setLayout(gridbag);
	
	c.weighty = 0.07;
	gridbag.setConstraints(infoPanel,c);
	roomLocation.setForeground(Color.black);
	telnetPanel.add(infoPanel);

	c.weighty = 1.0;
	topTextArea = new JTextArea("", 3, 10);
	topTextArea.setEditable(false);
	topTextArea.setLineWrap(true);
	tscrollPane = new JScrollPane(topTextArea);
	tscrollPane.setHorizontalScrollBarPolicy(tscrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	
	gridbag.setConstraints(tscrollPane, c);
	telnetPanel.add(tscrollPane);
	
	c.weightx = 1.0;
	c.weighty = 0.0;
	c.gridwidth = 1;
	c.insets = new Insets(6, 6, 0, 6);
	textInput = new TextField(40);
	gridbag.setConstraints(textInput, c);
	
	// the main MOO chat input field
	textInput.addKeyListener(this);
	textInput.requestFocus();
	telnetPanel.add(textInput);
	
	// and the work panel and sub-panels
	workPanel = new JPanel(new BorderLayout());
	tabbedPane = new JTabbedPane();
	
	// the who panel
	whoList = new WhoPanel();
	wscrollPane = new JScrollPane(whoList);
	
	// the objects in contents panel
	objList = new WhoPanel();
	oscrollPane = new JScrollPane(objList);
	
	// the inventory panel
	invList = new WhoPanel();
	iscrollPane = new JScrollPane(invList);
	
	// the me/option panel
	mePanel = new JPanel();
	mePanel.setLayout(new BoxLayout(mePanel, BoxLayout.Y_AXIS));
	mePanel.setBackground(Color.white);
	
	JPanel headLightPanel = new JPanel();
	headLightPanel.setLayout(new BoxLayout(headLightPanel, BoxLayout.X_AXIS));
	headLightPanel.setBackground(Color.white);
	
	JSlider s = new JSlider(0, 50, 30);
	s.putClientProperty( "JSlider.isFilled", Boolean.TRUE );
	s.getAccessibleContext().setAccessibleName("Headlight Volume");
	s.getAccessibleContext().setAccessibleDescription("A headLight volume slider");
 	s.addChangeListener(new ChangeListener(){	
		public void stateChanged(ChangeEvent e) 
		{
			JSlider s1 = (JSlider)e.getSource();
			float f1 = ((float) s1.getValue())/50;
			viewer.headLight.setColor(new Color3f(f1, f1, f1));
		}});
 	s.setBackground(Color.white);
 	s.setAlignmentY(s.CENTER_ALIGNMENT);	
 	s.setAlignmentX(s.CENTER_ALIGNMENT);			
 	
 	headLightPanel.add(s);
 	
 	JCheckBox button;
 	button = new JCheckBox("H-light", true);
	button.addItemListener(new ItemListener(){
		public void itemStateChanged(ItemEvent e)
		{
			if(e.getStateChange()==ItemEvent.SELECTED)
			{
				Viewer.headLight.setEnable(true);
	   		}
	   		else 
	   		{ 
	   			Viewer.headLight.setEnable(false);
	   		} 
	   	}});
	button.setToolTipText("Headlight controls");
	button.setMnemonic('h');
	button.setBackground(Color.white);
	button.setAlignmentY(button.CENTER_ALIGNMENT);	
	button.setAlignmentX(button.CENTER_ALIGNMENT);			
	
	headLightPanel.add(button);	
	headLightPanel.add(Box.createRigidArea(new Dimension(23, 0)));
	headLightPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
	headLightPanel.setAlignmentX(headLightPanel.CENTER_ALIGNMENT);
	mePanel.add(headLightPanel);
	
	JPanel scalePanel = new JPanel();
	scalePanel.setLayout(new BoxLayout(scalePanel, BoxLayout.X_AXIS));
	scalePanel.setBackground(Color.white);
	
	JSlider sc = new JSlider(0, 200, 100);
	sc.setPaintTicks(true);
	sc.setMajorTickSpacing(100);
	sc.setMinorTickSpacing(10);
	sc.setToolTipText("Avatar scale");
	sc.setPaintLabels( true );
	sc.setSnapToTicks( false );
	sc.getAccessibleContext().setAccessibleName("Avatar Scale");
	sc.getAccessibleContext().setAccessibleDescription("A avatar scale slider");
	ChangeListener cl = new ChangeListener() {   
		public void stateChanged(ChangeEvent e) 
		{
			JSlider s1 = (JSlider)e.getSource();
	    		float f1 = (((float) s1.getValue())/100)+ (float) 0.0001;
			try 
			{ 
				viewer.dispatcher.scaleAvatar(new VSFVec3f(f1, f1, f1));
			}
			catch (Exception eee) 
			{}
		}};
   	sc.addChangeListener(cl);
	sc.setBackground(Color.white);
	sc.setAlignmentY(s.CENTER_ALIGNMENT);	
	sc.setAlignmentX(s.CENTER_ALIGNMENT);			
	scalePanel.add(sc);
	
	JLabel theScaleLabel = new JLabel("Scale %");
	theScaleLabel.setToolTipText("Avatar scale");
	theScaleLabel.setAlignmentY(theScaleLabel.CENTER_ALIGNMENT);
	theScaleLabel.setAlignmentX(theScaleLabel.LEFT_ALIGNMENT);	
	
	scalePanel.add(theScaleLabel);
	scalePanel.add(Box.createRigidArea(new Dimension(44, 0)));
	scalePanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 3));
	scalePanel.setAlignmentX(scalePanel.CENTER_ALIGNMENT);
	mePanel.add(scalePanel);
	
	miscPanel = new JPanel();
	miscPanel.setLayout(new BoxLayout(miscPanel, BoxLayout.X_AXIS));
	miscPanel.setBackground(Color.white);
	
	viewpoints = new JComboBox();
	viewpoints.getAccessibleContext().setAccessibleName("Viewpoint");
	viewpoints.getAccessibleContext().setAccessibleDescription("Choose viewpoint");
	viewpoints.addItem("none");
	ItemListener il2 = new ItemListener() {
		public void itemStateChanged(ItemEvent evt) 
		{
			int theID = 0;
   			if (evt.getStateChange() == ItemEvent.SELECTED) 
			{
				theViewPoint = new String((String)evt.getItem());
				TransformGroup theViewTransG = (TransformGroup) viewHash.get(theViewPoint);
                        	if (viewer != null && theViewTransG != null) 
				{
					viewer.animateViewpoint(theViewTransG);
				}
   			}
		}};
    	viewpoints.addItemListener(il2);
    	viewpoints.setBackground(Color.white);
	viewpoints.disable();

	miscPanel.add(new JLabel("SpaceMark: "));
	miscPanel.add(Box.createRigidArea(new Dimension(2, 0)));	
 	miscPanel.add(viewpoints);
	miscPanel.add(Box.createRigidArea(new Dimension(83, 0)));	
	
	genderPanel = new JPanel();
	genderPanel.setLayout(new BoxLayout(genderPanel, BoxLayout.X_AXIS));
	genderPanel.setBackground(Color.white);
	JComboBox gender = new JComboBox();
	gender.addItem("neuter");
	gender.addItem("male");
	gender.addItem("female");
	gender.addItem("either");
	gender.addItem("plural");
	gender.addItem("egotistical");
	gender.addItem("royal");
	gender.getAccessibleContext().setAccessibleName("Gender");
	gender.getAccessibleContext().setAccessibleDescription("Choose gender");
 	ItemListener il = new ItemListener() {
		public void itemStateChanged(ItemEvent evt) 
		{
   		if (evt.getStateChange() == ItemEvent.SELECTED)
			writeToSocket("@gender "+ (String)evt.getItem());
   		}};
    	gender.addItemListener(il);
    	gender.setBackground(Color.white);
	genderPanel.add(new JLabel("Gender: "));
	genderPanel.add(Box.createRigidArea(new Dimension(17, 0)));
 	genderPanel.add(gender);
	genderPanel.add(Box.createRigidArea(new Dimension(88, 0)));

   	miscPanel.setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 7));			
 	genderPanel.setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 7));			

	mePanel.add(miscPanel);
	mePanel.add(genderPanel);

	avatarPanel = new JPanel();
	avatarPanel.setLayout(new BoxLayout(avatarPanel, BoxLayout.X_AXIS));
	avatarPanel.setBackground(Color.white);

	JComboBox avatar = new JComboBox();
      	avatar.addItem(" you select");
	for (int i = 0; i < avatarsName.length; i++) 
	{
		avatar.addItem(avatarsName[i]);
		avHash.put(avatarsName[i], avatarUrls[i]);		
    	}
	avatar.getAccessibleContext().setAccessibleName("Avatar");
	avatar.getAccessibleContext().setAccessibleDescription("Choose avatar");
	ItemListener il3 = new ItemListener() {
		public void itemStateChanged(ItemEvent evt) 
		{
   			if (evt.getStateChange() == ItemEvent.SELECTED)
			{
				theAvName = new String((String)evt.getItem());
				String theAvUrl = (String) avHash.get(theAvName);
                        	if (viewer != null && theAvUrl != null) 
				{
					writeToSocket("connect me to "+ (String)theAvUrl);
				}
   			}
		}};
	avatar.addItemListener(il3);
	avatar.setBackground(Color.white);
	
	avatarPanel.add(new JLabel("Set avatar: "));
	avatarPanel.add(avatar);
	avatarPanel.add(Box.createRigidArea(new Dimension(83, 0)));
	avatarPanel.setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 7));
	
	mePanel.add(avatarPanel);
	
	meScrollPanel = new JScrollPane(mePanel);	     		
	meScrollPanel.setHorizontalScrollBarPolicy(meScrollPanel.HORIZONTAL_SCROLLBAR_NEVER);
	
	// the authoring panel
	JPanel vrml97Panel = new JPanel();
	vrml97Panel.setLayout(new BorderLayout());
	vrml97Panel.setBorder(new TitledBorder(new EtchedBorder(), "VRML97 URL"));
	
	JPanel rootUrlPanel = new JPanel();
	rootUrlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	
	buildTextInput = new JTextField(20);
	rootUrlPanel.add(buildTextInput);
	vrml97Panel.add(rootUrlPanel, BorderLayout.NORTH);
	
	insertButton = new JButton("Add to BangSpace");
	insertButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e)
		{
			viewer.saveVRML97(buildTextInput.getText());
	   	}});
	insertButton.setEnabled(true);
	vrml97Panel.add(insertButton, BorderLayout.CENTER);

	JPanel spaceMarkPanel = new JPanel();
	spaceMarkPanel.setLayout(new BorderLayout());
	spaceMarkPanel.setBorder(new TitledBorder(new EtchedBorder(), "SpaceMark description"));

	JPanel descPanel = new JPanel();
	descPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
 	markTextInput = new JTextField(20);
	descPanel.add(markTextInput);
	spaceMarkPanel.add(descPanel, BorderLayout.NORTH);

	writeMarkButton = new JButton("Add to BangSpace");
	writeMarkButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e)
		{
                	viewer.saveSpaceMark(markTextInput.getText());
	   	}});
	writeMarkButton.setEnabled(true);
	spaceMarkPanel.add(writeMarkButton, BorderLayout.CENTER);

	JPanel rootSectorPanel = new JPanel(new FlowLayout());

	JPanel sectorPanel = new JPanel();
	sectorPanel.setLayout(new FlowLayout());
	theSectorString = new JLabel("Current Sector is xx:xx:xx");
	sectorPanel.add(theSectorString);

	rootSectorPanel.add(sectorPanel);

	JPanel sectorGoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

	JButton beemToSectorButton = new JButton("Beem to Sector");
	beemToSectorButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e)
		{
			Integer theBeemSector[] = new Integer[3];
			theBeemSector[0] = new Integer(goTextInput1.getText());
			theBeemSector[1] = new Integer(goTextInput2.getText());
			theBeemSector[2] = new Integer(goTextInput3.getText());
                        viewer.beemToSector(theBeemSector);
//                	viewer.saveSpaceMark(markTextInput.getText());
	   	}});
	beemToSectorButton.setEnabled(true);
	sectorGoPanel.add(beemToSectorButton);

	goTextInput1 = new JTextField(3);
	goTextInput2 = new JTextField(3);
	goTextInput3 = new JTextField(3);

	sectorGoPanel.add(goTextInput1);
	sectorGoPanel.add(goTextInput2);
	sectorGoPanel.add(goTextInput3);

	rootSectorPanel.add(sectorGoPanel);

	JPanel controlPanel = new JPanel();
    
	controlPanel.setLayout(new BorderLayout());
	controlPanel.add(spaceMarkPanel, BorderLayout.NORTH);
	controlPanel.add(rootSectorPanel, BorderLayout.CENTER);
	controlPanel.add(vrml97Panel, BorderLayout.SOUTH);
 
	// initialize the Noteditor
	noteditor = new Noteditor(thisBang);

	//Create all the popup menus
	objPopup = new JPopupMenu();
	JMenuItem popObjMenuItem = new JMenuItem("edit");
	ActionListener theObjPopupAction = new ActionListener()	{
		public void actionPerformed(ActionEvent e) 
		{
			int tmpPop = objList.locationToIndex(popupSelection);
			if (tmpPop >= 0)
			{
				objList.setSelectedIndex(tmpPop);
				writeToSocket("edit "+ (String) objList.getSelectedValue());
				objList.clearSelection();
				popupSelection = null;
				tabbedPane.setSelectedIndex(4); 
			}
		}};  

	popObjMenuItem.addActionListener(theObjPopupAction);
	objPopup.add(popObjMenuItem);

	invPopup = new JPopupMenu();
	JMenuItem popInvMenuItem = new JMenuItem("edit");
	ActionListener theInvPopupAction = new ActionListener() {
		public void actionPerformed(ActionEvent e)
		{
			int tmpPop = invList.locationToIndex(popupSelection);
			if (tmpPop >= 0) 
			{
				invList.setSelectedIndex(tmpPop);
				writeToSocket("edit "+ (String) invList.getSelectedValue());
				invList.clearSelection();
				popupSelection = null;
				tabbedPane.setSelectedIndex(4); 
			}
		}};

	JMenuItem popInvMenuItemRecycle = new JMenuItem("@recycle");
	ActionListener theInvPopupActionRecycle = new ActionListener() {
		public void actionPerformed(ActionEvent e) 
		{
			int tmpPop = invList.locationToIndex(popupSelection);
			if (tmpPop >= 0)
			{
				invList.setSelectedIndex(tmpPop);
				writeToSocket("@recycle "+ (String) invList.getSelectedValue());
				invList.clearSelection();
				// has to be removed from the Vector, tbd
			  	writeToSocket("i");
				popupSelection = null;
			}
		}};

	popInvMenuItem.addActionListener(theInvPopupAction);
	popInvMenuItemRecycle.addActionListener(theInvPopupActionRecycle);
	invPopup.add(popInvMenuItem);
	invPopup.add(popInvMenuItemRecycle);
	   
	MouseListener objPopupListener = new ObjPopupListener();
	MouseListener invPopupListener = new InvPopupListener();
       
	objList.addMouseListener(objPopupListener);
	invList.addMouseListener(invPopupListener);

	// we add all the panels to tabbedPane the main tab panel
	tabbedPane.addTab("who", null, wscrollPane);
	tabbedPane.addTab("me",null, meScrollPanel);
	tabbedPane.addTab("inv",null,iscrollPane);
	tabbedPane.addTab("obj",null,oscrollPane);
	tabbedPane.addTab("editor",null,noteditor);
 	tabbedPane.addTab("build",null,controlPanel);

	add(invPopup);
	add(objPopup);

	// the inital selected tabpanel
	tabbedPane.setSelectedIndex(5);
	
	// tabpanel added to workpanel
	workPanel.add(tabbedPane,BorderLayout.CENTER);
	
	// prefered size set again????
	telnetPanel.setPreferredSize(new Dimension(500,110));
	workPanel.setPreferredSize(new Dimension(100,200));
	
	talkerPanel.setLeftComponent(telnetPanel);
	talkerPanel.setRightComponent(workPanel);
	talkerPanel.setDividerLocation(337);
	talkerPanel.setDividerSize(4);
	
	// Provide minimum sizes for the two components in the split pane
	viewer.setMinimumSize(new Dimension(100,100));
	talkerPanel.setMinimumSize(new Dimension(50,50));
	
	// Add the two panes to this split pane
	setTopComponent(viewer);
	setRightComponent(talkerPanel);
	
	// Set the initial location and size of the divider
	setDividerLocation(310);
	setDividerSize(4);
	
	// Provide a preferred size for the split pane
	setPreferredSize(new Dimension(640, 680));
	
	progressBar.setValue(++currentProgressValue);
	progressLabel.setText("Connecting to MOO server");
	
	// Making the connection to the MOO server
	if (CONNECT_TO_MOO)
	{
		theMooConnection = new MooConnection(this);
		connected = true;
		theMooConnection.connect(proxyHost, proxyPort);
		progressBar.setValue(++currentProgressValue);
		progressLabel.setText("Connected :-)");
	}
	progressPanel.removeAll();
	frame.dispose();
	frame = null;
	// textInput.requestDefaultFocus();
 }
 

 /** 
 *   Inner class for handling popup menus for the obj tab. 
 */
 class ObjPopupListener extends MouseAdapter
 {
	public void mousePressed(MouseEvent e)
	{
		maybeShowPopup(e);
	}

	public void mouseReleased(MouseEvent e)
	{
		maybeShowPopup(e);
	}

	private void maybeShowPopup(MouseEvent e)
	{
		if (e.isPopupTrigger())
		{
			objPopup.show(e.getComponent(), e.getX(), e.getY());
			popupSelection = e.getPoint();
		}
	}
 }

 /** 
 *   Inner class for handling popup menus for the inv tab. 
 */
 class InvPopupListener extends MouseAdapter
 {
	public void mousePressed(MouseEvent e)
	{
		maybeShowPopup(e);
	}

	public void mouseReleased(MouseEvent e)
	{
		maybeShowPopup(e);
	}

	private void maybeShowPopup(MouseEvent e)
	{
		if (e.isPopupTrigger())
		{
			invPopup.show(e.getComponent(), e.getX(), e.getY());
			popupSelection = e.getPoint();
		}
        }
 }

 /** 
 *   Sets the text in the Noteditor coming from the MOO  
 */
 public synchronized void setMooText(String theText)
 {
	this.fromEditorText = theText;
	noteditor.removeUndo();
	noteditor.editor.setDocument(new PlainDocument());
	noteditor.setUndo();
	Runnable doWorkRunnable = new Runnable() {	
		public void run()
		{ 	  
			try
			{
				noteditor.editor.getDocument().insertString(0, 
							fromEditorText, null);
			}
			catch (BadLocationException e)
			{
				System.err.println(e.getMessage());
			}
		}};
	SwingUtilities.invokeLater(doWorkRunnable);
	tabbedPane.setSelectedIndex(4); 
 }

 /** 
 *   Opens up an RTP video Stream using JMF1.1  
 */
 public synchronized void goVideo(String theUrl, String thePort)
 {
	// uncoment if you java the JMF1.1 API
/*
	this.theRTPurl = theUrl;
	this.theRTPport = thePort;
	Runnable doMoreWorkRunnable = new Runnable() {
		public void run()
		{
			try
			{
				VideoChat theTempChat = new VideoChat(theRTPurl, 
							Integer.valueOf(theRTPport).intValue());
			}
		    	catch (Exception e)
			{
				System.err.println(e.getMessage());
			}
		}};
	SwingUtilities.invokeLater(doMoreWorkRunnable);		*/
 }

 /** 
 *   Starts the Jini based bangSpace server connection  
 */
 public static synchronized void goBangSpace()
 {
	Runnable goBangSpaceRunnable = new Runnable() {
		public void run()
		{
			space = new BangSpace();
		}};
	SwingUtilities.invokeLater(goBangSpaceRunnable);
 }

 /** 
 *   Opens up the voice reconizion system trough JavaSpeech API  
 */
 public synchronized void goVoice()
 {
	// uncoment if you java the JavaSpeech API
/*
	theBangVoice = new BangVoice();
	Runnable bangVoiceRunnable = new Runnable() {
		public void run()
		{
			theBangVoice.doVoice(thisBang);
		}};
	SwingUtilities.invokeLater(bangVoiceRunnable);	*/
 }

 /** 
 *   Kills the voice reconizion system (currently broken!!!)  
 */
 public synchronized void noVoice()
 {
	// uncoment if you java the JavaSpeech API
/*
	Runnable bangNoVoiceRunnable = new Runnable() {	
		public void run()
		{ 	
			theBangVoice.stop();
			theBangVoice = null;
			bangVoiceRunnable = null;
			System.out.println("JavaSpeech deactivated");
		}};
	SwingUtilities.invokeLater(bangNoVoiceRunnable);	*/
 }		

 /** 
 *   Stops the MOO thread and the Java3D viewer  
 */
 public final synchronized void stop()
 {
	// we disconnect from the moo server
	theMooConnection.disconnect();
	// and kill the viewer
	viewer.quit();
 }

 /** 
 *   Listens for keystrokes going to the MOO chat  
 */
 public synchronized void keyPressed(KeyEvent e)
 {
	int theCode = e.getKeyCode();  
	if (theCode == 10)
	{
		if (!connected)
		{
			textInput.setText("");
		}
		else
		{
			String theText = (textInput.getText());
			theMooConnection.writeToSocket(theText+"\n");
			textInput.setText("");
		}
	}
 }


 /** 
 *   Part of the KeyListener interface  
 */
 public void keyTyped(KeyEvent e) {}

 /** 
 *   Part of the KeyListener interface  
 */
 public void keyReleased(KeyEvent e) {}

 /** 
 *   Sets up the SpaceMark/Viewpoint menu  
 */
 public void setupVPmenu()
 {
	if (viewHash != null)
	{
		viewpoints.removeAllItems();
		for (Enumeration e = viewHash.keys() ; e.hasMoreElements() ;)
		{
			String tmpE = (String)e.nextElement();
			if (tmpE.equals(""))
			{
				viewpoints.addItem(" - no name - ");
			}
			else
			{
				viewpoints.addItem(tmpE);
			}
		}
      		viewpoints.enable();
		viewpoints.validate();
		miscPanel.validate();
	}
 }

 /** 
 *   Writes an string to the MOO server  
 */
 public void writeToSocket(String str)
 {
	theMooConnection.writeToSocket(str);
 }

 /** 
 *   Writes an string to the MOO user into the topTextArea  
 */
 public synchronized void writeToUser(String str)
 {
	topTextArea.append(str);
	topTextArea.setCaretPosition(topTextArea.getDocument().getLength());
 }
             
 /** 
 *   This method gets called if Bang is executed standalone
 *   with java org.bang.Bang  
 */
 public static void main(String[] args)
 {
	String urlProxyString = null;
	String telnetProxyString = null;
	String proxyHost = "vhmcomputers.com";
	String proxyPort = "6969";
	if (args.length != 0)
	{
		if (args.length != 2)
		{
			System.out.println("Usage: java org.bang.Bang urlProxy telnetProxy");
			System.exit(0);
	    	}
		else
		{
			urlProxyString = args[0];
    			try
			{
				StringTokenizer st = new StringTokenizer(urlProxyString,":");
				Properties props= new Properties(System.getProperties());
				props.put("http.proxySet", "true");
				props.put("http.proxyHost", st.nextToken());
				props.put("http.proxyPort", st.nextToken());
				Properties newprops = new Properties(props);
				System.setProperties(newprops);
				telnetProxyString = args[1];
				st = null;
				st = new StringTokenizer(telnetProxyString,":");
				proxyHost = st.nextToken();
				proxyPort = st.nextToken();
			}
			catch (Exception e)
			{
    				System.out.println("Usage: java org.bang.Bang urlproxy:port telnetproxy:port");
				System.exit(0);
			}
		}
	}

	// initializes Bang!
	Bang tempBang = new Bang(proxyHost, proxyPort);

	progressPanel.removeAll();

	// sets up the Main Window
	JFrame frame = new JFrame("bang "+buildNr);
	WindowListener l = new WindowAdapter() {
		public void windowClosing(WindowEvent e)
		{
			viewer.quit();
			System.exit(0);
		}};
	frame.addWindowListener(l);
	frame.getContentPane().setLayout(new BorderLayout());
	frame.getContentPane().add(tempBang, BorderLayout.CENTER);
	frame.setLocation(screenSize.width/2 - WIDTH/2, screenSize.height/2 - HEIGHT/2);
	frame.setSize(640, 640);
	frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	frame.validate();
	frame.repaint();
	frame.show();
 }
}
