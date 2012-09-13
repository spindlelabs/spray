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
package authentication

import akka.dispatch.AlreadyCompletedFuture

/**
 * A UserPassAuthenticator that uses plain-text username/password definitions from the spray/akka config file
 * for authentication. The config section should look like this:
 * {{{
 * spray {
 *   .... # other spray settings
 *   users {
 *     username = "password"
 *     ...
 *   }
 * ...
 * }
 * }}}
 */
object FromConfigUserPassAuthenticator extends UserPassAuthenticator[BasicUserContext] {
  def apply(userPass: Option[(String, String)]) = new AlreadyCompletedFuture(
    Right {
      userPass.flatMap {
        case (user, pass) => {
          akka.config.Config.config.getString("spray.users." + user).flatMap { pw =>
            if (SecureEquals.secureEquals(pw, pass)) {
              Some(BasicUserContext(user))
            } else {
              None
            }
          }
        }
      }
    }
  )
}