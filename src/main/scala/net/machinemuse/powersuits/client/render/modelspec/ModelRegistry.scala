package net.machinemuse.powersuits.client.render.modelspec

import net.machinemuse.numina.general.MuseLogger
import net.machinemuse.numina.scala.MuseRegistry
import net.machinemuse.utils.MuseStringUtils
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{ResourceLocation, Vec3}
import net.minecraftforge.client.model.AdvancedModelLoader
import net.minecraftforge.client.model.obj.WavefrontObject

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 7:44 AM, 4/28/13
 */
object ModelRegistry extends MuseRegistry[ModelSpec] {
  def loadModel(resource: ResourceLocation): Option[WavefrontObject] = {
    val name = MuseStringUtils.extractName(resource)
    get(name) match {
      case Some(i) => Some(i.model)
      case None => wrap(resource)
    }
  }

  def wrap(resource: ResourceLocation): Option[WavefrontObject] = {
    MuseLogger.logDebug("Loading " + resource + " as " + MuseStringUtils.extractName(resource))
    AdvancedModelLoader.loadModel(resource) match {
      case m: WavefrontObject => Some(m)
      case _ => MuseLogger.logError("Model loading failed :( " + resource)
        None
    }
  }

  def getModel(nbt: NBTTagCompound): Option[ModelSpec] = {
    get(nbt.getString("model"))
  }

  def getPart(nbt: NBTTagCompound, model: ModelSpec) = {
    model.get(nbt getString "part")
  }

  def getPart(nbt: NBTTagCompound): Option[ModelPartSpec] = {
    getModel(nbt).flatMap(m => m.get(nbt getString "part"))
  }

  def getSpecTag(museRenderTag: NBTTagCompound, spec: ModelPartSpec): Option[NBTTagCompound] = {
    val name = makeName(spec)
    if (museRenderTag.hasKey(name)) Some(museRenderTag.getCompoundTag(name)) else None
  }

  def makeName(spec: ModelPartSpec) = spec.modelSpec.getOwnName + "." + spec.partName
}

class ModelSpec(val model: WavefrontObject,
                val textures: Array[String],
                val offset: Option[Vec3],
                val rotation: Option[Vec3],
                val filename: String
                 ) extends MuseRegistry[ModelPartSpec] {
  def applyOffsetAndRotation() = {
    // TODO: Implement
  }

  def getOwnName = {
    ModelRegistry.getName(this).getOrElse("")
  }
}

class ModelPartSpec(val modelSpec: ModelSpec,
                    val morph: MorphTarget,
                    val partName: String,
                    val slot: Int,
                    val defaultcolourindex: Int = 0,
                    val defaultglow: Boolean = false,
                    val displayName: String) {

  def getTexture(nbt: NBTTagCompound): String = {
    if (nbt hasKey "texture") nbt getString "texture" else modelSpec.textures.head
  }

  def setTexture(nbt: NBTTagCompound, s: String) {
    if (s.equals("") || s.equalsIgnoreCase(modelSpec.textures.head)) nbt removeTag "texture" else nbt setString("texture", s)
  }

  def getColourIndex(nbt: NBTTagCompound): Int = {
    if (nbt hasKey "colourindex") nbt getInteger "colourindex" else defaultcolourindex
  }

  def setColourIndex(nbt: NBTTagCompound, c: Int) {
    if (c == defaultcolourindex) nbt removeTag "colourindex" else nbt setInteger("colourindex", c)
  }

  def getGlow(nbt: NBTTagCompound): Boolean = {
    if (nbt hasKey "glow") nbt getBoolean "glow" else defaultglow
  }

  def setGlow(nbt: NBTTagCompound, g: Boolean) {
    if (g == defaultglow) nbt removeTag "glow" else nbt setBoolean("glow", g)
  }

  def setModel(nbt: NBTTagCompound, model: ModelSpec) {
    setModel(nbt, ModelRegistry.inverse.getOrElse(model, ""))
  }

  def setModel(nbt: NBTTagCompound, modelname: String) {
    nbt setString("model", modelname)
  }

  def setPart(nbt: NBTTagCompound) {
    nbt setString("part", partName)
  }

  def setPartAndModel(nbt: NBTTagCompound) {
  }

  def multiSet(nbt: NBTTagCompound, tex: Option[String], glow: Option[Boolean], c: Option[Int]): NBTTagCompound = {
    setPart(nbt)
    setModel(nbt, this.modelSpec)
    setTexture(nbt, tex.getOrElse(""))
    setGlow(nbt, glow.getOrElse(false))
    setColourIndex(nbt, c.getOrElse(defaultcolourindex))
    nbt
  }

}