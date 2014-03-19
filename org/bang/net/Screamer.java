package org.bang.net;

import java.net.*;
import java.io.*;
import java.awt.Frame;

import javax.media.j3d.*;

/** 
 * This is the multicast channel. All communication goes through here.
 *   There is a very specific data format for each type of message.
 *   The first int defines the type of message [ MESSAGE | TRANSFORM | QUIT ]
 *   If the type is message the rest of the data is a UTF string that is the
 *   message being sent.
 *   If the type is transform the rest of the data is an array of 16 doubles
 *   that represent the new transformation matrix.
 *   If the type is quit there is no more data. The sender of the message
 *   is assumed to be the participant that is quiting.
 *   Please note that a participant joins the world just by sending one 
 *   TRANSFORM message to the world.
 *   @author Chris Heistad (heistad@ncsa.uiuc.edu or heistad@poboxes.com)
 *   @author Steve Pietrowicz (srp@ncsa.uiuc.edu or spietrowicz@yahoo.com)
 *   changed by
 *   @author Róbert Bjarnason (robert@this.is)
 */


public class Screamer implements Runnable{

  /*These are the constants that define what type of message this is*/

  static final int MESSAGE 			=0;
  static final int TRANSFORM 		=1;
  static final int QUIT				=2;
  static final int ONLOGIN			=3;
  static final int NEWAVATAR		=4; 

  MulticastSocket socket; //Our multicast socket for sending
  InetAddress address;    //The multicast address we are sending/resceiving on
  int port; //The port we are RECIEVING on
  byte ttl; //The number of hops that our packets will travel
//  String theName;

  Thread thread; //The thread that is receiving messages and processing them.
  PeopleManager pm;//The object we are reporting participant events to.

  InetAddress localhost; //Our machine ip address.
  TransformGroup me = null; //Our view platform transform group

  String myName;
  String myURL;

  /** Our only constructor.
   *	@param address The multicast address to send and receive on.
   *	@param port The port to receive messages on.
   *	@param ttl The number of hops to use in the transmission.
   *	@param theName The name of the Moo user
   *	@exception IOException If something goes wrong with MulticastSocket.
   */

  public Screamer(String address, String port, String ttl, String theName, String theURL) throws IOException{
	this.address = InetAddress.getByName(address);
	this.port = Integer.parseInt(port);
	this.ttl = (byte) Integer.parseInt(ttl);
	this.myName = theName;
	this.myURL = theURL;
	socket = new MulticastSocket(this.port);
	socket.joinGroup(this.address);
	socket.setTTL(this.ttl);

	//Start the receiving thread.
	thread = new Thread(this);
	thread.start();

	//cache the local host address
	//because we will use this later to cull messages we sent.
	try{ localhost = InetAddress.getLocalHost(); }
	catch(Exception e){}
		
  }

  /** 
   * Allows the branch group to be specified. This is the branch group upon
   * which all geometry will hang off. This function also constructs the people
   * manager that will track and represent all of the participants.
   * @param bg The branch group that we will hang all our geometry off.
   */
  public void setBranchGroup(BranchGroup bg){
	pm = new PeopleManager(bg);
	pm.setScreamer(this);
	if(me!=null)
		pm.setViewTransform(me);
  }


  /**
   * Allows our ViewTransform to be set. This is the transform that is sent
   * whenever we move our view point.
   * @param group The nex transform group.
   */
  public void setMyViewTransform(TransformGroup group){
	me = group;
	if(pm!=null)
		pm.setViewTransform(me);
  }

  /**
   * This method sends a transform out on the multicast channel.
   * @param trans The transform to send.
   */
  public void send(TransformGroup trans){
	encode(trans);	
  }

  /** 
   * This method sends a message that indicates that this member is 
   * quiting the world. This is unreliable and it is assumed that this
   * message may NOT be received.
   */
  public void quit(){
    try{
	ByteArrayOutputStream b = new ByteArrayOutputStream();
	DataOutputStream d = new DataOutputStream(b);

	d.writeInt(QUIT);
	d.flush();

	byte[] data = b.toByteArray();

	DatagramPacket packet = new DatagramPacket(
		data, data.length,address,port);

	socket.send(packet);	
    }
    catch(Exception e){
	  System.out.println("Screamer.quit " + e);
    }
  }

  /** 
   * Encodes a string of text and sends it out over the multicast channel.
   * This type of text is displayed in the HeadsUpDisplay.
   * @param message The message to be sent.
   */

  public void encode(TransformGroup trans, String message){
    try{
	ByteArrayOutputStream b = new ByteArrayOutputStream();
	DataOutputStream d = new DataOutputStream(b);
    System.out.println("encode " + message);
	d.writeInt(ONLOGIN);
	float[] array = new float[16];
	Transform3D transform = new Transform3D();
	trans.getTransform(transform);
	transform.get(array);
	
	for(int i = 0; i<16; i++)
		d.writeFloat(array[i]);

	d.writeUTF(message);

	d.flush();
	byte[] data = b.toByteArray();

	DatagramPacket packet = new DatagramPacket(
		data, data.length,address,port);

	socket.send(packet);	
    }
    catch(Exception e){
	System.out.println(e);
    }
  }

  /** 
   * encodes and sends a transform over the multicast channel.
   * @param trans The transform group to send.
   */

  public void encode(TransformGroup trans){
    try{
	ByteArrayOutputStream b = new ByteArrayOutputStream();
	DataOutputStream d = new DataOutputStream(b);

	d.writeInt(TRANSFORM);

	float[] array = new float[16];
	Transform3D transform = new Transform3D();
	trans.getTransform(transform);
	transform.get(array);
	
	for(int i = 0; i<16; i++)
		d.writeFloat(array[i]);

	d.flush();
	byte[] data = b.toByteArray();

	DatagramPacket packet = new DatagramPacket(
		data, data.length,address,port);
	socket.send(packet);	
    }
    catch(Exception e){
	System.out.println(e);
    }
  }

  /** 
   * The highest most portion of the packet decoder. This method just
   * figures out what type of message is contained in the packet.
   * @param data The actual packet data.
   * @param length The byte length of the data.
   * @param address The address from which the data was recieved.
   */

  public void decode(byte[] data, int length,InetAddress address){
     try{
	Transform3D transform;

	ByteArrayInputStream b = new ByteArrayInputStream(data,0,length);
	DataInputStream d = new DataInputStream(b);
	switch(d.readInt()){
	  case TRANSFORM:
		decode_TRANSFORM(d,address);
		break;
	  case ONLOGIN:
	  	decode_ONLOGIN(d,address);
	  	break;
	  case QUIT:
		pm.quit(address.toString());
		break;
	  default:
		System.out.println("Screamer.decode unknown message type");
	}
      }
      catch(Exception e){
	System.out.println("Screamer.decode " + e);
      }
  }

 /** 
   * This is the method that handles the processing of the message type.
   * The message is prepended with a member indentifier and diplayed in the
   * heads up display.
   * @param address The address of the sender.
   * @param d The stream that conatins the data.
   */
 public void decode_ONLOGIN(DataInputStream d, InetAddress address){
   
  // Object o = urls.get(address);
  
  // if (o==null && !localhost.equals(address)) { 	 
    try{
    float[] array = new float[16];
	
	for(int i =0 ; i<16; i++)
		array[i] = d.readFloat();

	Transform3D transform = new Transform3D(array);
	
	String string = d.readUTF();
	String name = address.getHostName();

    int idx = string.indexOf(';');
    int oldidx = 0;
    String theName = string.substring(oldidx, idx);
    oldidx = idx+1;
    idx = string.indexOf(';', idx+1);
	String avatarURL = string.substring(oldidx, idx);
	  System.out.println("decode " + string);
	pm.onLogin(address.toString(), transform, theName, avatarURL);
     }
     catch(Exception e){
	System.out.println("Screamer.decode_MESSAGE " + e);
     }
    // }
 }


 
  /** 
   * Decodes a transform packet and hands off the relavant information
   * to the people manager. The people manager is then responsible to 
   * update the position of the corrosponding avatar.
   * @param d The stream that contains the transform.
   * @param address The address of the sender.
   */

  public void decode_TRANSFORM(DataInputStream d, InetAddress address){
    try{
	float[] array = new float[16];
	
	for(int i =0 ; i<16; i++)
		array[i] = d.readFloat();

	Transform3D transform = new Transform3D(array);

	pm.update(address.toString(),transform);
     }
     catch(Exception e){
	System.out.println("Screamer.decode_TRANSFORM " + e);
     }
  }


  /** 
   * This is the code that receives packets and processes them.
   */
  public void run(){
    try{

	MulticastSocket socket = new MulticastSocket(port);
	socket.joinGroup(address);
	
	byte[] array = new byte[1600];
	while(true){
		DatagramPacket packet = new DatagramPacket(array,array.length);	
		socket.receive(packet);

		if(pm!=null  && !localhost.equals(packet.getAddress()))
			decode(packet.getData(),packet.getLength(),packet.getAddress());

	}
    }
    catch(Exception e){
	System.out.println(e);
    }

  }


}