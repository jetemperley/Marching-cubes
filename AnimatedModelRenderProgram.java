import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.texture.*;

class AnimatedModelRenderProgram extends ModelRenderProgram {

    int bonesLoc;

    AnimatedModelRenderProgram(GL4 g, int targetTexture) {
        setTexTarget(targetTexture);
        super.make(g, getDefaultVert(), getDefaultFrag());
        initUniforms(g);
        initVertAttributes(g);

        g.glFramebufferTexture(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, textureID, 0);
        g.glDrawBuffer(GL4.GL_FRONT);

    }

    void initUniforms(GL4 g) {
        super.initUniforms(g);

        // bonesLoc = new int[20];
        // for (int i = 0; i < bonesLoc.length; i++) {
        bonesLoc = g.glGetUniformLocation(ID, "bones");
        // System.out.println("loc " + bonesLoc[i]);
        // }

        // get locations for all the uniform variables
        sunDirLoc = g.glGetUniformLocation(ID, "sun_dir");
        // lightCamLoc = g.glGetUniformLocation(ID, "light_cam");
        ambLightLoc = g.glGetUniformLocation(ID, "ambientLight");
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
    }

    void setBones(GL4 g, Matrix4[] bones) {
        // System.out.println(bones.length +" bones set");
        for (int i = 0; i < bones.length; i++) {
            // bonesBuffer[i].put(bones.getMatrix());
            g.glUniformMatrix4fv(bonesLoc + i, 1, false, bones[i].getMatrix(), 0);
        }
    }

    void setSun(GL4 g, float x, float y, float z) {
        g.glUniform3f(sunDirLoc, x, y, z);
    }

    void setAmbientLight(GL4 g, float red, float green, float blue) {
        g.glUniform3f(ambLightLoc, red, green, blue);
    }

    void resetUniforms(GLGraphics g) {
        super.resetUniforms(g);
        setAmbientLight(g.g, g.ambLight.x, g.ambLight.y, g.ambLight.z);
        setBones(g.g, new Matrix4[] { new Matrix4() });
        setSun(g.g, g.sun.x, g.sun.y, g.sun.z);

    }

    static String[] getDefaultVert(){
        return new String[] {
            "// anim vert shader",
            "#version 430\n",
            "layout (location = 0) in vec3 position;\n",
            "layout (location = 1) in vec3 normal;\n",
            "layout (location = 2) in vec2 texcoord;\n",
            "layout (location = 3) in vec3 boneID;\n",
            "layout (location = 4) in vec3 boneWeight;\n",
            "uniform mat4 pv;\n",
            "// uniform mat4 light_cam;\n",
            "uniform vec3 draw_offset;\n",
            "uniform vec3 sun_dir;\n",
            "uniform mat4 bones[20];\n",
            "uniform mat4 model_form;\n",
            "out float lighting;\n",
            "out vec4 shadow_coord;\n",
            "out vec2 tc;\n",
            "void main(void) {\n",
            "// shadow_coord = light_cam*vec4(draw_offset.x + position.x, draw_offset.y + position.y, draw_offset.z + position.z, 1.0);\n",
            "vec4 totalLocalPos = vec4(0.0);\n",
            "vec4 totalNormal = vec4(0.0);\n",
            "for(int i=0;i<3;i++){\n",
            "mat4 jointTransform = bones[int(boneID[i])];\n",
            "vec4 posePosition = jointTransform * vec4(position, 1.0);\n",
            "totalLocalPos += posePosition * boneWeight[i];\n",
            "vec4 worldNormal = jointTransform * vec4(normal, 0.0);\n",
            "totalNormal += worldNormal * boneWeight[i];\n",
            "}\n",
            "totalNormal = model_form*totalNormal;\n",
            "gl_Position = pv * vec4(draw_offset.x + totalLocalPos.x, draw_offset.y + totalLocalPos.y, draw_offset.z + totalLocalPos.z, totalLocalPos.w);\n",
            "// gl_Position = pv*totalLocalPos;\n",
            "vec4 sun_dir4 = vec4(sun_dir, 0.0);\n",
            "lighting = max(dot(totalNormal, sun_dir4), 0.0);\n",
            "tc = texcoord;\n",
            "}\n",
        };
    }

    static String[] getDefaultFrag(){
        return new String[] {
            "// anim frag shader",
            "#version 430\n",
            "out vec4 color;\n",
            "uniform vec4 altcolor;\n",
            "uniform vec3 ambient_light;\n",
            "layout (binding = 0) uniform sampler2DShadow shadow_tex;\n",
            "layout (binding = 1) uniform sampler2D tex;\n",
            "in vec4 shadow_coord;\n",
            "in float lighting;\n",
            "in vec2 tc;\n",
            "void main(void){\n",
            "float tempLight = 0.4;\n",
            "tempLight += lighting;\n",
            "tempLight = min(tempLight, 1.0);\n",
            "tempLight *= 0.9;\n",
            "vec4 texcol = texture(tex, tc);\n",
            "color = vec4((altcolor.x+texcol.x)*tempLight, (altcolor.y+texcol.y)*tempLight, (altcolor.z+texcol.z)*tempLight, altcolor.w);\n",
            "}\n",
        };
    }

}