#version 330

#define PI 3.14

in vec3 Position;
in vec4 Color;
in vec2 TexCoord;
in float Z_far;

out vec4 color;

//uniform values
uniform sampler2D u_texture;
uniform vec2 u_resolution;

//alpha threshold for our occlusion map
const float THRESHOLD = 0.1;

void main(void) {
    float distance = 1.0;

    for (float y = 0.0; y < u_resolution.y; y += 1.0) {
        //rectangular to polar filter
        vec2 norm = vec2(TexCoord.s, y / u_resolution.y) * 2.0 - 1.0;
        float theta = PI * 1.5 + norm.x * PI; 
        float r = (1.0 + norm.y) * 0.5;

        //coord which we will sample from occlude map
        vec2 coord = vec2(-r * sin(theta), -r * cos(theta)) / 2.0 + 0.5;

        //sample the occlusion map
        vec4 data = texture2D(u_texture, coord);
        
        //the current distance is how far from the top we've come
        float dst = y / u_resolution.y;

        //if we've hit an opaque fragment (occluder), then get new distance
        //if the new distance is below the current, then we'll use that for our ray
        float caster = data.a;
        if (caster > THRESHOLD) {
            distance = min(distance, dst);
            //NOTE: we could probably use "break" or "return" here
        }
    }

    color = vec4(vec3(distance), 1.0);
    //color = texture2D(u_texture, TexCoord);
}

