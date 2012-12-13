package com.tooploox.blackberryjam.data;

import android.graphics.Bitmap;

public class ListItemData {
    private int uniqueId;
    private Bitmap image;
    private Bitmap thumbnail;
    private String caption;
    private String price;
    private Integer priceValue;
    private Integer noItems = 1;

    /**
     * List item data generated based on the camera output
     * 
     * @param image
     * @param thumbnail
     */
    public ListItemData(Bitmap image, int uniqueId) {
        this.uniqueId = uniqueId;
        this.image = image;
        this.thumbnail = null;
    }

    public void splitIdData(String recognizeId) {
        String[] tmp = recognizeId.split(":");
        if (tmp.length > 0) {
            caption = tmp[0];
            if (tmp.length > 1) {
                price = tmp[1] + ".00 PLN";
                priceValue = Integer.valueOf(tmp[1]);
            }
        }
    }

    /**
     * @return the image
     */
    public Bitmap getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(Bitmap image) {
        this.image = image;
    }

    /**
     * @return the thumbnail
     */
    public Bitmap getThumbnail() {
        return thumbnail;
    }

    /**
     * @param thumbnail the thumbnail to set
     */
    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     * @return the caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     * @param caption the caption to set
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * @return the price
     */
    public String getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(String price) {
        this.price = price;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public Integer getPriceValue() {
        return priceValue;
    }

    /**
     * @return the noItems
     */
    public Integer getNoItems() {
        return noItems;
    }

    /**
     * @param noItems the noItems to set
     */
    public void setNoItems(Integer noItems) {
        this.noItems = noItems;
    }

    /**
     * @return multiplied no. of items times item price.
     */
    public Integer getProductValue() {
        if (noItems != null && priceValue != null) {
            return noItems * priceValue;
        }
        return 0;
    }
}
