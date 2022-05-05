package th1;
// gui 1 so
import javacard.framework.*;

public class th1 extends Applet
{

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new th1().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	public void process(APDU apdu)
	{

		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		// doc du lieu vao (chinh trang thai ve doc du lieu)
		short byteRead = (short)(apdu.setIncomingAndReceive());
		
		// sua doi byteRead vao vi tri thu 3 cua buf (1 short chiem 2 byte)
		
		// chuyen du lieu ra
		// dat JCRE sang che do gui du lieu va nhan do dai du kien cua phan hoi  (Le)
		short le = apdu.setOutgoing();
		Util.setShort(buf,(short)3,byteRead);
		
		// thiet lp nhan 10 don vi
		apdu.setOutgoingLength((short)10);
		
		// gui byte tu 0 den 10
		apdu.sendBytes((short)0,(short)10);
	}

}
