import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.texture.*;

public class ModelRenderProgram extends GLProgram {

    // rendering program locs
    int sunDirLoc, lightCamLoc, ambLightLoc;

    int modelFormLoc;

    ModelRenderProgram(){
    }

    ModelRenderProgram(GL4 g, int targetTexture) {
        // create the program
        setTexTarget(targetTexture);
        super.make(g, "vert_shader.glsl", "frag_shader.glsl");

        // set up the custom depth buffer
        // g.glBindFramebuffer(GL4.GL_FRAMEBUFFER, customBuffers[0]);
        g.glFramebufferTexture(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, textureID, 0);
        g.glDrawBuffer(GL4.GL_FRONT);

        initUniforms(g);
        initVertAttributes(g);
    }

    void initUniforms(GL4 g) {
        super.initUniforms(g);
        // get locations for all the uniform variables
        sunDirLoc = g.glGetUniformLocation(ID, "sun_dir");
        // lightCamLoc = g.glGetUniformLocation(ID, "light_cam");
        ambLightLoc = g.glGetUniformLocation(ID, "ambientLight");
        modelFormLoc = g.glGetUniformLocation(ID, "model_form");

    }

    void ready(GL4 g) {

        g.glUseProgram(ID);

        g.glFramebufferTexture(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, textureID, 0);
        g.glDrawBuffer(GL4.GL_FRONT);

        g.glEnable(GL4.GL_DEPTH_TEST);
        g.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
        g.glEnable(GL4.GL_BLEND);
        g.glDepthFunc(GL4.GL_LEQUAL);
        // g.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_TRIANGLES);
        // g.glTexParameteri(GL4.GL_TEXTURE_CUBE_MAP, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
        // g.glTexParameteri(GL4.GL_TEXTURE_CUBE_MAP, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
        // g.glTexParameteri(GL4.GL_TEXTURE_CUBE_MAP, GL4.GL_TEXTURE_WRAP_R, GL4.GL_CLAMP_TO_EDGE);

        // 
        // lightCam2.loadIdentity();
        // 
        // lightCam2.multMatrix(transOffset);
        // 
        // lightCam2.multMatrix(cam.projection);
        // lightCam2.multMatrix(sunV);
        // g.glUniformMatrix4fv(lightCamLoc, 1, false, sunPV.getMatrix(), 0);

        // sModel.loadIdentity();
        // sModel.scale(3, 3, 3);
        // sModel.multMatrix(cam.camera);
        // g.glUniformMatrix4fv(pvLoc, 1, false, sModel.getMatrix(), 0);

        // VBO v;
        // v = vm.getVBO(VBOManager.DIAMOND);
        // g.glDrawArrays(v.vertexPattern, v.start/6, v.length);

    }

    void setSun(GL4 g, float x, float y, float z) {
        g.glUniform3f(sunDirLoc, x, y, z);
    }

    void setAmbientLight(GL4 g, float red, float green, float blue) {
        g.glUniform3f(ambLightLoc, red, green, blue);
    }

    void setModelForm(GL4 g, Matrix4 modelForm) {
        g.glUniformMatrix4fv(modelFormLoc, 1, false, modelForm.getMatrix(), 0);
    }

    void resetUniforms(GLGraphics g) {
        super.resetUniforms(g);
        setAmbientLight(g.g, g.ambLight.x, g.ambLight.y, g.ambLight.z);
        setSun(g.g, g.sun.x, g.sun.y, g.sun.z);
        setModelForm(g.g, new Matrix4());
    }

    static String[] getDefaultFrag(){
        return new String[] {
            "#version 430",
            "out vec4 color;",
            "uniform vec4 altcolor;",
            "uniform vec3 ambient_light;",
            "layout (binding = 0) uniform samplerCube cubeMap;",
            "layout (binding = 1) uniform sampler2D tex;",
            "",
            "in vec4 shadow_coord;",
            "in float lighting;",
            "in vec2 tc;",
            "",
            "void main(void){",
            "float tempLight = 0.4;",
            "vec4 coord = vec4(",
            "(shadow_coord.x + 20)/40,",
            "1-((shadow_coord.y + 20)/40),",
            "shadow_coord.z,",
            "shadow_coord.w",
            ");",
            "// float shadow = textureProj(shadow_tex, coord);",
            "",
            "tempLight += lighting;",
            "tempLight = min(tempLight, 1.0);",
            "tempLight *= 0.9;",
            "",
            "vec4 texcol = texture(tex, tc);",
            "",
            "color = vec4((altcolor.x+texcol.x)*tempLight, (altcolor.y+texcol.y)*tempLight, (altcolor.z+texcol.z)*tempLight, altcolor.w);",
            "",
            "}",
            };
    
    }

    static String[] getDefaultVert(){
        return new String[] {
            "#version 430",
            "layout (location = 0) in vec3 position;",
            "layout (location = 1) in vec3 normal;",
            "layout (location = 2) in vec2 texcoord;",
            "layout (location = 3) in ivec3 boneID;",
            "layout (location = 4) in vec3 boneWeight;",
            "",
            "// layout (binding = 0) uniform samplerCube cubeMap;",
            "",
            "uniform mat4 pv;",
            "// uniform mat4 light_cam;",
            "uniform vec3 draw_offset;",
            "uniform vec3 sun_dir;",
            "uniform mat4 model_form;",
            "",
            "out float lighting;",
            "out vec4 shadow_coord;",
            "out vec2 tc;",
            "",
            "void main(void) {",
            "// shadow_coord = light_cam*vec4(draw_offset.x + position.x, draw_offset.y + position.y, draw_offset.z + position.z, 1.0);",
            "// mat4 off = mat4(1.0);",
            "// off[3] = vec4(draw_offset, 1.0);",
            "gl_Position = pv * vec4(draw_offset.x + position.x, draw_offset.y + position.y, draw_offset.z + position.z, 1.0);",
            "// gl_Position = pv *off* vec4(position, 1.0);",
            "",
            "vec4 sun_dir4 = vec4(sun_dir, 0.0);",
            "lighting = max(dot(normal, sun_dir), 0.0);",
            "// gl_Position = pv*vec4(draw_offset.x + position.x, draw_offset.y + position.y, draw_offset.z + position.z, 1.0);",
            "tc = texcoord;",
            "}",
            };
    }
}