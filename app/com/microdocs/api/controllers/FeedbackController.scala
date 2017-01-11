package com.microdocs.api.controllers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.microdocs.api.model.Feedback
import com.microdocs.api.model.FeedbackModel.formatter

import akka.actor.ActorSystem
import javax.inject.Inject
import javax.inject.Singleton
import play.api.libs.json.JsError
import play.api.libs.json.JsObject
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.Action
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.ReactiveMongoComponents
import reactivemongo.api.ReadPreference
import reactivemongo.play.json.JsObjectDocumentWriter
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.play.json.collection.JSONCollectionProducer


@Singleton
class FeedbackController @Inject()(val reactiveMongoApi: ReactiveMongoApi,
      actorSystem: ActorSystem) extends Controller with MongoController with ReactiveMongoComponents  {

  def feedbackFuture: Future[JSONCollection] = database.map(_.collection[JSONCollection]("feedback"))
  
  def addFeedback = Action.async(parse.json) { request =>
    Json.fromJson[Feedback](request.body) match { 
      case JsSuccess(feedback, _) =>
        for {
          feedbackList <- feedbackFuture
          lastError <- feedbackList.insert(feedback)
        } yield {
          if (! lastError.hasErrors)
            Ok(Json.obj("success"->true))
          else 
            Ok(Json.obj("success"->false))
        }
      case JsError(errors) =>
        Future.successful(Ok(Json.obj("success"->false)))
    }
  }
  
  
  def getAllFeedbacks = Action.async {
    val feedbackList = feedbackFuture.flatMap {
       _.find(Json.obj()).   
       cursor[JsObject](ReadPreference.primary).collect[List]()
    }
    
    feedbackList.map { feedback =>
      Ok(Json.toJson(feedback))
    }
  }
  
  def getFeedbacksForProduct(productId: Int) = Action.async {
    val feedbackList = feedbackFuture.flatMap {
      
       _.find(Json.obj("productId" -> productId)).   
       cursor[JsObject](ReadPreference.primary).collect[List]()
    }
    
    feedbackList.map { feedback =>
      Ok(Json.toJson(feedback))
    }
  }
}


