// copyright (c) 1997,1998 stephen f. white
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
package org.bang.net.vnet;

import java.awt.*;
import java.util.*;

public class UserList extends java.awt.List {
    protected Hashtable	vid2name = new Hashtable();
    protected Hashtable	name2vid = new Hashtable();

    UserListObserver		observer;

    public UserList(UserListObserver observer) {
	this.observer = observer;
    }

    public synchronized void addUser(int vid, String name) {
	String oldName = (String) vid2name.get(new Integer(vid));
	if (oldName != null) {
	    name2vid.remove(oldName);
	    replaceItem(name, findItem(oldName));
	} else {
	    vid2name.put(new Integer(vid), name);
	    addItem(name);
	}
	vid2name.put(new Integer(vid), name);
	name2vid.put(name, new Integer(vid));
    }

    public synchronized void removeUser(int vid) {
	String name = (String) vid2name.get(new Integer(vid));
	int pos = findItem(name);
	if (pos >= 0) {
	    if (pos == getSelectedIndex()) {
		observer.onUserListDeselect();
	    }
	    delItem(pos);
	    vid2name.remove(new Integer(vid));
	    name2vid.remove(name);
	}
    }

    public synchronized void removeAllUsers() {
	clear();
	vid2name.clear();
	name2vid.clear();
    }

    public void selectUser(int vid) {
	int	pos = findItem((String) vid2name.get(new Integer(vid)));

	if (pos >= 0) {
	    select(pos);
	    observer.onUserListSelect(vid);
	}
    }

    // return the VID of the currently selected
    // item, or -1 if nothing selected
    private int getSelectedUser() {
	return ((Integer) name2vid.get(getSelectedItem())).intValue();
    }

    private int findItem(String name) {
	for (int i = 0; i < countItems(); i++) {
	    if (getItem(i).equals(name)) return i;
	}
	return -1;
    }

    public boolean handleEvent(Event evt) {
	switch(evt.id) {
	  case Event.LIST_SELECT:
	    observer.onUserListSelect(getSelectedUser());
	    return true;
	  case Event.LIST_DESELECT:
	    observer.onUserListDeselect();
	    return true;
	  default:
	    return super.handleEvent(evt);
	}
    }
}
