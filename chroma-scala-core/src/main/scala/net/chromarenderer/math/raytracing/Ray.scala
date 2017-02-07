package net.chromarenderer.math.raytracing

import net.chromarenderer.math.Vector3

/**
  * @author bensteinert
  */
//object Ray {
//  val NOWHERE = new Ray(Vector3.FLT_MAX, Vector3.Z_AXIS)
//}

class Ray(origin_in: Vector3, dir_in: Vector3, tMin_in: Float, tMax_in:Float) {

  val origin: Vector3 = origin_in
  val dir: Vector3 = dir_in
  //val invDir: Vector3 = dir_in.inverse
  val tMin: Float = tMin_in
  val tMax: Float = tMax_in

  //Precomputed sign bits: if sign == 1 -> direction negative else 0
  //val signX: Byte = if (dir.x < 0) 1 else 0
  //val signY: Byte = if (dir.y < 0) 1 else 0
  //val signZ: Byte = if (dir.z < 0) 1 else 0

}

