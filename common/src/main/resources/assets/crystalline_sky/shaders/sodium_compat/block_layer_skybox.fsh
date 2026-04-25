#version 330 core

#import <sodium:include/fog.glsl>

in vec2 v_uv0; // The top-left texture coordinates
in vec2 v_uv1; // The bottom-right texture coordinates
in vec3 v_Pos; // The interpolated view-relative coordinates
in float v_FragDistance; // The fragment's distance from the camera

in float v_MaterialMipBias;
in float v_MaterialAlphaCutoff;

uniform sampler2D u_BlockTex; // The block (skybox) texture

uniform vec4 u_FogColor; // The color of the shader fog
uniform float u_FogStart; // The starting position of the shader fog
uniform float u_FogEnd; // The ending position of the shader fog

uniform vec4 u_ColorModulator;

out vec4 fragColor; // The output fragment for the color framebuffer

#define SCALE 1
#define BAYER_BIAS 0.03125
#define FACE_DBG 0

const mat4x4 bayerMatrix4x4 = mat4x4(
    0.0,  8.0,  2.0, 10.0,
    12.0, 4.0,  14.0, 6.0,
    3.0,  11.0, 1.0, 9.0,
    15.0, 7.0,  13.0, 5.0
) / 16.0;

void main() {
    vec3 dir = normalize(v_Pos);
    vec3 dabs = abs(dir);
    float dmax = max(dabs.x, max(dabs.y, dabs.z));

    bool positive;
#if FACE_DBG
    vec3 col;
#else
    vec2 uv;
    vec2 section;
#endif

    if (dabs.x == dmax) {
        positive = dir.x > 0;
#if FACE_DBG
        col = vec3(1.0, 0.0, 0.0);
#else
        uv = positive ? -dir.zy : vec2(dir.z, -dir.y);
        section = vec2(positive ? 2.0 : 0.0, 1.0);
#endif
    } else if (dabs.y == dmax) {
        positive = dir.y > 0;
#if FACE_DBG
        col = vec3(0.0, 1.0, 0.0);
#else
        uv = positive ? dir.xz : vec2(dir.x, -dir.z);
        section = vec2(positive ? 1.0 : 2.0, 0.0);
#endif
    } else {
        positive = dir.z > 0;
#if FACE_DBG
        col = vec3(0.0, 0.0, 1.0);
#else
        uv = positive ? vec2(dir.x, -dir.y) : -dir.xy;
        section = vec2(positive ? 1.0 : 3.0, 1.0);
#endif
    }

#if FACE_DBG
    col *= positive ? 1.0 : 0.5;
    vec4 diffuseColor = vec4(col, 1.0);
#else
    uv = (uv / dmax + 1.0) / 2.0;
    uv = clamp(uv, vec2(0.0), vec2(1.0));
    uv = (uv + section) / vec2(4.0, 2.0);

    vec2 uvR = v_uv0 + uv * (v_uv1 - v_uv0);
    vec4 diffuseColor = texture(u_BlockTex, uvR, v_MaterialMipBias);
    //vec4 diffuseColor = vec4(v_uv0, uv.x*0.00000000001, 1.0);
#endif

    diffuseColor *= vec4(u_ColorModulator.rgb, 1.0);

#ifdef USE_FRAGMENT_DISCARD
    int x = int(gl_FragCoord.x / SCALE) % 4;
    int y = int(gl_FragCoord.y / SCALE) % 4;
    float bayer = bayerMatrix4x4[y][x];

    if (u_ColorModulator.a <= bayer + BAYER_BIAS) {
        discard;
    }
#endif

    fragColor = _linearFog(diffuseColor, v_FragDistance, u_FogColor, u_FogStart, u_FogEnd);
}
