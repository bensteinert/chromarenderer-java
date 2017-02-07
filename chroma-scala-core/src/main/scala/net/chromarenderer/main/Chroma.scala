package net.chromarenderer.main

import net.chromarenderer.math.Vector3


object Chroma {

  def main(args: Array[String]): Unit = {
    println("Hello Chroma.")
    val t1 = new Vector3(1,2,3)
    val t2 = new Vector3(4,5,6)

    println(t1 + t2)

  }

}

object Timer {
  def oncePerSecond(callback: () => Unit) {
    while (true) {
      callback(); Thread sleep 1000
    }
  }

  def timeFlies() {
    println("time flies like an arrow...")
  }

  def main(args: Array[String]) {
    oncePerSecond (timeFlies)
  }
}