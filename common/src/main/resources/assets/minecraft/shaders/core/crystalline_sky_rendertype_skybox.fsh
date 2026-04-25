#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
//in vec4 texProj0;
in vec2 uv0;
in vec2 uv1;
in vec3 vPos;

out vec4 fragColor;

#ifndef BAYER_BIAS
#define BAYER_BIAS 0.03125
#endif

#ifndef SCALE
#define SCALE 1
#endif

const mat4x4 bayerMatrix4x4 = mat4x4(
    0.0,  8.0,  2.0, 10.0,
    12.0, 4.0,  14.0, 6.0,
    3.0,  11.0, 1.0, 9.0,
    15.0, 7.0,  13.0, 5.0
) / 16.0;

void main() {
    int x = int(gl_FragCoord.x / SCALE) % 4;
    int y = int(gl_FragCoord.y / SCALE) % 4;
    float bayer = bayerMatrix4x4[y][x];

    if (ColorModulator.a <= bayer + BAYER_BIAS) {
        discard;
    }

    vec3 dir = normalize(vPos);
    vec3 dabs = abs(dir);
    float dmax = max(dabs.x, max(dabs.y, dabs.z));
    vec3 col;
    bool positive;
    vec2 uv;
    vec2 section;
    if (dabs.x == dmax) {
        col = vec3(1.0, 0.0, 0.0);
        positive = dir.x > 0;
        uv = positive ? -dir.zy : vec2(dir.z, -dir.y);
        section = vec2(positive ? 2.0 : 0.0, 1.0);
    } else if (dabs.y == dmax) {
        col = vec3(0.0, 1.0, 0.0);
        positive = dir.y > 0;
        uv = positive ? dir.xz : vec2(dir.x, -dir.z);
        section = vec2(positive ? 1.0 : 2.0, 0.0);
    } else {
        col = vec3(0.0, 0.0, 1.0);
        positive = dir.z > 0;
        uv = positive ? vec2(dir.x, -dir.y) : -dir.xy;
        section = vec2(positive ? 1.0 : 3.0, 1.0);
    }

    uv = (uv / dmax + 1.0) / 2.0;
    uv = clamp(uv, vec2(0.0), vec2(1.0));
    uv = (uv + section) / vec2(4.0, 2.0);

    col *= positive ? 1.0 : 0.5;
    //col = vec3(uv, 0.0);

    vec2 uvR = uv0 + uv * (uv1 - uv0);

    //vec4 color = vec4(col, 1.0) * vec4(ColorModulator.rgb, 1.0);
    vec4 color = texture(Sampler0, uvR) * vec4(ColorModulator.rgb, 1.0);
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
