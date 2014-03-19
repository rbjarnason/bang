
package org.bang.media;

import javax.media.rtp.*;
import javax.media.*;
import javax.media.protocol.*;
import java.io.*;
import java.net.*;
import com.sun.media.ui.*;

public class VideoChat implements ControllerListener{
	String address = "";
	String media = "video";
	int port = 49200;
	RTPSocket rtpsocket = null;
	RTPIODataSource rtcpsource = null;
	PlayerWindow playerWindow;
	Player player = null;
	private  int maxsize = 2000;
	
	UDPHandler  rtp = null;
	UDPHandler rtcp = null;
	
	public VideoChat(String address, int port){
		
		this.address = address;
		this.port = port;
		System.out.println("address="+address+" port="+String.valueOf(port));
		try {
			rtpsocket = new RTPSocket();
			String content = "rtpraw/" + media;
			rtpsocket.setContentType(content);
			rtp = new UDPHandler(address, port);
			rtpsocket.setOutputStream(rtp);
			rtcp = new UDPHandler(address, port +1);
			rtcpsource = rtpsocket.getControlChannel();
			rtcpsource.setOutputStream(rtcp);
			rtcpsource.setInputStream(rtcp);
			if (media.equals("audio"))
				EncodingUtil.Init(rtpsocket);
			try{
				rtpsocket.connect();
				player = Manager.createPlayer(rtpsocket);
			} catch (NoPlayerException e){
				System.err.println(e.getMessage());
				return;
			}
			catch (IOException e){
				System.err.println(e.getMessage());
				return;
			}
			if (player != null){
				player.addControllerListener(this);
				playerWindow = new PlayerWindow(player);
			}
		}
		catch (Exception e) { System.out.println("could not connect to RTP video stream ;("); }
	}
	public synchronized void controllerUpdate(ControllerEvent ce) {
		if ((ce instanceof DeallocateEvent) ||
				(ce instanceof ControllerErrorEvent)){
			if (rtp != null)
				rtp.close();
			if (rtcp != null)
				rtcp.close();
		}
		
	}
	
	private DatagramSocket InitSocket(String sockaddress, int sockport){
		InetAddress addr = null;
		DatagramSocket sock = null;
		try{
			addr = InetAddress.getByName(sockaddress);
			if (addr.isMulticastAddress()){
				MulticastSocket msock = new MulticastSocket(sockport);
				msock.joinGroup(addr);
				sock = (DatagramSocket)msock;		
			}else{		
				sock = new
				DatagramSocket(sockport,addr);
			}
			return sock;
		}
		catch (SocketException e){
			e.printStackTrace();
			return null;
		}
		catch (UnknownHostException e){
			e.printStackTrace();
			return null;
		}
		catch (IOException e){
			e.printStackTrace();
			return null;
		}
		
	}
	public class UDPHandler extends Thread
	implements PushSourceStream, PushDestStream{
		DatagramSocket mysock = null;
		DatagramPacket dp = null;
		SourceTransferHandler outputhandler = null;
		String myaddress = null;
		int myport;
		boolean closed = false;
		
		public UDPHandler(String haddress, int hport){
			myaddress = haddress;
			myport = hport;
			mysock = InitSocket(myaddress,myport);	    	    
			setDaemon(true);
			start();
		}
		
		public void run(){
			int len;
			while(true){
				if (closed){
					cleanup();
					return;
				}
				try{
					do{
						dp = new DatagramPacket(new byte[maxsize],maxsize);
						mysock.receive(dp);
						if (closed){
							cleanup();
							return;
						}
						len = dp.getLength();
						if (len > (maxsize >> 1))  
							maxsize = len << 1;
					}
					while (len >= dp.getData().length);
				}catch (Exception e){
					cleanup();
					return;
				}
				
				if (outputhandler != null)
					outputhandler.transferData(this);
			}
		}
		public void close(){
			closed = true;
		}
		private void cleanup(){
			mysock.close();
			stop();
		}
		
		public Object[] getControls() {
			return new Object[0];
		}
		
		public Object getControl(String controlName) {
			return null;
		}
		public  ContentDescriptor getContentDescriptor(){
			return null;
		}
		public long getContentLength(){
			return SourceStream.LENGTH_UNKNOWN;
		}
		public boolean endOfStream(){
			return false;
		}
		
		public int read(byte buffer[],
				int offset,
				int length){
			System.arraycopy(dp.getData(),
					0,
					buffer,
					offset,
					dp.getLength());
			return dp.getData().length;
			
		}		 
		
		public int getMinimumTransferSize(){
			return dp.getLength();
		}
		public void setTransferHandler(SourceTransferHandler transferHandler){
			this.outputhandler = transferHandler;
		}
		
		public int write(byte[] buffer,
				int offset,
				int length){
			InetAddress addr = null;
			try{
				addr = InetAddress.getByName(myaddress);
			}catch (UnknownHostException e){
			}
			DatagramPacket dp = new DatagramPacket(buffer,length,addr,myport);
			try{
				mysock.send(dp);
			}catch (IOException e){}
			return dp.getLength();
		}
		
	}
	
	
}