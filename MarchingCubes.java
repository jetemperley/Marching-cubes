import javax.swing.JFrame;
import javax.swing.Timer;

public class MarchingCubes{

    StandardWindow window;
    Timer timer;


    public static void main(String[] ags){
        MarchingCubes cubes = new MarchingCubes();
    }

    MarchingCubes(){
        int x = 500, y = 500;
        window = new StandardWindow("Marching Cubes", x, y);
        StandardGLCanvas canvas = new StandardGLCanvas(x, y);
        CubesAlgorithm cubes = new CubesAlgorithm();
        canvas.setDrawable(cubes);
        window.setCanvas(canvas);
        timer = new Timer(25, window);
        timer.start();
    }


}