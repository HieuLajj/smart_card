// em chua cho tham so dau vao len select van lay aid mac dinh
// thiet lap cac so diem tung mon 1 
		// Send: 00 00 01 07 09 11 22 33 44 55 66 77 88 33
		// Recv: 00 00 02 07 90 00
		// Time used: 10.000 ms
		// Send: 00 00 02 08 09 11 22 33 44 55 66 77 88 33
		// Recv: 00 00 04 08 90 00
		// Time used: 13.000 ms
		// Send: 00 00 03 03 09 11 22 33 44 55 66 77 88 33
		// Recv: 00 00 06 03 90 00
		// Time used: 15.000 ms
		// Send: 00 00 03 03 09 11 22 33 44 55 66 77 88 33
		// Recv: 90 00
		// Time used: 15.000 ms
		// Send: 00 00 04 05 09 11 22 33 44 55 66 77 88 33
		// Recv: 00 00 08 05 90 00
		// Time used: 10.000 ms
		// Send: 00 00 06 05 09 11 22 33 44 55 66 77 88 33
		// Recv: 00 00 0A 05 90 00
		// Time used: 13.000 ms
		// Send: 00 00 07 04 09 11 22 33 44 55 66 77 88 33
		// Recv: 00 00 0C 04 90 00
		// Time used: 12.000 ms
		// Send: 00 00 08 03 09 11 22 33 44 55 66 77 88 33
		// Recv: 00 00 0E 03 90 00
		// Time used: 16.000 ms
		// Send: 00 00 09 10 09 11 22 33 44 55 66 77 88 33
		// Recv: 00 00 10 10 90 00
		// Time used: 10.000 ms
		// Send: 00 00 10 02 09 11 22 33 44 55 66 77 88 33
		// Recv: 00 00 12 02 90 00
		// Time used: 11.000 ms
		// Send: 00 00 12 02 09 11 22 33 44 55 66 77 88 33
		// Recv: 90 00
		// Time used: 11.000 ms
// in deim ra man hinh
	// Send: 00 01 12 02 09 11 22 33 44 55 66 77 88 33
	// Recv: 01 07 02 08 03 03 04 05 06 05 07 04 08 03 09 10 10 02 90 00
	// Time used: 13.000 ms
// sua diem co id 01- diem 07 thanh 01-01
	// Send: 00 02 01 01 09 11 22 33 44 55 66 77 88 33
	// Recv: 00 00 0B 01 90 00
	// Time used: 14.000 ms
	// Send: 00 01 01 01 09 11 22 33 44 55 66 77 88 33
	// Recv: 01 01 02 08 03 03 04 05 06 05 07 04 08 03 09 10 10 02 90 00
	// Time used: 15.000 ms
// xoa diem co id 01
	// Send: 00 03 01 01 09 11 22 33 44 55 66 77 88 33
	// Recv: 90 00
	// Time used: 14.000 ms
	// Send: 00 01 01 01 09 11 22 33 44 55 66 77 88 33
	// Recv: 02 08 03 03 04 05 06 05 07 04 08 03 09 10 10 02 10 02 90 00
	// Time used: 9.000 ms




package th4_1;

import javacard.framework.*;

public class baitapvenha extends Applet
{
	final static byte SV_ID_LENGTH = (byte)0x04;
	private static byte[] sinhVien;
	private static byte soLuongMonThi;
	byte cLen,aLen;
	public short e= (short)0;
	public static byte[] diemThi;
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new baitapvenha(bArray,bOffset,bLength);
	}
	
	private baitapvenha(byte[] bArray, short bOffset, byte bLength){
		byte iLen = bArray[bOffset];
		if(iLen ==0){
			register();
		}else{
			register(bArray, (short)(bOffset+1), iLen);
		}
		bOffset = (short)(bOffset+iLen+1);
		cLen = bArray[bOffset];
		bOffset = (short)(bOffset+cLen+1);
		aLen = bArray[bOffset];
		bOffset = (short)(bOffset +1);
		if(aLen !=0){
			sinhVien = new byte[SV_ID_LENGTH];
			Util.arrayCopy(bArray,bOffset,sinhVien,(byte)0,SV_ID_LENGTH);
			bOffset +=SV_ID_LENGTH;
			soLuongMonThi = bArray[bOffset];
		}else{
			sinhVien = new byte[]{'S','V','0','1'};
			soLuongMonThi=(byte)0x09;
		}
		//diemThi =new byte[]{1,2,3,4,5,6,7,8};
		short c= (short)((short)2*soLuongMonThi);
		diemThi =new byte[c];
	}

	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		//diemThi = new byte[1,2,3,4,5,6,7,8,9,10,11,12];
		short byteRead = apdu.setIncomingAndReceive();
		//diem thi
		byte d =buf[ISO7816.OFFSET_P1];
		//gay nho
		switch (buf[ISO7816.OFFSET_INS])
		{
			// nhap diem
			case (byte)0x00:
				short m=(short)0;
				if((short)e<(short)diemThi.length){
						// kiem tra diem co trong mang diem thi chua
					for(short i =(short)0;i<(short)(diemThi.length);i+=2){
						if(d==diemThi[i]){
							m=(short)1;
							break;
						}
					}
					if(m==(short)0){
						Util.arrayCopy(buf,(short)2,diemThi,(short)e,(short)2);
						e=(short)(e+(short)2);
						Util.setShort(buf,(short)1,(short)e);
						apdu.setOutgoing();
						apdu.setOutgoingLength((short)4);
						apdu.sendBytes((short)0,(short)4);
					}
					
				}
				break;
			// in diem
			case (byte)0x01:
				Util.arrayCopy(diemThi,(short)0,buf,(short)0,(short)diemThi.length);
				short le= apdu.setOutgoing();
				apdu.setOutgoingLength((short)diemThi.length);
				apdu.sendBytes((short)0,(short)diemThi.length);
				
				break;
			// sua diem
			case (byte) 0x02:
				// kiem tra diem co trong mang diem thi chua
				for(short i =(short)0;i<(short)(diemThi.length);i+=2){
					if(d==diemThi[i]){
						short g= (short)(i+1);
						Util.arrayCopy(buf,(short)3,diemThi,(short)g,(short)1); //short 3 laf p2 diem
						Util.setShort(buf,(short)1,(short)11);
						apdu.setOutgoing();
						apdu.setOutgoingLength((short)4);
						apdu.sendBytes((short)0,(short)4);	
					}
				}										
				break;
			//xoa diem
			case (byte) 0x03:
				// kiem tra diem co trong mang diem thi chua
				for(short i =(short)0;i<(short)(diemThi.length);i+=2){
					if(d==diemThi[i]){
						
						for(short g= (short)(i);g<(short)((diemThi).length-2);g+=1){
							diemThi[g]=diemThi[g+2];
						}
						// Util.arrayCopy(diemThi,(short)g,diemThi,(short)g,(short)1); //short 3 laf p2 diem
						// Util.setShort(buf,(short)1,(short)11);
						// apdu.setOutgoing();
						// apdu.setOutgoingLength((short)4);
						// apdu.sendBytes((short)0,(short)4);	
					}
				}										
				break;
			default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	
	}

}
