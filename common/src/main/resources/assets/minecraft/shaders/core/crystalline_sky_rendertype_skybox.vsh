#version 150

#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 TextureMat;
uniform vec3 ChunkOffset;
uniform int FogShape;

out float vertexDistance;
out vec2 uv0;
out vec2 uv1;
out vec3 vPos;

void main() {
    vec3 pos = Position + ChunkOffset;
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);

    vertexDistance = fog_distance(pos, FogShape);
    uv0 = UV0;
    uv1 = (Color.ag * 255.0 / 256.0) + (Color.rb / 256.0);

    // somehow this unbreaks view bobbing. No clue *how*, but it does.
    vec3 screenPos = gl_Position.xyz / gl_Position.w;
    vec4 projected4 = TextureMat * vec4(screenPos, 1.0);
    vec3 projected = projected4.xyz / projected4.w;
    vec4 projectedO4 = TextureMat * vec4(0.0, 0.0, 1.0, 0.0);
    vec3 projectedO = projectedO4.xyz / projectedO4.w;
    vPos = projected - projectedO;
}
