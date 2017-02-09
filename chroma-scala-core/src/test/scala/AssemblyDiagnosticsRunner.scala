import net.chromarenderer.math.Vector3
import net.chromarenderer.math.geom.Triangle
import net.chromarenderer.math.raytracing.Ray


//-server -XX:+UnlockDiagnosticVMOptions -XX:+PrintCompilation -XX:+PrintAssembly -XX:PrintAssemblyOptions=intel
object AssemblyDiagnosticsRunner {

  def main(args: Array[String]) {
    val TRIANGLE = Triangle(Vector3(0.0f, 0.0f, 1.0f), //x
      Vector3(.0f, 1.0f, 1.0f), //y
      Vector3(1.0f, 0.0f, 1.0f), //z
      Vector3(0.0f, 0.0f, -1.0f))
    val RAY = Ray(Vector3(0.2f, 0.2f, 0.0f), Vector3(0.0f, 0.0f, 1.0f), 0.0f, Float.MaxValue)

    val start = System.currentTimeMillis
    var i = 0
    while (i < 1000000) {
      TRIANGLE.intersect(RAY)
      i+=1
    }
    val end = System.currentTimeMillis
    System.out.println("Took " + (end - start))
  }
}
