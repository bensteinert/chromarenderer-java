package net.chromarenderer.math

/**
  * @author bensteinert
  */
object Constants {

  val FLT_EPSILON: Float = nativeEpsilon
  val PI_f = 3.141593f
  val TWO_PI_f = 6.283185f

  private def nativeEpsilon = {
    var machEps = 1.0f
    do machEps /= 2.0f while ((1.0 + (machEps / 2.0)).toFloat != 1.0)
    machEps
  }

}
