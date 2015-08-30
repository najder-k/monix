/*
 * Copyright (c) 2014-2015 by its authors. Some rights reserved.
 * See the project homepage at: http://www.monifu.org
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

package monifu.reactive.subjects

import monifu.reactive.Ack.Continue
import monifu.reactive.observers.SynchronousObserver

object ReplaySubjectSuite extends BaseSubjectSuite {
  def alreadyTerminatedTest(expectedElems: Seq[Long]) = {
    val s = ReplaySubject[Long]()
    Sample(s, expectedElems.sum)
  }

  def continuousStreamingTest(expectedElems: Seq[Long]) = {
    val s = ReplaySubject[Long]()
    Some(Sample(s, expectedElems.sum))
  }

  test("subscribers should get everything") { implicit s =>
    var completed = 0

    def create(expectedSum: Long) = new SynchronousObserver[Int] {
      var received = 0L
      def onNext(elem: Int) = { received += elem; Continue }
      def onError(ex: Throwable): Unit = throw ex
      def onComplete(): Unit = {
        assertEquals(received, expectedSum)
        completed += 1
      }
    }

    val subject = ReplaySubject[Int]()
    subject.onSubscribe(create(20000))

    s.tick(); subject.onNext(2); s.tick()

    for (_ <- 1 until 5000) assertEquals(subject.onNext(2), Continue)

    subject.onSubscribe(create(20000))
    s.tick(); subject.onNext(2); s.tick()

    for (_ <- 1 until 5000) assertEquals(subject.onNext(2), Continue)

    subject.onSubscribe(create(20000))
    s.tick()

    subject.onComplete()
    s.tick()
    subject.onSubscribe(create(20000))
    s.tick()

    assertEquals(completed, 4)
  }
}