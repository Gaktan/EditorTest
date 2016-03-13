#version 330

in vec3 Position;
in vec4 Color;
in vec2 TexCoord;
in float Z_far;

out vec4 color;

uniform sampler2D u_texture;

const float Z_near = 0.2;
const vec3 lightDir = vec3(0.5, 0.3, 0.8);

float LinearizeDepth(float depth) {
    float z = depth * 2.0 - 1.0;
    return (2.0 * Z_near * Z_far) / (Z_far + Z_near - z * (Z_far - Z_near));	
}

void main() {
    float depth = LinearizeDepth(gl_FragCoord.z) / Z_far;
    vec3 color_depth = Color.rgb - vec3(depth);
    
	vec3 result = color_depth;
    
	color = texture(u_texture, TexCoord) * vec4(result, Color.a);
}
