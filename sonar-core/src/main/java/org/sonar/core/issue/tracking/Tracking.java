/*
 * SonarQube
 * Copyright (C) 2009-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.core.issue.tracking;

import com.google.common.base.MoreObjects;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.CheckForNull;

public class Tracking<RAW extends Trackable, BASE extends Trackable> {

  /**
   * Matched issues -> a raw issue is associated to a base issue
   */
  private final IdentityHashMap<RAW, BASE> rawToBase = new IdentityHashMap<>();
  private final IdentityHashMap<BASE, RAW> baseToRaw = new IdentityHashMap<>();

  private final Collection<RAW> raws;
  private final Collection<BASE> bases;

  public Tracking(Collection<RAW> rawInput, Collection<BASE> baseInput) {
    this.raws = rawInput;
    this.bases = baseInput;
  }

  /**
   * Returns an Iterable to be traversed when matching issues. That means
   * that the traversal does not fail if method {@link #match(Trackable, Trackable)}
   * is called.
   */
  public Stream<RAW> getUnmatchedRaws() {
    return raws.stream().filter(raw -> !rawToBase.containsKey(raw));
  }

  public Map<RAW, BASE> getMatchedRaws() {
    return rawToBase;
  }

  @CheckForNull
  public BASE baseFor(RAW raw) {
    return rawToBase.get(raw);
  }

  /**
   * The base issues that are not matched by a raw issue and that need to be closed.
   */
  public Stream<BASE> getUnmatchedBases() {
    return bases.stream().filter(base -> !baseToRaw.containsKey(base));
  }

  boolean containsUnmatchedBase(BASE base) {
    return !baseToRaw.containsKey(base);
  }

  void match(RAW raw, BASE base) {
    if (!rawToBase.containsKey(raw)) {
      rawToBase.put(raw, base);
      baseToRaw.put(base, raw);
    }
  }

  boolean isComplete() {
    return rawToBase.size() == raws.size();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("rawToBase", rawToBase)
      .add("baseToRaw", baseToRaw)
      .add("raws", raws)
      .add("bases", bases)
      .toString();
  }
}
