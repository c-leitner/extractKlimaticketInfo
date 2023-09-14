package at.velocit.projects.extractKlimaticketInfo;

import java.io.*;
import javax.imageio.ImageIO;
import java.util.zip.Inflater; 
import java.util.zip.DataFormatException;  
import java.util.BitSet;
import java.util.ArrayList;
import java.util.Arrays;
import org.open918.lib.UicTicketParser;
import org.open918.lib.domain.Ticket;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.aztec.AztecReader;
import com.google.zxing.Result;

import java.awt.image.BufferedImage;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.nio.charset.StandardCharsets ;


/**
 * Hello world!
 *
 */
public class App 
{
    private static final String OUTPUT_DIR = "/home/clemens/Documents/Github/extractKlimaticketInfo/testFiles/";

    public static void main(String[] args) throws Exception{

        try (final PDDocument document = PDDocument.load(new File("/home/clemens/Documents/Github/extractKlimaticketInfo/testFiles/klima1.pdf"))){

            PDPageTree list = document.getPages();
            for (PDPage page : list) {
                PDResources pdResources = page.getResources();
                int i = 1;
                for (COSName name : pdResources.getXObjectNames()) {
                    PDXObject o = pdResources.getXObject(name);
                    if (o instanceof PDImageXObject) {
                        PDImageXObject image = (PDImageXObject)o;
                        int imageH = image.getHeight();
                        int imageW = image.getWidth();
                        if(imageH == imageW){
                            BufferedImage buffIMG = image.getImage();
                            int imgRGBcenter = buffIMG.getRGB(imageH/2,imageH/2);
                            int red =   (imgRGBcenter >> 16) & 0xFF;
                            int green = (imgRGBcenter >>  8) & 0xFF;
                            int blue =  (imgRGBcenter) & 0xFF;
                            if(red==0&&blue==0&&green==0){
                                String filename = OUTPUT_DIR + "extracted-image-" + i + ".png";
                                ImageIO.write(buffIMG, "png", new File(filename));
                                i++;
                                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                                        new BufferedImageLuminanceSource(buffIMG)));
                                		Result qrCodeResult = new AztecReader().decode(binaryBitmap);
     
                                byte[] bResultus = qrCodeResult.getText().getBytes(); 
                                System.out.println(new String(bResultus));
                                Ticket decTick = UicTicketParser.decode(bResultus, true);
                                System.out.println(decTick.getMessageLength());
                                System.out.println(new String(decTick.getMessage(), StandardCharsets.UTF_8));
                                try (FileOutputStream fos = new FileOutputStream(OUTPUT_DIR+"out1.bin")) {
                                    fos.write(decTick.getMessage());
                                    //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
                                 }
                                 
/*                                 String sResult = "";
                                System.out.println("------------");
                                System.out.println(sResult);
                                System.out.println("------------");
                                byte[] compressedM = Arrays.copyOfRange(bResultus, 68, bResultus.length - 1);
                                String contents = new String(bResultus);
                                String messageLengthArea = contents.substring(61, 68);
                                System.out.println(messageLengthArea);

                                String output = new String(decompress(compressedM));
                                System.out.println(output); */

                                
                            }
                        }
                    }
                }
            }

        } catch (IOException e){
            System.err.println("Exception while trying to create pdf document - " + e);
        }
    }



      
  



/*     public static void main( String[] args ) throws Exception
    {
        // Existing PDF Document
        // to be Loaded using file io
        File newFile = new File("/home/clemens/Documents/Github/extAZTECfromPDF/testFiles/klima.pdf");
        PDDocument pdfDocument = PDDocument.load(newFile);

        // PDFRenderer class to be Instantiated
        // i.e. creating it's object
        PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);
  
        // Rendering an image
        // from the PDF document
        // using BufferedImage class
        BufferedImage img = pdfRenderer.renderImage(0);
        // Writing the extracted
        // image to a new file
        ImageIO.write(
            img, "PNG",
            new File("/home/clemens/Documents/Github/extAZTECfromPDF/testFiles/test.png")); 
        System.out.println("Image has been extracted successfully");
  
        // Closing the PDF document
        pdfDocument.close();
    } */
}
