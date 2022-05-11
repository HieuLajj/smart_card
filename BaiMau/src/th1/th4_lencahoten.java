package th1;

import javacard.framework.*;

public class th4_lencahoten extends Applet
{
	private byte[] name;
	private byte[] age;
	short b,c;

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new th4_lencahoten().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	
	}

	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		short byteRead = (short)apdu.setIncomingAndReceive();
		switch(buf[ISO7816.OFFSET_INS]){
		case (byte)0x00:
			// nhap ten
			b=(short)byteRead;
			name= new byte[b];
			Util.arrayCopy(buf,ISO7816.OFFSET_CDATA,name,(short)0,byteRead);
		case (byte)0x01:
			// nhap tuoi
			c=(short)byteRead;
			age = new byte[c];
			Util.arrayCopy(buf,ISO7816.OFFSET_CDATA,age,(short)0,byteRead);
		case (byte)0x05:
			short d =(short)(name.length + age.length);
			short le=apdu.setOutgoing();
			apdu.setOutgoingLength((short)d);
			Util.arrayCopy(name,(short)0,buf,(short)0,(short)name.length);
			apdu.sendBytes((short)0,(short)name.length);
			Util.arrayCopy(age,(short)0,buf,(short)0,(short)age.length);
			apdu.sendBytes((short)0,(short)age.length);
			
		}
		
	}

}
