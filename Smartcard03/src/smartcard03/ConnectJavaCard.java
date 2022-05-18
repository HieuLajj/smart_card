/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smartcard03;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.smartcardio.*;
import javax.swing.JOptionPane;

/**
 *
 * @author laihi
 */
public class ConnectJavaCard {
    private static Card card;
    private static TerminalFactory factory;
    private static CardChannel channel;
    private static CardTerminal terminal;
    private static List<CardTerminal> terminals;
    private static ResponseAPDU response;
     private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    
    public boolean connectapplet() {
        try {
           // Display the list of terminals
           factory = TerminalFactory.getDefault();
           terminals = factory.terminals().list();
           System.out.println("Terminals: " + terminals);

           // Use the first terminal
           terminal = terminals.get(0);

           // Connect wit hthe card
           card = terminal.connect("*");
           System.out.println("card: " + card);
           channel = card.getBasicChannel();

           // Send Select Applet command
           byte[] aid = {(byte) 0x11,0x22,0x33,0x44,0x55,0x00};
           response = channel.transmit(new CommandAPDU(0x00, 0xA4, 0x04, 0x00, aid));
           String check = Integer.toHexString(response.getSW());
           System.out.println("answer: " + response.toString()+ "laivanhieu");
           if(check.equals(("9000"))){
               JOptionPane.showMessageDialog(null,"Ket noi thanh cong 2");
               return true;
           }else if(check.equals(("6400"))){
               JOptionPane.showMessageDialog(null,"The bi vo hieu hoa");
               return true;
           }
           else{
               JOptionPane.showMessageDialog(null,"Ket noi that bai");
               return false;
           }
          
        } catch(Exception e) {
           System.out.println("Ouch: " + e.toString());
        }
        return false;
    }
    public void thongtin(){
        try{
            byte[] send  ={(byte) 0x11,0x22,0x33,0x44,0x55,0x00};
            response = channel.transmit(new CommandAPDU((byte)0x00, (byte)0x02, (byte)0x00, (byte)0x00));
            String pub = new String(response.getData(),StandardCharsets.UTF_8);
       
            System.out.println("answer: " + response.toString()+pub);
        }catch(Exception e){
            System.out.println("Ouch: " + e.toString());
        }
    }
    
    
    
    
    public boolean transmissionData(String cccd, String hoten, String ngaysinh, String sdt,String phong, String ngay_dk,String mapin,byte[] image){
        try {
            System.out.println("------------------------------------------------------------------------------------------");
            String data = cccd + "@" + hoten + "@" + ngaysinh + "@" + sdt + "@" + phong + "@" + ngay_dk+ "@"+ mapin;
            byte[] dataTrans = data.getBytes();
            
                                                   
            System.out.println("----data-----");
            System.out.println(dataTrans.length);
            System.out.println(dataTrans);
            System.out.println("----image-----");
            System.out.println(image.length);
            System.out.println(image);
            System.out.println("----combined-----");
            byte[] combined = new byte[dataTrans.length + image.length];
            
            for (int i = 0; i < combined.length; ++i)
            {
                combined[i] = i < dataTrans.length ? dataTrans[i] : image[i - dataTrans.length];
            }
            System.out.println(combined.length);
            System.out.println(combined);
            response = channel.transmit(new CommandAPDU((byte)0x00, (byte)0x00, (byte)0x11, (byte)0x00,dataTrans));
            //response = channel.transmit(new CommandAPDU((byte)0x00, (byte)0x00, (byte)0x11, (byte)0x00,combined));
            //String pub = bytesToHex(response.getData());
            String pub = new String(response.getData(),StandardCharsets.UTF_8);
            System.out.println("answer: " + response.toString()+"fawef"+pub);
            return true;
        } catch (CardException ex) {
            Logger.getLogger(ConnectJavaCard.class.getName()).log(Level.SEVERE, null, ex);
        }
            return false;
    }
    public boolean sendImage(byte[] image){
        try {
            response = channel.transmit(new CommandAPDU((byte)0x00, (byte)0x00, (byte)0x11, (byte)0x00,image));
            return true;
        } catch (CardException ex) {
            Logger.getLogger(ConnectJavaCard.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
     public  String getData(){
        try {
            response = channel.transmit(new CommandAPDU((byte) 0x00, (byte)0x02, (byte) 0x00,(byte) 0x00));
            return new String(response.getData(), StandardCharsets.UTF_8);
        } catch (CardException e) {
            System.out.println("Error :" + e);
            return "Lỗi";
        }
        
    }
    
    

    public static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
        int v = bytes[j] & 0xFF;
        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
        return new String(hexChars);
    }

}


