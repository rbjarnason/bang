
package org.bang.util;

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
import java.net.Socket;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Arrays;
import org.bang.Bang;

public class Noteditor extends JPanel
{
 private static ResourceBundle resources;
 static
 {
	try
	{
		resources = ResourceBundle.getBundle("resources.Notepad", 
							java.util.Locale.getDefault());
	}
	catch (MissingResourceException mre)
	{
		System.err.println("resources/Notepad.properties not found");
		System.exit(1);
	}
 }

 public JTextComponent editor;
 private JScrollPane scroller;
 private JViewport port;
 private Hashtable commands;
 private Hashtable menuItems;
 private JMenuBar menubar;
 private JToolBar toolbar;

 protected FileDialog fileDialog;
 Bang thisBang;

 public Noteditor(Bang thisBang)
 {
	this.thisBang = thisBang;
	editor = new JTextArea("");
	JScrollPane scroller = new JScrollPane();
	JViewport port = scroller.getViewport();
	
	setBorder(BorderFactory.createEtchedBorder());
	setLayout(new BorderLayout());

	// setup the embedded JTextComponent
	editor.setFont(new Font("monospaced", Font.PLAIN, 12));
	// Add this as a listener for undoable edits.
	editor.getDocument().addUndoableEditListener(undoHandler);
	// install the command table
	commands = new Hashtable();
	Action[] actions = getActions();
	for (int i = 0; i < actions.length; i++)
	{
		Action a = actions[i];
		//commands.put(a.getText(Action.NAME), a);
		commands.put(a.getValue(Action.NAME), a);
	}
	port.add(editor);
	try
	{
		String vpFlag = resources.getString("ViewportBackingStore");
		Boolean bs = new Boolean(vpFlag);
		port.setBackingStoreEnabled(bs.booleanValue());
	} 
	catch (MissingResourceException mre)
	{}

	menuItems = new Hashtable();
	menubar = createMenubar();
	add("North", menubar);
	add("Center", scroller);
    }

/**
     * Fetch the list of actions supported by this
     * editor.  It is implemented to return the list
     * of actions supported by the embedded JTextComponent
     * augmented with the actions defined locally.
     */
    public Action[] getActions() {
	return TextAction.augmentList(editor.getActions(), defaultActions);
    }

  

    /** 
     * Fetch the editor contained in this panel
     */
    protected JTextComponent getEditor() {
	return editor;
    }

    /**
     * Find the hosting frame, for the file-chooser dialog.
     */
    protected Frame getFrame() {
	for (Container p = getParent(); p != null; p = p.getParent()) {
	    if (p instanceof Frame) {
		return (Frame) p;
	    }
	}
	return null;
    }

    /**
     * This is the hook through which all menu items are
     * created.  It registers the result with the menuitem
     * hashtable so that it can be fetched with getMenuItem().
     * @see #getMenuItem
     */
    protected JMenuItem createMenuItem(String cmd) {
	JMenuItem mi = new JMenuItem(getResourceString(cmd + labelSuffix));
        URL url = getResource(cmd + imageSuffix);
	if (url != null) {
	    mi.setHorizontalTextPosition(JButton.RIGHT);
	    mi.setIcon(new ImageIcon(url));
	}
	String astr = getResourceString(cmd + actionSuffix);
	if (astr == null) {
	    astr = cmd;
	}
	mi.setActionCommand(astr);
	Action a = getAction(astr);
	if (a != null) {
	    mi.addActionListener(a);
	    a.addPropertyChangeListener(createActionChangeListener(mi));
	    mi.setEnabled(a.isEnabled());
	} else {
	    mi.setEnabled(false);
	}
	menuItems.put(cmd, mi);
	return mi;
    }

 public void removeUndo() {
 	editor.getDocument().removeUndoableEditListener(undoHandler);
}

 public void setUndo() {
	editor.getDocument().addUndoableEditListener(undoHandler);
	}

    /**
     * Fetch the menu item that was created for the given
     * command.
     * @param cmd  Name of the action.
     * @returns item created for the given command or null
     *  if one wasn't created.
     */
    protected JMenuItem getMenuItem(String cmd) {
	return (JMenuItem) menuItems.get(cmd);
    }

    protected Action getAction(String cmd) {
	return (Action) commands.get(cmd);
    }

    protected String getResourceString(String nm) {
	String str;
	try {
	    str = resources.getString(nm);
	} catch (MissingResourceException mre) {
	    str = null;
	}
	return str;
    }

    protected URL getResource(String key) {
	String name = getResourceString(key);
	if (name != null) {
	    URL url = this.getClass().getResource(name);
	    return url;
	}
	return null;
    }

    protected JMenuBar getMenubar() {
	return menubar;
    }


    /**
     * Take the given string and chop it up into a series
     * of strings on whitespace boundries.  This is useful
     * for trying to get an array of strings out of the
     * resource file.
     */
    protected String[] tokenize(String input) {
	Vector v = new Vector();
	StringTokenizer t = new StringTokenizer(input);
	String cmd[];

	while (t.hasMoreTokens())
	    v.addElement(t.nextToken());
	cmd = new String[v.size()];
	for (int i = 0; i < cmd.length; i++)
	    cmd[i] = (String) v.elementAt(i);

	return cmd;
    }

    /**
     * Create the menubar for the app.  By default this pulls the
     * definition of the menu from the associated resource file. 
     */
    protected JMenuBar createMenubar() {
	JMenuItem mi;
	JMenuBar mb = new JMenuBar();

	String[] menuKeys = tokenize(getResourceString("menubar"));
	for (int i = 0; i < menuKeys.length; i++) {
	    JMenu m = createMenu(menuKeys[i]);
	    if (m != null) {
		mb.add(m);
	    }
	}
	return mb;
    }

    /**
     * Create a menu for the app.  By default this pulls the
     * definition of the menu from the associated resource file.
     */
    protected JMenu createMenu(String key) {
	String[] itemKeys = tokenize(getResourceString(key));
	JMenu menu = new JMenu(getResourceString(key + "Label"));
	for (int i = 0; i < itemKeys.length; i++) {
	    if (itemKeys[i].equals("-")) {
		menu.addSeparator();
	    } else {
		JMenuItem mi = createMenuItem(itemKeys[i]);
		menu.add(mi);
	    }
	}
	return menu;
    }

    // Yarked from JMenu, ideally this would be public.
    protected PropertyChangeListener createActionChangeListener(JMenuItem b) {
	return new ActionChangedListener(b);
    }

    // Yarked from JMenu, ideally this would be public.
    private class ActionChangedListener implements PropertyChangeListener {
        JMenuItem menuItem;
        
        ActionChangedListener(JMenuItem mi) {
            super();
            this.menuItem = mi;
        }
        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            if (e.getPropertyName().equals(Action.NAME)) {
                String text = (String) e.getNewValue();
                menuItem.setText(text);
            } else if (propertyName.equals("enabled")) {
                Boolean enabledState = (Boolean) e.getNewValue();
                menuItem.setEnabled(enabledState.booleanValue());
            }
        }
    }
	

    /**
     * Listener for the edits on the current document.
     */
    protected UndoableEditListener undoHandler = new UndoHandler();

    /** UndoManager that we add edits to. */
    protected UndoManager undo = new UndoManager();

    /**
     * Suffix applied to the key used in resource file
     * lookups for an image.
     */
    public static final String imageSuffix = "Image";

    /**
     * Suffix applied to the key used in resource file
     * lookups for a label.
     */
    public static final String labelSuffix = "Label";

    /**
     * Suffix applied to the key used in resource file
     * lookups for an action.
     */
    public static final String actionSuffix = "Action";

    /**
     * Suffix applied to the key used in resource file
     * lookups for tooltip text.
     */
    public static final String tipSuffix = "Tooltip";

    public static final String openAction = "open";
    public static final String newAction  = "new";
    public static final String saveAction = "save";

    class UndoHandler implements UndoableEditListener {

	/**
	 * Messaged when the Document has created an edit, the edit is
	 * added to <code>undo</code>, an instance of UndoManager.
	 */
        public void undoableEditHappened(UndoableEditEvent e) {
	    undo.addEdit(e.getEdit());
	    undoAction.update();
	    redoAction.update();
	}
    }

  
    // --- action implementations -----------------------------------

    private UndoAction undoAction = new UndoAction();
    private RedoAction redoAction = new RedoAction();

    /**
     * Actions defined by the Notepad class
     */
    private Action[] defaultActions = {
	new NewAction(),
	new OpenAction(),
	new SaveAction(),
        undoAction,
        redoAction
    };

    class UndoAction extends AbstractAction {
	public UndoAction() {
	    super("Undo");
	    setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
	    try {
		undo.undo();
	    } catch (CannotUndoException ex) {
		System.out.println("Unable to undo: " + ex);
		ex.printStackTrace();
	    }
	    update();
	    redoAction.update();
	}

	protected void update() {
	    if(undo.canUndo()) {
		setEnabled(true);
		putValue(Action.NAME, undo.getUndoPresentationName());
	    }
	    else {
		setEnabled(false);
		putValue(Action.NAME, "Undo");
	    }
	}
    }

    class RedoAction extends AbstractAction {
	public RedoAction() {
	    super("Redo");
	    setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
	    try {
		undo.redo();
	    } catch (CannotRedoException ex) {
		System.out.println("Unable to redo: " + ex);
		ex.printStackTrace();
	    }
	    update();
	    undoAction.update();
	}

	protected void update() {
	    if(undo.canRedo()) {
		setEnabled(true);
		putValue(Action.NAME, undo.getRedoPresentationName());
	    }
	    else {
		setEnabled(false);
		putValue(Action.NAME, "Redo");
	    }
	}
    }

    class OpenAction extends NewAction {

	OpenAction() {
	    super(openAction);
	}

        public void actionPerformed(ActionEvent e) {
	    Frame frame = getFrame();
	    if (fileDialog == null) {
		fileDialog = new FileDialog(frame);
	    }
	    fileDialog.setMode(FileDialog.LOAD);
	    fileDialog.show();

	    String file = fileDialog.getFile();
	    if (file == null) {
		return;
	    }
	    String directory = fileDialog.getDirectory();
	    File f = new File(directory, file);
	    if (f.exists()) {
		Document oldDoc = getEditor().getDocument();
		if(oldDoc != null)
		    oldDoc.removeUndoableEditListener(undoHandler);
		getEditor().setDocument(new PlainDocument());
		frame.setTitle(file);
		Thread loader = new FileLoader(f, editor.getDocument());
		loader.start();
	    }
	}
    }
    
    class NewAction extends AbstractAction {

	NewAction() {
	    super(newAction);
	}

	NewAction(String nm) {
	    super(nm);
	}

        public void actionPerformed(ActionEvent e) {
	    Document oldDoc = getEditor().getDocument();
	    if(oldDoc != null)
		oldDoc.removeUndoableEditListener(undoHandler);
	    getEditor().setDocument(new PlainDocument());
	    getEditor().getDocument().addUndoableEditListener(undoHandler);
	    revalidate();
	}
    }

    class SaveAction extends AbstractAction {

	SaveAction() {
	    super(saveAction);
	}

	SaveAction(String nm) {
	    super(nm);
	}

        public void actionPerformed(ActionEvent e) {

	    Frame frame = getFrame();
	    String result;

		result = JOptionPane.showInputDialog(frame, "name of Moo note and property:");

		if(result != null) {
		    Object[] message = new Object[1];
		    message[0] = "Thank you for this info ;-)";
//		    message[1] = result;
		    JOptionPane.showMessageDialog(frame, message);
		}

	try {	   
		String	theWholeThing = getEditor().getDocument().getText(0,getEditor().getDocument().getLength());
		String theOneLiner = "";
		String toMoo = "@set "+result+" to {";
		boolean isIt = false;
		for (int i = 0; i < theWholeThing.length(); i++) {
			if (theWholeThing.charAt(i) == '"') { theOneLiner = theOneLiner + "\\"; }
			if (theWholeThing.charAt(i) == '\n')
			{	isIt = true;
				toMoo = toMoo + "\"" + theOneLiner + "\", ";
				theOneLiner = "";
			} else
			{ theOneLiner = theOneLiner + theWholeThing.charAt(i);
			}
		}
		
		if (isIt == false) toMoo = toMoo + "\"" + theOneLiner + "\", ";
	
		toMoo = toMoo.substring(0,toMoo.length()-2) + "}";
		
		thisBang.writeToSocket(toMoo);
		}	
	    catch (BadLocationException ee) {
				System.err.println(ee.getMessage());
	   			 }

	
	}
    }


    /**
     * Thread to load a file into the text storage model
     */
    class FileLoader extends Thread {

	FileLoader(File f, Document doc) {
	    setPriority(4);
	    this.f = f;
	    this.doc = doc;
	}

        public void run() {
	    try {
		// try to start reading
		Reader in = new FileReader(f);
		char[] buff = new char[4096];
		int nch;
		while ((nch = in.read(buff, 0, buff.length)) != -1) {
		    doc.insertString(doc.getLength(), new String(buff, 0, nch), null);
		}

		// we are done... get rid of progressbar
		doc.addUndoableEditListener(undoHandler);
	    }
	    catch (IOException e) {
		System.err.println(e.toString());
	    }
	    catch (BadLocationException e) {
		System.err.println(e.getMessage());
	    }
	}

	Document doc;
	File f;
    }
}
    