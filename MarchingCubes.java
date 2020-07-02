import javax.swing.JFrame;
import javax.swing.Timer;

public class MarchingCubes{

    StandardWindow window;
    Timer timer; 

    public static void main(String[] ags){
        MarchingCubes cubes = new MarchingCubes();
    }

    MarchingCubes(){
        window = new StandardWindow("Marching Cubes", 500, 500);
        StandardGLCanvas canvas = new StandardGLCanvas(null);
        CubesAlgorithm cubes = new CubesAlgorithm();
        canvas.setDrawable(cubes);
        window.setCanvas(canvas);
        timer = new Timer(25, window);
    }


}