import com.scale4j.Scale4j;
import com.scale4j.exception.ImageLoadException;
import com.scale4j.exception.ImageProcessException;
import java.awt.image.BufferedImage;
import java.io.File;

public class TestErrorHandling {
    public static void main(String[] args) {
        try {
            // Test ImageLoadException
            BufferedImage img = Scale4j.load(new File("nonexistent.jpg"));
            System.out.println("ERROR: Should have thrown ImageLoadException");
        } catch (ImageLoadException e) {
            System.out.println("✓ ImageLoadException caught: " + e.getMessage());
        }
        
        try {
            // Test ImageProcessException
            BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            Scale4j.load(img).resize(-10, 50).build();
            System.out.println("ERROR: Should have thrown ImageProcessException");
        } catch (ImageProcessException e) {
            System.out.println("✓ ImageProcessException caught: " + e.getMessage());
        }
        
        System.out.println("All error handling tests passed!");
    }
}