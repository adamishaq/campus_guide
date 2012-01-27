package uk.ac.ic.doc.campusProject.utils.pdf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import uk.ac.ic.doc.campusProject.model.FloorPlanDao;
import uk.ac.ic.doc.campusProject.model.SerializableBufferedImage;

public class PdfProcessor implements Serializable {
	private static final long serialVersionUID = 1L;
	static Logger log = Logger.getLogger(PdfProcessor.class);
	
	public static List<FloorPlanDao> pdfToImage (InputStream fileInputStream) {
		List<FloorPlanDao> images = new ArrayList<FloorPlanDao>();
	    try {
		    PDDocument doc = PDDocument.load(fileInputStream);
			@SuppressWarnings("unchecked")
			List<PDPage> pages = doc.getDocumentCatalog().getAllPages();
			for (PDPage page : pages) {
				SerializableBufferedImage image = new SerializableBufferedImage(page.convertToImage());
				images.add(new FloorPlanDao(image, "", ""));
			}
			//writeImagesToDisk(images);
			return images;  
	    } 
	    catch (IOException e) {
		    log.debug(e);
		    e.printStackTrace();
		    return null;
	    }
	}
	
	public static void writeImagesToDisk(List<SerializableBufferedImage> images) {
		for (int x = 0 ; x < images.size() ; x++) {
			File f = new File("C:\\Users\\Adam\\Downloads\\image" + x + ".png");
			try {
				f.createNewFile();
				InputStream in = new ByteArrayInputStream(images.get(x).getImage());
				ImageIO.write(ImageIO.read(in), "png", f);
				in.close();
			} catch (IOException e) {
				log.debug(e);
				e.printStackTrace();
			}
		}
		
		
	}

}
