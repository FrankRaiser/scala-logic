package scala.logic

import scala.logic.state.State
import scala.logic.rules.Rule

/**
 * A base trait for all semantic strategies. The foundation of all
 * available semantics is that they apply states to rules, so this trait
 * defines the necessary types and must be mixed into all strategies.
 * @author Frank Raiser
 */
trait SemanticStrategy {
  type StateType <: State
  type RuleType <: Rule
}