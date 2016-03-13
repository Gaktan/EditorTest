#version 330

in vec3 Position;
in vec4 Color;
in vec2 TexCoord;
in float Z_far;

out vec4 color;

uniform sampler2D u_texture;
uniform float u_lightZ;

void main() {  
	vec4 tex = texture(u_texture, TexCoord);
    
    float zPos = float(u_lightZ < Position.z);
    color = vec4(1.0, 1.0, 1.0, zPos * tex.a);
}
