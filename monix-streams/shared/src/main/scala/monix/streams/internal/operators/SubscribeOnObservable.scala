/*
 * Copyright (c) 2014-2016 by its authors. Some rights reserved.
 * See the project homepage at: https://monix.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package monix.streams.internal.operators

import monix.execution.cancelables.SingleAssignmentCancelable
import monix.execution.{Cancelable, Scheduler}
import monix.streams.Observable
import monix.streams.observers.Subscriber

private[streams] final
class SubscribeOnObservable[+A](source: Observable[A], s: Scheduler)
  extends Observable[A] {

  def unsafeSubscribeFn(subscriber: Subscriber[A]): Cancelable = {
    val subscription = SingleAssignmentCancelable()
    s.execute(new Runnable {
      def run(): Unit =
        subscription := source.unsafeSubscribeFn(subscriber)
    })

    subscription
  }
}