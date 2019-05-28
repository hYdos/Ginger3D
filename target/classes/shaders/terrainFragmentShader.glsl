#version 400 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[5];
in vec3 toCameraVector;
in float visibility;
in vec4 shadowCoords;

out vec4 out_Color;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;
uniform sampler2D shadowMap;

uniform vec3 attenuation[5];
uniform vec3 lightColour[5];
const float shineDamper = 0;
const float reflectivity = 0;
uniform vec3 skyColour;

void main(void){

	float objectNearestLight = texture(shadowMap, shadowCoords.xy).r;
	float lightFactor = 1.0;
	if(shadowCoords.z > objectNearestLight){
		lightFactor = 1.0 - 0.4;
	}

	vec4 blendMapColour = texture(blendMap, pass_textureCoords);

	vec3 unitVectorToCamera = normalize(toCameraVector);

	float backTextureAmount = 1 - (blendMapColour.r + blendMapColour.g + blendMapColour.b);
	vec2 tiledCoords = pass_textureCoords * 40.0;
	vec4 backgroundTextureColour = texture(backgroundTexture, tiledCoords) * backTextureAmount;
	vec4 rTextureColour = texture(rTexture, tiledCoords) * blendMapColour.r;
	vec4 gTextureColour = texture(gTexture, tiledCoords) * blendMapColour.g;
	vec4 bTextureColour = texture(bTexture, tiledCoords) * blendMapColour.b;

	vec4 totalColour = backgroundTextureColour + rTextureColour + gTextureColour + bTextureColour;


	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);

	for(int i=0;i<5;i++){
		float distance = length(toLightVector[i]);
		float attFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
		vec3 unitNormal = normalize(surfaceNormal);
		vec3 unitLightVector = normalize(toLightVector[i]);
		float nDotl = dot(unitNormal, unitLightVector);
		float brightness = max(nDotl, 0.2);
		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
		float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
		specularFactor = max(specularFactor, 0.0);
		float dampedFactor = pow(specularFactor, shineDamper);

		totalDiffuse = totalDiffuse + brightness * lightColour[i] / attFactor;
		totalSpecular = totalSpecular + dampedFactor * reflectivity * lightColour[i] / attFactor;

	}
	totalDiffuse = max(totalDiffuse, 0.2) * lightFactor;

	out_Color = vec4(totalDiffuse, 1.0) * totalColour + vec4(totalSpecular, 1.0);
	out_Color = mix(vec4(skyColour, 1.0), out_Color, visibility);
}
