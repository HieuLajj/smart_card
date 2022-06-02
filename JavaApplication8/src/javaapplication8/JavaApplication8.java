/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication8;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 *
 * @author laihi
 */
public class JavaApplication8 {

    /**
     * @param args the command line arguments
     */
    public static HashMap<String, Integer> Load(String path)
{
    try
    {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
        Object result = ois.readObject();
        //you can feel free to cast result to HashMap<String, Integer> if you know that only a HashMap is stored in the file
        return (HashMap<String, Integer>)result;
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
        return null;
}
    public static void main(String[] args) throws FileNotFoundException, IOException {
//       System.out.print("Fewffwe");
//       HashMap<String,Integer> aHashMap = new HashMap<String,Integer>();  
//       aHashMap.put("elee",1);   
//       aHashMap.put("five",2);
//       aHashMap.put("six",3);
//
//      File file = new File("G:\\khoarsa.txt");   
//      FileOutputStream fos = new FileOutputStream(file);   
//      ObjectOutputStream oos = new ObjectOutputStream(fos);           
//      oos.writeObject(aHashMap); 
//      oos.flush();
       System.out.println(Load("G:\\khoarsa.txt"));
       
    }
    
}
