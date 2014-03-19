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

import java.io.IOException;
import java.io.EOFException;
import java.net.Socket;

class ClientThread extends Thread implements WriterThreadObserver {
    VFieldInputStream	in;
    VFieldOutputStream	out;
    boolean		connected;
    Socket		socket;
    String		username;
    String		avatarURL;
    ClientThreadObserver	observer;
    String		host;
    int			port;
    int			userVid;
    final static boolean		debug = false;
    WriterThread	writer;

    ClientThread(ClientThreadObserver observer, String host, int port,
		    String username, String avatarURL) {
	this.observer = observer;
	this.host = host;
	this.port = port;
        this.username = username;
        this.avatarURL = avatarURL;
    }

    public void run() {

        try { 
	    socket = new Socket(host, port);
	    in = new VFieldInputStream(socket.getInputStream());
	    out = new VFieldOutputStream(socket.getOutputStream());
	} catch (IOException e) {
	    System.err.println("Failed connect:  " + host + ":" + port + ", " + e.getMessage());
	    observer.onError(e.getMessage());
	    return;
	}

	try {
	    out.writeUTF(username);
	    out.writeUTF(avatarURL);
	    userVid = in.readInt();
	} catch (IOException e) {
	    System.err.println("ClientThread blew up trying to send username");
	    return;
	}

        connected = true;

	observer.onNetConnect(userVid);

	if (userVid >= 0) {
	    writer = new WriterThread(out, this);
	    writer.start();

	    System.err.println(userVid + ":  connected");
	    while (connected) {
		try {
		    int		vid = in.readInt();
		    short	field = in.readShort();
		    VField	value = in.readField();

		    if (debug) {
			System.err.println(userVid + ":  recd:  " +
					   VIP.msgToString(vid, field, value));
		    }
		    observer.onNetInput(vid, field, value);
		} catch (EOFException e) {
		    connected = false;
		} catch (IOException e) {
		}
	    }
	    observer.onNetDisconnect();
	    System.err.println(userVid + ":  disconnected");
	    writer.stopThread();
	    writer = null;
	}
    }

    void send(int vid, short field, VField value)
    {
	if (debug) {
	    System.err.println(userVid + ":  sent:  " + VIP.msgToString(vid, field, value));
	}
	writer.send(new Message(vid, field, value));
    }

    // WriterThreadObserver callback
    public void onError(Exception e)
    {
        System.err.println(e + ", disconnecting");
        connected = false;
    }
}

