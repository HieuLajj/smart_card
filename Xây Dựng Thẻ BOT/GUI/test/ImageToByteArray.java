import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
public class ImageToByteArray {
    byte [] data;
   public void main() throws Exception{
      BufferedImage bImage = ImageIO.read(new File("C:\\Users\\ADMIN\\Desktop\\Javacard\\Untitled-1.jpg"));
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ImageIO.write(bImage, "jpg", bos);
      data = bos.toByteArray();
      for(int i = 0; i< data.length;i++)
      {
        System.out.print(data[i]);
        System.out.print(" ");
      }
   }
}