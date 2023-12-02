/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.repository;

import java.util.Collection;

import mn.erin.domain.base.repository.Repository;
import mn.erin.domain.bpm.model.variable.Variable;

/**
 * @author Tamir
 */
public interface VariableRepository extends Repository<Variable>
{
  /**
   * Finds variable by context.
   *
   * @param context describes variable context. For example customer, loan etc...
   * @return {@link Variable} entity has given context.
   */
  Collection<Variable> findByContext(String context);
}
