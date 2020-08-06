import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.awt.*;

import javax.swing.JFrame;
import javax.swing.Timer;

public class MarchingCubes {

    StandardWindow window;
    Timer timer;

    public static void main(String[] ags) {
        MarchingCubes cubes = new MarchingCubes();
    }

    MarchingCubes() {
        int x = 800, y = 800;

        window = new StandardWindow("Marching Cubes", x, y);
        StandardGLCanvas canvas = new StandardGLCanvas(x, y);
        canvas.setDrawable(null);
        window.setCanvas(canvas);

        // byte[][] arr = CubesAlgorithm.TRIANGLES;
        // System.out.println("{");
        // for (int i = 0; i < arr.length; i++){
        //     System.out.print("{");
        //     for (int j = 0; j < arr[i].length; j++){
        //         if (arr[i][j] != -1){
        //             System.out.print(arr[i][j] + ", ");
        //         }

        //     }
        //     System.out.println("},");
        // }
        // System.out.println("};");

        timer = new Timer(25, window);
        window.canvas.display();
        timer.start();
    }

}