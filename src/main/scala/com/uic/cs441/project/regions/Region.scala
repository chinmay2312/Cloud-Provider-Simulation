package com.uic.cs441.project.regions

import scala.util.Random

object Region extends Enumeration {


  type Region = Value

  val REGION1 = Value("Region1")
  val REGION2 = Value("Region2")
  val REGION3 = Value("Region3")
  val REGION4 = Value("Region4")
  val REGION5 = Value("Region5")
  val REGION6 = Value("Region6")
  val REGION7 = Value("Region7")
  val regionCount = 7

  val list: List[Region] =
    List(REGION1, REGION2, REGION3, REGION4, REGION5, REGION6, REGION7)

  def getRandomRegion(): Region = {
    list(Random.nextInt(regionCount))
  }

}
