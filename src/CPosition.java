
public class CPosition {
	private Integer x;
	private Integer y;
	private Integer z;
	public CPosition( Block block ) {
		x = block.getX();
		y = block.getY();
		z = block.getZ();
	}
	
	public CPosition(Integer x, Integer y, Integer z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public void setX(Integer x) {
		this.x = x;
	}
	public Integer getX() {
		return x;
	}
	public void setY(Integer y) {
		this.y = y;
	}
	public Integer getY() {
		return y;
	}
	public void setZ(Integer z) {
		this.z = z;
	}
	public Integer getZ() {
		return z;
	}
}
