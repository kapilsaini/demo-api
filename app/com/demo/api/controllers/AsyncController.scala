package com.demo.api.controllers

import akka.actor.ActorSystem
import javax.inject._
import play.api._
import play.api.mvc._
import scala.concurrent.{Future, Promise}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AsyncController @Inject() (actorSystem: ActorSystem) extends Controller {

  def message = Action.async {
    Future.successful { Ok("""{"name":"demo user","role":"developer","unit":"demo unit"}""") }
  }
}
