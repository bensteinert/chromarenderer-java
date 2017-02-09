package net.chromarenderer.main

import java.util.concurrent.CountDownLatch

import net.chromarenderer.math.Vector3
import net.chromarenderer.utils.ChromaStatistics
import net.chromarenderer.{Camera, Chroma, ChromaSettings}

object ChromaScalaCore extends Chroma {

  private var renderLatch: CountDownLatch = _
  private var changed = false
  private var breakLoop = false
  private var settings = null
  private var scene = null
  private var needsFlush = false

  private var pixels: Array[Vector3] = _


  override def start(): Unit = {
    while (!Thread.currentThread.isInterrupted) {
      try {
        renderLatch = new CountDownLatch(1)
        renderLatch.await()
        breakLoop = false
        do {

          // do the color-math here :)
          
          changed = true
          ChromaStatistics.frame()
        }
        while (!Thread.currentThread.isInterrupted && !breakLoop)
      }
      catch {
        case e: InterruptedException => Thread.currentThread().interrupt()
      }
    }
  }

  override def initialize(settings: ChromaSettings): Unit = {
    pixels = new Array[Vector3](settings.getImgHeight * settings.getImgWidth)
  }

  override def takeScreenShot(): Unit = ???

  override def stop(): Unit = ???

  override def flushOnNextImage(): Unit = ???

  override def hasChanges: Boolean = ???

  override def getCurrentFrame: Array[Byte] = ???

  override def getSettings: ChromaSettings = ???

  override def getCamera: Camera = ???

  override def run(): Unit = ???
}
