package imagejpa;

import java.io.Serializable;

import jakarta.persistence.*;


/**
 * The persistent class for the IMAGE database table.
 * 
 */
@Entity
@Table(name="IMAGE")

@NamedQueries({
	
	  @NamedQuery(
		name="selectImageIdsContainValue",
		query="select i from ImageJPA i where i.imageId like :imageIdsContainValue1"
		),
	
	  @NamedQuery(
		name="deleteImageIdsContainValue",
		query="delete from ImageJPA i where i.imageId like :imageIdsContainValue1"
		)
})	

public class ImageJPA implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="IMAGE_ID")
	private String imageId;

    @Lob()
	@Column(name="IMAGE_DATA")
	private byte[] imageData;

    public ImageJPA() {
    }

	public String getImageId() {
		return this.imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public byte[] getImageData() {
		return this.imageData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}

}