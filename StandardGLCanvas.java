
import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.awt.*;

class StandardGLCanvas extends GLCanvas implements GLEventListener{

    Drawable drawable;
    GLGraphics graphics;

    StandardGLCanvas(){
    }
    
    StandardGLCanvas(Drawable drawable) {
        this.drawable = drawable;
    }

    @Override
    public void init(GLAutoDrawable arg0) {
        GL4 g = (GL4) GLContext.getCurrentGL();
        GLGraphics graphics = new GLGraphics(g);
    }

    // call this.display() from a loop or timer to trigger display(drawable)
    // should occur every frame

    @Override
    public void display(GLAutoDrawable drawable) {

        // draw things
        if (this.drawable != null)
            this.drawable.draw(graphics);
    }

    void setDrawable(Drawable drawable){
        this.drawable = drawable;
    }

    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
        // method body
    }

    public void dispose(GLAutoDrawable arg0) {
        // method body
     }


}