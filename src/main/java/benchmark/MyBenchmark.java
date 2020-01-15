/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package benchmark;

import com.jusski.TieredVector;
import com.jusski.optimize.array.TieredVectorArray;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.State;

import org.openjdk.jmh.runner.Runner;

import org.openjdk.jmh.runner.RunnerException;

import org.openjdk.jmh.runner.options.Options;

import org.openjdk.jmh.runner.options.OptionsBuilder;
import java.util.Random;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.BenchmarkMode;

import org.openjdk.jmh.annotations.Measurement;

import org.openjdk.jmh.annotations.Warmup;

public class MyBenchmark {

	@State(Scope.Benchmark)
	public static class Rdx {
		public final com.jusski.TieredVector tieredVector = new TieredVector(200_000, 512);
		public final int[] index = new int[100_000];
		public int offset = 0;

		@TearDown(Level.Iteration)
		public void teardown() {
			for (int i = 0; i < 100_000; i ++) {
				tieredVector.remove(index[i]);
			}
			offset = 0;
		}

		@Setup(Level.Trial)
		public void setup() {
			Random random = new Random(1234);

			for (int i = 0; i < 100_000; i++) {
				index[i] = random.nextInt(100_000 - 1);
			}
			for (int i = 0; i < index.length; i++) {
				tieredVector.add(index[i]);
			}
			
			for (int j = 0; j < 5; j++) {
				for (int i = 0; i < index.length; i++) {
					tieredVector.add(49_000 + i, 33);
				}
				for (int i = 0; i < index.length; i++) {
					tieredVector.remove(index[i]);
				}
			}
		}

//		@Benchmark
//		@BenchmarkMode(Mode.AverageTime)
//		public void rdx() {
//			tieredVector.add(index[offset], index[offset]);
//		}
		@Benchmark
		@Warmup(iterations = 5, batchSize = 100_000)
		@Measurement(iterations = 5, batchSize = 100_000)
		@BenchmarkMode(Mode.SingleShotTime)
		public void rdx2() {
			tieredVector.add(49_000 + offset, 33);
			offset += 1;
		}

	}

	@State(Scope.Benchmark)
	public static class Rediska {
		public final TieredVector tieredVector = new TieredVector(200_000, 512);
		public final int[] index = new int[100_000];
		public int offset = 0;

		@TearDown(Level.Iteration)
		public void teardown() {
			for (int i = 0; i < 100_000; i ++) {
				tieredVector.remove(index[i]);
			}
			offset = 0;
		}

		@Setup(Level.Trial)
		public void setup() {
			Random random = new Random(1234);

			for (int i = 0; i < 100_000; i++) {
				index[i] = random.nextInt(100_000 - 1);
			}
			for (int i = 0; i < index.length; i++) {
				tieredVector.add(index[i]);
			}
			
			for (int j = 0; j < 5; j++) {
				for (int i = 0; i < index.length; i++) {
					tieredVector.add(49_000 + i, 33);
				}
				for (int i = 0; i < index.length; i++) {
					tieredVector.remove(index[i]);
				}
			}
		}

//		@Benchmark
//		@BenchmarkMode(Mode.AverageTime)
//		public void rediska() {
//			tieredVector.add(index[offset], index[offset]);
//		}
		@Benchmark
		@Warmup(iterations = 5, batchSize = 100_000)
		@Measurement(iterations = 5, batchSize = 100_000)
		@BenchmarkMode(Mode.SingleShotTime)
		public void rediska2() {
			tieredVector.add(49_000 + offset, 33);
			offset += 1;
		}

	}

	public static void main(String[] args) throws RunnerException {

		Options opt = new OptionsBuilder()

		.include(MyBenchmark.class.getSimpleName())

		.threads(1)

		.forks(1)

		.build();

		new Runner(opt).run();

	}

}
