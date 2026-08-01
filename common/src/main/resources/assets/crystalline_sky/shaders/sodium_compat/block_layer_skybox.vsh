#version 330 core

#import <sodium:include/fog.glsl>
#import <sodium:include/chunk_vertex.glsl>
#import <sodium:include/chunk_matrices.glsl>
#import <sodium:include/chunk_material.glsl>

// The inverse(model-view) * inverse(projection) matrix
uniform mat4 u_InvViewProjMatrix;

out vec2 v_uv0;
out vec2 v_uv1;
out vec3 v_Pos;

out float v_MaterialMipBias;
#ifdef USE_FRAGMENT_DISCARD
out float v_MaterialAlphaCutoff;
#endif

#ifdef USE_FOG
out float v_FragDistance;
#endif

uniform int u_FogShape;
uniform vec3 u_RegionOffset;
uniform vec2 u_TexCoordShrink;

uvec3 _get_relative_chunk_coord(uint pos) {
    // Packing scheme is defined by LocalSectionIndex
    return uvec3(pos) >> uvec3(5u, 0u, 2u) & uvec3(7u, 3u, 7u);
}

vec3 _get_draw_translation(uint pos) {
    return _get_relative_chunk_coord(pos) * vec3(16.0);
}

void main() {
    _vert_init();

    // Transform the chunk-local vertex position into world model space
    vec3 translation = u_RegionOffset + _get_draw_translation(_draw_id);
    vec3 position = _vert_position + translation;

#ifdef USE_FOG
    v_FragDistance = getFragDistance(u_FogShape, position);
#endif

    // Transform the vertex position into model-view-projection space
    gl_Position = u_ProjectionMatrix * u_ModelViewMatrix * vec4(position, 1.0);

    // somehow this unbreaks view bobbing. No clue *how*, but it does.
    vec3 screenPos = gl_Position.xyz / gl_Position.w;
    vec4 projected4 = u_InvViewProjMatrix * vec4(screenPos, 1.0);
    vec3 projected = projected4.xyz / projected4.w;
    vec4 projectedO4 = u_InvViewProjMatrix * vec4(0.0, 0.0, 1.0, 0.0);
    vec3 projectedO = projectedO4.xyz / projectedO4.w;
    v_Pos = projected - projectedO;

    v_uv0 = (vec2(1.0) * u_TexCoordShrink) + _vert_tex_diffuse_coord;  // FMA for precision
    v_uv1 = (vec2(-1.0) * u_TexCoordShrink) + ((_vert_color.ag * 255.0 / 256.0) + (_vert_color.rb / 256.0));

    v_MaterialMipBias = _material_use_mips(_material_params) ? 0.0 : -4.0;
#ifdef USE_FRAGMENT_DISCARD
    v_MaterialAlphaCutoff = _material_alpha_cutoff(_material_params);
#endif
}
