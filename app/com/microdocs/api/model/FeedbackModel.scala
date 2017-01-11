package com.microdocs.api.model

import play.api.libs.json.Json

case class Feedback(productId: Int, user: Option[String] = Some("Anonymous"), data: String, rating: Int)

object FeedbackModel {
  implicit val formatter = Json.format[Feedback]
}