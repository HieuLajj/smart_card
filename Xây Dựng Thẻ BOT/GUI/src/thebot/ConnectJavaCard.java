package thebot;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import javax.smartcardio.*;

public class ConnectJavaCard {
    String strsodu = "999.999.999";
    String strhoten = "default";
    String strngaysinh = "default";
    String strdiachi = "default";
    String strbienks = "default";
    String linkanh = "C:\\Users\\ADMIN\\Desktop\\Javacard\\default.png";
    int tiennap;
    byte[] napanh;
    byte[] taianh;
    String[] output;
    byte[] strtoarr;
    byte[] arranh;

    public void connectapplet() {
  try {
   // Display the list of terminals
   TerminalFactory factory = TerminalFactory.getDefault();
   List<CardTerminal> terminals = factory.terminals().list();
   System.out.println("Terminals: " + terminals);

   // Use the first terminal
   CardTerminal terminal = terminals.get(0);

   // Connect wit hthe card
   Card card = terminal.connect("*");
   System.out.println("card: " + card);
   CardChannel channel = card.getBasicChannel();

   // Send Select Applet command
   byte[] aid = {(byte) 0x54,0x68,0x65,0x20,0x42,0x4F,0x54};
   ResponseAPDU answer = channel.transmit(new CommandAPDU(0x00, 0xA4, 0x04, 0x00, aid));
   System.out.println("answer: " + answer.toString());
   } catch(Exception e) {
   System.out.println("Ouch: " + e.toString());
  }
 }

    public void napanh() {
  try {
   // Display the list of terminals
   TerminalFactory factory = TerminalFactory.getDefault();
   List<CardTerminal> terminals = factory.terminals().list();

   // Use the first terminal
   CardTerminal terminal = terminals.get(0);

   // Use the first terminal

   // Connect wit hthe card
   Card card = terminal.connect("*");
   System.out.println("card: " + card);
   CardChannel channel = card.getBasicChannel();

   // Send Select Applet command
   byte[] aid = {(byte) 0x54,0x68,0x65,0x20,0x42,0x4F,0x54};
   ResponseAPDU answer = channel.transmit(new CommandAPDU(0x00, 0xA4, 0x04, 0x00, aid));
   System.out.println("answer: " + answer.toString());

   BufferedImage bImage = ImageIO.read(new File(linkanh));
   ByteArrayOutputStream bos = new ByteArrayOutputStream();
   ImageIO.write(bImage, "jpg", bos);
   napanh = bos.toByteArray();
   System.out.println(napanh.length);
   int soLan = napanh.length/249;
   String strsend = new String(soLan + "S" + napanh.length%249);
   System.out.println(strsend);
   byte[] send = strsend.getBytes();
   answer = channel.transmit(new CommandAPDU(0xB0, 0x05, 0x00, 0x01, send));
   for(int i = 0; i<=soLan;i++){
       byte p1 = (byte) i;
       int start =0, end =0;
       start = i*249;
       if(i != soLan){
           end = (i+1) * 249;
       }else{
           end = napanh.length;
       }
       byte[] slice = Arrays.copyOfRange(napanh, start, end);
       System.out.println(slice.length);
       answer = channel.transmit(new CommandAPDU(0xB0, 0x01, p1, 0x01, slice));

   }

   // in anh


//   System.out.println("answer: " + answer.toString());
   } catch(Exception e) {
   System.out.println("Ouch: " + e.toString());
  }
 }

    public void taianh() {
        int size = 0;
  try {
   // Display the list of terminals
   TerminalFactory factory = TerminalFactory.getDefault();
   List<CardTerminal> terminals = factory.terminals().list();
   System.out.println("Terminals: " + terminals);

   // Use the first terminal
   CardTerminal terminal = terminals.get(0);

   // Connect wit hthe card
   Card card = terminal.connect("*");
   System.out.println("card: " + card);
   CardChannel channel = card.getBasicChannel();

   // Send Select Applet command
   byte[] aid = {(byte) 0x54,0x68,0x65,0x20,0x42,0x4F,0x54};
   ResponseAPDU answer = channel.transmit(new CommandAPDU(0x00, 0xA4, 0x04, 0x00, aid));
   System.out.println("answer: " + answer.toString());
   answer = channel.transmit(new CommandAPDU(0xB0, 0x06, 0x01, 0x01));
   byte[] sizeanh = answer.getData();
   arranh = new byte[10000];
   String strsizeanh = new String(sizeanh);
   System.out.println(strsizeanh);
   String[] output1 = strsizeanh.split("S");
   System.out.println(output1[0]);
   System.out.println(output1[1]);
   int lan = Integer.parseInt(output1[0].replaceAll("\\D", ""));
   int du = Integer.parseInt(output1[1].replaceAll("\\D", ""));
   size = lan * 249 + du;
   System.out.println("answer: " + answer.toString());
   int count =  size/249;
   
   for (int j = 0; j <= count; j++)
   {
       answer = channel.transmit(new CommandAPDU(0xB0, 0x02, (byte) j, 0x01));
       byte[] result = answer.getData();
       int leng = 249;
       if(j == count)
       {
           leng = size%249;
       }
       System.arraycopy(result, 0, arranh, j*249, leng);
   }

   } catch(Exception e) {
   System.out.println("Ouch: " + e.toString());
  }
 }


   public void thongtin() {
  try {

   // Send test command
   TerminalFactory factory = TerminalFactory.getDefault();
   List<CardTerminal> terminals = factory.terminals().list();
   CardTerminal terminal = terminals.get(0);
   Card card = terminal.connect("*");
   CardChannel channel = card.getBasicChannel();
   byte[] aid = {(byte) 0x54,0x68,0x65,0x20,0x42,0x4F,0x54};
   ResponseAPDU answer = channel.transmit(new CommandAPDU(0x00, 0xA4, 0x04, 0x00, aid));
   answer = channel.transmit(new CommandAPDU(0xB0, 0x00, 0x01, 0x01));
   System.out.println("answer: " + answer.toString());
   byte r[] = answer.getData();
   byte xuongdong = (byte)0x21;
   int hoten = 0;
   int ngaysinh = 0;
   int diachi = 0;
   int bienks = 0;
   int len = r.length;
   System.out.print(r.length);
   int j = 0;
   for (int i=0; i<len; i++)
   {

       if(r[i] == xuongdong) {
           j++;
           System.out.print("\n");
        //   i++
       }
       if(j == 0) {
           hoten = i;
       }
       else if(j == 1) {
           ngaysinh = i;
       }
       else if(j == 2) {
           diachi = i;
       }
       else if(j == 3) {
           bienks = i;
       }
       System.out.print((char)r[i]);
   }
   byte arrhoten[] = new byte[hoten+1];
   byte arrngaysinh[] = new byte[ngaysinh-hoten-1];
   byte arrdiachi[] = new byte[diachi-ngaysinh-1];
   byte arrbienks[] = new byte[bienks-diachi-1];

   System.arraycopy(r, 0, arrhoten, 0, hoten+1);
   System.arraycopy(r, (hoten+2), arrngaysinh, 0, (ngaysinh-hoten-1));
   System.arraycopy(r, (ngaysinh+2), arrdiachi, 0, (diachi-ngaysinh-1));
   System.arraycopy(r, (diachi+2), arrbienks, 0, (bienks-diachi-1));
   strhoten = new String(arrhoten);
   strngaysinh = new String(arrngaysinh);
   strdiachi = new String(arrdiachi);
   strbienks = new String(arrbienks);

   System.out.println();

   // Disconnect the card
   card.disconnect(false);
  } catch(Exception e) {
   System.out.println("Ouch: " + e.toString());
  }
 }

    public void sodu(){
        try {

   // Send test command
   TerminalFactory factory = TerminalFactory.getDefault();
   List<CardTerminal> terminals = factory.terminals().list();
   CardTerminal terminal = terminals.get(0);
   Card card = terminal.connect("*");
   CardChannel channel = card.getBasicChannel();
   byte[] aid = {(byte) 0x54,0x68,0x65,0x20,0x42,0x4F,0x54};
   ResponseAPDU answer = channel.transmit(new CommandAPDU(0x00, 0xA4, 0x04, 0x00, aid));
   answer = channel.transmit(new CommandAPDU(0xB0, 0x10, 0x01, 0x01));
   System.out.println("answer: " + answer.toString());
   byte r[] = answer.getData();
        r[0] = (byte) (r[0] << 8);
        int i = (r[1] & 0xff)*10000;
       System.out.print(i);
   System.out.print("\n");
   strsodu = String.format("%,d", i);
   System.out.print(strsodu);
   System.out.println();

   // Disconnect the card
   card.disconnect(false);
  } catch(Exception e) {
   System.out.println("Ouch: " + e.toString());
  }
    }

    public void naptien(){
   try{
   TerminalFactory factory = TerminalFactory.getDefault();
   List<CardTerminal> terminals = factory.terminals().list();
   CardTerminal terminal = terminals.get(0);
   Card card = terminal.connect("*");
   CardChannel channel = card.getBasicChannel();
   byte[] aid = {(byte) 0x54,0x68,0x65,0x20,0x42,0x4F,0x54};
   ResponseAPDU answer = channel.transmit(new CommandAPDU(0x00, 0xA4, 0x04, 0x00, aid));
   System.out.println(tiennap);
   answer = channel.transmit(new CommandAPDU(0xB0, 0x20, (byte)tiennap, 0x01));
   System.out.println("answer: " + answer.toString());

   System.out.println();

   // Disconnect the card
   card.disconnect(false);

  } catch(Exception e) {
   System.out.println("Ouch: " + e.toString());
  }
    }

    public void thanhtoan(){
   try{
   TerminalFactory factory = TerminalFactory.getDefault();
   List<CardTerminal> terminals = factory.terminals().list();
   CardTerminal terminal = terminals.get(0);
   Card card = terminal.connect("*");
   CardChannel channel = card.getBasicChannel();
   byte[] aid = {(byte) 0x54,0x68,0x65,0x20,0x42,0x4F,0x54};
   ResponseAPDU answer = channel.transmit(new CommandAPDU(0x00, 0xA4, 0x04, 0x00, aid));
   answer = channel.transmit(new CommandAPDU(0xB0, 0x30, 0x01, 0x01));
   String kq = answer.toString();
   System.out.println("answer: " + kq);
   output = kq.split("=");
   System.out.println(output[1]);

   // Disconnect the card
   card.disconnect(false);

  } catch(Exception e) {
   System.out.println("Ouch: " + e.toString());
  }
    }


    public void suatt() {
  try {
   // Display the list of terminals
   TerminalFactory factory = TerminalFactory.getDefault();
   List<CardTerminal> terminals = factory.terminals().list();
   System.out.println("Terminals: " + terminals);

   // Use the first terminal
   CardTerminal terminal = terminals.get(0);

   // Connect wit hthe card
   Card card = terminal.connect("*");
   System.out.println("card: " + card);
   CardChannel channel = card.getBasicChannel();

   // Send Select Applet command
   byte[] aid = {(byte) 0x54,0x68,0x65,0x20,0x42,0x4F,0x54};
   ResponseAPDU answer = channel.transmit(new CommandAPDU(0x00, 0xA4, 0x04, 0x00, aid));
   System.out.println("answer: " + answer.toString());
   answer = channel.transmit(new CommandAPDU(0xB0, 0x04, 0x00, 0x00, strtoarr));
   System.out.println("answer: " + answer.toString());
   answer = channel.transmit(new CommandAPDU(0xB0, 0x03, 0x01, 0x01, strtoarr));
   System.out.println("answer: " + answer.toString());
   } catch(Exception e) {
   System.out.println("Ouch: " + e.toString());
  }
 }

//    public void fix() {
//  try {
//   // Display the list of terminals
//   TerminalFactory factory = TerminalFactory.getDefault();
//   List<CardTerminal> terminals = factory.terminals().list();
//   System.out.println("Terminals: " + terminals);
//
//   // Use the first terminal
//   CardTerminal terminal = terminals.get(0);
//
//   // Connect wit hthe card
//   Card card = terminal.connect("*");
//   System.out.println("card: " + card);
//   CardChannel channel = card.getBasicChannel();
//
//   // Send Select Applet command
//   byte[] aid = {(byte) 0x54,0x68,0x65,0x20,0x42,0x4F,0x54};
//   ResponseAPDU answer = channel.transmit(new CommandAPDU(0x00, 0xA4, 0x04, 0x00, aid));
//   answer = channel.transmit(new CommandAPDU(0xB0, 0x05, 0x01, 0x01));
//   count = 0;
//   xtt.anh = new byte[10000];
//   System.out.println("answer: " + answer.toString());
//   } catch(Exception e) {
//   System.out.println("Ouch: " + e.toString());
//  }
// }


}


