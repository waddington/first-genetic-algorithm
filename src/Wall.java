import javafx.scene.shape.Rectangle;

public class Wall {
    int[] wallPosition;
    Rectangle wallShape;

    int[] getWallPosition() {
        return this.wallPosition;
    }
    void setWallPosition(int[] wallPosition) {
        this.wallPosition = wallPosition;
    }

    Rectangle getWallShape() {
        return this.wallShape;
    }
    void setWallShape(Rectangle wallShape) {
        this.wallShape = wallShape;
    }
}
