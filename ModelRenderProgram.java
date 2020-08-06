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
        super.make(g, getDefaultVert(), getDefaultFrag());

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
        g.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_TRIANGLES);
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
            "vec4 texcol = texture(tex, tc);\n",
            "color = vec4((altcolor.x+texcol.x)*tempLight, (altcolor.y+texcol.y)*tempLight, (altcolor.z+texcol.z)*tempLight, altcolor.w);\n",
            "}\n",
            };
    
    }

    static String[] getDefaultVert(){
        return new String[] {
            "// model vert shader",
            "#version 430\n",
            "layout (location = 0) in vec3 position;\n",
            "layout (location = 1) in vec3 normal;\n",
            "layout (location = 2) in vec2 texcoord;\n",
            "layout (location = 3) in ivec3 boneID;\n",
            "layout (location = 4) in vec3 boneWeight;\n",
            "// layout (binding = 0) uniform samplerCube cubeMap;\n",
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
            "// mat4 off = mat4(1.0);\n",
            "// off[3] = vec4(draw_offset, 1.0);\n",
            "gl_Position = pv * vec4(draw_offset.x + position.x, draw_offset.y + position.y, draw_offset.z + position.z, 1.0);\n",
            "// gl_Position = pv *off* vec4(position, 1.0);\n",
            "vec4 sun_dir4 = vec4(sun_dir, 0.0);\n",
            "lighting = max(dot(normal, sun_dir), 0.0);\n",
            "// gl_Position = pv*vec4(draw_offset.x + position.x, draw_offset.y + position.y, draw_offset.z + position.z, 1.0);\n",
            "tc = texcoord;\n",
            "}\n",
        };
    }
}