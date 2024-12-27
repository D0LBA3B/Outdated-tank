import models.{Cell, Terrain, Wall}
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}

object MapReader {
  def ReadJson(fileName: String): Array[Array[Cell]] = {
    val jsonContent = scala.io.Source.fromFile(s"./res/$fileName")
    val jsonData = jsonContent.getLines.mkString
    jsonContent.close();

    val json: JsArray = Json.parse(jsonData).as[JsArray]
    val outputArray: Array[Array[Cell]] = Array.ofDim(json.value.length, json.value.head.as[List[JsValue]].length)

    // Populate the output array from the JSON
    json.value.zipWithIndex.foreach { case (row, i) =>
      row.as[List[JsValue]].zipWithIndex.foreach { case (cell, j) =>
        val cellObj: JsObject = cell.as[JsObject]
        val cellType: String = (cellObj \ "t").as[String]

        // Create object type
        cellType match {
          case "W" =>
            val cellHp: Int= (cellObj \ "hp").as[Int]
            outputArray(i)(j) = new Cell(new Wall(hp=cellHp))
          case "T" =>
            outputArray(i)(j) = new Cell(new Terrain())
          case _ =>
            outputArray(i)(j) = new Cell(new Terrain()) // Terrain by default
        }
      }
    }

    outputArray
  }
}