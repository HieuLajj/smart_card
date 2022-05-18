/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smartcard03;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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
import utils.ConvertData;


/**
 *
 * @author laihi
 */
public class ConnectJavaCard {
    public final static String  SW_NO_ERROR = "9000";
    public final static String  SW_UNKNOWN = "6F00";
    
    public final static String  SW_INVALID_PARAMETER = "9C0F";
    //card block
    public final static String  SW_IDENTITY_BLOCKED = "9C0C";
    //error pin
    public final static String  SW_AUTH_FAILED = "9C02";
    //
    public final static String  SW_OPERATION_NOT_ALLOWED = "9C03";
    // Kiểm soát lỗi
    public final static String SW_INTERNAL_ERROR = "9CFF";
    // tr li 9c04 khi th cha c setup */
    public final static String SW_SETUP_NOT_DONE = "9C04";
	// Li P1*/
    public final static String SW_INCORRECT_P1 = "9C10";
	//** Li P2*/
    public final static String SW_INCORRECT_P2 = "9C11";
	//** Required operation was not authorized because of a lack of privileges */
    public final static String SW_UNAUTHORIZED = "9C06";
	//** Algorithm specified is not correct */
    public final static String SW_INCORRECT_ALG = "9C09";
         //Loi do dai
    public final static String SW_WRONG_LENGTH = "6700";
    
    public final static byte INS_CREATE_IMAGE = (byte)0x53;
    public final static byte INS_CREATE_SIZEIMAGE = (byte)0x54;//countanh
    public final static byte INS_OUT_SIZEIMAGE = (byte)0x55;
    public final static byte INS_OUT_IMAGE = (byte)0x56;
    
    
    
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
            response = channel.transmit(new CommandAPDU((byte)0x00, (byte)0x03, (byte)0x11, (byte)0x00,image));
            
            String pub = bytesToHex(response.getData());
       
            System.out.println("answer: " + response.toString()+pub);
            return true;
        } catch (CardException ex) {
            Logger.getLogger(ConnectJavaCard.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    public void receiveImage(){
        try{
           
            response = channel.transmit(new CommandAPDU((byte)0x00, (byte)0x11, (byte)0x00, (byte)0x00));
            //String pub = bytesToHex(response.getData());
       
           // System.out.println("answer: "+response.getData());
           
        }catch(Exception e){
            System.out.println("Ouch: " + e.toString());
        }
    }
    
    
    
    
    public boolean UploadImage(File file, String type){
       // connectapplet();
        try{
            
            
            BufferedImage bImage = ImageIO.read(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bImage, type, bos);
            
            byte[] napanh = bos.toByteArray();
            
            int soLan = napanh.length / 249;
            
            String strsend = soLan + "S" + napanh.length % 249;
            
            byte[] send = strsend.getBytes();
            
            ResponseAPDU response = channel.transmit(new CommandAPDU(0x00,INS_CREATE_SIZEIMAGE,0x00,0x01,send));
            String check = Integer.toHexString(response.getSW());
            
            if(check.equals(SW_NO_ERROR)){
                for(int i = 0;i<=soLan ;i++){
                    byte p1 = (byte) i;
                    int start = 0, end = 0;
                    start = i * 249;
                    if(i != soLan){
                        end = (i+1) *249;
                    }
                    else{
                        end = napanh.length;
                    }
                    byte[] slice = Arrays.copyOfRange(napanh, start, end);
                    response = channel.transmit(new CommandAPDU(0x00,INS_CREATE_IMAGE,p1,0x01,slice));
                    String checkSlide = Integer.toHexString(response.getSW());
                    if(!checkSlide.equals(SW_NO_ERROR)){
                        return false;
                    }
                }
                return true;
            }
            return true;
        }
        catch(Exception ex){
            return false;
        }
    }
    
     public BufferedImage DownloadImage(){
      //  connectapplet();
        try {
            
            int size = 0;
            ResponseAPDU answer = channel.transmit(new CommandAPDU(0xB0,INS_OUT_SIZEIMAGE,0x01,0x01));
            String check = Integer.toHexString(answer.getSW());
            if(check.equals(SW_NO_ERROR)){
                byte[] sizeAnh = answer.getData();
                if(ConvertData.isByteArrayAllZero(sizeAnh)){
                    return null;
                }
                byte[] arrAnh = new byte[10000];
                String strSizeAnh = new String(sizeAnh);
                String[] outPut1 = strSizeAnh.split("S");
                
                int lan = Integer.parseInt(outPut1[0].replaceAll("\\D", ""));
                int du = Integer.parseInt(outPut1[1].replaceAll("\\D", ""));
                size = lan * 249 + du;
                int count = size / 249;
                System.err.println(count);
                for(int j=0;j<=count;j++){
                    answer = channel.transmit(new CommandAPDU(0xB0,INS_OUT_IMAGE,(byte)j,0x01));
                    String check1 = Integer.toHexString(answer.getSW());
                    if(check1.equals(SW_NO_ERROR)){
                        byte[] result = answer.getData();
                        int leng = 249;
                        if(j == count){
                            leng = size % 249;
                        }
                        System.arraycopy(result, 0, arrAnh, j*249, leng);
                    }
                }
                
                ByteArrayInputStream bais = new ByteArrayInputStream(arrAnh);
                try {
                    BufferedImage image  = ImageIO.read(bais);
                    return image;
                } catch (Exception e) {
                    System.err.println("Error image");
                }
            }
        } catch (Exception e) {
            System.err.println("error dowloadimage");
        }
        return null;
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


