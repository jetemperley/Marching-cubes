
import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.awt.*;

class StandardGLCanvas extends GLCanvas implements GLEventListener {

    Drawable drawable;
    GLGraphics graphics;

    StandardGLCanvas(int x, int y) {
        this(null, x, y);
    }

    StandardGLCanvas(Drawable drawable, int x, int y) {

        this.drawable = drawable;
        addGLEventListener(this);
        setSize(x, y);
        setAutoSwapBufferMode(false);
    }

    @Override
    public void init(GLAutoDrawable arg0) {
        System.out.println();
        System.out.println("Canvas initialising...");
        GL4 g = (GL4) GLContext.getCurrentGL();
        
        graphics = new GLGraphics(g);
        System.out.println("graphics is null: " + (this.graphics==null));
        System.out.println("canvas init ... complete");

    }

    // call display() from a loop or timer to trigger display(drawable)
    // should occur every frame

    @Override
    public void display(GLAutoDrawable drawable) {
        //System.out.println("displayed");
        if (graphics != null) {
            
            graphics.clear();
            graphics.readyModelProg();
            graphics.drawCube(2.0f, 2.0f, 2.0f);

            if (this.drawable != null) {
                this.drawable.draw(graphics);
            }

        }

    }

    void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
        // method body
    }

    public void dispose(GLAutoDrawable arg0) {
        // method body
    }

}