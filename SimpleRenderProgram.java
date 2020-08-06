
import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.texture.*;

class SimpleRenderProgram extends ModelRenderProgram{

    SimpleRenderProgram(){
    }
    
    SimpleRenderProgram(GL4 g, int targetTexture){
        setTargetTex(targetTexture);
        super.make(g, getDefaultVert(), getDefaultFrag());

        g.glFramebufferTexture(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, textureID, 0);
        g.glDrawBuffer(GL4.GL_FRONT);

        initUniforms(g);
        initVertAttributes(g);
    }

    @Override
    void initVertAttributes(GL4 g){
        // configure pointer for position FIX*****
        g.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 12, 0);
        g.glEnableVertexAttribArray(0);
    }

    void ready(GL4 g){
        g.glUseProgram(ID);

        g.glFramebufferTexture(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, textureID, 0);
        g.glDrawBuffer(GL4.GL_FRONT);

        g.glEnable(GL4.GL_DEPTH_TEST);
        g.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
        g.glEnable(GL4.GL_BLEND);
        g.glDepthFunc(GL4.GL_LEQUAL);
    }   

    static String[] getDefaultVert(){
        return new String[] {
            "// anim vert shader",
            "#version 430\n",
            "layout (location = 0) in vec3 position;\n",

            "uniform mat4 pv;\n",
            "// uniform mat4 light_cam;\n",
            "uniform vec3 draw_offset;\n",
            "uniform vec3 sun_dir;\n",
            "uniform mat4 model_form;\n",
            "out float lighting;\n",
            "out vec4 shadow_coord;\n",
            "out vec2 tc;\n",
            
            "void main(void) {\n",
            "// shadow_coord = light_cam*vec4(draw_offset.x + position.x, draw_offset.y + position.y, draw_offset.z + position.z, 1.0);\n",
            "vec4 totalLocalPos = vec4(0.0);\n",

            // fix the normal here
            "vec4 totalNormal = vec4(0.0);\n",
            
            "totalNormal = model_form*totalNormal;\n",
            "gl_Position = pv * vec4(draw_offset.x + totalLocalPos.x, draw_offset.y + totalLocalPos.y, draw_offset.z + totalLocalPos.z, totalLocalPos.w);\n",
            "// gl_Position = pv*totalLocalPos;\n",
            "vec4 sun_dir4 = vec4(sun_dir, 0.0);\n",
            "lighting = max(dot(totalNormal, sun_dir4), 0.0);\n",
            "tc = vec2(0.0, 0.0);\n",
            "}\n",
        };
    }

    static String[] getDefaultFrag(){
        return new String[] {
            "// model frag shader",
            "#version 430\n",
            "out vec4 color;\n",
            "uniform vec4 altcolor;\n",
            "uniform vec3 ambient_light;\n",

            "layout (binding = 0) uniform samplerCube cubeMap;\n",
            "layout (binding = 1) uniform sampler2D tex;\n",

            "in vec4 shadow_coord;\n",
            "in float lighting;\n",
            "in vec2 tc;\n",

            "void main(void){\n",
            "float tempLight = 0.4;\n",
            "vec4 coord = vec4(\n",
            "(shadow_coord.x + 20)/40,\n",
            "1-((shadow_coord.y + 20)/40),\n",
            "shadow_coord.z,\n",
            "shadow_coord.w\n",
            ");\n",
            "// float shadow = textureProj(shadow_tex, coord);\n",
            "tempLight += lighting;\n",
            "tempLight = min(tempLight, 1.0);\n",
            "tempLight *= 0.9;\n",

            // maybe use a texture later
            // "vec4 texcol = texture(tex, tc);\n",
            // "color = vec4((altcolor.x+texcol.x)*tempLight, (altcolor.y+texcol.y)*tempLight, (altcolor.z+texcol.z)*tempLight, altcolor.w);\n",

            "color = vec4((altcolor.x)*tempLight, (altcolor.y)*tempLight, (altcolor.z)*tempLight, altcolor.w);\n",
            "}\n",
            };
    }
}