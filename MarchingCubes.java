import javax.swing.JFrame;
import javax.swing.Timer;

public class MarchingCubes{

    StandardWindow window;
    Timer timer; 

    public static void main(String[] ags){
        MarchingCubes cubes = new MarchingCubes();
    }

    MarchingCubes(){
        window = new StandardWindow();
        StandardGLCanvas canvas = new StandardGLCanvas(null);
        window.setCanvas(canvas);
        timer = new Timer(25, window);
    }
}