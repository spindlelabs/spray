/*
 * Copyright (C) 2011 Mathias Doenitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.spray

import http._
import StatusCodes._
import utils.{IllegalResponseException, Logging}

trait ErrorHandling {
  this: Logging =>

  protected[spray] def responseForException(request: Any, e: Exception): HttpResponse = {
    e match {
      case e: java.net.URISyntaxException => {
        log.warn("Service received bad/malformed request: " + e.getMessage + "\n" + e.getStackTraceString)
        HttpResponse(BadRequest)
      }
      case HttpException(failure, reason) =>
        log.warn("Request %s could not be handled normally, completing with %s response (%s)",
          request, failure.value, reason)
        HttpResponse(failure, reason)
      case e: IllegalResponseException => throw e
      case e: Exception =>
        log.warn("Error during processing of request %s: %s", request, e)
        HttpResponse(InternalServerError, InternalServerError.defaultMessage)
    }
  }

}