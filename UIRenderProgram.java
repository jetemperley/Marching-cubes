import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.texture.*;
/*  
    UI render program is designed to overlay a normal rendering program
    with a 2d ui built with 3d elements.
    Therefor the projection view matrix is replaced with a transformation
    matrix for the elemnt to be drawn.
    Elements are drawn by default with y-up, x-right, -z-forward (OpenGL standard)
    and depth culling is ignored for these elements

*/

public class UIRenderProgram extends GLProgram{

    int texLoc;
    UIRenderProgram(GL4 g, int targetTexture){
        setTexTarget(targetTexture);
        super.make(g, getDefaultVert(), getDefaultFrag());

        // pointer for texture coords
        // g.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[1]);
        
        // g.glActiveTexture(GL4.GL_TEXTURE0);

        // g.glBindTexture(GL4.GL_TEXTURE_2D, Assets.joglTexLocs[0]);

        // g.glBindFramebuffer(GL4.GL_FRAMEBUFFER, customBuffers[1]);
        g.glFramebufferTexture(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, textureID, 0);
        
        initVertAttributes(g);
        initUniforms(g);
    }

    void initUniforms(GL4 g) {
        super.initUniforms(g);
        
    }

    void ready(GL4 g) {

        g.glUseProgram(ID);

        g.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
        // g.glDrawBuffer(GL4.GL_FRONT);
        g.glDisable(GL4.GL_DEPTH_TEST);
        g.glDepthFunc(GL4.GL_LEQUAL);

        g.glUniform4f(altColorLoc, 0, 0, 0, 0);

        // g.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);

        // g.glBindFramebuffer(GL4.GL_FRAMEBUFFER, customBuffers[1]);
        g.glFramebufferTexture(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, textureID, 0);
        // g.glClear(GL4.GL_DEPTH_BUFFER_BIT);
        g.glDrawBuffer(GL4.GL_FRONT);

        // g.glDisable(GL4.GL_DEPTH_TEST);

        g.glActiveTexture(GL4.GL_TEXTURE0);
        g.glBindTexture(GL4.GL_TEXTURE_2D, Assets.joglTexLocs[0]);

    }

    static String[] getDefaultVert(){
        return new String[] {
            "// ui vert shader",
            "#version 430\n",
            "layout (location = 0) in vec3 position;\n",
            "layout (location = 2) in vec2 texCoord;\n",
            "uniform mat4 pv;\n",
            "out vec2 tc;\n",
            "void main(void) {\n",
            "tc = texCoord;\n",
            "gl_Position = pv*vec4(position, 1.0);\n",
            "}\n",
        };
            
    }

    static String[] getDefaultFrag(){
        return new String[] {
            "// ui frag shader",
            "#version 430\n",
            "layout (binding = 0) uniform sampler2D tex;\n",
            "uniform vec4 alt_color;\n",
            "out vec4 color;\n",
            "in vec2 tc;\n",
            "void main(void){\n",
            "vec4 tcolor = texture(tex, tc);\n",
            "color = tcolor + alt_color;\n",
            "}\n",
            };
    }

}