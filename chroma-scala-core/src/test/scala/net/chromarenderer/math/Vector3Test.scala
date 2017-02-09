package net.chromarenderer.math

import org.specs2._

/**
  * @author bensteinert
  */
class Vector3Test extends mutable.Specification {

  "Computing the normal of a plane spanned by three vertices" should {
    "lead to a perpendicular vector following the right-hand coordinate system rule." in {
      Vector3.calculateNormal(
        Vector3(1, 0, 0),
        Vector3(2, 0, 0),
        Vector3(1.5f, 2, 0)
      ) mustEqual Vector3(0, 0, 1)
    }
  }

  "The mirror direction of the vector to another vector" should {
      Vector3.mirror(normal = Vector3(0, 1, 0), direction = Vector3(-1, 1, 0).norm()) mustEqual Vector3(1, 1, 0).norm()
  }

}

