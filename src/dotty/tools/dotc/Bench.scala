/* NSC -- new Scala compiler
 * Copyright 2005-2013 LAMP/EPFL
 * @author  Martin Odersky
 */
package dotty.tools
package dotc

import core.Contexts.Context
import reporting.Reporter

object Bench extends Driver {

  @sharable private var numRuns = 1

  def newCompiler(): Compiler = new Compiler

  private def ntimes(n: Int)(op: => Reporter): Reporter =
    (emptyReporter /: (0 until n)) ((_, _) => op)

  override def doCompile(compiler: Compiler, fileNames: List[String])(implicit ctx: Context): Reporter =
    ntimes(numRuns) {
      val start = System.nanoTime()
      val r = super.doCompile(compiler, fileNames)
      println(s"time elapsed: ${(System.nanoTime - start) / 1000000}ms")
      r
    }

  def extractNumArg(args: Array[String], name: String, default: Int = 1): (Int, Array[String]) = {
    val pos = args indexOf name
    if (pos < 0) (default, args)
    else (args(pos + 1).toInt, (args take pos) ++ (args drop (pos + 2)))
  }

  override def process(args: Array[String], rootCtx: Context): Reporter = {
    val (numCompilers, args1) = extractNumArg(args, "#compilers")
    val (numRuns, args2) = extractNumArg(args1, "#runs")
    this.numRuns = numRuns
    ntimes(numCompilers)(super.process(args2, rootCtx))
  }
}


