#version 330 core

#import <sodium:include/fog.glsl>

in vec4 v_Color; // The interpolated vertex color
in vec4 v_TexProj; // The interpolated sky texture coordinates
in vec2 v_FragDistance; // The fragment's distance from the camera (cylindrical and spherical)

in float v_MaterialMipBias;
in float v_MaterialAlphaCutoff;

uniform sampler2D u_BlockTex; // The block (sky) texture

uniform vec4 u_FogColor; // The color of the shader fog
uniform vec2 u_EnvironmentFog; // The start and end position for environmental fog
uniform vec2 u_RenderFog; // The start and end position for border fog

uniform vec4 u_ColorModulator;

out vec4 fragColor; // The output fragment for the color framebuffer

#define SCALE 1
#define BAYER_BIAS 0.03125

const mat4x4 bayerMatrix4x4 = mat4x4(
    0.0,  8.0,  2.0, 10.0,
    12.0, 4.0,  14.0, 6.0,
    3.0,  11.0, 1.0, 9.0,
    15.0, 7.0,  13.0, 5.0
) / 16.0;

void main() {
    vec4 diffuseColor = textureProj(u_BlockTex, v_TexProj, v_MaterialMipBias);//texture(u_BlockTex, v_TexCoord, v_MaterialMipBias);

    /*// Apply per-vertex color
    diffuseColor *= v_Color;*/
    diffuseColor *= vec4(u_ColorModulator.rgb, 1.0);

#ifdef USE_FRAGMENT_DISCARD
    int x = int(gl_FragCoord.x / SCALE) % 4;
    int y = int(gl_FragCoord.y / SCALE) % 4;
    float bayer = bayerMatrix4x4[y][x];

    if (u_ColorModulator.a <= bayer + BAYER_BIAS) {
        discard;
    }
#endif

    fragColor = _linearFog(diffuseColor, v_FragDistance, u_FogColor, u_EnvironmentFog, u_RenderFog);
}
