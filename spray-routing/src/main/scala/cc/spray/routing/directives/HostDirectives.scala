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

package cc.spray.routing
package directives

import util.matching.Regex
import cc.spray.util._
import shapeless._


trait HostDirectives {
  import BasicDirectives._

  /**
   * Extracts the hostname part of the Host header value in the request.
   */
  def hostName: Directive[String :: HNil] = extract(_.request.host)

  /**
   * Rejects all requests with a host name different from the given one.
   */
  def host(hostName: String): Directive0 = host(_ == hostName)

  /**
   * Rejects all requests for whose host name the given predicate function returns false.
   */
  def host(predicate: String => Boolean): Directive0 = hostName.require(predicate)

  /**
   * Rejects all requests with a host name that doesn't have a prefix matching the given regular expression.
   * For all matching requests the prefix string matching the regex is extracted and passed to the inner route.
   * If the regex contains a capturing group only the string matched by this group is extracted.
   * If the regex contains more than one capturing group an IllegalArgumentException is thrown.
   */
  def host(regex: Regex): Directive[String :: HNil] = filter { ctx =>
    def run(regexMatch: String => Option[String]) = {
      regexMatch(ctx.request.host) match {
        case Some(matched) => Pass(matched :: HNil)
        case None => Reject.Empty
      }
    }
    regex.groupCount match {
      case 0 => run(regex.findPrefixOf(_))
      case 1 => run(regex.findPrefixMatchOf(_).map(_.group(1)))
      case _ => throw new IllegalArgumentException("Path regex '" + regex.pattern.pattern +
              "' must not contain more than one capturing group")
    }
  }

}

object HostDirectives extends HostDirectives