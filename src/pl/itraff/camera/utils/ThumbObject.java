package pl.itraff.camera.utils;

import java.io.Serializable;

/**
 * contains thumbnail data: id, photo data and product category
 * @author qba
 *
 */
public class ThumbObject implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private byte[] thumb;
	private long id;
	private int type;
	
	private boolean checked = false;
	
	public ThumbObject(byte[] thumb, long id, int type) {
		super();
		this.thumb = thumb;
		this.id = id;
		this.type = type;
	}

	public byte[] getThumb() {
		return thumb;
	}

	public long getId() {
		return id;
	}
	
	public int getType() {
		return type;
	}
	
	public boolean isChecked() {
		return checked;
	}
	
	public void setChecked(boolean ch) {
		checked = ch;
	}


}
