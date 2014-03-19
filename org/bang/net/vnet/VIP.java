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

public final class VIP {
    static final short	QUIT = -1;
    static final short	MESSAGE = -2;
    static final short	ADD_OBJECT = -3;
    static final short	REMOVE_OBJECT = -4;
    static final short	PRIVATE_MESSAGE = -5;
    static final short	CREATE_OBJECT = -6;
    static final short	USER_INFO = -7;

    static final short	POSITION = 0;
    static final short	ORIENTATION = 1;
    static final short	SCALE = 2;
    static final short	NAME = 3;

    // this is the number of fields reserved by the VIP protocol
    static final short	NUM_FIELDS = 4;

    static String fieldName(short value) {
        switch (value) {
	  case QUIT:		return "QUIT";
	  case MESSAGE:		return "message";
	  case ADD_OBJECT:	return "add_object";
	  case REMOVE_OBJECT:	return "remove_object";
	  case PRIVATE_MESSAGE:	return "private_message";
	  case CREATE_OBJECT:	return "create_object";
	  case USER_INFO:	return "user_info";

	  case POSITION:	return "position";
	  case ORIENTATION:	return "orientation";
	  case SCALE:		return "scale";
	  case NAME:		return "name";
	  default:		return String.valueOf(value);
	}
    }

    static String msgToString(int vid, short field, VField value) {
	return vid + " " + fieldName(field) + " " + value;
    }
}
