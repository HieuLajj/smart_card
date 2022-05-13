/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package classicapplet1;

import javacard.framework.*;

/**
 *
 * @author laihi
 */
public class Manage extends Applet {

    
    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new Manage();
    }

    /**
     * Only this class's install method should create the applet object.s
     */
    protected Manage() {
        register();
    }
    byte []ten = {'h','i','e','u'};
    byte []ngaysinh = {1,2,1,1,2,0,0,0};
    short len;
    short len2;
    short len3;

    /**
     * Processes an incoming APDU.
     * 
     * @see APDU
     * @param apdu
     *            the incoming APDU
     */
    public void process(APDU apdu) {
        if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		apdu.setIncomingAndReceive();
		
		switch (buf[ISO7816.OFFSET_INS])
		{
		case (byte)0x00:
			len= (short)ten.length;
			Util.arrayCopy(ten,(short)0, buf, (short)0,len);
			apdu.setOutgoingAndSend((short)0,len);
			break;
		case (byte)0x11:
			len2= (short)ngaysinh.length;
			Util.arrayCopy(ngaysinh,(short)0, buf, (short)0,len2);
			apdu.setOutgoingAndSend((short)0,len2);
			break;
		case (byte)0x22:
			short nameLen=(short)ten.length;
			short birthdayLen = (short)ngaysinh.length;
			short total= (short)(nameLen+birthdayLen);
			short le = apdu.setOutgoing();
			apdu.setOutgoingLength(total);
			Util.arrayCopy(ten,(short)0,buf,(short)0,nameLen);
			apdu.sendBytes((short)0,nameLen);
			Util.arrayCopy(ngaysinh,(short)0,buf,(short)0,birthdayLen);
			apdu.sendBytes((short)0,birthdayLen);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
    }
}
