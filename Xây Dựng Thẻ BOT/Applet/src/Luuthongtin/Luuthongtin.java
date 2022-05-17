package Luuthongtin;

import javacard.framework.*;


public class Luuthongtin extends Applet
{
	//CLA
	final static byte Bot_CLA = (byte) 0xB0;
	//INS
	final static byte INS_THONGTIN = (byte)0x00;
	final static byte INS_NAPANH = (byte)0x01;
	final static byte INS_ANH = (byte)0x02;
	final static byte INS_SETCOUNT = (byte)0x05;
	final static byte INS_COUNTANH = (byte)0x06;
	final static byte INS_INSERT = (byte)0x03;
	final static byte INS_COUNTINSERT = (byte)0x04;
	final static byte INS_SODU = (byte)0x10;
	final static byte INS_NAPTIEN =	(byte)0x20;
	final static byte INS_THANHTOAN = (byte)0x30;
	
	//Bien
	private static byte[] arrayhoten, arrayngaysinh, arraydiachi, arraybienks, image, size;
	final static byte phi = (byte) 0x03;
	byte balance = (byte) 0x0A;
	short countht, countns, countdc, countbks;
	 
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new Luuthongtin().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
		// arrayhoten = new byte[50];
		// arrayngaysinh = new byte[10];
		// arraydiachi = new byte[20];
		// arraybienks = new byte[20];
		image = new byte[10000];
		size = new byte[7];
	}
	
	
	
	public void process(APDU apdu)
	{
		
		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		apdu.setIncomingAndReceive();
			
				
			
		switch (buf[ISO7816.OFFSET_INS])
		{
		case INS_COUNTINSERT:
			short dataLen1 = (short)(buf[ISO7816.OFFSET_LC]&0xff);
			short flag1 = (short)1;
			countht = 0;
			countns = 0;
			countdc = 0;
			countbks = 0;
				for(short i = (short)(ISO7816.OFFSET_CDATA);i<(short)(ISO7816.OFFSET_CDATA +1+dataLen1);i++ ){
					if(buf[i]==(byte)0x21){
						flag1+=(short)1;
						continue;
					}
					if(flag1 ==(short)1){
						countht++;
					}
					else if(flag1 ==(short)2){
							countns++;
					}
					else if(flag1 ==(short)3){
						countdc++;
					}
					else if(flag1 ==(short)4){
						countbks++;
					}		
				}
			countht++;
			countns++;
			countdc++;
			break;	
		case INS_INSERT:
			short dataLen = (short)(buf[ISO7816.OFFSET_LC]&0xff);
			short flag = (short)1;
			arrayhoten = new byte[countht];
			arrayngaysinh = new byte[countns];
			arraydiachi = new byte[countdc];
			arraybienks = new byte[countbks];
			short tempIndex = (short)0;
				for(short i = (short)(ISO7816.OFFSET_CDATA);i<(short)(ISO7816.OFFSET_CDATA +1+dataLen);i++ ){
					if(buf[i]==(byte)0x21){
						if(flag ==(short)1){
							arrayhoten[tempIndex++]=buf[i];
						}
						else if(flag ==(short)2){
							arrayngaysinh[tempIndex++]=buf[i];
						}
						else if(flag ==(short)3){
							arraydiachi[tempIndex++]=buf[i];
						}
						flag+=(short)1;
						tempIndex = (short)0;
						continue;
					}
					if(flag ==(short)1){
						arrayhoten[tempIndex++]=buf[i];
					}
					else if(flag ==(short)2){
						arrayngaysinh[tempIndex++]=buf[i];
					}
					else if(flag ==(short)3){
						arraydiachi[tempIndex++]=buf[i];
					}
					else if(flag ==(short)4){
						arraybienks[tempIndex++]=buf[i];
					}					
				}
			break;			
		case INS_THONGTIN:			
			short lenhoten = (short) arrayhoten.length;
			short lenngaysinh = (short) arrayngaysinh.length;
			short lendiachi = (short) arraydiachi.length;
			short lenbienks = (short) arraybienks.length;
			short len = (short) (lenhoten + lenngaysinh + lendiachi + lenbienks);
			apdu.setOutgoing();
			apdu.setOutgoingLength(len);
			Util.arrayCopy(arrayhoten,(short)0,buf,(short)0,lenhoten);
			apdu.sendBytes((short)0, lenhoten);
			Util.arrayCopy(arrayngaysinh,(short)0,buf,(short)0,lenngaysinh);
			apdu.sendBytes((short)0, lenngaysinh);
			Util.arrayCopy(arraydiachi,(short)0,buf,(short)0,lendiachi);
			apdu.sendBytes((short)0, lendiachi);
			Util.arrayCopy(arraybienks,(short)0,buf,(short)0,lenbienks);
			apdu.sendBytes((short)0, lenbienks);
			break;
		case INS_SODU:
			apdu.setOutgoing();
			apdu.setOutgoingLength((short)2);
			buf[0] = (byte)(balance >> 8);
			buf[1] = (byte)(balance & 0xFF);
			Util.setShort(buf, (short)0, balance);
			apdu.sendBytes((short)0, (short)2);
			break;
		case INS_NAPTIEN:
			byte nap = buf[ISO7816.OFFSET_P1];
		
			JCSystem.beginTransaction();
			balance = (byte)(balance + nap);
		
			if ( nap > (byte)0x64)
			{
				ISOException.throwIt(ISO7816.SW_COMMAND_NOT_ALLOWED);
				JCSystem.abortTransaction();
			}
			
			
			// if ( balance > (byte)0x7F )
			// {
				// ISOException.throwIt(ISO7816.SW_COMMAND_NOT_ALLOWED);
				// JCSystem.abortTransaction();
			// }
			JCSystem.commitTransaction();
			
			break;
		case INS_THANHTOAN: insThanhToan(apdu);
			break;
		case INS_NAPANH:
			// CLA INS P1 P2 LC Data Le  
			short p1 = (short)(buf[ISO7816.OFFSET_P1]&0xff);
			short count1 = (short)(249 * p1);
			Util.arrayCopy(buf, ISO7816.OFFSET_CDATA, image, count1, (short)249);
			break;
		case INS_SETCOUNT:
			Util.arrayCopy(buf, ISO7816.OFFSET_CDATA, size, (short)0, (short)7);
			break;
		case INS_COUNTANH:
			Util.arrayCopy(size, (short)0, buf, (short)0, (short)(size.length));
			apdu.setOutgoingAndSend((short)0,(short)7);
			break;
		case INS_ANH:
			apdu.setOutgoing();
			short p = (short)(buf[ISO7816.OFFSET_P1]&0xff);
			short count = (short)(249 * p);
			apdu.setOutgoingLength((short)249);
			apdu.sendBytesLong(image, count, (short)249);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}
	
	

	private void insThanhToan(APDU apdu)
	{
		byte[] buf = apdu.getBuffer();
			
		if ( balance < phi)
			ISOException.throwIt(ISO7816.SW_COMMAND_NOT_ALLOWED);
		balance = (byte)(balance - phi);
	}

}
	