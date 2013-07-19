package scala.logic.execution

import scala.logic.rules.Rule
import scala.logic.rules.selection.RuleSelectionStrategy

/**
 * An ExecutionStrategy specifies how to execute a logic program.
 * 
 * @author Frank Raiser
 */
trait ExecutionStrategy {

  type RuleType <: Rule
  type RuleSelectionStrategyType <: RuleSelectionStrategy
}