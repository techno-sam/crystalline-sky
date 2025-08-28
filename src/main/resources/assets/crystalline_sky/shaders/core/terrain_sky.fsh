#version 150

#moj_import <minecraft:globals.glsl>
#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;

in float sphericalVertexDistance;
in float cylindricalVertexDistance;
in vec4 texProj0;

out vec4 fragColor;

#ifndef BAYER_BIAS
#define BAYER_BIAS 0.0
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

    vec4 color = textureProj(Sampler0, texProj0) * vec4(ColorModulator.rgb, 1.0);
    fragColor = apply_fog(color, sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}
