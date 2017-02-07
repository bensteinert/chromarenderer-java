package net.chromarenderer.math

import org.apache.commons.math3.util.FastMath

/**
  * @author bensteinert
  */
class Vector3(x_in: Float, y_in: Float, z_in: Float) {

  val x: Float = x_in
  val y: Float = y_in
  val z: Float = z_in
  val w: Float = 0

  def +(other: Vector3): Vector3 = {
    new Vector3(x + other.x, y + other.y, z + other.z)
  }

  def -(other: Vector3): Vector3 = {
    new Vector3(x - other.x, y - other.y, z - other.z)
  }

  def *(other: Float): Vector3 = {
    new Vector3(x * other, y * other, z * other)
  }

  def *(other: Vector3): Float = {
    x * other.x + y * other.y + z * other.z
  }

  def /(other: Float): Vector3 = {
    this * (1 / other)
  }

  def %(other: Vector3): Vector3 = {
    new Vector3(
      y * other.z - z * other.y,
      z * other.x - x * other.z,
      x * other.y - y * other.x
    )
  }

  def length: Float = {
    FastMath.sqrt(x * x + y * y + z * z).toFloat
  }

  def norm(): Vector3 = {
    this / length
  }

  def inverse: Vector3 = {
    new Vector3(1 / x, 1 / y, 1 / z)
  }

  override def toString = s"Vector3($x, $y, $z)"

}