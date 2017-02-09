package net.chromarenderer.math

import org.apache.commons.math3.util.FastMath

/**
  * @author bensteinert
  */

object Vector3 {
  def calculateNormal(p0: Vector3, p1: Vector3, p2: Vector3): Vector3 = {
    ((p1 - p0) % (p2 - p0)).norm()
  }

  def mirror(direction: Vector3, normal: Vector3): Vector3 = {
    //R = 2 * N * dot(N, OV) - OV;
    (normal * (normal * direction) * 2.0f) - direction
  }
}

case class Vector3(x: Float, y: Float, z: Float) {

  def +(other: Vector3): Vector3 = {
    Vector3(x + other.x, y + other.y, z + other.z)
  }

  def -(other: Vector3): Vector3 = {
    Vector3(x - other.x, y - other.y, z - other.z)
  }

  def *(other: Float): Vector3 = {
    Vector3(x * other, y * other, z * other)
  }

  def *(other: Vector3): Float = {
    x * other.x + y * other.y + z * other.z
  }

  def /(other: Float): Vector3 = {
    this * (1 / other)
  }

  def %(other: Vector3): Vector3 = {
    Vector3(
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
    Vector3(1 / x, 1 / y, 1 / z)
  }

}