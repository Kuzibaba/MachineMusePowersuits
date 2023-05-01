/*
 * Copyright (c) 2021. MachineMuse, Lehjr
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *      Redistributions of source code must retain the above copyright notice, this
 *      list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package lehjr.numina.client.model.helper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.math.Transformation;
import lehjr.numina.common.base.NuminaLogger;
import lehjr.numina.common.capabilities.render.modelspec.*;
import lehjr.numina.common.constants.NuminaConstants;
import lehjr.numina.common.math.Color;
import lehjr.numina.common.string.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Loads model settings into a common registry and queues Obj models for baking. These settings are beyond that of what Forge or vanilla Minecraft use.
 * Settings can include part settings, (color, visibility, rotation, scale, glow)
 *
 */
@OnlyIn(Dist.CLIENT)
public enum ModelSpecLoader {
    INSTANCE;
    static final String MODEL_SPEC = "modelSpec";
    static final String SPEC_NAME = "specName";
    static final String TYPE = "type";
    static final String DEFAULT = "default";
    static final String GLOW = "glow";
    static final String BINDING = "binding";
    static final String BINDINGS = "bindings";
    static final String PARTS = "parts";
    static final String PART = "part";
    static final String MODELS = "models";
    static final String MODEL = "model";
    static final String COLOR = "color";
    static final String DISPLAY = "display";



    // call from .... maybe in model bake event?
    public void parse() {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        try {
            Map<ResourceLocation, Resource> modelSpecFiles = manager.listResources("modelspec", (resourceLocation) -> resourceLocation.getPath().endsWith(".json"));
            for (Map.Entry<ResourceLocation, Resource> entry : modelSpecFiles.entrySet()) {
                BufferedReader reader = entry.getValue().openAsReader();
                Object object = JsonParser.parseReader(reader);
                if (object instanceof JsonObject && ((JsonObject) object).has(MODEL_SPEC)) {
                    parseFile(((JsonObject) object).getAsJsonObject(MODEL_SPEC));
                } else {
                    NuminaLogger.logError(entry.getKey().toString() + " not a valid ModelSpec file");
                }
                reader.close();
            }
        } catch (Exception e) {
            NuminaLogger.logException("failed to load ModelSpec", e);
        }
    }

    static void parseFile(JsonObject json) {
        String specName = json.getAsJsonPrimitive(SPEC_NAME).getAsString();
        String specTypeString = json.getAsJsonPrimitive(TYPE).getAsString();
        SpecType specType = SpecType.getTypeFromName(specTypeString);
        boolean isDefault = json.has(DEFAULT) && json.get(DEFAULT).getAsBoolean();
        if (specType == null) {
            NuminaLogger.logDebug("model spec loader spec type: NULL " + json);
            return;
        }

        switch (specType) {
            case HANDHELD_OBJ_MODEL -> {
                NuminaLogger.logDebug("model spec loader spec type: HANDHELD_OBJ_MODEL " + json);
                if (json.has(MODELS) && json.get(MODELS).isJsonArray()) {
                    json.getAsJsonArray(MODELS).iterator().forEachRemaining(jsonElement -> {
                        parseObjModelSpec(jsonElement.getAsJsonObject(), SpecType.HANDHELD_OBJ_MODEL, specName, isDefault);
                    });
                } else if (json.has(MODEL)) {
                    parseObjModelSpec(json.get(MODEL).getAsJsonObject(), SpecType.HANDHELD_OBJ_MODEL, specName, isDefault);
                }
            }
            case ARMOR_SKIN -> {
                NuminaLogger.logDebug("model spec loader spec type: ARMOR_SKIN " + json);
                JavaModelSpec javaModel = new JavaModelSpec(specName, isDefault);
                parseArmorJavaModelSpec(json, javaModel);
            }
            case ARMOR_OBJ_MODEL -> {
                NuminaLogger.logDebug("model spec loader spec type: ARMOR_OBJ_MODEL " + json);
                if (json.has(MODELS) && json.get(MODELS).isJsonArray()) {
                    json.getAsJsonArray(MODELS).iterator().forEachRemaining(jsonElement -> {
                        parseObjModelSpec(jsonElement.getAsJsonObject(), SpecType.ARMOR_OBJ_MODEL, specName, isDefault);
                    });
                } else if (json.has(MODEL)) {
                    parseObjModelSpec(json.get(MODEL).getAsJsonObject(), SpecType.ARMOR_OBJ_MODEL, specName, isDefault);
                }
            }
            case HANDHELD_JAVA_MODEL -> {
                NuminaLogger.logDebug("model spec loader spec type: HANDHELD_JAVA_MODE " + json);
                JavaModelSpec javaModel = new JavaModelSpec(specName, isDefault);
                parseHandHeldJavaModel(javaModel, json);
            }
            case NONE -> {
                System.out.println("model spec loader found NONE");
            }
        }
    }

    /**
     * Biggest difference between the ModelSpec for Armor vs PowerFistModel2 is that the armor models don't need item camera transforms
     */
    public static void parseObjModelSpec(JsonObject modelJson, SpecType specType, String specName, boolean isDefault) {
        Transformation modelTransform = Transformation.identity();
        ItemTransforms itemTransforms = ItemTransforms.NO_TRANSFORMS;

        // Load models
        ResourceLocation modelLocation = new ResourceLocation(modelJson.get("file").getAsString());
        // check for item camera transforms
        if (modelJson.has(DISPLAY) && modelJson.get(DISPLAY) instanceof JsonArray) {

//            JsonArray cameraTransformList = modelJson.getAsJsonArray("modelTransforms");
//            JsonElement cameraTransformNode = cameraTransformList.get(0);
//            modelTransform = getIModelTransform(cameraTransformNode);
        } else {
            // Get the transform for the model and add to the registry
            if (modelJson.has("transformationMatrix")) {
                modelTransform = getTransform(modelJson.getAsJsonObject("transformationMatrix"));
            }
        }

        ObjModelSpec modelspec = new ObjModelSpec(modelLocation, itemTransforms, specName, isDefault, specType);
        modelspec.setModelTransform(modelTransform);
        parseBinding(modelspec, modelJson);
        System.out.println("model location here: " + modelLocation);
        System.out.println("modelspec.getOwnName(): " + modelspec.getOwnName());

        NuminaModelRegistry.getInstance().put(StringUtils.extractName(modelLocation), modelspec);

        NuminaModelRegistry.getInstance().getSpecs().forEach(specBase -> System.out.println("specs so far: " + specBase.getOwnName() +", type: " + specBase.getSpecType()));

    }

    static void parseHandHeldJavaModel(JavaModelSpec javaModel, JsonObject json) {
        JavaModelSpec existingspec = (JavaModelSpec) NuminaModelRegistry.getInstance().put(javaModel.getName(), javaModel);

        if (json.has(MODELS) && json.get(MODELS).isJsonArray()) {
            json.getAsJsonArray(MODELS).iterator().forEachRemaining(jsonElement -> {
                JsonObject modelObject = jsonElement.getAsJsonObject();
                parseJavaModelPartsAndBindings(existingspec, modelObject);
            });
            // single model for both hands
        } else if (json.getAsJsonObject().has(MODEL)) {
            JsonObject modelObject = json.get(MODEL).getAsJsonObject();
            parseJavaModelPartsAndBindings(existingspec, modelObject);
        }
    }

    static void parseJavaModelPartsAndBindings(JavaModelSpec javaModel, JsonObject modelObject){
        System.out.println("modelObject: " + modelObject);

        // parts with different bindings
        if (modelObject.getAsJsonObject().has(BINDINGS) && modelObject.get(BINDINGS).isJsonArray()) {
            modelObject.get(BINDINGS).getAsJsonArray().iterator().forEachRemaining(bindingElement ->
                    parseBinding(javaModel, bindingElement.getAsJsonObject()));
        } else {
            // single binding in the model
            if (modelObject.has(BINDING)) {
                parseBinding(javaModel, modelObject.get(BINDING).getAsJsonObject());
            }
        }

        System.out.println("successfully loaded spec and parts? " + NuminaModelRegistry.getInstance().get(javaModel.getName()).getPartsAsStream().collect(Collectors.toList()).size());
    }

    static void parseBinding(ObjModelSpec objModelSpec, JsonObject bindingJson) {
        SpecBinding binding = getBinding(bindingJson);
        if (bindingJson.has(PARTS) && bindingJson.get(PARTS).isJsonArray()) {
            bindingJson.getAsJsonArray(PARTS).iterator().forEachRemaining(jsonElement -> {
                parseObjModelPart(objModelSpec, jsonElement.getAsJsonObject(), binding);
            });
        } else if (bindingJson.has(PART)) {
            parseObjModelPart(objModelSpec, bindingJson.get(PART).getAsJsonObject(), binding);
        }
    }

    static void parseObjModelPart(ObjModelSpec objModelSpec, JsonObject partJson, SpecBinding binding) {
        String name = INSTANCE.makeHandheldTexturePartSpecName(binding.getTarget(), partJson.get("name").getAsString());
        boolean glow = partJson.has(GLOW) && partJson.get(GLOW).getAsBoolean();
        Color color = partJson.has(COLOR) ? parseColor(partJson.get(COLOR).getAsString()) : Color.WHITE;
        objModelSpec.put(new ObjlPartSpec(
                objModelSpec,
                binding,
                name,
                objModelSpec.addColorIfNotExist(color), glow), name);
    }

    static void parseBinding(JavaModelSpec javaModel, JsonObject bindingJson) {
        System.out.println("bindingJson: " + bindingJson);

        SpecBinding binding = getBinding(bindingJson);
        if (bindingJson.has(PARTS) && bindingJson.get(PARTS).isJsonArray()) {
            bindingJson.getAsJsonArray(PARTS).iterator().forEachRemaining(jsonElement -> {
                parseJavaModelPart(javaModel, jsonElement.getAsJsonObject(), binding);
            });
        } else if (bindingJson.has(PART)) {
            parseJavaModelPart(javaModel, bindingJson.get(PART).getAsJsonObject(), binding);
        }
    }

    // target added as prefix to differentiate models
    String makeHandheldTexturePartSpecName(MorphTarget target, String name) {
        return target.name() + "." + name;
    }

    static final ResourceLocation ignored = new ResourceLocation(NuminaConstants.MOD_ID, "ignored");
    static void parseJavaModelPart(JavaModelSpec javaModel, JsonObject partJson, SpecBinding binding) {
        String name = INSTANCE.makeHandheldTexturePartSpecName(binding.getTarget(), partJson.get("name").getAsString());
        boolean glow = partJson.has(GLOW) && partJson.get(GLOW).getAsBoolean();
        Color color = partJson.has(COLOR) ? parseColor(partJson.get(COLOR).getAsString()) : Color.WHITE;
        javaModel.put(new JavaPartSpec(javaModel, binding, javaModel.addColorIfNotExist(color), name,  ignored, glow), name);
    }

    /**
     * Populate texture based armor, like vanilla armor models
     * @param json
     * @param javaModelSpec
     */
    public static void parseArmorJavaModelSpec(JsonObject json, JavaModelSpec javaModelSpec) {
        // ModelBase textures are not registered or stored like baked models are.
        JavaModelSpec existingspec = (JavaModelSpec) NuminaModelRegistry.getInstance().put(javaModelSpec.getName(), javaModelSpec);
        JsonObject bodyJson = json.get("body").getAsJsonObject();
        JsonObject legsJson = json.get("legs").getAsJsonObject();
        Color bodyColor = bodyJson.has(COLOR) ? parseColor(bodyJson.get(COLOR).getAsString()) : Color.WHITE;
        Color legsColor = legsJson.has(COLOR) ? parseColor(legsJson.get(COLOR).getAsString()) : Color.WHITE;

        ResourceLocation bodyTexture = new ResourceLocation(bodyJson.get("texture").getAsString());
        ResourceLocation legsTexture = new ResourceLocation(legsJson.get("texture").getAsString());

        putJavaModelPartSpecs(false, existingspec, bodyTexture, bodyColor);
        putJavaModelPartSpecs(true, existingspec, legsTexture, legsColor);
    }

    String makeArmorTexturePartSpecName(EquipmentSlot slot, MorphTarget target) {
        return slot.getName() + "." + target.name();
    }

    static void putJavaModelPartSpecs(boolean isLegs, JavaModelSpec javaModelSpec, ResourceLocation textureLocation, Color color) {
        if (isLegs) {
            EquipmentSlot slot = EquipmentSlot.LEGS;
            for (MorphTarget target : MorphTarget.getMorphTargetsFromEquipmentSlot(slot)) {
                String partName = INSTANCE.makeArmorTexturePartSpecName(slot, target);
                javaModelSpec.put(new JavaPartSpec(javaModelSpec, new SpecBinding(target, slot, "all"), javaModelSpec.addColorIfNotExist(color), partName,  textureLocation), partName);
            }
        } else {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (slot.equals(EquipmentSlot.LEGS) || !slot.isArmor()) continue;
                for (MorphTarget target : MorphTarget.getMorphTargetsFromEquipmentSlot(slot)) {
                    String partName = INSTANCE.makeArmorTexturePartSpecName(slot, target);
                    javaModelSpec.put(new JavaPartSpec(javaModelSpec, new SpecBinding(target, slot, "all"), javaModelSpec.addColorIfNotExist(color), partName,  textureLocation), partName);
                }
            }
        }
    }

//    @Nullable
//    public static String validatePolygroup(String s, ObjModelSpec m) {
//        return m.getModel().getPart(s) != null ? s : null;
//    }

    /**
     * This gets the transforms for baking the models. Transformation is also used for item camera transforms to alter the
     * position, scale, and translation of a held/dropped/framed item
     *
     * @param transformationJson
     * @return
     */
    static final Vector3f ZERO = new Vector3f(0, 0, 0);
    static final Vector3f ONE = new Vector3f(1, 1, 1);
    public static Transformation getTransform(JsonObject transformationJson) {
        Vector3f translation = parseVector(transformationJson.get("translation"), ZERO);
        Vector3f rotation = parseVector(transformationJson.get("rotation"), ZERO);
        Vector3f scale = parseVector(transformationJson.get("scale"), ONE);
        return ModelHelper.getTransform(translation, rotation, scale);
    }

    /**
     * SpecBinding is a subset if settings for the ModelPartSpec
     */
    public static SpecBinding getBinding(JsonObject bindingJson) {
        NuminaLogger.logDebug("bindingJson: " + bindingJson);
        return new SpecBinding(bindingJson.has("target") ? MorphTarget.getMorph(bindingJson.get("target").getAsString()) : null,
                bindingJson.has("itemSlot") ? EquipmentSlot.byName(bindingJson.get("itemSlot").getAsString().toLowerCase()) : null,
                bindingJson.has("itemState") ? bindingJson.get("itemState").getAsString() : "all");
    }

    @Nullable
    public static Vector3f parseVector(JsonElement object, Vector3f fallback) {
        try {
            String s = object.getAsString();
            String[] ss = s.split(",");
            float x = Float.parseFloat(ss[0]);
            float y = Float.parseFloat(ss[1]);
            float z = Float.parseFloat(ss[2]);
            return new Vector3f(x, y, z);
        } catch (Exception e) {
            return fallback;
        }
    }

    public static Color parseColor(String colorString) {
        return Color.fromARGBHexString(colorString);
    }
}
