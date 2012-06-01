package uk.ac.ic.doc.campusProject.model;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

public class SerializableBufferedImage implements Serializable {
	private static final long serialVersionUID = 1L;
	static Logger log = Logger.getLogger(SerializableBufferedImage.class);
	byte[] image;
	byte[] thumb;
	
	
	public byte[] getThumb() {
		return this.thumb;
	}

	public SerializableBufferedImage() {
		this.image = new byte[]{};
		this.thumb = new byte[]{};
	}
	
	public SerializableBufferedImage(BufferedImage image) {
		this.image = generateImage(image);
		this.thumb = generateThumbnail(image);
	}

	public byte[] getImage() {
		return this.image;
	}

	public void setBufferedImage(BufferedImage image) {
		this.image = generateImage(image);
		this.thumb = generateThumbnail(image);
	}
	
	public void setImage(byte[] image) {
		this.image = image;
	}
	
	private byte[] generateThumbnail(BufferedImage image) {
		int h = new Double(image.getHeight() * 0.4).intValue();
		int w = new Double(image.getWidth() * 0.4).intValue();
		BufferedImage newImage = new BufferedImage(w, h, image.getType());
		Graphics2D g = newImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(image, 0, 0, w, h, 0, 0, image.getWidth(), image.getHeight(), null);
		g.dispose();
		return generateImage(newImage);
	}
	
	private byte[] generateImage(BufferedImage image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "png", baos);
			baos.flush();
			byte[] byteImage = baos.toByteArray();
			baos.close();
			return byteImage;
		} catch (IOException e) {
			log.debug("Issue in SBI");
			return null;
		}
	}
	
	

}
