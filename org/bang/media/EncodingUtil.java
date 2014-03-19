package org.bang.media;

import javax.media.protocol.DataSource;
import javax.media.rtp.RTPControl;
import javax.media.rtp.BaseEncodingInfo;
import javax.media.rtp.session.RTPSessionManager;

import com.sun.media.ui.RTPSocket;

public class EncodingUtil {
	private static final String rtpcontrol = "javax.media.rtp.RTPControl";
	
	public static void Init(DataSource ds){
		RTPControl control = (RTPControl)ds.getControl(rtpcontrol);
		if (control == null){
			return;
		}
		Init(control);
	}
	public static void Init(RTPSessionManager mgr){
		BaseEncodingInfo info = null;
		// dvi at 44100 Hz
		info = new BaseEncodingInfo(18,"dvi",44100,
				1,4,
				null);
		mgr.addEncoding(info);
	}
	
	public static  void Init(RTPControl control){
		BaseEncodingInfo myinfo = null;
		if (control == null)
			return;
		myinfo = new BaseEncodingInfo(18,"dvi",44100, 1,4,null);
		control.addEncoding(myinfo);
	}
	public static void Init(RTPSocket rtpsocket) {
		// TODO Auto-generated method stub
		
	}
}
