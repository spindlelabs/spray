/*
 * Copyright (C) 2011-2012 spray.cc
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

package cc.spray.can

import cc.spray.io.pipelining.PipelineStageTest
import org.specs2.matcher.BeEqualTo
import cc.spray.io.{IOPeer, Event, Command}
import cc.spray.util._
import cc.spray.http._
import HttpHeaders.RawHeader


trait HttpPipelineStageSpec extends PipelineStageTest {

  def produce(commands: Seq[Command] = Nil,
              events: Seq[Event] = Nil,
              ignoreTellSender: Boolean = false) = {
    new BeEqualTo(commands -> events) ^^ { (pr: PipelineRun) =>
      pr.commands.map {
        case x: IOPeer.Tell if ignoreTellSender => x.copy(sender = IgnoreSender)
        case IOPeer.Send(bufs, _) => SendStringCommand {
          val sb = new java.lang.StringBuilder
          for (b <- bufs) while (b.remaining > 0) sb.append(b.get.toChar)
          sb.toString.fastSplit('\n').map {
            case s if s.startsWith("Date:") => "Date: XXXX\r"
            case s => s
          }.mkString("\n")
        }
        case x => x
      } -> pr.events
    }
  }

  def request(content: String = "") = HttpRequest().withEntity(content)

  def emptyRawRequest(method: String = "GET") = prep {
    """|%s / HTTP/1.1
       |Host: example.com:8080
       |User-Agent: spray/1.0
       |
       |"""
  }.format(method)

  def rawRequest(content: String, method: String = "GET") = prep {
    """|%s / HTTP/1.1
       |Host: example.com:8080
       |User-Agent: spray/1.0
       |Content-Type: text/plain
       |Content-Length: %s
       |
       |%s"""
  }.format(method, content.length, content)

  def response = HttpResponse(
    status = 200,
    headers = List(
      RawHeader("content-length", "0"),
      RawHeader("date", "Thu, 25 Aug 2011 09:10:29 GMT"),
      RawHeader("server", "spray/1.0")
    )
  )

  def response(content: String) = HttpResponse(
    status = 200,
    headers = List(
      RawHeader("content-type", "text/plain"),
      RawHeader("content-length", content.length.toString),
      RawHeader("date", "Thu, 25 Aug 2011 09:10:29 GMT"),
      RawHeader("server", "spray/1.0")
    ),
    entity = content
  )

  def rawResponse = prep {
    """|HTTP/1.1 200 OK
       |Server: spray/1.0
       |Date: Thu, 25 Aug 2011 09:10:29 GMT
       |Content-Length: 0
       |
       |"""
  }

  def rawResponse(content: String) = prep {
    """|HTTP/1.1 200 OK
       |Server: spray/1.0
       |Date: Thu, 25 Aug 2011 09:10:29 GMT
       |Content-Length: %s
       |Content-Type: text/plain
       |
       |%s"""
  }.format(content.length, content)

  def prep(s: String) = s.stripMargin.replace(EOL, "\r\n")
}
