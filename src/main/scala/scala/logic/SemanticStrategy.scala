// Copyright (C) 2013 Frank Raiser
// See the LICENCE file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
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
